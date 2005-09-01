/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.dischargelamps.SingleAtomModule;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.model.Plate;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.GroundState;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * CollisionEnergyIndicator
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CollisionEnergyIndicator extends PhetShapeGraphic implements DischargeLampModel.ChangeListener,
                                                                          Atom.ChangeListener {

    private static Shape line = new Line2D.Double( 0,0,100, 0);
    private static Stroke stroke;
    private static Paint paint = Color.red;
    private Atom atom;
    private DischargeLampModel model;
    private ModelViewTransform1D energyYTx;

    static {
        float miterLimit = 10f;
        float[] dashPattern = {10f};
        float dashPhase = 5f;
        float strokeThickness = 1;
        stroke = new BasicStroke( strokeThickness, BasicStroke.CAP_BUTT,
                                  BasicStroke.JOIN_MITER, miterLimit, dashPattern, dashPhase );
    }

    public CollisionEnergyIndicator( DischargeLampEnergyLevelMonitorPanel elmp, SingleAtomModule module ) {
        super( elmp, line, stroke, paint );
        model = (DischargeLampModel)module.getModel();
        model.addChangeListener( this );
        atom = module.getAtom();
        atom.addChangeListener( this );
        this.energyYTx = elmp.getEnergyYTx();
        energyYTx.addListener( new ModelViewTransform1D.Observer() {
            public void transformChanged( ModelViewTransform1D transform ) {
                update();
            }
        } );

        module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                update();
            }
        } );
    }

    public void update() {
        // Get the distance of the atom from the emitting plate, and the distance
        // between the plates
        double voltage = model.getVoltage();
        double plateToPlateDist = model.getLeftHandPlate().getPosition().distance(
                model.getRightHandPlate().getPosition() );
        double plateToAtomDist = 0;
        Plate emittingPlate = null;
        if( voltage > 0 ) {
            emittingPlate = model.getLeftHandPlate();
        }
        else {
            emittingPlate = model.getRightHandPlate();
        }
        plateToAtomDist = emittingPlate.getPosition().distance( atom.getPosition() ) - atom.getRadius();

        // The energy an electron has when it hits the atom
        double electronEnergy = Math.abs(voltage) * ( plateToAtomDist / plateToPlateDist );

        // This factor converts
        double fudge = 5.67;
        new GroundState().getEnergyLevel();
        int y = energyYTx.modelToView( ( electronEnergy * fudge) + model.getAtomicStates()[0].getEnergyLevel() );
        setLocation( 0, Math.max( y, 5 ));
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------
    // DischargeLampModel.ChangeListener implementation 
    //----------------------------------------------------------------

    public void energyLevelsChanged( DischargeLampModel.ChangeEvent event ) {
        // noop
    }

    public void voltageChanged( DischargeLampModel.ChangeEvent event ) {
        update();
    }

    //----------------------------------------------------------------
    // DischargeLampAtom.ChangeListener implementation
    //----------------------------------------------------------------

    public void stateChanged( Atom.ChangeEvent event ) {
        // noop
    }

    public void positionChanged( DischargeLampAtom.ChangeEvent event ) {
        update();
    }
}
