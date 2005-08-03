/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 9:32:14 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class BarGraphic2D extends PNode {
    private ModelViewTransform1D transform1D;
    private double value;
    private int x;
    private int width;
    private int y;
    private VerticalTextGraphic label;
    private PPath rectangle3DGraphic;
    public double labelHeight;
    private double labelWidth;

    public BarGraphic2D( String text, ModelViewTransform1D transform1D, double value, int x, int width, int y, int dx, int dy, Paint paint ) {
        super();
        this.transform1D = transform1D;
        this.value = value;
        this.x = x;
        this.y = y;
        this.width = width;

        rectangle3DGraphic = new PPath( null );
        rectangle3DGraphic.setPaint( paint );
        rectangle3DGraphic.setStroke( new BasicStroke( 1 ) );
        rectangle3DGraphic.setStrokePaint( Color.black );
        //, paint, new BasicStroke( 1 ), Color.black );

        Color lightBlue = new Color( 240, 225, 255 );
//        label = new VerticalTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 20 ), text, lightBlue, Color.black );
        label = new VerticalTextGraphic( new Font( "Lucida Sans", Font.BOLD, 21 ), text, Color.black, lightBlue );
        addChild( rectangle3DGraphic );

        addChild( label );
        labelHeight = label.getHeight();
        labelWidth = label.getWidth();
        updateBar();
    }

    private void updateBar() {
        int height = computeHeight();
        Rectangle rect = new Rectangle( x, y - height, width, height );
//        label.setLocation( rect.x + 5, (int)( 5 + rect.getMaxY() + label.getHeight() ) );
//        if (label.getText().equalsIgnoreCase( "kinetic")){
//        System.out.println( "Name=" + label.getText() + ", y=" + y + ", labelHeight=" + label.getHeight() );
//        }
        label.setOffset( rect.x + 7 - labelWidth, (int)( 5 + y + labelHeight ) );
        rectangle3DGraphic.setPathTo( rect );
    }

    public void setValue( double value ) {
        if( value != this.value && Math.abs( value ) != Math.abs( this.value ) ) {
//            System.out.println( "value = " + value + ", oldValue=" + this.value );

            this.value = value;
            if( value < 0 ) {
                rectangle3DGraphic.setOffset( 0, -computeHeight() );//a big hack to make negative values work.
            }
            else {
                rectangle3DGraphic.setOffset( 0, 0 );
            }
            this.value = Math.abs( value );
            updateBar();
        }
    }

    private int computeHeight() {
        return transform1D.modelToView( value );
    }
}
