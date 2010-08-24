// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.circuit;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.conductivity.macro.battery.Battery;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.circuit:
//            CompositeLinearBranch, Wire, Resistor, LinearBranch

public class Circuit {

    public Circuit( double d, double d1 ) {
        circuit = new CompositeLinearBranch();
        at = new Vector2D( d, d1 );
    }

    public LinearBranch wireAt( int i ) {
        return circuit.branchAt( i );
    }

    public Wire wireTo( double d, double d1 ) {
        Vector2D phetvector = new Vector2D( d, d1 );
        Wire wire = new Wire( at, phetvector );
        circuit.addBranch( wire );
        at = phetvector;
        return wire;
    }

    public Resistor resistorTo( double d, double d1 ) {
        Vector2D phetvector = new Vector2D( d, d1 );
        Resistor resistor = new Resistor( at, phetvector );
        circuit.addBranch( resistor );
        at = phetvector;
        return resistor;
    }

    public Battery batteryTo( double d, double d1 ) {
        Vector2D phetvector = new Vector2D( d, d1 );
        Battery battery = new Battery( at, phetvector );
        circuit.addBranch( battery );
        at = phetvector;
        return battery;
    }

    public double getLength() {
        return circuit.getLength();
    }

    public int numBranches() {
        return circuit.numBranches();
    }

    public AbstractVector2DInterface getPosition( double d ) {
        return circuit.getPosition( d );
    }

    public boolean contains( double d ) {
        return d >= 0.0D && d <= getLength();
    }

    CompositeLinearBranch circuit;
    Vector2D at;
}
