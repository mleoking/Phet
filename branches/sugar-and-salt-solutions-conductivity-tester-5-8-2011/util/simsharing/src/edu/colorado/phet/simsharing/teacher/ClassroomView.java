// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import akka.actor.ActorRef;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.simsharing.GetStudentList;
import edu.colorado.phet.simsharing.SessionID;
import edu.colorado.phet.simsharing.StudentSummary;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * @author Sam Reid
 */
public class ClassroomView extends PSwingCanvas {
    private final ActorRef server;
    private final String[] args;
    private final PNode studentThumbnailNode;
    private PNode summaryNode;

    public ClassroomView( final ActorRef server, final String[] args ) {
        this.server = server;
        this.args = args;
        summaryNode = new PNode() {
            @Override public void addChild( PNode child ) {
                child.setOffset( child.getOffset().getX(), getFullBounds().getMaxY() + 2 );
                super.addChild( child );
            }
        };
        getLayer().addChild( summaryNode );

        studentThumbnailNode = new PNode();
        getLayer().addChild( studentThumbnailNode );

        //Look for new students this often
        new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    try {
                        Thread.sleep( 1000 );
                        final StudentList list = (StudentList) server.sendRequestReply( new GetStudentList() );
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                summaryNode.removeAllChildren();
                                summaryNode.addChild( new PText( "Students: " + list.size() ) );
                                final double avgUpTime = average( list.toList(), new Function1<StudentSummary, Double>() {
                                    public Double apply( StudentSummary s ) {
                                        return (double) ( s.getUpTime() );
                                    }
                                } );
                                summaryNode.addChild( new PText( "Average up time: " + new DecimalFormat( "0.00" ).format( avgUpTime / 1000.0 ) + " sec" ) );
                                final double avgLatency = average( list.toList(), new Function1<StudentSummary, Double>() {
                                    public Double apply( StudentSummary s ) {
                                        return (double) ( s.getTimeSinceLastEvent() );
                                    }
                                } );
                                summaryNode.addChild( new PText( "Average latency: " + avgLatency + " ms" ) );

                                double y = summaryNode.getFullBounds().getMaxY();

                                for ( int i = 0; i < list.size(); i++ ) {
                                    StudentSummary student = list.get( i );
                                    final SessionID sessionID = student.getSessionID();
                                    StudentComponent component = getComponent( sessionID );
                                    if ( component == null ) {
                                        component = new StudentComponent( sessionID, new VoidFunction0() {
                                            public void apply() {
                                                new SimView( args, sessionID, new SimView.SampleSource.RemoteActor( server, sessionID ), false ).start();
                                            }
                                        } );
                                        studentThumbnailNode.addChild( component );
                                    }
                                    component.setThumbnail( student.getBufferedImage() );
                                    component.setUpTime( student.getUpTime() );
                                    component.setTimeSinceLastEvent( student.getTimeSinceLastEvent() );
                                    component.setOffset( 0, y + 2 );
                                    y = component.getFullBounds().getMaxY();
                                }

                                //Remove components for students that have exited
                                ArrayList<PNode> toRemove = new ArrayList<PNode>();
                                for ( int i = 0; i < studentThumbnailNode.getChildrenCount(); i++ ) {
                                    PNode child = studentThumbnailNode.getChild( i );
                                    if ( child instanceof StudentComponent && !list.containsStudent( ( (StudentComponent) child ).getSessionID() ) ) {
                                        toRemove.add( child );
                                    }
                                }
                                studentThumbnailNode.removeChildren( toRemove );
                            }
                        } );
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    private StudentComponent getComponent( SessionID sessionID ) {
        for ( int i = 0; i < studentThumbnailNode.getChildrenCount(); i++ ) {
            PNode child = studentThumbnailNode.getChild( i );
            if ( child instanceof StudentComponent && ( (StudentComponent) child ).sessionID.equals( sessionID ) ) {
                return (StudentComponent) child;
            }
        }
        return null;
    }

    public static <T> double average( ArrayList<T> list, Function1<T, Double> function ) {
        double sum = 0.0;
        for ( T t : list ) {
            sum += function.apply( t );
        }
        return sum / list.size();
    }
}