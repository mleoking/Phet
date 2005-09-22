/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.Battery;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * BatteryReadout
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BatteryReadout extends GraphicLayerSet {
    private Point centerPoint;
    private JTextField readout;
    private PhetGraphic readoutGraphic;

    public BatteryReadout( final Component component, final Battery battery, Point centerPoint, int offset ) {
        super( component );
        this.centerPoint = centerPoint;

        readout = new JTextField( 4 );
        readout.setHorizontalAlignment( JTextField.HORIZONTAL );
        readout.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                double voltage = 0;
                try {
                    String text = readout.getText().toLowerCase();
                    int vLoc = text.indexOf( 'v' );
                    text = vLoc >= 0 ? readout.getText().substring( 0, vLoc ) : text;
                    voltage = Double.parseDouble( text );
                    battery.setVoltage( voltage / DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR );
                }
                catch( NumberFormatException e1 ) {
                    JOptionPane.showMessageDialog( SwingUtilities.getRoot( component ), "Voltage must be numeric, or a number followed by \"v\"" );
                }
            }
        } );
        readoutGraphic = PhetJComponent.newInstance( component, readout );
        addGraphic( readoutGraphic, 1E9 );

        battery.addChangeListener( new Battery.ChangeListener() {
            public void voltageChanged( Battery.ChangeEvent event ) {
                double voltage = event.getVoltageSource().getVoltage();
                update( voltage );
            }
        } );
        update( battery.getVoltage() );
    }

    private void update( double voltage ) {
        DecimalFormat voltageFormat = new DecimalFormat( "#0.0" );
        Object[] args = {voltageFormat.format( Math.abs( voltage ) * DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR )};
        String text = MessageFormat.format( SimStrings.get( "BatteryGraphic.voltage" ), args );
        readout.setText( text );
        readoutGraphic.setLocation( (int)centerPoint.getX(), (int)centerPoint.getY() );
        readoutGraphic.setBoundsDirty();
        readoutGraphic.repaint();
    }
}
