/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------
 * LineRenderer3D.java
 * -------------------
 * (C) Copyright 2004, 2005, by Tobias Selb and Contributors.
 *
 * Original Author:  Tobias Selb;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id$
 *
 * Changes
 * -------
 * 15-Oct-2004 : Version 1 (TS);
 * 05-Nov-2004 : Modified drawItem() signature (DG);
 * 11-Nov-2004 : Now uses ShapeUtilities class to translate shapes (DG);
 * 26-Jan-2005 : Update for changes in super class (DG);
 * 13-Apr-2005 : Check item visibility in drawItem() method (DG);
 * 09-Jun-2005 : Use addItemEntity() in drawItem() method (DG);
 * 10-Jun-2005 : Fixed capitalisation of setXOffset() and setYOffset() (DG);
 * 
 */

package org.jfree.chart.renderer.category;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.Effect3D;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.ShapeUtilities;

/**
 * A line renderer with a 3D effect.
 * 
 * @author Tobias Selb (http://www.uepselon.com) 
 */
public class LineRenderer3D extends LineAndShapeRenderer 
                            implements Effect3D, Serializable {
   
    /** For serialization. */
    private static final long serialVersionUID = 5467931468380928736L;
    
    /** The default x-offset for the 3D effect. */
    public static final double DEFAULT_X_OFFSET = 12.0;

    /** The default y-offset for the 3D effect. */
    public static final double DEFAULT_Y_OFFSET = 8.0;
   
    /** The default wall paint. */
    public static final Paint DEFAULT_WALL_PAINT = new Color(0xDD, 0xDD, 0xDD);
   
    /** The size of x-offset for the 3D effect. */
    private double xOffset;

    /** The size of y-offset for the 3D effect. */
    private double yOffset;
   
    /** The paint used to shade the left and lower 3D wall. */
    private transient Paint wallPaint;
   
    /**
     * Creates a new renderer.
     */
    public LineRenderer3D() {
        super(true, false);  //Create a line renderer only
        this.xOffset = DEFAULT_X_OFFSET;
        this.yOffset = DEFAULT_Y_OFFSET;
        this.wallPaint = DEFAULT_WALL_PAINT;
    }
   
    /**
     * Returns the x-offset for the 3D effect.
     *
     * @return The x-offset.
     */
    public double getXOffset() {
        return this.xOffset;
    }

    /**
     * Returns the y-offset for the 3D effect.
     *
     * @return The y-offset.
     */
    public double getYOffset() {
        return this.yOffset;
    }
   
    /**
     * Sets the x-offset.
     * 
     * @param xOffset  the x-offset.
     */
    public void setXOffset(double xOffset) {
        this.xOffset = xOffset;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Sets the y-offset.
     * 
     * @param yOffset  the y-offset.
     */
    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the paint used to highlight the left and bottom wall in the plot
     * background.
     *
     * @return The paint.
     */
    public Paint getWallPaint() {
        return this.wallPaint;
    }

    /**
     * Sets the paint used to hightlight the left and bottom walls in the plot
     * background.
     *
     * @param paint  the paint.
     */
    public void setWallPaint(Paint paint) {
        this.wallPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }
   
    /**
     * Draws the background for the plot.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area inside the axes.
     */
    public void drawBackground(Graphics2D g2, CategoryPlot plot, 
                               Rectangle2D dataArea) {

        float x0 = (float) dataArea.getX();
        float x1 = x0 + (float) Math.abs(this.xOffset);
        float x3 = (float) dataArea.getMaxX();
        float x2 = x3 - (float) Math.abs(this.xOffset);

        float y0 = (float) dataArea.getMaxY();
        float y1 = y0 - (float) Math.abs(this.yOffset);
        float y3 = (float) dataArea.getMinY();
        float y2 = y3 + (float) Math.abs(this.yOffset);

        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();

        // fill background...
        Paint backgroundPaint = plot.getBackgroundPaint();
        if (backgroundPaint != null) {
            g2.setPaint(backgroundPaint);
            g2.fill(clip);
        }

        GeneralPath leftWall = new GeneralPath();
        leftWall.moveTo(x0, y0);
        leftWall.lineTo(x0, y2);
        leftWall.lineTo(x1, y3);
        leftWall.lineTo(x1, y1);
        leftWall.closePath();
        g2.setPaint(getWallPaint());
        g2.fill(leftWall);

        GeneralPath bottomWall = new GeneralPath();
        bottomWall.moveTo(x0, y0);
        bottomWall.lineTo(x1, y1);
        bottomWall.lineTo(x3, y1);
        bottomWall.lineTo(x2, y0);
        bottomWall.closePath();
        g2.setPaint(getWallPaint());
        g2.fill(bottomWall);

        // higlight the background corners...
        g2.setPaint(Color.lightGray);
        Line2D corner = new Line2D.Double(x0, y0, x1, y1);
        g2.draw(corner);
        corner.setLine(x1, y1, x1, y3);
        g2.draw(corner);
        corner.setLine(x1, y1, x3, y1);
        g2.draw(corner);

        // draw background image, if there is one...
        Image backgroundImage = plot.getBackgroundImage();
        if (backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC, plot.getBackgroundAlpha())
            );
            g2.drawImage(
                backgroundImage,
                (int) x1, (int) y3,
                (int) (x3 - x1 + 1), (int) (y1 - y3 + 1),
                null
            );
            g2.setComposite(originalComposite);
        }

    }

    /**
     * Draws the outline for the plot.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area inside the axes.
     */
    public void drawOutline(Graphics2D g2, CategoryPlot plot, 
                            Rectangle2D dataArea) {

        float x0 = (float) dataArea.getX();
        float x1 = x0 + (float) Math.abs(this.xOffset);
        float x3 = (float) dataArea.getMaxX();
        float x2 = x3 - (float) Math.abs(this.xOffset);

        float y0 = (float) dataArea.getMaxY();
        float y1 = y0 - (float) Math.abs(this.yOffset);
        float y3 = (float) dataArea.getMinY();
        float y2 = y3 + (float) Math.abs(this.yOffset);

        GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y2);
        clip.lineTo(x1, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y1);
        clip.lineTo(x2, y0);
        clip.closePath();

        // put an outline around the data area...
        Stroke outlineStroke = plot.getOutlineStroke();
        Paint outlinePaint = plot.getOutlinePaint();
        if ((outlineStroke != null) && (outlinePaint != null)) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(clip);
        }

    }

    /**
     * Draws a grid line against the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data (not yet adjusted for any 
     *                  3D effect).
     * @param value  the Java2D value at which the grid line should be drawn.
     *
     */
    public void drawDomainGridline(Graphics2D g2,
                                   CategoryPlot plot,
                                   Rectangle2D dataArea,
                                   double value) {

        Line2D line1 = null;
        Line2D line2 = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            double y0 = value;
            double y1 = value - getYOffset();
            double x0 = dataArea.getMinX();
            double x1 = x0 + getXOffset();
            double x2 = dataArea.getMaxY();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x2, y1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            double x0 = value;
            double x1 = value + getXOffset();
            double y0 = dataArea.getMaxY();
            double y1 = y0 - getYOffset();
            double y2 = dataArea.getMinY();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x1, y2);
        }
        g2.setPaint(plot.getDomainGridlinePaint());
        g2.setStroke(plot.getDomainGridlineStroke());
        g2.draw(line1);
        g2.draw(line2);

    }

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data (not yet adjusted for any 
     *                  3D effect).
     * @param value  the value at which the grid line should be drawn.
     *
     */
    public void drawRangeGridline(Graphics2D g2,
                                  CategoryPlot plot,
                                  ValueAxis axis,
                                  Rectangle2D dataArea,
                                  double value) {

        Range range = axis.getRange();

        if (!range.contains(value)) {
            return;
        }

        Rectangle2D adjusted = new Rectangle2D.Double(
            dataArea.getX(),
            dataArea.getY() + getYOffset(),
            dataArea.getWidth() - getXOffset(),
            dataArea.getHeight() - getYOffset()
        );

        Line2D line1 = null;
        Line2D line2 = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            double x0 = axis.valueToJava2D(value, adjusted, 
                    plot.getRangeAxisEdge());
            double x1 = x0 + getXOffset();
            double y0 = dataArea.getMaxY();
            double y1 = y0 - getYOffset();
            double y2 = dataArea.getMinY();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x1, y2);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            double y0 = axis.valueToJava2D(value, adjusted,
                    plot.getRangeAxisEdge());
            double y1 = y0 - getYOffset();
            double x0 = dataArea.getMinX();
            double x1 = x0 + getXOffset();
            double x2 = dataArea.getMaxX();
            line1 = new Line2D.Double(x0, y0, x1, y1);
            line2 = new Line2D.Double(x1, y1, x2, y1);
        }
        g2.setPaint(plot.getRangeGridlinePaint());
        g2.setStroke(plot.getRangeGridlineStroke());
        g2.draw(line1);
        g2.draw(line2);

    }

    /**
     * Draws a range marker.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker.
     * @param dataArea  the area for plotting data (not including 3D effect).
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot,
                                ValueAxis axis,
                                Marker marker,
                                Rectangle2D dataArea) {

        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = axis.getRange();
            if (!range.contains(value)) {
                return;
            }

            Rectangle2D adjusted = new Rectangle2D.Double(
                dataArea.getX(), dataArea.getY() + getYOffset(),
                dataArea.getWidth() - getXOffset(), 
                dataArea.getHeight() - getYOffset()
            );

            GeneralPath path = null;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                float x = (float) axis.valueToJava2D(
                    value, adjusted, plot.getRangeAxisEdge()
                );
                float y = (float) adjusted.getMaxY();
                path = new GeneralPath();
                path.moveTo(x, y);
                path.lineTo((float) (x + getXOffset()), 
                        y - (float) getYOffset());
                path.lineTo(
                    (float) (x + getXOffset()), 
                    (float) (adjusted.getMinY() - getYOffset())
                );
                path.lineTo(x, (float) adjusted.getMinY());
                path.closePath();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                float y = (float) axis.valueToJava2D(
                    value, adjusted, plot.getRangeAxisEdge()
                );
                float x = (float) dataArea.getX();
                path = new GeneralPath();
                path.moveTo(x, y);
                path.lineTo(x + (float) this.xOffset, y - (float) this.yOffset);
                path.lineTo((float) (adjusted.getMaxX() + this.xOffset), 
                        y - (float) this.yOffset);
                path.lineTo((float) (adjusted.getMaxX()), y);
                path.closePath();
            }
            g2.setPaint(marker.getPaint());
            g2.fill(path);
            g2.setPaint(marker.getOutlinePaint());
            g2.draw(path);
        }
    }
   
   /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column,
                         int pass) {

        if (!getItemVisible(row, column)) {
            return;   
        }
        
        // nothing is drawn for null...
        Number v = dataset.getValue(row, column);
        if (v == null) {
            return;
        }
       
        Rectangle2D adjusted = new Rectangle2D.Double(
            dataArea.getX(),
            dataArea.getY() + getYOffset(),
            dataArea.getWidth() - getXOffset(),
            dataArea.getHeight() - getYOffset()
        );
       
        PlotOrientation orientation = plot.getOrientation();

        // current data point...
        double x1 = domainAxis.getCategoryMiddle(
            column, getColumnCount(), adjusted, plot.getDomainAxisEdge()
        );
        double value = v.doubleValue();
        double y1 = rangeAxis.valueToJava2D(value, adjusted, 
                plot.getRangeAxisEdge());

        Shape shape = getItemShape(row, column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
        }
       
        if (getItemLineVisible(row, column)) {
            if (column != 0) {

                Number previousValue = dataset.getValue(row, column - 1);
                if (previousValue != null) {

                    // previous data point...
                    double previous = previousValue.doubleValue();
                    double x0 = domainAxis.getCategoryMiddle(
                        column - 1, getColumnCount(), adjusted, 
                        plot.getDomainAxisEdge()
                    );
                    double y0 = rangeAxis.valueToJava2D(
                        previous, adjusted, plot.getRangeAxisEdge()
                    );

                    double x2 = x0 + getXOffset();
                    double y2 = y0 - getYOffset();
                    double x3 = x1 + getXOffset();
                    double y3 = y1 - getYOffset();
                   
                    GeneralPath clip = new GeneralPath();
                   
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        clip.moveTo((float) y0, (float) x0);
                        clip.lineTo((float) y1, (float) x1);
                        clip.lineTo((float) y3, (float) x3);
                        clip.lineTo((float) y2, (float) x2);
                        clip.lineTo((float) y0, (float) x0);
                        clip.closePath();
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        clip.moveTo((float) x0, (float) y0);
                        clip.lineTo((float) x1, (float) y1);
                        clip.lineTo((float) x3, (float) y3);
                        clip.lineTo((float) x2, (float) y2);
                        clip.lineTo((float) x0, (float) y0);
                        clip.closePath();
                    }
                   
                    g2.setPaint(getItemPaint(row, column));
                    g2.fill(clip);
                    g2.setStroke(getItemOutlineStroke(row, column));
                    g2.setPaint(getItemOutlinePaint(row, column));
                    g2.draw(clip);
                }
            }
        }

        // draw the item label if there is one...
        if (isItemLabelVisible(row, column)) {
            drawItemLabel(
                g2, orientation, dataset, row, column, x1, y1, (value < 0.0)
            );
        }

        // add an item entity, if this information is being collected
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, shape);
        }

    }

}
