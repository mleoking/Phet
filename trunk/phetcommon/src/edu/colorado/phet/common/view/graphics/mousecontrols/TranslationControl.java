/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics.mousecontrols;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:43:47 AM
 * Copyright (c) Oct 9, 2003 by Sam Reid
 */
public class TranslationControl implements MouseInputListener {
    private Translatable t;
    private Point last;

    public TranslationControl(Translatable t) {
        this.t = t;
    }

    public void mouseDragged(MouseEvent event) {
        if (last == null) {
            mousePressed(event);
            return;
        }
        Point modelLoc = event.getPoint();
        Point2D.Double dx = new Point2D.Double(modelLoc.x - last.x, modelLoc.y - last.y);
        t.translate(dx.x, dx.y);
        last = modelLoc;
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent event) {
        last = event.getPoint();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseEntered(MouseEvent event, Point2D.Double modelLoc) {
    }

    public void mouseExited(MouseEvent event, Point2D.Double modelLoc) {
    }
}
