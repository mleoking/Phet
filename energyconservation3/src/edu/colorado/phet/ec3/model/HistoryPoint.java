/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 4:51:38 PM
 * Copyright (c) Oct 23, 2005 by Sam Reid
 */

public class HistoryPoint {
    private EnergySkateParkModel model;
    private Body body;
    private double x;
    private double y;
    private double time;
    private double ke;
    private double pe;
    private double totalEnergy;
    private double thermalEnergy;

    public HistoryPoint( EnergySkateParkModel model, Body body ) {
        this.model = model;
        this.body = body;
        this.x = body.getX();
        this.y = body.getY();
        this.time = model.getTime();
        this.ke = body.getKineticEnergy();
        this.pe = body.getPotentialEnergy();
        this.thermalEnergy = body.getThermalEnergy();
        this.totalEnergy = body.getTotalEnergy();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTime() {
        return time;
    }

    public double getKE() {
        return ke;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public double getPe() {
        return pe;
    }

    public double getThermalEnergy() {
        return thermalEnergy;
    }
}
