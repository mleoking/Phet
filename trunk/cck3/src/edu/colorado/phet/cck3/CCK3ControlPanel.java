/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import net.n3.nanoxml.*;
import org.srr.localjnlp.ServiceSource;
import org.srr.localjnlp.local.InputStreamFileContents;

import javax.jnlp.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jun 1, 2004
 * Time: 11:03:06 AM
 * Copyright (c) Jun 1, 2004 by Sam Reid
 */
public class CCK3ControlPanel extends JPanel {
    private CCK3Module module;


    public CCK3ControlPanel( final CCK3Module module ) {
        JLabel titleLabel = ( new JLabel( new ImageIcon( getClass().getClassLoader().getResource( "images/phet-cck-small.gif" ) ) ) );
        titleLabel.setBorder( BorderFactory.createRaisedBevelBorder() );
        titleLabel.setBorder( BorderFactory.createLineBorder( Color.black, 2 ) );
        this.module = module;
        //        setLayout( new GridBagLayout() );
        //        setLayout( new VerticalBagLayout( ) );
        //        setLayout( new GridLayout( 0, 1 ) );
        //        setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        //        setLayout( new BasicSplitPaneUI.BasicVerticalLayoutManager() );
        //        GridBagConstraints gbc=new GridBagConstraints( );
        //        gbc.gridheight=8;
        //        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        //        setLayout( new GridLayout( 8,1) );

        JPanel filePanel = makeFilePanel();
        JPanel circuitPanel = makeCircuitPanel();

        //        JButton clear = new JButton( "Clear" );
        //        clear.addActionListener( new ActionListener() {
        //            public void actionPerformed( ActionEvent e ) {
        //                int answer = JOptionPane.showConfirmDialog( module.getApparatusPanel(), "Delete the entire circuit and start over?" );
        //                if( answer == JOptionPane.OK_OPTION ) {
        //                    module.clear();
        //                }
        //            }
        //        } );
        //        filePanel.setAlignmentX( Component.LEFT_ALIGNMENT );
        //
        //        JButton printKirkhoffsLaws = new JButton( "Show Equations" );
        //        printKirkhoffsLaws.addActionListener( new ActionListener() {
        //            public void actionPerformed( ActionEvent e ) {
        //                printEm();
        //            }
        //        } );
        //        final JCheckBox showReadouts = new JCheckBox( "Show Values" );
        //        showReadouts.addActionListener( new ActionListener() {
        //            public void actionPerformed( ActionEvent e ) {
        //                boolean r = showReadouts.isSelected();
        //                module.getCircuitGraphic().setReadoutMapVisible( r );
        //                module.getApparatusPanel().repaint();
        //            }
        //        } );
        //        JPanel circuitPanel = new JPanel();
        //        circuitPanel.setLayout( new BoxLayout( circuitPanel, BoxLayout.Y_AXIS ) );
        //        circuitPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Circuit" ) );
        //        circuitPanel.add( printKirkhoffsLaws );
        //        circuitPanel.add( clear );
        //        if( !module.isVirtualLabMode() ) {
        //            circuitPanel.add( showReadouts );
        //        }

        JRadioButton lifelike = new JRadioButton( "Lifelike", true );
        JRadioButton schematic = new JRadioButton( "Schematic", false );
        ButtonGroup bg = new ButtonGroup();
        bg.add( lifelike );
        bg.add( schematic );
        lifelike.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setLifelike( true );
            }
        } );
        schematic.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setLifelike( false );
            }
        } );

        JPanel visualizationPanel = new JPanel();
        visualizationPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Visual" ) );
        visualizationPanel.setLayout( new BoxLayout( visualizationPanel, BoxLayout.Y_AXIS ) );
        visualizationPanel.add( lifelike );
        visualizationPanel.add( schematic );

        //        add( virtualAmmeter );
        JPanel toolPanel = new JPanel();
        //        toolPanel.setLayout( new BoxLayout( toolPanel, BoxLayout.Y_AXIS ) );
        toolPanel.setLayout( new GridBagLayout() );
        GridBagConstraints lhs = new GridBagConstraints( 1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        GridBagConstraints rhs = new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );

        ImageIcon voltIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/dvm-thumb.gif" ) );
        ImageIcon ammIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/va-thumb.gif" ) );
        final JCheckBox voltmeter = new JCheckBox( "Voltmeter", false );
        voltmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVoltmeterVisible( voltmeter.isSelected() );
                module.getApparatusPanel().repaint();
            }
        } );
        //        voltmeter.add( new JLabel(voltIcon));
        //        JPanel vm = new JPanel();
        //        vm.setLayout( new GridBagLayout() );
        //        GridBagConstraints gbc=new GridBagConstraints( );
        //        gbc.gridwidth=2;
        //        gbc.gridheight=1;
        //        gbc.gridx=0;
        //        gbc.gridy=1;
        //        vm.add
        //        vm.add( voltmeter );
        //        vm.add( new JLabel( voltIcon ) );

        final JCheckBox virtualAmmeter = new JCheckBox( "<html>Non-Contact<br>Ammeter</html>", false );
        virtualAmmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVirtualAmmeterVisible( virtualAmmeter.isSelected() );
            }
        } );
        final JCheckBox seriesAmmeter = new JCheckBox( "Ammeter(s)", false );
        seriesAmmeter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setSeriesAmmeterVisible( seriesAmmeter.isSelected() );
            }
        } );

        toolPanel.add( seriesAmmeter, rhs );
        rhs.gridy++;
        toolPanel.add( voltmeter, rhs );
        rhs.gridy++;
        if( !module.isVirtualLabMode() ) {
            toolPanel.add( virtualAmmeter, rhs );
        }
        lhs.gridy = 0;
        toolPanel.add( new JLabel( ammIcon ), lhs );
        lhs.gridy++;
        toolPanel.add( new JLabel( voltIcon ), lhs );

        toolPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Tools" ) );

        final JSpinner zoom = new JSpinner( new SpinnerNumberModel( 1, .1, 10, .1 ) );
        zoom.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Number value = (Number)zoom.getValue();
                double v = value.doubleValue();
                zoom( v );
            }
        } );
        zoom.setSize( 50, zoom.getPreferredSize().height );
        zoom.setPreferredSize( new Dimension( 50, zoom.getPreferredSize().height ) );
        //        add( zoom );

        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout( new BoxLayout( zoomPanel, BoxLayout.Y_AXIS ) );
        zoomPanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "Size" ) );
        ButtonGroup zoomGroup = new ButtonGroup();
        JRadioButton small = new JRadioButton( "Small" );
        small.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( 2 );
            }
        } );
        JRadioButton medium = new JRadioButton( "Medium" );
        medium.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( 1 );
            }
        } );
        JRadioButton large = new JRadioButton( "Large" );
        large.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( .5 );
            }
        } );
        medium.setSelected( true );
        zoomGroup.add( large );
        zoomGroup.add( medium );
        zoomGroup.add( small );
        zoomPanel.add( large );
        zoomPanel.add( medium );
        zoomPanel.add( small );

        JButton jb = new JButton( "Local Help" );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showHelpImage();
            }
        } );
        //        add( jb );
        JButton browserGIF = new JButton( "GIF Help" );
        browserGIF.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                showHelpGIF();
            }
        } );
        //        add( browserGIF );
        //        add( new JSeparator() );

        HelpPanel hp = new HelpPanel( module );
        //        hp.setBorder( BorderFactory.createRaisedBevelBorder() );


        JButton xml = new JButton( "Show XML" );
        xml.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCircuit().toXML();
            }
        } );
        //        add( xml );

        JButton changeBunch = new JButton( "Change view 200x" );
        changeBunch.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int IMAX = 200;
                for( int i = 0; i < IMAX; i++ ) {
                    System.out.println( "i = " + i + "/" + IMAX );
                    module.setLifelike( !module.getCircuitGraphic().isLifelike() );
                }
            }
        } );
        //        add( changeBunch );
        JButton manyComp = new JButton( "add 100 batteries" );
        final Random rand = new Random( 0 );
        manyComp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                for( int i = 0; i < 100; i++ ) {
                    double x1 = rand.nextDouble() * 10;
                    double y1 = rand.nextDouble() * 10;
                    Battery batt = new Battery( new Point2D.Double( x1, y1 ), new ImmutableVector2D.Double( 1, 0 ),
                                                CCK3Module.BATTERY_DIMENSION.getLength(), CCK3Module.BATTERY_DIMENSION.getHeight(), module.getKirkhoffListener() );
                    module.getCircuit().addBranch( batt );
                    module.getCircuitGraphic().addGraphic( batt );
                    System.out.println( "i = " + i );
                }
                module.relayout( module.getCircuit().getBranches() );
            }
        } );
        //        add( manyComp );

        this.setLayout( new BorderLayout() );
        add( titleLabel, BorderLayout.NORTH );
        JPanel controlPanel = new JPanel( new GridBagLayout() );

        GridBagConstraints constraints = new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        controlPanel.add( filePanel, constraints );
        constraints.gridy++;
        controlPanel.add( circuitPanel, constraints );
        constraints.gridy++;
        controlPanel.add( visualizationPanel, constraints );
        constraints.gridy++;
        controlPanel.add( toolPanel, constraints );
        constraints.gridy++;
        controlPanel.add( zoomPanel, constraints );
        constraints.gridy++;

        add( controlPanel, BorderLayout.CENTER );
        JPanel helpPanel = new JPanel();
        helpPanel.add( hp );
        this.add( helpPanel, BorderLayout.SOUTH );
    }

    public class GridBagLayoutHelper extends GridBagConstraints {
        public GridBagLayoutHelper() {
        }
    }

    private void load() throws IOException, XMLException {
        ServiceSource ss = new ServiceSource();
        FileOpenService fos = ss.getFileOpenService( module.getApparatusPanel() );
        FileContents open = fos.openFileDialog( "Open Which CCK File?", new String[]{"cck"} );
        if( open == null ) {
            return;
        }
        InputStreamReader isr = new InputStreamReader( open.getInputStream() );
        BufferedReader br = new BufferedReader( isr );
        String str = "";
        while( br.ready() ) {
            String read = br.readLine();
            System.out.println( "read = " + read );
            str += read;
        }
        IXMLParser parser = new StdXMLParser();
        parser.setReader( new StdXMLReader( new StringReader( str ) ) );
        parser.setBuilder( new StdXMLBuilder() );
        parser.setValidator( new NonValidator() );

        IXMLElement parsed = (IXMLElement)parser.parse();
        Circuit circuit = Circuit.parseXML( parsed, module.getKirkhoffListener(), module );
        module.setCircuit( circuit );
    }

    private void save() throws IOException {
        ServiceSource ss = new ServiceSource();
        FileSaveService fos = ss.getFileSaveService( module.getApparatusPanel() );

        XMLElement xml = module.getCircuit().toXML();
        StringWriter sw = new StringWriter();
        XMLWriter writer = new XMLWriter( sw );
        writer.write( xml );
        String circuitxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sw.toString();
        InputStream stream = new ByteArrayInputStream( circuitxml.getBytes() );
        FileContents data = new InputStreamFileContents( "circuitxml", stream );
        FileContents out = fos.saveAsFileDialog( "circuit.cck", new String[]{"cck"}, data );
        System.out.println( "out = " + out );
    }

    public void showHelpGIF() {
        ServiceSource ss = new ServiceSource();
        BasicService bs = ss.getBasicService();
        //        URL url=getClass().getClassLoader().getResource( "cck.pdf");
        URL url = null;
        try {
            //            url = new URL( "http://www.colorado.edu/physics/phet/projects/cck/cck.pdf" );
            url = new URL( "http://www.colorado.edu/physics/phet/projects/cck/v8/cck-help.gif" );
            System.out.println( "url = " + url );
            bs.showDocument( url );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    private void download() {
        DownloadService ds = null;
        try {
            ds = (DownloadService)ServiceManager.lookup( "javax.jnlp.DownloadService" );
        }
        catch( UnavailableServiceException e ) {
            ds = null;
        }

        if( ds != null ) {

            try {

                // determine if a particular resource is cached
                final URL url =
                        new URL( "http://www.colorado.edu/physics/phet/projects/cck/v8/help.jar" );
                //                        new URL( "http://www.colorado.edu/physics/phet/projects/cck/v8/help.jar" );
                boolean cached = ds.isResourceCached( url, "1.0" );
                System.out.println( "cached = " + cached );
                // remove the resource from the cache
                if( cached ) {
                    System.out.println( "Removing." );
                    ds.removeResource( url, "1.0" );
                }
                // reload the resource into the cache

                final DownloadServiceListener dsl = ds.getDefaultProgressWindow();
                DownloadServiceListener mydsl = new DownloadServiceListener() {
                    public void downloadFailed( URL url, String s ) {
                        dsl.downloadFailed( url, s );
                        System.out.println( "Failed." );
                    }

                    public void progress( URL url, String s, long l, long l1, int i ) {
                        dsl.progress( url, s, l, l1, i );
                        System.out.println( "progress" );
                    }

                    public void upgradingArchive( URL url, String s, int i, int i1 ) {
                        dsl.upgradingArchive( url, s, i, i1 );
                        System.out.println( "upgrading" );
                    }

                    public void validating( URL url, String s, long l, long l1, int i ) {
                        dsl.validating( url, s, l, l1, i );
                        System.out.println( "validating." );
                    }
                };
                System.out.println( "Calling loadResource" );
                ds.loadResource( url, "1.0", mydsl );
                System.out.println( "Finished calling loadresource." );
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
        //        }
    }

    public void showHelpImage() {

        final JFrame imageFrame = new JFrame();
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/cck-help.gif" );
            JLabel label = new JLabel( new ImageIcon( image ) );
            imageFrame.setContentPane( label );
            imageFrame.pack();
            GraphicsUtil.centerWindowOnScreen( imageFrame );
            imageFrame.setVisible( true );
            imageFrame.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent e ) {
                    imageFrame.dispose();
                }
            } );
            imageFrame.setResizable( false );
        }

        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void zoom( double scale ) {
        module.setZoom( scale );
    }

    private void printEm() {
        KirkhoffSolver ks = new KirkhoffSolver();
        Circuit circuit = module.getCircuit();
        KirkhoffSolver.MatrixTable mt = new KirkhoffSolver.MatrixTable( circuit );
        System.out.println( "mt = " + mt );

        KirkhoffSolver.Equation[] junctionEquations = ks.getJunctionEquations( circuit, mt );
        KirkhoffSolver.Equation[] loopEquations = ks.getLoopEquations( circuit, mt );
        KirkhoffSolver.Equation[] ohmsLaws = ks.getOhmsLaw( circuit, mt );

        String je = mt.describe( junctionEquations, "Junction Equations" );
        String le = mt.describe( loopEquations, "Loop Equations" );
        String oh = mt.describe( ohmsLaws, "Ohms Law Equations" );
        System.out.println( je );
        System.out.println( le );
        System.out.println( oh );

        JFrame readoutFrame = new JFrame();
        JTextArea jta = new JTextArea( je + "\n" + le + "\n" + oh + "\n" ) {
            protected void paintComponent( Graphics g ) {
                GraphicsUtil.setAntiAliasingOn( (Graphics2D)g );
                super.paintComponent( g );
            }
        };
        jta.setEditable( false );
        jta.setFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
        readoutFrame.setContentPane( jta );
        readoutFrame.pack();
        GraphicsUtil.centerWindowOnScreen( readoutFrame );
        readoutFrame.setVisible( true );
    }

    private JPanel makeFilePanel() {
        //        JPanel filePanel = new JPanel();
        //        filePanel.setLayout( new BoxLayout( filePanel, BoxLayout.LINE_AXIS ) );//X_AXIS ) );


        JButton save = new JButton( "Save" );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    save();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        JButton load = new JButton( "Load" );
        load.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    load();
                }
                catch( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        //        filePanel.add( save );
        //        filePanel.add( load );

        JPanel filePanelContents = new JPanel();
        filePanelContents.add( save );
        filePanelContents.add( load );

        //        JPanel filePanel = createJPanel( "File", filePanelContents );
        //        filePanel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), "File" ) );
        //        filePanel.setLayout( new GridBagLayout() );
        //                GraphicsUtil.addGridBagComponent( filePanel, Box.createRigidArea( new Dimension( ) ), );
        //        Insets insets = new Insets( 0, 10, 0, 10 );

        //        GraphicsUtil.addGridBagComponent( filePanel, filePanelContents, 0, 0, 1, 1, GridBagConstraints.NONE, GridBagConstraints.CENTER, insets );
        return placeInPanel( "File", filePanelContents, new Insets( 0, 10, 0, 10 ) );
    }

    private static JPanel placeInPanel( String title, JPanel contents, Insets insets ) {
        GridBagConstraints constraints = new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0 );
        JPanel panel = new JPanel( new GridBagLayout() );
        panel.setBorder( BorderFactory.createTitledBorder( BorderFactory.createRaisedBevelBorder(), title ) );
        panel.add( contents, constraints );
        return panel;
    }

    private JPanel makeCircuitPanel() {
        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int answer = JOptionPane.showConfirmDialog( module.getApparatusPanel(), "Delete the entire circuit and start over?" );
                if( answer == JOptionPane.OK_OPTION ) {
                    module.clear();
                }
            }
        } );

        JButton printKirkhoffsLaws = new JButton( "Show Equations" );
        printKirkhoffsLaws.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                printEm();
            }
        } );
        final JCheckBox showReadouts = new JCheckBox( "Show Values" );
        showReadouts.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean r = showReadouts.isSelected();
                module.getCircuitGraphic().setReadoutMapVisible( r );
                module.getApparatusPanel().repaint();
            }
        } );
        JPanel circuitPanel = new JPanel();
        circuitPanel.setLayout( new BoxLayout( circuitPanel, BoxLayout.Y_AXIS ) );
        circuitPanel.add( printKirkhoffsLaws );
        circuitPanel.add( clear );
        if( !module.isVirtualLabMode() ) {
            circuitPanel.add( showReadouts );
        }
        return placeInPanel( "Circuit", circuitPanel, new Insets( 0, 10, 0, 10 ) );
    }
}

//        super.showMegaHelp();
//        ServiceSource ss = new ServiceSource();
//        BasicService bs = ss.getBasicService();
////        URL url=getClass().getClassLoader().getResource( "cck.pdf");
//        URL url = null;
//        try {
//            url = new URL( "http://www.colorado.edu/physics/phet/projects/cck/cck.pdf" );
////            url = new URL( "http://www.colorado.edu/physics/phet/projects/cck/cck-help.gif" );
//            System.out.println( "url = " + url );
//            bs.showDocument( url );
//        }
//        catch( MalformedURLException e ) {
//            e.printStackTrace();
//        }