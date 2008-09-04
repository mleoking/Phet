/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.module.intro;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.control.*;
import edu.colorado.phet.glaciers.model.GlaciersModel;
import edu.colorado.phet.glaciers.view.GlaciersPlayArea;

/**
 * Control panel for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.CONTROL_PANEL_BACKGROUND_COLOR;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ViewControlPanel _viewControlPanel;
    private final ClimateControlPanel _climateControlPanel;
    private final GlaciersClockControlPanel _clockControlPanel;
    private final MiscControlPanel _miscControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public IntroControlPanel( GlaciersModel model, GlaciersPlayArea playArea, Frame dialogOwner, Module module ) {
        super();
        
        _viewControlPanel = new ViewControlPanel( playArea, dialogOwner );
        _viewControlPanel.setCoordinatesCheckBoxVisible( false );
        _viewControlPanel.setIceFlowCheckBoxVisible( false );
        
        _climateControlPanel = new ClimateControlPanel( model.getClimate() );
        _clockControlPanel = new GlaciersClockControlPanel( model.getClock() );
        _miscControlPanel = new MiscControlPanel( model.getGlacier(), dialogOwner, module );
        
        int row;
        int column;
        
        JPanel topPanel = new JPanel();
        EasyGridBagLayout topLayout = new EasyGridBagLayout( topPanel );
        topPanel.setLayout( topLayout  );
        row = 0;
        column = 0;
        topLayout.addFilledComponent( _viewControlPanel, row, column++, GridBagConstraints.VERTICAL );
        topLayout.addFilledComponent( _climateControlPanel, row, column++, GridBagConstraints.VERTICAL  );
        
        JPanel bottomPanel = new JPanel();
        EasyGridBagLayout bottomLayout = new EasyGridBagLayout( bottomPanel );
        bottomPanel.setLayout( bottomLayout );
        row = 0;
        column = 0;
        bottomLayout.addAnchoredComponent( _clockControlPanel, row, column++, GridBagConstraints.WEST );
        bottomLayout.addComponent( Box.createHorizontalStrut( 10 ), row, column++ );
        bottomLayout.addFilledComponent( new JSeparator( SwingConstants.VERTICAL ), row, column++, GridBagConstraints.VERTICAL );
        bottomLayout.addComponent( Box.createHorizontalStrut( 10 ), row, column++ );
        bottomLayout.addAnchoredComponent( _miscControlPanel, row, column++, GridBagConstraints.EAST );
        
        JPanel p = new JPanel( new BorderLayout() );
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.add( topPanel );
        p.add( bottomPanel );
        add( p, BorderLayout.WEST );
        
        Class[] excludedClasses = { ViewControlPanel.class, ClimateControlPanel.class, GraphsControlPanel.class, JTextComponent.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public ViewControlPanel getViewControlPanel() {
        return _viewControlPanel;
    }
    
    public ClimateControlPanel getClimateControlPanel() {
        return _climateControlPanel;
    }
    
    public MiscControlPanel getMiscControlPanel() {
        return _miscControlPanel;
    }
    
    public void setHelpEnabled( boolean enabled ) {
        _miscControlPanel.setHelpEnabled( enabled );
    }
    
    public void activate() {
        _miscControlPanel.activate();
    }
    
    public void deactivate() {
        _miscControlPanel.deactivate();
    }
}
