package edu.colorado.phet.movingmanii.model;

import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.movingmanii.charts.ChartCursor;
import edu.colorado.phet.movingmanii.view.Range;

import java.util.ArrayList;

public class MovingManModel {
    public static final double DT = 1.0 / 30.0;
    private MovingMan movingMan;

    public static final int DERIVATIVE_RADIUS = 1;//Kathy chose this value because it is a good balance between derivative sharpness and responsiveness
    public static final int NUMBER_MOUSE_POINTS_TO_AVERAGE = 4;//Kathy chose this value because it smoothes well enough, but without creating too much of a lag between the mouse and the character

    //These serieses are used for computing derivatives.  Must be size limited to avoid processor/memory leaks
    private static final int sizeLimit = Math.max(NUMBER_MOUSE_POINTS_TO_AVERAGE, (DERIVATIVE_RADIUS * 2 + 1) * 2);
    private MovingManDataSeries mouseDataModelSeries = new MovingManDataSeries.LimitedSize(sizeLimit);
    private MovingManDataSeries positionModelSeries = new MovingManDataSeries.LimitedSize(sizeLimit);
    private MovingManDataSeries velocityModelSeries = new MovingManDataSeries.LimitedSize(sizeLimit);
    private MovingManDataSeries accelerationModelSeries = new MovingManDataSeries.LimitedSize(sizeLimit);

    //These serieses are displayed in the graphs
    private MovingManDataSeries positionGraphSeries = new MovingManDataSeries.LimitedTime(20.0);
    private MovingManDataSeries velocityGraphSeries = new MovingManDataSeries.LimitedTime(20.0);
    private MovingManDataSeries accelerationGraphSeries = new MovingManDataSeries.LimitedTime(20.0);

    private ChartCursor chartCursor = new ChartCursor();
    private double time = 0.0;
    private double mousePosition;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private MutableBoolean velocityVectorVisible = new MutableBoolean(false);
    private MutableBoolean accelerationVectorVisible = new MutableBoolean(false);
    private MutableBoolean walls = new MutableBoolean(true);
    private Range range = new Range(-10, 10);
    private BooleanGetter isPaused;

    public static interface BooleanGetter {
        boolean isTrue();
    }

    public MovingManModel(BooleanGetter isPaused) {//need to be able to update position immediately when paused instead of smoothing through the mouse data series
        this.movingMan = new MovingMan();
        this.isPaused = isPaused;
    }

    public static class WallResult {
        public final double position;
        public final boolean collided;

        public WallResult(double position, boolean collided) {
            this.position = position;
            this.collided = collided;
        }
    }

    public WallResult clampIfWalled(double x) {
        double clamped = range.clamp(x);
        if (walls.getValue()) return new WallResult(clamped, clamped != x);
        else return new WallResult(x, false);
    }

    public void simulationTimeChanged(double dt) {
        time = time + dt;
        if (movingMan.isPositionDriven()) {
            mouseDataModelSeries.addPoint(clampIfWalled(mousePosition).position, time);
//            //take the position as the average of the latest mouseDataSeries points.
            TimeData[] position = mouseDataModelSeries.getPointsInRange(mouseDataModelSeries.getNumPoints() - NUMBER_MOUSE_POINTS_TO_AVERAGE, mouseDataModelSeries.getNumPoints());
            double sum = 0;
            for (TimeData timeData : position) {
                sum += timeData.getValue();
            }
            double averagePosition = clampIfWalled(sum / position.length).position;
            positionModelSeries.addPoint(averagePosition, time);

            //update model derivatives
            velocityModelSeries.setData(estimateCenteredDerivatives(positionModelSeries));
            accelerationModelSeries.setData(estimateCenteredDerivatives(velocityModelSeries));

            positionGraphSeries.addPoint(averagePosition, time);
            velocityGraphSeries.addPoint(velocityModelSeries.getMidPoint());
            accelerationGraphSeries.addPoint(accelerationModelSeries.getMidPoint());

            //no integrals

            //set instantaneous values
            movingMan.setPosition(averagePosition);
            movingMan.setVelocity(velocityGraphSeries.getDataPoint(velocityGraphSeries.getNumPoints() - 1).getValue());//TODO: subtract off derivative radius so that the last value showed on chart is the same as the value on the man
            movingMan.setAcceleration(accelerationGraphSeries.getDataPoint(accelerationGraphSeries.getNumPoints() - 1).getValue()); //- DERIVATIVE_RADIUS * 2
        } else if (movingMan.isVelocityDriven()) {
            mouseDataModelSeries.clear();//so that if the user switches to mouse-driven, it won't remember the wrong location.
            //record set point
            velocityModelSeries.addPoint(movingMan.getVelocity(), time);
            velocityGraphSeries.addPoint(movingMan.getVelocity(), time);

            //update derivatives
            accelerationModelSeries.setData(estimateCenteredDerivatives(velocityModelSeries));
            accelerationGraphSeries.addPoint(accelerationModelSeries.getMidPoint());

            //update integrals
            final double targetPosition = movingMan.getPosition() + movingMan.getVelocity() * dt;
            WallResult wallResult = clampIfWalled(targetPosition);
            positionModelSeries.addPoint(wallResult.position, time);
            positionGraphSeries.addPoint(wallResult.position, time);

            //set instantaneous values
            setMousePosition(wallResult.position);//so that if the user switches to mouse-driven, it will have the right location
            movingMan.setPosition(wallResult.position);
            movingMan.setAcceleration(accelerationGraphSeries.getDataPoint(accelerationGraphSeries.getNumPoints() - 1).getValue());//todo: subtract - DERIVATIVE_RADIUS if possible
            if (wallResult.collided) {
                movingMan.setVelocity(0.0);
            }
        } else if (movingMan.isAccelerationDriven()) {
            mouseDataModelSeries.clear();//so that if the user switches to mouse-driven, it won't remember the wrong location.
            //record set point
            accelerationModelSeries.addPoint(movingMan.getAcceleration(), time);
            accelerationGraphSeries.addPoint(movingMan.getAcceleration(), time);

            //no derivatives

            //update integrals
            double newVelocity = movingMan.getVelocity() + movingMan.getAcceleration() * dt;
            velocityGraphSeries.addPoint(newVelocity, time);
            velocityModelSeries.addPoint(newVelocity, time);

            double estVel = (movingMan.getVelocity() + newVelocity) / 2.0;//todo: just use newVelocity?
            WallResult wallResult = clampIfWalled(movingMan.getPosition() + estVel * dt);
            positionGraphSeries.addPoint(wallResult.position, time);
            positionModelSeries.addPoint(wallResult.position, time);

            //set instantaneous values
            setMousePosition(wallResult.position);//so that if the user switches to mouse-driven, it will have the right location
            movingMan.setPosition(wallResult.position);
            movingMan.setVelocity(newVelocity);
            if (wallResult.collided) {
                movingMan.setVelocity(0.0);
                movingMan.setAcceleration(0.0);//todo: should have brief burst of acceleration against the wall in a collision.
            }
        }
    }

    private TimeData[] estimateCenteredDerivatives(MovingManDataSeries series) {
        int radius = DERIVATIVE_RADIUS;
        ArrayList<TimeData> points = new ArrayList<TimeData>();
        for (int i = 0; i < series.getNumPoints(); i++) {
            TimeData[] range = series.getPointsInRange(i - radius, i + radius);
            double derivative = MotionMath.estimateDerivative(range);
            points.add(new TimeData(derivative, series.getDataPoint(i).getTime()));
        }
        return points.toArray(new TimeData[points.size()]);
    }

    public MovingMan getMovingMan() {
        return movingMan;
    }

    public MovingManDataSeries getPositionGraphSeries() {
        return positionGraphSeries;
    }

    public MovingManDataSeries getVelocityGraphSeries() {
        return velocityGraphSeries;
    }

    public MovingManDataSeries getAccelerationGraphSeries() {
        return accelerationGraphSeries;
    }

    public ChartCursor getChartCursor() {
        return chartCursor;
    }

    public void clear() {
        time = 0.0;
        positionGraphSeries.clear();
        velocityGraphSeries.clear();
        accelerationGraphSeries.clear();
    }

    public MovingManState getRecordingState() {
        return new MovingManState(time, movingMan.getState(), walls.getValue());
    }

    public void setPlaybackState(MovingManState state) {
        this.time = state.getTime();
        this.walls.setValue(state.getWalls());
        this.movingMan.setState(state.getMovingManState());
        this.chartCursor.setTime(time);
        //todo: notify time changed?
    }

    /**
     * This method allows recording the mouse position separately, so that it can be smoothed out before stored as data on the man character.
     *
     * @param mousePosition
     */
    public void setMousePosition(double mousePosition) {
        if (this.mousePosition != mousePosition) {
            this.mousePosition = clampIfWalled(mousePosition).position;
            if (isPaused.isTrue()) {
                movingMan.setPosition(mousePosition);
            }
            for (Listener listener : listeners) {
                listener.mousePositionChanged();
            }
        }
    }

    public double getMousePosition() {
        return mousePosition;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public Range getRange() {
        return range;
    }

    public MutableBoolean getWalls() {
        return walls;
    }

    public static interface Listener {
        void mousePositionChanged();
    }

    public MutableBoolean getVelocityVectorVisible() {
        return velocityVectorVisible;
    }

    public MutableBoolean getAccelerationVectorVisible() {
        return accelerationVectorVisible;
    }
}
