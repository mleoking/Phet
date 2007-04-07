/*Copyrig/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.common.PhetCommonProjectConfig;

/**
 * ClockControlPanel
 *
 * @author ?
 * @version $Revision$
 */
public class ClockControlPanel extends JPanel implements ClockListener {
    
    private static final String IMAGES_DIRECTORY = "icons/java/media/";
    
    // These image filenames may be used by sim-specific classes that have custom clock control panels!
    public static final String IMAGE_PAUSE = IMAGES_DIRECTORY + "Pause24.gif";
    public static final String IMAGE_PLAY = IMAGES_DIRECTORY + "Play24.gif";
    public static final String IMAGE_STEP = IMAGES_DIRECTORY + "StepForward24.gif";
    public static final String IMAGE_REWIND = IMAGES_DIRECTORY + "Rewind24.gif";
    public static final String IMAGE_FAST_FORWARD = IMAGES_DIRECTORY + "FastForward24.gif";
    public static final String IMAGE_STOP = IMAGES_DIRECTORY + "Stop24.gif";
    
    public static final String PROPERTY_PLAY = "Common.ClockControlPanel.Play";
    public static final String PROPERTY_PAUSE = "Common.ClockControlPanel.Pause";
    public static final String PROPERTY_STEP = "Common.ClockControlPanel.Step";
    
    private JButton playPause;
    private JButton step;
    private IClock clock;
    private JPanel buttonPanel;
    
    ImageIcon playIcon;
    ImageIcon pauseIcon;
    
    String playString;
    String pauseString;

    public ClockControlPanel( final IClock clock ) {
        
        if( clock == null ) {
            throw new RuntimeException( "Cannot have a control panel for a null clock." );
        }
        this.clock = clock;
        clock.addClockListener( this );

        // Button labels
        playString = PhetCommonProjectConfig.getInstance().getString( PROPERTY_PLAY );
        pauseString = PhetCommonProjectConfig.getInstance().getString( PROPERTY_PAUSE );
        String stepString = PhetCommonProjectConfig.getInstance().getString( PROPERTY_STEP );
        
        // Button icons
        BufferedImage playU = PhetCommonProjectConfig.getInstance().getImage( IMAGE_PLAY );
        BufferedImage pauseU = PhetCommonProjectConfig.getInstance().getImage( IMAGE_PAUSE );
        BufferedImage stepU = PhetCommonProjectConfig.getInstance().getImage( IMAGE_STEP );
        playIcon = new ImageIcon( playU );
        pauseIcon = new ImageIcon( pauseU );
        ImageIcon stepIcon = new ImageIcon( stepU );
        
        // Play/Pause button
        // Set this button to its maximum size so that the contents of
        // the control panel don't horizontally shift as the button state changes.
        {
            playPause = new JButton();
            
            // Get dimensions for "Play" state
            playPause.setText( playString );
            playPause.setIcon( playIcon );
            Dimension playSize = playPause.getPreferredSize();
            
            // Get dimensions for "Pause" state
            playPause.setText( pauseString );
            playPause.setIcon( pauseIcon );
            Dimension pauseSize = playPause.getPreferredSize();
            
            // Set max dimensions
            int maxWidth = (int) Math.max( playSize.getWidth(), pauseSize.getWidth() );
            int maxHeight = (int) Math.max( playSize.getHeight(), pauseSize.getHeight() );
            playPause.setPreferredSize( new Dimension( maxWidth, maxHeight ) );
        }
        
        // Step button
        step = new JButton( stepString, stepIcon );

        SwingUtils.fixButtonOpacity( playPause );
        SwingUtils.fixButtonOpacity( step );

        playPause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( clock.isPaused() ) {
                    clock.start();
                }
                else {
                    clock.pause();
                }
                updateButtons();
            }
        } );

        step.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( clock.isPaused() ) {
                    clock.stepClockWhilePaused();
                }
            }
        } );

        setLayout( new BorderLayout() );
        buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( playPause );
        buttonPanel.add( step );
        this.add( buttonPanel, BorderLayout.CENTER );

        updateButtons();
    }

    /**
     * Adds a component to the sub-panel which contains the main content for this control panel.
     * @param control
     */
    public void addControl(JComponent control){
        buttonPanel.add(control);
    }

    /**
     * Enables or disables the clock control panel.
     * When disabled, all buttons (play/pause/step) are also disabled.
     * When enabled, the buttons are enabled to correspond to the clock state.
     *
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        updateButtons();
    }
    
    public void addToLeft( JComponent component ) {
        add( component, BorderLayout.WEST );
    }
    
    public void addToRight( JComponent component ) {
        add( component, BorderLayout.EAST );
    }

    ////////////////////////////////////////////////////////////////////////////
    // Updaters
    //
    
    /*
     * Updates the state of the buttons to correspond to
     * the state of the clock and the control panel.
     */
    private void updateButtons() {
        boolean enabled = isEnabled();
        boolean isPaused = clock.isPaused();
        if( isPaused ) {
            playPause.setText( playString );
            playPause.setIcon( playIcon );
        }
        else {
            playPause.setText( pauseString );
            playPause.setIcon( pauseIcon );
        }
        playPause.setEnabled( enabled );
        step.setEnabled( enabled && isPaused );
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Event handlers
    //

    public void clockTicked( ClockEvent clockEvent ) {
    }

    public void clockStarted( ClockEvent clockEvent ) {
        updateButtons();
    }

    public void clockPaused( ClockEvent clockEvent ) {
        updateButtons();
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
    }
}
