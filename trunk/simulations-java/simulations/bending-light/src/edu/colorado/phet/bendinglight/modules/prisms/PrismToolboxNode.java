// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.modules.intro.HBox;
import edu.colorado.phet.bendinglight.modules.intro.ToolIconNode;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.MediumControlPanel;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.bendinglight.modules.prisms.PrismsModel.getPrismPrototypes;

/**
 * Prism toolbox which contains draggable prisms as well as the control panel for their index of refraction.
 *
 * @author Sam Reid
 */
public class PrismToolboxNode extends PNode {
    public PrismToolboxNode( final PrismsCanvas canvas, final ModelViewTransform transform, final PrismsModel model ) {
        //Create and add Title label for the prism toolbox
        final PText titleLabel = new PText( BendingLightStrings.PRISMS ) {{
            setFont( BendingLightCanvas.labelFont );
        }};
        addChild( titleLabel );
        HBox content = new HBox();
        content.setOffset( 0, 5 );//Move it down so it doesn't overlap the title label
        addChild( content );

        //Iterate over the prism prototypes in the model and create a draggable icon for each one
        for ( final Prism prism : getPrismPrototypes() ) {
            content.addChild( new PrismIcon( prism, model, transform, canvas, new Function0<Rectangle2D>() {
                public Rectangle2D apply() {
                    return getGlobalFullBounds();
                }
            } ) );
        }

        //Allow the user to control the type of material in the prisms
        content.addChild( new MediumControlPanel( canvas, model.prismMedium, BendingLightStrings.OBJECTS, false, model.wavelengthProperty, "0.0000000", 8 ) );
    }

    static class PrismIcon extends ToolIconNode {
        public PrismIcon( final Prism prism, final PrismsModel model, ModelViewTransform transform, PrismsCanvas canvas, final Function0<Rectangle2D> globalToolboxBounds ) {
            super( toThumbnail( prism, model, transform ), new Property<Boolean>( false ) {
                @Override public void setValue( Boolean value ) {
                    super.setValue( false );
                }
            }, transform, canvas, new NodeFactory() {
                public ToolNode createNode( ModelViewTransform transform, Property<Boolean> visible, Point2D location ) {
                    return new PrismToolNode( transform, prism.copy(), model, location );
                }
            }, model, globalToolboxBounds );
            dragMultiple = true;
        }

        @Override protected void addChild( BendingLightCanvas canvas, ToolNode node ) {
            canvas.addChildBehindLight( node );
        }

        @Override protected void doRemoveChild( BendingLightCanvas canvas, ToolNode node ) {
            canvas.removeChildBehindLight( node );
        }

        private static Image toThumbnail( Prism prism, PrismsModel model, ModelViewTransform transform ) {
            PrismNode prismNode = new PrismNode( transform, prism, model.prismMedium );
            final int thumbnailHeight = 70;
            final Image image = prismNode.toImage( (int) ( prismNode.getFullBounds().getWidth() * thumbnailHeight / prismNode.getFullBounds().getHeight() ), thumbnailHeight, null );
            try {
                ImageIO.write( BufferedImageUtils.toBufferedImage( image ), "PNG", new File( "C:/Users/Sam/Desktop/testimage_" + System.currentTimeMillis() + ".png" ) );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            return image;
        }
    }

    static class PrismToolNode extends ToolNode {
        private final ModelViewTransform transform;
        private final Prism prism;

        PrismToolNode( final ModelViewTransform transform, final Prism prism, final PrismsModel model, Point2D modelPoint ) {
            this.transform = transform;
            this.prism = prism;
            //Create a new prism model, and add it to the model
            final Rectangle2D bounds = prism.getBounds();
            Point2D copyCenter = new Point2D.Double( bounds.getX(), bounds.getY() );
            prism.translate( modelPoint.getX() - copyCenter.getX() - bounds.getWidth() / 2, modelPoint.getY() - copyCenter.getY() - bounds.getHeight() / 2 );
            model.addPrism( prism );
            addChild( new PrismNode( transform, prism, model.prismMedium ) );
        }

        @Override public void dragAll( PDimension viewDelta ) {
            prism.translate( transform.viewToModelDelta( viewDelta ) );
        }
    }
}