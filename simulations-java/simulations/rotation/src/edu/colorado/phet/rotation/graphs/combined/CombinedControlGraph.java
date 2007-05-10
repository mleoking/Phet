package edu.colorado.phet.rotation.graphs.combined;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 11:33:46 AM
 */

public class CombinedControlGraph extends PNode {
    private JFreeChart jFreeChart;
    private JFreeChartNode chartNode;
    private PNode controlNode;
    private ArrayList controlSets = new ArrayList();
    private double padX;
    private ArrayList chartSliders = new ArrayList();
    private ArrayList closeButtons = new ArrayList();

    public CombinedControlGraph( final XYPlot[] subplot ) {
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot( new NumberAxis( "Domain" ) );
        plot.setOrientation( PlotOrientation.VERTICAL );
        plot.setGap( 10.0 );

        controlNode = new PNode();

        for( int i = 0; i < subplot.length; i++ ) {
            XYPlot xyPlot = subplot[i];
            plot.add( xyPlot );
        }

        // return a new chart containing the overlaid plot...
        this.jFreeChart = new JFreeChart( "Combined Chart", JFreeChart.DEFAULT_TITLE_FONT, plot, true );
        jFreeChart.setBackgroundPaint( new Color( 0, 0, 0, 0 ) );

        chartNode = new JFreeChartNode( jFreeChart );
        addChild( chartNode );
        addChild( controlNode );
        chartNode.setOffset( 0, 0 );

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                relayout();
            }
        } );
        timer.start();

        for( int i = 0; i < subplot.length; i++ ) {
            JButton closeButton = new JButton( "Close Plot[" + i + "]" );
            final int i1 = i;
            closeButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    plot.remove( subplot[i1] );
                }
            } );
            PSwing pSwing = new PSwing( closeButton );
            addCloseButton( pSwing );
        }
    }

    private void addCloseButton( PSwing pSwing ) {
        closeButtons.add( pSwing );
        addChild( pSwing );
    }

    public JFreeChart getJFreeChart() {
        return jFreeChart;
    }

    public JFreeChartNode getChartNode() {
        return chartNode;
    }

    public boolean setBounds( double x, double y, double width, double height ) {
        setOffset( x, y );
        chartNode.setBounds( 0, 0, width - getInsetPadX(), height );
        relayout();
        return super.setBounds( x, y, width, height );
    }


    public void addControlSet( ControlSet controlSet, CombinedChartSlider slider ) {
        addControlSet( controlSet );
        addChartSlider( slider );

        relayout();
    }

    private void addChartSlider( CombinedChartSlider combinedChartSlider ) {
        chartSliders.add( combinedChartSlider );
        addControl( combinedChartSlider );
    }

    private void addControlSet( ControlSet controlSet ) {
        controlSets.add( controlSet );
        addControl( controlSet );
    }

    public static class ControlSet extends PNode {
        private int subplotIndex;
        private PNode leftControl;
        private PNode rightControl;

        public ControlSet( int subplotIndex, PNode leftControl, PNode rightControl ) {
            this.subplotIndex = subplotIndex;
            this.leftControl = leftControl;
            this.rightControl = rightControl;
            addChild( leftControl );
            addChild( rightControl );
        }

        public int getSubplotIndex() {
            return subplotIndex;
        }

        public PNode getLeftControl() {
            return leftControl;
        }

        public PNode getRightControl() {
            return rightControl;
        }
    }

    private double getInsetPadX() {
        relayout();
        return padX;
    }

    public void relayout() {
        double chartX = 0;
        double insetControlSliderX = 30;
        double insetSliderGraphX = 20;
        for( int i = 0; i < controlSets.size(); i++ ) {
            ControlSet controlSet = (ControlSet)controlSets.get( i );
            Rectangle2D dataArea = getChartNode().getDataArea( controlSet.getSubplotIndex() );
            controlSet.getLeftControl().setOffset( 0, dataArea.getY() );
            chartX = Math.max( chartX, controlSet.getLeftControl().getFullBounds().getMaxX() );
        }

        for( int i = 0; i < chartSliders.size(); i++ ) {
            CombinedChartSlider combinedChartSlider = (CombinedChartSlider)chartSliders.get( i );
            combinedChartSlider.setOffset( chartX + insetSliderGraphX, 0 );
        }
        chartNode.setOffset( chartX + insetControlSliderX + insetSliderGraphX, 0 );

        double maxRight = 0;
        for( int i = 0; i < controlSets.size(); i++ ) {
            ControlSet controlSet = (ControlSet)controlSets.get( i );
            Rectangle2D dataArea = getChartNode().getDataArea( controlSet.getSubplotIndex() );
            controlSet.getRightControl().setOffset( chartNode.getFullBounds().getMaxX(), dataArea.getCenterY() - controlSet.getRightControl().getFullBounds().getHeight() / 2 );
            maxRight = Math.max( maxRight, controlSet.getRightControl().getFullBounds().getWidth() );
        }
        this.padX = chartX + insetControlSliderX + insetSliderGraphX + maxRight;

        for( int i = 0; i < closeButtons.size(); i++ ) {
            PSwing closeButton = (PSwing)closeButtons.get( i );
            Rectangle2D dataArea = getChartNode().getDataArea( i );
            closeButton.setOffset( chartNode.getFullBounds().getMaxX() - closeButton.getFullBounds().getWidth(), dataArea.getY() );
        }
    }

    public void addControl( PNode control ) {
        controlNode.addChild( control );
    }
}
