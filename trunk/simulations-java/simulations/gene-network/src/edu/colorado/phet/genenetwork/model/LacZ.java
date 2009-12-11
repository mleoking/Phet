/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents LacZ, which is the model element that breaks up the
 * lactose.
 * 
 * @author John Blanco
 */
public class LacZ extends SimpleModelElement {
	
	private static final double SIZE = 10; // In nanometers.
	private static final Paint ELEMENT_PAINT = new GradientPaint(new Point2D.Double(-SIZE, 0), 
			new Color(185, 147, 187), new Point2D.Double(SIZE * 5, 0), Color.WHITE);
	private static final double EXISTENCE_TIME = 15; // Seconds.
	
	public LacZ(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT, true, EXISTENCE_TIME);
		addAttachmentPoint(new AttachmentPoint(ModelElementType.GLUCOSE, new PDimension(0, -SIZE/2)));
		addAttachmentPoint(new AttachmentPoint(ModelElementType.GALACTOSE, new PDimension(0, -SIZE/2)));
		setMotionStrategy(new StillnessMotionStrategy(this));
	}
	
	public LacZ(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
	}
	
	public LacZ(){
		this(null);
	}
	
	private static Shape createShape(){
		// Start with a circle.
		Ellipse2D startingShape = new Ellipse2D.Double(-SIZE/2, -SIZE/2, SIZE, SIZE);
		Area area = new Area(startingShape);
		
		// Get the shape of a lactose molecule and shift it to the appropriate
		// position.
		Shape lactoseShape = new Lactose().getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, -SIZE/2 );
		lactoseShape = transform.createTransformedShape(lactoseShape);
		
		// Subtract off the shape of the lactose molecule.
		area.subtract(new Area(lactoseShape));
		return area;
	}

	@Override
	protected void onTransitionToExistingState() {
		setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
	}
}
