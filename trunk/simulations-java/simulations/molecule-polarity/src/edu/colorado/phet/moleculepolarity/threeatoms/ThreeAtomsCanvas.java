// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.EFieldControlPanel;
import edu.colorado.phet.moleculepolarity.common.control.ElectronegativityControlNode;
import edu.colorado.phet.moleculepolarity.common.control.MPResetAllButtonNode;
import edu.colorado.phet.moleculepolarity.common.control.ViewControlPanel;
import edu.colorado.phet.moleculepolarity.common.view.BondDipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.DiatomicIsosurfaceNode;
import edu.colorado.phet.moleculepolarity.common.view.MPCanvas;
import edu.colorado.phet.moleculepolarity.common.view.MolecularDipoleNode;
import edu.colorado.phet.moleculepolarity.common.view.NegativePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.PartialChargeNode;
import edu.colorado.phet.moleculepolarity.common.view.PositivePlateNode;
import edu.colorado.phet.moleculepolarity.common.view.SurfaceColorKeyNode.ElectronDensityColorKeyNode;
import edu.colorado.phet.moleculepolarity.common.view.SurfaceColorKeyNode.ElectrostaticPotentialColorKeyNode;
import edu.colorado.phet.moleculepolarity.common.view.TriatomicMoleculeNode;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Three Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThreeAtomsCanvas extends MPCanvas {

    private static final double DIPOLE_SCALE = 1.0; // how much to scale the dipoles in the view

    public ThreeAtomsCanvas( ThreeAtomsModel model, ViewProperties viewProperties, Frame parentFrame ) {
        super();

        // nodes
        PNode negativePlateNode = new NegativePlateNode( model.eField );
        PNode positivePlateNode = new PositivePlateNode( model.eField );
        TriatomicMoleculeNode moleculeNode = new TriatomicMoleculeNode( model.molecule );
        final PartialChargeNode partialChargeNodeA = new PartialChargeNode( model.molecule.atomA, model.molecule.bondAB );
        final PartialChargeNode partialChargeNodeC = new PartialChargeNode( model.molecule.atomC, model.molecule.bondBC );
        final BondDipoleNode bondDipoleABNode = new BondDipoleNode( model.molecule.bondAB, DIPOLE_SCALE );
        final BondDipoleNode bondDipoleBCNode = new BondDipoleNode( model.molecule.bondBC, DIPOLE_SCALE );
        final MolecularDipoleNode molecularDipoleNode = new MolecularDipoleNode( model.molecule, DIPOLE_SCALE );
        final PNode electrostaticPotentialNode = new DiatomicIsosurfaceNode( model.molecule );
        final PNode electronDensityNode = new DiatomicIsosurfaceNode( model.molecule );
        ElectronegativityControlNode enControlA = new ElectronegativityControlNode( model.molecule.atomA, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        ElectronegativityControlNode enControlB = new ElectronegativityControlNode( model.molecule.atomB, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        ElectronegativityControlNode enControlC = new ElectronegativityControlNode( model.molecule.atomC, model.molecule, MPConstants.ELECTRONEGATIVITY_RANGE, MPConstants.ELECTRONEGATIVITY_SNAP_INTERVAL );
        PNode viewControlsNode = new ControlPanelNode( new ViewControlPanel( viewProperties, true, false, false, false, MPStrings.BOND_DIPOLES ) );
        PNode eFieldControlsNode = new ControlPanelNode( new EFieldControlPanel( model.eField.enabled ) );
        PNode resetAllButtonNode = new MPResetAllButtonNode( new Resettable[] { model, viewProperties }, parentFrame );
        final PNode electrostaticPotentialColorKeyNode = new ElectrostaticPotentialColorKeyNode();
        final PNode electronDensityColorKeyNode = new ElectronDensityColorKeyNode();

        // rendering order
        {
            // plates
            addChild( negativePlateNode );
            addChild( positivePlateNode );

            // controls
            addChild( enControlA );
            addChild( enControlB );
            addChild( enControlC );
            addChild( viewControlsNode );
            addChild( eFieldControlsNode );
            addChild( resetAllButtonNode );

            // indicators
            addChild( electrostaticPotentialColorKeyNode );
            addChild( electronDensityColorKeyNode );

            // molecule
            addChild( electrostaticPotentialNode );
            addChild( electronDensityNode );
            addChild( moleculeNode );
            addChild( partialChargeNodeA );
            addChild( partialChargeNodeC );
            addChild( bondDipoleABNode );
            addChild( bondDipoleBCNode );
            addChild( molecularDipoleNode );
        }

        // layout
        {
            final double xSpacing = 50;
            final double ySpacing = 10;
            negativePlateNode.setOffset( 30, 100 - PNodeLayoutUtils.getOriginYOffset( negativePlateNode ) );
            enControlA.setOffset( negativePlateNode.getFullBoundsReference().getMaxX() + xSpacing, 50 );
            enControlB.setOffset( enControlA.getFullBounds().getMaxX() + 10, enControlA.getYOffset() );
            enControlC.setOffset( enControlB.getFullBounds().getMaxX() + 10, enControlB.getYOffset() );
            positivePlateNode.setOffset( enControlC.getFullBounds().getMaxX() + xSpacing, negativePlateNode.getYOffset() );
            electrostaticPotentialColorKeyNode.setOffset( model.molecule.getLocation().getX() - ( electrostaticPotentialColorKeyNode.getFullBoundsReference().getWidth() / 2 ),
                                                          negativePlateNode.getFullBoundsReference().getMaxY() );
            electronDensityColorKeyNode.setOffset( electrostaticPotentialColorKeyNode.getOffset() );
            viewControlsNode.setOffset( positivePlateNode.getFullBoundsReference().getMaxX() + xSpacing, positivePlateNode.getYOffset() );
            eFieldControlsNode.setOffset( viewControlsNode.getXOffset(), viewControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
            resetAllButtonNode.setOffset( viewControlsNode.getXOffset(), eFieldControlsNode.getFullBoundsReference().getMaxY() + ySpacing );
        }

        // synchronize with view properties
        {
            viewProperties.bondDipolesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    bondDipoleABNode.setVisible( visible );
                    bondDipoleBCNode.setVisible( visible );
                }
            } );

            viewProperties.molecularDipoleVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    molecularDipoleNode.setVisible( visible );
                }
            } );

            viewProperties.partialChargesVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    partialChargeNodeA.setVisible( visible );
                    //TODO set visibility of partial changes for atom B
                    partialChargeNodeC.setVisible( visible );
                }
            } );

            viewProperties.isosurfaceType.addObserver( new VoidFunction1<SurfaceType>() {
                public void apply( SurfaceType isosurfaceType ) {
                    electrostaticPotentialNode.setVisible( isosurfaceType == SurfaceType.ELECTROSTATIC_POTENTIAL );
                    electrostaticPotentialColorKeyNode.setVisible( electrostaticPotentialNode.getVisible() );
                    electronDensityNode.setVisible( isosurfaceType == SurfaceType.ELECTRON_DENSITY );
                    electronDensityColorKeyNode.setVisible( electronDensityNode.getVisible() );
                }
            } );
        }
    }
}
