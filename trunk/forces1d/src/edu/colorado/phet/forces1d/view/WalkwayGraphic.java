package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.forces1d.Force1DModule;
import edu.colorado.phet.forces1d.model.Force1DModel;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 5, 2003
 * Time: 5:34:02 PM
 * To change this template use Options | File Templates.
 */
public class WalkwayGraphic extends CompositePhetGraphic {
    private double treex;
    private double housex;
    private Function.LinearFunction transform;
    private PhetImageGraphic treeGraphic;
    private PhetImageGraphic cottageGraphic;
    private PhetShapeGraphic backgroundGraphic;
    private PhetShapeGraphic floorGraphic;
    private TickSetGraphic tickSetGraphic;
    private Rectangle bounds = new Rectangle();
    private int floorHeight = 6;
    private PhetImageGraphic leftWallGraphic;
    private PhetImageGraphic rightWallGraphic;

    public WalkwayGraphic( ApparatusPanel panel, Force1DModule module, int numTickMarks, Function.LinearFunction transform ) throws IOException {
        this( panel, module, numTickMarks, -10, 10, transform );
    }

    public WalkwayGraphic( ApparatusPanel panel, final Force1DModule module, int numTickMarks, double treex, double housex, Function.LinearFunction transform ) throws IOException {
        super( panel );
        this.treex = treex;
        this.housex = housex;
        this.transform = transform;

        treeGraphic = new PhetImageGraphic( panel, "images/tree.gif" );
        cottageGraphic = new PhetImageGraphic( panel, "images/cottage.gif" );
        backgroundGraphic = new PhetShapeGraphic( panel, null, Color.white, new BasicStroke( 1.0f ), Color.black );
        floorGraphic = new PhetShapeGraphic( panel, null, Color.white );

        tickSetGraphic = new TickSetGraphic( panel, transform );

        leftWallGraphic = new PhetImageGraphic( getComponent(), "images/barrier.jpg" );
        rightWallGraphic = new PhetImageGraphic( getComponent(), "images/barrier.jpg" );
        leftWallGraphic.setVisible( false );
        rightWallGraphic.setVisible( false );
//        leftWallGraphic.setLocation( )

        addGraphic( backgroundGraphic );
        addGraphic( floorGraphic );

        addGraphic( treeGraphic );
        addGraphic( cottageGraphic );

        addGraphic( leftWallGraphic );
        addGraphic( rightWallGraphic );

        addGraphic( tickSetGraphic );
        setCursorHand();
        update();

        module.getForceModel().addBoundaryConditionListener( new Force1DModel.BoundaryConditionListener() {
            public void boundaryConditionOpen() {

                try {

                    treeGraphic.setImage( ImageLoader.loadBufferedImage( "images/tree.gif" ) );
                    treeGraphic.setTransform( new AffineTransform() );
//                    treeGraphic.scale( 0.5);
                    cottageGraphic.setImage( ImageLoader.loadBufferedImage( "images/cottage.gif" ) );
                    cottageGraphic.setTransform( new AffineTransform() );
                    treeGraphic.setVisible( true );
                    cottageGraphic.setVisible( true );
                    leftWallGraphic.setVisible( false );
                    rightWallGraphic.setVisible( false );
                    module.getForcePanel().repaintBuffer();
                    setBoundsDirty();
                    autorepaint();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }

            public void boundaryConditionWalls() {
                leftWallGraphic.setVisible( true );
                rightWallGraphic.setVisible( true );
//                try {
//                    treeGraphic.setImage( ImageLoader.loadBufferedImage( "images/barrier.jpg" ) );
//                    treeGraphic.setTransform( new AffineTransform() );
//                    treeGraphic.scale( 0.5 );
//                    cottageGraphic.setImage( ImageLoader.loadBufferedImage( "images/barrier.jpg" ) );
//                    cottageGraphic.setTransform( new AffineTransform() );
//                    cottageGraphic.scale( 0.5 );
                treeGraphic.setVisible( false );
                cottageGraphic.setVisible( false );
                module.getForcePanel().repaintBuffer();
                setBoundsDirty();
                autorepaint();
//                }
//                catch( IOException e ) {
//                    e.printStackTrace();
//                }
            }
        } );
    }

    public int getFloorY() {
        return bounds.height - floorHeight;
    }

    public void setBounds( int x, int y, int width, int height ) {
        this.bounds.setBounds( x, y, width, height );
        update();
    }

    public static class TickSetGraphic extends CompositePhetGraphic {

        private int numTickMarks = 11;
        private DecimalFormat format = new DecimalFormat( "##" );
        ArrayList graphicList = new ArrayList();

        public TickSetGraphic( Component component, Function.LinearFunction transform ) {
            super( component );

            double minValue = -10;
            double maxValue = -minValue;
            double modelRange = maxValue - minValue;
            double modelDX = modelRange / ( numTickMarks - 1 );

            for( int i = 0; i < numTickMarks; i++ ) {

                double modelx = minValue + i * modelDX;
                String str = format.format( modelx );
                if( str.equals( "0" ) ) {
                    str = "0 meters";
                }
                TickGraphic tickGraphic = new TickGraphic( component, modelx, transform, str );
                graphicList.add( tickGraphic );
                addGraphic( tickGraphic );
            }
        }

        public void update() {
            for( int i = 0; i < graphicList.size(); i++ ) {
                TickGraphic tickGraphic = (TickGraphic)graphicList.get( i );
                tickGraphic.update();
            }
        }

        public void setY( int y ) {
            for( int i = 0; i < graphicList.size(); i++ ) {
                TickGraphic tickGraphic = (TickGraphic)graphicList.get( i );
                tickGraphic.setY( y );
            }
        }
    }

    public static class TickGraphic extends CompositePhetGraphic {
        private double modelx;
        private Function.LinearFunction transform;
        private String text;
        private int y = 134;
        private PhetShapeGraphic shapeGraphic;
        private Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
        private PhetTextGraphic textGraphic;

        public TickGraphic( Component component ) {
            super( component );
        }

        public TickGraphic( Component component, double modelx, Function.LinearFunction transform, String text ) {
            super( component );

            this.modelx = modelx;
            this.transform = transform;
            this.text = text;
            shapeGraphic = new PhetShapeGraphic( component, null, new BasicStroke( 1.0f ), Color.black );
            addGraphic( shapeGraphic );
            textGraphic = new PhetTextGraphic( component, font, text, Color.black, 0, 0 );
            addGraphic( textGraphic );
        }

        public void update() {
            int x = (int)transform.evaluate( modelx );
            int dy = 5;
            Line2D.Double line = new Line2D.Double( x, y, x, y + dy );
            shapeGraphic.setShape( line );
            textGraphic.setLocation( x - textGraphic.getWidth() / 2, y + dy );
        }

        public void setY( int y ) {
            this.y = y;
        }
    }

    private void update() {
        Rectangle skyRect = new Rectangle( bounds );
        skyRect.height = bounds.height - floorHeight;
        backgroundGraphic.setShape( skyRect );
        Color lightBLue = new Color( 150, 120, 255 );
        backgroundGraphic.setPaint( new GradientPaint( skyRect.x, skyRect.y, lightBLue, skyRect.x, skyRect.y + skyRect.height, Color.white ) );

        Color root = new Color( 100, 100, 255 );
        Rectangle floor = new Rectangle( bounds );
        floor.y = bounds.y + bounds.height - floorHeight;
        floor.height = bounds.y + bounds.height - floor.y;
        floorGraphic.setPaint( new GradientPaint( floor.x, floor.y, root, floor.x, floor.y + floor.height, Color.white ) );
        floorGraphic.setShape( floor );
        tickSetGraphic.setY( floor.y );
        tickSetGraphic.update();

        treeGraphic.setLocation( (int)transform.evaluate( treex ) - treeGraphic.getWidth() / 2, floor.y - treeGraphic.getHeight() );
        cottageGraphic.setLocation( (int)transform.evaluate( housex ) - cottageGraphic.getWidth() / 2, floor.y - cottageGraphic.getHeight() );

        leftWallGraphic.setLocation( bounds.x, bounds.y );
        leftWallGraphic.setTransform( new AffineTransform() );
        leftWallGraphic.scale( ( transform.evaluate( treex ) - bounds.x ) / leftWallGraphic.getImage().getWidth(), bounds.getHeight() / leftWallGraphic.getImage().getHeight() );

        rightWallGraphic.setLocation( (int)transform.evaluate( housex ), 0 );
        rightWallGraphic.setTransform( new AffineTransform() );
        double newWidth = bounds.x + bounds.width - transform.evaluate( housex );
        rightWallGraphic.scale( newWidth / rightWallGraphic.getImage().getWidth(), bounds.getHeight() / rightWallGraphic.getImage().getHeight() );

        setBoundsDirty();
        repaint();
    }

}
