/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;
import edu.colorado.phet.naturalselection.dialog.PedigreeChartCanvas;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.view.LandscapeNode;
import edu.colorado.phet.naturalselection.view.NaturalSelectionCanvas;

/**
 * Wires up parts of the control panel, model and views
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionController {

    /**
     * Constructor
     *
     * @param model        The model
     * @param canvas       The main simulation canvas
     * @param controlPanel The control panel
     * @param module       The module itself
     */
    public NaturalSelectionController( final NaturalSelectionModel model, final NaturalSelectionCanvas canvas, final NaturalSelectionControlPanel controlPanel, final NaturalSelectionModule module ) {

        // if a new bunny is created in the model, we should create a sprite for it. (Also bushes and trees change
        // depending on environment and selection factors)
        model.addListener( canvas.landscapeNode );

        // if the environment changes, we need to modify the background
        model.addListener( canvas.backgroundNode );

        //----------------------------------------------------------------------------
        // Control panel buttons
        //----------------------------------------------------------------------------

        controlPanel.getClimatePanel().getArcticButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setClimate( NaturalSelectionModel.CLIMATE_ARCTIC );
            }
        } );

        controlPanel.getClimatePanel().getEquatorButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setClimate( NaturalSelectionModel.CLIMATE_EQUATOR );
            }
        } );

        controlPanel.getSelectionPanel().noneButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setSelectionFactor( NaturalSelectionModel.SELECTION_NONE );
            }
        } );

        controlPanel.getSelectionPanel().foodButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setSelectionFactor( NaturalSelectionModel.SELECTION_FOOD );
            }
        } );

        controlPanel.getSelectionPanel().wolvesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setSelectionFactor( NaturalSelectionModel.SELECTION_WOLVES );
            }
        } );

        controlPanel.getResetAllButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                module.reset();
            }
        } );

        canvas.getLandscapeNode().addListener( new LandscapeNode.Listener() {
            public void onBunnySelected( Bunny bunny ) {
                controlPanel.getPedigreeChart().displayBunny( bunny );
            }
        } );

        ColorGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                canvas.handleMutationChange( ColorGene.getInstance(), mutatable );
            }
        } );

        TailGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                canvas.handleMutationChange( TailGene.getInstance(), mutatable );
            }
        } );

        TeethGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                canvas.handleMutationChange( TeethGene.getInstance(), mutatable );
            }
        } );

        model.addListener( new NaturalSelectionModel.Listener() {
            public void onEvent( NaturalSelectionModel.Event event ) {
                if ( event.getType() == NaturalSelectionModel.Event.TYPE_GAME_OVER ) {
                    module.showGameOver();
                }
            }
        } );

        model.addListener( new NaturalSelectionModel.Listener() {
            public void onEvent( NaturalSelectionModel.Event event ) {
                if ( event.getType() == NaturalSelectionModel.Event.TYPE_BUNNIES_TAKE_OVER ) {
                    module.showBunniesTakeOver();
                }
            }
        } );

        ColorGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {
                if ( primary + secondary == 0 ) {
                    model.endGame();
                }
                else if ( primary + secondary > NaturalSelectionConstants.getSettings().getMaxPopulation() ) {
                    model.bunniesTakeOver();
                }
            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {

            }
        } );

        //----------------------------------------------------------------------------
        // When a mutation button is pressed, make sure to enable the corresponding part of the gene panel
        //----------------------------------------------------------------------------

        controlPanel.getMutationPanel().getColorButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getGenePanel().setColorEnabled( true );
            }
        } );

        controlPanel.getMutationPanel().getTailButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getGenePanel().setTailEnabled( true );
            }
        } );

        controlPanel.getMutationPanel().getTeethButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getGenePanel().setTeethEnabled( true );
            }
        } );

        //----------------------------------------------------------------------------
        // Interactions between the switcher and detachable panels
        //----------------------------------------------------------------------------

        // on switch from pedigree to statistics panel
        controlPanel.getSwitcherPanel().getStatisticsRadioButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getDetachPanel().showStaticChild();

                // remove the selection from the current bunny if it is selected
                if ( Bunny.getSelectedBunny() != null ) {
                    Bunny.getSelectedBunny().setSelected( false );
                }
            }
        } );

        // on switch from statistics to pedigree chart
        controlPanel.getSwitcherPanel().getPedigreeRadioButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getDetachPanel().showDetachableChild();

                // if necessary, cause the last selected bunny to be reselected
                PedigreeChartCanvas pedigreeChart = controlPanel.getPedigreeChart();
                Bunny lastBunny = pedigreeChart.getLastDisplayedBunny();
                if ( lastBunny != null ) {
                    if ( Bunny.getSelectedBunny() == null && lastBunny.isAlive() ) {
                        pedigreeChart.getLastDisplayedBunny().setSelected( true );
                    }
                }
            }
        } );

        // allow detach, reattach and closing events to be passed to the switcher panel
        controlPanel.getDetachPanel().addListener( controlPanel.getSwitcherPanel() );

    }

}
