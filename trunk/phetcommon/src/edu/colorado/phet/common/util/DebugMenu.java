/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.LineGrid;
import edu.colorado.phet.common.view.util.MouseTracker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * DebugMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DebugMenu extends JMenu {
    private HashMap appPanelsToGrids = new HashMap();
    private PhetApplication app;

    public DebugMenu( final PhetApplication app ) {
        super( "Debug" );
        this.app = app;
        setMnemonic( 'D' );

        this.add( new GridMenuItem() );
        this.add( new MouseTrackerMenuItem() );
        this.add( new FrameRateMenuItem() );
        this.add( new OffscreenBufferMenuItem() );
    }

    private ApparatusPanel getApparatusPanel() {
        ApparatusPanel appPanel = app.getModuleManager().getActiveModule().getApparatusPanel();
        return appPanel;
    }


    //----------------------------------------------------------------
    // Menu Items
    //----------------------------------------------------------------

    /**
     * Displays/hides a 100 pixel-spaced grid
     */
    private class GridMenuItem extends JCheckBoxMenuItem {
        public GridMenuItem() {
            super( "Grid" );
            this.setMnemonic( 'G' );
            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ApparatusPanel appPanel = getApparatusPanel();
                    if( appPanel instanceof ApparatusPanel2 ) {
                        ApparatusPanel2 appPanel2 = (ApparatusPanel2)appPanel;
                        if( isSelected() ) {
                            LineGrid grid = new LineGrid( appPanel2, 100, 100, new Color( 0, 128, 0 ) );
                            appPanelsToGrids.put( appPanel2, grid );
                            appPanel2.addGraphic( grid );
                            appPanel.repaint();
                        }
                        else {
                            LineGrid grid = (LineGrid)appPanelsToGrids.get( appPanel2 );
                            appPanel2.removeGraphic( grid );
                            appPanelsToGrids.remove( appPanel2 );
                            appPanel2.repaint();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog( app.getPhetFrame(), "<html>This option only applies to modules<br>that use ApparatusPanel2</html>" );
                    }
                }
            } );
        }

    }

    /**
     * Shows/hides a MouseTracker
     */
    private class MouseTrackerMenuItem extends JCheckBoxMenuItem {
        private HashMap appPanelToTracker = new HashMap();

        public MouseTrackerMenuItem() {
            super( "Mouse tracker " );
            this.setMnemonic( 'M' );
            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ApparatusPanel appPanel = getApparatusPanel();
                    if( appPanel instanceof ApparatusPanel2 ) {
                        ApparatusPanel2 appPanel2 = (ApparatusPanel2)appPanel;
                        if( isSelected() ) {
                            MouseTracker tracker = new MouseTracker( appPanel2 );
                            appPanel.addGraphic( tracker, Double.MAX_VALUE );
                            appPanelToTracker.put( appPanel2, tracker );
                        }
                        else {
                            MouseTracker tracker = (MouseTracker)appPanelToTracker.get( appPanel );
                            appPanel.removeGraphic( tracker );
                            appPanelToTracker.remove( appPanel );
                        }
                    }
                }
            } );
        }
    }

    /**
     * Shows/hides a window that reports the frame rate every second
     */
    private class FrameRateMenuItem extends JCheckBoxMenuItem {
        private JDialog frameRateDlg;

        public FrameRateMenuItem() {
            super( "Show frame rate" );
            this.setMnemonic( 'f' );
            frameRateDlg = new JDialog( app.getPhetFrame(), "Frame Rate", false );
            JTextArea textArea = new JTextArea( 10, 5 );
            frameRateDlg.getContentPane().add( new JScrollPane( textArea,
                                                                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
            frameRateDlg.setUndecorated( true );
            frameRateDlg.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
            frameRateDlg.pack();

            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                        frameRateDlg.setVisible( isSelected() );
                }
            } );

            startRecording( app.getApplicationModel().getClock(), textArea );
        }

        void startRecording( AbstractClock clock, final JTextArea textArea ) {
            clock.addClockTickListener( new ClockTickListener() {
                int frameCnt = 0;
                long lastTickTime = System.currentTimeMillis();
                long averagingTime = 1000;

                public void clockTicked( ClockTickEvent event ) {
                    frameCnt++;
                    long currTime = System.currentTimeMillis();
                    if( currTime - lastTickTime > averagingTime ) {
                        double rate = frameCnt * 1000 / ( currTime - lastTickTime );
                        lastTickTime = currTime;
                        frameCnt = 0;
                        textArea.append( "    " + Double.toString( rate ) + "\n" );
                    }
                }
            } );
        }
    }

    private class OffscreenBufferMenuItem extends JCheckBoxMenuItem {
        public OffscreenBufferMenuItem() {
            super("Use offscreen buffer");
            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    PhetApplication app = PhetApplication.instance();
                    ModuleManager mm = app.getModuleManager();
                    for( int i = 0; i < mm.numModules(); i++ ) {
                        Module module = mm.moduleAt( i );
                        ApparatusPanel ap = module.getApparatusPanel();
                        if( ap instanceof ApparatusPanel2 ) {
                            ApparatusPanel2 ap2 = (ApparatusPanel2)ap;
                            ap2.setUseOffscreenBuffer( isSelected() );
                            ap2.revalidate();
                            ap2.repaint( ap2.getBounds() );
                        }
                    }
                }
            } );
        }
    }
}
