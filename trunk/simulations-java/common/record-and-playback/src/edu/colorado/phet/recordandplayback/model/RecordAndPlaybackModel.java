package edu.colorado.phet.recordandplayback.model;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

import java.util.ArrayList;

/**
 * This is the main model class for sims that support recording and playing back.
 *
 * @author Sam Reid
 * @param <T> the type of state that is recorded and restored, should be immutable.
 */
public abstract class RecordAndPlaybackModel<T> extends SimpleObservable {

    /**
     * Behavior modes that were decided upon after testing
     */
    public static final boolean pauseAtEndOfPlayback = true;
    public static final boolean recordAtEndOfPlayback = false;

    //The history of data points that have been recorded from the model.
    private final ArrayList<DataPoint<T>> recordHistory = new ArrayList<DataPoint<T>>();

    private boolean record = true;//True if the sim is in record mode (may be paused too)
    private boolean paused = true;//True if the current mode is paused 
    private double time = 0.0;//Current time of recording or playback
    private double playbackIndexFloat = 0.0; //floor this to get playbackIndex
    private double playbackSpeed = 1.0;//The speed at which playback will occur.

    private ArrayList<HistoryClearListener> historyClearListeners = new ArrayList<HistoryClearListener>();
    private ArrayList<Listener> historyRemainderClearListeners = new ArrayList<Listener>();

    //Extension points for subclasses

    public abstract void stepRecord();

    public abstract void setPlaybackState(T state);

    public abstract int getMaxRecordPoints();

    public abstract void handleRecordStartedDuringPlayback();

    protected RecordAndPlaybackModel() {
    }

    public void setStateToPlaybackIndex() {
        int playbackIndex = getPlaybackIndex();
        if (playbackIndex >= 0 && playbackIndex < recordHistory.size()) {
            setPlaybackState(recordHistory.get(getPlaybackIndex()).getState());
            time = recordHistory.get(getPlaybackIndex()).getTime();
        }
    }

    public void setPlayback(double speed) {
        setPlaybackSpeed(speed);
        setRecord(false);
    }

    public void rewind() {
        setPlaybackIndexFloat(0.0);
    }

    public void setTime(double t) {
        time = t;
    }

    public void resetAll() {
        record = true;
        paused = true;
        playbackIndexFloat = 0.0;
        playbackSpeed = 1.0;
        recordHistory.clear();
        time = 0;

        notifyObservers();
    }

    public void addRecordedPoint(DataPoint<T> point) {
        recordHistory.add(point);
    }

    public void removeHistoryPoint(int point) {
        recordHistory.remove(point);
    }

    /**
     * Returns a defensive copy of the recorded history points.
     *
     * @return
     */
    public ArrayList<DataPoint<T>> getRecordingHistory() {
        ArrayList<DataPoint<T>> data = new ArrayList<DataPoint<T>>();
        data.addAll(recordHistory);
        return data;
    }

    public void setPlaybackSpeed(double speed) {
        if (speed != playbackSpeed) {
            playbackSpeed = speed;
            notifyObservers();
        }
    }

    public double getPlaybackIndexFloat() {
        return playbackIndexFloat;
    }

    public void setRecord(boolean rec) {
        if (record != rec) {
            record = rec;
            if (record) {
                clearHistoryRemainder();
                handleRecordStartedDuringPlayback();
            }

            notifyObservers();
        }
    }

    public int getNumRecordedPoints() {
        return recordHistory.size();
    }

    public static interface Listener {
        void historyRemainderCleared();
    }

    public void clearHistoryRemainder() {
        ArrayList<DataPoint<T>> keep = new ArrayList<DataPoint<T>>();
        for (DataPoint<T> dataPoint : recordHistory) {
            if (dataPoint.getTime() < time) {
                keep.add(dataPoint);
            }
        }
        recordHistory.clear();
        recordHistory.addAll(keep);
        notifyObservers();
        for (Listener historyRemainderClearListener : historyRemainderClearListeners) {
            historyRemainderClearListener.historyRemainderCleared();
        }
    }

    public void stepPlayback() {
        if (getPlaybackIndex() < recordHistory.size()) {
            setStateToPlaybackIndex();
            time = recordHistory.get(getPlaybackIndex()).getTime();
            playbackIndexFloat = playbackIndexFloat + playbackSpeed;
            notifyObservers();
        } else {
            if (RecordAndPlaybackModel.recordAtEndOfPlayback) {
                setRecord(true);
            }
            if (RecordAndPlaybackModel.pauseAtEndOfPlayback) {
                setPaused(true);
            }
        }
    }

    public void addHistoryClearListener(HistoryClearListener listener) {
        historyClearListeners.add(listener);
    }

    public static interface HistoryClearListener {
        void historyCleared();
    }

    public void clearHistory() {
        recordHistory.clear();
        notifyObservers();
        for (HistoryClearListener historyClearListener : historyClearListeners) {
            historyClearListener.historyCleared();
        }
    }

    public boolean isPlayback() {
        return !record;
    }

    public boolean isRecord() {
        return record;
    }

    public void setPlaybackIndexFloat(double index) {
        playbackIndexFloat = index;
        setStateToPlaybackIndex();
        notifyObservers();
    }

    public void setPaused(boolean p) {
        if (paused != p) {
            paused = p;
            notifyObservers();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public int getPlaybackIndex() {
        return (int) Math.floor(playbackIndexFloat);
    }

    public boolean isRecordingFull() {
        return recordHistory.size() >= getMaxRecordPoints();
    }

//    public ArrayList<DataPoint<T>> getRecordingHistory() {
//        return recordHistory;
//    }

    public double getRecordedTimeRange() {
        if (recordHistory.size() == 0) {
            return 0;
        } else {
            return recordHistory.get(recordHistory.size() - 1).getTime() - recordHistory.get(0).getTime();
        }
    }

    public double getTime() {
        return time;
    }

    public double getMaxRecordedTime() {
        if (recordHistory.size() == 0) return 0.0;
        else return recordHistory.get(recordHistory.size() - 1).getTime();
    }

    public double getMinRecordedTime() {
        if (recordHistory.size() == 0) return 0.0;
        else return recordHistory.get(0).getTime();
    }

    public void setPlaybackTime(double time) {
        Function.LinearFunction f = new Function.LinearFunction(getMinRecordedTime(), getMaxRecordedTime(), 0, recordHistory.size() - 1);
        setPlaybackIndexFloat(f.evaluate(time));
    }

    public double getFloatTime() {
        Function.LinearFunction f = new Function.LinearFunction(0, recordHistory.size() - 1, getMinRecordedTime(), getMaxRecordedTime());
        return f.evaluate(playbackIndexFloat);
    }
}