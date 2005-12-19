/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo.detectorscreen;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DetectorSet;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.WavefunctionGraphic;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:43:24 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class IntensityGraphic extends PNode {
    private SchrodingerModule schrodingerModule;
    private SchrodingerPanel schrodingerPanel;

    private DetectorSheetPNode detectorSheetPNode;
    private int detectorHeight;
    private Random random;
    private int h = 2;
    private int y = 2;
    private double probabilityScaleFudgeFactor = 1.0;
    public static double NORM_DECREMENT = 1.0;
    private int multiplier = 1;
    private ArrayList listeners = new ArrayList();

    public IntensityGraphic( SchrodingerModule schrodingerModule, SchrodingerPanel schrodingerPanel, int detectorHeight, WavefunctionGraphic wavefunctionGraphic ) {
        this.schrodingerModule = schrodingerModule;
        this.schrodingerPanel = schrodingerPanel;
        this.detectorHeight = detectorHeight;
        this.random = new Random();
        detectorSheetPNode = new DetectorSheetPNode( schrodingerPanel, wavefunctionGraphic, detectorHeight );
        addChild( detectorSheetPNode );
        detectorSheetPNode.setOffset( wavefunctionGraphic.getX(), 0 );
    }

    public void tryDetecting() {
        Wavefunction sub = getDetectionRegion();
        double probability = sub.getMagnitude() * probabilityScaleFudgeFactor;
        for( int i = 0; i < multiplier; i++ ) {
            double rand = random.nextDouble();
            if( rand <= probability ) {
                detectOne( sub );
                updateWavefunctionAfterDetection();
                notifyDetection();
            }
        }
    }

    public void setDisplayPhotonColor( Photon photon ) {
        detectorSheetPNode.setDisplayPhotonColor( photon );
    }

    public void setHighIntensityMode() {
        setMultiplier( 100 );
        setProbabilityScaleFudgeFactor( 10 );
        setNormDecrement( 0.0 );
        getDetectorSheet().setHighIntensityMode();
    }

    public boolean isFadeEnabled() {
        return detectorSheetPNode.isFadeEnabled();
    }

    public static interface Listener {
        void detectionOccurred();
    }

    private void notifyDetection() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.detectionOccurred();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void detectOne( Wavefunction sub ) {
        Point pt = getCollapsePoint( sub );
        double randOffsetX = 0;
        if( random.nextDouble() < 1.0 ) {
            int randAmount = random.nextInt( 4 ) + 1;
            if( random.nextBoolean() ) {
                randOffsetX = -randAmount;
            }
            else {
                randOffsetX = randAmount;
            }
        }

        int x = (int)( getModelToViewTransform1d().evaluate( pt.x ) + randOffsetX );
        x *= 1.0;//getWavePanelScale();
        int y = getDetectY();

        detectorSheetPNode.addDetectionEvent( x, y );
    }

//    private double getWavePanelScale() {
//        return 1.0;
//    }

    public Function.LinearFunction getModelToViewTransform1d() {
        return new Function.LinearFunction( 0, getDiscreteModel().getGridWidth(),
                                            0, getDetectorSheet().getBufferedImage().getWidth() );
    }

    private int getDetectY() {
        return (int)( random.nextDouble() * detectorHeight * 0.5 );
    }

    private int getYGaussian() {
        double scale = detectorHeight / 8;
        double offset = detectorHeight / 2;
        return (int)( random.nextGaussian() * scale + offset );
    }

    private void updateWavefunctionAfterDetection() {
        getDiscreteModel().updateWavefunctionAfterDetection();
    }

    public Wavefunction getDetectionRegion() {
        return getDiscreteModel().getDetectionRegion( 0, getDetectionY(),
                                                      getDiscreteModel().getWavefunction().getWidth(), h );
    }

    private int getDetectionY() {
        return y;
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    private DiscreteModel getDiscreteModel() {
        return schrodingerModule.getDiscreteModel();
    }

    private Point getCollapsePoint( Wavefunction sub ) {
        DetectorSet detectorSet = new DetectorSet( sub );
        return detectorSet.getCollapsePoint();
    }

    public void reset() {
        detectorSheetPNode.reset();
    }

    public double getProbabilityScaleFudgeFactor() {
        return probabilityScaleFudgeFactor;
    }

    public void setProbabilityScaleFudgeFactor( double probabilityScaleFudgeFactor ) {
        this.probabilityScaleFudgeFactor = probabilityScaleFudgeFactor;
    }

    public double getNormDecrement() {
        return NORM_DECREMENT;
    }

    public void setNormDecrement( double normDecrement ) {
        this.NORM_DECREMENT = normDecrement;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier( int multiplier ) {
        this.multiplier = multiplier;
    }

    public DetectorSheetPNode getDetectorSheet() {
        return detectorSheetPNode;
    }

    public int getOpacity() {
        return detectorSheetPNode.getOpacity();
    }

    public void setOpacity( int opacity ) {
        detectorSheetPNode.setOpacity( opacity );
    }

    public int getDetectorHeight() {
        return detectorHeight;
    }

    public void clearScreen() {
        detectorSheetPNode.clearScreen();
    }
}
