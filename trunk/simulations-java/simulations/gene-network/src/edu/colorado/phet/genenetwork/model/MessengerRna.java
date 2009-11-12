/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;


/**
 * Class that represents messenger RNA in the model.
 * 
 * NOTE: For the purposes of this sim, it is assume that the messenger RNA is
 * always fairly horizontal in the model, and never vertical.
 * 
 * @author John Blanco
 */
public class MessengerRna extends SimpleModelElement {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	// Define the appearance.
	private static final Paint ELEMENT_PAINT = Color.BLACK;
	private static float THICKNESS = 0.25f;  // In nanometers.
	
	// Default initial length, used if none is specified.
	private static float DEFAULT_LENGTH = 30;  // In nanometers.

	// The shape for this element is made up of a series of segments.  This
	// defines the length of each.
	private static double SEGMENT_LENGTH = 1;
	
	// Used so that every strand looks a little different.
	private static final Random RAND = new Random();
	
	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------
	
	private double length = 0;
	
	//----------------------------------------------------------------------------
	// Constructor(s)
	//----------------------------------------------------------------------------
	
	public MessengerRna(IObtainGeneModelElements model, Point2D initialPosition, double initialLength) {
		super(model, createInitialShape(initialLength), initialPosition, ELEMENT_PAINT);
		length = initialLength;
	}
	
	public MessengerRna(IObtainGeneModelElements model, double initialLength) {
		this(model, new Point2D.Double(), initialLength);
		length = initialLength;
	}

	public MessengerRna(IObtainGeneModelElements model) {
		this(model, new Point2D.Double(), DEFAULT_LENGTH);
	}

	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.MESSENGER_RNA;
	}
	
	static Shape createInitialShape(double length){
		
		/*
		return new Line2D.Double(0, 0, 20, 0);
		*/
		
		/*
		return new QuadCurve2D.Double(-10, 0, 0, 5, 10, 0);
		*/
		
		/*
		 * Creates a simple curve.
		GeneralPath path = new GeneralPath();
		float curveHeight = 5;
		path.moveTo(-APPROX_LENGTH/2, 0);
		path.quadTo(0, curveHeight, APPROX_LENGTH/2, 0);
		path.lineTo(APPROX_LENGTH/2, -THICKNESS);
		path.quadTo(0, curveHeight - THICKNESS, -APPROX_LENGTH/2, -THICKNESS);
		path.closePath();
		return path;
		 */

		if (length == 0){
			// For a length of zero, return what is essentially a dot.
			return new Ellipse2D.Double(-1, -1, 2, 2);
		}
		
		// Create the set of points that will define the curve.
		ArrayList<Point2DFloat> curvePoints = new ArrayList<Point2DFloat>();
		float curveHeight = 4;
		curvePoints.add(new Point2DFloat(-DEFAULT_LENGTH/2, 0));
		curvePoints.add(new Point2DFloat(-DEFAULT_LENGTH/3, curveHeight));
		curvePoints.add(new Point2DFloat(-DEFAULT_LENGTH/6, -curveHeight));
		curvePoints.add(new Point2DFloat(0, curveHeight/4));
		curvePoints.add(new Point2DFloat(DEFAULT_LENGTH/6, curveHeight));
		curvePoints.add(new Point2DFloat(DEFAULT_LENGTH/3, -curveHeight));
		curvePoints.add(new Point2DFloat(DEFAULT_LENGTH/2, 0));
		
		// Create the path.  Note that in order to create a closed shape, the
		// top line is drawn, then the line is essentially reversed but a
		// little lower, and the two lines are connected.
		GeneralPath path = new GeneralPath();
		path.moveTo(curvePoints.get(0).getX(), curvePoints.get(0).getY());
		path.curveTo(curvePoints.get(1).getX(), curvePoints.get(1).getY(), curvePoints.get(2).getX(),
				curvePoints.get(2).getY(), curvePoints.get(3).getX(), curvePoints.get(3).getY());
		path.curveTo(curvePoints.get(4).getX(), curvePoints.get(4).getY(), curvePoints.get(5).getX(),
				curvePoints.get(5).getY(), curvePoints.get(6).getX(), curvePoints.get(6).getY());
		
		path.lineTo(curvePoints.get(6).getX(), curvePoints.get(6).getY() - THICKNESS);
		path.curveTo(curvePoints.get(5).getX(), curvePoints.get(5).getY() - THICKNESS, curvePoints.get(4).getX(),
				curvePoints.get(4).getY() - THICKNESS, curvePoints.get(3).getX(), 
				curvePoints.get(3).getY() - THICKNESS);
		path.curveTo(curvePoints.get(2).getX(), curvePoints.get(2).getY() - THICKNESS, curvePoints.get(1).getX(),
				curvePoints.get(1).getY() - THICKNESS, curvePoints.get(0).getX(), 
				curvePoints.get(0).getY() - THICKNESS);
		path.closePath();
		return path;
		
		/*
		GeneralPath squiggly = new GeneralPath();
		squiggly.moveTo(-10, 0);
		for (int i=0; i<10; i++){
			squiggly.lineTo(i*5, 1);
		}
		return squiggly;
		*/
	}
	
	private static class Point2DFloat{
		private final float x;
		private final float y;
		
		public Point2DFloat(float x, float y) {
			super();
			this.x = x;
			this.y = y;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}
	}
}
