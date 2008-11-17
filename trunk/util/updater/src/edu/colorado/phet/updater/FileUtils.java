package edu.colorado.phet.updater;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FileUtils {

    public static void copyTo( File source, File dest ) throws IOException {
        copyAndClose( new FileInputStream( source ), new FileOutputStream( dest ) );
    }

    private static void copyAndClose( InputStream source, OutputStream dest ) throws IOException {
        copy( source, dest );
        source.close();
        dest.close();
    }

    private static void copy( InputStream source, OutputStream dest ) throws IOException {
        int bytesRead;

        byte[] buffer = new byte[1024];

        while ( ( bytesRead = source.read( buffer ) ) >= 0 ) {
            dest.write( buffer, 0, bytesRead );
        }
    }

    // copied from phetcommon FileUtils
    /**
     * Determines if a file has a specified suffix.
     * The suffix is case insensitive.
     * You can specify either "xyz" or ".xyz" and this will do the right thing.
     *
     * @param file
     * @param suffix
     * @return
     */
    public static boolean hasSuffix( File file, String suffix ) {
        if ( !suffix.startsWith( "." ) ) {
            suffix = "." + suffix;
        }
        return file.getName().toLowerCase().endsWith( suffix );
    }

    // copied from phetcommon FileUtils
    /**
     * Gets the JAR file that this class was launched from.
     */
    public static File getCodeSource() {
        URL url = FileUtils.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            URI uri = new URI( url.toString() );
            return new File( uri.getPath() );
        }
        catch( URISyntaxException e ) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
