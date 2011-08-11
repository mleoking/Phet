// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Base class for all objects that can be placed on the balance.
 *
 * @author John Blanco
 */
public abstract class Mass implements UserMovableModelElement {


    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    protected static final double ANIMATION_VELOCITY = 1; // In meters/sec.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Property that indicates whether this mass is currently user controlled,
    // i.e. being moved around by the user.
    public final BooleanProperty userControlled = new BooleanProperty( false );

    // The mass of this...mass.  Yes, it is the same word used as two
    // different linguistic constructs, but the physicists said that the term
    // "weight" was not appropriate, so we're stuck with this situation.
    private final double mass;

    // Property that contains the rotational angle, in radians, of the model
    // element.  By convention for this simulation, the point of rotation is
    // considered to be the center bottom of the model element.
    final protected Property<Double> rotationalAngleProperty = new Property<Double>( 0.0 );

    // Boolean property that indicates whether this model element is currently
    // animating back to its original add location.
    final protected BooleanProperty animatingToAddPoint = new BooleanProperty( false );

    // Since not all objects are symmetrical, some may need to have an offset
    // that indicates where their center of mass is when placed on a balance.
    // This is the horizontal offset from the center of the shape or image.
    private double centerOfMassXOffset = 0;

    // Destination of linear animation.
    protected Point2D animationDestination = new Point2D.Double();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public Mass( double mass ) {
        this.mass = mass;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public double getMass() {
        return mass;
    }

    public double getCenterOfMassXOffset() {
        return centerOfMassXOffset;
    }

    public void setCenterOfMassXOffset( double centerOfMassXOffset ) {
        this.centerOfMassXOffset = centerOfMassXOffset;
    }

    public abstract void translate( double x, double y );

    public abstract void translate( ImmutableVector2D delta );

    public abstract Point2D getPosition();

    public abstract Point2D getMiddlePoint();

    /**
     * Animate this element's return to its initial location.  This consists
     * of moving the element in a stepwise fashion back to the point where it
     * was originally added to the model while simultaneously reducing its
     * size, and then signaling that the animation is complete.  At that
     * point, it is generally removed from the model.
     */
    public void animateReturnToAddPoint() {
        // In the default implementation, the signal is sent that says that
        // the animation is complete, but no actual animation is done.
        // Override to implement the subclass-specific animation.
        animatingToAddPoint.set( true );
        animatingToAddPoint.set( false );
    }

    public void addAnimationStateObserver( VoidFunction1<Boolean> animationStateObserver ) {
        animatingToAddPoint.addObserver( animationStateObserver );
    }

    public void removeAnimationStateObserver( VoidFunction1<Boolean> animationStateObserver ) {
        animatingToAddPoint.removeObserver( animationStateObserver );
    }

    public void setOnPlank( boolean onPlank ) {
        // Handle any changes that need to happen when added to the plank,
        // such as changes to shape or image.  By default, this does nothing.
    }

    /**
     * Set the angle of rotation.  The point of rotation is the position
     * handle.  For a mass, that means that this method can be used to make
     * it appear to sit will on plank.
     *
     * @param angle rotational angle in radians.
     */
    public void setRotationAngle( double angle ) {
        rotationalAngleProperty.set( angle );
        // Override to implement the updates to the shape if needed.
    }

    public double getRotationAngle() {
        return rotationalAngleProperty.get();
    }

    /**
     * The user has released this mass.
     */
    public void release() {
        userControlled.set( false );
    }

    public void setAnimationDestination( double x, double y ) {
        animationDestination.setLocation( x, y );
    }


    /**
     * Implements any time-dependent behavior of the mass.
     *
     * @param dt - Time change since last call.
     */
    public void stepInTime( double dt ) {
        // Default implementation does nothing.
    }
}
