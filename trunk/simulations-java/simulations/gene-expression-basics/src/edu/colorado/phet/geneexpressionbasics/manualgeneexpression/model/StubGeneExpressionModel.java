// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * Gene expression model that doesn't do anything.  This is needed in cases
 * where we want to create biomolecules without needing a full blown model, such
 * as on control panels.
 *
 * @author John Blanco
 */
public class StubGeneExpressionModel extends GeneExpressionModel {
    @Override public DnaMolecule getDnaMolecule() {
        System.out.println( getClass().getName() + " - Warning: Unimplemented method called in stub class." );
        return null;
    }

    @Override public void addMobileBiomolecule( MobileBiomolecule mobileBiomolecule ) {
        System.out.println( getClass().getName() + " - Warning: Unimplemented method called in stub class." );
    }
}
