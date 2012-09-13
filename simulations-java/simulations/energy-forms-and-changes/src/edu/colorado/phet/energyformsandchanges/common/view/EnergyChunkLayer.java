// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.view;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.umd.cs.piccolo.PNode;

/**
 * This class is a PNode that monitors the comings and goings of energy
 * chunks on a observable list and adds/removes them from this node.  This is
 * intended to be used in other PNodes that represent model elements that
 * contain energy chunks.
 * <p/>
 * This was done as a separate class so that it could be used in composition
 * rather than inheritance, because composition allows better control over the
 * layering within the parent PNode.
 *
 * @author John Blanco
 */
public class EnergyChunkLayer extends PNode {

    public EnergyChunkLayer( final ObservableList<EnergyChunk> energyChunkList, PNode parentNode, final ModelViewTransform mvt ) {

//        setGlobalTranslation( new Point2D.Double( 0, 0 ) );

        parentNode.addPropertyChangeListener( "transform", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                System.out.println( "=======================Transform changed=======================" );
//                setOffset( globalToLocal( new Point2D.Double( 0, 0 ) ) );
                setGlobalTranslation( new Point2D.Double( 0, 0 ) );
                System.out.println( "getOffset() = " + getOffset() );
                System.out.println( "localToGlobal( getOffset() ) = " + localToGlobal( getOffset() ) );
                System.out.println( "getGlobalTranslation = " + getGlobalTranslation() );
            }
        } );

        // Add energy chunk nodes as children as the energy chunks come in to
        // existence in the model.
        energyChunkList.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final EnergyChunkNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                addChild( energyChunkNode );
                // Remove the energy chunk nodes as they are removed from the model.
                energyChunkList.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            removeChild( energyChunkNode );
                        }
                    }
                } );
            }
        } );
    }
}
