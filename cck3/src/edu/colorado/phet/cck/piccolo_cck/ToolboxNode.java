package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKLookAndFeel;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.*;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 5:12:28 PM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class ToolboxNode extends PhetPNode {
    private PPath toolboxBounds;
    private PhetPCanvas canvas;
    private CCKModel model;
    private ArrayList branchMakers = new ArrayList();
    private static final int TOP_INSET = 30;
    private static final double BETWEEN_INSET = 6;
    private CircuitInteractionModel circuitInteractionModel;
    private ICCKModule module;
    private AmmeterMaker ammeterMaker;
    private ToolboxNode.WireMaker wireMaker;

    public ToolboxNode( PhetPCanvas canvas, CCKModel model, ICCKModule module ) {
        this.module = module;
        this.canvas = canvas;
        this.model = model;
        this.circuitInteractionModel = new CircuitInteractionModel( model.getCircuit() );
        this.toolboxBounds = new PPath( new Rectangle( 100, 100 ) );
        toolboxBounds.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ) );
        toolboxBounds.setPaint( CCKLookAndFeel.toolboxColor );
        addChild( toolboxBounds );

        wireMaker = new WireMaker();
        addBranchMaker( wireMaker );
        addBranchMaker( new ResistorMaker() );
        addBranchMaker( new BatteryMaker() );
        addBranchMaker( new BulbMaker() );
        addBranchMaker( new SwitchMaker() );
        addBranchMaker( new ACVoltageMaker() );
        addBranchMaker( new CapacitorMaker() );
        addBranchMaker( new InductorMaker() );
        ammeterMaker = new AmmeterMaker();
        addBranchMaker( ammeterMaker );

        toolboxBounds.setPathTo( new Rectangle2D.Double( 0, 0, 100, getYForNextItem( new ResistorMaker() ) ) );
        setSeriesAmmeterVisible( false );
    }

    private void addBranchMaker( BranchMaker branchMaker ) {
        double y = getYForNextItem( branchMaker );
        branchMaker.setOffset( toolboxBounds.getFullBounds().getCenterX() - branchMaker.getFullBounds().getWidth() / 2 - branchMaker.getFullBounds().getMinX(), y );
        addChild( branchMaker );
        branchMakers.add( branchMaker );
    }

    private double getYForNextItem( BranchMaker nextItem ) {
        if( branchMakers.size() == 0 ) {
            return TOP_INSET;
        }
        else {
            BranchMaker prev = (BranchMaker)branchMakers.get( branchMakers.size() - 1 );
            double val = prev.getFullBounds().getMaxY() + BETWEEN_INSET;
            if( nextItem.getFullBounds().getMinY() < 0 ) {
                val -= nextItem.getFullBounds().getMinY();
            }
            return val;
        }
    }

    public void setBackground( Paint paint ) {
        toolboxBounds.setPaint( paint );
    }

    public Color getBackgroundColor() {
        return (Color)toolboxBounds.getPaint();
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
        ammeterMaker.setVisible( selected );
    }

    abstract class BranchMaker extends PComposite {//tricky way of circumventing children's behaviors
        private PText label;
        private Branch createdBranch;

        public BranchMaker( String name ) {
            label = new PText( name );
            label.setFont( createFont() );
//            label.setFont( new Font( "Lucida Sans", Font.BOLD, 12 ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    // If we haven't created the branch yet
                    if( createdBranch == null ) {
                        createdBranch = createBranch();
                        // Position the branch so it's centered on the mouse event.
                        setBranchLocationFromEvent( event );
                        model.getCircuit().addBranch( createdBranch );
                        model.getCircuit().setSelection( createdBranch );
                    }
                    circuitInteractionModel.translate( createdBranch, getWorldLocation( event ) );
                }

                public void mouseReleased( PInputEvent event ) {
                    circuitInteractionModel.dropBranch( createdBranch );
                    createdBranch = null;
                }
            } );
        }

        public void setVisible( boolean isVisible ) {
            super.setVisible( isVisible );
            setPickable( isVisible );
            setChildrenPickable( isVisible );
        }

        private Font createFont() {
            return Toolkit.getDefaultToolkit().getScreenSize().width <= 1024 ? new Font( "Lucida Sans", Font.PLAIN, 16 ) : new Font( "Lucida Sans", Font.PLAIN, 12 );
        }

        public Point2D getWorldLocation( PInputEvent event ) {
            return event.getPositionRelativeTo( ToolboxNode.this.getParent() );
        }

        //This assumes the branch is always centered on the mouse.
        private void setBranchLocationFromEvent( PInputEvent event ) {
            Point2D location = getWorldLocation( event );
            double dx = createdBranch.getEndPoint().getX() - createdBranch.getStartPoint().getX();
            double dy = createdBranch.getEndPoint().getY() - createdBranch.getStartPoint().getY();
            createdBranch.getStartJunction().setPosition( location.getX() - dx / 2,
                                                          location.getY() - dy / 2 );
            createdBranch.getEndJunction().setPosition( location.getX() + dx / 2,
                                                        location.getY() + dy / 2 );
        }

        protected abstract Branch createBranch();

        public void setDisplayGraphic( PNode child ) {
            addChild( child );
            addChild( label );
            double labelInsetDY = 4;
            label.setOffset( child.getFullBounds().getWidth() / 2 - label.getFullBounds().getWidth() / 2, child.getFullBounds().getMaxY() + labelInsetDY );
        }
    }

    class WireMaker extends BranchMaker {
        public WireMaker() {
            super( "Wire" );
            WireNode child = new WireNode( model, createWire(), canvas );
            child.scale( 40 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        private Wire createWire() {
            return new Wire( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1.5, 0 ) );
        }

        protected Branch createBranch() {
            return createWire();
        }
    }

    class ResistorMaker extends BranchMaker {
        public ResistorMaker() {
            super( "Resistor" );
            ComponentNode child = new ResistorNode( model, createResistor(), canvas, module );
            child.scale( 60 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createResistor();
        }

        private Resistor createResistor() {
            double L = CCKModel.RESISTOR_DIMENSION.getLength() * 1.3 * 1.3;
            double H = CCKModel.RESISTOR_DIMENSION.getHeight() * 1.3 * 1.3;
            Resistor resistor = new Resistor( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( L, 0 ), L, H );
            resistor.setResistance( 10.0 );
            return resistor;
        }
    }

    class BatteryMaker extends BranchMaker {
        public BatteryMaker() {
            super( "Battery" );
            ComponentNode child = new BatteryNode( model, createBattery(), canvas, module );
            child.scale( 45 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createBattery();
        }

        private Battery createBattery() {
            double L = 1.6;
            return new Battery( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( L, 0 ), L, 1, 0.01, true );
        }
    }

    class BulbMaker extends BranchMaker {
        public BulbMaker() {
            super( "Light Bulb" );
            PNode child = new BulbNode( createBulb() );
            child.transformBy( AffineTransform.getScaleInstance( 50, 75 ) );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createBulb();
        }

        private Bulb createBulb() {
            Bulb dummyBulb = new Bulb( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), 1, 1, 0.01, true );
            double tilt = BulbComponentNode.getTiltValue( dummyBulb ) - 0.3;
            Bulb bulb = new Bulb( new Point(), Vector2D.Double.parseAngleAndMagnitude( 1, -tilt - Math.PI / 2 ), 0.43, 1, 1, model.getCircuitChangeListener() );
            bulb.flip( null );
            return bulb;
        }
    }

    class SwitchMaker extends BranchMaker {
        public SwitchMaker() {
            super( "Switch" );
            SwitchNode child = new SwitchNode( model, createSwitch(), canvas );
            child.scale( 60 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createSwitch();
        }

        private Switch createSwitch() {
            return new Switch( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), false, 1, 1 );
        }
    }

    class ACVoltageMaker extends BranchMaker {
        public ACVoltageMaker() {
            super( "AC Voltage" );
            ACVoltageSourceNode child = new ACVoltageSourceNode( model, createSwitch(), canvas, module );
            child.scale( 60 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createSwitch();
        }

        private ACVoltageSource createSwitch() {
            return new ACVoltageSource( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), 1, 1, 0.01, false );
        }
    }

    class CapacitorMaker extends BranchMaker {
        public CapacitorMaker() {
            super( "Capacitor" );
            CapacitorNode child = new CapacitorNode( model, createCapacitor(), canvas, module );
            child.scale( 60 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createCapacitor();
        }

        private Capacitor createCapacitor() {
            return new Capacitor( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), 1, 1 );
        }
    }

    class InductorMaker extends BranchMaker {
        public InductorMaker() {
            super( "Inductor" );
            InductorNode child = new InductorNode( model, createInductor(), canvas, module );
            child.scale( 42 );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createInductor();
        }

        private Inductor createInductor() {
            return new Inductor( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( 1, 0 ), 1, 0.4 );
        }
    }

    class AmmeterMaker extends BranchMaker {
        public AmmeterMaker() {
            super( "Ammeter" );
            SeriesAmmeterNode child = new SeriesAmmeterNode( canvas, createAmmeter(), module );
            child.scale( 30.0 );//todo choose scale based on insets?
//            child.scale( );//todo choose scale based on insets?
            setDisplayGraphic( child );
        }

        protected Branch createBranch() {
            return createAmmeter();
        }

        private SeriesAmmeter createAmmeter() {
            double L = 2.0;
            return new SeriesAmmeter( model.getCircuitChangeListener(), new Junction( 0, 0 ), new Junction( L, 0 ), L, 0.6 );
        }
    }

    public WireMaker getWireMaker() {
        return wireMaker;
    }
}
