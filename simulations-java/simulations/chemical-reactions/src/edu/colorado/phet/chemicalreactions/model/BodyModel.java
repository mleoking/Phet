// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Wrapper for a box2d body that gives us more convenient properties in a possibly different frame of reference
 */
public class BodyModel {

    private final Property<ImmutableVector2D> _position = new Property<ImmutableVector2D>( new ImmutableVector2D() );
    private final Property<ImmutableVector2D> _velocity = new Property<ImmutableVector2D>( new ImmutableVector2D() );
    private final Property<Float> _angle = new Property<Float>( 0f );
    private final Property<Float> _angularVelocity = new Property<Float>( 0f ); // may have issues based on shears or scales

    // public read-only copies
    public final ObservableProperty<ImmutableVector2D> position = _position;
    public final ObservableProperty<ImmutableVector2D> velocity = _velocity;
    public final ObservableProperty<Float> angle = _angle;
    public final ObservableProperty<Float> angularVelocity = _angularVelocity;

    private final BodyDef bodyDef;
    private final ModelViewTransform bodyToModelTransform;
    private Body body;

    public BodyModel( BodyDef bodyDef, ModelViewTransform bodyToModelTransform ) {
        this.bodyDef = bodyDef;
        this.bodyToModelTransform = bodyToModelTransform;
    }

    public Body getBody() {
        return body;
    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public void setBody( Body body ) {
        this.body = body;
    }

    public void setPosition( ImmutableVector2D p ) {
        assert body != null;
        // TODO: consider not changing this until postStep()?
        _position.set( p );
        ImmutableVector2D boxPosition = bodyToModelTransform.viewToModel( p );
        body.getPosition().x = (float) boxPosition.getX();
        body.getPosition().y = (float) boxPosition.getY();
    }

    public void postStep() {
        assert body != null;
        _position.set( bodyToModelTransform.modelToView( new ImmutableVector2D( body.getPosition().x, body.getPosition().y ) ) );
        _velocity.set( bodyToModelTransform.modelToViewDelta( new ImmutableVector2D( body.getLinearVelocity().x, body.getLinearVelocity().y ) ) );
        _angle.set( transformAngle( body.getAngle() ) );
        _angularVelocity.set( body.getAngularVelocity() * ( isTransformReflected() ? -1 : 1 ) ); // reverse angular velocity in reflection case
    }

    private boolean isTransformReflected() {
        return bodyToModelTransform.getTransform().getScaleX() * bodyToModelTransform.getTransform().getScaleX() < 0;
    }

    private float transformAngle( float angle ) {
        ImmutableVector2D newVector = bodyToModelTransform.modelToViewDelta( new ImmutableVector2D( Math.cos( angle ), Math.sin( angle ) ) ).getNormalizedInstance();
        return (float) Math.atan2( newVector.getY(), newVector.getX() );
    }
}
