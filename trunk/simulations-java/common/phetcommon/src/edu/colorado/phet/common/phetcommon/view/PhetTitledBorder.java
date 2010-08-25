package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * This is the default Border to be used in subsections of the control panel in phet simulations.
 * It provides the proper font and font style, and provides antialiasing and curved edges for the border.
 * See ticket #2476: https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/2476
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class PhetTitledBorder extends TitledBorder {
    public PhetTitledBorder( String title ) {
        super( new PhetLineBorder(), title );
        init();
    }

    public PhetTitledBorder( String title, int titleJustification, int titlePosition ) {
        super( new PhetLineBorder(), title, titleJustification, titlePosition );
        init();
    }

    public PhetTitledBorder( String title, int titleJustification, int titlePosition, Font titleFont ) {
        super( new PhetLineBorder(), title, titleJustification, titlePosition, titleFont );
        init();
    }

    public PhetTitledBorder( String title, int titleJustification, int titlePosition, Font titleFont, Color titleColor ) {
        super( new PhetLineBorder(), title, titleJustification, titlePosition, titleFont, titleColor );
        init();
    }

    //The following methods allow the client to specify an underlying border other than the default line border

    public PhetTitledBorder( Border border ) {
        super( border );
        init();
    }

    public PhetTitledBorder( Border border, String title ) {
        super( border, title );
        init();
    }

    public PhetTitledBorder( Border border, String title, int titleJustification, int titlePosition ) {
        super( border, title, titleJustification, titlePosition );
        init();
    }

    public PhetTitledBorder( Border border, String title, int titleJustification, int titlePosition, Font titleFont ) {
        super( border, title, titleJustification, titlePosition, titleFont );
        init();
    }

    public PhetTitledBorder( Border border, String title, int titleJustification, int titlePosition, Font titleFont, Color titleColor ) {
        super( border, title, titleJustification, titlePosition, titleFont, titleColor );
        init();
    }

    private void init() {
        setTitleFont( new PhetFont( Font.BOLD, PhetFont.getDefaultFontSize() + 4 ) );
    }

    @Override
    public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
        Graphics2D g2 = (Graphics2D) g;
        Object oldAntialiasHint = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintBorder( c, g, x, y, width, height );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldAntialiasHint );
    }

    private static class PhetLineBorder extends LineBorder {
        public PhetLineBorder() {
            super( Color.black, 1, true );
        }

        /*Overrides paintBorder to round the corners and curve the edges
         */

        @Override
        public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
            Graphics2D g2 = (Graphics2D) g;
            Object oldAntialiasHint = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
            Color oldColor = g2.getColor();
            g2.setColor( getLineColor() );
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.drawRoundRect( x, y, width - 1, height - 1, 8, 8 );
            g2.setColor( oldColor );
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldAntialiasHint );
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final JPanel contentPane = new VerticalLayoutPanel();
        contentPane.setBorder( new PhetTitledBorder( "Border" ) );
        for ( int i = 0; i < 10; i++ ) {
            contentPane.add( new JLabel( "medium sized label " + i ) );
        }
        contentPane.add( new JButton( "A button" ) );
        frame.setContentPane( contentPane );
        frame.pack();
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
