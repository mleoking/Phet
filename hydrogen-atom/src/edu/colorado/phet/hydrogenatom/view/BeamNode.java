/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.Color;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BeamNode is the beam the comes out of the gun.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BeamNode extends PhetPNode {

    private static final double TAIL_TO_HEAD_RATIO = 1.0;
    
    private PPath _pathNode;
    private int _r, _g, _b;
    private int _intensity;
    
    public BeamNode( double width, double height ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        GeneralPath path = new GeneralPath();
        float headWidth = (float)( width / 2 );
        float tailWidth = (float)( headWidth * TAIL_TO_HEAD_RATIO );
        path.moveTo( -headWidth, 0 );
        path.lineTo( +headWidth, 0 );
        path.lineTo( +tailWidth, (float)height );
        path.lineTo( -tailWidth, (float)height );
        path.closePath();
        
        _pathNode = new PPath();
        addChild( _pathNode );
        _pathNode.setPathTo( path );
        _pathNode.setStroke( null );
        
        _r = 255;
        _g = 255;
        _b = 255;
        _intensity = 100;
        updateColor();
    }
    
    public void setColor( Color hue, int intensity ) {
        if ( intensity < 0 || intensity > 100 ) {
            throw new IllegalArgumentException( "intensity out of range: " + intensity );
        }
        _r = hue.getRed();
        _g = hue.getGreen();
        _b = hue.getBlue();
        _intensity = intensity;
        updateColor();
    }
    
    private void updateColor() {
        int a = (int)( ( _intensity / 100d ) * 255 );
        _pathNode.setPaint( new Color( _r, _g, _b, a ) );
    }
}
