// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.laws;

import phys2d_efield.DoublePoint;
import phys2d_efield.Particle;

public interface ForceLaw
{

    public abstract DoublePoint getForce(Particle particle, Particle particle1);
}
