/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.ComponentDimension;
import edu.colorado.phet.cck3.circuit.components.*;
import edu.colorado.phet.cck3.circuit.particles.ParticleSetGraphic;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.HashedImageLoader;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 10:17:59 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class CircuitGraphic extends CompositeGraphic {
    static double junctionRadius = CCK3Module.JUNCTION_RADIUS;
    public static final Color COPPER = new Color( Integer.parseInt( "D98719", 16 ) );//new Color(214, 18, 34);
    private CompositeGraphic filamentLayer = new CompositeGraphic();
    private CompositeGraphic branches = new CompositeGraphic();
    private CompositeGraphic fires = new CompositeGraphic();
    private CompositeGraphic leverLayer = new CompositeGraphic();
    private CompositeGraphic junctionLayer = new CompositeGraphic();
    private ParticleSetGraphic particleSetGraphic;
    private CompositeGraphic ammeterTopLayer = new CompositeGraphic();
    private CompositeGraphic readouts = new CompositeGraphic();

    private Circuit circuit;
    private ModelViewTransform2D transform;
    private ApparatusPanel apparatusPanel;
    private double STICKY_THRESHOLD = CCK3Module.STICKY_THRESHOLD;
    private CCK3Module module;
    private ArrayList listeners = new ArrayList();
    private Hashtable readoutMap = new Hashtable();
    private boolean lifelike = true;
    private GraphicSource graphicSource;
    private boolean readoutGraphicsVisible = false;
    private HashedImageLoader hashedImageLoader = new HashedImageLoader();

    public CircuitGraphic( final CCK3Module module ) throws IOException {
        graphicSource = new Lifelike();
        lifelike = true;
        this.module = module;
        this.circuit = module.getCircuit();
        particleSetGraphic = new ParticleSetGraphic( module );
        this.transform = module.getTransform();
        this.apparatusPanel = module.getApparatusPanel();

        Graphic solderGraphic = new Graphic() {
            public void paint( Graphics2D g ) {
                if( isLifelike() ) {
                    g.setColor( COPPER );
                }
                else {
                    g.setColor( Color.black );
                }
                Graphic[] gr = junctionLayer.getGraphics();
                for( int i = 0; i < gr.length; i++ ) {
                    HasJunctionGraphic ho = (HasJunctionGraphic)gr[i];
//                    ho.getJunctionGraphic().getJunction().notifyObservers();
                    Junction jo = ho.getJunctionGraphic().getJunction();
                    Branch[] n = circuit.getAdjacentBranches( jo );
                    boolean onlyBranches = true;
                    for( int j = 0; j < n.length; j++ ) {
                        Branch branch = n[j];
                        if( !branch.getClass().getName().equals( Branch.class.getName() ) ) {
                            onlyBranches = false;
                            break;
                        }
                    }
                    if( onlyBranches && n.length > 1 ) {
                        Shape shape = ho.getJunctionGraphic().getShape();
                        g.fill( shape );
                    }
                }
            }
        };
        addGraphic( solderGraphic );

        addGraphic( branches );
        addGraphic( fires );
        addGraphic( filamentLayer );
        addGraphic( leverLayer );

        addGraphic( junctionLayer );
        addGraphic( particleSetGraphic );
        addGraphic( ammeterTopLayer );
        addGraphic( readouts );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                Graphic[] g = junctionLayer.getGraphics();
                for( int i = 0; i < g.length; i++ ) {
                    HasJunctionGraphic ho = (HasJunctionGraphic)g[i];
                    ho.getJunctionGraphic().getJunction().notifyObservers();
                }
            }
        } );
        addCircuitGraphicListener( new CircuitGraphicListener() {
            public void graphicAdded( Branch branch, InteractiveGraphic graphic ) {
                if( branch instanceof CircuitComponent ) {
                    ReadoutGraphic rg = null;
                    if( branch instanceof Battery ) {
                        rg = new ReadoutGraphic.BatteryReadout( branch, transform, module.getApparatusPanel(), readoutGraphicsVisible );
                    }
                    else if( branch instanceof SeriesAmmeter ) {
                        rg = null;
                    }
                    else {
                        rg = new ReadoutGraphic( branch, transform, module.getApparatusPanel(), readoutGraphicsVisible );
                    }
                    if( rg != null ) {
                        readoutMap.put( branch, rg );
                        readouts.addGraphic( rg );
                    }
                }
            }

            public void graphicRemoved( Branch branch, InteractiveGraphic graphic ) {
                ReadoutGraphic rg = (ReadoutGraphic)readoutMap.get( branch );
                if( rg != null ) {
                    readouts.removeGraphic( rg );
                }
            }
        } );
    }

    public void setReadoutMapVisible( boolean visible ) {
        this.readoutGraphicsVisible = visible;
        Collection values = readoutMap.values();
        for( Iterator iterator = values.iterator(); iterator.hasNext(); ) {
            ReadoutGraphic readoutGraphic = (ReadoutGraphic)iterator.next();
            readoutGraphic.setVisible( visible );
        }
    }

    public void addCircuitGraphicListener( CircuitGraphicListener cgl ) {
        listeners.add( cgl );
    }

    public String toString() {
        return "CircuitGraphic, branches=" + branches + ", junctionLayer=" + junctionLayer;
    }

    public void addGraphic( Branch b ) {
        graphicSource.addGraphic( b );
        InteractiveGraphic g = getGraphic( b );
        fireGraphicAdded( b, g );
    }

    static class AmmeterTopGraphic implements Graphic {
        private SeriesAmmeter sam;
        Graphic graphic;

        public AmmeterTopGraphic( SeriesAmmeter sam, Graphic graphic ) {
            this.sam = sam;
            this.graphic = graphic;
        }

        public void paint( Graphics2D g ) {
            graphic.paint( g );
        }

        public SeriesAmmeter getSeriesAmmeter() {
            return sam;
        }

        public void delete() {
            //nothing to do.
        }
    }

    private void fireGraphicAdded( Branch branch, InteractiveGraphic graphic ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitGraphicListener circuitGraphicListener = (CircuitGraphicListener)listeners.get( i );
            circuitGraphicListener.graphicAdded( branch, graphic );
        }
    }

    private void addGraphic( CircuitComponent component, IComponentGraphic ccbg ) {
        TotalComponentGraphic tcg = new TotalComponentGraphic( this, component, apparatusPanel, transform,
                                                               ccbg, junctionRadius, module );
        branches.addGraphic( tcg.getInteractiveBranchGraphic(), 1 );
        if( getGraphic( component.getStartJunction() ) == null ) {
            junctionLayer.addGraphic( tcg.getInteractiveJunctionGraphic1() );
        }
        if( getGraphic( component.getEndJunction() ) == null ) {
            junctionLayer.addGraphic( tcg.getInteractiveJunctionGraphic2() );
        }
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public void removeGraphic( Junction j ) {
        Graphic[] g = junctionLayer.getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            HasJunctionGraphic ho = (HasJunctionGraphic)g[i];
            if( ho.getJunctionGraphic().getJunction() == j ) {
                junctionLayer.removeGraphic( ho );
                ho.delete();
            }
        }
    }

    public HasJunctionGraphic getGraphic( Junction j ) {
        Graphic[] g = junctionLayer.getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            HasJunctionGraphic ho = (HasJunctionGraphic)g[i];
            if( ho.getJunctionGraphic().getJunction() == j ) {
                return ho;
            }
        }
        return null;
    }

    public boolean wouldConnectionCauseOverlappingBranches( Junction a, Junction b ) {
        Junction[] neighborsOfA = getCircuit().getNeighbors( a );
        Junction[] neighborsOfB = getCircuit().getNeighbors( b );
        for( int i = 0; i < neighborsOfA.length; i++ ) {
            Junction junction = neighborsOfA[i];
            for( int j = 0; j < neighborsOfB.length; j++ ) {
                Junction junction1 = neighborsOfB[j];
                if( junction == junction1 ) {
                    return true;
                }
            }
        }
        return false;
    }

    public Junction getBestDragMatch( Junction dragging, Point2D loc ) {
        Junction closestJunction = null;
        double closestValue = Double.POSITIVE_INFINITY;
        for( int i = 0; i < getCircuit().numJunctions(); i++ ) {
            Junction j = getCircuit().junctionAt( i );
            double dist = loc.distance( j.getPosition() );
            if( j != dragging && !getCircuit().hasBranch( dragging, j ) &&
                !wouldConnectionCauseOverlappingBranches( dragging, j ) ) {
                if( closestJunction == null || dist < closestValue ) {
                    if( dist <= STICKY_THRESHOLD ) {
                        closestValue = dist;
                        closestJunction = j;
                    }
                }
            }
        }
        return closestJunction;
    }

    public void convertToComponentGraphic( Junction junction, CircuitComponent branch ) {
//        InteractiveWireJunctionGraphic j = new InteractiveWireJunctionGraphic( this, new JunctionGraphic( apparatusPanel, junction, getTransform(), junctionRadius ), getTransform(), module );
        JunctionGraphic jg = new JunctionGraphic( apparatusPanel, junction, getTransform(), junctionRadius );
        InteractiveComponentJunctionGraphic ij = new InteractiveComponentJunctionGraphic( this, jg, branch, module );
        removeGraphic( junction );
        junctionLayer.addGraphic( ij );
    }

    public void collapseJunctions( Junction j1, Junction j2 ) {
        remove( j1 );
        remove( j2 );
        Junction replacement = new Junction( j1.getX(), j1.getY() );
        InteractiveWireJunctionGraphic ij = newJunctionGraphic( replacement );
        circuit.addJunction( replacement );
        circuit.replaceJunction( j1, replacement );
        circuit.replaceJunction( j2, replacement );

        circuit.fireKirkhoffChanged();
        junctionLayer.addGraphic( ij );
    }

    private void remove( Junction junction ) {
        removeGraphic( junction );
        circuit.remove( junction );
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public InteractiveWireJunctionGraphic newJunctionGraphic( Junction junction ) {
        InteractiveWireJunctionGraphic j = new InteractiveWireJunctionGraphic( this, new JunctionGraphic( apparatusPanel, junction, getTransform(), junctionRadius ), getTransform(), module );
        return j;
    }

    public void split( Junction junction ) {
        Junction[] newJ = circuit.split( junction );
        ArrayList branches = new ArrayList();
        for( int i = 0; i < newJ.length; i++ ) {
            Junction junction1 = newJ[i];
            branches.addAll( Arrays.asList( circuit.getAdjacentBranches( junction1 ) ) );
        }
        Branch[] bo = (Branch[])branches.toArray( new Branch[0] );
        module.relayout( bo );
        for( int i = 0; i < newJ.length; i++ ) {
            Junction junction1 = newJ[i];
            Branch connection = circuit.getAdjacentBranches( junction1 )[0];
            if( connection instanceof CircuitComponent ) {
                InteractiveComponentJunctionGraphic j = new InteractiveComponentJunctionGraphic( this, new JunctionGraphic( apparatusPanel, junction1, getTransform(), junctionRadius ), (CircuitComponent)connection, module );
                junctionLayer.addGraphic( j );
            }
            else {
                InteractiveWireJunctionGraphic j = newJunctionGraphic( junction1 );
                junctionLayer.addGraphic( j );
            }
        }
        removeGraphic( junction );
        circuit.fireKirkhoffChanged();
    }

    public Graphic[] getBranchGraphics() {
        return branches.getGraphics();
    }

    public InteractiveGraphic getGraphic( Branch b ) {
        Graphic[] g = branches.getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            Graphic graphic = g[i];
            if( graphic instanceof InteractiveBranchGraphic ) {
                InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)graphic;
                if( ibg.getBranch() == b ) {
                    return ibg;
                }

            }
            else if( graphic instanceof CircuitComponentInteractiveGraphic ) {
                CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)graphic;
                if( ccig.getBranch() == b ) {
                    return ccig;
                }
            }
            else {
                throw new RuntimeException( "Unknown type.=" + g[i].getClass() );
            }
        }
        return null;
    }

    public CCK3Module getModule() {
        return module;
    }

    public void removeGraphic( Branch branch ) {
        InteractiveGraphic g = getGraphic( branch );
        branches.removeGraphic( g );
        if( !( g instanceof Deletable ) ) {
            throw new RuntimeException( g.getClass().getName() + " does not implement Deletable" );
        }
        ( (Deletable)g ).delete();
        Graphic[] fire = fires.getGraphics();
        for( int i = 0; i < fire.length; i++ ) {
            FlameGraphic graphic = (FlameGraphic)fire[i];
            if( graphic.getBranch() == branch ) {
                fires.removeGraphic( graphic );
                graphic.delete();
            }
        }

        fireGraphicRemoved( branch, g );
        if( branch instanceof Switch ) {
            Graphic[] levers = leverLayer.getGraphics();
            for( int i = 0; i < levers.length; i++ ) {
                if( levers[i] instanceof LeverInteraction ) {
                    LeverInteraction lever = (LeverInteraction)levers[i];
                    if( lever.getComponent() == branch ) {
                        leverLayer.removeGraphic( lever );
                        //hopefully only one.
                        lever.delete();
                    }
                }
            }
        }
        else if( branch instanceof SeriesAmmeter ) {
            Graphic[] gr = ammeterTopLayer.getGraphics();
            for( int i = 0; i < gr.length; i++ ) {
                AmmeterTopGraphic graphic = (AmmeterTopGraphic)gr[i];
                if( graphic.getSeriesAmmeter() == branch ) {
                    ammeterTopLayer.removeGraphic( graphic );
                    graphic.delete();
                }
            }
        }
        else if( branch instanceof Bulb ) {
            Graphic[] gr = filamentLayer.getGraphics();
            Bulb bulb = (Bulb)branch;
            for( int i = 0; i < gr.length; i++ ) {
                Graphic graphic = gr[i];
                FilamentGraphic fg = (FilamentGraphic)graphic;
                if( fg.getFilament() == bulb.getFilament() ) {
                    filamentLayer.removeGraphic( fg );
                    fg.delete();
                }
            }
        }
    }

    private void fireGraphicRemoved( Branch branch, InteractiveGraphic g ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitGraphicListener circuitGraphicListener = (CircuitGraphicListener)listeners.get( i );
            circuitGraphicListener.graphicRemoved( branch, g );
        }
    }

    public ParticleSetGraphic getParticleSetGraphic() {
        return particleSetGraphic;
    }

    public void setLifelike( boolean lifelike ) {
        if( this.lifelike == lifelike ) {
            return;
        }
        this.lifelike = lifelike;
        if( lifelike ) {
            this.graphicSource = new Lifelike();
        }
        else {
            this.graphicSource = new Schematic();
        }

        //clear out all the graphics, replace with the new ones.
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch br = circuit.branchAt( i );
            removeGraphic( br );
            removeGraphic( br.getStartJunction() );
            removeGraphic( br.getEndJunction() );
        }

        //have to preprocess to fix the bulb model.
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            if( branch instanceof Bulb ) {
                Bulb bulb = (Bulb)branch;
                bulb.setSchematic( !lifelike, circuit );
            }
        }

        for( int i = 0; i < circuit.numBranches(); i++ ) {
            addGraphic( circuit.branchAt( i ) );
        }
    }

    public boolean isLifelike() {
        return lifelike;
    }

    public FlameGraphic getFlameGraphic( Branch b ) {
        Graphic[] g = fires.getGraphics();
        for( int i = 0; i < g.length; i++ ) {
            Graphic graphic = g[i];
            FlameGraphic fg = (FlameGraphic)graphic;
            if( fg.getBranch() == b ) {
                return fg;
            }
        }
        return null;
    }

    public void setOnFire( Branch b, boolean onFire ) {
        if( onFire ) {
            if( getFlameGraphic( b ) != null ) {
                return;
            }
            else {
                if( b instanceof CircuitComponent ) {
                    CircuitComponent cc = (CircuitComponent)b;
                    Graphic graphic = getGraphic( b );
                    fires.addGraphic( createFlameGraphic( graphic, cc ) );
                }
            }
        }
        else {
            Graphic graphic = getFlameGraphic( b );
            fires.removeGraphic( graphic );
        }
    }

    private FlameGraphic createFlameGraphic( Graphic graphic, CircuitComponent b ) {
        if( b instanceof Bulb ) {
            return null;
        }
        else {
            try {
                return new FlameGraphic( apparatusPanel, b, hashedImageLoader.loadImage( "images/flame.gif" ), getTransform() );
            }
            catch( IOException e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }
    }

    public int numJunctionGraphics() {
        return junctionLayer.getGraphics().length;
    }

    interface GraphicSource {
        void addGraphic( Branch b );
    }

    class Lifelike implements GraphicSource {
        double wireThickness = .3;

        public void addGraphic( Branch b ) {
            if( b instanceof Battery ) {
                addBatteryGraphic( (Battery)b );
            }
            else if( b instanceof Bulb ) {
                addBulbGraphic( (Bulb)b );
            }
            else if( b instanceof Resistor ) {
                addResistorGraphic( (Resistor)b );
            }
            else if( b instanceof Switch ) {
                addSwitchGraphic( (Switch)b );
            }
            else if( b instanceof SeriesAmmeter ) {
                addSeriesAmmeterGraphic( (SeriesAmmeter)b );
            }
            else {
                addWireGraphic( b );
            }
        }

        private void addBatteryGraphic( Battery component ) {
            CircuitComponentImageGraphic ccbg = new CircuitComponentImageGraphic( module.getImageSuite().getLifelikeSuite().getBatteryImage(), apparatusPanel, component, transform );
            CircuitGraphic.this.addGraphic( component, ccbg );
        }

        private void addSeriesAmmeterGraphic( SeriesAmmeter seriesAmmeter ) {
            final SeriesAmmeterGraphic sag = new SeriesAmmeterGraphic( apparatusPanel, seriesAmmeter, getTransform(), module );
            CircuitGraphic.this.addGraphic( seriesAmmeter, sag );
            AmmeterTopGraphic atg = new AmmeterTopGraphic( seriesAmmeter, sag );
            ammeterTopLayer.addGraphic( atg );
        }

        private void addSwitchGraphic( Switch aSwitch ) {
            CircuitComponentImageGraphic switchGraphic = new CircuitComponentImageGraphic( module.getImageSuite().getKnifeBoardImage(), apparatusPanel,
                                                                                           aSwitch, getTransform() );
            CircuitGraphic.this.addGraphic( aSwitch, switchGraphic );
            BufferedImage lever = module.getImageSuite().getKnifeHandleImage();
            ComponentDimension leverDimension = CCK3Module.LEVER_DIMENSION;
            LeverGraphic leverGraphic = new LeverGraphic( switchGraphic, lever, apparatusPanel, getTransform(), leverDimension.getLength(), leverDimension.getHeight() );
            InteractiveLever interactiveLever = new InteractiveLever( transform, apparatusPanel, leverGraphic );
            leverLayer.addGraphic( interactiveLever );
        }

        private void addWireGraphic( Branch branch ) {
            TotalBranchGraphic totalBranchGraphic = new TotalBranchGraphic( CircuitGraphic.this, branch, apparatusPanel, transform, COPPER, junctionRadius, module, wireThickness );
            branches.addGraphic( totalBranchGraphic.getInteractiveBranchGraphic(), 0 );
            junctionLayer.addGraphic( totalBranchGraphic.getInteractiveJunctionGraphic1() );
            junctionLayer.addGraphic( totalBranchGraphic.getInteractiveJunctionGraphic2() );
        }

        private void addBulbGraphic( Bulb component ) {
            BulbComponentGraphic ccbg = new BulbComponentGraphic( apparatusPanel, component, transform, module );
            FilamentGraphic fg = new FilamentGraphic( component.getFilament(), transform, ccbg );
            component.setSchematic( false, getCircuit() );
            filamentLayer.addGraphic( fg );
            //TODO could also be coupled at the bulb layer
            CircuitGraphic.this.addGraphic( component, ccbg );
        }

        private void addResistorGraphic( Resistor component ) {
            ResistorGraphic ccbg = new ResistorGraphic( module.getImageSuite().getLifelikeSuite().getResistorImage(), apparatusPanel, component, transform );
            CircuitGraphic.this.addGraphic( component, ccbg );
        }
    }

    class Schematic implements GraphicSource {
        double wireThickness = .13;

        public void addGraphic( Branch b ) {
            if( b instanceof Battery ) {
                addBatteryGraphic( (Battery)b );
            }
            else if( b instanceof Bulb ) {
                addBulbGraphic( (Bulb)b );
            }
            else if( b instanceof Resistor ) {
                addResistorGraphic( (Resistor)b );
            }
            else if( b instanceof Switch ) {
                addSwitchGraphic( (Switch)b );
            }
            else if( b instanceof SeriesAmmeter ) {
                addSeriesAmmeterGraphic( (SeriesAmmeter)b );
            }
            else {
                addWireGraphic( b );
            }
        }

        private void addBulbGraphic( Bulb bulb ) {
            SchematicBulbGraphic sebulb = new SchematicBulbGraphic( apparatusPanel, bulb, getTransform(), wireThickness );
//            BulbComponentGraphic ccbg = new BulbComponentGraphic( apparatusPanel, bulb, transform, module );
//            FilamentGraphic fg = new FilamentGraphic( bulb.getFilament(), transform );
//            filamentLayer.addGraphic( fg );
//            //TODO could also be coupled at the bulb layer
            bulb.setSchematic( true, getCircuit() );
            CircuitGraphic.this.addGraphic( bulb, sebulb );
        }

        private void addSeriesAmmeterGraphic( SeriesAmmeter seriesAmmeter ) {
            SchematicAmmeterGraphic ccbg = new SchematicAmmeterGraphic( apparatusPanel, seriesAmmeter, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( seriesAmmeter, ccbg );
        }

        private void addSwitchGraphic( Switch aSwitch ) {
            SchematicSwitchGraphic ssg = new SchematicSwitchGraphic( apparatusPanel, aSwitch, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( aSwitch, ssg );
            SchematicLeverGraphic schematicSwitchGraphic = new SchematicLeverGraphic( ssg, apparatusPanel, getTransform(),
                                                                                      wireThickness, ssg.getLeverLengthModelCoordinates() );
            InteractiveSchematicLever isl = new InteractiveSchematicLever( getTransform(), apparatusPanel, schematicSwitchGraphic );
            leverLayer.addGraphic( isl );
        }

        private void addResistorGraphic( Resistor resistor ) {
            SchematicResistorGraphic ccbg = new SchematicResistorGraphic( apparatusPanel, resistor, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( resistor, ccbg );
        }

        private void addWireGraphic( Branch b ) {
            TotalBranchGraphic totalBranchGraphic = new TotalBranchGraphic( CircuitGraphic.this, b, apparatusPanel, transform, Color.black, junctionRadius, module, wireThickness );
            branches.addGraphic( totalBranchGraphic.getInteractiveBranchGraphic() );
            junctionLayer.addGraphic( totalBranchGraphic.getInteractiveJunctionGraphic1() );
            junctionLayer.addGraphic( totalBranchGraphic.getInteractiveJunctionGraphic2() );
        }

        private void addBatteryGraphic( Battery component ) {
            SchematicBatteryGraphic ccbg = new SchematicBatteryGraphic( apparatusPanel, component, getTransform(), wireThickness );
            CircuitGraphic.this.addGraphic( component, ccbg );
        }

    }
}
