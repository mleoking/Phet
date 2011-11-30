// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.colorado.phet.energyskatepark.view.swing.GridLinesCheckBox;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Misc controls for visibility of things in the view (charts, grid).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends ControlPanelNode {

    public ViewControlPanel( final EnergySkateParkBasicsModule module, final EnergySkateParkSimulationPanel energySkateParkSimulationPanel, final JDialog barChartDialog ) {
        super( new VBox(
                new HBox(
                        //Checkbox to show/hide bar chart
                        new PSwing( new JCheckBox( EnergySkateParkStrings.getString( "plots.bar-graph" ), module.isBarChartVisible() ) {{
                            setFont( EnergySkateParkBasicsModule.CONTROL_FONT );
                            addActionListener( new ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    module.setBarChartVisible( isSelected() );
                                }
                            } );
                            module.addResetListener( new VoidFunction0() {
                                public void apply() {
                                    setSelected( module.isPieChartVisible() );
                                }
                            } );
                            // set the check box state when the dialog is closed via its window dressing
                            barChartDialog.addWindowListener( new WindowAdapter() {
                                // called when the close button in the dialog's window dressing is clicked
                                public void windowClosing( WindowEvent e ) {
                                    setSelected( module.isPieChartVisible() );
                                }

                                // called by JDialog.dispose
                                public void windowClosed( WindowEvent e ) {
                                    setSelected( module.isPieChartVisible() );
                                }
                            } );
                        }} ),
                        new PImage( EnergySkateParkStrings.getImage( "icons/bar_icon.png" ) ) ),

                new HBox(
                        //Checkbox to show/hide the pie chart
                        new PSwing( new JCheckBox( EnergySkateParkStrings.getString( "pieChart" ), module.isPieChartVisible() ) {{
                            setFont( EnergySkateParkBasicsModule.CONTROL_FONT );
                            addActionListener( new ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    module.setPieChartVisible( isSelected() );
                                }
                            } );
                            module.addResetListener( new VoidFunction0() {
                                public void apply() {
                                    setSelected( module.isPieChartVisible() );
                                }
                            } );
                        }} ),
                        new PImage( EnergySkateParkStrings.getImage( "icons/pie_icon.png" ) ) ),

                new HBox(
                        //Checkbox to show/hide the grid lines
                        new PSwing( new GridLinesCheckBox( module ) {{checkBox.setFont( EnergySkateParkBasicsModule.CONTROL_FONT );}} ),
                        new PImage( EnergySkateParkStrings.getImage( "icons/grid_icon.png" ) ) )

        ), EnergySkateParkLookAndFeel.backgroundColor );

        //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
        energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
            public void apply() {
                setOffset( energySkateParkSimulationPanel.getWidth() - getFullBounds().getWidth() - EnergySkateParkBasicsModule.INSET, EnergySkateParkBasicsModule.INSET );
            }
        } );
    }
}
