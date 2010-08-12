package chum.f;

import java.nio.FloatBuffer;


/** 
    A 2-dimensional floating-point vector
    Uses (u,v) because it's primarily intended to be used
    for texcoords.
 */
public class Vec2 {

    public float u;
    public float v;
    
    public Vec2() {
        this.u = 0;
        this.v = 0;
    }

    public Vec2(Vec2 v) {
        set(v);
    }

    public Vec2( float u, float v ) {
        this.u = u;
        this.v = v;
    }

    public boolean equals(Object other) {
        if (other instanceof Vec2) {
            Vec2 o = (Vec2)other;
            return (u == o.u && v == o.v);
        }
        return false;
    }


    public final void set (Vec2 o) {
        u = o.u;
        v = o.v;
    }


    public final void put ( FloatBuffer buffer ) {
        buffer.put(u);
        buffer.put(v);
    }


    public final void put ( float[] vertBuffer, int offset ) {
        vertBuffer[offset++] = u;
        vertBuffer[offset++] = v;
    }


    @Override
    public String toString() {
        return String.format("(%.3f,%.3f)", u, v);
    }

}
