/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.math.Vector2D;

import java.util.EventListener;
import java.util.EventObject;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

/**
 * Molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class Molecule extends Body implements Collidable {
    private CollidableAdapter collidableAdapter;
    private boolean isPartOfComposite;

    protected Molecule() {
        this( new Point2D.Double(), new Vector2D.Double(), new Vector2D.Double(), 0, 0 );
        collidableAdapter = new CollidableAdapter( this );
    }

    protected Molecule( Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
        collidableAdapter = new CollidableAdapter( this );
    }

    public void setPosition( double x, double y ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updatePosition();
        }
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updatePosition();
        }
        super.setPosition( position );
    }

    public Vector2D getVelocity() {
        return super.getVelocity();
    }

    public void setVelocity( Vector2D velocity ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updateVelocity();
        }
        super.setVelocity( velocity );
    }

    public void setVelocity( double vx, double vy ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updateVelocity();
        }
        super.setVelocity( vx, vy );
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public boolean isPartOfComposite() {
        return isPartOfComposite;
    }

    public void setPartOfComposite( boolean partOfComposite ) {
        isPartOfComposite = partOfComposite;
    }

    /**
     * If the molecule is part of a larger composite, there should be not stepInTime
     * behavior. It will be taken care of by the CompositeMolecule
     * 
     * @param dt
     */
    public void stepInTime( double dt ) {
        if( !isPartOfComposite ) {
            super.stepInTime( dt );
        }
    }

    //--------------------------------------------------------------------------------------------------
    //  Abstract methods
    //--------------------------------------------------------------------------------------------------

    abstract public Molecule[] getComponentMolecules();

    abstract public Rectangle2D getBoundingBox();
}