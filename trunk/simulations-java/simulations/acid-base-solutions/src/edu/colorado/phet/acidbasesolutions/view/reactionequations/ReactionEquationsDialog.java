package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;


public class ReactionEquationsDialog extends PaintImmediateDialog {
    
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 650, 140 );
    private static final Dimension BOTTOM_CANVAS_SIZE = TOP_CANVAS_SIZE;
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    
    private final PhetPCanvas topCanvas, bottomCanvas;
    private AbstractReactionEquationNode soluteNode;
    private final WaterReactionEquationNode waterNode;
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    
    public ReactionEquationsDialog( Frame owner, final AqueousSolution solution ) {
        super( owner);
        setTitle( ABSStrings.TITLE_REACTION_EQUATIONS );
        setResizable( false );
        
        this.solution = solution;
        this.solutionListener = new SolutionListener() {
            
            public void soluteChanged() {
                handleSoluteChanged();
            }

            public void concentrationChanged() {
                handleConcentrationOrStrengthChanged();
            }

            public void strengthChanged() {
                handleConcentrationOrStrengthChanged();
            }
        };
        this.solution.addSolutionListener( solutionListener );
        
        // scale on/off
        JLabel scaleOnOffLabel = new JLabel( ABSStrings.LABEL_EQUATION_SCALING );
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleScalingEnabled();
            }
        };
        scaleOnRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_ON );
        scaleOnRadioButton.addActionListener( scaleOnOffActionListener );
        scaleOffRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_OFF );
        scaleOffRadioButton.addActionListener( scaleOnOffActionListener );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( scaleOffRadioButton );
        buttonGroup.add( scaleOnRadioButton );
        scaleOffRadioButton.setSelected( true );
        JPanel scaleOnOffPanel = new JPanel( new GridBagLayout() );
        scaleOnOffPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = GridBagConstraints.RELATIVE;
        scaleOnOffPanel.add( scaleOnOffLabel );
        scaleOnOffPanel.add( scaleOnRadioButton );
        scaleOnOffPanel.add( scaleOffRadioButton );
        
        // top canvas
        topCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                super.updateLayout();
                updateTopLayout();
            }
        };
        topCanvas.setPreferredSize( TOP_CANVAS_SIZE );
        topCanvas.setBackground( ABSConstants.REACTION_EQUATIONS_BACKGROUND );
        
        // solute equation, set based on solution
        soluteNode = null;
        
        // bottom canvas
        bottomCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                super.updateLayout();
                updateBottomLayout();
            }
        };
        bottomCanvas.setPreferredSize( BOTTOM_CANVAS_SIZE );
        bottomCanvas.setBackground( ABSConstants.REACTION_EQUATIONS_BACKGROUND );
        
        // water equation
        waterNode = new WaterReactionEquationNode( solution );
        bottomCanvas.getLayer().addChild( waterNode );
        
        // layout
        JPanel canvasPanel = new JPanel( new GridLayout( 0, 1 ) );
        canvasPanel.add( topCanvas );
        canvasPanel.add( bottomCanvas);
        JPanel userPanel = new JPanel( new BorderLayout() );
        userPanel.add( scaleOnOffPanel, BorderLayout.NORTH );
        userPanel.add( canvasPanel, BorderLayout.CENTER );
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( userPanel, BorderLayout.CENTER );
            
        handleSoluteChanged();
        
        getContentPane().add( mainPanel, BorderLayout.CENTER );
        pack();
    }
    
    private void cleanup() {
        solution.removeSolutionListener( solutionListener );
    }
    
    public void dispose() {
        cleanup();
        super.dispose();
    }
    
    private void handleSoluteChanged() {
        
        // remove any existing solute equation
        if ( soluteNode != null ) {
            topCanvas.getLayer().removeChild( soluteNode );
            soluteNode = null;
        }
        
        // create the proper type of solute equation 
        if ( solution.isAcidic() ) {
            soluteNode = new AcidReactionEquationNode( solution );
        }
        else if ( solution.isBasic() ) {
            soluteNode = new BaseReactionEquationNode( solution );
        }
        
        // add the new solute equation
        if ( soluteNode != null ) {
            soluteNode.setScalingEnabled( isScalingEnabled() );
            topCanvas.getLayer().addChild( soluteNode );
            updateTopLayout();
        }
        
        // update the water equation
        waterNode.update();
    }
    
    private void handleConcentrationOrStrengthChanged() {
        if ( soluteNode != null ) {
            soluteNode.update();
        }
        waterNode.update();
    }
    
    public void setScalingEnabled( boolean enabled ) {
        scaleOnRadioButton.setSelected( enabled );
        handleScalingEnabled();
    }
    
    public boolean isScalingEnabled() {
        return scaleOnRadioButton.isSelected();
    }
    
    private void handleScalingEnabled() {
        if ( soluteNode != null ) {
            soluteNode.setScalingEnabled( isScalingEnabled() );
        }
        waterNode.setScalingEnabled( isScalingEnabled() );
    }
    
    private void updateTopLayout() {
       centerEquation( soluteNode, topCanvas );
    }
    
    private void updateBottomLayout() {
        centerEquation( waterNode, bottomCanvas );
    }
    
    private static void centerEquation( AbstractReactionEquationNode node, PCanvas canvas ) {
        if ( node != null ) {
            final boolean isScalingEnabled = node.isScalingEnabled();
            node.setScalingEnabled( false ); // do the layout with scaling off
            double xOffset = ( canvas.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
            double yOffset = ( ( canvas.getHeight() - node.getFullBoundsReference().getHeight() ) / 2 ) - PNodeUtils.getOriginYOffset( node );
            node.setOffset( xOffset, yOffset );
            node.setScalingEnabled( isScalingEnabled ); // restore scaling
        }
    }
}
