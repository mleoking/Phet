// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.CellProteinSynthesisSimulator;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;

/**
 * Control panel that allows the user to change parameters related to
 * degradation of biomolecules in the multi-cell model.
 *
 * @author John Blanco
 */
public class DegradationParameterController extends CellParameterControlPanel {

    public DegradationParameterController( MultipleCellsModel model ) {
        // TODO: i18n
        super( "Degradation", new VBox(
                20,
                new DoubleParameterSliderNode( UserComponents.proteinDegradationRateSlider,
                                               CellProteinSynthesisSimulator.PROTEIN_DEGRADATION_RANGE.getMin(),
                                               CellProteinSynthesisSimulator.PROTEIN_DEGRADATION_RANGE.getMax(),
                                               model.proteinDegradationRate,
                                               "<center>Protein<br>Degradation Rate</center>" ),
                new LogarithmicParameterSliderNode( UserComponents.rnaDestroyerLevel,
                                                    CellProteinSynthesisSimulator.MRNA_DEGRADATION_RATE_RANGE.getMin(),
                                                    CellProteinSynthesisSimulator.MRNA_DEGRADATION_RATE_RANGE.getMax(),
                                                    model.mRnaDegradationRate,
                                                    "<center>mRNA Destroyer<br>Level</center>" )
        ) );
    }
}
