package chum.f;

/** 
    A 4-dimensional floating-point vector
    Mostly useful as a representation of a quarternion.
    Adapted from JCollada (Quat.java)

    Also used to represent planes for the viewport's frustum
 */
public class Vec4 extends Vec3 {

    public float w;
    
    public Vec4() {
        super();
        this.w = 0f;
    }

    public Vec4(Vec4 v) {
        super();
        set(v);
    }

    public Vec4(Vec3 v) {
        super(v);
        this.w = 0f;
    }

    public Vec4( float x, float y, float z, float w ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Vec4) {
            Vec4 o = (Vec4)other;
            return (x == o.x && y == o.y && z == o.z && w == o.w);
        }
        return false;
    }


    public final void set (Vec4 o) {
        x = o.x;
        y = o.y;
        z = o.z;
        w = o.w;
    }


    @Override
    public float length () {
        float square = x*x + y*y + z*z + w*w;
        return (float)Math.sqrt(square);
    }


    public void scale (float scale, Vec4 dest) {
        dest.x = x * scale;
        dest.y = y * scale;
        dest.z = z * scale;
        dest.w = w * scale;
    }


    @Override
    public float normalize () {
        float len = length();
        if ( len > 0 ) {
            x = x/len;
            y = y/len;
            z = z/len;
            w = w/len;
        }
        return len;
    }


    @Override
    public String toString() {
        return "["+
            x+","+
            y+","+
            z+","+
            w+"]";
    }

}
