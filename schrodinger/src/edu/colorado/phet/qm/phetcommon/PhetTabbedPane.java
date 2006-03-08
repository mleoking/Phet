/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.piccolo.nodes.HTMLGraphic;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 1, 2006
 * Time: 3:13:05 PM
 * Copyright (c) Mar 1, 2006 by Sam Reid
 */

public class PhetTabbedPane extends JPanel {
    private TabPane tabPane;
    private JComponent component;
    private Color selectedTabColor;
    private ArrayList changeListeners = new ArrayList();
    private static final Color DEFAULT_SELECTED_TAB_COLOR = new Color( 150, 150, 255 );

    public PhetTabbedPane() {
        this( DEFAULT_SELECTED_TAB_COLOR );
    }

    public PhetTabbedPane( Color selectedTabColor ) {
        super( new BorderLayout() );
        this.selectedTabColor = selectedTabColor;
        component = new JPanel();//empty component to start
        tabPane = new TabPane( selectedTabColor );
        add( tabPane, BorderLayout.NORTH );
        setComponent( component );
        addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                relayoutComponents();
            }

            public void componentShown( ComponentEvent e ) {
                relayoutComponents();
            }
        } );
    }

    public int getTabCount() {
        return tabPane.getTabCount();
    }

    private void relayoutComponents() {
        Rectangle bounds = component.getBounds();
        for( int i = 0; i < getTabCount(); i++ ) {
            tabPane.getTabs()[i].getComponent().setBounds( bounds );//to mimic behavior in JTabbedPane
        }
    }

    public String getTitleAt( int i ) {
        return tabPane.getTitleAt( i );
    }

    public void removeTabAt( int i ) {
        tabPane.removeTabAt( i );
        if( getTabCount() > 0 ) {
            setSelectedIndex( i - 1 );
        }
    }

    public void addChangeListener( ChangeListener changeListener ) {
        changeListeners.add( changeListener );
    }

    public int getSelectedIndex() {
        return tabPane.getSelectedIndex();
    }

    public void setSelectedTabColor( Color color ) {
        this.selectedTabColor = color;
        tabPane.setSelectedTabColor( color );
    }

    public void addTab( String title, JComponent content ) {
        final AbstractTabNode tab = new TabNodeFactory().createTabNode( title, content, selectedTabColor );
        tab.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseReleased( PInputEvent e ) {
                if( tab.getFullBounds().contains( e.getCanvasPosition() ) ) {
                    setSelectedTab( tab );
                }
            }

            public void mouseEntered( PInputEvent event ) {
                PhetTabbedPane.this.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
            }

            public void mouseExited( PInputEvent event ) {
                PhetTabbedPane.this.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            }
        } );
        if( tabPane.getTabs().length == 0 ) {
            setSelectedTab( tab );
            tabPane.setActiveTab( tab );
        }
        else {
            tab.setSelected( false );
        }
        tabPane.addTab( tab );
    }

    public void setSelectedIndex( int index ) {
        if( index < 0 || index >= getTabCount() ) {
            throw new RuntimeException( "Illegal tab index: " + index + ", tab count=" + getTabCount() );
        }
        setSelectedTab( tabPane.getTabs()[index] );
    }

    private void setSelectedTab( AbstractTabNode tab ) {
        setComponent( tab.getComponent() );
        tab.setSelected( true );
        for( int i = 0; i < tabPane.getTabs().length; i++ ) {
            AbstractTabNode t = tabPane.getTabs()[i];
            if( t != tab ) {
                t.setSelected( false );
            }
        }
        tabPane.setActiveTab( tab );
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        ChangeEvent changeEvent = new ChangeEvent( this );
        for( int i = 0; i < changeListeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)changeListeners.get( i );
            changeListener.stateChanged( changeEvent );
        }
    }

    private void setComponent( JComponent component ) {
        if( this.component != null ) {
            remove( this.component );
        }
        this.component = component;
        add( component, BorderLayout.CENTER );
        invalidate();
        doLayout();
        validateTree();
        repaint();
    }

    public static class TabNodeFactory {
        public AbstractTabNode createTabNode( String text, JComponent component, Color selectedTabColor ) {
//            return new TextTabNode( text, component, selectedTabColor );
            return new HTMLTabNode( text, component, selectedTabColor );
        }
    }

    public static class HTMLTabNode extends AbstractTabNode {
        private HTMLGraphic htmlGraphic;

        public HTMLTabNode( String text, JComponent component, Color selectedTabColor ) {
            super( text, component, selectedTabColor );
        }

        protected PNode createTextNode( String text, Color selectedTabColor ) {
            this.htmlGraphic = new HTMLGraphic( text, getTabFont(), Color.black );
            return this.htmlGraphic;
        }

        protected void updateTextNode() {
            this.htmlGraphic.setFont( getTabFont() );
            this.htmlGraphic.setColor( (Color)getTextPaint() );
        }
    }

    public static class TextTabNode extends AbstractTabNode {
        private PText pText;

        public TextTabNode( String text, JComponent component, Color selectedTabColor ) {
            super( text, component, selectedTabColor );
        }

        protected PNode createTextNode( String text, Color selectedTabColor ) {
            this.pText = new PText( text );
            this.pText.setFont( getTabFont() );
            return pText;
        }

        protected void updateTextNode() {
            pText.setFont( getTabFont() );
            pText.setTextPaint( getTextPaint() );
        }
    }

    public static abstract class AbstractTabNode extends PNode {
        private String text;
        private JComponent component;
        private PNode textNode;
        private PPath background;
        private boolean selected;
        private static final Insets tabInsets = new Insets( 2, 15, 0, 15 );
        private float tiltWidth = 11;
        private Color selectedTabColor;
        private PPath outlineNode;

        public AbstractTabNode( String text, JComponent component, Color selectedTabColor ) {
            this.selectedTabColor = selectedTabColor;
            this.text = text;
            this.component = component;

            textNode = createTextNode( text, selectedTabColor );

            outlineNode = new PPath( createTabTopBorder( textNode.getFullBounds().getWidth(), textNode.getFullBounds().getHeight() ) );
            background = new PPath( createTabShape( textNode.getFullBounds().getWidth(), textNode.getFullBounds().getHeight() ) );
            background.setPaint( selectedTabColor );
            background.setStroke( null );
            addChild( background );
            addChild( textNode );
            addChild( outlineNode );
        }

        protected abstract PNode createTextNode( String text, Color selectedTabColor );

        public void setTabTextHeight( double tabHeight ) {
            background.setPathTo( createTabShape( textNode.getFullBounds().getWidth(), tabHeight ) );
            outlineNode.setPathTo( createTabTopBorder( textNode.getFullBounds().getWidth(), tabHeight ) );
        }

        private GeneralPath createTabTopBorder( double textWidth, double textHeight ) {
            GeneralPath outline = new GeneralPath();
            outline.moveTo( -tabInsets.left, (float)( textHeight + tabInsets.bottom ) );
            outline.lineTo( -tabInsets.left, -tabInsets.top );
            outline.lineTo( (float)( textWidth + tabInsets.right ), -tabInsets.top );
            outline.lineTo( (float)textWidth + tabInsets.right + tiltWidth, (float)( textHeight + tabInsets.bottom ) );
            return outline;
        }

        private GeneralPath createTabShape( double textWidth, double textHeight ) {
            GeneralPath path = new GeneralPath();
            path.moveTo( -tabInsets.left, -tabInsets.top );
            path.lineTo( (float)( textWidth + tabInsets.right ), -tabInsets.top );
            path.lineTo( (float)textWidth + tabInsets.right + tiltWidth, (float)( textHeight + tabInsets.bottom ) );
            path.lineTo( -tabInsets.left, (float)( textHeight + tabInsets.bottom ) );
            path.closePath();
            return path;
        }

        public JComponent getComponent() {
            return component;
        }

        public void setSelected( boolean selected ) {
            this.selected = selected;
            updateTextNode();
            background.setStroke( getBorderStroke() );
            outlineNode.setVisible( selected );
            updatePaint();
        }

        protected abstract void updateTextNode();

        private void updatePaint() {
            background.setPaint( getBackgroundPaint() );
            background.setStrokePaint( getBorderStrokePaint() );
        }

        private Paint getBorderStrokePaint() {
            return Color.gray;
        }

        private Stroke getBorderStroke() {
            return ( !selected ) ? new BasicStroke( 1.0f ) : null;
        }

        private Paint getBackgroundPaint() {
            if( selected ) {
                return new GradientPaint( 0, (float)background.getFullBounds().getY() - 2, selectedTabColor.brighter(), 0, (float)( background.getFullBounds().getY() + 6 ), selectedTabColor );
            }
            else {
                return new GradientPaint( 0, 0, new Color( 240, 240, 240 ), 0, 15, new Color( 200, 200, 200 ) );//grayed out
            }
        }

        protected Paint getTextPaint() {
            return selected ? Color.black : Color.gray;
        }

        public Font getTabFont() {
            return new Font( "Lucida Sans", Font.BOLD, 22 );
        }

        public String getText() {
            return text;
        }

        public void setSelectedTabColor( Color color ) {
            this.selectedTabColor = color;
            updatePaint();
        }

        public double getTextHeight() {
            return textNode.getFullBounds().getHeight();
        }
    }

    public static class TabBase extends PNode {
        private final PPath path;
        private int tabBaseHeight = 6;
        private Color selectedTabColor;

        public TabBase( Color selectedTabColor ) {
            this.selectedTabColor = selectedTabColor;
            path = new PPath( new Rectangle( 0, 0, 200, tabBaseHeight ) );
            path.setPaint( selectedTabColor );
            path.setPaint( new GradientPaint( 0, 0, selectedTabColor, 0, tabBaseHeight + 4, selectedTabColor.darker() ) );
            path.setStroke( null );
            addChild( path );
            updatePaint();
        }

        public void setTabBaseWidth( int width ) {
            path.setPathTo( new Rectangle( 0, 0, width, tabBaseHeight ) );
        }

        public void updatePaint() {
            path.setPaint( new GradientPaint( 0, 0, selectedTabColor, 0, tabBaseHeight, darker( selectedTabColor, 75 ) ) );
        }

        public void setSelectedTabColor( Color color ) {
            this.selectedTabColor = color;
            updatePaint();
        }
    }

    public static int darker( int value, int d ) {
        return Math.max( 0, value - d );
    }

    public static Color darker( Color a, int d ) {
        return new Color( darker( a.getRed(), d ), darker( a.getGreen(), d ), darker( a.getBlue(), d ) );
    }

    static class TabPane extends PCanvas {
        private ArrayList tabs = new ArrayList();
        private double distBetweenTabs = -6;
        private TabBase tabBase;
        private int tabTopInset = 3;
        private PImage logo;
        private AbstractTabNode activeTab;
        private static final int LEFT_TAB_INSET = 10;

        public AbstractTabNode getActiveTab() {
            return activeTab;
        }

        public TabPane( Color selectedTabColor ) {
            logo = PImageFactory.create( "images/phetlogo3.png" );
            tabBase = new TabBase( selectedTabColor );
            setPanEventHandler( null );
            setZoomEventHandler( null );
            setOpaque( false );

            getLayer().addChild( logo );
            getLayer().addChild( tabBase );
            addComponentListener( new ComponentListener() {
                public void componentHidden( ComponentEvent e ) {
                }

                public void componentMoved( ComponentEvent e ) {
                }

                public void componentResized( ComponentEvent e ) {
                    relayout();
                }

                public void componentShown( ComponentEvent e ) {
                    relayout();
                }
            } );
            relayout();
        }

        public void addTab( AbstractTabNode tab ) {
            tabs.add( tab );
            getLayer().addChild( 0, tab );
            relayout();
            setActiveTab( getActiveTab() );//updates
        }

        private void relayout() {
            tabBase.setTabBaseWidth( getWidth() );
            int x = AbstractTabNode.tabInsets.left + LEFT_TAB_INSET;
            double maxTabTextHeight = getMaxTabTextHeight();
            for( int i = 0; i < tabs.size(); i++ ) {
                AbstractTabNode tabNode = (AbstractTabNode)tabs.get( i );
                tabNode.setOffset( x, tabTopInset );
                tabNode.setTabTextHeight( maxTabTextHeight );
                x += tabNode.getFullBounds().getWidth() + distBetweenTabs;
            }
            tabBase.setOffset( 0, getHeight() - tabBase.getFullBounds().getHeight() );
            logo.setOffset( getWidth() - logo.getFullBounds().getWidth(), 0 );
            if( tabs.size() > 0 ) {
                AbstractTabNode lastTab = (AbstractTabNode)tabs.get( tabs.size() - 1 );
                if( logo.getXOffset() < lastTab.getFullBounds().getMaxX() ) {
                    logo.setVisible( false );
                }
                else {
                    logo.setVisible( true );
                }
            }
            for( int i = 0; i < tabs.size(); i++ ) {
                tabAt( i ).updatePaint();
            }
            tabBase.updatePaint();
        }

        public Dimension getPreferredSize() {
            relayout();
            int h = getMaxTabHeight();
            int width = (int)getLayer().getFullBounds().getWidth();
            width = Math.max( width, super.getPreferredSize().width );
            return new Dimension( width, (int)( h + tabBase.getFullBounds().getHeight() ) );
        }

        public double getMaxTabTextHeight() {
            double h = 0;
            for( int i = 0; i < tabs.size(); i++ ) {
                AbstractTabNode tabNode = (AbstractTabNode)tabs.get( i );
                h = Math.max( h, tabNode.getTextHeight() );
            }
            return h;
        }

        public int getMaxTabHeight() {
            int h = 0;
            for( int i = 0; i < tabs.size(); i++ ) {
                AbstractTabNode tabNode = (AbstractTabNode)tabs.get( i );
                h = (int)Math.max( h, tabNode.getFullBounds().getHeight() );
            }
            return h;
        }

        public AbstractTabNode[] getTabs() {
            return (AbstractTabNode[])tabs.toArray( new AbstractTabNode[0] );
        }

        public void setActiveTab( AbstractTabNode tab ) {
            this.activeTab = tab;
            getLayer().removeChild( tabBase );
            getLayer().addChild( tabBase );
            if( getLayer().getChildrenReference().contains( tab ) ) {
                getLayer().removeChild( tab );
            }
            getLayer().addChild( tab );
        }

        public int getSelectedIndex() {
            return tabs.indexOf( activeTab );
        }

        public int getTabCount() {
            return tabs.size();
        }

        public String getTitleAt( int i ) {
            return tabAt( i ).getText();
        }

        private AbstractTabNode tabAt( int i ) {
            return (AbstractTabNode)tabs.get( i );
        }

        public void removeTabAt( int i ) {
            tabs.remove( i );
        }

        public void setSelectedTabColor( Color color ) {
            for( int i = 0; i < tabs.size(); i++ ) {
                AbstractTabNode tabNode = (AbstractTabNode)tabs.get( i );
                tabNode.setSelectedTabColor( color );
            }
            tabBase.setSelectedTabColor( color );
        }
    }

}
