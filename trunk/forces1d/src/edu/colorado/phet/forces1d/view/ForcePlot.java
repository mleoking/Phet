/*PhET, 2004.*/
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.HorizontalCursor;
import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.forces1d.model.DataSeries;
import edu.colorado.phet.forces1d.model.MMTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:54:39 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class ForcePlot implements Graphic {
    private String title;
    private PlotConnection module;
    private DataSeries dataSeries;
    private MMTimer timer;
    private Color color;
    private Stroke stroke;
    private double xShift;

    private boolean visible = true;
    private Chart chart;
    private DataSet dataSet;
    private float lastTime;
    private Font axisFont = MMFontManager.getFontSet().getAxisFont();
    private Font titleFont = MMFontManager.getFontSet().getTitleFont();
    private Font readoutFont = MMFontManager.getFontSet().getReadoutFont();

    private VerticalChartSlider verticalChartSlider;
    private HorizontalCursor horizontalCursor;
    private GeneralPath path = new GeneralPath();
    private CloseButton closeButton;
    private ChartButton showButton;
    private MagButton magPlus;
    private MagButton magMinus;
    private TextBox textBox;
    private boolean cursorVisible;
    private PhetTextGraphic readout;
    private PhetTextGraphic readoutValue;

    private DecimalFormat format = new DecimalFormat( "0.00" );
    private double value;
    private FloatingControl floatingControl;
    private String units;
    private JLabel titleLable;
    private PhetTextGraphic superScriptGraphic;
    private Font verticalTitleFont = MMFontManager.getFontSet().getVerticalTitleFont();
    private ArrayList listeners = new ArrayList();

    public void valueChanged( double value ) {
        verticalChartSlider.setValue( value );
        setTextValue( value );
    }

    public void addSuperScript( String s ) {
        Font superScriptFont = new Font( "Lucida Sans", Font.BOLD, 12 );
        superScriptGraphic = new PhetTextGraphic( module.getApparatusPanel(), superScriptFont, s, color, 330, 230 );
        module.getApparatusPanel().addGraphic( superScriptGraphic, 999 );
    }

    public static interface Listener {
        void nominalValueChanged( double value );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    static class FloatingControl extends VerticalLayoutPanel {
        static BufferedImage play;
        static BufferedImage pause;
        private PlotConnection module;
//        private JLabel titleLabel;
        private JButton pauseButton;
        private JButton recordButton;
        private JButton resetButton;

        static {
            try {
                play = ImageLoader.loadBufferedImage( "images/icons/java/media/Play16.gif" );
                pause = ImageLoader.loadBufferedImage( "images/icons/java/media/Pause16.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        static class ControlButton extends JButton {
            static Font font = MMFontManager.getFontSet().getControlButtonFont();

            public ControlButton( String text ) {
                super( text );
                setFont( font );
            }
        }

        public FloatingControl( final PlotConnection module ) {
            this.module = module;
//            this.titleLabel = titleLabel;
            pauseButton = new ControlButton( "Pause" );
            pauseButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPaused( true );
                }
            } );
//            final JButton recordButton = new JButton( new ImageIcon( play ) );
            recordButton = new ControlButton( "Record" );
            recordButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setRecordMode();
                    module.setPaused( false );
                }
            } );

            resetButton = new ControlButton( "Reset" );
            resetButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    boolean paused = module.isPaused();
                    module.setPaused( true );
                    int option = JOptionPane.showConfirmDialog( module.getApparatusPanel(), "Are you sure you want to clear the graphs?", "Confirm Reset", JOptionPane.YES_NO_CANCEL_OPTION );
                    if( option == JOptionPane.OK_OPTION || option == JOptionPane.YES_OPTION ) {
                        module.reset();
                    }
                    else if( option == JOptionPane.CANCEL_OPTION || option == JOptionPane.NO_OPTION ) {
                        module.setPaused( paused );
                    }
                }
            } );
            module.addListener( new PlotConnection.ListenerAdapter() {
                public void recordingStarted() {
                    setButtons( false, true, true );
                }

                public void recordingPaused() {
                    setButtons( true, false, true );
                }

                public void recordingFinished() {
                    setButtons( false, false, true );
                }

                public void reset() {
                    setButtons( true, false, false );
                }

                public void rewind() {
                    setButtons( true, false, true );
                }
            } );
//            add( titleLabel );
            add( recordButton );
            add( pauseButton );
            add( resetButton );
            pauseButton.setEnabled( false );
        }

        private void setButtons( boolean record, boolean pause, boolean reset ) {
            recordButton.setEnabled( record );
            pauseButton.setEnabled( pause );
            resetButton.setEnabled( reset );
        }

        public void setVisible( boolean aFlag ) {
            super.setVisible( aFlag );
        }
    }

    public ForcePlot( ApparatusPanel panel,String title, final PlotConnection module, final DataSeries series, MMTimer timer, Color color, Stroke stroke, Rectangle2D.Double inputBox, double xShift, String units, String labelStr )
            throws IOException {
        this.units = units;
        this.title = title;
        this.module = module;
        this.dataSeries = series;
        this.timer = timer;
        this.color = color;
        this.stroke = stroke;
//        this.buffer = buffer;
        this.xShift = xShift;
        chart = new Chart( panel, new Range2D( inputBox ), new Rectangle( 0, 0, 100, 100 ) );
        horizontalCursor = new HorizontalCursor( chart, new Color( 15, 0, 255, 50 ), new Color( 50, 0, 255, 150 ), 8 );
        panel.addGraphic( horizontalCursor, 1000 );

        chart.setBackground( createBackground() );
        dataSet = new DataSet();
        setInputRange( inputBox );
//        timer.addObserver( this );
        timer.addListener( new MMTimer.Listener() {
            public void timeChanged() {
                update();
            }
        } );
        chart.getHorizontalTicks().setVisible( false );
        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.darkGray );
        chart.getVerticalGridlines().setMajorGridlinesColor( Color.darkGray );
        chart.getXAxis().setMajorTickFont( axisFont );
        chart.getYAxis().setMajorTicksVisible( false );
        chart.getYAxis().setMajorTickFont( axisFont );
        chart.getVerticalGridlines().setMinorGridlinesVisible( false );
        chart.getXAxis().setMajorGridlines( new double[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20} ); //to ignore the 0.0
        chart.getXAxis().setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{6, 6}, 0 ) );

        chart.setVerticalTitle( title, color, verticalTitleFont );

        verticalChartSlider = new VerticalChartSlider( chart );
        chart.getVerticalTicks().setMajorOffset( -verticalChartSlider.getSlider().getWidth() - 5, 0 );
        horizontalCursor.addListener( new HorizontalCursor.Listener() {
            public void modelValueChanged( double modelX ) {
                module.cursorMovedToTime( modelX );
            }
        } );
        closeButton = new CloseButton();
        closeButton.setToolTipText( "Close Graph" );
        panel.add( closeButton );

        setCloseHandler( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
                module.relayout();
            }
        } );
        showButton = new ChartButton( "Show " + title );
        showButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( true );
            }
        } );

        BufferedImage imgPlus = ImageLoader.loadBufferedImage( "images/icons/mag-plus-10.gif" );
        BufferedImage imgMinus = ImageLoader.loadBufferedImage( "images/icons/mag-minus-10.gif" );
        final double smooth = 1;
        ActionListener smoothPos = new Increment( smooth );
        ActionListener smoothNeg = new Decrement( smooth );
        ActionListener incPos = new Increment( 5 );
        ActionListener incNeg = new Decrement( 5 );
        magPlus = new MagButton( new ImageIcon( imgPlus ), smoothPos, incPos, "Zoom In" );
        magMinus = new MagButton( new ImageIcon( imgMinus ), smoothNeg, incNeg, "Zoom Out" );
        panel.add( magPlus );
        panel.add( magMinus );

        readout = new PhetTextGraphic( panel, readoutFont, title + " = ", color, 100, 100 );
        panel.addGraphic( readout, 10000 );
        readoutValue = new PhetTextGraphic( panel, readoutFont, units, color, 100, 100 );
        panel.addGraphic( readoutValue, 10000 );
        textBox = new TextBox( module, 5, labelStr );
        textBox.setHorizontalAlignment( JTextField.RIGHT );

        panel.add( textBox );

        setTextValue( 0 );
        module.getRecordingTimer().addListener( new MMTimer.Listener() {
            public void timeChanged() {
                updateTextBox( module, series );
            }
        } );
        module.getPlaybackTimer().addListener( new MMTimer.Listener() {
            public void timeChanged() {
                updateTextBox( module, series );
            }
        } );

        titleLable = new JLabel( title );
        titleLable.setFont( titleFont );
        titleLable.setBackground( module.getBackgroundColor() );
        titleLable.setOpaque( true );
        titleLable.setForeground( color );//TODO titleLabel

        panel.add( titleLable );
        floatingControl = new FloatingControl( module );//, titleLable );
        panel.add( floatingControl );
        module.addListener( new PlotConnection.ListenerAdapter() {
            public void rewind() {
                horizontalCursor.setX( 0 );
            }
        } );
    }

    private void updateTextBox( final PlotConnection module, final DataSeries series ) {
        int index = 0;
        if( module.isTakingData() ) {
            index = series.size() - 1;
        }
        else {
            double time = module.getPlaybackTimer().getTime() + getxShift();
            index = (int)time;//(int)( time / MovingManModel.TIMER_SCALE );
        }
        if( series.indexInBounds( index ) ) {
            value = series.pointAt( index );
            setTextValue( value );
        }
    }

    public void setTextValue( double value ) {
        String valueString = format.format( value );
        if( valueString.equals( "-0.00" ) ) {
            valueString = "0.00";
        }
        if( !textBox.getText().equals( valueString ) ) {
            textBox.setText( valueString );
        }
        readoutValue.setText( valueString + " " + units );
        moveScript();
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.nominalValueChanged( value );
        }
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public void requestTypingFocus() {
        textBox.requestFocusInWindow();
    }

    public static class TextBox extends JPanel {
        boolean changedByUser;
        JTextField textField;
        JLabel label;
        static Font font = MMFontManager.getFontSet().getTextBoxFont();
        private PlotConnection module;

        public TextBox( PlotConnection module, int text, String labelText ) {
            this.module = module;
            textField = new JTextField( text );
            label = new JLabel( labelText );
            setLayout( new FlowLayout( FlowLayout.CENTER ) );
            textField.addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent e ) {
                    if( isEnabled() ) {
                        textField.selectAll();
                    }
                }
            } );
            textField.addKeyListener( new KeyListener() {
                public void keyTyped( KeyEvent e ) {
                    changedByUser = true;
                }

                public void keyPressed( KeyEvent e ) {
                }

                public void keyReleased( KeyEvent e ) {
                }
            } );
            label.setFont( font );
            textField.setFont( font );
            add( label );
            add( textField );
            setBorder( BorderFactory.createLineBorder( Color.black ) );
            module.addListener( new PlotConnection.Listener() {
                public void recordingStarted() {
                    textField.setEditable( false );
                }

                public void recordingPaused() {
                    textField.setEditable( true );
                }

                public void recordingFinished() {
                    textField.setEditable( false );
                }

                public void playbackStarted() {
                    textField.setEditable( false );
                }

                public void playbackPaused() {
                    textField.setEditable( true );
                }

                public void playbackFinished() {
                    textField.setEditable( false );
                }

                public void reset() {
                    textField.setEditable( true );
                }

                public void rewind() {
                    textField.setEditable( true );
                }
            } );
        }

        public void clearChangedByUser() {
            changedByUser = false;
        }

        public boolean isChangedByUser() {
            return changedByUser;
        }

        public synchronized void addKeyListener( KeyListener l ) {
            textField.addKeyListener( l );
        }

        public void setEditable( boolean b ) {
            textField.setEditable( b );
        }

        public void setHorizontalAlignment( int right ) {
            textField.setHorizontalAlignment( right );
        }

        public String getText() {
            return textField.getText();
        }

        public void setText( String valueString ) {
            if( valueString.length() > textField.getColumns() ) {
                valueString = valueString.subSequence( 0, textField.getColumns() ) + "";
            }
            textField.setText( valueString );
        }
    }

    class Decrement implements ActionListener {
        double increment;

        public Decrement( double increment ) {
            this.increment = increment;
        }

        public void actionPerformed( ActionEvent e ) {
            Range2D origRange = chart.getRange();
            double diffY = origRange.getMaxY();
            double newDiffY = diffY + increment;
            int MAX = 100;
            if( newDiffY < MAX ) {
                setMagnitude( newDiffY );
                setPaintYLines( getYLines( newDiffY, 5 ) );
                module.repaintBackground();
            }
        }
    }

    class Increment implements ActionListener {
        double increment;

        public Increment( double increment ) {
            this.increment = increment;
        }

        public void actionPerformed( ActionEvent e ) {
            Range2D origRange = chart.getRange();
            double diffY = origRange.getMaxY();
            double newDiffY = diffY - increment;
            if( newDiffY > 0 ) {
                setMagnitude( newDiffY );
                setPaintYLines( getYLines( newDiffY, 5 ) );
                module.repaintBackground();
            }
        }

    }

    private double[] getYLines( double magnitude, double dy ) {
        ArrayList values = new ArrayList();
        for( double i = dy; i < magnitude; i += dy ) {
            values.add( new Double( i ) );
        }
        if( values.size() > 5 ) {
            return getYLines( magnitude, dy * 2 );
        }
        if( values.size() <= 1 ) {
            return getYLines( magnitude, dy / 2 );
        }
        double[] d = new double[values.size()];
        for( int i = 0; i < d.length; i++ ) {
            d[i] = ( (Double)values.get( i ) ).doubleValue();
        }
        return d;
    }

    static class RepeatClicker extends MouseAdapter {
        ActionListener target;
        private ActionListener discrete;
        int initDelay = 300;
        int delay = 30;
        Timer timer;
        private long pressTime;

        public RepeatClicker( ActionListener smooth, ActionListener discrete ) {
            this.target = smooth;
            this.discrete = discrete;
        }

        public void mouseClicked( MouseEvent e ) {
        }

        public void mousePressed( MouseEvent e ) {
            pressTime = System.currentTimeMillis();
            timer = new Timer( delay, target );
            timer.setInitialDelay( initDelay );
            timer.start();
        }

        public void mouseReleased( MouseEvent e ) {
            if( timer != null ) {
                timer.stop();
                long time = System.currentTimeMillis();
                if( time - pressTime < initDelay ) {
                    discrete.actionPerformed( null );
                }
            }
        }
    }

    class MagButton extends JButton {
        public MagButton( Icon icon, ActionListener smooth, ActionListener click, String tooltip ) {
            super( icon );
            addMouseListener( new RepeatClicker( smooth, click ) );
            setToolTipText( tooltip );
        }

    }

    public ChartButton getShowButton() {
        return showButton;
    }

    public static class ChartButton extends JButton {
        private static Font font = MMFontManager.getFontSet().getChartButtonFont();//new Font( "Lucida Sans", Font.BOLD, 14 );

        public ChartButton( String label ) throws IOException {
            super( label, new ImageIcon( ImageLoader.loadBufferedImage( "images/arrow-right.gif" ) ) );
            setFont( font );
            setVerticalTextPosition( AbstractButton.CENTER );
            setHorizontalTextPosition( AbstractButton.LEFT );
        }
    }

    private Paint createBackground() {
        return Color.yellow;
    }

    public void setCloseHandler( ActionListener actionListener ) {
        closeButton.addActionListener( actionListener );
    }

    public PlotConnection getModule() {
        return module;
    }

    public void reset() {
        path.reset();
        dataSet.clear();
        horizontalCursor.setMaxX( Double.POSITIVE_INFINITY );//so it can't be dragged past, hopefully.
        setTextValue( 0 );
        verticalChartSlider.setValue( 0 );
    }

    public void setViewBounds( int x, int y, int width, int height ) {
        setViewBounds( new Rectangle( x, y, width, height ) );
    }

    public boolean isVisible() {
        return visible;
    }

    private static class CloseButton extends JButton {
        private static Icon icon;

        public CloseButton() throws IOException {
            super( loadIcon() );
        }

        public static Icon loadIcon() throws IOException {
            if( icon == null ) {
                BufferedImage image = ImageLoader.loadBufferedImage( "images/x-25.gif" );
                icon = new ImageIcon( image );
            }
            return icon;
        }

        public void setPosition( int x, int y ) {
            reshape( x, y, getPreferredSize().width, getPreferredSize().height );
        }
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            GraphicsState state = new GraphicsState( g );
            chart.paint( g );
            Point pt = chart.getTransform().modelToView( 15, 0 );
            pt.y -= 3;
            PhetTextGraphic ptt = new PhetTextGraphic( module.getApparatusPanel(), MMFontManager.getFontSet().getTimeLabelFont(), "Time", Color.red, pt.x, pt.y );
            ptt.paint( g );
            Rectangle bounds = ptt.getBounds();
            Point2D tail = RectangleUtils.getRightCenter( bounds );
            tail = new Point2D.Double( tail.getX() + 5, tail.getY() );
            Point2D tip = new Point2D.Double( tail.getX() + 30, tail.getY() );
            Arrow arrow = new Arrow( tail, tip, 9, 9, 5 );
            PhetShapeGraphic psg = new PhetShapeGraphic( module.getApparatusPanel(), arrow.getShape(), Color.red, new BasicStroke( 1 ), Color.black );
            psg.paint( g );

            g.setClip( chart.getViewBounds() );
            g.setColor( color );
            g.setStroke( stroke );
            g.draw( path );

            state.restoreGraphics();
        }
    }

    public double getxShift() {
        return xShift;
    }

    public ModelViewTransform2D getTransform() {
        return chart.getTransform();
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
        setSliderVisible( visible );
        if( visible && cursorVisible ) {
            horizontalCursor.setVisible( true );
        }
        else {
            horizontalCursor.setVisible( false );
        }
        closeButton.setVisible( visible );
        module.getApparatusPanel().setLayout( null );
        module.getApparatusPanel().add( showButton );
        showButton.reshape( 100, 100, showButton.getPreferredSize().width, showButton.getPreferredSize().height );
        module.relayout();
        showButton.setVisible( !visible );
        magPlus.setVisible( visible );
        magMinus.setVisible( visible );

        readout.setVisible( visible );
        textBox.setVisible( visible );
        readoutValue.setVisible( visible );

        floatingControl.setVisible( visible );
        titleLable.setVisible( visible );
        if( superScriptGraphic != null ) {
            superScriptGraphic.setVisible( visible );
        }
    }

    public void setShift( double xShift ) {
        this.xShift = xShift;
    }

    public void setInputRange( Rectangle2D.Double inputBox ) {
        Range2D range = new Range2D( inputBox );
        chart.setRange( range );
        refitCurve();
        module.repaintBackground( chart.getViewBounds() );
    }

    private void refitCurve() {
        path.reset();
        Point2D.Double[] copy = dataSet.toArray();
        dataSet.clear();
        for( int i = 0; i < copy.length; i++ ) {
            Point2D.Double aDouble = copy[i];
            dataSet.addPoint( aDouble );
            drawSegment();
        }
    }

    public void setPaintYLines( double[] lines ) {
        double[] full = new double[lines.length * 2 + 1];
        for( int i = 0; i < lines.length; i++ ) {
            full[i] = lines[i];
            full[full.length - 1 - i] = -lines[i];
        }
        full[lines.length] = 0;

        double[] half = new double[lines.length * 2];
        for( int i = 0; i < lines.length; i++ ) {
            half[i] = lines[i];
            half[half.length - 1 - i] = -lines[i];
        }
        chart.getHorizonalGridlines().setMajorGridlines( half );
        chart.getVerticalTicks().setMajorGridlines( full );
        chart.getYAxis().setMajorGridlines( full );
    }

    public void setViewBounds( Rectangle rectangle ) {
        chart.setViewBounds( rectangle );
        chart.setBackground( createBackground() );
        verticalChartSlider.setOffsetX( chart.getVerticalTicks().getMajorTickTextBounds().width + chart.getTitle().getBounds().width );
        verticalChartSlider.update();
        chart.getVerticalTicks().setMajorOffset( 0, 0 );
        Rectangle vb = chart.getViewBounds();
        int x = vb.x + vb.width - closeButton.getPreferredSize().width;
        int y = vb.y;
        closeButton.setPosition( x - 2, y + 2 );

        Dimension buttonSize = magPlus.getPreferredSize();
        JSlider js = verticalChartSlider.getSlider();
        int magSep = 1;
        int magOffsetY = 7;
        int magY = js.getY() + js.getHeight() - 2 * buttonSize.height - magSep - magOffsetY;

        int magX = chart.getViewBounds().x + 3;
        magPlus.reshape( magX, magY, buttonSize.width, buttonSize.height );
        magMinus.reshape( magX, magY + magSep + buttonSize.height, buttonSize.width, buttonSize.height );

        readout.setPosition( chart.getViewBounds().x + 15, chart.getViewBounds().y + readout.getHeight() - 5 );
        readoutValue.setPosition( readout.getX() + readout.getWidth() + 5, readout.getY() );
        moveScript();

        int floaterX = 5;

        titleLable.reshape( floaterX, chart.getViewBounds().y, titleLable.getPreferredSize().width, titleLable.getPreferredSize().height );
        textBox.reshape( floaterX,
                         titleLable.getY() + titleLable.getHeight() + 5,
                         textBox.getPreferredSize().width,
                         textBox.getPreferredSize().height );
        int dw = Math.abs( textBox.getWidth() - floatingControl.getPreferredSize().width );
        int floatX = floaterX + dw / 2;
        floatingControl.reshape( floatX, textBox.getY() + textBox.getHeight() + 5, floatingControl.getPreferredSize().width, floatingControl.getPreferredSize().height );

        refitCurve();
    }

    private void moveScript() {
        if( superScriptGraphic != null ) {
            Rectangle b = readoutValue.getBounds();
            superScriptGraphic.setPosition( b.x + b.width, b.y + b.height / 2 );
        }
    }

    public void update() {
        float time = (float)timer.getTime();
        if( time == lastTime ) {
            return;
        }
        lastTime = time;
        if( dataSeries.size() <= 1 ) {
            dataSet.clear();
        }
        else {
            float position = (float)dataSeries.getLastPoint();// * scale + yoffset;
            if( Float.isInfinite( position ) ) {
                return;
            }
            Point2D.Double pt = new Point2D.Double( time - xShift, position );
            dataSet.addPoint( pt );
            horizontalCursor.setMaxX( time );//so it can't be dragged past the end of recorded pressTime.
            drawSegment();
        }
    }

    private void drawSegment() {
        if( visible && dataSet.size() >= 2 ) {
            int element = dataSet.size();
            Point2D a = chart.getTransform().modelToView( dataSet.pointAt( element - 2 ) );
            Point2D b = chart.getTransform().modelToView( dataSet.pointAt( element - 1 ) );
            Line2D.Double line = new Line2D.Double( a, b );
            if( dataSet.size() == 2 ) {
                path.reset();
                path.moveTo( (int)a.getX(), (float)a.getY() );
            }
            if( path.getCurrentPoint() != null ) {
                path.lineTo( (float)b.getX(), (float)b.getY() );
                Graphics2D g2 = module.getBackground().getImage().createGraphics();
                g2.setStroke( new BasicStroke( 2 ) );
                g2.setColor( color );
                g2.setClip( chart.getViewBounds() );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
                g2.draw( line );
                Shape shape = stroke.createStrokedShape( line );
                module.getApparatusPanel().repaint( shape.getBounds() );
            }
        }
    }

    public void setMagnitude( double magnitude ) {
        Rectangle2D.Double positionInputBox = new Rectangle2D.Double( module.getMinTime(), -magnitude, module.getMaxTime() - module.getMinTime(), magnitude * 2 );
        setInputRange( positionInputBox );
        module.repaintBackground( chart.getViewBounds() );
    }

    public void setSliderVisible( boolean b ) {
        verticalChartSlider.setVisible( b );
    }

    public void addSliderListener( VerticalChartSlider.Listener listener ) {
        verticalChartSlider.addListener( listener );
    }

    public VerticalChartSlider getVerticalChartSlider() {
        return verticalChartSlider;
    }

    public void updateSlider() {
        JSlider js = verticalChartSlider.getSlider();
        if( !js.getValueIsAdjusting() && dataSet.size() > 0 ) {
            double lastY = dataSet.getLastPoint().getY();
            verticalChartSlider.setValue( lastY );
        }
    }

    public void cursorMovedToTime( double time, int index ) {
        horizontalCursor.setX( time );
        verticalChartSlider.setValue( dataSeries.pointAt( index ) );
        setTextValue( dataSeries.pointAt( index ) );
    }

    public void setCursorVisible( boolean visible ) {
        if( isVisible() ) {
            horizontalCursor.setVisible( visible );
        }
        cursorVisible = visible;
    }
}
