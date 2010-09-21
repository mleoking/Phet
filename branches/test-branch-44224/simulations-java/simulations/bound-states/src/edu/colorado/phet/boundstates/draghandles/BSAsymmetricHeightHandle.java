/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.draghandles;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.BSResources;
import edu.colorado.phet.boundstates.model.BSAsymmetricPotential;
import edu.colorado.phet.boundstates.module.BSPotentialSpec;
import edu.colorado.phet.boundstates.view.BSCombinedChartNode;

/**
 * BSAsymmetricHeightHandle is the drag handle used to control the 
 * height attribute of a potential composed of a single asymmetric well.
 * <p>
 * The handle is at the top right of the well.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricHeightHandle extends BSPotentialHandle {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param potential
     * @param potentialSpec used to get the range of the attribute controlled
     * @param chartNode
     */
    public BSAsymmetricHeightHandle( BSAsymmetricPotential potential, BSPotentialSpec potentialSpec, BSCombinedChartNode chartNode ) {
        super( potential, potentialSpec, chartNode, BSAbstractHandle.VERTICAL );
        
        int significantDecimalPlaces = potentialSpec.getHeightRange().getSignificantDecimalPlaces();
        String numberFormat = createNumberFormat( significantDecimalPlaces );
        setValueNumberFormat( numberFormat );
        setValuePattern( BSResources.getString( "drag.height" ) );
        
        updateDragBounds();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDragHandle implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the drag bounds.
     */
    public void updateDragBounds() {
        
        BSAsymmetricPotential potential = (BSAsymmetricPotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        BSCombinedChartNode chartNode = getChartNode();
        
        if ( potential.getCenter() != 0 || potential.getNumberOfWells() != 1 ) {
            throw new UnsupportedOperationException( "this implementation only supports 1 well centered at 0" );
        }
        
        // position -> x coordinates
        final double minPosition = BSConstants.POSITION_VIEW_RANGE.getLowerBound();
        final double maxPosition = BSConstants.POSITION_VIEW_RANGE.getUpperBound();
        final double minX = chartNode.positionToNode( minPosition );
        final double maxX = chartNode.positionToNode( maxPosition );
        
        // energy -> y coordinates (+y is down!)
        final double minEnergy = potential.getOffset() + spec.getHeightRange().getMin();
        final double maxEnergy = potential.getOffset() + spec.getHeightRange().getMax();
        final double minY = chartNode.energyToNode( maxEnergy );
        final double maxY = chartNode.energyToNode( minEnergy );
        
        // bounds, local coordinates
        final double w = maxX - minX;
        final double h = maxY - minY;
        Rectangle2D dragBounds = new Rectangle2D.Double( minX, minY, w, h );

        // Convert to global coordinates
        dragBounds = chartNode.localToGlobal( dragBounds );

        setDragBounds( dragBounds );
        updateView();
    }
    
    /**
     * Updates the model to match the drag handle.
     */
    protected void updateModel() {
        
        BSAsymmetricPotential potential = (BSAsymmetricPotential)getPotential();
        BSPotentialSpec spec = getPotentialSpec();
        
        potential.deleteObserver( this );
        {
            // Convert the drag handle's location to model coordinates
            Point2D viewPoint = getGlobalPosition();
            Point2D modelPoint = viewToModel( viewPoint );
            final double handleEnergy = modelPoint.getY();
            
            // Calculate the height
            double height = handleEnergy - potential.getOffset();
            final int numberOfSignicantDecimalPlaces = spec.getHeightRange().getSignificantDecimalPlaces();
            height = round( height, numberOfSignicantDecimalPlaces );
            
            potential.setHeight( height );
            setValueDisplay( height );
        }
        potential.addObserver( this );
    }

    /**
     * Updates the drag handle to match the model.
     */
    protected void updateView() {
        
        BSAsymmetricPotential potential = (BSAsymmetricPotential)getPotential();
        
        removePropertyChangeListener( this );
        {
            // Some potential attributes that we need...
            final double width = potential.getWidth();
            final double height = potential.getHeight();
            final double offset = potential.getOffset();
            
            // Calculate the handle's model coordinates
            final double handlePosition = ( width / 2 ) + 0.2; // handle to the right of well
            final double handleEnergy = offset + height; // handle at top of well
            
            // Convert to view coordinates
            Point2D modelPoint = new Point2D.Double( handlePosition, handleEnergy );
            Point2D viewPoint = modelToView( modelPoint );
            
            setGlobalPosition( viewPoint );
            setValueDisplay( height );
        }
        addPropertyChangeListener( this );
    }
}
