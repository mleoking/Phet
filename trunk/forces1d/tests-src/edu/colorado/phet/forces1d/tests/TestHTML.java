package edu.colorado.phet.forces1d.tests;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.forces1d.common.HTMLGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 22, 2004
 * Time: 2:20:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestHTML {
    public static void main( String[] args ) {
//        JPanel fakePanel = new JPanel( new BorderLayout() );

        final JButton button = new JButton( "<html>Button<br>new<sub>mom<sup>Line<sub>2</html>" );
        final BufferedImage im = new BufferedImage( 200, 200, BufferedImage.TYPE_INT_RGB );
        button.setEnabled( true );
        button.setVisible( true );
        button.reshape( 100, 100, 100, 100 );

        ApparatusPanel ap = new ApparatusPanel() {
            protected void paintComponent( Graphics graphics ) {
                super.paintComponent( graphics );

//                label.paintAll( graphics );
                button.paintAll( graphics );

                Graphics2D g2 = (Graphics2D)graphics;
                g2.drawRenderedImage( im, AffineTransform.getTranslateInstance( 400, 50 ) );
//                button.paint( graphics );
//                button.paintComponents( graphics );
//                button.
            }
        };
        ap.add( button );


//        ap.setLayout( new BorderLayout());
//        ap.addTo(button,BorderLayout.CENTER);
        HTMLGraphic htmlGraphic = new HTMLGraphic( ap, "HELLO", new Font( "Lucida Sans", 0, 28 ), Color.black );
        htmlGraphic.setLocation( 300, 300 );
        ap.addGraphic( htmlGraphic );

        JFrame frame = new JFrame();

        frame.setContentPane( ap );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );
        frame.show();

        im.createGraphics().setColor( Color.red );
        im.createGraphics().fillRect( 0, 0, im.getWidth(), im.getHeight() );
        button.paintAll( im.createGraphics() );
        ap.repaint();
    }
}
