/**
 * Class: RightMirrorReflectivityControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Apr 1, 2003
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class RightMirrorReflectivityControlPanel extends JPanel implements SimpleObserver {

    private JSlider reflectivitySlider;
    private JTextField reflectivityTF;
    private PartialMirror mirror;

    /**
     */
    public RightMirrorReflectivityControlPanel( final PartialMirror mirror ) {

        this.mirror = mirror;
        mirror.addObserver( this );
        reflectivityTF = new JTextField( 4 );
        reflectivityTF.setEditable( false );
        reflectivityTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = reflectivityTF.getFont();
        reflectivityTF.setFont( new Font( clockFont.getName(),
                                          LaserConfig.CONTROL_FONT_STYLE,
                                          LaserConfig.CONTROL_FONT_SIZE ) );
        reflectivityTF.setText( Double.toString( 10 ) );

        reflectivitySlider = new JSlider( JSlider.VERTICAL,
                                          0,
                                          100,
                                          50 );
        reflectivitySlider.setPreferredSize( new Dimension( 40, 50 ) );
        reflectivitySlider.setPaintTicks( true );
        reflectivitySlider.setMajorTickSpacing( 10 );
        reflectivitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                mirror.setReflectivity( ( (double)reflectivitySlider.getValue() ) / 100 );
                reflectivityTF.setText( Double.toString( reflectivitySlider.getValue() ) );
            }
        } );

        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        this.setLayout( new GridBagLayout() );
        JLabel title = new JLabel( SimStrings.get( "RightMirrorReflectivityControlPanel.BorderTitle" ) );
        gbc.gridwidth = 2;
        this.add( title, gbc );
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets( 0, 5, 0, 0 );
        this.add( reflectivitySlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets( 0, 0, 0, 5 );
        this.add( reflectivityTF, gbc );

        //        Border border = new TitledBorder( SimStrings.get( "RightMirrorReflectivityControlPanel.BorderTitle" ) );
        //        this.setBorder( border );
    }

    //    private void updateReflectivity( float reflectivity ) {
    //        new SetCavityReflectivityCmd( reflectivity ).doIt();
    //    }

    public void update() {
        int reflectivity = (int)( mirror.getReflectivity() * 100 );
        //        int reflectivity = (int)( cavity.getReflectivity() * 100 );
        if( reflectivitySlider.getValue() != reflectivity ) {
            reflectivityTF.setText( Integer.toString( reflectivity ) );
            reflectivitySlider.setValue( reflectivity );

        }
    }
}
