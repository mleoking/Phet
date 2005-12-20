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
 * ------------------------
 * NotOutlierException.java
 * ------------------------
 * (C) Copyright 2003-2005, by David Browning and Contributors.
 *
 * Original Author:  David Browning (for Australian Institute of Marine 
 *                   Science);
 * Contributor(s):   -;
 *
 * $Id$
 *
 * Changes
 * -------
 * 05-Aug-2003 : Version 1, contributed by David Browning (DG);
 *
 */

package org.jfree.chart.renderer;

/**
 * An exception that is generated by the {@link Outlier}, {@link OutlierList} 
 * and {@link OutlierListCollection} classes.
 *
 * @author David Browning
 */
public class NotOutlierException extends Exception {

    /**
     * Creates a new exception.
     *
     * @param message  the exception message.
     */
    public NotOutlierException(String message) {
        super(message);
    }

}
