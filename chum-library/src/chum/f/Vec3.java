package chum.f;

import java.nio.FloatBuffer;


/** 
 * A 3-dimensional floating-point vector
 */
public class Vec3 implements java.io.Serializable {

    public float x;
    public float y;
    public float z;
    
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

    public Vec3( float x, float y, float z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
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


    public void scale (float scale, Vec3 dest) {
        dest.x = this.x * scale;
        dest.y = this.y * scale;
        dest.z = this.z * scale;
    }


    public final float dot (Vec3 o) {
        return (x * o.x) + (y * o.y) + (z * o.z);
    }

    
    public final void cross (Vec3 o, Vec3 dest) {
        dest.x = y * o.z - o.y * z;
        dest.y = z * o.x - o.z * x;
        dest.z = x * o.y - o.x * y;
    }


    public float length () {
        final float x2 = x*x;
        final float y2 = y*y;
        final float z2 = z*z;
        final float square = x2 + y2 + z2;
        return (float)Math.sqrt(square);
    }
    

    public final float manhattan () {
        float sum = 0f;
        sum += (x < 0f)? -x : x;
        sum += (y < 0f)? -y : y;
        sum += (z < 0f)? -z : z;
        return sum;
    }
    

    public float normalize () {
        float len = length();
        if ( len > 0f ) {
            x = x/len;
            y = y/len;
            z = z/len;
        }   
        return len;
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


    public final Vec3 set(float x,float y,float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }


    public final Vec3 set(Sphere3 sph) {
        float theta = sph.theta;
        while ( theta < 0 ) theta += Sphere3.PI_TIMES_2;
        while ( theta > Sphere3.PI_TIMES_2 ) theta -= Sphere3.PI_TIMES_2;

        float phi = sph.phi;
        while ( phi < 0f ) phi += Sphere3.PI_TIMES_2;
        while ( phi > Sphere3.PI_TIMES_2 ) phi -= Sphere3.PI_TIMES_2;

        float r_sin_theta = sph.r * (float)Math.sin(theta);
        float r_cos_theta = sph.r * (float)Math.cos(theta);

        this.x = r_sin_theta * (float)Math.sin(phi);
        this.y = r_cos_theta;
        this.z = r_sin_theta * (float)Math.cos(phi);

        return this;
    }


    public final void put ( FloatBuffer vertBuffer ) {
        vertBuffer.put(x);
        vertBuffer.put(y);
        vertBuffer.put(z);
        
    }


    public final void put ( float[] vertBuffer, int offset ) {
        vertBuffer[offset++] = x;
        vertBuffer[offset++] = y;
        vertBuffer[offset++] = z;
    }


    @Override
    public String toString() {
        return String.format("[%.3f,%.3f,%.3f]", x, y, z);
    }

}
