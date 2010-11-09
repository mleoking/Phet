/* Copyright 2009, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.greenhouse.GreenhouseDefaults;
import edu.colorado.phet.greenhouse.model.Molecule;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas on which the interaction between molecules and light (photons) is
 * depicted.
 *
 * @author John Blanco
 */
public class MoleculesAndLightCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final double FLASHLIGHT_WIDTH = 300;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // Model-view transform.
    private final ModelViewTransform2D mvt;

    // Local root node for world children.
    PNode myWorldNode;

    // Layers for the canvas.
    private final PNode moleculeLayer;
    private final PNode photonLayer;
    private final PNode photonEmitterLayer;

    // Data structures that match model objects to their representations in
    // the view.
    private final HashMap<Photon, PhotonNode> photonMap = new HashMap<Photon, PhotonNode>();
    private final HashMap<Molecule, MoleculeNode> moleculeMap = new HashMap<Molecule, MoleculeNode>();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param photonAbsorptionModel - Model that is being portrayed on this canvas.
     */
    public MoleculesAndLightCanvas( PhotonAbsorptionModel photonAbsorptionModel ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteringBoxStrategy( this, GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE ) );

        // Set up the model-canvas transform.  The multiplier values below can
        // be used to shift the center of the play area right or left, and the
        // scale factor can be used to essentially zoom in or out.
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.width * 0.65 ),
                (int) Math.round( GreenhouseDefaults.INTERMEDIATE_RENDERING_SIZE.height * 0.3 ) ),
                0.18, // Scale factor - smaller numbers "zoom out", bigger ones "zoom in".
        true );

        setBackground( Color.BLACK );

        // Listen to the model for notifications that we care about.
        photonAbsorptionModel.addListener( new PhotonAbsorptionModel.Adapter() {

            @Override
            public void photonRemoved( Photon photon ) {
                if ( photonLayer.removeChild( photonMap.get( photon ) ) == null ) {
                    System.out.println( getClass().getName() + " - Error: PhotonNode not found for photon." );
                }
                photonMap.remove( photon );
            }

            @Override
            public void photonAdded( Photon photon ) {
                PhotonNode photonNode = new PhotonNode( photon, mvt );
                photonLayer.addChild( photonNode );
                photonMap.put( photon, photonNode );
            }

            @Override
            public void moleculeRemoved( Molecule molecule ) {
                if ( moleculeLayer.removeChild( moleculeMap.get( molecule ) ) == null ) {
                    System.out.println( getClass().getName() + " - Error: MoleculeNode not found for molecule." );
                }
                moleculeMap.remove( molecule );
            }

            @Override
            public void moleculeAdded( Molecule molecule ) {
                addMolecule( molecule );
            }
        } );

        // Create the node that will be the root for all the world children on
        // this canvas.  This is done to make it easier to zoom in and out on
        // the world without affecting screen children.
        myWorldNode = new PNode();
        addWorldChild( myWorldNode );

        // Add the layers.
        moleculeLayer = new PNode();
        myWorldNode.addChild( moleculeLayer );
        photonLayer = new PNode();
        myWorldNode.addChild( photonLayer );
        photonEmitterLayer = new PNode();
        myWorldNode.addChild( photonEmitterLayer );

        // Add the photon emitter.
        PNode flashlightNode = new PhotonEmitterNode( FLASHLIGHT_WIDTH, mvt, photonAbsorptionModel );
        flashlightNode.setOffset( mvt.modelToViewDouble( photonAbsorptionModel.getPhotonEmissionLocation() ) );
        photonEmitterLayer.addChild( flashlightNode );

        // Add in the initial molecule(s).
        for ( Molecule molecule : photonAbsorptionModel.getMolecules() ) {
            addMolecule( molecule );
        }

        // Update the layout.
        updateLayout();
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    private void addMolecule( Molecule molecule ) {
        MoleculeNode moleculeNode = new MoleculeNode( molecule, mvt );
        moleculeLayer.addChild( moleculeNode );
        moleculeMap.put( molecule, moleculeNode );
    }
}
