/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.view.*;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;

/**
 * EnergyView
 * <p/>
 * A view of the MRModel that shows the potential energy of two individual molecules,
 * or their composite molecule. This is a fairly abstract view.
 * <p/>
 * The diagram below shows the basic layout of this view, with the names of fields
 * corresponding to its main elements
 * <p/>
 * -------------------------------------------
 * |                                         |
 * |                                         |
 * |           upperPane                     |
 * |                                         |
 * |                                         |
 * -------------------------------------------
 * |          curvePane                      |
 * |  .....................................  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .       curveArea                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .                                   .  |
 * |  .....................................  |
 * |  .       legendPane                  .  |
 * |  .....................................  |
 * -------------------------------------------
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyView extends PNode implements SimpleObserver, Resetable {

    private int width = 300;
    private Dimension upperPaneSize = new Dimension( width, 150 );
    private Dimension curvePaneSize = new Dimension( width, 310 );
    private Color moleculePaneBackgroundColor = MRConfig.MOLECULE_PANE_BACKGROUND;
    private Color energyPaneBackgroundColor = Color.black;
    private Color curveColor = Color.cyan;

    private SimpleMolecule selectedMolecule;
    private SimpleMolecule nearestToSelectedMolecule;
    private EnergyMoleculeGraphic selectedMoleculeGraphic;
    private EnergyMoleculeGraphic nearestToSelectedMoleculeGraphic;
    private MyBondGraphic bondGraphic;

    private EnergyCursor cursor;
    private Insets curveAreaInsets = new Insets( 20, 30, 40, 10 );
    private Dimension curveAreaSize;
    private Font labelFont;
    private PNode moleculePaneAxisNode;
    private PNode moleculeLayer;
    private SeparationIndicatorArrow separationIndicatorArrow;
    private MRModule module;
    private PPath curvePane;
    private TotalEnergyLine totalEnergyLine;
    private PPath upperPane;
    private PPath moleculePane;
    private EnergyProfileGraphic energyProfileGraphic;
    private PNode curvePaneLegend;

    /**
     *
     */
    public EnergyView( MRModule module ) {
        this.module = module;
        MRModel model = module.getMRModel();

        // Set up the font for labels
        Font defaultFont = UIManager.getFont( "Label.font" );
        labelFont = new Font( defaultFont.getName(), Font.BOLD, defaultFont.getSize() + 1 );

        // The pane that has the molecules
        moleculePane = createMoleculePane();
        addChild( moleculePane );

        // Add another pane on top of the molecule pane to display charts.
        // It's a reall hack, but this pane is made visible when another
        upperPane = new PPath( new Rectangle2D.Double( 0, 0,
                                                       upperPaneSize.getWidth(),
                                                       upperPaneSize.getHeight() ) );
        upperPane.setWidth( upperPaneSize.getWidth() );
        upperPane.setHeight( upperPaneSize.getHeight() );
        upperPane.setPaint( moleculePaneBackgroundColor );
        upperPane.setStroke( null );
        upperPane.setVisible( false );
        addChild( upperPane );

        // The graphic that shows the reaction mechanics
        PPath legendNode = new PPath( new Rectangle2D.Double( 0, 0, width, 40 ) );
        legendNode.setPaint( Color.black );
        legendNode.setOffset( 0, upperPaneSize.getHeight() + curvePaneSize.getHeight() );
        ReactionGraphic reactionGraphic = new ReactionGraphic( model.getReaction(), Color.white, module.getMRModel() );
        legendNode.addChild( reactionGraphic );
        reactionGraphic.setOffset( legendNode.getWidth() / 2, legendNode.getHeight() - 20 );
        addChild( legendNode );

        // The pane that has the curve and cursor
        curvePane = createCurvePane( moleculePane, model );
        addChild( curvePane );

        // Listen for changes in the selected molecule and the molecule closest to it
        SelectedMoleculeListener selectedMoleculeListener = new SelectedMoleculeListener();
        model.addSelectedMoleculeTrackerListener( selectedMoleculeListener );
        model.addListener( selectedMoleculeListener );
        update();
    }

    /**
     *
     */
    public void reset() {

        selectedMolecule = null;
        nearestToSelectedMolecule = null;

        if( selectedMoleculeGraphic != null ) {
            moleculeLayer.removeChild( selectedMoleculeGraphic );
        }
        if( nearestToSelectedMoleculeGraphic != null ) {
            moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
        }
        selectedMoleculeGraphic = null;
        nearestToSelectedMoleculeGraphic = null;

        // Listen for changes in the selected molecule and the molecule closest to it
        module.getMRModel().addSelectedMoleculeTrackerListener( new SelectedMoleculeListener() );
    }

    /**
     *
     */
    public Dimension getUpperPaneSize() {
        return upperPaneSize;
    }

    /**
     * Adds a pNode to the upper pane
     *
     * @param pNode
     */
    public void addToUpperPane( PNode pNode ) {
        upperPane.addChild( pNode );
        upperPane.setVisible( true );
    }

    /**
     * Removes a pNode from the upper pane
     *
     * @param pNode
     */
    public void removeFromUpperPane( PNode pNode ) {
        if( upperPane.getChildrenReference().contains( pNode ) ) {
            upperPane.removeChild( pNode );
            upperPane.setVisible( false );
        }
    }

    /**
     * Sets the visibility of the legend for the profile curve
     *
     * @param visible
     */
    public void setProfileLegendVisible( boolean visible ) {
        curvePaneLegend.setVisible( visible );
    }

    /**
     * Sets the visibility of the total energy line
     *
     * @param visible
     */
    public void setTotalEnergyLineVisible( boolean visible ) {
        totalEnergyLine.setVisible( visible );
    }

    /**
     * Creates the pane that has the energy curve, cursor, and the total energy line
     *
     * @param moleculePane
     * @return a PNode
     */
    private PPath createCurvePane( PPath moleculePane, final MRModel model ) {
        final PNode curveLayer = new PNode();
        curveLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );
        PNode cursorLayer = new PNode();
        cursorLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );
        PPath curvePane = new PPath( new Rectangle2D.Double( 0,
                                                             0,
                                                             curvePaneSize.getWidth(),
                                                             curvePaneSize.getHeight() ) );
        curvePane.setOffset( 0, moleculePane.getHeight() );
        curvePane.setPaint( energyPaneBackgroundColor );
        curvePane.addChild( curveLayer );
        curvePane.addChild( cursorLayer );

        // Create the curve, and add a listener to the model that will update the curve if the
        // model's energy profile changes
        curveAreaSize = new Dimension( (int)curvePaneSize.getWidth() - curveAreaInsets.left - curveAreaInsets.right,
                                       (int)curvePaneSize.getHeight() - curveAreaInsets.top - curveAreaInsets.bottom );
        createCurve( model, curveLayer );
        model.addListener( new MRModel.ModelListener() {
            public void energyProfileChanged( EnergyProfile profile ) {
                createCurve( model, curveLayer );
            }
        } );

        // Create the line that shows total energy
        this.totalEnergyLine = new TotalEnergyLine( curveAreaSize, model, module.getClock() );
        curveLayer.addChild( this.totalEnergyLine );

        // Create the cursor
        cursor = new EnergyCursor( curveAreaSize.getHeight(), 0, curveAreaSize.getWidth(), model );
        cursor.setVisible( false );
        cursorLayer.addChild( cursor );

        // Add axes
        RegisterablePNode xAxis = new RegisterablePNode( new AxisNode( SimStrings.get( "EnergyView.ReactionCoordinate" ),
                                                                       200,
                                                                       Color.white,
                                                                       AxisNode.HORIZONTAL,
                                                                       AxisNode.BOTTOM ) );
        xAxis.setRegistrationPoint( xAxis.getFullBounds().getWidth() / 2, 0 );
        xAxis.setOffset( curvePane.getFullBounds().getWidth() / 2 + curveAreaInsets.left / 2,
                         curvePane.getHeight() - 25 );
        curvePane.addChild( xAxis );

        RegisterablePNode yAxis = new RegisterablePNode( new AxisNode( "Energy", 200, Color.white, AxisNode.VERTICAL, AxisNode.TOP ) );
        yAxis.setRegistrationPoint( yAxis.getFullBounds().getWidth() / 2,
                                    -yAxis.getFullBounds().getHeight() / 2 );
        yAxis.setOffset( curveAreaInsets.left / 2, curvePane.getFullBounds().getHeight() / 2 );
        curvePane.addChild( yAxis );

        // Add a curvePaneLegend
        curvePaneLegend = new PNode();
        Line2D.Double legendLine = new Line2D.Double( 0, 0, 15, 0 );
        PPath totalEnergyLine = new PPath( legendLine );
        totalEnergyLine.setStroke( TotalEnergyLine.lineStroke );
        totalEnergyLine.setStrokePaint( TotalEnergyLine.linePaint );
        PText totalEnergyText = new PText( SimStrings.get( "EnergyView.Legend.totalEnergy" ) );
        totalEnergyText.setFont( labelFont );
        totalEnergyText.setTextPaint( Color.white );
        curvePaneLegend.addChild( totalEnergyLine );
        curvePaneLegend.addChild( totalEnergyText );

        PPath potentialEnergyLine = new PPath( legendLine );
        potentialEnergyLine.setStroke( TotalEnergyLine.lineStroke );
        potentialEnergyLine.setStrokePaint( curveColor );
        PText potentialEnergyText = new PText( SimStrings.get( "EnergyView.Legend.potentialEnergy" ) );
        potentialEnergyText.setFont( labelFont );
        potentialEnergyText.setTextPaint( Color.white );
        curvePaneLegend.addChild( potentialEnergyLine );
        curvePaneLegend.addChild( potentialEnergyText );

        Insets insets = new Insets( 5, 30, 0, 30 );
        potentialEnergyLine.setOffset( insets.left,
                                       insets.top + potentialEnergyText.getFullBounds().getHeight() / 2 );
        potentialEnergyText.setOffset( potentialEnergyLine.getOffset().getX() + potentialEnergyLine.getFullBounds().getWidth() + 10,
                                       insets.top );

        totalEnergyText.setOffset( curvePaneSize.getWidth() - insets.right - totalEnergyText.getFullBounds().getWidth(),
                                   insets.top );
        totalEnergyLine.setOffset( totalEnergyText.getOffset().getX() - 10 - totalEnergyLine.getFullBounds().getWidth(),
                                   insets.top + totalEnergyText.getFullBounds().getHeight() / 2 );
        curvePaneLegend.setOffset( 0, 0 );
        curvePane.addChild( curvePaneLegend );

        return curvePane;
    }

    private void createCurve( MRModel model, PNode curveLayer ) {
        if( energyProfileGraphic != null ) {
            curveLayer.removeChild( energyProfileGraphic );
        }
        energyProfileGraphic = new EnergyProfileGraphic( model.getEnergyProfile(),
                                                         curveAreaSize,
                                                         curveColor );
        curveLayer.addChild( energyProfileGraphic );
    }

    /**
     * Creates the pane that shows the molecules being tracked
     *
     * @return a PNode
     */
    private PPath createMoleculePane() {
        PPath moleculePane = new PPath( new Rectangle2D.Double( 0, 0,
                                                                upperPaneSize.getWidth(),
                                                                upperPaneSize.getHeight() ) );
        moleculePane.setPaint( moleculePaneBackgroundColor );
        moleculeLayer = new PNode();
        moleculeLayer.setOffset( curveAreaInsets.left, 0 );
        moleculePane.addChild( moleculeLayer );

        // Axis: An arrow that shows separation of molecules and text label
        // They are grouped in a single node so that they can be made visible or
        // invisible as necessary
        moleculePaneAxisNode = new PNode();
        separationIndicatorArrow = new SeparationIndicatorArrow( Color.black );
        moleculePaneAxisNode.addChild( separationIndicatorArrow );
        PText siaLabel = new PText( SimStrings.get( "EnergyView.separation" ) );
        siaLabel.setFont( labelFont );
        siaLabel.rotate( -Math.PI / 2 );
        siaLabel.setOffset( curveAreaInsets.left / 2 - siaLabel.getFullBounds().getWidth() + 2,
                            moleculePane.getFullBounds().getHeight() / 2 + siaLabel.getFullBounds().getHeight() / 2 );
        moleculePaneAxisNode.addChild( siaLabel );
        moleculePaneAxisNode.setVisible( false );
        moleculePane.addChild( moleculePaneAxisNode );

        return moleculePane;
    }

    /**
     * Updates the positions of the graphics
     * <p/>
     * We have a problem because the colliding molecules step their positions before we determine that
     * a hard collision should take place. And so the cursor ends up showing the molecules as having
     * gone over the threshold when they really couldn't have.
     */
    public void update() {
        if( selectedMolecule != null && selectedMoleculeGraphic != null && nearestToSelectedMoleculeGraphic != null ) {

            // Which side of the profile the molecules show up on depends on their type

            // Identify which molecule is the free one, and which one is bound in a composite
            // There is an intermediate state in the model where the reaction has occured but
            // the agent that tracks the selected molecule and the nearest one to it has not been
            // updated yet. We have to handle that by returning without updating ourselves/
            SimpleMolecule boundMolecule = null;
            SimpleMolecule freeMolecule = null;
            if( selectedMolecule.isPartOfComposite() ) {
                boundMolecule = selectedMolecule;
                if( !nearestToSelectedMolecule.isPartOfComposite() ) {
                    freeMolecule = nearestToSelectedMolecule;
                }
                else {
                    return;
                }
            }
            else if( nearestToSelectedMolecule.isPartOfComposite() ) {
                boundMolecule = nearestToSelectedMolecule;
                if( !selectedMolecule.isPartOfComposite() ) {
                    freeMolecule = selectedMolecule;
                }
                else {
                    return;
                }
            }

            // If neither molecule is part of a composite, then the selected molecule might
            // have just gone through a reaction and is now on its own, but the rest of the
            // model hasn't gone through the time step so the SelectedMoleculeTracker hasn't
            // had a chance to change the nearestToSelectedMolecule. The easiest thing to do
            // in this case is take a pass for now...
            else {
                System.out.println( "selectedMolecule = " + selectedMolecule );
                System.out.println( "nearestToSelectedMolecule = " + nearestToSelectedMolecule );
                return;
            }

            // Figure out on which side of the centerline the molecules should appear
            int direction = 0;
            // If the selected molecule is an A molecule and it's free, we're on the left
            if( selectedMolecule instanceof MoleculeA && selectedMolecule == freeMolecule ) {
                direction = -1;
            }
            // If the selected molecule is an A molecule and it's bound, we're on the right
            else if( selectedMolecule instanceof MoleculeA && selectedMolecule == boundMolecule ) {
                direction = 1;
            }
            // If the selected molecule is a C molecule and it's free, we're on the right
            else if( selectedMolecule instanceof MoleculeC && selectedMolecule == freeMolecule ) {
                direction = 1;
            }
            // If the selected molecule is a C molecule and it's bound, we're on the left
            else if( selectedMolecule instanceof MoleculeC && selectedMolecule == boundMolecule ) {
                direction = -1;
            }
            else {
                throw new RuntimeException( "internal error" );
            }

            // Position the molecule graphics
            double cmDist = selectedMolecule.getPosition().distance( nearestToSelectedMolecule.getPosition() );
            A_BC_AB_C_Reaction reaction = (A_BC_AB_C_Reaction)module.getMRModel().getReaction();
            double edgeDist = reaction.getDistanceToCollision( freeMolecule, boundMolecule.getParentComposite() );

            // The distance that the molecules have to overlap to react
            double distAtFootOfHill = Math.min( selectedMolecule.getRadius(), nearestToSelectedMolecule.getRadius() );

            // In the middle of the reaction, the collision distance is underfined
            if( Double.isNaN( edgeDist ) ) {
//                edgeDist = model.getReaction().getDistanceToCollision( freeMolecule, boundMolecule.getParentComposite() );
                edgeDist = 0;
            }
//            double edgeDist = cmDist - selectedMolecule.getRadius() - nearestToSelectedMolecule.getRadius();
            double maxSeparation = 80;
            double yOffset = 35;
            double xOffset = 20;

            // Determine what the max overlap would be based on the energy of the molecules
            double collisionEnergy = MRModelUtil.getCollisionEnergy( freeMolecule, boundMolecule.getParentComposite() );
            double floorEnergy = boundMolecule.getParentComposite() instanceof MoleculeAB ?
                                 reaction.getEnergyProfile().getRightLevel() :
                                 reaction.getEnergyProfile().getLeftLevel();
            double slope = ( reaction.getEnergyProfile().getPeakLevel() - floorEnergy ) / ( reaction.getEnergyProfile().getThresholdWidth() / 2 );
            double minDx = collisionEnergy / slope;

            // Scale the actual inter-molecular distance to the scale of the energy profile
            double r = ( reaction.getEnergyProfile().getThresholdWidth() / 2 ) / distAtFootOfHill;
            double dx = Math.max( ( edgeDist + distAtFootOfHill ) * r, minDx );
            double xOffsetFromCenter = Math.min( curveAreaSize.getWidth() / 2 - xOffset, dx );
            double x = curveAreaSize.getWidth() / 2 + ( xOffsetFromCenter * direction );
            Point2D midPoint = new Point2D.Double( x, yOffset + maxSeparation / 2 );
            double yMin = midPoint.getY() - Math.min( cmDist, maxSeparation ) / 2;
            double yMax = midPoint.getY() + Math.min( cmDist, maxSeparation ) / 2;

            // Set locatation of molecules. Use the *direction* variable we set above
            // to determine which graphic should be on top
            if( freeMolecule instanceof MoleculeC && freeMolecule == selectedMolecule ) {
                selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
            }
            else if( freeMolecule instanceof MoleculeC && freeMolecule == nearestToSelectedMolecule ) {
                selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            }
            else if( freeMolecule instanceof MoleculeA && freeMolecule == selectedMolecule ) {
                selectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
                nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
            }
            else if( freeMolecule instanceof MoleculeA && freeMolecule == nearestToSelectedMolecule ) {
                selectedMoleculeGraphic.setOffset( midPoint.getX(), yMax );
                nearestToSelectedMoleculeGraphic.setOffset( midPoint.getX(), yMin );
            }

            // Set the size of the separation indicator arrow
            separationIndicatorArrow.setEndpoints( curveAreaInsets.left / 2 + 10, yMin,
                                                   curveAreaInsets.left / 2 + 10, yMax );

            // Set the location of the bond graphic
            if( bondGraphic != null ) {
                bondGraphic.update( boundMolecule );
            }

            // set location of cursor
            cursor.setOffset( midPoint.getX(), 0 );
        }
        else if( selectedMoleculeGraphic != null ) {
            selectedMoleculeGraphic.setOffset( 20, 20 );
        }
        else if( nearestToSelectedMoleculeGraphic != null ) {
            nearestToSelectedMoleculeGraphic.setOffset( 20, 50 );
        }
    }

    public void setManualControl( boolean manualControl ) {
        cursor.setManualControlEnabled( manualControl );
    }

    public PPath getCurvePane() {
        return curvePane;
    }

    public void hideSelectedMolecule( boolean hide ) {
        upperPane.setVisible( hide );
    }

    public void setProfileManipulable( boolean manipulable ) {
        energyProfileGraphic.setManipulable( manipulable );
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private class SelectedMoleculeListener implements SelectedMoleculeTracker.Listener, MRModel.ModelListener {

        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                 SimpleMolecule prevTrackedMolecule ) {
            if( selectedMolecule != null ) {
                selectedMolecule.removeObserver( EnergyView.this );
            }

            selectedMolecule = newTrackedMolecule;
            if( selectedMoleculeGraphic != null
                && moleculeLayer.getChildrenReference().contains( selectedMoleculeGraphic ) ) {
                moleculeLayer.removeChild( selectedMoleculeGraphic );
            }

            if( newTrackedMolecule != null ) {
                selectedMoleculeGraphic = new EnergyMoleculeGraphic( newTrackedMolecule.getFullMolecule(),
                                                                     module.getMRModel().getEnergyProfile() );
                moleculeLayer.addChild( selectedMoleculeGraphic );
                newTrackedMolecule.addObserver( EnergyView.this );
                moleculePaneAxisNode.setVisible( true );
            }
            else {
                moleculePaneAxisNode.setVisible( false );

            }
            cursor.setVisible( selectedMolecule != null );
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule,
                                            SimpleMolecule prevClosestMolecule ) {
            if( nearestToSelectedMolecule != null ) {
                nearestToSelectedMolecule.removeObserver( EnergyView.this );
            }

            nearestToSelectedMolecule = newClosestMolecule;
            if( nearestToSelectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
            }
            nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( newClosestMolecule.getFullMolecule(),
                                                                          module.getMRModel().getEnergyProfile() );
            moleculeLayer.addChild( nearestToSelectedMoleculeGraphic );

            newClosestMolecule.addObserver( EnergyView.this );
//            if( bondGraphic != null ) {
//                removeChild( bondGraphic );
//            }
//            if( nearestToSelectedMolecule.isPartOfComposite() ) {
//                bondGraphic = new MyBondGraphic( nearestToSelectedMoleculeGraphic );
//                addChild( bondGraphic );
//            }

            update();
        }


        public void energyProfileChanged( EnergyProfile profile ) {
            if( selectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( selectedMoleculeGraphic );
                selectedMoleculeGraphic = new EnergyMoleculeGraphic( selectedMolecule.getFullMolecule(),
                                                                     profile );
                moleculeLayer.addChild( selectedMoleculeGraphic );
            }
            if( nearestToSelectedMoleculeGraphic != null ) {
                moleculeLayer.removeChild( nearestToSelectedMoleculeGraphic );
                nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( nearestToSelectedMolecule.getFullMolecule(),
                                                                              profile );
                moleculeLayer.addChild( nearestToSelectedMoleculeGraphic );
            }
        }
    }

    private class MyBondGraphic extends BondGraphic {
        private EnergyMoleculeGraphic emg;

        public MyBondGraphic( EnergyMoleculeGraphic emg ) {
            super( new Bond( selectedMolecule, nearestToSelectedMolecule ) );
            this.emg = emg;
        }

        public void update( SimpleMolecule sm ) {
        }
    }
}
