/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics2.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationCanvas;
import edu.colorado.phet.nuclearphysics2.module.alpharadiation.AlphaRadiationModel;
import edu.colorado.phet.nuclearphysics2.util.DoubleArrowNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.nodes.PLine;


/**
 * AlphaRadiationChart - This class displays the chart at the bottom of the
 * Alpha Radiation tab in this sim.  The chart shows the interaction between
 * the alpha particles and the energy barrier.
 *
 * @author John Blanco
 */
public class AlphaRadiationEnergyChart extends PComposite implements AlphaParticle.Listener {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants for controlling the appearance of the chart.
    private static final Color   BORDER_COLOR = Color.DARK_GRAY;
    private static final float   BORDER_STROKE_WIDTH = 6f;
    private static final Stroke  BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = Color.WHITE;
    private static final double  AXES_LINE_WIDTH = 2f;
    private static final double  ORIGIN_PROPORTION_X = 0.1d;
    private static final double  ORIGIN_PROPORTION_Y = 0.33d;
    private static final float   ENERGY_LINE_STROKE_WIDTH = 2f;
    private static final Stroke  ENERGY_LINE_STROKE = new BasicStroke( ENERGY_LINE_STROKE_WIDTH );
    private static final Color   TOTAL_ENERGY_LINE_COLOR = Color.GREEN;
    private static final Color   POTENTIAL_ENERGY_LINE_COLOR = new Color(0x990099);
    private static final Color   LEGEND_BORDER_COLOR = Color.GRAY;
    private static final float   LEGEND_BORDER_STROKE_WIDTH = 4f;
    private static final Stroke  LEGEND_BORDER_STROKE = new BasicStroke( LEGEND_BORDER_STROKE_WIDTH );
    private static final Color   LEGEND_BACKGROUND_COLOR = new Color(0xffffe0);
    private static final double  LEGEND_WIDTH = 190.0d;
    private static final double  LEGEND_HEIGHT = 80.0d;
    private static final double  ALPHA_PARTICLE_SCALE_FACTOR = 0.075;
    private static final int     MAX_ALPHA_PARTICLES_DISPLAYED = 6;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Reference to the model that this chart monitors in order to present its
    // information.
    private AlphaRadiationModel _model;
    
    // Reference to the canvas on which everything is being displayed.
    private PhetPCanvas _canvas;
    
    // Reference to the alpha particles that are monitored & displayed.
    private AlphaParticle _tunneledAlpha;
    private ArrayList     _alphaParticles = new ArrayList();
    private ArrayList     _currentlyTrackedAlphas = new ArrayList(MAX_ALPHA_PARTICLES_DISPLAYED);
    
    // Width of the energy well in the chart in screen coordinates.
    private double _energyWellWidth;

    // References to the various components of the chart.
    private PPath _borderNode;
    private PLine _totalEnergyLine;
    private PPath _potentialEnergyWell;
    private DoubleArrowNode _xAxisOfGraph;
    private DoubleArrowNode _yAxisOfGraph;
    private PText _yAxisLabel1;
    private PText _yAxisLabel2;
    private PText _xAxisLabel;
    private PPath _legend;
    private PText _legendTitle;
    private PText _potentialEnergyLabel;
    private PText _totalEnergyLabel;
    private PLine _potentialEnergyLegendLine;
    private PLine _totalEnergyLegendLine;
    private PImage _tunneledAlphaParticleImage;
    private PImage [] _alphaParticleImages = new PImage [MAX_ALPHA_PARTICLES_DISPLAYED];
    
    // Variables used for positioning nodes within the graph.
    double _usableAreaOriginX;
    double _usableAreaOriginY;
    double _usableWidth;
    double _usableHeight;
    double _graphOriginX;
    double _graphOriginY;
    
    // JPB TBD
    boolean _decayOccurred = false;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Constructor for this chart, which creates all of the elements of the
     * chart.  Note that it does not lay them out - it counts on calls to
     * the updateBounds routine to do that.
     */
    public AlphaRadiationEnergyChart(AlphaRadiationModel model, PhetPCanvas canvas) {
        
        setPickable( false );
        _model = model;
        _canvas = canvas;
        
        // Register as a listener with the model so that we can see when decay occurs.
        _model.getAtomNucleus().addListener( new AtomicNucleus.Listener(){
            public void atomicWeightChanged(int numProtons, int numNeutrons, ArrayList byProducts){
                if (byProducts != null){
                    handleDecayEvent(byProducts);
                }
                else{
                    // Must have been a reset of the nucleus.
                    _decayOccurred = false;
                }
            }
            public void positionChanged(){
                // Do nothing, since we don't care about this.
            }
        });
        
        // Register as a listener with the alpha particles so that we can
        // monitor them and display some number of them moving around within
        // the well.
        ArrayList nucleusConstituents = _model.getAtomNucleus().getConstituents();
        for (int i = 0; i < nucleusConstituents.size(); i++){
            if (nucleusConstituents.get( i ) instanceof AlphaParticle){
                // Add this to our overall list of watched particles.
                _alphaParticles.add( nucleusConstituents.get( i ) );
                
                // If we don't have enough yet, add this to our list of
                // particles that we are tracking and possibly displaying.
                if (_currentlyTrackedAlphas.size() < MAX_ALPHA_PARTICLES_DISPLAYED){
                    _currentlyTrackedAlphas.add( nucleusConstituents.get( i ) );
                }
                
                // Register as a listener to this particle.
                ((AlphaParticle)nucleusConstituents.get( i )).addListener( this );
            }
        }
        
        // Create the border for this chart.
        
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        addChild( _borderNode );
        
        // Initialize the arrow nodes that will comprise the axes of the
        // chart.  The initial sizes and positions are arbitrary, and the
        // real sizes and positions will be set when the bounds are updated.

        _xAxisOfGraph = new DoubleArrowNode( new Point2D.Double( 0, 0), new Point2D.Double( 100, 100), 
                10, 8, AXES_LINE_WIDTH);
        _xAxisOfGraph.setPaint( Color.black );
        _xAxisOfGraph.setStrokePaint( Color.black );
        addChild( _xAxisOfGraph);
        
        _yAxisOfGraph = new DoubleArrowNode( new Point2D.Double( 0, 0), new Point2D.Double( 100, 100), 
                10, 8, AXES_LINE_WIDTH);
        _yAxisOfGraph.setPaint( Color.black );
        _yAxisOfGraph.setStrokePaint( Color.black );
        addChild( _yAxisOfGraph);
                
        // Initialize attributes of the line that shows the total energy level.
        
        _totalEnergyLine = new PLine();
        _totalEnergyLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
        _totalEnergyLine.setStroke( ENERGY_LINE_STROKE );
        addChild( _totalEnergyLine);
        
        // Initialize attributes of the curve that shows the potential energy well.
        
        _potentialEnergyWell = new PPath();
        _potentialEnergyWell.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        _potentialEnergyWell.setStroke( ENERGY_LINE_STROKE );
        addChild( _potentialEnergyWell);
        
        // Add the text for the Y axis.

         _yAxisLabel1 = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.YAxisLabel1" ) );
         _yAxisLabel1.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
         _yAxisLabel1.rotate( 1.5 * Math.PI );
         addChild( _yAxisLabel1 );
         
         _yAxisLabel2 = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.YAxisLabel2" ) );
         _yAxisLabel2.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
         _yAxisLabel2.rotate( 1.5 * Math.PI );
         addChild( _yAxisLabel2 );
         
        // Add the text for the X axis.
        _xAxisLabel = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.XAxisLabel" ));
        _xAxisLabel.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        addChild( _xAxisLabel );
        
        // Create the legend (i.e. key) node for the chart.
        _legend = new PPath();
        _legend.setStroke( LEGEND_BORDER_STROKE );
        _legend.setStrokePaint( LEGEND_BORDER_COLOR );
        _legend.setPaint( LEGEND_BACKGROUND_COLOR );
        addChild( _legend );
        
        // Add the title to the legend.
        _legendTitle = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.legend.title"));
        _legendTitle.setFont( new PhetDefaultFont( Font.BOLD, 16 ) );
        _legend.addChild( _legendTitle );
        
        // Add other text and graphics to the legend.
        _potentialEnergyLegendLine = new PLine ();
        _potentialEnergyLegendLine.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        _potentialEnergyLegendLine.setStroke( ENERGY_LINE_STROKE );
        _legend.addChild( _potentialEnergyLegendLine );
        
        _potentialEnergyLabel = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.legend.PotentialEnergy") );
        _potentialEnergyLabel.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        _legend.addChild( _potentialEnergyLabel );
        
        _totalEnergyLegendLine = new PLine ();
        _totalEnergyLegendLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
        _totalEnergyLegendLine.setStroke( ENERGY_LINE_STROKE );
        _legend.addChild( _totalEnergyLegendLine );
        
        _totalEnergyLabel = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.legend.TotalEnergy") );
        _totalEnergyLabel.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        _legend.addChild( _totalEnergyLabel );
        
        // Add the images that will depict alpha particles moving around
        // within the nucleus.
        for (int i = 0; i < MAX_ALPHA_PARTICLES_DISPLAYED; i++){
            if ((i % 2) == 0){
               _alphaParticleImages[i] = NuclearPhysics2Resources.getImageNode("Alpha Particle 001.png");
            }
            else{
                _alphaParticleImages[i] = NuclearPhysics2Resources.getImageNode("Alpha Particle 002.png");                
            }
            _alphaParticleImages[i].setVisible( true );
            _alphaParticleImages[i].setScale( ALPHA_PARTICLE_SCALE_FACTOR );
            addChild( _alphaParticleImages[i] );
        }
        
        // Add the image that depicts the tunneling alpha particle.
        _tunneledAlphaParticleImage = NuclearPhysics2Resources.getImageNode("Alpha Particle 001.png");
        _tunneledAlphaParticleImage.setVisible( false );
        _tunneledAlphaParticleImage.setScale( ALPHA_PARTICLE_SCALE_FACTOR );
        addChild( _tunneledAlphaParticleImage );
    }

    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

    /**
     * This method causes the chart to resize itself based on the (presumably
     * different) size of the overall canvas on which it appears.
     * 
     * @param rect - Position on the canvas where this chart should appear.
     */
    public void componentResized( Rectangle2D rect ) {
        updateBounds( rect );
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------

    /**
     * This method is called to re-scale the chart, which generally occurs
     * when the overall size of the simulation canvas is changed.
     * 
     * @param rect - Rectangle where this chart should appear on the canvas.
     */
    private void updateBounds( Rectangle2D rect ) {

        // Recalculate the usable area and origin for the chart.
        
        _usableAreaOriginX = rect.getX() + BORDER_STROKE_WIDTH;
        _usableAreaOriginY = rect.getY() + BORDER_STROKE_WIDTH;
        _usableWidth       = rect.getWidth() - ( BORDER_STROKE_WIDTH * 2 );
        _usableHeight      = rect.getHeight() - ( BORDER_STROKE_WIDTH * 2);
        _graphOriginX      = _usableWidth * ORIGIN_PROPORTION_X + _usableAreaOriginX;
        _graphOriginY      = _usableHeight * ORIGIN_PROPORTION_Y + _usableAreaOriginY;
        
        // Recalculate energy well width.
        // Get the diameter of the atomic nucleus so that it can be
        // used to set the width of the energy well in the chart.
        double nucleusDiameter = _model.getAtomNucleus().getDiameter();
        PDimension nucleusDiameterDim = new PDimension(nucleusDiameter, nucleusDiameter);
        
        // Convert the diameter to screen coordinates so that we have
        // the right units for setting the width of the energy well in
        // the chart.
        _canvas.getPhetRootNode().worldToScreen( nucleusDiameterDim );
        _energyWellWidth = nucleusDiameterDim.getWidth();
        

        
        // Set up the border for the graph.
        
        _borderNode.setPathTo( new RoundRectangle2D.Double( 
                _usableAreaOriginX,
                _usableAreaOriginY,
                _usableWidth,
                _usableHeight,
                20,
                20 ) );
        
        // Position the axes for the graph.

        Point2D xAxisTailPt = new Point2D.Double( _usableAreaOriginX + BORDER_STROKE_WIDTH, _graphOriginY );
        Point2D xAxisTipPt = new Point2D.Double( _usableAreaOriginX + _usableWidth - BORDER_STROKE_WIDTH, _graphOriginY );
        _xAxisOfGraph.setTipAndTailLocations( xAxisTailPt, xAxisTipPt );
        Point2D yAxisTailPt = new Point2D.Double( _graphOriginX, _usableAreaOriginY + BORDER_STROKE_WIDTH );
        Point2D yAxisTipPt = new Point2D.Double( _graphOriginX, _usableAreaOriginY + _usableHeight - BORDER_STROKE_WIDTH );
        _yAxisOfGraph.setTipAndTailLocations( yAxisTailPt, yAxisTipPt );
        
        // Position the labels for the axes.
        
        _yAxisLabel1.setOffset( _graphOriginX - (2.5 * _yAxisLabel1.getFont().getSize()), 
                _graphOriginY + (0.5 * (yAxisTipPt.getY() - _graphOriginY + _yAxisLabel1.getWidth())));

        _yAxisLabel2.setOffset( _graphOriginX - (1.5 * _yAxisLabel2.getFont().getSize()), 
                _graphOriginY + (0.5 * (yAxisTipPt.getY() - _graphOriginY + _yAxisLabel2.getWidth())));

        _xAxisLabel.setOffset( xAxisTipPt.getX() - _xAxisLabel.getWidth() - _xAxisOfGraph.getHeadHeight() - 10,
                _graphOriginY + 5);

        
        // Position the line that represents the total energy.
        
        _totalEnergyLine.removeAllPoints();
        _totalEnergyLine.addPoint( 0, _usableAreaOriginX + 3*BORDER_STROKE_WIDTH, _graphOriginY - _usableHeight * 0.1 );
        _totalEnergyLine.addPoint( 1, _usableAreaOriginX + _usableWidth - 3*BORDER_STROKE_WIDTH, _graphOriginY - _usableHeight * 0.1 );
            

        // Position the curve that represents the potential energy.

        _potentialEnergyWell.reset();
        
        Point2D leftPeakOfEnergyWell = 
            new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) - (_energyWellWidth/2), 
                    _graphOriginY - (0.20 * _usableHeight));
        Point2D leftBottomOfEnergyWell = 
            new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) - (_energyWellWidth/2),
                _graphOriginY + (0.50 * _usableHeight));
        Point2D rightBottomOfEnergyWell = 
            new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) + (_energyWellWidth/2),
                _graphOriginY + (0.50 * _usableHeight));
        Point2D rightPeakOfEnergyWell = 
            new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) + (_energyWellWidth/2),
                _graphOriginY - (0.20 * _usableHeight));
        
        _potentialEnergyWell.append( new QuadCurve2D.Double(_usableAreaOriginX + 3 * BORDER_STROKE_WIDTH, 
        _graphOriginY - (0.03 * _usableHeight), (_usableAreaOriginX + (_usableWidth/2)) * 0.8,
        _graphOriginY - (0.05 * _usableHeight), leftPeakOfEnergyWell.getX(), leftPeakOfEnergyWell.getY()), 
                false );
        _potentialEnergyWell.append( new Line2D.Double(leftPeakOfEnergyWell, leftBottomOfEnergyWell), false);
        _potentialEnergyWell.append( new Line2D.Double(leftBottomOfEnergyWell, rightBottomOfEnergyWell), false);
        _potentialEnergyWell.append( new Line2D.Double(rightBottomOfEnergyWell, rightPeakOfEnergyWell), false);
        _potentialEnergyWell.append( new QuadCurve2D.Double(rightPeakOfEnergyWell.getX(),
                rightPeakOfEnergyWell.getY(), (_usableAreaOriginX + (_usableWidth/2)) * 1.2,
                _graphOriginY - (0.05 * _usableHeight),
                _usableAreaOriginX + _usableWidth - 3 * BORDER_STROKE_WIDTH,
                _graphOriginY - (0.03 * _usableHeight)), false );
        
        // Lay out the legend.
        
        double legendOriginX = _usableAreaOriginX + _usableWidth - LEGEND_WIDTH - (2 * LEGEND_BORDER_STROKE_WIDTH);
        double legendOriginY = _usableAreaOriginY + _usableHeight - LEGEND_HEIGHT - (2 * LEGEND_BORDER_STROKE_WIDTH);
        double legendUsableHeight = LEGEND_HEIGHT - (2 * LEGEND_BORDER_STROKE_WIDTH);
        _legend.setPathTo( new RoundRectangle2D.Double( 
                legendOriginX,
                legendOriginY,
                LEGEND_WIDTH,
                LEGEND_HEIGHT,
                10,
                10 ) );
        
        _legendTitle.setOffset(legendOriginX + LEGEND_BORDER_STROKE_WIDTH, legendOriginY + LEGEND_BORDER_STROKE_WIDTH);
        
        _totalEnergyLegendLine.removeAllPoints();        
        _totalEnergyLegendLine.addPoint( 0, legendOriginX + 15, legendOriginY + legendUsableHeight * 0.55 );
        _totalEnergyLegendLine.addPoint( 1, legendOriginX + 40, legendOriginY + legendUsableHeight * 0.55 );
        
        _totalEnergyLabel.setOffset(legendOriginX + 50, legendOriginY + legendUsableHeight * 0.4);
        
        _potentialEnergyLegendLine.removeAllPoints();        
        _potentialEnergyLegendLine.addPoint( 0, legendOriginX + 15, legendOriginY + legendUsableHeight * 0.8 );
        _potentialEnergyLegendLine.addPoint( 1, legendOriginX + 40, legendOriginY + legendUsableHeight * 0.8 );
        
        _potentialEnergyLabel.setOffset(legendOriginX + 50, legendOriginY + legendUsableHeight * 0.7);
    }
    
    /**
     * Handle notification of a decay event from the nucleus.  If everything
     * is correct, register with the alpha particle that was generated as a
     * result of the decay event so we can portray it moving away from the
     * nucleus.
     * 
     * @param decayProducts
     */
    private void handleDecayEvent(ArrayList decayProducts){
        
        if (decayProducts != null){
            
            // First make sure that this decay event is what is expected.
            if ((decayProducts.size() == 1) && (decayProducts.get( 0 ) instanceof AlphaParticle)){
                
                // This is the expected event.  Track this particle and make
                // its representation visible.
                _tunneledAlpha = (AlphaParticle)decayProducts.get( 0 );
                _tunneledAlphaParticleImage.setVisible(true);
                setAlphaImageOffset(_tunneledAlphaParticleImage, _tunneledAlpha);
                _decayOccurred = true;
            }
            else{
                System.err.println("Error: Unexpected decay event received.");
                assert false;
            }
            
        }
    }

    /**
     * Handle a change in the position of an alpha particle, and update the
     * displayed graphics accordingly.
     */
    public void positionChanged(AlphaParticle alpha){
        
        if ((_tunneledAlpha != null) && (_tunneledAlpha == alpha)){
            
            // Convert the current position into a distance from the center of
            // the nucleus and then into screen coordinates.
            
            Point2D tunneledAlphaPosition = _tunneledAlpha.getPosition();
            double distanceFromNucleus = tunneledAlphaPosition.distance( 0, 0 );
            PDimension distanceDim = new PDimension(distanceFromNucleus, distanceFromNucleus);
            _canvas.getPhetRootNode().worldToScreen( distanceDim );
            double distance;
            if (tunneledAlphaPosition.getX() < 0){
                distance = -(distanceDim.getWidth());
            }
            else{
                distance = distanceDim.getWidth();                
            }
            
            // Figure out where to place the image of the particle.
            
            if (Math.abs( distance ) > _usableWidth/2){
                // This guy is off the chart, so forget about him.
                _tunneledAlphaParticleImage.setVisible( false );
                _tunneledAlpha = null;
            }
            else{
                setAlphaImageOffset( _tunneledAlphaParticleImage, _tunneledAlpha );
            }
        }
        else if ((alpha.getPosition().distance( 0, 0 ) > _model.getAtomNucleus().getDiameter()) &&
                 (alpha.getPosition().distance( 0, 0 ) < _model.getAtomNucleus().getTunnelingRegionRadius())){
            // This particle is in the region outside of the nucleus but not
            // fully tunneled away from it.  We want to display this, so we
            // swap it for one of the currently non-tunneled particles.
            if (!(_currentlyTrackedAlphas.contains( alpha ))){
                for (int i = 0; i < _currentlyTrackedAlphas.size(); i++){
                    AlphaParticle trackedAlpha = (AlphaParticle)_currentlyTrackedAlphas.get( i );
                    if (trackedAlpha.getPosition().distance( 0, 0 ) <= _model.getAtomNucleus().getDiameter()/2){
                        // We will drop this one from the tracked list and replace
                        // it with the new one.
                        _currentlyTrackedAlphas.set( i, alpha );
                        setAlphaImageOffset( _alphaParticleImages[i], alpha);
                        break;
                    }
                }
            }
        }
        else if (_currentlyTrackedAlphas.contains( alpha )){
            // This is a particle that we're tracking, so we should update its
            // position.
            int index = _currentlyTrackedAlphas.indexOf( alpha );
            setAlphaImageOffset(_alphaParticleImages[index], alpha);
        }
    }
    
    /**
     * Set the position on the chart of one of the alpha particle graphics
     * based on the setting of the given alpha particle.
     * 
     * @param index
     * @param alpha
     */
    private void setAlphaImageOffset(PImage image, AlphaParticle alpha){
        assert image != null;
        if (image == null){
            return;
        }
        
        double yPos;
        if ((!_decayOccurred) || (alpha == _tunneledAlpha)){
            yPos = _graphOriginY - _usableHeight * 0.1 - 9;
        }
        else{
            yPos = _graphOriginY + 8;            
        }
        
        Point2D alphaPosition = alpha.getPosition();
        double distanceFromNucleus = alphaPosition.distance( 0, 0 );
        PDimension distanceDim = new PDimension(distanceFromNucleus, distanceFromNucleus);
        _canvas.getPhetRootNode().worldToScreen( distanceDim );
        double xPos;
        if (alphaPosition.getX() < 0){
            xPos = _usableWidth / 2 - distanceDim.getWidth();
        }
        else{
            xPos = _usableWidth / 2 + distanceDim.getWidth();                
        }
        image.setOffset( xPos, yPos );
    }
}