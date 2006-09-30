package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:56:28 PM
 * Copyright (c) Jun 14, 2004 by Sam Reid
 */
public class VirtualAmmeterNode extends PhetPNode {
    private TargetReadoutToolNode trt;
    private Component panel;
    private ICCKModule module;
    private Circuit circuit;

    public VirtualAmmeterNode( Circuit circuit, Component panel, ICCKModule module ) {
        this( new TargetReadoutToolNode(), panel, circuit, module );
    }

    public VirtualAmmeterNode( TargetReadoutToolNode targetReadoutTool, final Component panel, Circuit circuit, final ICCKModule module ) {
        this.trt = targetReadoutTool;
        this.panel = panel;
        this.module = module;
        this.circuit = circuit;
        trt.scale( 1.0 / 60.0 );
        trt.setOffset( 1, 1 );
        addChild( targetReadoutTool );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension pt = event.getDeltaRelativeTo( VirtualAmmeterNode.this );
                Rectangle2D.Double rect = getFullBounds();
                Rectangle2D proposedBounds = AffineTransform.getTranslateInstance( pt.width, pt.height ).createTransformedShape( rect ).getBounds2D();
                if( module.getCCKModel().getModelBounds().contains( proposedBounds ) ) {
                    translate( pt.width, pt.height );
                    recompute();
                }
            }
//            if( module.getCCKModel().getcontains( trt.getPoint().x + (int)dx, trt.getPoint().y + (int)dy ) )
//                        {
//                            trt.translate( (int)dx, (int)dy );
//                            recompute();
//                        }
        } );
        resetText();
        setVisible( false );
    }

    public void recompute() {
        Point2D target = new Point2D.Double();
        //check for intersect with circuit.
        Branch branch = circuit.getBranch( target );
        if( branch != null ) {
            double current = branch.getCurrent();
            DecimalFormat df = new DecimalFormat( "0.00" );
            String amps = df.format( Math.abs( current ) );
            trt.setText( amps + " " + SimStrings.get( "VirtualAmmeter.Amps" ) );
            return;
        }
        resetText();
    }

    private void resetText() {
        String[] text = new String[]{
                SimStrings.get( "VirtualAmmeter.HelpString1" ),
                SimStrings.get( "VirtualAmmeter.HelpString2" )
        };
        trt.setText( text );
    }

}
