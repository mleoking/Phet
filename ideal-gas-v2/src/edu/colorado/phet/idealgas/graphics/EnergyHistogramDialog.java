/**
 * Class: EnergyHistogramDialog
 * Class: edu.colorado.phet.idealgas.graphics
 * User: Ron LeMaster
 * Date: Jan 19, 2004
 * Time: 9:19:30 AM
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.graphics.Histogram;
//import edu.colorado.phet.graphics.util.GraphicsUtil;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.IdealGasSystem;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.mechanics.Body;
//import edu.colorado.phet.model.body.Body;
//import edu.colorado.phet.model.body.Particle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

public class EnergyHistogramDialog extends JDialog {

    private Histogram energyHistogram;
    private IdealGasApplication application;
    // Number of energyHistogram updates between times it will be displayed and then
    // the data cleared
    private int averagingRatio = 4;
    // Bin count beyond which the energyHistogram will clip
    private int initialEnergyClippingLevel = 50;
    private int initialSpeedClippingLevel = 20;
    private Histogram speedHistogram;
    private Histogram heavySpeedHistogram;
    private Histogram lightSpeedHistogram;
    private boolean showDetails;
    private JButton detailsBtn;
    private JLabel lightSpeedLabel;
    private JLabel heavySpeedLabel;

    public EnergyHistogramDialog( IdealGasApplication application ) throws HeadlessException {
        super( application.getPhetFrame() );
        this.setTitle( "IdealGasParticle Statistics" );

        this.application = application;
        this.setResizable( false );

        // Create the histograms
        energyHistogram = new Histogram( 200, 150, 0, 100E3, 20, initialEnergyClippingLevel * averagingRatio, new Color( 0, 0, 0 ) );
        speedHistogram = new Histogram( 200, 150, 0, 70, 20, initialSpeedClippingLevel * averagingRatio, new Color( 0, 0, 0 ) );
        heavySpeedHistogram = new Histogram( 200, 150, 0, 70, 20, initialSpeedClippingLevel * averagingRatio, new Color( 20, 0, 200 ) );
        lightSpeedHistogram = new Histogram( 200, 150, 0, 70, 20, initialSpeedClippingLevel * averagingRatio, new Color( 200, 0, 20 ) );

        // Add a button for hiding/displaying the individual species
        detailsBtn = new JButton();
        detailsBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showDetails = !showDetails;
                heavySpeedHistogram.setVisible( showDetails );
                heavySpeedLabel.setVisible( showDetails );
                lightSpeedHistogram.setVisible( showDetails );
                lightSpeedLabel.setVisible( showDetails );

                EnergyHistogramDialog.this.pack();
                EnergyHistogramDialog.this.repaint();
            }
        } );
        this.layoutComponents();
        this.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE );

        heavySpeedHistogram.setVisible( showDetails );
        heavySpeedLabel.setVisible( showDetails );
        lightSpeedHistogram.setVisible( showDetails );
        lightSpeedLabel.setVisible( showDetails );

        this.pack();

        // Add a listener for the close event that gets rid of this dialog
        this.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent evt ) {
                JDialog dlg = (JDialog)evt.getSource();

                // Hide the frame
                dlg.setVisible( false );

                // If the frame is no longer needed, call dispose
                dlg.dispose();
            }
        } );

        // Create and start updaters for the histograms
        Updater updater = new Updater( application.getIdealGasSystem() );
        updater.addClient( new EnergyUpdaterClient( application.getIdealGasSystem(), energyHistogram ));
        updater.addClient( new SpeedUpdaterClient( speedHistogram ));
        updater.addClient( new SpeciesSpeedUpdaterClient( HeavySpecies.class, heavySpeedHistogram ));
        updater.addClient( new SpeciesSpeedUpdaterClient( LightSpecies.class, lightSpeedHistogram ));
        updater.start();
    }

    private void layoutComponents() {

        if( showDetails ) {
            detailsBtn.setText( "<< Fewer details" );
        }
        else {
            detailsBtn.setText( "More details >>" );
        }

        this.getContentPane().setLayout( new GridBagLayout() );
        try {
            int rowIdx = 0;
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              energyHistogram,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              new JLabel( "Energy Distribution" ),
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              speedHistogram,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              new JLabel( "Speed Distribution" ),
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              heavySpeedHistogram,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            heavySpeedLabel = new JLabel( "Heavy Speed Distribution" );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              heavySpeedLabel,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              lightSpeedHistogram,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            lightSpeedLabel = new JLabel( "Light Speed Distribution" );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              lightSpeedLabel,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( this.getContentPane(),
                                              detailsBtn,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        this.repaint();
    }

    public void paintComponents( Graphics g ) {
        super.paintComponents( g );
    }

    //
    // Static fields and methods
    //

    //
    // Inner classes
    //
    private class Updater extends Thread {
        private IdealGasSystem model;
        private ArrayList clients = new ArrayList();

        Updater( IdealGasSystem model ) {
            this.model = model;
        }

        void addClient( UpdaterClient client ) {
            clients.add( client );
        }

        public void run() {
            int cnt = 0;
            while( true ) {
                try {
//                    Thread.sleep( 500 );
                    Thread.sleep( 50000 );

                    // If the dialog isn't visible, don't go through the work of
                    // collecting the information
                    if( EnergyHistogramDialog.this.isVisible() ) {
                        // If we are at the first iteration of an averaging cycle, clear the data from the energyHistogram
                        // and compute the new clipping level
                        if( ( cnt % averagingRatio ) == 1 ) {
                            for( int i = 0; i < clients.size(); i++ ) {
                                UpdaterClient client = (UpdaterClient)clients.get( i );
                                client.clear();
                            }
                        }
                        List bodies = model.getBodies();
                        for( int i = 0; i < bodies.size(); i++ ) {
                            Body body = (Body)bodies.get( i );
                            if( body instanceof GasMolecule ) {
                                for( int j = 0; j < clients.size(); j++ ) {
                                    UpdaterClient client = (UpdaterClient)clients.get( j );
                                    client.recordParticle( body );
                                }
                            }
                        }

                        // Force2D a redraw
                        if( ( cnt++ % averagingRatio ) == 0 ) {
                            EnergyHistogramDialog.this.repaint();
                        }
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }

            }
        }

        protected IdealGasSystem getModel() {
            return model;
        }
    }


    private class EnergyUpdaterClient extends UpdaterClient {
        private IdealGasSystem model;
        EnergyUpdaterClient( IdealGasSystem model, Histogram histogram ) {
            super( histogram );
            this.model = model;
        }

        protected double getParticleAttribute( Particle particle ) {
            return model.getBodyEnergy( particle );
        }

        protected int getClippingLevel() {
            int cl = averagingRatio * Math.max( application.getIdealGasSystem().getBodies().size() / 3,
                                                initialEnergyClippingLevel );
            return cl;
        }
    }

    private abstract class UpdaterClient {
        Histogram histogram;

        UpdaterClient( Histogram histogram ) {
            this.histogram = histogram;
        }

         void clear() {
            histogram.clear();
            histogram.setClippingLevel( this.getClippingLevel() );
        }

        void recordParticle( Particle particle ) {
            histogram.add( getParticleAttribute( particle ) );
        }

        abstract int getClippingLevel();
        abstract double getParticleAttribute( Particle particle );
    }


    private class SpeedUpdaterClient extends UpdaterClient {
        SpeedUpdaterClient( Histogram histogram ) {
            super( histogram );
        }

        protected double getParticleAttribute( Particle particle ) {
            return particle.getSpeed();
        }

        protected int getClippingLevel() {
            int cl = averagingRatio * Math.max( application.getIdealGasSystem().getBodies().size() / 5,
                                                initialSpeedClippingLevel );
            return cl;
        }
    }

    private class SpeciesSpeedUpdaterClient extends SpeedUpdaterClient {
        private Class species;

        SpeciesSpeedUpdaterClient( Class species, Histogram histogram ) {
            super( histogram );
            this.species = species;
        }

        protected double getParticleAttribute( Particle particle ) {
            if( species.isInstance( particle ) ) {
                return super.getParticleAttribute( particle );
            }
            else {
                return -1;
            }
        }
    }
}
