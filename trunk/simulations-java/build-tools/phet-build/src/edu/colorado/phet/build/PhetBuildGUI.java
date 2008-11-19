package edu.colorado.phet.build;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.build.java.BuildScript;
import edu.colorado.phet.build.java.PhetServer;

/**
 * Provides a front-end user interface for building and deploying phet's java simulations.
 * This entry point has no ant dependencies.
 */
public class PhetBuildGUI {
    private JFrame frame = new JFrame();
    private Object blocker = new Object();
    private JList simList;
    private JButton runButton;
    private File baseDir;

    public PhetBuildGUI( File baseDir ) {
        this.baseDir = baseDir;
        this.frame = new JFrame( "PhET Build" );
        frame.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                synchronized( blocker ) {
                    blocker.notifyAll();
                }
            }
        } );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetProject[] a = PhetProject.getAllProjects( baseDir );
        MyPhetProject[] b = convertToMyPhetProjecets( a );
        for ( int i = 0; i < a.length; i++ ) {
            b[i].setAntBaseDir( baseDir );
        }
        Project[] p = toProjects( b );
        simList = new JList( p );
        simList.setSelectedIndex( 0 );
        simList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        JPanel contentPane = new JPanel();

        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets( 2, 2, 2, 2 ), 0, 0 );
        JScrollPane simListPane = new JScrollPane( simList );
        simListPane.setBorder( BorderFactory.createTitledBorder( "Projects" ) );
        contentPane.add( simListPane, gridBagConstraints );

        JPanel commandPanel = new JPanel();
        JButton cleanButton = new JButton( "Clean" );
        cleanButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    getBuildScript().clean();
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );


        JButton buildButton = new JButton( "Build" );
        buildButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().build();
            }
        } );

        JButton buildJNLP = new JButton( "Build Local JNLP" );
        buildJNLP.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().buildJNLP( "file:///" + getSelectedProject().getDefaultDeployJar().getParentFile().getAbsolutePath() );
            }
        } );

        runButton = new JButton( "Run" );
        runButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().runSim();
            }
        } );

        JButton svnStatus = new JButton( "SVN Status" );
        svnStatus.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().isSVNInSync();
            }
        } );

        JButton deployDev = new JButton( "Deploy Dev" );
        deployDev.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().deploy( PhetServer.DEVELOPMENT, getDevelopmentAuthentication() );
            }
        } );

        JButton incrementVersionNumber = new JButton( "Increment Dev" );
        incrementVersionNumber.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getBuildScript().incrementDevVersion();
            }
        } );

        JButton getSVN = new JButton( "Get SVN version" );
        getSVN.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "getBuildScript().getSVNVersion() = " + getBuildScript().getSVNVersion() );
            }
        } );


        GridBagConstraints commandConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        commandPanel.setLayout( new GridBagLayout() );
//        commandPanel.add( refresh, commandConstraints );
//        commandPanel.add( showLocalizationFile, commandConstraints );

        commandPanel.add( cleanButton, commandConstraints );
        commandPanel.add( buildButton, commandConstraints );
        commandPanel.add( runButton, commandConstraints );
        commandPanel.add( buildJNLP, commandConstraints );
        commandPanel.add( svnStatus, commandConstraints );
        commandPanel.add( incrementVersionNumber, commandConstraints );
        commandPanel.add( getSVN, commandConstraints );

        commandPanel.add( deployDev, commandConstraints );
        commandPanel.add( Box.createVerticalBox() );

        contentPane.add( commandPanel, gridBagConstraints );


        frame.setSize( 800, 600 );
        frame.setContentPane( contentPane );
    }

    private BuildScript getBuildScript() {
        return new BuildScript( baseDir, getSelectedProject() );
    }


    private AuthenticationInfo getDevelopmentAuthentication() {
        return new AuthenticationInfo();
    }

    private MyPhetProject[] convertToMyPhetProjecets( PhetProject[] a ) {
        MyPhetProject[] b = new MyPhetProject[a.length];
        for ( int i = 0; i < a.length; i++ ) {
            try {
                b[i] = new MyPhetProject( a[i].getProjectDir() );
                b[i].setAntBaseDir( baseDir );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return b;
    }


    private Project[] toProjects( PhetProject[] a ) {
        Project[] p = new Project[a.length];
        for ( int i = 0; i < p.length; i++ ) {
            p[i] = new Project( a[i] );
        }
        return p;
    }

    static class Project {
        PhetProject p;

        Project( PhetProject p ) {
            this.p = p;
        }

        public String toString() {
            return p.getName();
        }
    }

    private void runCommandWithWaitDialog( String msg, final Runnable r ) {
        final JDialog dialog = new JDialog( frame, msg );
        JLabel label = new JLabel( "Building " + getSelectedProject() + ", please wait..." );
        label.setOpaque( true );
        JPanel panel = new JPanel();
        panel.setBorder( BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );
        panel.add( label );
        dialog.setContentPane( panel );
        dialog.pack();
        dialog.setResizable( false );
        dialog.setLocation( frame.getX() + frame.getWidth() / 2 - dialog.getWidth() / 2, frame.getY() + frame.getHeight() / 2 - dialog.getHeight() / 2 );
        dialog.setVisible( true );

        Runnable r2 = new Runnable() {
            public void run() {
                r.run();
                dialog.dispose();
            }
        };
        Thread thread = new Thread( r2 );
        thread.start();
    }

    private PhetProject getSelectedProject() {
        return ( (Project) simList.getSelectedValue() ).p;
    }


    private void start() {
        frame.setVisible( true );
    }

    private class MyPhetProject extends PhetProject {
        private File baseDir;

        public MyPhetProject( File projectRoot ) throws IOException {
            super( projectRoot );
        }

        public MyPhetProject( File parentDir, String name ) throws IOException {
            super( parentDir, name );
        }

        public void setAntBaseDir( File baseDir ) {
            this.baseDir = baseDir;
        }

        public File getAntBaseDir() {
            return baseDir;
        }

        public PhetProject[] getDependencies() {
            PhetProject[] a = super.getDependencies();
            return convertToMyPhetProjecets( a );
        }
    }

    public static void main( String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "Usage: args[0]=basedir" );
        }
        else {
            new PhetBuildGUI( new File( args[0] ) ).start();
        }
    }

    public static class AuthenticationInfo {

        public String getUsername() {
            return JOptionPane.showInputDialog( "login username" );
        }

        public String getPassword() {
            return JOptionPane.showInputDialog( "login password" );
        }
    }
}
