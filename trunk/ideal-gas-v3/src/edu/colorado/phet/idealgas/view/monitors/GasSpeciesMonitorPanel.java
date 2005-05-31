/*
 * Class: IdealGasMonitorPanel
 * Package: edu.colorado.phet.graphicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 30, 2002
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.NumberFormat;

/**
 *
 */
public class GasSpeciesMonitorPanel extends PhetMonitorPanel implements SimpleObserver {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    // The following factors make the speed of molecules displayed correct
    private double s_screenToModelFactor = 476.0 / 327;
//    private double s_screenToModelFactor = 476.0 / 290;
    private double s_aveSpeedReadoutFactor = 10 * s_screenToModelFactor;


    private Class speciesClass;
    private JTextField numParticlesTF;
    private NumberFormat aveSpeedFormat = NumberFormat.getInstance();
    private JTextField aveSpeedTF;
    private IdealGasModel model;


    /**
     * Constructor
     */
    public GasSpeciesMonitorPanel( Class speciesClass, String speciesName, final IdealGasModel model ) {
        this.model = model;
        this.speciesClass = speciesClass;

        setUpdateInterval( 500 );

        // Sanity check on parameter
        if( !GasMolecule.class.isAssignableFrom( speciesClass ) ) {
            throw new RuntimeException( "Class other than a gas species class sent to constructor for GasSpeciesMonitorPanel" );
        }

        this.setPreferredSize( new Dimension( 410, 60 ) );
        Border border = new TitledBorder( speciesName );
        this.setBorder( border );

        // Set up the readout for the number of gas molecules
        this.add( new JLabel( SimStrings.get( "GasSpeciesMonitorPanel.Number_of_Gas_Molecules" ) + ": " ) );
        numParticlesTF = new JTextField( 4 );
        numParticlesTF.setEditable( false );
        this.add( numParticlesTF );

        // Set up the average speed readout
        aveSpeedFormat.setMaximumFractionDigits( 2 );
        //aveSpeedFormat.setMinimumFractionDigits( 2 );
        this.add( new JLabel( SimStrings.get( "GasSpeciesMonitorPanel.Average_speed" ) + ": " ) );
        aveSpeedTF = new JTextField( 6 );
        aveSpeedTF.setEditable( false );
        this.add( aveSpeedTF );
        this.add( new JLabel( "m/sec"));

        // Hook up to the model
        model.addObserver( this );
    }

    /**
     * Clears the values in the readouts
     */
    public void clear() {
        numParticlesTF.setText( "" );
        aveSpeedTF.setText( "" );
    }

    /**
     *
     */
    public void update() {

        // Get the number of molecules, average speed of the molecules
        double aveSpeed = 0;
        int numMolecules = 0;
        if( HeavySpecies.class.isAssignableFrom( speciesClass ) ) {
            numMolecules = model.getHeavySpeciesCnt();
            aveSpeed = model.getHeavySpeciesAveSpeed();
        }
        if( LightSpecies.class.isAssignableFrom( speciesClass ) ) {
            numMolecules = model.getLightSpeciesCnt();
            aveSpeed = model.getLightSpeciesAveSpeed();
        }

        // Track the values we got
        long now = System.currentTimeMillis();
        if( now - getLastUpdateTime() >= getUpdateInterval() ) {

            setLastUpdateTime( now );
            //Display the readings
            numParticlesTF.setText( Integer.toString( numMolecules ) );

            if( Double.isNaN( runningAveSpeed ) ) {
            }
            aveSpeedTF.setText( aveSpeedFormat.format( ( runningAveSpeed / sampleCnt ) * s_aveSpeedReadoutFactor ) );
            sampleCnt = 0;
            runningAveSpeed = 0;
        }
        else {
            sampleCnt++;
            runningAveSpeed += aveSpeed;
        }
    }

    private int sampleCnt;
    private double runningAveSpeed;

    /**
     *
     */
    public void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );
    }
}
