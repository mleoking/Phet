/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.util.RangeLimitedIntegerTextField;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;

/**
 * ExperimentSetupPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ExperimentSetupPanel extends JPanel {
    private JTextField numATF;
    private JTextField numBCTF;
    private JTextField numABTF;
    private JTextField numCTF;
    private MoleculeParamGenerator moleculeParamGenerator;
    private MRModule module;


    public ExperimentSetupPanel( MRModule module ) {
        super( new GridBagLayout() );
        this.module = module;

        // Create a generator for molecule parameters
        Rectangle2D r = module.getMRModel().getBox().getBounds();
        Rectangle2D generatorBounds = new Rectangle2D.Double( r.getMinX() + 20,
                                                              r.getMinY() + 20,
                                                              r.getWidth() - 40,
                                                              r.getHeight() - 40 );
        moleculeParamGenerator = new RandomMoleculeParamGenerator( generatorBounds,
                                                                   5,
                                                                   .1,
                                                                   0,
                                                                   Math.PI * 2 );

        setBorder( ControlBorderFactory.createPrimaryBorder( "" ) );

        JLabel topLineLbl = new JLabel( SimStrings.get( "ExperimentSetup.topLine" ) );
        JLabel numALbl = new JLabel( SimStrings.get( "ExperimentSetup.numA" ) );
        JLabel numBCLbl = new JLabel( SimStrings.get( "ExperimentSetup.numBC" ) );
        JLabel numABLbl = new JLabel( SimStrings.get( "ExperimentSetup.numAB" ) );
        JLabel numCLbl = new JLabel( SimStrings.get( "ExperimentSetup.numC" ) );

        numATF = new RangeLimitedIntegerTextField( 0, 99 );
        numBCTF = new RangeLimitedIntegerTextField( 0, 99 );
        numABTF = new RangeLimitedIntegerTextField( 0, 99 );
        numCTF = new RangeLimitedIntegerTextField( 0, 99 );

        JButton goBtn = new GoStopResetBtn( module );

        // Lay out the controls
        GridBagConstraints labelGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.CENTER,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 2, 3, 3, 3 ),
                                                              0, 0 );
        GridBagConstraints textFieldGbc = new GridBagConstraints( 1, 1,
                                                                  1, 1, 1, 1,
                                                                  GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets( 2, 3, 3, 3 ),
                                                                  0, 0 );
        labelGbc.gridwidth = 2;
        add( topLineLbl, labelGbc );
        labelGbc.gridwidth = 1;
        labelGbc.anchor = GridBagConstraints.EAST;
        add( numALbl, labelGbc );
        add( numBCLbl, labelGbc );
        add( numABLbl, labelGbc );
        add( numCLbl, labelGbc );

        add( numATF, textFieldGbc );
        textFieldGbc.gridy = GridBagConstraints.RELATIVE;
        add( numBCTF, textFieldGbc );
        add( numABTF, textFieldGbc );
        add( numCTF, textFieldGbc );

        labelGbc.gridwidth = 2;
        labelGbc.anchor = GridBagConstraints.CENTER;
        add( goBtn, labelGbc );
    }

    private void initModel() {
        generateMolecules( MoleculeA.class, Integer.parseInt( numATF.getText() ));
        generateMolecules( MoleculeBC.class, Integer.parseInt( numBCTF.getText() ));
        generateMolecules( MoleculeAB.class, Integer.parseInt( numABTF.getText() ));
        generateMolecules( MoleculeC.class, Integer.parseInt( numCTF.getText() ));
    }

    private void generateMolecules( Class moleculeClass, int numMolecules ) {
        for( int i = 0; i < numMolecules; i++ ) {
            AbstractMolecule m = MoleculeFactory.createMolecule( moleculeClass,
                                                                 moleculeParamGenerator );
            if( m instanceof CompositeMolecule ) {
                CompositeMolecule cm = (CompositeMolecule)m;
                for( int j = 0; j < cm.getComponentMolecules().length; j++ ) {
                    module.getMRModel().addModelElement( cm.getComponentMolecules()[j] );
                }
            }
            module.getMRModel().addModelElement( m );
        }
    }

    private class GoStopResetBtn extends JButton {
        private Object go = new Object();
        private Object stop = new Object();
        private Object setup = new Object();
        private Object state = setup;
        private String goString = SimStrings.get( "ExperimentSetup.go" );
        private String stopString = SimStrings.get( "ExperimentSetup.stop" );
        private String setupString = SimStrings.get( "ExperimentSetup.setup" );
        private IClock clock;
        private MRModule module;

        public GoStopResetBtn( MRModule module ) {
            this.clock = module.getClock();
            this.module = module;
            state = setup;
            setText( getTextForState() );
            addActionListener( new ActionHandler() );
        }

        private String getTextForState() {
            String result = null;
            if( state == setup ) {
                result = setupString;
            }
            else if( state == go ) {
                result = goString;
            }
            else if( state == stop ) {
                result = stopString;
            }
            return result;
        }


        private class ActionHandler implements ActionListener {
            public void actionPerformed( ActionEvent e ) {
                if( state == go ) {
                    clock.start();
                    initModel();
                    state = stop;
                    setText( getTextForState() );
                }
                else if( state == stop ) {
                    clock.pause();
                    state = setup;
                    setText( getTextForState() );
                }
                else if( state == setup ) {
                    module.reset();
                    clock.pause();
                    state = go;
                    setText( getTextForState() );
                }
            }
        }
    }
}
