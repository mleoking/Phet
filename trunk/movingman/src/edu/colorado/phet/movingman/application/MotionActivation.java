package edu.colorado.phet.movingman.application;


import edu.colorado.phet.movingman.application.motionsuites.MotionSuite;
import edu.colorado.phet.movingman.application.motionsuites.OscillateSuite;
import edu.colorado.phet.movingman.common.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:33:04 PM
 * To change this template use Options | File Templates.
 */
public class MotionActivation {
    private MovingManModule module;
    private JDialog dialog;
    private static ArrayList dialogs = new ArrayList();

    public MotionActivation( MovingManModule module ) {
        this.module = module;
    }

    public void setupInDialog( MotionSuite mac, final MovingManControlPanel controls ) {
        if( dialog != null ) {
            dialog.setVisible( false );
            dialog.dispose();
            dialog = null;
        }
        dialog = new JDialog( (Frame)SwingUtilities.getWindowAncestor( module.getApparatusPanel() ), "Controls", false );
        dialog.addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                repaintLater();
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowActivated( WindowEvent e ) {
                repaintNowAndLater();
            }

            public void windowGainedFocus( WindowEvent e ) {
                repaintNowAndLater();
            }

            public void windowOpened( WindowEvent e ) {
                repaintNowAndLater();
            }

            public void windowDeiconified( WindowEvent e ) {
                repaintNowAndLater();

            }
        } );
        dialogs.add( dialog );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                if( module.isMotionMode() ) {
                    module.setPauseMode();
                }
                controls.resetComboBox();
            }
        } );
        controls.getInitialPositionSpinner().setValue( new Double( module.getMan().getX() ) );

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        if( !( mac instanceof OscillateSuite ) ) {
            panel.add( controls.getInitialPositionSpinner() );
        }
        panel.add( mac.getControlPanel() );

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
        buttonPanel.add( controls.getStartMotionButton() );
        buttonPanel.add( controls.getAnotherPauseButton() );

        panel.add( buttonPanel );
        dialog.setContentPane( panel );
        dialog.setTitle( mac.getName() );
        dialog.setBackground( Color.yellow );
        dialog.pack();

        JFrame f = (JFrame)SwingUtilities.getWindowAncestor( module.getApparatusPanel() );
        centerInParent( dialog, f );
        moveRight( dialog );
        dialog.setVisible( true );
        MovingManControlPanel mainPanel = module.getMovingManControlPanel();
        mainPanel.getStartMotionButton().setEnabled( true );
        controls.getAnotherPauseButton().setEnabled( false );
        module.setMotionMode( mac );//.getStepMotion());
        mac.initialize( module.getMan() );
        module.setPauseMode();
        SwingUtilities.updateComponentTreeUI( dialog );
        repaintDialog();
        repaintLater();
    }

    private void repaintNowAndLater() {
        repaintDialog();
        repaintLater();
    }

    private void repaintLater() {
        Thread t = new Thread( new Runnable() {
            public void run() {
                try {
                    Thread.sleep( 150 );
                    repaintDialog();
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();

    }

    private void repaintDialog() {
        dialog.invalidate();
        dialog.validate();
        dialog.repaint();
        Container pan = dialog.getContentPane();
        pan.invalidate();
        pan.validate();
        pan.repaint();
    }

    private void moveRight( JDialog dialog ) {
        int width = dialog.getWidth();
        Window ancestor = SwingUtilities.getWindowAncestor( module.getApparatusPanel() );
        int x = ancestor.getWidth() + ancestor.getX() - width;
        dialog.setLocation( x, dialog.getY() );
    }

    public static void centerInParent( JDialog dialog, JFrame parent ) {
        Rectangle frameBounds = parent.getBounds();
        Rectangle dialogBounds = new Rectangle( (int)( frameBounds.getMinX() + frameBounds.getWidth() / 2 - dialog.getWidth() / 2 ),
                                                (int)( frameBounds.getMinY() + frameBounds.getHeight() / 2 - dialog.getHeight() / 2 ),
                                                dialog.getWidth(), dialog.getHeight() );
        dialog.setBounds( dialogBounds );
    }

    public void clearDialogs() {
        for( int i = 0; i < dialogs.size(); i++ ) {
            JDialog jDialog = (JDialog)dialogs.get( i );
            jDialog.setVisible( false );
        }
    }
}
