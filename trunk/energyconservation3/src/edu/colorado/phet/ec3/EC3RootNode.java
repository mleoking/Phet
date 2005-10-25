/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.FloorGraphic;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.piccolo.PhetRootPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Oct 21, 2005
 * Time: 2:16:21 PM
 * Copyright (c) Oct 21, 2005 by Sam Reid
 */

public class EC3RootNode extends PhetRootPNode {
    private PNode bodyGraphics = new PNode();
    private PNode splineGraphics = new PNode();
    private PNode buses;
    private EC3Module ec3Module;
    private EC3Canvas ec3Canvas;
    private PNode historyGraphics = new PNode();

    public EC3RootNode( EC3Module ec3Module, EC3Canvas ec3Canvas ) {
        this.ec3Module = ec3Module;
        this.ec3Canvas = ec3Canvas;
        EnergyConservationModel ec3Model = getModel();
        Floor floor = ec3Model.floorAt( 0 );
        SplineToolbox splineToolbox = new SplineToolbox( ec3Canvas, 50, 50 );

        addLayer();
        addLayer();
//        PhetRootPNode.Layer layer1 = new PhetRootPNode.Layer();
//        addLayer( layer1, 0 );
        layerAt( 0 ).getWorldNode().addChild( new SkyGraphic( floor.getY() ) );
        layerAt( 0 ).getWorldNode().addChild( new FloorGraphic( floor ) );

        layerAt( 0 ).getScreenNode().addChild( splineToolbox );
        layerAt( 1 ).getWorldNode().addChild( splineGraphics );
        layerAt( 1 ).getWorldNode().addChild( bodyGraphics );
        layerAt( 1 ).getWorldNode().addChild( historyGraphics );

//        PhetRootPNode.Layer topLayer = new Layer();
//        addLayer( topLayer );


    }

    private EnergyConservationModel getModel() {
        EnergyConservationModel ec3Model = ec3Module.getEnergyConservationModel();
        return ec3Model;
    }

    public void clearBuses() {
        if( buses != null ) {
            buses.removeAllChildren();
            removeWorldChild( buses );
            buses = null;
        }
    }

    public void addBuses() {
        if( buses == null ) {
            try {
                buses = new PNode();
                Floor floor = getModel().floorAt( 0 );
                BufferedImage newImage = ImageLoader.loadBufferedImage( "images/schoolbus200.gif" );
                PImage schoolBus = new PImage( newImage );
                double y = floor.getY() - schoolBus.getFullBounds().getHeight() + 10;
                schoolBus.setOffset( 0, y );
                double busStart = 500;
                for( int i = 0; i < 10; i++ ) {
                    PImage bus = new PImage( newImage );
                    double dbus = 2;
                    bus.setOffset( busStart + i * ( bus.getFullBounds().getWidth() + dbus ), y );
                    buses.addChild( bus );
                }
                addWorldChild( buses );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void addSplineGraphic( SplineGraphic splineGraphic ) {
        splineGraphics.addChild( splineGraphic );
    }

    public void reset() {
        bodyGraphics.removeAllChildren();
        splineGraphics.removeAllChildren();
        clearBuses();
    }

    public void addBodyGraphic( BodyGraphic bodyGraphic ) {
        bodyGraphics.addChild( bodyGraphic );
    }

    public void toggleBox() {
        if( bodyGraphics.getChildrenReference().size() > 0 ) {
            boolean state = ( (BodyGraphic)bodyGraphics.getChildrenReference().get( 0 ) ).isBoxVisible();
            for( int i = 0; i < bodyGraphics.getChildrenReference().size(); i++ ) {
                BodyGraphic bodyGraphic = (BodyGraphic)bodyGraphics.getChildrenReference().get( i );
                bodyGraphic.setBoxVisible( !state );
            }
        }
    }

    public SplineGraphic splineGraphicAt( int i ) {
        return (SplineGraphic)splineGraphics.getChildrenReference().get( i );
    }

    public int numSplineGraphics() {
        return splineGraphics.getChildrenReference().size();
    }

    public void removeSplineGraphic( SplineGraphic splineGraphic ) {
        splineGraphics.removeChild( splineGraphic );
    }

    public void updateGraphics() {
        updateSplines();
        updateBodies();
        updateHistory();
    }

    private void updateHistory() {
        System.out.println( "numHistoryGraphics() = " + numHistoryGraphics() );
        System.out.println( "getModel().numHistoryPoints() = " + getModel().numHistoryPoints() );
        while( numHistoryGraphics() < getModel().numHistoryPoints() ) {
            addHistoryGraphic( new HistoryPointGraphic( getModel().historyPointAt( 0 ) ) );
        }
        while( numHistoryGraphics() > getModel().numHistoryPoints() ) {
            removeHistoryPointGraphic( historyGraphicAt( numHistoryGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numHistoryPoints(); i++ ) {
            historyGraphicAt( i ).setHistoryPoint( getModel().historyPointAt( i ) );
        }
    }

    private HistoryPointGraphic historyGraphicAt( int i ) {
        return (HistoryPointGraphic)historyGraphics.getChild( i );
    }

    private void removeHistoryPointGraphic( HistoryPointGraphic graphic ) {
        historyGraphics.removeChild( graphic );
    }

    private void addHistoryGraphic( HistoryPointGraphic historyPointGraphic ) {
        historyGraphics.addChild( historyPointGraphic );
    }

    private int numHistoryGraphics() {
        return historyGraphics.getChildrenCount();
    }

    private void updateBodies() {
        while( numBodyGraphics() < getModel().numBodies() ) {
            addBodyGraphic( new BodyGraphic( ec3Module, getModel().bodyAt( 0 ) ) );
        }
        while( numBodyGraphics() > getModel().numBodies() ) {
            removeBodyGraphic( bodyGraphicAt( numBodyGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numBodies(); i++ ) {
            bodyGraphicAt( i ).setBody( getModel().bodyAt( i ) );
        }
    }

    private void updateSplines() {
        while( numSplineGraphics() < getModel().numSplineSurfaces() ) {
            addSplineGraphic( new SplineGraphic( ec3Canvas, getModel().splineSurfaceAt( 0 ) ) );
        }
        while( numSplineGraphics() > getModel().numSplineSurfaces() ) {
            removeSplineGraphic( splineGraphicAt( numSplineGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numSplineSurfaces(); i++ ) {
            splineGraphicAt( i ).setSplineSurface( getModel().splineSurfaceAt( i ) );
        }
    }

    private void removeBodyGraphic( BodyGraphic bodyGraphic ) {
        bodyGraphics.removeChild( bodyGraphic );
    }

    private int numBodyGraphics() {
        return bodyGraphics.getChildrenCount();
    }

    public BodyGraphic bodyGraphicAt( int i ) {
        return (BodyGraphic)bodyGraphics.getChild( i );
    }

}
