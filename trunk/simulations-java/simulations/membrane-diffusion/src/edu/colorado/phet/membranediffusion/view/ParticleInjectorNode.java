/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.membranediffusion.MembraneDiffusionResources;
import edu.colorado.phet.membranediffusion.model.InjectionMotionStrategy;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.colorado.phet.membranediffusion.model.Particle;
import edu.colorado.phet.membranediffusion.model.ParticleType;
import edu.colorado.phet.membranediffusion.model.PotassiumIon;
import edu.colorado.phet.membranediffusion.model.SodiumIon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Node that represents the thing with which the user interacts in order to
 * inject particles (generally ions) into the chamber.
 * 
 * @author John Blanco
 */
public class ParticleInjectorNode extends PNode {

	//------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// Height of the injector prior to rotation.  This is in world size, which
	// is close to pixels (but not quite exactly due to all that transform
	// craziness).
	private static final double INJECTOR_HEIGHT = 130;
	
	// Offset of button within this node.  This was determined by trial and
	// error and will need to be tweaked if the images change.
	private static final Point2D BUTTON_OFFSET = new Point2D.Double(-100, -65);
	
	// Velocity at which particles are injected in to the model.
	private static double NOMINAL_ION_INJECTION_VELOCITY = 30;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private final ParticleType particleType;
	private final PNode injectorBodyImageNode;
	private final PNode unpressedButtonImageNode;
	private final PNode pressedButtonImageNode;
	private final PNode particleTypeLabel;
	private final MembraneDiffusionModel model;
	private final ModelViewTransform2D mvt;
	private Dimension2D injectionPointOffset = new PDimension();
	private Point2D injectionPointInModelCoords = new Point2D.Double();
	private Vector2D nominalInjectionVelocityVector = new Vector2D.Double(NOMINAL_ION_INJECTION_VELOCITY, 0);
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Constructs a particle injection node.
     * @param particleType TODO
     * @param model - The model into which the particles will be injected.
     * @param mvt - Model-view transform for relating view space to model space.
     * @param rotationAngle - Angle of rotation for the injection bulb.
     */
	public ParticleInjectorNode(ParticleType particleType, final MembraneDiffusionModel model,
			ModelViewTransform2D mvt, double rotationAngle) {
		
		this.particleType = particleType;
		this.model = model;
		this.mvt = mvt;
		
		nominalInjectionVelocityVector.rotate(rotationAngle);

		PNode injectorNode = new PNode();
		
		// Load the graphic images for this device.  These are offset in order
		// to make the center of rotation be the center of the bulb.
		BufferedImage injectorBodyImage = MembraneDiffusionResources.getImage( "squeezer_background.png" );
        injectorBodyImageNode = new PImage( injectorBodyImage );
        Rectangle2D originalBodyBounds = injectorBodyImageNode.getFullBounds();
        injectorBodyImageNode.setOffset(-originalBodyBounds.getWidth() / 2, -originalBodyBounds.getHeight() / 2);
        injectorNode.addChild(injectorBodyImageNode);
        BufferedImage pressedButtonImage = MembraneDiffusionResources.getImage( "button_pressed.png" );
        pressedButtonImageNode = new PImage(pressedButtonImage);
        pressedButtonImageNode.setOffset(BUTTON_OFFSET);
        injectorNode.addChild(pressedButtonImageNode);
        BufferedImage unpressedButtonImage = MembraneDiffusionResources.getImage( "button_unpressed.png" );
        unpressedButtonImageNode = new PImage(unpressedButtonImage);
        unpressedButtonImageNode.setOffset(BUTTON_OFFSET);
        injectorNode.addChild(unpressedButtonImageNode);
        
        // Rotate and scale the image node as a whole.
        double scale = INJECTOR_HEIGHT / injectorBodyImageNode.getFullBoundsReference().height;
        injectorNode.rotate(-rotationAngle);
        injectorNode.scale(scale);
        
        // Add the node that allows control of automatic injection.
        particleTypeLabel = new ParticleTypeSelectorNode(INJECTOR_HEIGHT * 0.6, particleType);
        particleTypeLabel.setOffset(
        	injectorNode.getFullBoundsReference().getMinX() - particleTypeLabel.getFullBoundsReference().width + 5,
        	injectorNode.getFullBoundsReference().getCenterY() - particleTypeLabel.getFullBoundsReference().height / 2);
        addChild(particleTypeLabel);
        
        // Add the injector image node.  Note that the position has to be
        // tweaked in order to account for the rotation of the node image,
        // since the rotation of the square image enlarges the bounds.
        injectorNode.setOffset(-Math.abs(Math.sin(rotationAngle * 2)) * 30, 0);
        addChild(injectorNode);
        
        // Set up the injection point offset. This makes some assumptions
        // about the nature of the image, and will need to be updated if the
        // image is changed.
        double distanceCenterToTip = 0.7 * INJECTOR_HEIGHT;
        double centerOffsetX = 0.4 * INJECTOR_HEIGHT;
        injectionPointOffset.setSize(distanceCenterToTip * Math.cos(rotationAngle) + centerOffsetX,
        		distanceCenterToTip * Math.sin(-rotationAngle));
        
        // Set the point for automatic injection to be at the tip of the
        // injector.
        updateInjectionPoint();
        
        // Set up the button handling.
        unpressedButtonImageNode.setPickable(true);
        pressedButtonImageNode.setPickable(false);
        injectorBodyImageNode.setPickable(false);
        unpressedButtonImageNode.addInputEventListener(new CursorHandler());
        pressedButtonImageNode.addInputEventListener(new CursorHandler());
        unpressedButtonImageNode.addInputEventListener(new PBasicInputEventHandler(){
        	@Override
            public void mousePressed( PInputEvent event ) {
                unpressedButtonImageNode.setVisible(false);
                injectParticle();
            }
        	
        	@Override
            public void mouseReleased( PInputEvent event ) {
                unpressedButtonImageNode.setVisible(true);
            }
        });
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

	private void injectParticle(){
		Particle particleToInject = Particle.createParticle(particleType);
		particleToInject.setPosition(injectionPointInModelCoords);
		particleToInject.setMotionStrategy(new InjectionMotionStrategy(injectionPointInModelCoords, model, 0));
		model.injectParticle(particleToInject);
	}
	
	private void updateInjectionPoint(){
		double injectionPointX = mvt.viewToModelX(getFullBoundsReference().getCenter2D().getX() + injectionPointOffset.getWidth());
		double injectionPointY = mvt.viewToModelY(getFullBoundsReference().getCenter2D().getY() + injectionPointOffset.getHeight());
		injectionPointInModelCoords.setLocation(injectionPointX, injectionPointY);
	}
	
	@Override
	public void setOffset(double x, double y) {
		super.setOffset(x, y);
		updateInjectionPoint();
	}

	/**
	 * Class that defines the box that sits off to the side of the injector
	 * and that controls what type of particle is injected.
	 *
	 */
	private static class ParticleTypeSelectorNode extends PNode {
		
		private static final Color BACKGROUND_COLOR = new Color(248, 236, 84);
		private static final Stroke BORDER_STROKE = new BasicStroke(1f);
		private static final Color BORDER_COLOR = BACKGROUND_COLOR;
		private static final Stroke CONNECTOR_STROKE = new BasicStroke(8f);
		private static final Color CONNECTOR_COLOR = BACKGROUND_COLOR;
		private static final double CONNECTOR_LENGTH = 10;  // In screen coords, which is roughly pixels.
		private static final ModelViewTransform2D PARTICLE_MVT = new ModelViewTransform2D(
				new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-10, -10, 20, 20));
		
		/**
		 * Constructor.  The height is specified, and the width is then
		 * determined as a function of the height and the string lengths.
		 * 
		 * @param model
		 * @param height
		 */
		public ParticleTypeSelectorNode(double height, ParticleType particleType){
		    
            Image sodiumImage = new ParticleNode(new SodiumIon(), PARTICLE_MVT).toImage();
            JRadioButton sodiumButton = new JRadioButton();
            sodiumButton.setBackground( BACKGROUND_COLOR );
            JPanel sodiumButtonPanel = new HorizontalLayoutPanel();
            sodiumButtonPanel.setBackground( BACKGROUND_COLOR );
            sodiumButtonPanel.add( sodiumButton );
            sodiumButtonPanel.add( new JLabel( new ImageIcon( sodiumImage ) ) );
            
            Image potassiumImage = new ParticleNode(new PotassiumIon(), PARTICLE_MVT).toImage();
            JRadioButton potassiumButton = new JRadioButton();
            potassiumButton.setBackground( BACKGROUND_COLOR );
            JPanel potassiumButtonPanel = new HorizontalLayoutPanel();
            potassiumButtonPanel.setBackground( BACKGROUND_COLOR );
            potassiumButtonPanel.add( potassiumButton );
            potassiumButtonPanel.add( new JLabel( new ImageIcon( potassiumImage ) ) );
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( sodiumButton );
            buttonGroup.add( potassiumButton );
            
		    JPanel buttonPanel = new VerticalLayoutPanel();
		    buttonPanel.add( sodiumButtonPanel );
		    buttonPanel.add( potassiumButtonPanel );
		    
		    PSwing buttonPanelPSwing = new PSwing( buttonPanel );
		    
		    buttonPanelPSwing.setScale( height * 0.8 / buttonPanelPSwing.getFullBoundsReference().height );
		    
			// Create the body of the node.
			double bodyHeight = height;
			double bodyWidth = buttonPanelPSwing.getFullBoundsReference().width * 1.2;
            PhetPPath body = new PhetPPath(new RoundRectangle2D.Double(0, 0, bodyWidth,
                    bodyHeight, 14, 14), BACKGROUND_COLOR, BORDER_STROKE, BORDER_COLOR);
			
			// Put the particle selection buttons on the tag.
            buttonPanelPSwing.setOffset( 
                    body.getFullBoundsReference().getCenterX() - buttonPanelPSwing.getFullBoundsReference().getWidth() / 2, 
                    body.getFullBoundsReference().getCenterY() - buttonPanelPSwing.getFullBoundsReference().getHeight() / 2);
			body.addChild(buttonPanelPSwing);
			
			// Create the line that will visually connect this node to the
			// main injector node.
			PhetPPath connector = new PhetPPath(new Line2D.Double(0, 0, CONNECTOR_LENGTH, 0), CONNECTOR_STROKE, 
					CONNECTOR_COLOR);
			
			// Add the body and the line that will connect it to the injector node.
			addChild(connector);
			addChild(body);
			connector.setOffset(body.getFullBoundsReference().width, body.getFullBoundsReference().height / 2);
		}
	}
}
