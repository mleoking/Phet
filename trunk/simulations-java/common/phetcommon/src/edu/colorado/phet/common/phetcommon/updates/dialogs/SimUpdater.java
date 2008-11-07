package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.io.*;
import java.util.Arrays;
import java.net.URLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;

public class SimUpdater {

    public void updateSim( String project, String sim,String locale ) {
        TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATE_NOW_PRESSED );
        //download the updater
        try {
            File f = File.createTempFile( "updater", ".jar" );
            download( "http://www.colorado.edu/physics/phet/dev/temp/updater.jar", f );
            println( "downloaded updater to: \n" + f.getAbsolutePath() );

            String javaPath = System.getProperty( "java.home" ) + System.getProperty( "file.separator" ) + "bin" + System.getProperty( "file.separator" ) + "java";
            File location = getCodeSource();
            if ( !location.getName().toLowerCase().endsWith( ".jar" ) ) {
                println( "Not running from a jar" );
                location = File.createTempFile( "" + sim, ".jar" );
                println( "CHanged download location to: " + location );
            }
            String[] cmd = new String[]{javaPath, "-jar", f.getAbsolutePath(), project, sim, locale, location.getAbsolutePath()};//todo support for locales

            println( "Starting updater with command: \n" + Arrays.toString( cmd ) );
            //TODO: disable opening a webpage unless someone asks for this feature
//                    OpenWebPageToNewVersion.openWebPageToNewVersion( project, sim );
            try {
                Thread.sleep( 10000 );
            }
            catch( InterruptedException e1 ) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Process p = Runtime.getRuntime().exec( cmd );
            System.exit( 0 );

            //todo: updater should allow 5 seconds or so for this to exit
        }
        catch( IOException e1 ) {
            e1.printStackTrace();
        }
    }


    /*//todo consolidate with many copies
    * Download data from URLs and save
    * it to local files. Run like this:
    * java FileDownload http://schmidt.devlib.org/java/file-download.html
    * @author Marco Schmidt
    * http://schmidt.devlib.org/java/file-download.html#source
    */
    public static void download( String address, File localFileName ) throws FileNotFoundException {
        localFileName.getParentFile().mkdirs();
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        try {
            URL url = new URL( address );
            out = new BufferedOutputStream( new FileOutputStream( localFileName ) );
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int numRead;
            long numWritten = 0;
            while ( ( numRead = in.read( buffer ) ) != -1 ) {
                out.write( buffer, 0, numRead );
                numWritten += numRead;
            }
//            println( localFileName + "\t" + numWritten );
        }
        catch( FileNotFoundException f ) {
            throw f;
        }
        catch( Exception exception ) {
            exception.printStackTrace();
        }
        finally {
            try {
                if ( in != null ) {
                    in.close();
                }
                if ( out != null ) {
                    out.close();
                }
            }
            catch( IOException ioe ) {
            }
        }
    }


    /*//todo consolidate with copy from FlashLauncher
    * Gets the JAR file that this class was launched from.
    */
    private File getCodeSource() {
        URL url = UpdateButton.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            URI uri = new URI( url.toString() );
            return new File( uri.getPath() );
        }
        catch( URISyntaxException e ) {
            println( e.getMessage() );
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    private void println( String message ) {
        DebugLogger.println( getClass().getName() + "> " + message );
    }

}
