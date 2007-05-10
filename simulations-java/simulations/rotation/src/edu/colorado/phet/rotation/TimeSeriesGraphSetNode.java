package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.GraphSetNode;
import edu.colorado.phet.rotation.timeseries.TimeSeriesControlPanel;
import edu.colorado.phet.rotation.timeseries.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:23 AM
 */

public class TimeSeriesGraphSetNode extends PNode {
    private GraphSetNode graphSetNode;
    private PSwing timeSeriesControlPanelNode;
    private boolean constructed = false;

    public TimeSeriesGraphSetNode( GraphSetModel graphSetModel, TimeSeriesModel timeSeriesModel ) {
        setBounds( 0, 0, 800, 600 );
        graphSetNode = new GraphSetNode( graphSetModel );
        TimeSeriesControlPanel timeSeriesControlPanel = new TimeSeriesControlPanel( timeSeriesModel );
        timeSeriesControlPanelNode = new PSwing( timeSeriesControlPanel );

        addChild( graphSetNode );
        addChild( timeSeriesControlPanelNode );

        PropertyChangeListener relayout = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                relayout();
            }
        };
        addPropertyChangeListener( PNode.PROPERTY_BOUNDS, relayout );
        addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, relayout );
        addPropertyChangeListener( PNode.PROPERTY_VISIBLE, relayout );

        constructed = true;
        relayout();
    }

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        relayout();
    }

    private void relayout() {
        if( constructed ) {
            Rectangle2D bounds = getBounds();
            System.out.println( "TSGSN::bounds = " + bounds );
            graphSetNode.setBounds( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() - timeSeriesControlPanelNode.getFullBounds().getHeight() );
            timeSeriesControlPanelNode.setOffset( bounds.getX() + bounds.getWidth() / 2.0 - timeSeriesControlPanelNode.getFullBounds().getWidth() / 2.0, graphSetNode.getFullBounds().getMaxY() );
        }
    }

    public void setFlowLayout() {
        graphSetNode.setFlowLayout();
    }

    public void setAlignedLayout() {
        graphSetNode.setAlignedLayout();
    }
}
