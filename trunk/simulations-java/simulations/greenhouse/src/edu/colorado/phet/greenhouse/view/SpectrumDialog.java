/* Copyright 2008, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;


/**
 * This class defines a dialog window that shows a representation of the
 * electromagnetic spectrum.
 *
 */
public class SpectrumDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public SpectrumDialog( Frame parentFrame ) {
        super( parentFrame, true );

        // Don't let the user resize this window.
        setResizable( false );

        // Create the panel that will contain the canvas and the "Close" button.
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder( BorderFactory.createEmptyBorder( 15, 15, 15, 15 ) ); // top, left, bottom, right
        mainPanel.setLayout( new BoxLayout( mainPanel, BoxLayout.Y_AXIS ) );

        // Create the canvas and add it to the panel.
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground( new Color( 233, 236, 174 ) );
        canvas.setPreferredSize( SpectrumDiagram.OVERALL_DIMENSIONS );
        canvas.setBorder( BorderFactory.createEtchedBorder() ); // top, left, bottom, right
        mainPanel.add( canvas );

        // Create the spectrum diagram on the canvas.
        canvas.addWorldChild( new SpectrumDiagram() );

        // Add an invisible panel that will create space between the diagram
        // and the close button.
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize( new Dimension( 1, 15) );
        mainPanel.add( spacerPanel );

        // Add the close button.
        // TODO: i18n
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                SpectrumDialog.this.dispose();
            }
        });
        closeButton.setAlignmentX( Component.CENTER_ALIGNMENT );
        mainPanel.add( closeButton );

        // Add to the dialog
        setContentPane( mainPanel );
        pack();
    }

    /**
     * Class that contains the diagram of the EM spectrum.  This is done as a
     * PNode in order to be translatable.
     */
    private static class SpectrumDiagram extends PNode {

        private static final Dimension OVERALL_DIMENSIONS = new Dimension( 600, 400 );
        private static final double HORIZONTAL_INSET = 20;
        private static final double VERTICAL_INSET = 20;

        public SpectrumDiagram(){

            // Add the title.
            // TODO: i18n
            PText title = new PText("Light Spectrum");
            title.setFont( new PhetFont( 30 ) );
            title.setOffset( OVERALL_DIMENSIONS.getWidth() / 2 - title.getFullBoundsReference().width / 2, VERTICAL_INSET );
            addChild( title );

            // Add the frequency arrow.
            // TODO: i18n
            LabeledArrow frequencyArrow = new LabeledArrow( OVERALL_DIMENSIONS.getWidth() - HORIZONTAL_INSET * 2,
                    LabeledArrow.Orientation.POINTING_RIGHT, "Increasing frequency and energy", new Color( 98, 93, 169 ),
                    Color.WHITE );
            frequencyArrow.setOffset( HORIZONTAL_INSET, title.getFullBoundsReference().getMaxY() + 10 );
            addChild( frequencyArrow );

            // Add the spectrum portion.
            LabeledSpectrumNode spectrum = new LabeledSpectrumNode( OVERALL_DIMENSIONS.width - 2 * HORIZONTAL_INSET );
            spectrum.setOffset( HORIZONTAL_INSET, frequencyArrow.getFullBoundsReference().getMaxY() + 20 );
            addChild( spectrum );

            // Add the wavelength arrow.
            // TODO: i18n
            LabeledArrow wavelengthArrow = new LabeledArrow( OVERALL_DIMENSIONS.getWidth() - HORIZONTAL_INSET * 2,
                    LabeledArrow.Orientation.POINTING_LEFT, "Increasing wavelength", Color.WHITE,
                    new Color( 205, 99, 78 ) );
            wavelengthArrow.setOffset( HORIZONTAL_INSET, spectrum.getFullBoundsReference().getMaxY() + 10 );
            addChild( wavelengthArrow );

            // Add the diagram that depicts the wave that gets shorter.
            DecreasingWavelengthWaveNode decreasingWavelengthNode =
                new DecreasingWavelengthWaveNode( OVERALL_DIMENSIONS.width - 2 * HORIZONTAL_INSET );
            decreasingWavelengthNode.setOffset( HORIZONTAL_INSET, wavelengthArrow.getFullBoundsReference().getMaxY() + 20 );
            addChild( decreasingWavelengthNode );
        }
    }

    /**
     * Class that defines a labeled arrow node.
     */
    private static class LabeledArrow extends PNode {
        public static double ARROW_HEAD_HEIGHT = 40;
        private static double ARROW_HEAD_WIDTH = 40;
        private static double ARROW_TAIL_WIDTH = 20;
        private static Font LABEL_FONT = new PhetFont( 16 );
        private static Stroke STROKE = new BasicStroke( 2 );

        public enum Orientation { POINTING_LEFT, POINTING_RIGHT };

        public LabeledArrow ( double length, Orientation orientation, String captionText, Color topColor, Color bottomColor ){

            // Create the paint that will be used to depict the arrow.  It is
            // assumed that the arrow has a gradient that changes in the
            // vertical direction.
            Paint gradientPaint = new GradientPaint( 0, (float) -ARROW_HEAD_HEIGHT / 2, topColor, 0,
                    (float) ARROW_HEAD_HEIGHT / 2, bottomColor );

            // Create and add the arrow node.
            ArrowNode arrowNode;
            if ( orientation == Orientation.POINTING_RIGHT ) {
                arrowNode = new ArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( length, 0 ),
                        ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            }
            else {
                assert orientation == Orientation.POINTING_LEFT;
                arrowNode = new ArrowNode( new Point2D.Double( length, 0 ), new Point2D.Double( 0, 0 ),
                        ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            }
            arrowNode.setPaint( gradientPaint );
            arrowNode.setStroke( STROKE );
            arrowNode.setOffset( 0, ARROW_HEAD_HEIGHT / 2 );
            addChild( arrowNode );

            // Create and add the textual label.
            PText label = new PText( captionText );
            label.setFont( LABEL_FONT );
            label.centerFullBoundsOnPoint( arrowNode.getFullBoundsReference().getCenterX(),
                    arrowNode.getFullBoundsReference().getCenterY() );
            addChild( label );
        }
    }

    /**
     * Class that depicts the frequencies and wavelengths of the EM spectrum
     * and labels the subsections (e.g. "Infrared").
     *
     * @author John Blanco
     */
    private static class LabeledSpectrumNode extends PNode {
        public LabeledSpectrumNode( double width ){
            addChild( new PhetPPath(new Rectangle2D.Double( 0, 0, width, width / 6 ), Color.BLUE ));
        }
    }

    /**
     * Class that depicts a wave that gets progressively shorter in wavelength
     * from left to right.
     */
    private static class DecreasingWavelengthWaveNode extends PNode {
        public DecreasingWavelengthWaveNode( double width ){
            addChild( new PhetPPath(new Rectangle2D.Double( 0, 0, width, width / 7 ), Color.GREEN));
        }
    }
}
