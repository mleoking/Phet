package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class WeakBaseConcentrationGraphNode extends AbstractConcentrationGraphNode{

    private static final int BASE_INDEX = 0;
    private static final int ACID_INDEX = 1;
    
    public WeakBaseConcentrationGraphNode() {
        super();
        setMolecule( BASE_INDEX, ABSImages.B_MOLECULE, ABSSymbols.B, ABSConstants.B_COLOR );
        setMolecule( ACID_INDEX, ABSImages.BH_PLUS_MOLECULE, ABSSymbols.BH_PLUS, ABSConstants.BH_COLOR );
    }
    
    public void setBaseConcentration( double concentration ) {
        setConcentration( BASE_INDEX, concentration );
    }
    
    public void setAcidConcentration( double concentration ) {
        setConcentration( ACID_INDEX, concentration );
    }
}
