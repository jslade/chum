package chum.fp;

import java.nio.IntBuffer;


/** 
 * A 3-dimensional Fixed-point vector
 * <p>
 * Adapted from Android ApiDemos 'kube' sample (GLVertex.java)
 * <p>
 * Optimizations also based on
 * http://www.devx.com/Java/Article/21850/0/page/2
 */
public class Vec3 implements java.io.Serializable {

    public int x;
    public int y;
    public int z;
    
    public static Vec3 ORIGIN = new Vec3();
    public static Vec3 X_AXIS = new Vec3(1f,0f,0f);
    public static Vec3 Y_AXIS = new Vec3(0f,1f,0f);
    public static Vec3 Z_AXIS = new Vec3(0f,0f,1f);

    public Vec3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3(Vec3 v) {
        set(v);
    }

    public Vec3( int x, int y, int z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3( float x, float y, float z ) {
        this.x = FP.floatToFP(x);
        this.y = FP.floatToFP(y);
        this.z = FP.floatToFP(z);
    }

    public boolean equals(Object other) {
        if (other instanceof Vec3) {
            Vec3 v = (Vec3)other;
            return (x == v.x && y == v.y && z == v.z);
        }
        return false;
    }


    public final Vec3 set (Vec3 o) {
        x = o.x;
        y = o.y;
        z = o.z;
        return this;
    }


    public final void add (Vec3 o, Vec3 dest) {
        dest.x = x + o.x;
        dest.y = y + o.y;
        dest.z = z + o.z;
    }


    public final void delta (Vec3 o, Vec3 dest) {
        dest.x = x - o.x;
        dest.y = y - o.y;
        dest.z = z - o.z;
    }


    public void scale (int scale, Vec3 dest) {
        long lscale = (long)scale;
        long t = (long)this.x * lscale;
        dest.x = (int)(t >> 16);

        t = (long)this.y * lscale;
        dest.y = (int)(t >> 16);

        t = (long)this.z * lscale;
        dest.z = (int)(t >> 16);
    }


    public final int dot (Vec3 o) {
        //    = (x * o.x) + (y * o.y) + (z * o.z)
        // or = FP.mul(x,o.x) + FP.mul(y,o.y) + FP.mul(z,o.z);
        final int x2 = (int)((((long)x) * ((long)o.x)) >> 16);
        final int y2 = (int)((((long)y) * ((long)o.y)) >> 16);
        final int z2 = (int)((((long)z) * ((long)o.z)) >> 16);
        return x2 + y2 + z2;
    }

    
    public final void cross (Vec3 o, Vec3 dest) {
        // x = t.y * o.z - o.y * t.z
        // y = t.z * o.x - o.z * t.x
        // z = t.x * o.y - o.x * t.y
        dest.x = (int)((((long)y * (long)o.z) - ((long)o.y * (long)z)) >> 16);
        dest.y = (int)((((long)z * (long)o.x) - ((long)o.z * (long)x)) >> 16);
        dest.z = (int)((((long)x * (long)o.y) - ((long)o.x * (long)y)) >> 16);
    }


    public int length () {
        //int square = this.dot(this);
        final long lx = (long)x;
        final long ly = (long)y;
        final long lz = (long)z;
        final long x2 = (lx*lx) >> 16;
        final long y2 = (ly*ly) >> 16;
        final long z2 = (lz*lz) >> 16;
        final long square = x2 + y2 + z2;
        return FP.sqrt((int)square);
    }
    

    public final int manhattan () {
        int sum = 0;
        sum += (x < 0)? -x : x;
        sum += (y < 0)? -y : y;
        sum += (z < 0)? -z : z;
        return sum;
    }
    

    public void normalize () {
        int len = length();
        if ( len == 0 ) return;

        long t = (long)x << 32;
        x = (int)( (t/len) >> 16 );

        t = (long)y << 32;
        y = (int)( (t/len) >> 16 );

        t = (long)z << 32;
        z = (int)( (t/len) >> 16 );
    }


    public final void maximum(Vec3 o, Vec3 dest) {
        dest.x = (x > o.x) ? x : o.x;
        dest.y = (y > o.y) ? y : o.y;
        dest.z = (z > o.z) ? z : o.z;
    }


    public final void minimum(Vec3 o, Vec3 dest) {
        dest.x = (x < o.x) ? x : o.x;
        dest.y = (y < o.y) ? y : o.y;
        dest.z = (z < o.z) ? z : o.z;
    }


    public final Vec3 set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }


    public final Vec3 set(float x, float y, float z) {
        this.x = FP.floatToFP(x);
        this.y = FP.floatToFP(y);
        this.z = FP.floatToFP(z);
        return this;
    }


    public final Vec3 set(Sphere3 sph) {
        int theta = sph.theta;
        while ( theta < 0 ) theta += FP.PI_TIMES_2;
        while ( theta > FP.PI_TIMES_2 ) theta -= FP.PI_TIMES_2;

        int phi = sph.phi;
        while ( phi < 0 ) phi += FP.PI_TIMES_2;
        while ( phi > FP.PI_TIMES_2 ) phi -= FP.PI_TIMES_2;

        int r_sin_theta = FP.mul(sph.r, FP.sin(theta));
        int r_cos_theta = FP.mul(sph.r, FP.cos(theta));

        this.x = FP.mul( r_sin_theta, FP.sin(phi) );
        this.y = r_cos_theta;
        this.z = FP.mul( r_sin_theta, FP.cos(phi) );

        return this;
    }


    public final void put ( IntBuffer vertBuffer ) {
        vertBuffer.put(x);
        vertBuffer.put(y);
        vertBuffer.put(z);
        
    }


    public final void put ( int[] vertBuffer, int offset ) {
        vertBuffer[offset++] = x;
        vertBuffer[offset++] = y;
        vertBuffer[offset++] = z;
    }


    @Override
    public String toString() {
        return String.format("[%.3f,%.3f,%.3f]",
                             FP.toFloat(x),
                             FP.toFloat(y),
                             FP.toFloat(z));
    }

}
