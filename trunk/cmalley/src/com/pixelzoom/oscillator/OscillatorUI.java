package com.pixelzoom.oscillator;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.sound.sampled.*;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * OscillatorUI
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OscillatorUI extends JFrame implements ActionListener, Runnable {

    private static final boolean DEBUG = true;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final float SAMPLE_RATE = 44100.0F;
    private static final float FRAME_RATE = SAMPLE_RATE;
    private static final  float FREQUENCY = 1000.0F;
    private static final float AMPLITUDE = 0.7F;
    private static final int BUFFER_SIZE = 128000;
    
    private static final String CHOICE_SINE = "sine";
    private static final String CHOICE_SQUARE = "square";
    private static final String CHOICE_TRIANGLE = "triangle";
    private static final String CHOICE_SAWTOOTH = "sawtooth";
    private static final String[] CHOICES = { CHOICE_SINE, CHOICE_SQUARE, CHOICE_TRIANGLE, CHOICE_SAWTOOTH };
   
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Oscillator _oscillator;
    private SourceDataLine _sourceDataLine;
    private JComboBox _waveformComboBox;
    private JCheckBox _soundCheckBox;
    private boolean _isPlaying;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public OscillatorUI() {
        super( "OscillatorUI" );
        initUI();
        initSound();
        setVisible( true );
    }
    
    private void initUI() {
        _waveformComboBox = new JComboBox( CHOICES );
        _waveformComboBox.setSelectedItem( CHOICE_SINE );
        _waveformComboBox.addActionListener( this );
        
        _soundCheckBox = new JCheckBox( "Sound" );
        _soundCheckBox.setSelected( false );
        _soundCheckBox.addActionListener( this );
        
        JPanel panel = new JPanel();
        panel.add( _waveformComboBox );
        panel.add( _soundCheckBox );
        getContentPane().add( panel );

        // Set the frame's size
        setSize( getPreferredSize().width + 20, getPreferredSize().height + 20 );

        // Center the frame on the screen.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = ( screenSize.width - getSize().width ) / 2;
        int y = ( screenSize.height - getSize().height ) / 2;
        setLocation( x, y );
        
        // Add a listener for closing the window.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                _isPlaying = false; // stops the sound thread
                System.exit( 0 );
            }
        });
    }
    
    private void initSound() {
        _isPlaying = false;
        // Set up the source data line.
        AudioFormat audioFormat = new AudioFormat( AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, 16, 2, 4, FRAME_RATE, false );
        _oscillator = new Oscillator( Oscillator.WAVEFORM_SINE, FREQUENCY, AMPLITUDE, audioFormat, AudioSystem.NOT_SPECIFIED );
        DataLine.Info info = new DataLine.Info( SourceDataLine.class, audioFormat );
        try {
            _sourceDataLine = (SourceDataLine) AudioSystem.getLine( info );
            _sourceDataLine.open( audioFormat );
        }
        catch ( LineUnavailableException e ) {
            e.printStackTrace();
        }
    }
    
    //----------------------------------------------------------------------------
    // ActionListener implementation
    //----------------------------------------------------------------------------
    
    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() == _waveformComboBox ) {
            handleWaveform();
        }
        else if ( event.getSource() == _soundCheckBox ) {
            handleSound();
        }
    }
    
    private void handleWaveform() {
        debug( _waveformComboBox.getSelectedItem().toString() );
        if ( _isPlaying ) {
            _sourceDataLine.stop();
            _sourceDataLine.flush();
        }
        String sWaveformType = (String) _waveformComboBox.getSelectedItem();
        int nWaveform = getWaveformType( sWaveformType );
        _oscillator.setWaveformType( nWaveform );
        if ( _isPlaying ) {
            _sourceDataLine.start();
        }
    }
    
    private int getWaveformType( String sWaveformType) {
        int nWaveform = Oscillator.WAVEFORM_SINE;
        if ( sWaveformType.equals( CHOICE_SINE ) ) {
            nWaveform = Oscillator.WAVEFORM_SINE;
        }
        else if ( sWaveformType.equals( CHOICE_SQUARE ) ) {
            nWaveform = Oscillator.WAVEFORM_SQUARE;
        }
        else if ( sWaveformType.equals( CHOICE_TRIANGLE ) ) {
            nWaveform = Oscillator.WAVEFORM_TRIANGLE;
        }
        else if ( sWaveformType.equals( CHOICE_SAWTOOTH ) ) {
            nWaveform = Oscillator.WAVEFORM_SAWTOOTH;
        }
        return nWaveform;
    }
    
    private void handleSound() {
        debug( ( _soundCheckBox.isSelected() ? "on" : "off" ) );
        if ( _soundCheckBox.isSelected() ) {
            _isPlaying = true;
            _sourceDataLine.start();
            Thread soundThread = new Thread( this );
            soundThread.start();
        }
        else {
            _isPlaying = false; // stops the sound thread
            _sourceDataLine.stop();
            _sourceDataLine.flush();
        }
    }

    //----------------------------------------------------------------------------
    // Runnable implementation
    //----------------------------------------------------------------------------
    
    public void run() {
        debug( "run" );
        byte[] buffer = new byte[BUFFER_SIZE];
        while ( _isPlaying ) {
            try {
                int nRead = _oscillator.read( buffer );
                int nWritten = _sourceDataLine.write( buffer, 0, nRead );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        debug( "run exiting" );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------
    
    public static void main( String[] args ) {
        OscillatorUI ui = new OscillatorUI();
    }
    
    public void debug( String message ) {
        if ( DEBUG ) {
            System.out.println( message );
        }
    }
}
