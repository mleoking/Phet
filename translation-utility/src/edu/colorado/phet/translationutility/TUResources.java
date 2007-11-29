/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * This is a convenience wrapper around PhetResources that provides 
 * access to localized strings and images that reside in the classpath.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TUResources {
    
    private static final PhetResources RESOURCES = PhetResources.forProject( "translation-utility" );
    
    /* not intended for instantiation */
    private TUResources() {}
    
    public static final PhetResources getResourceLoader() {
        return RESOURCES;
    }
    
    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name  );
    }
    
    public static final char getChar( String name, char defaultValue ) {
        return RESOURCES.getLocalizedChar( name, defaultValue );
    }

    public static final int getInt( String name, int defaultValue ) {
        return RESOURCES.getLocalizedInt( name, defaultValue );
    }
    
    public static final BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }
    
    public static final Icon getIcon( String name ) {
        return new ImageIcon( RESOURCES.getImage( name ) );
    }
    
    public static final String getCommonString( String name ) {
        return PhetCommonResources.getInstance().getLocalizedString( name );
    }
    
    public static final BufferedImage getCommonImage( String name ) {
        return PhetCommonResources.getInstance().getImage( name );
    }
}
