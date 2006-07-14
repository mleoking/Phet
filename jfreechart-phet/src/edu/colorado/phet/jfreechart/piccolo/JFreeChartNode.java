/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.jfreechart.piccolo;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * JFreeChartNode is a Piccolo node for displaying a JFreeChart.
 * <p>
 * The bounds of the node determine the size of the chart.
 * The node registers with the chart to receive notification
 * of changes to any component of the chart.  The chart is 
 * redrawn automatically whenever this notification is received.
 * <p>
 * The node can be buffered or unbuffered. If buffered, the 
 * chart is drawn using an off-screen image that is updated 
 * whenever the chart or node changes.  If unbuffered, the 
 * chart is drawn directly to the screen whenever paint is
 * called; this can be unnecessarily costly if the paint request
 * is not the result of changes to the chart or node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class JFreeChartNode extends PNode implements ChartChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JFreeChart _chart; // chart associated with the node
    private ChartRenderingInfo _info; // the chart's rendering info
    private boolean _buffered; // draws the chart to an offscreen buffer
    private BufferedImage _chartImage; // buffered chart image
    private AffineTransform _imageTransform; // transform used in with buffered image
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a node that displays the specified chart.
     * The chart is not buffered.
     * 
     * @param chart
     */
    public JFreeChartNode( JFreeChart chart ) {
        this( chart, false /* buffered */ );
    }
    
    /**
     * Constructs a node that displays the specified chart.
     * You can specify whether the chart's image should be buffered.
     * 
     * @param chart
     * @param buffered
     */
    public JFreeChartNode( JFreeChart chart, boolean buffered ) {
        super();

        _chart = chart;
        _chart.addChangeListener( this );
        _info = new ChartRenderingInfo();
        
        _buffered = buffered;
        _chartImage = null;
        _imageTransform = new AffineTransform();
        
        addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                _chartImage = null;
                repaint();  
            }
        } );
        
        updateChartRenderingInfo();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the chart that is associated with this node.
     * 
     * @return JFreeChart
     */
    public JFreeChart getChart() {
        return _chart;
    }
    
    /**
     * Gets the chart's rendering info.
     * Changes to the chart are not reflected in the rendering info
     * until after the chart has been painted.
     * You can force the rendering info to be updated by calling
     * updateChartRenderingInfo.
     * 
     * @return ChartRenderingInfo
     */
    public ChartRenderingInfo getChartRenderingInfo() {
        return _info;
    }
    
    /**
     * Forces an update of the chart's ChartRenderingInfo,
     * normally not updated until the next call to paint.
     * Call this directly if you have made changes to the
     * chart and need to calculate something that is based 
     * on the ChartRenderingInfo before the next paint occurs.
     */
    public void updateChartRenderingInfo() {
        BufferedImage image = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = image.createGraphics();
        _chart.draw( g2, getBounds(), _info );
    }
    
    /**
     * Gets the data area of the primary plot.
     * 
     * @return
     */
    public Rectangle2D getDataArea() {
        ChartRenderingInfo chartInfo = getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = plotInfo.getDataArea();
        Rectangle2D dataArea = new Rectangle2D.Double();
        dataArea.setRect( dataAreaRef );
        return dataArea;
    }
    
    /**
     * Gets the data area of a subplot.
     * Only combined charts have subplots.
     * 
     * @param subplotIndex
     * @throws IndexOutOfBoundsException if subplotIndex is out of bounds
     * @return
     */
    public Rectangle2D getDataArea( int subplotIndex ) {
        ChartRenderingInfo chartInfo = getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
        if ( subplotIndex >= plotInfo.getSubplotCount() ) {
            throw new IndexOutOfBoundsException( "subplotIndex is out of range: " + subplotIndex );
        }
        PlotRenderingInfo subplotInfo = plotInfo.getSubplotInfo( subplotIndex );
        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = subplotInfo.getDataArea();
        Rectangle2D dataArea = new Rectangle2D.Double();
        dataArea.setRect( dataAreaRef );
        return dataArea;
    }
    
    /*
     * Gets the XYPlot associated with this chart.
     * 
     * @throws UnsupportedOperationException if the primary plot is not an XYPlot
     * @return
     */
    private XYPlot getXYPlot() {
        XYPlot plot = null;
        if ( _chart.getPlot() instanceof XYPlot ) {
            plot = (XYPlot) _chart.getPlot();
        }
        else {
            throw new UnsupportedOperationException( 
                "only works for charts whose primary plot is an XYPlot" );
        }
        return plot;
    }
    
    /*
     * Gets the XYSubplot of a combined chart.
     * 
     * @param subplotIndex
     * @throws UnsupportedOperationException if the primary plot is not a combined XY plot
     * @throws IndexOutOfBoundsException if subplotIndex is out of bounds
     * @return
     */
    private XYPlot getXYSubplot( int subplotIndex ) {
        XYPlot subplot = null;
        
        List subplots = null;
        Plot plot = _chart.getPlot();
        if ( plot instanceof CombinedDomainXYPlot ) {
            CombinedDomainXYPlot combinedPlot = (CombinedDomainXYPlot) plot;
            subplots = combinedPlot.getSubplots();
        }
        else if ( plot instanceof CombinedRangeXYPlot ) {
            CombinedRangeXYPlot combinedPlot = (CombinedRangeXYPlot) plot;
            subplots = combinedPlot.getSubplots();
        }
        else {
            throw new UnsupportedOperationException( 
                "only works for for charts whose primary plot is a CombinedDomainXYPlot or CombinedRangeXYPlot" );
        }
        if ( subplotIndex >= subplots.size() ) {
            throw new IndexOutOfBoundsException( "subplotIndex is out of range: " + subplotIndex );
        }
        subplot = (XYPlot) subplots.get( subplotIndex );
        
        return subplot;
    }
    
    /**
     * Determines whether the chart is rendered to a buffered image
     * before being drawn to the paint method's graphics context.
     * The chart's image is generated only when the chart changes.
     * 
     * @param buffered true or false
     */
    public void setBuffered( boolean buffered ) {
        _buffered = buffered;
        _chartImage = null;
    }
    
    /**
     * Is the chart's image buffered?
     * 
     * @return true or false
     */
    public boolean isBuffered() {
        return _buffered;
    }
    
    //----------------------------------------------------------------------------
    // Coordinate transforms
    //----------------------------------------------------------------------------
    
    /**
     * Converts a point in the node's local coordinate system
     * to a point in the primary plot's coordinate system.
     * The primary plot must be an XYPlot.
     * 
     * @param nodePoint
     * @return
     */
    public Point2D nodeToPlot( Point2D nodePoint ) {
        XYPlot plot = getXYPlot();
        Rectangle2D dataArea = getDataArea();
        Point2D plotPoint = nodeToPlot( nodePoint, plot, dataArea );
        return plotPoint;
    }
    
    /**
     * Converts a point in the node's local coordinate system
     * to a point in a subplot's coordinate system.
     * The primary plot must be a combined XY plot,
     * which implies that all subplots are XYPlots.
     * 
     * @param nodePoint
     * @param subplotIndex
     * @return
     */
    public Point2D nodeToSubplot( Point2D nodePoint, int subplotIndex ) {
        XYPlot subplot = getXYSubplot( subplotIndex );
        Rectangle2D dataArea = getDataArea( subplotIndex );
        Point2D subplotPoint = nodeToPlot( nodePoint, subplot, dataArea );
        return subplotPoint;
    }
    
    /**
     * Converts a point in the primary plot's coordinate system
     * to a point in the node's local coordinate system.
     * The primary plot must be an XYPlot.
     * 
     * @param plotPoint
     * @return
     */
    public Point2D plotToNode( Point2D plotPoint ) {
        XYPlot plot = getXYPlot();
        Rectangle2D dataArea = getDataArea();
        Point2D nodePoint = plotToNode( plotPoint, plot, dataArea );
        return nodePoint;
    }
    
    /**
     * Converts a point in a subplot's coordinate system
     * to a point in the node's local coordinate system.
     * The primary plot must be a combined XY plot,
     * which implies that all subplots are XYPlots.
     * 
     * @param plotPoint
     * @param subplotIndex
     * @return
     */
    public Point2D subplotToNode( Point2D plotPoint, int subplotIndex ) {
        XYPlot subplot = getXYSubplot( subplotIndex );
        Rectangle2D dataArea = getDataArea( subplotIndex );
        Point2D nodePoint = plotToNode( plotPoint, subplot, dataArea );
        return nodePoint;
    }
    
    /*
     * Converts a node point to a plot point.
     * 
     * @param nodePoint
     * @param plot
     * @param dataArea
     * @return plot point, in model coordinates
     */
    private Point2D nodeToPlot( Point2D nodePoint, XYPlot plot, Rectangle2D dataArea ) {
        final double plotX = plot.getDomainAxis().java2DToValue( nodePoint.getX(), dataArea, plot.getDomainAxisEdge() );
        final double plotY = plot.getRangeAxis().java2DToValue( nodePoint.getY(), dataArea, plot.getRangeAxisEdge() );
        return new Point2D.Double( plotX, plotY );
    }
    
    /*
     * Converts a plot point to a node point.
     * 
     * @param plotPoint
     * @param plot
     * @param dataArea
     * @return node point, in local coordinates
     */
    private Point2D plotToNode( Point2D plotPoint, XYPlot plot, Rectangle2D dataArea ) {
        final double nodeX = plot.getDomainAxis().valueToJava2D( plotPoint.getX(), dataArea, plot.getDomainAxisEdge() );
        final double nodeY = plot.getRangeAxis().valueToJava2D( plotPoint.getY(), dataArea, plot.getRangeAxisEdge() );
        return new Point2D.Double( nodeX, nodeY );
    }
    
    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------
    
    /*
     * Paints the node.
     * The node's bounds (in the node's local coordinate system)
     * are used to determine the size and location of the chart.
     * Painting the node updates the chart's rendering info.
     */
    protected void paint( PPaintContext paintContext ) {
        if ( _buffered ) {
            paintBuffered( paintContext );
        }
        else {
            paintDirect( paintContext );
        }
    }
        
    /*
     * Paints the node directly to the specified graphics context.
     * 
     * @param paintContext
     */
    private void paintDirect( PPaintContext paintContext ) {
        Graphics2D g2 = paintContext.getGraphics();
        _chart.draw( g2, getBoundsReference(), _info );
    }
    
    /*
     * Paints the node to a buffered image that is
     * then drawn to the specified graphics context.
     * 
     * @param paintContext
     */
    private void paintBuffered( PPaintContext paintContext ) {
        Rectangle2D bounds = getBoundsReference();
        
        if ( _chartImage == null ) {
            _chartImage = _chart.createBufferedImage( (int) bounds.getWidth(), (int) bounds.getHeight(), 
                    BufferedImage.TYPE_INT_ARGB, _info );
        }
        
        Graphics2D g2 = paintContext.getGraphics();
        
        // Set interpolation to "nearest neighbor" to avoid JDK 1.5 performance problems.
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        
        _imageTransform.setToTranslation( bounds.getX(), bounds.getY() );
        g2.drawRenderedImage( _chartImage, _imageTransform );
    }
 
    //----------------------------------------------------------------------------
    // ChartChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Receives notification of changes to the chart (or any of its components),
     * and redraws the chart.
     * 
     * @param event
     */
    public void chartChanged( ChartChangeEvent event ) {
        /* 
         * Do not look at event.getSource(), since the source of the event is
         * likely to be one of the chart's components rather than the chart itself.
         */
        _chartImage = null; // the image needs to be regenerated
        repaint();
    }
}
