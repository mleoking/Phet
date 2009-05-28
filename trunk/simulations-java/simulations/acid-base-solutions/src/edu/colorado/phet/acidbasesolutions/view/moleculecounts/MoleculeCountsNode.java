
package edu.colorado.phet.acidbasesolutions.view.moleculecounts;

import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.*;
import edu.umd.cs.piccolox.nodes.PComposite;


public class MoleculeCountsNode extends PComposite {

    private final NoSoluteMoleculeCountsNode waterNode;
    private final AcidMoleculeCountsNode acidNode;
    private final WeakBaseMoleculeCountsNode weakBaseNode;
    private final StrongBaseMoleculeCountsNode strongBaseNode;
    
    public MoleculeCountsNode( AqueousSolution solution ) {
        this();
        solution.addSolutionListener( new ModelViewController( solution, this ) );
    }
    
    public MoleculeCountsNode() {
        super();
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        waterNode = new NoSoluteMoleculeCountsNode();
        addChild( waterNode );
        
        acidNode = new AcidMoleculeCountsNode();
        addChild( acidNode );
        
        weakBaseNode = new WeakBaseMoleculeCountsNode();
        addChild( weakBaseNode );
        
        strongBaseNode = new StrongBaseMoleculeCountsNode();
        addChild( strongBaseNode );
    }
    
    protected NoSoluteMoleculeCountsNode getWaterNode() {
        return waterNode;
    }
    
    protected AcidMoleculeCountsNode getAcidNode() {
        return acidNode;
    }
    
    protected WeakBaseMoleculeCountsNode getWeakBaseNode() {
        return weakBaseNode;
    }
    
    protected StrongBaseMoleculeCountsNode getStrongBaseNode() {
        return strongBaseNode;
    }
    
    private static class ModelViewController implements SolutionListener {

        private final AqueousSolution solution;
        private final MoleculeCountsNode countsNode;
        
        public ModelViewController( AqueousSolution solution, MoleculeCountsNode countsNode ) {
            this.solution = solution;
            this.countsNode = countsNode;
            updateView();
        }
        
        public void soluteChanged() {
            updateView();
        }
        
        public void concentrationChanged() {
            updateView();
        }

        public void strengthChanged() {
            updateView();
        }
        
        private void updateView() {
            
            EquilibriumModel equilibriumModel = solution.getEquilibriumModel();
            
            // visibility
            countsNode.getWaterNode().setVisible( equilibriumModel instanceof PureWaterEquilibriumModel );
            countsNode.getAcidNode().setVisible( equilibriumModel instanceof AcidEquilibriumModel );
            countsNode.getWeakBaseNode().setVisible( equilibriumModel instanceof WeakBaseEquilibriumModel );
            countsNode.getStrongBaseNode().setVisible( equilibriumModel instanceof StrongBaseEquilibriumModel );
            
            // counts & labels
            if ( equilibriumModel instanceof PureWaterEquilibriumModel ) {
                NoSoluteMoleculeCountsNode node = countsNode.getWaterNode();
                node.setH3OCount( equilibriumModel.getH3OMoleculeCount() );
                node.setOHCount( equilibriumModel.getOHMoleculeCount() );
                node.setH2OCount( equilibriumModel.getH2OMoleculeCount() );
            }
            else if ( equilibriumModel instanceof AcidEquilibriumModel ) {
                AcidMoleculeCountsNode node = countsNode.getAcidNode();
                AcidEquilibriumModel model = (AcidEquilibriumModel) equilibriumModel;
                // counts
                node.setAcidCount( model.getAcidMoleculeCount() );
                node.setBaseCount( model.getBaseMoleculeCount() );
                node.setH3OCount( model.getH3OMoleculeCount() );
                node.setOHCount( model.getOHMoleculeCount() );
                node.setH2OCount( model.getH2OMoleculeCount() );
                // labels
                node.setAcidLabel( solution.getSolute().getSymbol() );
                Solute solute = solution.getSolute();
                if ( solute instanceof Acid ) {
                    node.setBaseLabel( ((Acid)solution.getSolute()).getConjugateSymbol() );
                }
                else {
                    throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
                }
            }
            else if ( equilibriumModel instanceof WeakBaseEquilibriumModel ) {
                WeakBaseMoleculeCountsNode node = countsNode.getWeakBaseNode();
                WeakBaseEquilibriumModel model = (WeakBaseEquilibriumModel) equilibriumModel;
                // counts
                node.setAcidCount( model.getAcidMoleculeCount() );
                node.setBaseCount( model.getBaseMoleculeCount() );
                node.setH3OCount( model.getH3OMoleculeCount() );
                node.setOHCount( model.getOHMoleculeCount() );
                node.setH2OCount( model.getH2OMoleculeCount() );
                // labels
                node.setBaseLabel( solution.getSolute().getSymbol() );
                Solute solute = solution.getSolute();
                if ( solute instanceof WeakBase ) {
                    node.setAcidLabel( ((WeakBase)solution.getSolute()).getConjugateSymbol() );
                }
                else if ( solute instanceof CustomBase ) {
                    node.setAcidLabel( ((CustomBase)solution.getSolute()).getConjugateSymbol() );
                }
                else {
                    throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
                }
            }
            else if ( equilibriumModel instanceof StrongBaseEquilibriumModel ) {
                StrongBaseMoleculeCountsNode node = countsNode.getStrongBaseNode();
                StrongBaseEquilibriumModel model = (StrongBaseEquilibriumModel) equilibriumModel;
                // counts
                node.setBaseCount( model.getBaseMoleculeCount() );
                node.setMetalCount( model.getMetalMoleculeCount() );
                node.setH3OCount( model.getH3OMoleculeCount() );
                node.setOHCount( model.getOHMoleculeCount() );
                node.setH2OCount( model.getH2OMoleculeCount() );
                // labels
                node.setBaseLabel( solution.getSolute().getSymbol() );
                Solute solute = solution.getSolute();
                if ( solute instanceof StrongBase ) {
                    node.setMetalLabel( ((StrongBase)solution.getSolute()).getMetalSymbol() );
                }
                else if ( solute instanceof CustomBase ) {
                    node.setMetalLabel( ((CustomBase)solution.getSolute()).getMetalSymbol() );
                }
                else {
                    throw new IllegalStateException( "unexpected solute type: " + solute.getClass().getName() );
                }
            }
            else { 
                throw new UnsupportedOperationException( "unsupported concentration model type: " + equilibriumModel.getClass().getName() );
            }
        }
    }
}
