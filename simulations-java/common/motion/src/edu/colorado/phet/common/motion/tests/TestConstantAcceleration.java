package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.model.AccelerationDriven;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import junit.framework.TestCase;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:47:31 PM
 */

public class TestConstantAcceleration extends TestCase {
    public void testConstantAcceleration() {
        MotionModel motionModel = new MotionModel( new SwingClock( 30, 1 ) );
        assertEquals( motionModel.getMotionBody().getPosition(), 0.0, 0.0 );
        motionModel.setAccelerationDriven();
        motionModel.getMotionBody().setAcceleration( 1.0 );
        motionModel.stepInTime( 1.0 );
        assertEquals( "Velocity should have increased to 1.0",1.0,motionModel.getMotionBody().getVelocity(), 1E-7 );
//        assertEquals( motionModel.getPosition(),0,1E-7);//todo: what should the position be now?  Depends on integration scheme...
    }

    public static void main( String[] args ) {
        AccelerationDriven accelerationDriven = new AccelerationDriven();
        MotionModel model = new MotionModel( new SwingClock( 30, 1 ) );
        model.getMotionBody().setAcceleration( 1.0 );
        model.setUpdateStrategy( accelerationDriven );
        System.out.println( "init state=" + model);
        for( int i = 0; i <= 100; i++ ) {
            model.stepInTime( 1.0 );
            System.out.println( "i = " + i + ", state=" + model);
        }

        TimeData[] timeData = model.getRecentAccelerationTimeSeries( 5 );
        for( int i = 0; i < timeData.length; i++ ) {
            TimeData data = timeData[i];
            System.out.println( "i = " + i + ", data=" + data );
        }
    }
}
