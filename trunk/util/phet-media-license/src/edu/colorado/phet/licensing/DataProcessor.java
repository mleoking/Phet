package edu.colorado.phet.licensing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class DataProcessor {

    public AnnotatedFile[] visitDirectory( PhetProject phetProject, File dir ) {
        ArrayList list = new ArrayList();
        File licenseFile = new File( dir, "license.txt" );
        ResourceAnnotationList resourceAnnotationList;
        resourceAnnotationList = ResourceAnnotationList.read( licenseFile );

        File[] f = dir.listFiles();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            if ( file.isDirectory() ) {
                if ( !ignoreDirectory( file ) ) {
                    AnnotatedFile[] fx = visitDirectory( phetProject, file );
                    list.addAll( Arrays.asList( fx ) );
                }
            }
            else {
                if ( !ignoreFile( phetProject, file ) ) {
                    ResourceAnnotation fx = visitFile( resourceAnnotationList, file );
                    list.add( new AnnotatedFile( file, fx ) );
                }
            }
        }
        return (AnnotatedFile[]) list.toArray( new AnnotatedFile[list.size()] );
    }

    protected ResourceAnnotation visitFile( ResourceAnnotationList resourceAnnotationList, File file ) {
        return resourceAnnotationList.getEntry( file.getName() );
    }

    private boolean ignoreFile( PhetProject project, File file ) {
        return file.getName().equals( project.getName() + ".properties" ) || file.getName().equalsIgnoreCase( "license.txt" ) || file.getName().equalsIgnoreCase( "license-orig.txt" ) || file.getName().equalsIgnoreCase( "sun-license.txt" );
    }

    private boolean ignoreDirectory( File dir ) {
        return dir.getName().equalsIgnoreCase( ".svn" ) || dir.getName().equalsIgnoreCase( "localization" );
    }
}