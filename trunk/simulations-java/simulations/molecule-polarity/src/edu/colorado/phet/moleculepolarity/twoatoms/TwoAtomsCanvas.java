// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.twoatoms;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.common.control.IsosurfaceControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.TestControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.BondTypeNode;
import edu.colorado.phet.moleculepolarity.common.view.DipoleNode.BondDipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode;
import edu.colorado.phet.moleculepolarity.common.view.PositivePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.TwoAtomMoleculeNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "One Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoAtomsCanvas extends MPCanvas {

    public TwoAtomsCanvas( TwoAtomsModel model, ViewProperties viewProperties, Frame parentFrame ) {
        super();

        TwoAtomMoleculeNode moleculeNode = new TwoAtomMoleculeNode( model.molecule );
        addChild( moleculeNode );

        final BondDipoleNode bondDipoleNode = new BondDipoleNode( model.molecule.bond );
        addChild( bondDipoleNode );

        final PartialChargeNode partialChargeNodeA = new PartialChargeNode( model.molecule.bond, model.molecule.atomA, false );
        addChild( partialChargeNodeA );

        final PartialChargeNode partialChargeNodeB = new PartialChargeNode( model.molecule.bond, model.molecule.atomB, true );
        addChild( partialChargeNodeB );

        ElectronegativityControlNode enControlA = new ElectronegativityControlNode( model.molecule.atomA, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        ElectronegativityControlNode enControlB = new ElectronegativityControlNode( model.molecule.atomB, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        addChild( enControlA );
        addChild( enControlB );

        final PNode bondTypeNode = new BondTypeNode( model.molecule );
        addChild( bondTypeNode );

        PNode viewControlsNode = new ControlPanelNode( new ViewControlPanel( viewProperties, false, true, false ) );
        addChild( viewControlsNode );

        PNode isosurfaceControlsNode = new ControlPanelNode( new IsosurfaceControlPanel( viewProperties.isosurfaceType ) );
        addChild( isosurfaceControlsNode );

        PNode testControlsNode = new ControlPanelNode( new TestControlPanel( model.eField.enabled ) );
        addChild( testControlsNode );

        Resettable[] resettables = new Resettable[] { model, viewProperties };
        PNode resetAllButtonNode = new ResetAllButtonNode( resettables, parentFrame, 16, Color.BLACK, Color.YELLOW );
        addChild( resetAllButtonNode );

        PNode negativePlateNode = new NegativePlateNode( model.eField );
        addChild( negativePlateNode );

        PNode positivePlateNode = new PositivePlateNode( model.eField );
        addChild( positivePlateNode );

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativePlateNode.setOffset( 30, 75 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            enControlA.setOffset( negativePlateNode.getFullBoundsReference().getMaxX() + xSpacing, 100 );
            enControlB.setOffset( enControlA.getFullBounds().getMaxX() + xSpacing, enControlA.getYOffset() );
            positivePlateNode.setOffset( enControlB.getFullBounds().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            bondTypeNode.setOffset( 150, negativePlateNode.getFullBoundsReference().getMaxY() ); //TODO compute horizontal center
            viewControlsNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + xSpacing, enControlA.getYOffset() );
            isosurfaceControlsNode.setOffset( viewControlsNode.getXOffset(), viewControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            testControlsNode.setOffset( isosurfaceControlsNode.getXOffset(), isosurfaceControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            resetAllButtonNode.setOffset( isosurfaceControlsNode.getXOffset(), testControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
        }

        viewProperties.bondDipolesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                bondDipoleNode.setVisible( visible );
            }
        } );

        viewProperties.partialChargesVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                partialChargeNodeA.setVisible( visible );
                partialChargeNodeB.setVisible( visible );
            }
        } );

        viewProperties.bondTypeVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                bondTypeNode.setVisible( visible );
            }
        } );
    }
}
