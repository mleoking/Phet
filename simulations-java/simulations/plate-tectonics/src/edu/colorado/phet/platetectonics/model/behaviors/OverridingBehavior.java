// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.regions.MagmaRegion;

public class OverridingBehavior extends PlateBehavior {

    private MagmaRegion magmaChamber;
    private float magmaCenterX;

    public OverridingBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate ) {
        super( plate, otherPlate );

        getLithosphere().moveToFront();
        getCrust().moveToFront();

        magmaCenterX = 0; // TODO: update with values based on plate type!

//        magmaChamber = new MagmaRegion( plate.getTextureStrategy(), getMagmaChamberScale(), (float) ( Math.PI / 2 ), 16,
//                                        new ImmutableVector2F( 0, plate.getPlateType().getCrustTopY() ) );
//        plate.regions.add( magmaChamber );
//        magmaChamber.moveToFront();
    }

    private float getMagmaChamberScale() {
        return plate.getPlateType().getCrustThickness() / ( plate.getPlateType().isOceanic() ? 3f : 6f );
    }

    private SubductingBehavior getSubductingBehavior() {
        // cast OK, overriding should only be paired with subducting
        return (SubductingBehavior) getOtherPlate().getBehavior();
    }

    @Override public void stepInTime( float millionsOfYears ) {
        // bring the edge down to the other level fairly quickly
        {
            float boundaryElevation = getSubductingBehavior().getBoundaryElevation();

            Sample edgeSample = getTopCrustBoundary().getEdgeSample( getOppositeSide() );
            float currentElevation = edgeSample.getPosition().y;

            float diff = boundaryElevation - currentElevation;
            float delta = (float) ( diff * ( 1 - Math.exp( -millionsOfYears ) ) ); // using exponential to keep timestep-independence

            final int columnIndex = getOppositeSide().getIndex( getNumCrustXSamples() );
            getCrust().layoutColumn( columnIndex,
                                     currentElevation + delta,
                                     getCrust().getBottomElevation( columnIndex ) + delta,
                                     plate.getTextureStrategy(), true );
            getTerrain().shiftColumnElevation( columnIndex, delta );
        }


        /*---------------------------------------------------------------------------*
        * smooth out continental crustal corner
        *----------------------------------------------------------------------------*/

        // NOTE: somewhat copied code from RiftingBehavior, but only changes the crust
        for ( int columnIndex = 0; columnIndex < getNumCrustXSamples(); columnIndex++ ) {
            Sample topSample = getCrust().getTopBoundary().samples.get( columnIndex );

            // blending crust sizes here (and preferably lithosphere too?)
            float fakeNeighborhoodY = topSample.getPosition().y;
            int count = 1;
            if ( columnIndex > 0 ) {
                fakeNeighborhoodY += getCrust().getTopBoundary().samples.get( columnIndex - 1 ).getPosition().y;
                count += 1;
            }
            if ( columnIndex < getNumCrustXSamples() - 1 ) {
                fakeNeighborhoodY += getCrust().getTopBoundary().samples.get( columnIndex + 1 ).getPosition().y;
                count += 1;
            }
            fakeNeighborhoodY /= count;

            float currentCrustTop = getCrust().getTopElevation( columnIndex );
            float currentCrustBottom = getCrust().getBottomElevation( columnIndex );
            float currentCrustWidth = currentCrustTop - currentCrustBottom;

            // try subtracting off top and bottom, and see how much all of this would change
            float resizeFactor = ( currentCrustWidth - 2 * ( currentCrustTop - fakeNeighborhoodY ) ) / currentCrustWidth;

            // don't ever grow the crust height
            if ( resizeFactor > 1 ) {
                resizeFactor = 1;
            }
            resizeFactor = (float) Math.pow( resizeFactor, millionsOfYears );
            float center = ( currentCrustTop + currentCrustBottom ) / 2;

            final float newCrustTop = ( currentCrustTop - center ) * resizeFactor + center;

            // compute new bottom with the same delta
            final float newCrustBottom = currentCrustBottom + ( newCrustTop - currentCrustTop );

            getCrust().layoutColumn( columnIndex,
                                     newCrustTop,
                                     newCrustBottom,
                                     plate.getTextureStrategy(), true );
            getTerrain().shiftColumnElevation( columnIndex, newCrustTop - currentCrustTop );
        }

        getTerrain().elevationChanged.updateListeners();
    }
}
