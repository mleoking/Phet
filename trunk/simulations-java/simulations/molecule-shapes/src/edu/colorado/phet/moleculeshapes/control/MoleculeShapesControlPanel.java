// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Images;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.jme.JmeActionListener;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.util.SimpleTarget;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * The main Molecule Shapes control panel on the right hand side. It is composed of multiple sub-panels,
 * namely "bonding", "lone pair", "options" and "real examples".
 */
public class MoleculeShapesControlPanel extends PNode {
    private static final double PANEL_SPACER = 20; // space between text and bond lines

    private final MoleculeShapesPanelNode realMoleculePanel;
    private RealMoleculePanelNode realMoleculeNode;

    public MoleculeShapesControlPanel( final MoleculeJMEApplication app, RealMoleculeOverlayNode overlayNode ) {

        /*---------------------------------------------------------------------------*
        * bonding panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode bondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new Spacer( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ) );

            final double spaceBetweenTypes = 8;

            final PNode singleNode = new BondTypeControlNode( app, new PImage( Images.SINGLE_BOND_SMALL ), 1 ) {{
                setOffset( 0, 10 );
            }};
            addChild( singleNode );
            final PNode doubleNode = new BondTypeControlNode( app, new PImage( Images.DOUBLE_BOND_SMALL ), 2 ) {{
                setOffset( 0, singleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
            }};
            addChild( doubleNode );
            final PNode tripleNode = new BondTypeControlNode( app, new PImage( Images.TRIPLE_BOND_SMALL ), 3 ) {{
                setOffset( 0, doubleNode.getFullBounds().getMaxY() + spaceBetweenTypes );
            }};
            addChild( tripleNode );
        }}, "Bonding" );
        addChild( bondingPanel );

        // put it on 0 vertically
        setOffset( 0, 10 );

        /*---------------------------------------------------------------------------*
        * lone pair panel
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode nonBondingPanel = new MoleculeShapesPanelNode( new PNode() {{
            // padding, and make sure we have the width
            addChild( new Spacer( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ) );

            /*---------------------------------------------------------------------------*
            * lone pair control
            *----------------------------------------------------------------------------*/
            final BondTypeControlNode lonePairNode = new BondTypeControlNode( app, new PImage( Images.LONE_PAIR_SMALL ), 0 ) {
                {
                    setOffset( 0, 10 );

                    // make sure to update our state when "show lone pairs" changes
                    MoleculeJMEApplication.showLonePairs.addObserver( JmeUtils.swingObserver( new Runnable() {
                        public void run() {
                            updateState();
                        }
                    } ), false );
                }

                @Override protected boolean isEnabled() {
                    // add the extra constraint on visibility
                    return super.isEnabled() && MoleculeJMEApplication.showLonePairs.get();
                }
            };
            addChild( lonePairNode );
        }}, "Lone Pair" ) {{
            setOffset( 0, bondingPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( nonBondingPanel );

        final PNode removeAllButtonNode = new TextButtonNode( Strings.CONTROL__REMOVE_ALL,
                                                              MoleculeShapesConstants.REMOVE_BUTTON_FONT,
                                                              MoleculeShapesConstants.REMOVE_BUTTON_BACKGROUND_COLOR.get() ) {{
            addActionListener( new JmeActionListener( new Runnable() {
                public void run() {
                    app.removeAllAtoms();
                }
            } ) );

            MoleculeShapesConstants.REMOVE_BUTTON_BACKGROUND_COLOR.addObserver(
                    new SimpleObserver() {
                        public void update() {
                            setBackground( MoleculeShapesConstants.REMOVE_BUTTON_BACKGROUND_COLOR.get() );
                            repaint();
                        }
                    }, false );

            MoleculeShapesConstants.REMOVE_BUTTON_TEXT_COLOR.addObserver( new SimpleObserver() {
                public void update() {
                    setEnabledTextColor( MoleculeShapesConstants.REMOVE_BUTTON_TEXT_COLOR.get() );
                    repaint(); // TODO: repaint shouldn't be necessary?
                }
            } );

            setOffset( ( nonBondingPanel.getFullBounds().getWidth() - getFullBounds().getWidth() ) / 2,
                       nonBondingPanel.getFullBounds().getMaxY() + PANEL_SPACER - 4 );
        }};
        addChild( removeAllButtonNode );

        /*---------------------------------------------------------------------------*
        * options
        *----------------------------------------------------------------------------*/
        final MoleculeShapesPanelNode optionsPanel = new MoleculeShapesPanelNode( new PNode() {{
            // enforce the width constraint
            addChild( new Spacer( 0, 0, MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH, 10 ) );

            PNode checkboxContainer = new PNode();

            /*---------------------------------------------------------------------------*
            * show lone pairs
            *----------------------------------------------------------------------------*/
            final PNode showLonePairsNode = new PropertyCheckBoxNode( Strings.CONTROL__SHOW_LONE_PAIRS, MoleculeJMEApplication.showLonePairs ) {{
                // enabled when there are lone pairs on the molecule
                Runnable updateEnabled = new Runnable() {
                    public void run() {
                        setEnabled( !app.getMolecule().getLonePairs().isEmpty() );
                    }
                };
                app.getMolecule().onGroupChanged.addTarget( JmeUtils.swingTarget( updateEnabled ) );

                /*
                 * Run this in the current thread. should be in EDT for construction. Needed since the other call
                 * is fired off to run in the next JME frame, so we have a flickering initial effect otherwise.
                 */
                updateEnabled.run();
            }};
            checkboxContainer.addChild( showLonePairsNode );

            /*---------------------------------------------------------------------------*
            * show bond angles
            *----------------------------------------------------------------------------*/
            checkboxContainer.addChild( new PropertyCheckBoxNode( Strings.CONTROL__SHOW_BOND_ANGLES, MoleculeShapesProperties.showBondAngles ) {{
                // enabled when there are 2 or more bonds (or always)
                Runnable updateEnabled = new Runnable() {
                    public void run() {
                        setEnabled( !MoleculeShapesProperties.disableNAShowBondAngles.get()
                                    || app.getMolecule().getBondedGroups().size() >= 2 );
                    }
                };
                app.getMolecule().onGroupChanged.addTarget( JmeUtils.swingTarget( updateEnabled ) );
                MoleculeShapesProperties.disableNAShowBondAngles.addObserver( JmeUtils.swingObserver( updateEnabled ) );

                setOffset( 0, showLonePairsNode.getFullBounds().getMaxY() );
            }} );

            checkboxContainer.setOffset( ( MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH - checkboxContainer.getFullBounds().getWidth() ) / 2, 0 );
            addChild( checkboxContainer );

        }}, "Options" ) {{
            setOffset( 0, removeAllButtonNode.getFullBounds().getMaxY() + PANEL_SPACER * 1.5 );
        }};
        addChild( optionsPanel );

        /*---------------------------------------------------------------------------*
        * real molecules panel
        *----------------------------------------------------------------------------*/
        final Property<Boolean> minimized = new Property<Boolean>( true ) {{
            // reset minimization on app reset
            app.resetNotifier.addTarget( new SimpleTarget() {
                public void update() {
                    reset();
                }
            } );
        }};

        realMoleculeNode = new RealMoleculePanelNode( app.getMolecule(), app, overlayNode, minimized );
        realMoleculePanel = new MoleculeShapesPanelNode( realMoleculeNode, new PNode() {{
            final PText title = new TitledControlPanelNode.TitleNode( Strings.REAL_EXAMPLES__TITLE );
            addChild( title );

            final double TEXT_PADDING = 4;
            addChild( new MinimizeMaximizeButtonNode( minimized ) {{
                setOffset( title.getWidth() + TEXT_PADDING, ( title.getFullBounds().getHeight() - getFullBounds().getHeight() ) / 2 + 1 );
            }} );
        }} ) {{
            setOffset( 0, optionsPanel.getFullBounds().getMaxY() + PANEL_SPACER );
        }};
        addChild( realMoleculePanel );
    }

    public boolean isOverlayVisible() {
        return realMoleculeNode.isOverlayVisible();
    }

    /**
     * @return Where to position the 3D molecule overlay
     */
    public PBounds getOverlayBounds() {
        return realMoleculeNode.getOverlayBounds();
    }

}
