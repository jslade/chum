package chum.fp;

/** 
    A 4-dimensional Fixed-point vector
    Mostly useful as a representation of a quarternion.
    Adapted from JCollada (Quat.java)

    Also used to represent planes for the viewport's frustum
 */
public class Vec4FP extends Vec3FP {

    public int w;
    
    public Vec4FP() {
        super();
        this.w = 0;
    }

    public Vec4FP(Vec4FP v) {
        super();
        set(v);
    }

    public Vec4FP(Vec3FP v) {
        super(v);
        this.w = 0;
    }

    public Vec4FP( int x, int y, int z, int w ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4FP( float x, float y, float z, float w ) {
        this.x = FP.floatToFP(x);
        this.y = FP.floatToFP(y);
        this.z = FP.floatToFP(z);
        this.w = FP.floatToFP(w);
    }

    public boolean equals(Object other) {
        if (other instanceof Vec4FP) {
            Vec4FP o = (Vec4FP)other;
            return (x == o.x && y == o.y && z == o.z && w == o.w);
        }
        return false;
    }


    public final void set (Vec4FP o) {
        x = o.x;
        y = o.y;
        z = o.z;
        w = o.w;
    }


    public int length () {
        int square = ( (int)(( (long)x * (long)x ) >> 16) +
                       (int)(( (long)y * (long)y ) >> 16) +
                       (int)(( (long)z * (long)z ) >> 16) +
                       (int)(( (long)w * (long)w ) >> 16) );
        return FP.sqrt(square);
    }


    public void scale (int scale, Vec4FP dest) {
        long lscale = (long)scale;

        long t = (long)this.x * lscale;
        dest.x = (int)(t >> 16);

        t = (long)this.y * lscale;
        dest.y = (int)(t >> 16);

        t = (long)this.z * lscale;
        dest.z = (int)(t >> 16);

        t = (long)this.w * lscale;
        dest.w = (int)(t >> 16);
    }


    public void normalize () {
        int len = length();
        
        long t = (long)x << 32;
        x = (int)( (t/len) >> 16 );

        t = (long)y << 32;
        y = (int)( (t/len) >> 16 );

        t = (long)z << 32;
        z = (int)( (t/len) >> 16 );
 
        t = (long)w << 32;
        w = (int)( (t/len) >> 16 );
    }


    @Override
    public String toString() {
        return "["+
            FP.toFloat(x)+","+
            FP.toFloat(y)+","+
            FP.toFloat(z)+","+
            FP.toFloat(w)+"]";
    }

}
