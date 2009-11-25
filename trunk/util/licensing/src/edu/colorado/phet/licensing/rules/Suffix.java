package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

public class Suffix extends AbstractRule {

    public Suffix( String pattern ) {
        super( pattern );
    }

    public boolean matches( ResourceAnnotation annotation ) {
        return endsWithPattern( annotation.getName() );
    }

    public boolean matches( LicenseInfo info ) {
        return false;
    }
}
