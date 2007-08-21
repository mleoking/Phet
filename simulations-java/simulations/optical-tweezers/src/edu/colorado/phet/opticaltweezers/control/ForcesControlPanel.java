/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.view.DNAForceNode;
import edu.colorado.phet.opticaltweezers.view.FluidDragForceNode;
import edu.colorado.phet.opticaltweezers.view.TrapForceNode;
import edu.umd.cs.piccolo.PNode;

/**
 * ForcesControlPanel controls the view of forces.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ForcesControlPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Fluid _fluid;
    private TrapForceNode _trapForceNode;
    private FluidDragForceNode _dragForceNode;
    private DNAForceNode _dnaForceNode;
    
    private JCheckBox _trapForceCheckBox;
    private JCheckBox _dragForceCheckBox;
    private JCheckBox _brownianMotionCheckBox;
    private JCheckBox _dnaForceCheckBox;
    private JCheckBox _showValuesCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     * @param bead
     * @param fluid
     * @param trapForceNode
     * @param dragForceNode
     * @param dnaForceNode optional
     * @param showValuesControlIsVisible
     */
    public ForcesControlPanel( 
            Font titleFont, 
            Font controlFont, 
            Bead bead, 
            Fluid fluid,
            TrapForceNode trapForceNode, 
            FluidDragForceNode dragForceNode, 
            DNAForceNode dnaForceNode ) {
        super();
        
        _bead = bead;
        _bead.addObserver( this );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _trapForceNode = trapForceNode;
        _dragForceNode = dragForceNode;
        _dnaForceNode = dnaForceNode;
        
        JLabel titleLabel = new JLabel( OTResources.getString( "label.forcesOnBead" ) );
        titleLabel.setFont( titleFont );
        
        _trapForceCheckBox = new JCheckBox( OTResources.getString( "label.showTrapForce" ) );
        _trapForceCheckBox.setFont( controlFont );
        _trapForceCheckBox.addActionListener( new ActionListener() {
           public void actionPerformed( ActionEvent event ) {
               handleTrapForceCheckBox();
           }
        });

        _dragForceCheckBox = new JCheckBox( OTResources.getString( "label.showDragForce" ) );
        _dragForceCheckBox.setFont( controlFont );
        _dragForceCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleFluidDragCheckBox();
            }
         });
        
        if ( _dnaForceNode != null ) {
            _dnaForceCheckBox = new JCheckBox( OTResources.getString( "label.showDNAForce" ) );
            _dnaForceCheckBox.setFont( controlFont );
            _dnaForceCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleDNAForceCheckBox();
                }
            } );
        }
        
        _brownianMotionCheckBox = new JCheckBox( OTResources.getString( "label.enableBrownianMotion" ) );
        _brownianMotionCheckBox.setFont( controlFont );
        _brownianMotionCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleBrownianMotionCheckBox();
            }
         });
        
        _showValuesCheckBox = new JCheckBox( OTResources.getString( "label.showForceValues" ) );
        _showValuesCheckBox.setFont( controlFont );
        _showValuesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleShowValuesCheckBox();
            }
        } );
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column );
        layout.addComponent( _trapForceCheckBox, row++, column );
        layout.addComponent( _dragForceCheckBox, row++, column );
        if ( _dnaForceCheckBox != null ) {
            layout.addComponent( _dnaForceCheckBox, row++, column );
        }
        layout.addComponent( _brownianMotionCheckBox, row++, column );
        layout.addComponent( _showValuesCheckBox, row++, column );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _trapForceCheckBox.setSelected( false );
        _dragForceCheckBox.setSelected( false );
        _dragForceCheckBox.setEnabled( _fluid.isEnabled() );
        _brownianMotionCheckBox.setSelected( _bead.isBrownianMotionEnabled() );
        _brownianMotionCheckBox.setEnabled( _fluid.isEnabled() );
        if ( _dnaForceCheckBox != null ) {
            _dnaForceCheckBox.setSelected( false );
        }
    }
    
    public void cleanup() {
        _bead.deleteObserver( this );
        _fluid.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setTrapForceSelected( boolean b ) {
        _trapForceCheckBox.setSelected( b );
        handleTrapForceCheckBox();
    }
    
    public boolean isTrapForceSelected() {
        return _trapForceCheckBox.isSelected();
    }
    
    public void setDragForceSelected( boolean b ) {
        _dragForceCheckBox.setSelected( b );
        handleFluidDragCheckBox();
    }
    
    public boolean isDragForceSelected() {
        return _dragForceCheckBox.isSelected();
    }
   
    public void setBrownianMotionSelected( boolean b ) {
        _brownianMotionCheckBox.setSelected( b );
        handleBrownianMotionCheckBox();
    }
    
    public boolean isBrownianMotionSelected() {
        return _brownianMotionCheckBox.isSelected();
    }
    
    public void setDNAForceSelected( boolean b ) {
        if ( _dnaForceCheckBox != null ) {
            _dnaForceCheckBox.setSelected( b );
            handleDNAForceCheckBox();
        }
    }
    
    public boolean isDNAForceSelected() { 
        boolean selected = false;
        if ( _dnaForceCheckBox != null ) {
            selected = _dnaForceCheckBox.isSelected();
        }
        return selected;
    }
    
    public void setShowValuesSelected( boolean b ) {
        _showValuesCheckBox.setSelected( b );
        handleShowValuesCheckBox();
    }
    
    public boolean isShowValuesSelected() {
        return _showValuesCheckBox.isSelected();
    }
    
    /**
     * Sets visibility of the checkbox that controls Brownian motion.
     * This feature is not visible in for some control panels.
     * 
     * @param visible
     */
    public void setBrownianMotionCheckBoxVisible( boolean visible ) {
        _brownianMotionCheckBox.setVisible( visible );
    }
    
    /**
     * Sets visibility of the "Show values" checkbox.
     * This feature is not visible in for some control panels.
     * 
     * @param visible
     */
    public void setShowValuesCheckBoxVisible( boolean visible ) {
        _showValuesCheckBox.setVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleTrapForceCheckBox() {
        final boolean selected = _trapForceCheckBox.isSelected();
        _trapForceNode.setVisible( selected );
    }
    
    private void handleFluidDragCheckBox() {
        final boolean selected = _dragForceCheckBox.isSelected();
        _dragForceNode.setVisible( selected );
    }
    
    private void handleBrownianMotionCheckBox() {
        final boolean selected = _brownianMotionCheckBox.isSelected();
        _bead.setBrownianMotionEnabled( selected );
    }
    
    private void handleDNAForceCheckBox() {
        final boolean selected = _dnaForceCheckBox.isSelected();
        _dnaForceNode.setVisible( selected );
    }
    
    private void handleShowValuesCheckBox() {
        final boolean visible = _showValuesCheckBox.isSelected();
        _trapForceNode.setValuesVisible( visible );
        _dragForceNode.setValuesVisible( visible );
        if ( _dnaForceNode != null ) {
            _dnaForceNode.setValuesVisible( visible );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _fluid ) {
            if ( arg == Fluid.PROPERTY_ENABLED ) {
                _brownianMotionCheckBox.setEnabled( _fluid.isEnabled() );
                _dragForceCheckBox.setEnabled( _fluid.isEnabled() );
                _dragForceNode.setVisible( _fluid.isEnabled() && _dragForceCheckBox.isSelected() );
            }
        }
        else if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_BROWNIAN_MOTION_ENABLED ) {
                _brownianMotionCheckBox.setSelected( _bead.isBrownianMotionEnabled() );
            }
        }
    }
}
