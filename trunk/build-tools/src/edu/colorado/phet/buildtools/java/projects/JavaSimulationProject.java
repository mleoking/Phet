package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.java.JavaProject;

public class JavaSimulationProject extends JavaProject {
    public JavaSimulationProject( File file ) throws IOException {
        super( file );
    }

    public JavaSimulationProject( File parentDir, String name ) throws IOException {
        super( parentDir, name );
    }

    public File getTrunkAbsolute() {
        return new File( getProjectDir(), "../../.." );
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
        System.out.println( "Building JNLP." );
        buildJNLP( URL, dev );
    }

    public String getAlternateMainClass() {
        return null;//uses jarlauncher
    }

    public String getProdServerDeployPath() {
        return null;//deploys to simulations
    }

    public boolean getSignJar() {
        return true;
    }
}