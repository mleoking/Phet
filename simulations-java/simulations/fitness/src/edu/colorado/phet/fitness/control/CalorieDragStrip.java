package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ToolTipNode;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.FitnessStrings;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.colorado.phet.fitness.model.Human;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Created by: Sam
 * May 26, 2008 at 10:14:57 AM
 */
public class CalorieDragStrip extends PNode {
    private static Random random = new Random();
    private ArrayList listeners = new ArrayList();
    private static final int HEIGHT = 45;
    private PNode tooltipLayer = new PNode();
    private PNode stripPanel;
    private int count = 5;
    private ArrayList panels = new ArrayList();
    private Color buttonColor = new Color( 128, 128, 255 );
    private PClip stripPanelClip;

    public CalorieDragStrip( final CalorieSet available ) {
        for ( int i = 0; i < available.getItemCount(); i += count ) {
            panels.add( getPanel( available, i, Math.min( i + count, available.getItemCount() ) ) );
        }
        stripPanelClip = new PClip();
        stripPanel = (PNode) panels.get( 0 );
        stripPanelClip.addChild( stripPanel );

        addChild( stripPanelClip );

        GradientButtonNode leftButton = new GradientButtonNode( "<html>&gt;</html>", 13, buttonColor );
        leftButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                nextPanel( +1 );
            }
        } );
        addChild( leftButton );
        leftButton.setOffset( getMaxPanelWidth(), getMaxPanelHeight() / 2 - leftButton.getFullBounds().getHeight() / 2 );

        GradientButtonNode rightButton = new GradientButtonNode( "<html>&lt;</html>", 13, buttonColor );
        rightButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                nextPanel( -1 );
            }
        } );
        addChild( rightButton );
        rightButton.setOffset( -rightButton.getFullBounds().getWidth(), getMaxPanelHeight() / 2 - rightButton.getFullBounds().getHeight() / 2 );

        centerItems();
        stripPanelClip.setPathTo( new Rectangle2D.Double( 0, 0, getMaxPanelWidth(), getMaxPanelHeight() ) );
    }

    private void centerItems() {
        for ( int i = 0; i < panels.size(); i++ ) {
            PNode pNode = (PNode) panels.get( i );
            for ( int k = 0; k < pNode.getChildrenCount(); k++ ) {
                PNode child = pNode.getChild( k );
                if ( child instanceof DefaultDragNode ) {
                    child.setOffset( getMaxPanelWidth() / 2 - child.getFullBounds().getWidth() / 2, child.getOffset().getY() );
                }
            }
        }
    }

    private double getMaxPanelHeight() {
        double max = Double.NaN;
        for ( int i = 0; i < panels.size(); i++ ) {
            PNode pNode = (PNode) panels.get( i );
            if ( Double.isNaN( max ) || pNode.getFullBounds().getHeight() > max ) {
                max = pNode.getFullBounds().getHeight();
            }
        }
        return max;
    }

    private double getMaxPanelWidth() {
        double max = Double.NaN;
        for ( int i = 0; i < panels.size(); i++ ) {
            PNode pNode = (PNode) panels.get( i );
            if ( Double.isNaN( max ) || pNode.getFullBounds().getWidth() > max ) {
                max = pNode.getFullBounds().getWidth();
            }
        }
        return max;
    }

    Timer timer = null;

    private void nextPanel( final int increment ) {
        if ( timer != null && timer.isRunning() ) {
            return;
        }
        final PNode oldStripPanel = stripPanel;
        stripPanel = (PNode) panels.get( nextIndex( increment ) );
        stripPanelClip.addChild( stripPanel );
        stripPanel.setOffset( 100 * increment, 0 );

        timer = new Timer( 30, null );
        timer.addActionListener( new ActionListener() {
            int count = 0;

            public void actionPerformed( ActionEvent e ) {
                oldStripPanel.translate( -10 * increment, 0 );
                stripPanel.translate( -10 * increment, 0 );
                count++;
                if ( count >= 10 ) {
                    stripPanelClip.removeChild( oldStripPanel );
                    timer.stop();
                }
            }
        } );
        timer.start();
    }

    private int nextIndex( int increment ) {
        int index = panels.indexOf( stripPanel );
        int newIndex = index + increment;
        if ( newIndex >= panels.size() ) {
            newIndex = 0;
        }
        if ( newIndex < 0 ) {
            newIndex = panels.size() - 1;
        }
        return newIndex;
    }

    private PNode getPanel( final CalorieSet available, int min, int max ) {
        ArrayList nodes = new ArrayList();
        PNode sourceLayer = new PNode();
        for ( int i = min; i < max; i++ ) {
            final DefaultDragNode node = createNode( available.getItem( i ) );
            final int i1 = i;
            node.addInputEventListener( new PDragSequenceEventHandler() {
                private DefaultDragNode createdNode = null;

                protected void startDrag( PInputEvent e ) {
                    super.startDrag( e );
                    CaloricItem caloricItem = (CaloricItem) available.getItem( i1 ).clone();

                    createdNode = createNode( caloricItem );
                    createdNode.addDragHandler();
                    createdNode.getPNode().setOffset( node.getOffset() );
                    createdNode.setDragging( true );
                    addChild( createdNode.getPNode() );
                }

                protected void drag( PInputEvent event ) {
                    super.drag( event );
                    createdNode.getPNode().translate( event.getDelta().getWidth(), event.getDelta().getHeight() );

                    notifyDragged( createdNode );
                }

                protected void endDrag( PInputEvent e ) {
                    super.endDrag( e );
                    createdNode.setDragging( false );
                    notifyDropped( createdNode );
                }
            } );

            nodes.add( node );
        }
        int COLS = 1;
        for ( int i = 1; i < nodes.size(); i++ ) {
            int row = i / COLS;
            int col = i % COLS;
            PNode pNode = (PNode) nodes.get( i );
            PNode prev = (PNode) nodes.get( i - 1 );
            pNode.setOffset( col == 0 ? 0 : prev.getFullBounds().getMaxX(), row * HEIGHT );

        }
        for ( int i = 0; i < nodes.size(); i++ ) {
            sourceLayer.addChild( (PNode) nodes.get( i ) );
        }
        return sourceLayer;
    }

    //To be used in an external layer in order to simplify the layout code 
    public PNode getTooltipLayer() {
        return tooltipLayer;
    }

    public void removeItem( DragNode droppedNode ) {
        removeChild( droppedNode.getPNode() );
    }

    public void itemRemoved( CaloricItem item ) {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if ( child instanceof DefaultDragNode ) {
                DefaultDragNode dragNode = (DefaultDragNode) child;
                if ( !dragNode.isDragging() && dragNode.getItem() == item ) {
                    removeChild( child );
                    i--;
                }
            }
        }
    }

    public DefaultDragNode getNode( CaloricItem item ) {
        //should only handle events from sources other than this
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if ( child instanceof DefaultDragNode ) {
                DefaultDragNode dragNode = (DefaultDragNode) child;
                if ( dragNode.getItem() == item ) {
//                    removeChild( child );
//                    i--;
                    return (DefaultDragNode) child;
                }
            }
        }
        return null;
    }

    public PNode addItemNode( CaloricItem item ) {
        final DefaultDragNode node = createNode( item );
        node.addDragHandler();
        addChild( node );
        return node.getPNode();
    }

    public Rectangle2D getSourceBounds() {
        return stripPanel.getFullBounds();
    }

    public void resetAll() {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if ( child instanceof DefaultDragNode ) {
                DefaultDragNode dragNode = (DefaultDragNode) child;
                if ( !dragNode.isDragging() ) {
                    removeChild( child );
                    i--;
                }
            }
        }
    }

    private class DefaultDragNode extends PNode implements DragNode {
        private CaloricItem item;
        private PNode node;
        private boolean dragging = false;//todo: could coalesce with PDragSequenceEventHandler.isDragging

        public DefaultDragNode( PNode node, CaloricItem item ) {
            this.item = item;
            this.node = node;
            addChild( node );
            node.addInputEventListener( new CursorHandler() );
        }

        public void addDragHandler() {
            node.addInputEventListener( new PDragSequenceEventHandler() {
                protected void startDrag( PInputEvent e ) {
                    super.startDrag( e );
                    setDragging( true );
                    moveToFront();
                }

                protected void drag( PInputEvent event ) {
                    super.drag( event );
                    setDragging( true );//todo: remove this workaround, which was necessary because setDragging(true) from startDrag wasn't being called at the right time
                    getPNode().translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
                    notifyDragged( DefaultDragNode.this );
                }

                protected void endDrag( PInputEvent e ) {
                    setDragging( false );
                    notifyDropped( DefaultDragNode.this );
                }
            } );
        }

        public boolean isDragging() {
            return dragging;
        }

        public PNode getPNode() {
            return this;
        }

        public CaloricItem getItem() {
            return item;
        }

        public void setDragging( boolean b ) {
            this.dragging = b;
//            System.out.println( "CalorieDragStrip$DefaultDragNode.setDragging: " + b );
        }
    }

    private DefaultDragNode createNode( final CaloricItem item ) {
        if ( item.getImage() != null && item.getImage().trim().length() > 0 ) {
            final DefaultDragNode dragNode = new DefaultDragNode( new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), HEIGHT ) ), item );
            ToolTipNode toolTipNode = new ToolTipNode( "<html>" + item.getName() + " (" + FitnessStrings.KCAL_PER_DAY_FORMAT.format( item.getCalories() ) + " " + FitnessResources.getString( "units.cal" ) + ")</html>", dragNode );
            toolTipNode.setFont( new PhetFont( 16, true ) );

            if ( item.getImage().equals( Human.FOOD_PYRAMID ) ) {
                handleFoodPyramid( item, dragNode );
            }
            tooltipLayer.addChild( toolTipNode );
            return dragNode;
        }
        else {
            return new DefaultDragNode( new PhetPPath( new Rectangle( 0, 0, 10, 10 ), new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) ) ), item );
        }
    }

    private void handleFoodPyramid( final CaloricItem item, DefaultDragNode dragNode ) {
        final JDialog dialog = new JDialog();
        JLabel contentPane = new JLabel( item.getLabelText(), new ImageIcon( FitnessResources.getImage( item.getImage() ) ), SwingConstants.CENTER ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };

        contentPane.setOpaque( true );
        contentPane.setBackground( Color.white );
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.setContentPane( contentPane );
        dialog.pack();

        GradientButtonNode gradientButtonNode = new GradientButtonNode( "?", 12, Color.red );
        gradientButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.setVisible( true );
            }
        } );
        dragNode.addChild( gradientButtonNode );
        gradientButtonNode.setOffset( dragNode.getFullBounds().getMaxX() - gradientButtonNode.getFullBounds().getWidth() / 2, dragNode.getFullBounds().getY() );
    }

    public static interface DragNode {
        PNode getPNode();

        CaloricItem getItem();
    }

    public static interface Listener {
        void nodeDropped( DragNode node );

        void nodeDragged( DragNode createdNode );
    }

    public static class Adapter implements Listener {
        public void nodeDropped( DragNode node ) {
        }

        public void nodeDragged( DragNode createdNode ) {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDropped( DragNode createdNode ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).nodeDropped( createdNode );
        }
    }

    public void notifyDragged( DragNode createdNode ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).nodeDragged( createdNode );
        }
    }
}
