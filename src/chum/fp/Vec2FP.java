package chum.fp;

import java.nio.IntBuffer;


/** 
    A 2-dimensional Fixed-point vector
    Uses (u,v) because it's primarily intended to be used
    for texcoords.
 */
public class Vec2FP {

    public int u;
    public int v;
    
    public Vec2FP() {
        this.u = 0;
        this.v = 0;
    }

    public Vec2FP(Vec2FP v) {
        set(v);
    }

    public Vec2FP( int u, int v ) {
        this.u = u;
        this.v = v;
    }

    public Vec2FP( float u, float v ) {
        this.u = FP.floatToFP(u);
        this.v = FP.floatToFP(v);
    }

    public boolean equals(Object other) {
        if (other instanceof Vec2FP) {
            Vec2FP o = (Vec2FP)other;
            return (u == o.u && v == o.v);
        }
        return false;
    }


    public final void set (Vec2FP o) {
        u = o.u;
        v = o.v;
    }


    public final void put ( IntBuffer buffer ) {
        buffer.put(u);
        buffer.put(v);
    }


    public final void put ( int[] vertBuffer, int offset ) {
        vertBuffer[offset++] = u;
        vertBuffer[offset++] = v;
    }


    @Override
    public String toString() {
        return String.format("(%.3f,%.3f)",
                             FP.toFloat(u),
                             FP.toFloat(v));
    }

}
