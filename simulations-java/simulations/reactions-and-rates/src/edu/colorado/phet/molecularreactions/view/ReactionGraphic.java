/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.MoleculeA;
import edu.colorado.phet.molecularreactions.model.MoleculeAB;
import edu.colorado.phet.molecularreactions.model.MoleculeBC;
import edu.colorado.phet.molecularreactions.model.MoleculeC;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.model.reactions.Reaction;
import edu.colorado.phet.molecularreactions.view.icons.MoleculeIcon;
import edu.colorado.phet.molecularreactions.view.icons.ReactionArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * ReactionGraphic
 * <p/>
 * A graphic that shows the mechanics of the reaction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReactionGraphic extends RegisterablePNode implements MRModel.ModelListener {
    private PImage aNode = new PImage();
    private PImage cNode = new PImage();
    private PImage abNode = new PImage();
    private PImage bcNode = new PImage();

    /**
     * @param reaction
     * @param arrowColor
     * @param model
     */
    public ReactionGraphic( Reaction reaction, Color arrowColor, MRModel model ) {

        model.addListener( this );
        if( reaction instanceof A_BC_AB_C_Reaction ) {
            Insets insets = new Insets( 0, 3, 0, 3 );
            setMoleculeImages( model.getEnergyProfile() );
            PNode arrowNode = new ReactionArrowNode( arrowColor );


            Font font = new PhetDefaultFont( Font.BOLD, 18 );
            PText plusA = new PText( "+" );
            plusA.setTextPaint( arrowColor );
            plusA.setFont( font );
            PText plusC = new PText( "+" );
            plusC.setFont( font );
            plusC.setTextPaint( arrowColor );

            aNode.setOffset( 0, -aNode.getFullBounds().getHeight() / 2 );
            setChainedOffset( plusA, aNode, insets );
            setChainedOffset( bcNode, plusA, insets );
            setChainedOffset( arrowNode, bcNode, insets );
            setChainedOffset( abNode, arrowNode, insets );
            setChainedOffset( plusC, abNode, insets );
            setChainedOffset( cNode, plusC, insets );

            addChild( aNode );
            addChild( cNode );
            addChild( abNode );
            addChild( bcNode );
            addChild( arrowNode );
            addChild( plusA );
            addChild( plusC );

            setRegistrationPoint( getFullBounds().getWidth() / 2, 0 );
        }
        else {
            throw new IllegalArgumentException( "Reaction not recognized" );
        }
    }

    private void setChainedOffset( PNode nodeToSet, PNode nodeToLeft, Insets insets ) {
        Point2D offset = new Point2D.Double( nodeToLeft.getOffset().getX() + nodeToLeft.getFullBounds().getWidth() + insets.left + insets.right,
                                             -nodeToSet.getFullBounds().getHeight() / 2 );
        nodeToSet.setOffset( offset );
    }

    private void setMoleculeImages( EnergyProfile profile ) {
        aNode.setImage( new MoleculeIcon( MoleculeA.class, profile ).getImage() );
        cNode.setImage( new MoleculeIcon( MoleculeC.class, profile ).getImage() );
        abNode.setImage( new MoleculeIcon( MoleculeAB.class, profile ).getImage() );
        bcNode.setImage( new MoleculeIcon( MoleculeBC.class, profile ).getImage() );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of MRModel.ModelListener
    //--------------------------------------------------------------------------------------------------

    public void notifyEnergyProfileChanged( EnergyProfile profile ) {
        setMoleculeImages( profile );
    }


    public void notifyDefaultTemperatureChanged( double newInitialTemperature ) {

    }
}
