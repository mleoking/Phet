/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.Manifest;

import javax.swing.JFileChooser;

import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.jar.JarFactory;
import edu.colorado.phet.translationutility.jar.JarUtils;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.Command.CommandException;

/**
 * AbstractSimulation is the base class for all PhET simulations.
 * All PhET simulations are delivered as JAR files.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Simulation {
    
    /**
     * Exception type thrown by methods of this class.
     * Provides a layer of abstraction for more general types of exceptions.
     */
    public static class SimulationException extends Exception {
        public SimulationException( Throwable cause ) {
            super( cause );
        }
        public SimulationException( String message ) {
            super( message );
        }
        public SimulationException( String message, Throwable cause ) {
            super( message, cause );
        }
    }
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // temporary JAR file used to test translations
    protected static final String TEST_JAR = TUResources.getTmpdir() + TUConstants.FILE_PATH_SEPARATOR + "phet-test-translation.jar";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String jarFileName;
    private final Manifest manifest;
    private final String projectName;
    private final String simulationName;
    private final JarFactory jarFactory;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Simulation( String jarFileName, String projectName, String simulationName, JarFactory jarFactory ) throws SimulationException {
        this.jarFileName = jarFileName;
        try {
            this.manifest = JarUtils.getManifest( jarFileName );
        }
        catch ( IOException e ) {
            throw new SimulationException( "error reading manifest from " + jarFileName );
        }
        this.projectName = projectName;
        this.simulationName = simulationName;
        this.jarFactory = jarFactory;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    protected String getJarFileName() {
        return jarFileName;
    }
    
    protected Manifest getManifest() {
        return manifest;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public String getSimulationName() {
        return simulationName;
    }
    
    public JarFactory getJarFactory() {
        return jarFactory;
    }
    
    /**
     * Gets version information from a properties file in the jar.
     * @param propertiesFileName
     * @return version string, null if we couldn't get the version info
     * @throws SimulationException
     */
    protected String getProjectVersion( String propertiesFileName ) throws SimulationException {
        
        // read properties file that contains the version info
        Properties projectProperties = null;
        try {
            projectProperties = JarUtils.readProperties( getJarFileName(), propertiesFileName );
        }
        catch ( IOException e ) {
            throw new SimulationException( "error reading " + propertiesFileName + " from " + getJarFileName() );
        }
        if ( projectProperties == null ) {
            throw new SimulationException( "cannot find the version info file: " + propertiesFileName );
        }
        
        // extract the version info
        String major = projectProperties.getProperty( "version.major" );
        String minor = projectProperties.getProperty( "version.minor" );
        String dev = projectProperties.getProperty( "version.dev" );
        String revision = projectProperties.getProperty( "version.revision" );
        
        // format the version name
        Object[] args = { major, minor, dev, revision };
        return MessageFormat.format( "{0}.{1}.{2} ({3})", args );
    }
    
    //----------------------------------------------------------------------------
    // abstract
    //----------------------------------------------------------------------------
    
    /**
     * Gets the localized strings for a specified locale.
     * 
     * @param locale
     * @return
     * @throws SimulationException
     */
    public abstract Properties getStrings( Locale locale ) throws SimulationException;

    /**
     * Loads a set of localized strings from a file.
     * 
     * @param file
     * @return
     * @throws SimulationException
     */
    public abstract Properties loadStrings( File file ) throws SimulationException;

    /**
     * Saves a set of localized strings to a file.
     * 
     * @param properties
     * @param file
     * @throws SimulationException
     */
    public abstract void saveStrings( Properties properties, File file ) throws SimulationException;
    
    /**
     * Gets the name of the string file for a specified locale.
     * 
     * @param locale
     * @return
     */
    public abstract String getStringsFileName( Locale locale );
    
    /**
     * Gets the suffix used for string files.
     * @return
     */
    public abstract String getStringsFileSuffix();
    
    /**
     * Gets the path to the JAR resource that contains localized strings.
     */
    public abstract String getStringsResourcePath( Locale locale );
    
    /**
     * Gets a file chooser that is appropriate for the simulations string files.
     */
    public abstract JFileChooser getStringsFileChooser();
    
    //----------------------------------------------------------------------------
    // common
    //----------------------------------------------------------------------------
    
    public void testStrings( Locale locale, Properties localizedStrings ) throws SimulationException {
        
        // create the test jar
        final String testJarFileName = TEST_JAR;
        try {
            getJarFactory().createLocalizedJar( getJarFileName(), testJarFileName, locale, localizedStrings, getStringsResourcePath( locale ), true /* deleteOnExit */ );
        }
        catch ( IOException ioe ) {
            throw new SimulationException( "failed to create test jar" );
        }
        
        // run the test jar
        try {
            String[] cmdArray = { "java", "-jar", testJarFileName };
            Command.run( cmdArray, false /* waitForCompletion */ );
        }
        catch ( CommandException e ) {
            throw new SimulationException( e );
        }
    }
    
    /**
     * Gets a comment that describes the translation file.
     * 
     * @param fileName
     * @param projectName
     * @param projectVersion
     * @return
     */
    protected static String getTranslationFileHeader( String fileName, String projectName, String projectVersion ) {
        String timestamp = new Date().toString(); // timestamp is redundant for a properties file, but is included so that Java and Flash implementations are identical
        return fileName + " created " + timestamp + " using: " + projectName + " " + projectVersion + ", translation-utility " + TUResources.getVersion();
    }
}
