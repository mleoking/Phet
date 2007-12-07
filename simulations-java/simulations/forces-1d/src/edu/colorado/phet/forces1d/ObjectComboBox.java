/*  */
package edu.colorado.phet.forces1d;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.forces1d.model.Force1dObject;

/**
 * User: Sam Reid
 * Date: Feb 4, 2005
 * Time: 7:55:21 AM
 */

public class ObjectComboBox extends JComboBox {
    private static Font font = new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 10 );

    public ObjectComboBox( final Force1DApplication module, final Force1dObject[] imageElements ) {
        super( toLabelArray( imageElements ) );
        setRenderer( new ComboBoxRenderer() );
        if ( Toolkit.getDefaultToolkit().getScreenSize().width >= 1280 ) {
            setBorder( Force1DUtil.createSmoothBorder( SimStrings.get( "ObjectComboBox.chooseObject" ) ) );
        }
        addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
//                Object sel = getSelectedItem();
                int index = getSelectedIndex();
//                controlPanel.setup( imageElements[index] );
                module.setObject( imageElements[index] );
            }
        } );
        setFont( font );
    }

    private static ImageIcon[] toLabelArray( Force1dObject[] imageElements ) {
        ImageIcon[] lab = new ImageIcon[imageElements.length];
        for ( int i = 0; i < lab.length; i++ ) {
            try {
                BufferedImage image = ImageLoader.loadBufferedImage( imageElements[i].getLocation() );
                image = BufferedImageUtils.rescaleYMaintainAspectRatio( image, 35 );
                ImageIcon icon = new ImageIcon( image );
                icon.setDescription( imageElements[i].getName() + " (" + imageElements[i].getMass() + " " + SimStrings.get( "ObjectComboBox.kg" ) + ")" );

                lab[i] = icon;
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return lab;
    }

    public static class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        public ComboBoxRenderer() {
            setOpaque( true );
            setHorizontalAlignment( CENTER );
            setVerticalAlignment( CENTER );

//            setFont( new Font( PhetDefaultFont.LUCIDA_SANS,Font.BOLD, 10) );
        }

        public Component getListCellRendererComponent( JList list,
                                                       Object value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus ) {
            if ( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            ImageIcon icon = (ImageIcon) value;
            setText( icon.getDescription() );
            setIcon( icon );
            setFont( font );
            return this;
        }
    }
}
