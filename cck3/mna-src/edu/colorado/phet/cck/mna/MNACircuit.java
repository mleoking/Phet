/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.mna;

import Jama.Matrix;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * Assumes nodes are numbered consecutively: i.e. a netlist:
 * i1 0 3 1.0
 * r1 1 3 1.0 would be illegal.
 * <p/>
 * Todo:
 * make sure we're eliminating node 0.
 */

public class MNACircuit {
    private ArrayList components = new ArrayList();

    public void addComponent( MNAComponent component ) {
        components.add( component );
    }

    public String toString() {
        return components.toString();
    }

    public void parseNetList( String[]netlist ) {
        clear();
        for( int i = 0; i < netlist.length; i++ ) {
            String line = netlist[i];
            addComponent( parseLine( line ) );
        }
    }

    private MNAComponent parseLine( String line ) {
        StringTokenizer st = new StringTokenizer( line, " " );
        String name = st.nextToken();
        int start = Integer.parseInt( st.nextToken() );
        int end = Integer.parseInt( st.nextToken() );
        ArrayList details = new ArrayList();
        while( st.hasMoreTokens() ) {
            details.add( st.nextToken() );
        }
        String[]detailArray = (String[])details.toArray( new String[0] );
        if( name.toLowerCase().startsWith( "r" ) ) {
            return new MNAResistor( name, start, end, Double.parseDouble( detailArray[0] ) );
        }
        else if( name.toLowerCase().startsWith( "i" ) ) {
            return new MNACurrentSource( name, start, end, Double.parseDouble( detailArray[0] ) );
        }
        else if( name.toLowerCase().startsWith( "v" ) ) {
            return new MNAVoltageSource( name, start, end, Double.parseDouble( detailArray[0] ) );
        }
        else {
            throw new RuntimeException( "Illegal component type: " + line );
        }
    }

    private void clear() {
        components.clear();
    }

    public void parseNetList( String netlist ) {
        StringTokenizer st = new StringTokenizer( netlist, "\n" + System.getProperty( "line.separator" ) );
        ArrayList list = new ArrayList();
        while( st.hasMoreTokens() ) {
            list.add( st.nextToken() );
        }
        parseNetList( (String[])list.toArray( new String[0] ) );
    }

    public static abstract class MNAComponent {
        String name;
        int startJunction;
        int endJunction;

        public MNAComponent( String name, int startJunction, int endJunction ) {
            this.name = name;
            this.startJunction = startJunction;
            this.endJunction = endJunction;
        }

        public String getName() {
            return name;
        }

        public int getStartJunction() {
            return startJunction;
        }

        public int getEndJunction() {
            return endJunction;
        }

        public String toString() {
            return name + " " + startJunction + " " + endJunction;
        }

        public int getCurrentVariableCount() {
            return 0;
        }

        public abstract void stamp( MNASystem system );
    }

    public static class MNAResistor extends MNAComponent {
        private double resistance;

        public MNAResistor( String name, int startJunction, int endJunction, double resistance ) {
            super( name, startJunction, endJunction );
            this.resistance = resistance;
        }

        public double getResistance() {
            return resistance;
        }

        public String toString() {
            return super.toString() + " " + resistance;
        }

        public void stamp( MNASystem s ) {
            int i = getStartJunction();
            int j = getEndJunction();
            s.addAdmittance( i, i, 1 / resistance );
            s.addAdmittance( j, j, 1 / resistance );
            s.addAdmittance( i, j, -1 / resistance );
            s.addAdmittance( j, i, -1 / resistance );
        }
    }

    public static class MNACurrentSource extends MNAComponent {
        double current;

        public MNACurrentSource( String name, int startJunction, int endJunction, double current ) {
            super( name, startJunction, endJunction );
            this.current = current;
        }

        public double getCurrent() {
            return current;
        }

        public String toString() {
            return super.toString() + " " + current;
        }

        public void stamp( MNASystem system ) {
            system.addSource( getStartJunction(), -current );
            system.addSource( getEndJunction(), current );
        }

    }

    public static class MNAVoltageSource extends MNAComponent {
        double voltage;

        public MNAVoltageSource( String name, int startJunction, int endJunction, double voltage ) {
            super( name, startJunction, endJunction );
            this.voltage = voltage;
        }

        public double getVoltage() {
            return voltage;
        }

        public String toString() {
            return super.toString() + " " + voltage;
        }

        public int getCurrentVariableCount() {
            return 1;
        }

        public void stamp( MNASystem system ) {
            //voltage goes from k to l...
            int k = getStartJunction();
            int L = getEndJunction();
            system.addVoltageTerm( this );
        }
    }

    /**
     * Admittance * x = source.
     */
    public static class MNASystem {
        Matrix admittance;
        Matrix source;
        private int numVoltageVariables;
        private int numCurrentVariables;
        ArrayList voltageSources = new ArrayList();

        public MNASystem( int numVoltageVariables, int numCurrentVariables ) {
            this.numVoltageVariables = numVoltageVariables;
            this.numCurrentVariables = numCurrentVariables;

            admittance = new Matrix( getNumVariables(), getNumVariables() );
            source = new Matrix( getNumVariables(), 1 );
        }

        private int getNumVariables() {
            return numVoltageVariables + numCurrentVariables;
        }

        public void addAdmittance( int row, int col, double value ) {
            admittance.set( row, col, admittance.get( row, col ) + value );
        }

        public void addSource( int row, double v ) {
            source.set( row, 0, source.get( row, 0 ) + v );
        }

        public String toString() {
            return toString( 3, 3 );
        }

        private String toString( int w, int d ) {
            StringWriter admString = new StringWriter();
            admittance.print( new PrintWriter( admString ), w, d );
            StringWriter sourceString = new StringWriter();
            source.print( new PrintWriter( sourceString ), w, d );
            return admString + "\n" + sourceString;
        }

        public void addVoltageTerm( MNAVoltageSource voltageSource ) {
            voltageSources.add( voltageSource );
            int k = voltageSource.getStartJunction();
            int L = voltageSource.getEndJunction();
            int at = numVoltageVariables + voltageSources.size();
            admittance.set( at, k, 1 );
            admittance.set( at, L, -1 );

            admittance.set( k, at, 1 );
            admittance.set( L, at, -1 );

            source.set( at, 0, voltageSource.getVoltage() );
        }
    }

    public MNASystem getMNASystem() {
        MNASystem system = new MNASystem( getNodeCount(), getCurrentVariableCount() );
        for( int i = 0; i < components.size(); i++ ) {
            MNAComponent component = (MNAComponent)components.get( i );
            component.stamp( system );
        }
        return system;
    }

    private int getCurrentVariableCount() {
        int sum = 0;
        for( int i = 0; i < components.size(); i++ ) {
            MNAComponent component = (MNAComponent)components.get( i );
            sum += component.getCurrentVariableCount();
        }
        return sum;
    }

    private int getNodeCount() {
        HashSet hashSet = new HashSet();
        for( int i = 0; i < components.size(); i++ ) {
            MNAComponent component = (MNAComponent)components.get( i );
            hashSet.add( new Integer( component.getStartJunction() ) );
            hashSet.add( new Integer( component.getEndJunction() ) );
        }
        return hashSet.size();
    }

}
