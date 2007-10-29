/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import edu.colorado.phet.common.phetcommon.util.OrderedProperties;

/**
 * AbstractResourceLoader provides a default implementation for
 * portions of IResourceLoader that are not PhET specific.
 */
/* package private */
abstract class AbstractResourceLoader implements IResourceLoader {

    private static final String PROPERTIES_SUFFIX = ".properties";

    /**
     * Determines if a named resource exists.
     *
     * @param resourceName
     * @return true or false
     */
    public boolean exists( String resource ) {
        ClassLoader cl = this.getClass().getClassLoader();
        URL url = cl.getResource( resource );

        //if (url == null) {
        //    new Exception("The resource " + resource + " could not be found").printStackTrace();
        //}

        return url != null;
    }

    /**
     * Gets properties by resource name.
     * <p/>
     * Looks up a resource named 'resourceName' in the classpath.
     * The resource must map to a file with .properties extention.
     * The name may be an absolute or relative path and can use either
     * "/" or "." for package segment separation with an optional
     * leading "/" and optional ".properties" suffix. Thus, the
     * following names refer to the same resource:
     * <pre>
     * some.pkg.Resource
     * some.pkg.Resource.properties
     * some/pkg/Resource
     * some/pkg/Resource.properties
     * /some/pkg/Resource
     * /some/pkg/Resource.properties
     * </pre>
     *
     * @param resourceName classpath resource name [may not be null]
     * @param locale       the locale
     * @return resource converted to java.util.Properties
     */
    public PhetProperties getProperties( String resourceName, final Locale locale ) {

        final String fullResourceName = getFullResourceName( resourceName, locale );
        // Get the resource bundle
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle( resourceName, locale );
        }
        catch( Exception e ) {
            e.printStackTrace();
            rb = null;
        }

        // Load the resource bundle into Properties
        Properties properties = new Properties();
        if ( rb != null ) {
            for ( Enumeration keys = rb.getKeys(); keys.hasMoreElements(); ) {
                final String key = (String) keys.nextElement();
                final String value = rb.getString( key );

                properties.put( key, value );
            }
        }

        //The following code demonstrates one way obtain an Enumeration on Properties that has the same ordering as the original file
        //This code is not thoroughly tested, and has obvious performance impacts that could be avoided.
        boolean useOrderedProperties = false;
        if ( useOrderedProperties ) {
            return new PhetProperties( properties )
            {
                public Enumeration propertyNames() {
                    InputStream is1 = Thread.currentThread().getContextClassLoader().getResourceAsStream( fullResourceName );
                    InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream( fullResourceName );
                    if ( is1 == null || is2 == null ) {
                        return super.propertyNames();
                    }
                    OrderedProperties orderedProperties = null;
                    try {
                        orderedProperties = new OrderedProperties( is1, is2 );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                    return new OrderedProperties.OrderedPropertiesAdapter( orderedProperties ).propertyNames();
                }
            };
        }
        else {
            return new PhetProperties( properties );
        }
    }

    private String getFullResourceName( String resourceName, Locale locale ) {
        if ( resourceName == null ) {
            throw new IllegalArgumentException( "name is null" );
        }

        // Process the resource name
        if ( resourceName.startsWith( "/" ) ) {
            resourceName = resourceName.substring( 1 );
        }
        if ( resourceName.endsWith( PROPERTIES_SUFFIX ) ) {
            resourceName = resourceName.substring( 0, resourceName.length() - PROPERTIES_SUFFIX.length() );
        }
        final String rn=""+resourceName;
        resourceName = resourceName.replace( '/', '.' );

        String lang = "_" + locale.getLanguage();
        if ( lang.equals( "_en" ) ) {
            lang = "";
        }
        String fullResourceName = rn + lang + ".properties";
        return resourceName;
    }

    /**
     * Gets an input stream for the specified resource.
     *
     * @param resourceName
     * @return InputStream
     */
    public InputStream getResourceAsStream( String resource ) throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream( resource );
        if ( stream == null ) {
            throw new IOException( "invalid resource: " + resource );
        }
        return stream;
    }

    /**
     * Gets a byte array for the specified resource.
     *
     * @param resourceName
     * @return byte[]
     */
    public byte[] getResource( String resource ) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream stream = getResourceAsStream( resource );
        try {
            byte[] buffer = new byte[1000];
            int bytesRead;
            while ( ( bytesRead = stream.read( buffer ) ) >= 0 ) {
                out.write( buffer, 0, bytesRead );
            }
            out.flush();
        }
        catch( Exception e ) {
            try {
                stream.close();
            }
            catch( IOException e1 ) {
                e1.printStackTrace();
            }
        }
        return out.toByteArray();
    }
}
