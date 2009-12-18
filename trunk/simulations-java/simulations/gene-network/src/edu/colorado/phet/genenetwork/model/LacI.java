/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents LacI, which in real life is a protein that inhibits
 * (hence the 'I' in the name) the expression of genes coding for proteins
 * involved in lactose metabolism in bacteria.
 * 
 * @author John Blanco
 */
public class LacI extends SimpleModelElement {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// Constants that control size and appearance.
	private static final Paint ELEMENT_PAINT = new Color(200, 200, 200);
	private static double WIDTH = 7;   // In nanometers.
	private static double HEIGHT = 4;  // In nanometers.
	
	// Attachment point offset for attaching to lac operator.
	private static PDimension LAC_OPERATOR_ATTACHMENT_POINT_OFFSET = 
		new PDimension(0, -HEIGHT / 2  + LacOperator.getBindingRegionSize().getHeight());
	
	// Attachment point offset for attaching to glucose.
	private static PDimension GLUCOSE_ATTACHMENT_POINT_OFFSET = new PDimension(0, HEIGHT / 2);
	
	// Time definition for the amount of time that this is unavailable after
	// detaching.  Prevents instant detach/re-attach cycles.
	private static double UNAVAILABLE_TIME = 5; // In seconds.
	
	// Time of existence.
	private static final double EXISTENCE_TIME = 50; // Seconds.
	
	// Amount of time that this element and the lactose it bonds with
	// continues to exist after the bond has occurred.
	private static final double POST_LACTOSE_BOND_EXISTENCE_TIME = 7;
	
	// Point where the lacI heads towards when it is first created.  This was
	// needed due to a tendency for it to hang around the general area where
	// it was spawned and thus be a long way from the DNA binding region.
	// This point is empirically determined, tweak as needed.
	private static final Point2D INITIAL_DESTINATION_POINT = new Point2D.Double(20, 0);
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private LacOperator lacOperatorAttachmentPartner = null;
	private AttachmentState lacOperatorAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private Glucose glucoseAttachmentPartner = null;
	private AttachmentState glucoseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private Point2D targetPositionForLacOperatorAttachment = new Point2D.Double();
	private double unavailableTimeCountdown = 0;
	
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
	
	public LacI(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, createActiveConformationShape(), initialPosition, ELEMENT_PAINT, true, EXISTENCE_TIME);
		setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds(),
				INITIAL_DESTINATION_POINT));
	}
	
	public LacI(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
	}
	
	public LacI(){
		this(null);
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	@Override
	public void setDragging(boolean dragging) {
		if (dragging == true && lacOperatorAttachmentPartner != null){
			// The user has grabbed this node and is moving it, so release
			// any relationship that exists with the lac operator.
			lacOperatorAttachmentPartner.detach(this);
			lacOperatorAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			lacOperatorAttachmentPartner = null;
			setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBoundsExcludingDna()));
		}
		super.setDragging(dragging);
	}

	private static Shape createActiveConformationShape(){
		
		// Create the overall outline.
		GeneralPath outline = new GeneralPath();
		
		outline.moveTo(0, (float)HEIGHT/2);
		outline.quadTo((float)WIDTH / 2, (float)HEIGHT / 2, (float)WIDTH/2, -(float)HEIGHT/2);
		outline.lineTo((float)-WIDTH/2, (float)-HEIGHT/2);
		outline.lineTo((float)-WIDTH/2, (float)(HEIGHT * 0.25));
		outline.closePath();
		Area area = new Area(outline);
		
		// Get the shape of a lactose molecule and shift it to the appropriate
		// position.
		Shape lactoseShape = new Lactose().getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, HEIGHT/2 );
		lactoseShape = transform.createTransformedShape(lactoseShape);
		
		// Get the size of the binding region where this protein will bind to
		// the lac operator and create a shape for it.
		Dimension2D bindingRegionSize = LacOperator.getBindingRegionSize();
		Rectangle2D bindingRegionRect = new Rectangle2D.Double(-bindingRegionSize.getWidth() / 2,
				-HEIGHT/2, bindingRegionSize.getWidth(), bindingRegionSize.getHeight());
		
		// Subtract off the shape of the lactose molecule.
		area.subtract(new Area(lactoseShape));
		
		// Subtract off the shape of the binding region.
		area.subtract(new Area(bindingRegionRect));
		
		return area;
	}
	
	private static Shape createInactiveConformationShape(){
		
		// Create the overall outline.
		GeneralPath outline = new GeneralPath();
		
		outline.moveTo(0, (float)HEIGHT/2);
		outline.quadTo((float)WIDTH / 2, (float)HEIGHT / 2, (float)WIDTH/2, -(float)HEIGHT/3);
		outline.quadTo(0, (float)-HEIGHT * 0.8, (float)-WIDTH/2, (float)-HEIGHT/3);
		outline.lineTo((float)-WIDTH/2, (float)(HEIGHT * 0.25));
		outline.closePath();
		Area area = new Area(outline);
		
		// Get the shape of a lactose molecule and shift it to the appropriate
		// position.
		Shape lactoseShape = new Lactose().getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, HEIGHT/2 );
		lactoseShape = transform.createTransformedShape(lactoseShape);
		
		// Subtract off the shape of the lactose molecule.
		area.subtract(new Area(lactoseShape));
		
		return area;
	}
	
	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		
		if (!isUserControlled()){
			updateAttachements(dt);
			Point2D currentDestination = getMotionStrategyRef().getDestination();
			if ( currentDestination != null && 
					currentDestination.getX() == INITIAL_DESTINATION_POINT.getX() && 
					currentDestination.getY() == INITIAL_DESTINATION_POINT.getY()){
				
				if (currentDestination.distance(getPositionRef()) < 4){
					// We were moving toward the initial destination and are in
					// the neighborhood, so we can stop any directed motion and
					// just hang out here until we are told to do otherwise.
					getMotionStrategyRef().setDestination(null);
				}
			}
		}
	}

	private void updateAttachements(double dt) {
		// Update any attachment state related to lac operator first.  Note
		// that it is up to the lac operator to initiate and teminate the
		// attachment, so most of the effort for this relationship happens
		// in that class.
		if (lacOperatorAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVALABLE){
			if (unavailableTimeCountdown != Double.POSITIVE_INFINITY){
				unavailableTimeCountdown -= dt;
				if (unavailableTimeCountdown <= 0){
					// The recovery period is over, we can be available again.
					lacOperatorAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
				}
			}
		}
		
		// Now update any attachment state related to lactose.  Note that the
		// variable names are actually "glucose" since that is the molecule
		// to which we try to attach once we verify that it is attached to
		// galactose, thus forming lactose.
		if (getExistenceState() == ExistenceState.EXISTING &&
			glucoseAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE){
				
			// Look for some lactose to attach to.
			glucoseAttachmentPartner = getModel().findNearestFreeLactose(getPositionRef());
			
			if (glucoseAttachmentPartner != null){
				// We found a lactose that is free, so start the process of
				// attaching to it.
				if (glucoseAttachmentPartner.considerProposalFrom(this) != true){
					assert false;  // As designed, this should always succeed, so debug if it doesn't.
				}
				else{
					glucoseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
					
					// Prevent fadeout from occurring while attached to lactose.
					setOkayToFade(false);
				}
			}
		}
		else if (glucoseAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT){
			// See if we are close enough to finalize the bond with glucose.
			if (getGlucoseAttachmentPointLocation().distance(glucoseAttachmentPartner.getLacZAttachmentPointLocation()) < ATTACHMENT_FORMING_DISTANCE){
				
				// Finalize the attachment.
				glucoseAttachmentPartner.attach(this);
				glucoseAttachmentState = AttachmentState.ATTACHED;
				setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
				setShape(createInactiveConformationShape());
				
				// If we are currently attached to the lac operator, detach
				// from it now, since the basic idea is that when lactose
				// attaches to lacI it prevents it from being able to attach
				// to the DNA.
				if (lacOperatorAttachmentPartner != null){
					lacOperatorAttachmentPartner.detach(this);
					lacOperatorAttachmentPartner = null;
					lacOperatorAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
					setMotionStrategy(new DetachFromDnaThenRandomMotionWalkStrategy(this, LacOperonModel.getMotionBounds()));
				}
				
				// Set ourself and the lactose up so that we will fade out of
				// existence.
				setOkayToFade(true);
				setExistenceTime(POST_LACTOSE_BOND_EXISTENCE_TIME);
				glucoseAttachmentPartner.setLactoseExistenceTime(POST_LACTOSE_BOND_EXISTENCE_TIME);
			}
		}
	}

	/**
	 * Get the location in absolute space of the attachment point for this
	 * type of model element.
	 */
	public Point2D getGlucoseAttachmentPointLocation(){
		return new Point2D.Double(getPositionRef().getX() + GLUCOSE_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + GLUCOSE_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	public static Dimension2D getGlucoseAttachmentPointOffset() {
		return new PDimension(GLUCOSE_ATTACHMENT_POINT_OFFSET);
	}

	public boolean considerProposalFrom(LacOperator lacOperator) {
		boolean proposalAccepted = false;
		
		if (lacOperatorAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE && 
			getExistenceState() == ExistenceState.EXISTING &&
			glucoseAttachmentPartner == null){
			
			// We can accept the proposal.
			
			assert lacOperatorAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			lacOperatorAttachmentPartner = lacOperator;
			lacOperatorAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			proposalAccepted = true;
			setOkayToFade(false);
			
			// Set ourself up to move toward the attaching location.
			double xDest = lacOperatorAttachmentPartner.getLacIAttachmentPointLocation().getX() - 
				LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getWidth();
			double yDest = lacOperatorAttachmentPartner.getLacIAttachmentPointLocation().getY() -
				LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getHeight();
			setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds(),
					new Point2D.Double(xDest, yDest)));
			targetPositionForLacOperatorAttachment.setLocation(xDest, yDest);
		}
		
		return proposalAccepted;
	}
	
	public void attach(LacOperator lacOperator){
		if (lacOperator != lacOperatorAttachmentPartner){
			System.err.println(getClass().getName() + " - Error: Finalize request from non-partner.");
			assert false;
			return;
		}
		setMotionStrategy(new StillnessMotionStrategy(this));
		setPosition(targetPositionForLacOperatorAttachment);
		lacOperatorAttachmentState = AttachmentState.ATTACHED;
	}
	
	/**
	 * Get the location in absolute space of the attachment point for this
	 * type of model element.
	 */
	public Point2D getAttachmentPointLocation(LacOperator lacOperator){
		return new Point2D.Double(getPositionRef().getX() + LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	public static Dimension2D getLacOperatorAttachementPointOffset(){
		return LAC_OPERATOR_ATTACHMENT_POINT_OFFSET;
	}
	
	public void detach(LacOperator lacOperator){
		if (lacOperator != lacOperatorAttachmentPartner){
			System.err.println(getClass().getName() + " - Warning: Request to disconnect received from non-partner.");
			return;
		}
		
		lacOperatorAttachmentPartner = null;
		lacOperatorAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		unavailableTimeCountdown = UNAVAILABLE_TIME;
		setMotionStrategy(new DetachFromDnaThenRandomMotionWalkStrategy(this, LacOperonModel.getMotionBounds()));
		setOkayToFade(true);
	}
}
