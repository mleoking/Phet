/* BellCurve.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

/**
 * BellCurve draws a bell curve shape.
 * The origin of the curve is at the peak in the center of the curve.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$ $Name$
 */
public class BellCurve extends PhetShapeGraphic
{
  /**
   * Sole constructor
   * 
   * @param component the parent Component
   * @param x the X coordinate of the curve's location
   * @param y the Y coordinate of the curve's location
   * @param width the width of the curve, in pixels
   * @param height the height of the curve, in pixels
   * @param angle angle to rotate, in radians
   */
  public BellCurve( Component component, int x, int y, int width, int height, double angle )
  {
    super( component, null, null );
    
    //  Request antialiasing.
    RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
    super.setRenderingHints( hints );

    // Default stroke attributes.
    super.setBorderColor( Color.BLACK );
    super.setStroke( new BasicStroke(1f) );
    
    // Create the path that describes the curve.
    GeneralPath path = new GeneralPath();
    path.moveTo(  -.50f * width, 1f * height ); // lower left
    path.curveTo( -.25f * width, 1f * height,
                  -.25f * width, 0f * height, 
                     0f * width, 0f * height  ); // left curve
    path.curveTo(  .25f * width, 0f * height, 
                   .25f * width, 1f * height,
                   .50f * width, 1f * height ); // right curve
    Shape shape = path;
    
    // Rotate and translate.
    AffineTransform transform = new AffineTransform();
    transform.translate( x, y );
    transform.rotate( angle );
    shape = transform.createTransformedShape( shape );
    
    super.setShape( shape );
  }
  
}


/* end of file */