/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2.components;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.clock2.AbstractClock;
import edu.colorado.phet.coreadditions.clock2.ClockStateListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Sep 8, 2003
 * Time: 12:07:08 PM
 * Copyright (c) Sep 8, 2003 by Sam Reid
 */
public class DefaultClockStatePanel extends JPanel {
    AbstractClock clock;
    JButton play;
    JButton stop;
    JButton kill;
    JSpinner changeDelay;
    
    static {
        SimStrings.setStrings( "localization/HPA-22Strings" );
    }

    public DefaultClockStatePanel( final AbstractClock clock ) {
        this.clock = clock;
        play = new JButton( SimStrings.get( "DefaultClockStatePanel.PlayButton") );
        play.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.start();
            }
        } );

        stop = new JButton( SimStrings.get( "DefaultClockStatePanel.StopButton" ) );
        stop.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.stop();
            }
        } );
        kill = new JButton( SimStrings.get( "DefaultClockStatePanel.KillButton" ) );
        kill.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clock.kill();
            }
        } );
        final SpinnerModel model = new SpinnerNumberModel( clock.getRequestedDelay(), 5, 200, 5 );
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer value = (Integer)model.getValue();
                clock.setRequestedDelay( value.intValue() );
            }
        } );
        changeDelay = new JSpinner( model );
        changeDelay.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "DefaultClockStatePanel.BorderTitle" ) ) );
        clock.addClockStateListener( new ClockStateListener() {
            public void clockStarted( AbstractClock source ) {
                play.setEnabled( false );
                stop.setEnabled( true );
                kill.setEnabled( true );
            }

            public void clockStopped( AbstractClock source ) {
                play.setEnabled( true );
                stop.setEnabled( false );
                kill.setEnabled( true );
            }

            public void clockDestroyed( AbstractClock source ) {
                kill.setEnabled( false );
                play.setEnabled( false );
                stop.setEnabled( false );
                changeDelay.setEnabled( false );
            }

            public void clockDelayChanged( AbstractClock source, int newDelay ) {
                changeDelay.setValue( new Integer( newDelay ) );
            }
        } );
//        setLayout(new );
        add( play );
        add( stop );
        add( kill );
        add( changeDelay );
    }


}
