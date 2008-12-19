package edu.colorado.phet.build.java;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.build.PhetProject;

public class ProjectPanel extends JPanel {
    private File basedir;
    private PhetProject project;
    private JLabel titleLabel;
    private JTextArea changesTextArea;
    private JList flavorList;
    private JList localeList;
    private JScrollPane changesScrollPane;
    private PhetProject.Listener listener = new PhetProject.Listener() {
        public void changesTextChanged() {
            updateChangesText();
            updateTitleLabel();
        }
    };

    private void updateTitleLabel() {
        titleLabel.setText( project.getName() + " (" + project.getVersionString() + ")" );
    }

    private LocalProperties localProperties;
    private JButton deployProdButton;

    public ProjectPanel( File basedir, final PhetProject project ) {
        this.basedir = basedir;
        this.project = project;
        this.localProperties = new LocalProperties( basedir );
        titleLabel = new JLabel( project.getName() );


        changesTextArea = new JTextArea( 10, 30 );
        changesTextArea.setEditable( false );
        changesScrollPane = new JScrollPane( changesTextArea );
        changesScrollPane.setPreferredSize( new Dimension( 600, 250 ) );


        flavorList = new JList( project.getFlavorNames() );
        JScrollPane flavorScrollPane = new JScrollPane( flavorList );
        flavorScrollPane.setBorder( BorderFactory.createTitledBorder( "Simulations" ) );
        flavorScrollPane.setPreferredSize( new Dimension( 300, 100 ) );


        localeList = new JList( project.getLocales() );
        JScrollPane localeScroll = new JScrollPane( localeList );
        localeScroll.setBorder( BorderFactory.createTitledBorder( "Locales" ) );
        localeScroll.setPreferredSize( new Dimension( 300, 200 ) );

        JPanel controlPanel = new JPanel();
        JButton testButton = new JButton( "Test" );
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doTest();
            }
        } );
        controlPanel.add( testButton );
        JButton deployDevButton = new JButton( "Deploy Dev" );
        deployDevButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doDev();
            }
        } );
        controlPanel.add( deployDevButton );
        deployProdButton = new JButton( "Deploy Prod" );
        deployProdButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doProd();
            }
        } );
        controlPanel.add( deployProdButton );

        testButton.setMnemonic( 't' );
        deployDevButton.setMnemonic( 'd' );
        deployProdButton.setMnemonic( 'p' );

        //For testing
//        JButton createHeader = new JButton( "Create Header" );
//        createHeader.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                getBuildScript().createHeader( -1 );
//            }
//        } );
//        controlPanel.add( createHeader );

        add(
                verticalBox(
                        horizontalBox(
                                verticalBox( flavorScrollPane, localeScroll ),
                                verticalBox( titleLabel, changesScrollPane ) ),
                        controlPanel )
        );

        setProject( project );
    }

    private void doProd() {
        int option = JOptionPane.showConfirmDialog( deployProdButton, "Are you sure you are ready to deploy " + project.getName() + " to " + PhetServer.PRODUCTION.getHost() + "?" );
        if ( option == JOptionPane.YES_OPTION ) {
            getBuildScript().deployProd( getDevelopmentAuthentication( "dev" ), getDevelopmentAuthentication( "prod" ) );
        }
        else {
            System.out.println( "Cancelled" );
        }
    }

    private void doDev() {
        getBuildScript().deployDev( getDevelopmentAuthentication( "dev" ) );
    }

    private void doTest() {
        getBuildScript().build();
        getBuildScript().runSim( getSelectedLocale(), getSelectedFlavor() );
    }

    private AuthenticationInfo getDevelopmentAuthentication( String serverType ) {
        return new AuthenticationInfo( getLocalProperty( "deploy." + serverType + ".username" ), getLocalProperty( "deploy." + serverType + ".password" ) );
    }

    private BuildScript getBuildScript() {
        return new BuildScript( basedir, project, new AuthenticationInfo( getLocalProperty( "svn.username" ), getLocalProperty( "svn.password" ) ), getLocalProperty( "browser" ) );
    }

    private String getLocalProperty( String s ) {
        return localProperties.getProperty( s );
    }

    public JComponent horizontalBox( JComponent a, JComponent b ) {
        return box( a, b, BoxLayout.X_AXIS );
    }

    public JComponent verticalBox( JComponent a, JComponent b ) {
        return box( a, b, BoxLayout.Y_AXIS );
    }

    private JComponent box( JComponent a, JComponent b, int axis ) {
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, axis ) );
        panel.add( a );
        panel.add( b );
        return panel;
    }

    private String getSelectedFlavor() {
        return (String) flavorList.getSelectedValue();
    }

    private Locale getSelectedLocale() {
        return (Locale) localeList.getSelectedValue();
    }

    public void setProject( PhetProject project ) {
        this.project.removeListener( listener );
        this.project = project;
        this.project.addListener( listener );
        updateTitleLabel();
        flavorList.setListData( project.getFlavorNames() );
        flavorList.setSelectedIndex( 0 );
        localeList.setListData( project.getLocales() );

        updateChangesText();
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                changesTextArea.scrollRectToVisible( new Rectangle( 0, 0, 1, 1 ) );
                localeList.scrollRectToVisible( new Rectangle( 0, 0, 1, 1 ) );
                localeList.setSelectedValue( new Locale( "en" ), true );
            }
        } );
    }

    private void updateChangesText() {
        changesTextArea.setText( project.getChangesText() );
    }

}
