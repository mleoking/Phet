package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

public class DnaStrandWithLacY extends DnaStrand {

	protected DnaSegmentSpace lacYGeneSpace;
	
	public DnaStrandWithLacY(IGeneNetworkModelControl model, Dimension2D size, Point2D initialPosition) {
		super(model, size, initialPosition);
		
		double xPos = getLacZGeneLocation().getX() + new LacZGene().getShape().getBounds2D().getMaxX() +
			new LacYGene().getShape().getBounds2D().getWidth() / 2 + 1;
		lacYGeneSpace = new DnaSegmentSpace(this, new LacYGene().getShape(), new Point2D.Double(xPos, 0));
		shapeList.add(lacYGeneSpace);
	}

	
	@Override
	protected void handleModelElementAdded(SimpleModelElement modelElement) {
		super.handleModelElementAdded(modelElement);
		// Add highlighting for the LacY gene.
		if (modelElement instanceof LacYGene){
			lacYGeneSpace.setEyeCatching(true);
			lacYGeneSpace.setModelElement(modelElement);
		}
	}


	@Override
	public Point2D getLacYGeneLocation() {
		return new Point2D.Double(getPositionRef().getX() + lacYGeneSpace.getOffsetFromDnaStrandPosRef().getX(),
				getPositionRef().getY() + lacYGeneSpace.getOffsetFromDnaStrandPosRef().getY());
	}
}
