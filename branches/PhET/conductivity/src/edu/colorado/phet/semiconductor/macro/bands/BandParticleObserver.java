// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.bands;


// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            BandParticle

public interface BandParticleObserver {

    public abstract void particleRemoved( BandParticle bandparticle );

    public abstract void particleAdded( BandParticle bandparticle );
}
