// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.BackButton;
import edu.colorado.phet.common.piccolophet.nodes.kit.ForwardButton;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.colorado.phet.moleculeshapes.util.Fireable;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.colorado.phet.moleculeshapes.view.MoleculeNode;
import edu.colorado.phet.moleculeshapes.view.MoleculeNode.DisplayMode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Displays a 3D view for molecules that are "real" versions of the currently visible VSEPR model
 */
public class RealMoleculePanelNode extends PNode {

    private final MoleculeModel molecule;
    private final MoleculeJMEApplication app;
    private final Property<Boolean> minimized;
    private final double SIZE = MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH;
    private final double CONTROL_OFFSET = 40;
    private final double ARROW_Y_OFFSET = 5;
    private PhetPPath overlayTarget;

    private PNode containerNode = new PNode();

    private int kitIndex = 0;
    private Property<RealMolecule> selectedMolecule = new Property<RealMolecule>( null );
    private List<RealMolecule> molecules = new ArrayList<RealMolecule>();

    public RealMoleculePanelNode( MoleculeModel molecule, final MoleculeJMEApplication app, final RealMoleculeOverlayNode overlayNode,
                                  final Property<Boolean> minimized ) {
        this.molecule = molecule;
        this.app = app;
        this.minimized = minimized;

        minimized.addObserver( new SimpleObserver() {
            public void update() {
                if ( minimized.get() && containerNode.getParent() != null ) {
                    removeChild( containerNode );
                }
                if ( !minimized.get() && containerNode.getParent() == null ) {
                    addChild( containerNode );
                }
            }
        } );

        // make sure we have something at the very top so the panel doesn't shrink in
        addChild( new Spacer( 0, 0, SIZE, 10 ) );

        /*---------------------------------------------------------------------------*
        * back button
        *----------------------------------------------------------------------------*/
        containerNode.addChild( new BackButton() {{
            selectedMolecule.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( kitIndex > 0 && !molecules.isEmpty() );
                }
            } );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    kitIndex--;
                    synchronized ( app ) {
                        selectedMolecule.set( molecules.get( kitIndex ) );
                    }
                }
            } );
            setOffset( 0, ARROW_Y_OFFSET );
        }} );

        /*---------------------------------------------------------------------------*
        * forward button
        *----------------------------------------------------------------------------*/
        containerNode.addChild( new ForwardButton() {{
            selectedMolecule.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( kitIndex < molecules.size() - 1 && !molecules.isEmpty() );
                }
            } );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    kitIndex++;
                    synchronized ( app ) {
                        selectedMolecule.set( molecules.get( kitIndex ) );
                    }
                }
            } );
            setOffset( SIZE - getFullBounds().getWidth(), ARROW_Y_OFFSET );
        }} );

        /*---------------------------------------------------------------------------*
        * molecular formula label
        *----------------------------------------------------------------------------*/
        containerNode.addChild( new HTMLNode( "", MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR, new PhetFont( 14, true ) ) {{
            selectedMolecule.addObserver( new SimpleObserver() {
                public void update() {
                    synchronized ( app ) {
                        if ( selectedMolecule.get() != null ) {
                            setHTML( ChemUtils.toIonSuperscript( ChemUtils.toSubscript( selectedMolecule.get().getDisplayName() ) ) );
                        }
                        else {
                            setHTML( "(none)" );
                        }

                        // center vertically and horizontally
                        setOffset( ( SIZE - getFullBounds().getWidth() ) / 2, ( CONTROL_OFFSET - getFullBounds().getHeight() ) / 2 );

                        // if it goes past 0, push it down
                        if ( getFullBounds().getMinY() < 0 ) {
                            setOffset( getOffset().getX(), getOffset().getY() - getFullBounds().getMinY() );
                        }

                        repaint();
                    }
                }
            } );
        }} );

        /*---------------------------------------------------------------------------*
        * overlay target
        *----------------------------------------------------------------------------*/
        final float overlayBorderWidth = 1;
        overlayTarget = new PhetPPath( new Rectangle2D.Double( 0, 0, SIZE - overlayBorderWidth, SIZE - overlayBorderWidth ), new Color( 0f, 0f, 0f, 0f ) ) {{
            setStroke( new BasicStroke( overlayBorderWidth ) );
            setStrokePaint( new Color( 60, 60, 60 ) );

            // make room for the buttons and labels above
            setOffset( 0, CONTROL_OFFSET );

            // if the user presses the mouse here, start dragging the molecule
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    app.startOverlayMoleculeDrag();
                }
            } );
        }};
        containerNode.addChild( overlayTarget );

        /*---------------------------------------------------------------------------*
        * display type selection
        *----------------------------------------------------------------------------*/
        final ButtonGroup group = new ButtonGroup();
        final PSwing ballAndStickPSwing = new PSwing( new JRadioButton( "Ball and Stick", false ) {{
            group.add( this );
            setFont( MoleculeShapesConstants.CHECKBOX_FONT_SIZE );
            setForeground( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
            setOpaque( false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    overlayNode.displayMode.set( DisplayMode.BALL_AND_STICK );
                }
            } );

            // keep this checkbox up-to-date with the property
            overlayNode.displayMode.addObserver( new SimpleObserver() {
                public void update() {
                    boolean shouldBeSelected = overlayNode.displayMode.get() == DisplayMode.BALL_AND_STICK;
                    if ( isSelected() != shouldBeSelected ) {
                        setSelected( shouldBeSelected );
                    }
                }
            } );
        }} ) {{
            setOffset( 0, SIZE + CONTROL_OFFSET );
        }};
        containerNode.addChild( ballAndStickPSwing );
        final PSwing spaceFillPSwing = new PSwing( new JRadioButton( "Space Filling", true ) {{
            group.add( this );
            setFont( MoleculeShapesConstants.CHECKBOX_FONT_SIZE );
            setForeground( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
            setOpaque( false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    overlayNode.displayMode.set( MoleculeNode.DisplayMode.SPACE_FILL );
                }
            } );
        }} ) {{
            setOffset( 0, ballAndStickPSwing.getFullBounds().getMaxY() );
        }};
        containerNode.addChild( spaceFillPSwing );

        onModelChange();

        // when the VSEPR molecule changes, update our possible molecules
        molecule.onGroupChanged.addTarget( new Fireable<PairGroup>() {
            public void fire( PairGroup param ) {
                onModelChange();
            }
        } );

        // update the overlay (3D molecule view) when our selected molecule changes
        selectedMolecule.addObserver( new SimpleObserver() {
            public void update() {
                overlayNode.showMolecule( selectedMolecule.get() );
            }
        } );
    }

    public boolean isOverlayVisible() {
        return !minimized.get();
    }

    public PBounds getOverlayBounds() {
        return overlayTarget.getGlobalFullBounds();
    }

    private void onModelChange() {
        synchronized ( app ) {
            // get the list of real molecules that correspond to our VSEPR model
            molecules = RealMolecule.getMatchingMolecules( molecule );
            kitIndex = 0;

            boolean showingMolecule = !molecules.isEmpty();

            if ( showingMolecule ) {
                selectedMolecule.set( molecules.get( 0 ) );
            }
            else {
                selectedMolecule.set( null );
            }

            // TODO: allow the collapse-on-no-model changes
//            if ( getChildrenReference().contains( overlayTarget ) != showingMolecule ) {
//                if ( showingMolecule ) {
//                    containerNode.addChild( overlayTarget );
//                }
//                else {
//                    containerNode.removeChild( overlayTarget );
//                }
//            }

            repaint();
        }
    }

    private static <A, B> List<B> map( List<A> list, Function1<A, B> map ) {
        // TODO: move to somewhere more convenient
        List<B> result = new ArrayList<B>();
        for ( A element : list ) {
            result.add( map.apply( element ) );
        }
        return result;
    }

    private PText labelText( String str ) {
        return new PText( str ) {{
            setFont( new PhetFont( 14 ) );
            setTextPaint( MoleculeShapesConstants.CONTROL_PANEL_BORDER_COLOR );
        }};
    }
}
