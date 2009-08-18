/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.alphadecay;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.view.AlphaParticleNode;
import edu.colorado.phet.nuclearphysics.view.StandaloneNeutronNode;
import edu.colorado.phet.nuclearphysics.view.StandaloneProtonNode;
import edu.umd.cs.piccolo.PNode;


/**
 * This class displays the legend for the Alpha Decay tabs.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 *
 * @author John Blanco
 */
public class AlphaDecayLegendPanel extends JPanel {
        
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Amount to scale up the particle nodes to make them look reasonable.
    private static final double PARTICLE_SCALE_FACTOR = 8;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaDecayLegendPanel() {
        
        // Add the border around the legend.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysicsStrings.LEGEND_BORDER_LABEL,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridLayout(0, 2) );

        // Add the images and labels for the simple portion of the legend.
        
        PNode neutron = new StandaloneNeutronNode();
        neutron.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( neutron.toImage(), NuclearPhysicsStrings.NEUTRON_LEGEND_LABEL ); 
        PNode proton = new StandaloneProtonNode();
        proton.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( proton.toImage(), NuclearPhysicsStrings.PROTON_LEGEND_LABEL ); 
        PNode alphaParticle = new AlphaParticleNode();
        alphaParticle.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( alphaParticle.toImage(), NuclearPhysicsStrings.ALPHA_PARTICLE_LEGEND_LABEL );
    }
    
    /**
     * This method adds simple legend items, i.e. those that only include an
     * image and a label, to the legend.
     */
    private void addLegendItem( Image im, String label ) {
        ImageIcon icon = new ImageIcon(im);
        add(new JLabel(icon));
        add(new JLabel( label ));
    }
}
