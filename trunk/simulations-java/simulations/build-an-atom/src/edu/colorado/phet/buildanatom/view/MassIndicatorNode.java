package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows a scale with a numeric readout of the atom weight.  Origin is the top left of the scale body (not the platform).
 *
 * @author Sam Reid
 */
public class MassIndicatorNode extends PNode {
    private Atom atom;

    public MassIndicatorNode( final Atom atom ) {
        this.atom=atom;
        final Rectangle2D.Double baseShape = new Rectangle2D.Double( 0, 0, 100, 30 );
        final double stemWidth = 4;
        final double platformHeight = 3;

        final PhetPPath base = new PhetPPath( baseShape, BuildAnAtomConstants.READOUT_BACKGROUND_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( base );

        final PhetPPath stem = new PhetPPath( BuildAnAtomConstants.READOUT_BACKGROUND_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( stem );

        final PhetPPath platformNode = new PhetPPath( BuildAnAtomConstants.READOUT_BACKGROUND_COLOR, new BasicStroke( 1 ), Color.black );
        addChild( platformNode );

        final PText readoutPText = new PText() {{
            setFont( BuildAnAtomConstants.READOUT_FONT );
            setTextPaint( Color.red );
        }};
        addChild( readoutPText );

        SimpleObserver updateText = new SimpleObserver() {
            public void update() {
                readoutPText.setText( atom.getAtomicMassNumber() + "" );
                readoutPText.setOffset( base.getFullBounds().getCenterX() - readoutPText.getFullBounds().getWidth() / 2, base.getFullBounds().getCenterY() - readoutPText.getFullBounds().getHeight() / 2 );
            }
        };
        atom.addObserver( updateText );
        updateText.update();

        //from 9/30/2010 meeting
        //will students think the atom on the scale is an electron?
        //use small icon of orbits/cloud instead of cloud

        //TODO: copied from BuildAnAtomCanvas, should be factored out into something like ElectronShellNode
        final PNode atomNode = new PNode();
        //Make it small enough so it looks to scale, but also so we don't have to indicate atomic substructure
        Stroke stroke = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 1.5f, 1.5f }, 0 );
        double scale = 1.0/7;
        ModelViewTransform2D mvt = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 1, 1 ), new Rectangle2D.Double( 0, 0, scale,scale), false );
        for ( Double shellRadius : atom.getElectronShellRadii() ) {
            Shape electronShellShape = mvt.createTransformedShape( new Ellipse2D.Double(
                    -shellRadius,
                    -shellRadius,
                    shellRadius * 2,
                    shellRadius * 2 ) );
            PNode electronShellNode = new PhetPPath( electronShellShape, stroke, Color.BLUE );
            atomNode.addChild( electronShellNode );
        }
        double nucleusWidth=1;
        atomNode.addChild( new PhetPPath( new Ellipse2D.Double( -nucleusWidth / 2, -nucleusWidth / 2, nucleusWidth, nucleusWidth ), Color.red ) {{
            setOffset( atomNode.getFullBounds().getCenter2D() );
            final SimpleObserver updateNucleusNode = new SimpleObserver() {
                public void update() {
                    setVisible( atom.getNumProtons() + atom.getNumNeutrons() > 0 );
                    if ( atom.getNumProtons() > 0 ) {
                        setPaint( Color.red );//if any protons, it should look red
                    }
                    else {
                        setPaint( Color.gray );
                    } //if no protons, but some neutrons, should look neutron colored
                }
            };
            atom.addObserver( updateNucleusNode );
            updateNucleusNode.update();
        }} );
        addChild( atomNode );

        //have the scale compress with increased weight
        final SimpleObserver updateCompression = new SimpleObserver() {
            public void update() {
                Rectangle2D.Double stemShape = new Rectangle2D.Double( baseShape.getCenterX() - stemWidth / 2, baseShape.getMinY() - getStemHeight(), stemWidth, getStemHeight() );
                stem.setPathTo( stemShape );
                Rectangle.Double platform = new Rectangle2D.Double( baseShape.getX(), stemShape.getY() - platformHeight, baseShape.getWidth(), platformHeight );
                platformNode.setPathTo( platform );
                atomNode.setOffset( platform.getCenterX(), platform.getCenterY()-atomNode.getFullBounds().getHeight()/2 //set the atom on the scale
                                                   - 4 );//looks weird if shell sits on the scale, so have it float a little
            }
        };
        atom.addObserver( updateCompression );
        updateCompression.update();
    }

    public double getStemHeight(){
        Function.LinearFunction linearFunction=new Function.LinearFunction( 0,23,//23 max atomic weight based on allowed number protons and neutrons
                                                                            8,//tallest stem
                                                                            2);//shortest stem
        double stemHeight = linearFunction.evaluate( atom.getAtomicMassNumber() );
        return stemHeight;
    }
}
