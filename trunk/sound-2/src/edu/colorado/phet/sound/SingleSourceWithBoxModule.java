/**
 * Class: SingleSourceWithBoxModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 19, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.sound.model.AttenuationFunction;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.view.SoundControlPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class SingleSourceWithBoxModule extends SingleSourceListenModule {
    private AirBoxGraphic boxInteriorGraphic;

    protected SingleSourceWithBoxModule( ApplicationModel appModel ) {
        super( appModel, "<html>Listen with<br>Varying Air Pressure</html>" );
        init();
    }

    private void init() {
        SoundModel soundModel = (SoundModel)getModel();
        WaveMedium waveMedium = soundModel.getWaveMedium();

        Rectangle2D.Double region = new Rectangle2D.Double( SoundConfig.s_speakerBaseX - 50,
                                                            SoundConfig.s_speakerBaseY - 100,
                                                            200, 350 );
        VariableWaveMediumAttenuationFunction attenuationFunction = new VariableWaveMediumAttenuationFunction();
        attenuationFunction.setVariableRegion( region );
        waveMedium.setAttenuationFunction( attenuationFunction );
        PhetShapeGraphic boxGraphic = new PhetShapeGraphic( getApparatusPanel(), region, new BasicStroke( 4f ), new Color( 200, 200, 120 ) );
        addGraphic( boxGraphic, 8 );
        boxInteriorGraphic = new AirBoxGraphic( getApparatusPanel(), region );
        addGraphic( boxInteriorGraphic, 6 );

        SoundControlPanel controlPanel = (SoundControlPanel)getControlPanel();
        controlPanel.addPanel( new BoxAirDensityControlPanel( attenuationFunction ) );
    }

    public int rgbAt( int x, int y ) {
        if( boxInteriorGraphic.contains( x, y ) ){
            System.out.println( "!!!" );            
            return boxInteriorGraphic.getGrayLevel();
        }
        else {
            return super.rgbAt( x, y );
        }
    }

    static class AirBoxGraphic extends PhetShapeGraphic {
        static Color[] grayLevels = new Color[256];
        private int grayLevel;

        static {
            for( int i = 0; i < 256; i++ ) {
                grayLevels[i] = new Color( i, i, i );
            }
        }

        AirBoxGraphic( Component component, Shape shape ) {
            super( component, shape, new Color( 128, 128, 128 ), new BasicStroke( 4f ), new Color( 200, 200, 100 ) );
        }

        void setAirDensity( double density ) {
            grayLevel = 255 - (int)( 128 * density );
            this.setPaint( grayLevels[grayLevel] );
        }

        int getGrayLevel() {
            return grayLevel;
        }
    }

    class BoxAirDensityControlPanel extends JPanel {
        VariableWaveMediumAttenuationFunction attenuationFunction;

        public BoxAirDensityControlPanel( final VariableWaveMediumAttenuationFunction attenuationFunction ) {
            this.attenuationFunction = attenuationFunction;
            final int maxValue = 100;
            final JSlider densitySlider = new JSlider( JSlider.HORIZONTAL, 0, maxValue, maxValue );
            densitySlider.setSnapToTicks( true );
            densitySlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double airDensity = ( (double)densitySlider.getValue() ) / maxValue;
                    attenuationFunction.setVariableRegionAttenuation( airDensity );
                    boxInteriorGraphic.setAirDensity( airDensity );
                }
            } );
            this.setLayout( new GridLayout( 2, 1 ) );
            this.setBorder( new TitledBorder( "Air Density" ) );
            this.setPreferredSize( new Dimension( 120, 80 ) );
            this.add( densitySlider );
        }
    }

    class VariableWaveMediumAttenuationFunction implements AttenuationFunction {
        private Shape variableRegion;
        private double variableRegionAttenuation = 1;

        public void setVariableRegion( Shape region ) {
            this.variableRegion = region;
        }

        public void setVariableRegionAttenuation( double variableRegionAttenuation ) {
            this.variableRegionAttenuation = Math.sin( variableRegionAttenuation * Math.PI / 2 );
        }

        public double getAttenuation( double x, double y ) {
            if( variableRegion != null && variableRegion.contains( x + SoundConfig.s_speakerBaseX, y + 201 ) ) {
                return variableRegionAttenuation;
            }
            else {
                return 1;
            }
        }
    }
}
