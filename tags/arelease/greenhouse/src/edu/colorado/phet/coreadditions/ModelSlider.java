/**
 * Class: ModelSlider
 * Class: edu.colorado.phet.coreadditions
 * User: Ron LeMaster
 * Date: Nov 5, 2003
 * Time: 9:40:45 AM
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;

class ModelSlider extends JPanel {
        private JSlider slider;
        private ModelViewTx1D tx;

        ModelSlider( String title, double minModelValue, double maxModelValue, double defaultModelValue ) {
            this.setLayout( new GridBagLayout() );
            tx = new ModelViewTx1D( minModelValue, maxModelValue,
                                    0, 10000 );
            slider = new JSlider( SwingConstants.HORIZONTAL, 0,
                                  10000,
                                  (int)tx.modelToView( defaultModelValue ) );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double rate = tx.viewToModel( slider.getValue() );
                }
            } );

            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( title ), 0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, slider, 0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }

        double getModelValue() {
            return tx.viewToModel( slider.getValue() );
        }

        void addChangeListener( ChangeListener changeListener ) {
            slider.addChangeListener( changeListener );
        }

        void setModelValue( double value ) {
            slider.setValue( (int)tx.modelToView( value ) );
        }
    }

    class SliderWithReadout extends JPanel {
        private JTextField modelValueTF;
        private JSlider slider;
        private ModelViewTx1D tx;
        private String units;

        SliderWithReadout( String title, String units, double minModelValue, double maxModelValue, double defaultModelValue ) {
            this.units = units;
            this.setLayout( new GridBagLayout() );
            modelValueTF = new JTextField( 10 );
            setModelValueTF( defaultModelValue );
            tx = new ModelViewTx1D( minModelValue, maxModelValue,
                                    0, 100 );
            slider = new JSlider( SwingConstants.HORIZONTAL, 0,
                                  100,
                                  (int)tx.modelToView( defaultModelValue ) );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double rate = tx.viewToModel( slider.getValue() );
                    setModelValueTF( rate );
                }
            } );

            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( title ), 0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, slider, 0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, modelValueTF, 0, rowIdx++, 1, 1,
                                                  GridBagConstraints.NONE, GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }

        private void setModelValueTF( double value ) {
            modelValueTF.setText( "  " + Double.toString( value ) + " " + units );
        }

        double getModelValue() {
            return tx.viewToModel( slider.getValue() );
        }

        void addChangeListener( ChangeListener changeListener ) {
            slider.addChangeListener( changeListener );
        }
    }
