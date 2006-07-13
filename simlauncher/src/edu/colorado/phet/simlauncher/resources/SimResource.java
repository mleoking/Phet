/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.resources;

import edu.colorado.phet.simlauncher.MetaData;
import edu.colorado.phet.simlauncher.PhetSiteConnection;
import edu.colorado.phet.simlauncher.util.FileUtil;
import edu.colorado.phet.simlauncher.util.LauncherUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * SimResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimResource {
    URL url;
    private MetaData metaData;
    private File localFile;
    private File localRoot;

    /**
     * @param url
     * @param localRoot
     */
    public SimResource( URL url, File localRoot ) {
        this.url = url;
        this.localRoot = localRoot;
        localFile = getLocalFile( localRoot );
        if( isInstalled() ) {
            try {
                metaData = new MetaData( localFile );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tells if the resource is installed locally
     *
     * @return true if the resource is installed locally
     */
    public boolean isInstalled() {
        return localFile != null && localFile.exists();
    }

    /**
     * Tells if the remote component of the resource is accessible
     *
     * @return true if the remote component is accessible, false otherwise
     */
    public boolean isRemoteAvailable() {
        if( !PhetSiteConnection.instance().isConnected() ) {
            return false;
        }
        else {
            return LauncherUtil.instance().isRemoteAvailable( url );
        }
    }

    /**
     * Tells if the local version of the resource is current with the remote version
     *
     * @return true if the local version of the resource is current
     */
    public boolean isCurrent() throws SimResourceException {
        if( !isRemoteAvailable() ) {
            throw new SimResourceException( "Not online" );
        }

        // Get timestamp for remote
        long remoteTimestamp = 0;
        try {
            remoteTimestamp = url.openConnection().getLastModified();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        if( metaData == null ) {
            System.out.println( "SimResource.isCurrent : metadate == null" );
            return false;
        }

        // get timestamp of metadata
        long localTimestamp = metaData.getLastModified();

        // compare and return result
        if( localTimestamp > remoteTimestamp ) {
            throw new RuntimeException( "local timestamp newer than remote timestamp" );
        }
        return localTimestamp == remoteTimestamp;
    }

    public void download() throws SimResourceException {
        if( !isCurrent() ) {
            try {
                if( !localFile.getParentFile().exists() ) {
                    localFile.getParentFile().mkdirs();
                }
                if( !localFile.exists() ) {
                    localFile.createNewFile();
                }
                InputStream in = url.openStream();
                FileOutputStream out = new FileOutputStream( localFile );

                // Transfer bytes from in to out, from almanac
                byte[] buf = new byte[1024];
                int len;
                while( ( len = in.read( buf ) ) > 0 ) {
                    out.write( buf, 0, len );
                }
                out.flush();
                in.close();
                out.close();
                saveMetaData();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private void saveMetaData() throws IOException {
        int cnt = 0;
        while( metaData == null ) {
            this.metaData = new MetaData( url );
            cnt++;
        }
        System.out.println( "cnt = " + cnt );
        metaData.saveForFile( localFile );
//        System.out.println( "cnt = " + cnt );
    }

    public void update() throws SimResourceException {
        if( !isCurrent() ) {
            download();
        }
    }

    protected File getLocalRoot() {
        return localRoot;
    }

    public File getLocalFile() {
        return localFile;
    }

    /**
     * Creates a local file for the resource
     *
     * @param localRoot
     * @return the local file
     */
    private File getLocalFile( File localRoot ) {
        // Parse the URL to get path relative to URL root
        String path = url.getPath();
        String pathSeparator = FileUtil.getPathSeparator();
        path = path.replace( '/', pathSeparator.charAt( 0 ) );
        path = path.replace( '\\', pathSeparator.charAt( 0 ) );
        return new File( localRoot, url.getHost() + pathSeparator + path );
    }

    /**
     * Removes the resource's local file and clears its metadata
     */
    public void uninstall() {
        localFile.delete();
        if( metaData != null ) {
            metaData.deleteForFile( localFile );
        }
        else {
            System.out.println( "metaData == null. this = " + this );
        }
        metaData = null;
    }
}