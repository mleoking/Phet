/**
 * Class: CompositeInteractiveGraphicMouseDelegator
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Jun 18, 2004
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

public class CompositeInteractiveGraphicMouseDelegator implements MouseInputListener {
    CompositeGraphic compositeInteractiveGraphic;
    MouseInputListener activeUnit;

    public CompositeInteractiveGraphicMouseDelegator( CompositeGraphic compositeInteractiveGraphic ) {
        this.compositeInteractiveGraphic = compositeInteractiveGraphic;
    }

    public void startDragging( MouseEvent event, MouseInputListener activeUnit ) {
        if( activeUnit != null ) {
            activeUnit.mouseExited( event );
        }
        this.activeUnit = activeUnit;
    }

    public void handleEntranceAndExit( MouseEvent e ) {
        // Find the topmost graphic that can handle the event
        MouseInputListener unit = getLeaf( e.getPoint(), compositeInteractiveGraphic );
        if( unit == null ) {
            // If the mouse isn't over anything contained in the
            // CompositeGraphic...
            if( activeUnit != null ) {
                activeUnit.mouseExited( e );
                activeUnit = null;
            }
        }
        else {//unit was non-null.
            if( activeUnit == unit ) {
                //same guy
            }
            else if( activeUnit == null ) {
                //Fire a mouse entered, set the active unit.
                activeUnit = unit;
                activeUnit.mouseEntered( e );
            }
            else if( activeUnit != unit ) {
                //Switch active units.
                activeUnit.mouseExited( e );
                activeUnit = unit;
                activeUnit.mouseEntered( e );
            }
        }
    }

    private MouseInputListener getLeaf( Point p, CompositeGraphic cig ) {
        Graphic[] graphics = cig.getGraphics();
        MouseInputListener result = null;
        for( int i = graphics.length - 1; result == null && i >= 0; i-- ) {
            if( graphics[i] instanceof CompositeGraphic ) {
                CompositeGraphic compositeInteractiveGraphic = (CompositeGraphic)graphics[i];
                if( compositeInteractiveGraphic.isVisible() ) {
                    result = getLeaf( p, compositeInteractiveGraphic );
                }
            }
            else if( graphics[i] instanceof MouseInputListener && graphics[i] instanceof Boundary ) {
                Boundary b = (Boundary)graphics[i];
                if( b.contains( p.x, p.y ) ) {
                    result = (MouseInputListener)graphics[i];
                }
            }
        }
        return result;
    }

    public void mouseClicked( MouseEvent e ) {
        //Make sure we're over the active guy.
        handleEntranceAndExit( e );
        if( activeUnit != null ) {
            activeUnit.mouseClicked( e );
        }
    }

    public void mousePressed( MouseEvent e ) {
        handleEntranceAndExit( e );
        if( activeUnit != null ) {
            activeUnit.mousePressed( e );
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if( activeUnit != null ) {
            activeUnit.mouseReleased( e );
        }
    }

    /**
     * This method is no-op because if the user is dragging a graphic,
     * and handleEntranceAndExit() gets
     * called, the boundary may be dropped.
     *
     * @param e
     */
    public void mouseEntered( MouseEvent e ) {
    }

    /**
     * This method is no-op because if the user is dragging a graphic,
     * and handleEntranceAndExit() gets
     * called, the boundary may be dropped.
     *
     * @param e
     */
    public void mouseExited( MouseEvent e ) {
    }

    public void mouseDragged( MouseEvent e ) {
        if( activeUnit != null ) {
            activeUnit.mouseDragged( e );
        }
    }

    public void mouseMoved( MouseEvent e ) {
        //iterate down over the mouse handlers.
        handleEntranceAndExit( e );
        if( activeUnit != null ) {
            activeUnit.mouseMoved( e );
        }
    }
}
