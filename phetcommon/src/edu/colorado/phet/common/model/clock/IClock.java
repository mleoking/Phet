/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model.clock;

/**
 * IClock models passage of time through a Module in a PhetApplication.
 * <p>
 * IClocks are always in one of two states: running or paused.
 * All clocks may have listeners attached, to receive notification 
 * of state changes or time changes.
 * <p>
 * IClock maintains a separation between wall time and simulation time.
 */
public interface IClock {
    /**
     * Starts the clock (puts it in the running state). 
     * If the clock was already running, nothing happens.
     * <p>
     * ClockListeners will have their clockStarted method called
     * if the clock's state changes.
     */
    void start();

    /**
     * Pauses the clock (puts it in the paused state).  
     * If the clock was already paused, nothing happens.
     * <p/>
     * ClockListeners will have their clockPaused method called
     * if the clock's state changes.
     */
    void pause();

    /**
     * Get whether the clock is paused.
     *
     * @return whether the clock is paused.
     */
    boolean isPaused();

    /**
     * Get whether the clock is running.
     *
     * @return whether the clock is running.
     */
    boolean isRunning();

    /**
     * Adds a ClockListener to this IClock.
     *
     * @param clockListener
     */
    void addClockListener( ClockListener clockListener );

    /**
     * Removes a ClockListener from this IClock.
     *
     * @param clockListener
     */
    void removeClockListener( ClockListener clockListener );

    /**
     * Sets the simulation time to zero.
     * <p>
     * ClockListeners will have their simulationTimeReset method called.
     * If the simulation time actually changes, the ClockListeners will
     * also have their simulationTimeChanged method called.
     */
    void resetSimulationTime();

    /**
     * Determines the last read wall-clock time.
     *
     * @return the last read wall-clock time.
     */
    long getWallTime();

    /**
     * Determines how many milliseconds passed since the previous tick.
     *
     * @return how many milliseconds of wall-time passed since the previous tick.
     */
    long getWallTimeChange();

    /**
     * Gets the time change in simulation time units.
     *
     * @return the time change in simulation time units.
     */
    double getSimulationTimeChange();

    /**
     * Gets the current running time of the simulation.
     *
     * @return the current running time of the simulation.
     */
    double getSimulationTime();

    /**
     * Specifies an exact simulation time.
     * <p>
     * If the simulation time actually changes, then ClockListeners 
     * will have their simulationTimeChanged method called.
     *
     * @param simulationTime
     */
    void setSimulationTime( double simulationTime );

    /**
     * Advances the clock by one tick.
     * <p>
     * ClockListeners will have their clockTicked method called.
     * If the simulation time changes, then ClockListeners will
     * also have their simulationTimeChanged method called.
     */
    void tickOnce();

    /**
     * Sets the number of milliseconds of simulation tim the clock will
     * report with each tick
     *
     * @param dt
     */
    void setSimulationDt( double dt );
}
