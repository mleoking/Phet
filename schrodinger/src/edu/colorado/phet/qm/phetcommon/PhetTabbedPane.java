/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
    private JComponent body;
    private Color selectedTabColor;
    private ArrayList changeListeners = new ArrayList();

    public PhetTabbedPane() {
        this( new Color( 150, 150, 255 ) );
    }

    public int getTabCount() {
        return tabPane.getTabCount();
    }

    public PhetTabbedPane( Color selectedTabColor ) {
        super( new BorderLayout() );
        this.selectedTabColor = selectedTabColor;
        body = new JPanel();//empty body to start
        tabPane = new TabPane( selectedTabColor );
        add( tabPane, BorderLayout.NORTH );
        setBody( body );
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
    }

    private void relayout() {
        Rectangle bounds = body.getBounds();
        for( int i = 0; i < getTabCount(); i++ ) {
            tabPane.getTabs()[i].getContentPanel().setBounds( bounds );//to mimic behavior in JTabbedPane
        }
    }

    public String getTitleAt( int i ) {
        return tabPane.getTitleAt( i );
    }

    public void removeTabAt( int i ) {
        tabPane.removeTabAt( i );
        setSelectedIndex( i - 1 );
    }

    public void addChangeListener( ChangeListener changeListener ) {
        changeListeners.add( changeListener );
    }

    public int getSelectedIndex() {
        return tabPane.getSelectedIndex();
    }

    public void addTab( String title, JComponent content ) {
        final TabNode tab = new TabNode( title, content, selectedTabColor );
        tab.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseReleased( PInputEvent e ) {
                setSelectedTab( tab );
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
        setSelectedTab( tabPane.getTabs()[index] );
    }

    private void setSelectedTab( TabNode tab ) {
        setBody( tab.getContentPanel() );
        tab.setSelected( true );
        for( int i = 0; i < tabPane.getTabs().length; i++ ) {
            TabNode t = tabPane.getTabs()[i];
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

    private void setBody( JComponent contentPanel ) {
        if( body != null ) {
            remove( body );
        }
        this.body = contentPanel;
        add( contentPanel, BorderLayout.CENTER );
        invalidate();
        doLayout();
        validateTree();
        repaint();
    }

    public static class TabNode extends PNode {
        private String text;
        private JComponent content;
        private final PText ptext;
        private final PPath background;
        private boolean selected;
        private static final Insets tabInsets = new Insets( 2, 15, 0, 15 );
        private float tiltWidth = 11;
        private Color selectedTabColor;

        public TabNode( String text, JComponent content, Color selectedTabColor ) {
            this.selectedTabColor = selectedTabColor;
            this.text = text;
            this.content = content;

            ptext = new PText( text );
            ptext.setFont( getTabFont() );

            GeneralPath path = new GeneralPath();
            path.moveTo( -tabInsets.left, -tabInsets.top );
            path.lineTo( (float)( ptext.getFullBounds().getWidth() + tabInsets.right ), -tabInsets.top );
            path.lineTo( (float)ptext.getFullBounds().getWidth() + tabInsets.right + tiltWidth, (float)( ptext.getFullBounds().getHeight() + tabInsets.bottom ) );
            path.lineTo( -tabInsets.left, (float)( ptext.getFullBounds().getHeight() + tabInsets.bottom ) );
            path.closePath();
            background = new PPath( path );
            background.setPaint( selectedTabColor );
            background.setStroke( null );
            addChild( background );
            addChild( ptext );
        }

        public JComponent getContentPanel() {
            return content;
        }

        public void setSelected( boolean selected ) {
            this.selected = selected;
            ptext.setFont( getTabFont() );
            ptext.setTextPaint( getTextPaint() );
            background.setStroke( getBorderStroke() );
            updatePaint();
        }

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

        private Paint getTextPaint() {
            return selected ? Color.black : Color.gray;
        }

        private Font getTabFont() {
            return new Font( "Lucida Sans", Font.BOLD, 22 );
        }

        public String getText() {
            return text;
        }
    }

    static class TabBase extends PNode {
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
            path.setPaint( new GradientPaint( 0, 0, selectedTabColor, 0, tabBaseHeight, selectedTabColor.darker() ) );
        }
    }

    static class TabPane extends PCanvas {
        private ArrayList tabs = new ArrayList();
        private double distBetweenTabs = -6;
        private TabBase tabBase;
        private int tabTopInset = 3;
        private PImage logo;
        private TabNode activeTab;
        private static final int LEFT_TAB_INSET = 10;

        public TabNode getActiveTab() {
            return activeTab;
        }

        public TabPane( Color selectedTabColor ) {
            logo = PImageFactory.create( "images/phetlogo2.png" );
            logo.scale( 0.9 );
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

        public void addTab( TabNode tab ) {
            tabs.add( tab );
            getLayer().addChild( 0, tab );
            relayout();
            setActiveTab( getActiveTab() );//updates
        }

        private void relayout() {
            tabBase.setTabBaseWidth( getWidth() );
            int x = TabNode.tabInsets.left + LEFT_TAB_INSET;
            for( int i = 0; i < tabs.size(); i++ ) {
                TabNode tabNode = (TabNode)tabs.get( i );
                tabNode.setOffset( x, tabTopInset );
                x += tabNode.getFullBounds().getWidth() + distBetweenTabs;
            }
            tabBase.setOffset( 0, getHeight() - tabBase.getFullBounds().getHeight() );
            logo.setOffset( getWidth() - logo.getFullBounds().getWidth(), 2 );
            if( tabs.size() > 0 ) {
                TabNode lastTab = (TabNode)tabs.get( tabs.size() - 1 );
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
            int h = 0;
            for( int i = 0; i < tabs.size(); i++ ) {
                TabNode tabNode = (TabNode)tabs.get( i );
                h = (int)Math.max( h, tabNode.getFullBounds().getHeight() );
            }
            int width = (int)getLayer().getFullBounds().getWidth();
            width = Math.max( width, super.getPreferredSize().width );
            return new Dimension( width, (int)( h + tabBase.getFullBounds().getHeight() ) );
        }

        public TabNode[] getTabs() {
            return (TabNode[])tabs.toArray( new TabNode[0] );
        }

        public void setActiveTab( TabNode tab ) {
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

        private TabNode tabAt( int i ) {
            return (TabNode)tabs.get( i );
        }

        public void removeTabAt( int i ) {
            tabs.remove( i );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Tab Test" );
        PhetTabbedPane phetTabbedPane = new PhetTabbedPane();
        phetTabbedPane.addTab( "Hello Tab", new JLabel( "Hello" ) );
        phetTabbedPane.addTab( "Slider Tab", new JSlider() );
        phetTabbedPane.addTab( "Color Chooser", new JColorChooser() );
        JButton content = new JButton( "Button" );
        phetTabbedPane.addTab( "A Button", content );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
        frame.addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_X ) {
                    System.exit( 0 );
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        JMenuBar jmenubar = new JMenuBar();
        jmenubar.add( new JMenu( "File Menu" ) );
        frame.setJMenuBar( jmenubar );
        frame.setContentPane( phetTabbedPane );
        frame.pack();
    }

}
