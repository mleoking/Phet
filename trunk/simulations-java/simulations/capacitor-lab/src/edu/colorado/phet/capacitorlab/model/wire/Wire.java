// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model.wire;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.shapes.WireShapeFactory;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * A wire is a collection of connected wire segments.
 * <p/>
 * NOTE: It's the client's responsibility to ensure that all segments are connected. No checking is done here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Wire {

    private final ArrayList<WireSegment> segments;
    private final SimpleObserver segmentObserver;
    private final double thickness;
    private final WireShapeFactory shapeFactory;

    // observable properties
    private final Property<Shape> shapeProperty; // Shape in view coordinates!

    public Wire( CLModelViewTransform3D mvt, double thickness, final WireSegment segment ) {
        this( mvt, thickness, new ArrayList<WireSegment>() {{ add( segment ); }} );
    }

    public Wire( CLModelViewTransform3D mvt, double thickness, ArrayList<WireSegment> segments ) {
        assert ( segments != null );
        assert ( thickness > 0 );

        this.segments = new ArrayList<WireSegment>( segments );
        this.thickness = thickness;
        this.shapeFactory = new WireShapeFactory( this, mvt );

        this.shapeProperty = new Property<Shape>( createShape() );

        // when any segment changes, update the shape property
        segmentObserver = new SimpleObserver() {
            public void update() {
                setShape( createShape() );
            }
        };
        for ( WireSegment segment : segments ) {
            segment.startPointProperty.addObserver( segmentObserver );
            segment.endPointProperty.addObserver( segmentObserver );
        }
    }

    // For use by subclasses who wish to add their segments via addSegment.
    protected Wire( CLModelViewTransform3D mvt, double thickness ) {
        this( mvt, thickness, new ArrayList<WireSegment>() );
    }

    public void cleanup() {
        for ( WireSegment segment : segments ) {
            segment.cleanup();
        }
    }

    protected void addSegment( WireSegment segment ) {
        segments.add( segment );
        segment.startPointProperty.addObserver( segmentObserver );
        segment.endPointProperty.addObserver( segmentObserver );
    }

    public double getThickness() {
        return thickness;
    }

    public ArrayList<WireSegment> getSegmentsReference() {
        return segments;
    }

    public void addShapeObserver( SimpleObserver o ) {
        shapeProperty.addObserver( o );
    }

    public Shape getShape() {
        return shapeProperty.get();
    }

    protected void setShape( Shape shape ) {
        shapeProperty.set( shape );
    }

    public boolean intersects( Shape shape ) {
        return ShapeUtils.intersects( shapeProperty.get(), shape );
    }

    protected Shape createShape() {
        return shapeFactory.createWireShape();
    }

    protected double getCornerOffset() {
        return shapeFactory.getCornerOffset();
    }

    protected double getEndOffset() {
        return shapeFactory.getEndOffset();
    }
}
