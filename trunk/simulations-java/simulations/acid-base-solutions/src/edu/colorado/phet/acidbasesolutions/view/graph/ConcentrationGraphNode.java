
package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;
import edu.colorado.phet.acidbasesolutions.model.equilibrium.*;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class ConcentrationGraphNode extends PComposite {

    private final NoSoluteConcentrationGraphNode waterNode;
    private final AcidConcentrationGraphNode acidNode;
    private final WeakBaseConcentrationGraphNode weakBaseNode;
    private final StrongBaseConcentrationGraphNode strongBaseNode;
    
    public ConcentrationGraphNode( PDimension outlineSize, AqueousSolution solution ) {
        this( outlineSize );
        solution.addSolutionListener( new ModelViewController( solution, this ) );
    }
    
    public ConcentrationGraphNode( PDimension outlineSize ) {
        super();
        
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        
        waterNode = new NoSoluteConcentrationGraphNode( outlineSize );
        addChild( waterNode );
        
        acidNode = new AcidConcentrationGraphNode( outlineSize );
        addChild( acidNode );
        
        weakBaseNode = new WeakBaseConcentrationGraphNode( outlineSize );
        addChild( weakBaseNode );
        
        strongBaseNode = new StrongBaseConcentrationGraphNode( outlineSize );
        addChild( strongBaseNode );
    }
    
    protected NoSoluteConcentrationGraphNode getWaterNode() {
        return waterNode;
    }
    
    protected AcidConcentrationGraphNode getAcidNode() {
        return acidNode;
    }
    
    protected WeakBaseConcentrationGraphNode getWeakBaseNode() {
        return weakBaseNode;
    }
    
    protected StrongBaseConcentrationGraphNode getStrongBaseNode() {
        return strongBaseNode;
    }
    
    private static class ModelViewController implements SolutionListener {

        private final AqueousSolution solution;
        private final ConcentrationGraphNode countsNode;
        
        public ModelViewController( AqueousSolution solution, ConcentrationGraphNode countsNode ) {
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
                NoSoluteConcentrationGraphNode node = countsNode.getWaterNode();
                node.setH3OConcentration( equilibriumModel.getH3OConcentration() );
                node.setOHConcentration( equilibriumModel.getOHConcentration() );
                node.setH2OConcentration( equilibriumModel.getH2OConcentration() );
            }
            else if ( equilibriumModel instanceof AcidEquilibriumModel ) {
                AcidConcentrationGraphNode node = countsNode.getAcidNode();
                AcidEquilibriumModel model = (AcidEquilibriumModel) equilibriumModel;
                // counts
                node.setAcidConcentration( model.getAcidConcentration() );
                node.setBaseConcentration( model.getBaseConcentration() );
                node.setH3OConcentration( model.getH3OConcentration() );
                node.setOHConcentration( model.getOHConcentration() );
                node.setH2OConcentration( model.getH2OConcentration() );
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
                WeakBaseConcentrationGraphNode node = countsNode.getWeakBaseNode();
                WeakBaseEquilibriumModel model = (WeakBaseEquilibriumModel) equilibriumModel;
                // counts
                node.setAcidConcentration( model.getAcidConcentration() );
                node.setBaseConcentration( model.getBaseConcentration() );
                node.setH3OConcentration( model.getH3OConcentration() );
                node.setOHConcentration( model.getOHConcentration() );
                node.setH2OConcentration( model.getH2OConcentration() );
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
                StrongBaseConcentrationGraphNode node = countsNode.getStrongBaseNode();
                StrongBaseEquilibriumModel model = (StrongBaseEquilibriumModel) equilibriumModel;
                // counts
                node.setBaseConcentration( model.getBaseConcentration() );
                node.setMetalConcentration( model.getMetalConcentration() );
                node.setH3OConcentration( model.getH3OConcentration() );
                node.setOHConcentration( model.getOHConcentration() );
                node.setH2OConcentration( model.getH2OConcentration() );
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
