/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.PhetLineBorder;
import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * Control panel template.
 */
public class GravityAndOrbitsControlPanel extends VerticalLayoutPanel {
    public static Color BACKGROUND = new Color( 3, 0, 133 );
    public static Color FOREGROUND = Color.white;
    public static final Font CONTROL_FONT = new PhetFont( 16, true );

    public GravityAndOrbitsControlPanel( final GravityAndOrbitsModule module, GravityAndOrbitsModel model, GravityAndOrbitsMode mode ) {
        super();

        setFillNone();
        setAnchor( GridBagConstraints.WEST );
        // add mode check-boxes
        for ( GravityAndOrbitsMode m : module.getModes() ) {
            addControlFullWidth( m.newComponent( module.getModeProperty() ) );
        }
        setFillHorizontal();

        addControlFullWidth( new VerticalLayoutPanel() {{
            setBackground( BACKGROUND );
            setOpaque( false );
            setBorder( new PhetTitledBorder( new PhetLineBorder( Color.white ), "Show" ) {{
                setTitleColor( Color.white );
                setTitleFont( CONTROL_FONT );
            }} );
            setFillNone();
            setAnchor( GridBagConstraints.WEST );

            add( new JPanel( new GridLayout( 2, 2 ) ) {
                {
                    setBackground( BACKGROUND );
                    setOpaque( false );

                    add( new GOCheckBox( "Gravity Force", module.getShowGravityForceProperty() ) );
                    addArrow( PhetColorScheme.GRAVITATIONAL_FORCE );
                    add( new GOCheckBox( "Velocity", module.getShowVelocityProperty() ) );
                    addArrow( PhetColorScheme.VELOCITY );
                    setMaximumSize( getPreferredSize() );
                }

                private void addArrow( final Color color ) {
                    add( new JLabel( new ImageIcon( new ArrowNode( new Point2D.Double(), new Point2D.Double( 65, 0 ), 15, 15, 5, 2, true ) {{
                        setPaint( color );
                        setStrokePaint( Color.darkGray );
                    }}.toImage() ) ) );
                }
            } );
            add( new GOCheckBox( "Mass", module.getShowMassProperty() ) );
            add( new GOCheckBox( "Path", module.getShowPathProperty() ) );
        }} );

        addControlFullWidth( new VerticalLayoutPanel() {{
            setBackground( BACKGROUND );
            setOpaque( false );
            setBorder( new PhetTitledBorder( new PhetLineBorder( Color.white ), "Scale" ) {{
                setTitleColor( Color.white );
                setTitleFont( CONTROL_FONT );
            }} );
            setFillNone();
            setAnchor( GridBagConstraints.WEST );

            add( new GORadioButton<Scale>( "Cartoon (not to scale)", module.getScaleProperty(), Scale.CARTOON ) );
            add( new GORadioButton<Scale>( "Real", module.getScaleProperty(), Scale.REAL ) );
        }} );
        for ( Body body : model.getBodies() ) {
            if ( body.isMassSettable() ) {
                addControlFullWidth( new BodyMassControl( body, body.getMassProperty().getDefaultValue() / 2, body.getMassProperty().getDefaultValue() * 2, "Large", "Very Large" ) );
            }
        }
        setBackground( BACKGROUND );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }
}