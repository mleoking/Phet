/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.MultiMap;
import edu.colorado.phet.common.util.persistence.*;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.beans.*;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * ModuleManager
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModuleManager {

    public static boolean USE_GZIP_STREAMS = true;

    private ArrayList modules = new ArrayList();
    private Module activeModule;
    private ArrayList observers = new ArrayList();
    private PhetApplication phetApplication;

    public ModuleManager() {
    }

    public ModuleManager( PhetApplication phetApplication ) {
        this.phetApplication = phetApplication;
    }

    public Module moduleAt( int i ) {
        return (Module)modules.get( i );
    }

    public Module getActiveModule() {
        return activeModule;
    }

    public int numModules() {
        return modules.size();
    }

    public void addModule( Module module ) {
        addModule( module, false );
    }

    public boolean moduleIsWellFormed( Module module ) {
        boolean result = true;
        result &= module.getModel() != null;
        result &= module.getApparatusPanel() != null;
        return result;
    }

    public void addModule( Module module, boolean isActive ) {

        // Check that the module is well-formed
        if( !moduleIsWellFormed( module ) ) {
            throw new RuntimeException( "Module is missing something." );
        }

        modules.add( module );
        if( isActive ) {
            setActiveModule( module );
        }
        for( int i = 0; i < observers.size(); i++ ) {
            PhetApplication.ModuleObserver moduleObserver = (PhetApplication.ModuleObserver)observers.get( i );
            moduleObserver.moduleAdded( module );
        }
    }

    public void setActiveModule( int i ) {
        setActiveModule( moduleAt( i ) );
    }

    public void setActiveModule( Module module ) {
        if( activeModule != module ) {
            if( activeModule != null ) {
                activeModule.deactivate( phetApplication );
//                    activeModule.deactivate( PhetApplication.this );
            }
            activeModule = module;
            module.activate( phetApplication );
//                module.activate( PhetApplication.this );
        }
        for( int i = 0; i < observers.size(); i++ ) {
            PhetApplication.ModuleObserver moduleObserver = (PhetApplication.ModuleObserver)observers.get( i );
            moduleObserver.activeModuleChanged( module );
        }
    }

    public void addModuleObserver( PhetApplication.ModuleObserver observer ) {
        observers.add( observer );
    }

    public int indexOf( Module m ) {
        return modules.indexOf( m );
    }

    public void addAllModules( Module[] modules ) {
        for( int i = 0; i < modules.length; i++ ) {
            addModule( modules[i] );
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Save/restore methods
    //
    public void saveStateToConsole() {
        for( int i = 0; i < modules.size(); i++ ) {
            Module module = (Module)modules.get( i );
            XMLEncoder encoder = new XMLEncoder( System.out );
            encoder.writeObject( module );
            encoder.close();
        }
    }

    /**
     * Saves the state of the active module.
     *
     * @param fileName
     */
    public void saveState( String fileName ) {

        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.addChoosableFileFilter( new PstFilter() );
        fileChooser.showSaveDialog( phetApplication.getPhetFrame() );
        File file = fileChooser.getSelectedFile();

        if( file != null ) {
            XMLEncoder encoder = null;
            try {
                // Prevent the component for a PhetGraphic from being persisted for now. This keeps
                // ApparatusPanel from being persisted, for now.
                BeanInfo info = Introspector.getBeanInfo( PhetImageGraphic.class );
                PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
                for( int i = 0; i < propertyDescriptors.length; i++ ) {
                    PropertyDescriptor pd = propertyDescriptors[i];
                    if( pd.getName().equals( "image" ) ) {
                        pd.setValue( "transient", Boolean.TRUE );
                    }
                }

                OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( file ) );
//            OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( fileName ) );
                if( USE_GZIP_STREAMS ) {
                    outputStream = new GZIPOutputStream( outputStream );
                }
                encoder = new XMLEncoder( outputStream );

                encoder.setPersistenceDelegate( AffineTransform.class, new AffineTransformPersistenceDelegate() );
                encoder.setPersistenceDelegate( BasicStroke.class, new BasicStrokePersistenceDelegate() );
                encoder.setPersistenceDelegate( Ellipse2D.Double.class, new Ellipse2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( Ellipse2D.Float.class, new Ellipse2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( GeneralPath.class, new GeneralPathPersistenceDelegate() );
                encoder.setPersistenceDelegate( GradientPaint.class, new GradientPaintPersistenceDelegate() );
                encoder.setPersistenceDelegate( Point2D.Double.class, new Point2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( Point2D.Float.class, new Point2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( Rectangle2D.Double.class, new Rectangle2DPersistenceDelegate() );
                encoder.setPersistenceDelegate( Rectangle2D.Float.class, new Rectangle2DPersistenceDelegate() );

            }
            catch( Exception ex ) {
                ex.printStackTrace();
            }
            Module module = getActiveModule();
            StateDescriptor sd = module.getState();
            encoder.writeObject( sd );
            encoder.close();
        }
    }

    /**
     * Sets the active module to the one specified in the named file, and sets the state of the
     * module to that specified in the file.
     *
     * @param fileName
     */
    public void restoreState( String fileName ) {

        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.addChoosableFileFilter( new PstFilter() );
        fileChooser.showOpenDialog( phetApplication.getPhetFrame() );
        File file = fileChooser.getSelectedFile();

        if( file != null ) {

            XMLDecoder decoder = null;
            try {
                InputStream inputStream = new BufferedInputStream( new FileInputStream( file ) );
//                InputStream inputStream = new BufferedInputStream( new FileInputStream( fileName ) );
                if( USE_GZIP_STREAMS ) {
                    inputStream = new GZIPInputStream( inputStream );
                }
                decoder = new XMLDecoder( inputStream );
            }
            catch( FileNotFoundException e ) {
                e.printStackTrace();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

            // Read in the ModuleStateDescriptor
            ModuleStateDescriptor sd = (ModuleStateDescriptor)decoder.readObject();
            decoder.setExceptionListener( new ExceptionListener() {
                public void exceptionThrown( Exception exception ) {
                    exception.printStackTrace();
                }
            } );

            // Find the module that is of the same class as the one that we're
            // restoring. Set it to be the active module, and tell it to
            // restore itself from the saved state
            for( int i = 0; i < modules.size(); i++ ) {
                Module module = (Module)modules.get( i );
                if( module.getClass().getName().equals( sd.getModuleClassName() ) ) {
                    setActiveModule( module );
                    sd.setState( module );
                }
            }
        }
    }

    /**
     * File filter for *.pst files
     */
    private class PstFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept( File file ) {
            String filename = file.getName();
            return filename.endsWith( ".pst" ) || file.isDirectory();
        }

        public String getDescription() {
            return "*.pst";
        }
    }
}
