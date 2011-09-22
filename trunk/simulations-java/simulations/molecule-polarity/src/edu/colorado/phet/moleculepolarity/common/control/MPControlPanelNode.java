// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that arranges multiple "floating" control panels in a vertical stack, left justified.
 * Optional "Reset All" button is added.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPControlPanelNode extends PNode {

    private static final int Y_SPACING = 10;

    public MPControlPanelNode( Frame parentFrame, Resettable[] resettables, MPVerticalPanel... panels ) {

        /*
         * Workaround for #3077 (PSwing clips labels in floating control panels)
         * Traverse the containment hierarchy and add a small amount of horizontal padding to each component.
         */
        for ( MPVerticalPanel panel : panels ) {
            for ( Component component : panel.getComponents() ) {
                SwingUtils.padPreferredWidthDeep( component, 5 );
            }
        }

        // Uniform width
        int minWidth = 0;
        for ( MPVerticalPanel panel : panels ) {
            minWidth = (int) Math.max( minWidth, panel.getPreferredSize().getWidth() );
        }

        PNode previousNode = null;
        for ( MPVerticalPanel panel : panels ) {

            // uniform width
            panel.setMinWidth( minWidth );

            // panel node
            PNode panelNode = new ControlPanelNode( panel );
            addChild( panelNode );

            // layout: vertical, left justified
            if ( previousNode != null ) {
                panelNode.setOffset( previousNode.getXOffset(), previousNode.getFullBoundsReference().getMaxY() + Y_SPACING );
            }
            previousNode = panelNode;
        }

        // Reset All button
        if ( resettables != null ) {
            PNode resetAllButtonNode = new MPResetAllButtonNode( resettables, parentFrame );
            addChild( resetAllButtonNode );
            resetAllButtonNode.setOffset( previousNode.getXOffset(), previousNode.getFullBoundsReference().getMaxY() + Y_SPACING );
        }
    }

    // Base class for all individual panels, vertical layout.
    public static abstract class MPVerticalPanel extends GridPanel {

        public MPVerticalPanel( String title ) {
            setGridX( 0 ); // vertical
            setAnchor( Anchor.WEST ); // left justified

            // title
            add( new JLabel( title ) {{
                setFont( MPConstants.TITLE_FONT );
            }} );

            // space between title and controls below it
            add( Box.createVerticalStrut( 5 ) );
        }

        // provided so that we can set a uniform width for all panels
        public void setMinWidth( int minWidth ) {
            add( Box.createHorizontalStrut( minWidth ) );
        }
    }

    // Encapsulates the look of all check boxes
    public static class MPCheckBox extends PropertyCheckBox {
        public MPCheckBox( String text, Property<Boolean> property ) {
            super( text, property );
            setFont( MPConstants.CONTROL_FONT );
        }
    }

    // Encapsulates the look of all radio buttons
    public static class MPRadioButton<T> extends PropertyRadioButton<T> {
        public MPRadioButton( String text, Property<T> property, T value ) {
            super( text, property, value );
            super.setFont( MPConstants.CONTROL_FONT );
        }
    }
}
