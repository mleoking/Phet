/**
 * Class: LinePlot
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class LinePlot extends DataSetGraphic {
    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    private static final Color DEFAULT_COLOR = Color.BLACK;
    
    private GeneralPath generalPath;
    private PhetShapeGraphic phetShapeGraphic;

    public LinePlot( Component component, Chart chart ) {
        this( component, chart, null );
    }
    
    public LinePlot( Component component, Chart chart, DataSet dataSet ) {
        this( component, chart, dataSet, DEFAULT_STROKE, DEFAULT_COLOR );
    }

    public LinePlot( Component component, final Chart chart, DataSet dataSet, Stroke stroke, Paint paint ) {
        super( component, chart, dataSet );
        phetShapeGraphic = new PhetShapeGraphic( getComponent(), null, stroke, paint );
        addGraphic( phetShapeGraphic );
        setClip( chart.getChartBounds() );
        chart.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                setClip( chart.getChartBounds() );
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
            }
        } );
        addAllPoints();
    }

    public void pointsAdded( Point2D[] points ) {
        for( int i = 0; i < points.length; i++ ) {
            pointAddedNoRepaint( points[i] );
        }
        repaintAll();

    }

    protected void pointAddedNoRepaint( Point2D point ) {
        if( point == null ) {
            throw new RuntimeException( "Null point" );
        }
        Point2D viewLocation = getChart().transformDouble( point );
        if( generalPath == null ) {
            generalPath = new GeneralPath();
            generalPath.moveTo( (float)viewLocation.getX(), (float)viewLocation.getY() );
            phetShapeGraphic.setShape( generalPath );
        }
        else {
            //Determine the exact region for repaint.
            generalPath.lineTo( (float)viewLocation.getX(), (float)viewLocation.getY() );
        }
    }

    public void pointAdded( Point2D point ) {
        pointAddedNoRepaint( point );
        repaintAll();
    }

    private void repaintAll() {
        phetShapeGraphic.setShapeDirty();
        setBoundsDirty();
        autorepaint();
    }

    public void cleared() {
        phetShapeGraphic.setShape( null );
        generalPath = null;
    }

    public void transformChanged() {
        generalPath = null;
        phetShapeGraphic.setShape( null );
        addAllPoints();
    }

    public void setStroke( Stroke stroke ) {
        phetShapeGraphic.setStroke( stroke );
        autorepaint();
    }
    
    public void setBorderColor( Color color ) {
        phetShapeGraphic.setBorderColor( color );
        autorepaint();
    }
}