package edu.colorado.phet.common.phetcommon.updates;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jnlp.UnavailableServiceException;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

public class OpenWebPageToNewVersion {
    public static void openWebPageToNewVersion( String project, String sim ) {
        try {
            PhetServiceManager.getBasicService().showDocument( new URL( AbstractUpdateDialog.getSimURL( project, sim ) ) );
        }
        catch( UnavailableServiceException e1 ) {
            e1.printStackTrace();
        }
        catch( MalformedURLException e1 ) {
            e1.printStackTrace();
        }
    }
}
