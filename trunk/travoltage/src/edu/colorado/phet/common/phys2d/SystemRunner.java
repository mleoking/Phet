package edu.colorado.phet.common.phys2d;


public class SystemRunner implements Runnable {
    System2D system;
    boolean running = true;
    boolean alive;
    double dt;
    int waitTime;

    public SystemRunner( System2D system, double dt, int waitTime ) {
        this.system = system;
        this.dt = dt;
        this.waitTime = waitTime;
    }

    public boolean isActiveAndRunning() {
        return alive && running;
    }

    public void run() {
        this.alive = true;
        this.running = true;
        while( alive ) {
            while( running ) {
                system.iterate( dt );
                edu.colorado.phet.common.util.ThreadHelper.quietNap( waitTime );
            }
        }
    }
}



