package edu.colorado.phet.neuron.view;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.neuron.model.Atom;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Class that represents atoms in the view.
 */
public class AtomNode extends PNode {
	
	private Atom atom;
    private ModelViewTransform2D modelViewTransform;
    private SphericalNode sphere;
    private PText label;

    public AtomNode( Atom atom, ModelViewTransform2D modelViewTransform ) {
    	
		this.atom = atom;
        this.modelViewTransform = modelViewTransform;

        atom.addListener(new Atom.Listener() {
			public void positionChanged() {
				updateOffset();
			}
		});

        // Create the sphere that represents this atom.
        sphere = new SphericalNode( modelViewTransform.modelToViewDifferentialXDouble(atom.getDiameter()), 
        		atom.getRepresentationColor(), true);
		addChild( sphere );
        updateOffset();
        
        // Create the label.
        String labelText = MessageFormat.format("{0}{1}", atom.getChemicalSymbol(), atom.getChargeString());
        label = new PText(labelText);
        label.setFont(new PhetFont());
        sphere.addChild(label);
        
        // Scale the label to fit within the sphere.
        double maxDimension = Math.max(label.getFullBoundsReference().width, label.getFullBoundsReference().height);
        double scale = sphere.getFullBoundsReference().width / maxDimension;
        label.setScale(scale);
        
        // Center the label both vertically and horizontally.  This assumes
        // that the sphere is centered at 0,0.
        label.setOffset(-label.getFullBoundsReference().width / 2, -label.getFullBoundsReference().height / 2);
	}

    private void updateOffset() {
        sphere.setOffset( modelViewTransform.modelToView( atom.getPosition() ));
    }
}
