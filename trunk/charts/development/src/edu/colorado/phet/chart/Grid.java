/** Sam Reid*/
package edu.colorado.phet.chart;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:25:31 AM
 * Copyright (c) Sep 21, 2004 by Sam Reid
 */
public class Grid extends AbstractGrid {
    private GridTicks ticks;

    public Grid( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing, double crossesOtherAxisAt ) {
        super( chart, orientation, stroke, color, tickSpacing, crossesOtherAxisAt );
        ticks = new GridTicks( chart, orientation, new BasicStroke( 2 ), Color.black, tickSpacing );
    }

    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            Stroke stroke = super.getStroke();
            int orientation = super.getOrientation();
            Color color = super.getColor();
            double crossesOtherAxisAt = super.getCrossesOtherAxisAt();
            Chart chart = super.getChart();
            double tickSpacing = super.getSpacing();
            Stroke origStroke = g.getStroke();
            Color origColor = g.getColor();
            g.setStroke( stroke );
            g.setColor( color );
            if( orientation == VERTICAL ) {
                double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinX(), chart.getRange().getMaxX(), tickSpacing );

                for( int i = 0; i < gridLines.length; i++ ) {
                    double gridLineX = gridLines[i];
                    Point src = chart.transform( gridLineX, chart.getRange().getMinY() );
                    Point dst = chart.transform( gridLineX, chart.getRange().getMaxY() );
                    g.drawLine( src.x, src.y, dst.x, dst.y );
                }
            }
            else if( orientation == HORIZONTAL ) {
                double[] gridLines = getGridLines( crossesOtherAxisAt, chart.getRange().getMinY(), chart.getRange().getMaxY(), tickSpacing );

                for( int i = 0; i < gridLines.length; i++ ) {
                    double gridLineY = gridLines[i];
                    Point src = chart.transform( chart.getRange().getMinX(), gridLineY );
                    Point dst = chart.transform( chart.getRange().getMaxX(), gridLineY );
                    g.drawLine( src.x, src.y, dst.x, dst.y );
                }
            }
            g.setStroke( origStroke );
            g.setColor( origColor );

        }
        ticks.paint( g );
    }

    public void setSpacing( double spacing ) {
        super.setSpacing( spacing );
        ticks.setSpacing( spacing );
    }

    public void setTicksVisible( boolean visible ) {
        ticks.setVisible( visible );
    }

    public static class GridTicks extends AbstractTicks {
        public GridTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing ) {
            super( chart, orientation, stroke, color, tickSpacing );
        }

        public int getVerticalTickX() {
            Chart chart = getChart();
            return chart.transformX( chart.getRange().getMinX() );
        }

        public int getHorizontalTickY() {
            Chart chart = getChart();
            return chart.transformY( chart.getRange().getMinY() );
        }

    }


}
