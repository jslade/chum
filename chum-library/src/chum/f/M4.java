package chum.f;

/** 
 * A 4x4 floating-point matrix
 */
public class M4 {
    public float[] m = new float[16];
    private static M4 xform = new M4();
    private static M4 tmp = new M4();
    private static Vec3 vtmp = new Vec3();
    
	
    public M4() {
        this(true);
    }
	

    public M4(boolean identity) {
        if ( identity ) setIdentity();
    }
	

    public M4(M4 other) {
        set(other);
    }

    // TODO: deprecate this
    public final M4 copy(M4 other) {
        return set(other);
    }
    
    public final M4 set(M4 other) {
        for (int i = 0; i < 16; ++i)
                m[i] = other.m[i];
        return this;
    }

    public final void multiply(Vec3 src, Vec3 dest) {
        synchronized(vtmp) {
            if ( dest == src ) dest = vtmp;
            dest.x = src.x * m[0] + src.y * m[1] + src.z * m[2] + m[3];
            dest.y = src.x * m[4] + src.y * m[5] + src.z * m[6] + m[7];
            dest.z = src.x * m[8] + src.y * m[9] + src.z * m[10] + m[11];
            if ( dest == vtmp ) src.set(vtmp);
        }
    }


    public final void multiply(M4 other, M4 dest) {
        dest.m[0 ] = m[0 ]*other.m[0] + m[1 ]*other.m[4] + m[2 ]*other.m[8 ] + m[3 ]*other.m[12];
        dest.m[1 ] = m[0 ]*other.m[1] + m[1 ]*other.m[5] + m[2 ]*other.m[9 ] + m[3 ]*other.m[13];
        dest.m[2 ] = m[0 ]*other.m[2] + m[1 ]*other.m[6] + m[2 ]*other.m[10] + m[3 ]*other.m[14];
        dest.m[3 ] = m[0 ]*other.m[3] + m[1 ]*other.m[7] + m[2 ]*other.m[11] + m[3 ]*other.m[15];
        dest.m[4 ] = m[4 ]*other.m[0] + m[5 ]*other.m[4] + m[6 ]*other.m[8 ] + m[7 ]*other.m[12];
        dest.m[5 ] = m[4 ]*other.m[1] + m[5 ]*other.m[5] + m[6 ]*other.m[9 ] + m[7 ]*other.m[13];
        dest.m[6 ] = m[4 ]*other.m[2] + m[5 ]*other.m[6] + m[6 ]*other.m[10] + m[7 ]*other.m[14];
        dest.m[7 ] = m[4 ]*other.m[3] + m[5 ]*other.m[7] + m[6 ]*other.m[11] + m[7 ]*other.m[15];
        dest.m[8 ] = m[8 ]*other.m[0] + m[9 ]*other.m[4] + m[10]*other.m[8 ] + m[11]*other.m[12];
        dest.m[9 ] = m[8 ]*other.m[1] + m[9 ]*other.m[5] + m[10]*other.m[9 ] + m[11]*other.m[13];
        dest.m[10] = m[8 ]*other.m[2] + m[9 ]*other.m[6] + m[10]*other.m[10] + m[11]*other.m[14];
        dest.m[11] = m[8 ]*other.m[3] + m[9 ]*other.m[7] + m[10]*other.m[11] + m[11]*other.m[15];
        dest.m[12] = m[12]*other.m[0] + m[13]*other.m[4] + m[14]*other.m[8 ] + m[15]*other.m[12];
        dest.m[13] = m[12]*other.m[1] + m[13]*other.m[5] + m[14]*other.m[9 ] + m[15]*other.m[13];
        dest.m[14] = m[12]*other.m[2] + m[13]*other.m[6] + m[14]*other.m[10] + m[15]*other.m[14];
        dest.m[15] = m[12]*other.m[3] + m[13]*other.m[7] + m[14]*other.m[11] + m[15]*other.m[15];
    }
	

    public final M4 setIdentity() {
        for (int i = 1; i < 16; ++i)
            m[i] = 0f;
        m[0] = m[5] = m[10] = m[15] = 1f;
        return this;
    }


    public final M4 clear() {
        for (int i = 0; i < 16; ++i)
            m[i] = 0f;
        return this;
    }


    public final void transpose(M4 dest) {
        if ( dest == this ) dest = tmp;
        dest.m[0] = m[0];
        dest.m[1 ] = m[4];
        dest.m[2 ] = m[8];
        dest.m[3 ] = m[12];
        dest.m[4 ] = m[1];
        dest.m[5 ] = m[5];
        dest.m[6 ] = m[6];
        dest.m[7 ] = m[7];
        dest.m[8 ] = m[2];
        dest.m[9 ] = m[6];
        dest.m[10] = m[10];
        dest.m[11] = m[14];
        dest.m[12] = m[3];
        dest.m[13] = m[7];
        dest.m[14] = m[11];
        dest.m[15] = m[15];
        if ( dest == tmp ) this.set(dest);
    }

    
    public final M4 scale(float x, float y, float z) {
        clear();
        m[0] = x;
        m[5] = y;
        m[10] = z;
        m[15] = 1f;
        return this;
    }

    
    public final void scale(float x, float y, float z, M4 dest) {
        synchronized(xform) {
            if ( dest == this ) dest = tmp;
            xform.scale(x,y,z);
            this.multiply(xform,dest);
            if ( dest == tmp ) this.set(dest);
        }   
    }

    
    public final M4 scale(float scale) {
        clear();
        m[0] = scale;
        m[5] = scale;
        m[10] = scale;
        m[15] = 1f;
        return this;
    }

    
    public final void scale(float scale, M4 dest) {
        synchronized(xform) {
            if ( dest == this ) dest = tmp;
            xform.scale(scale);
            this.multiply(xform,dest);
            if ( dest == tmp ) this.set(dest);
        }   
    }

    
    public final M4 translate(Vec3 v) {
        setIdentity();
        m[3] = v.x;
        m[7] = v.y;
        m[11] = v.z;
        return this;
    }

    
    public final void translate(Vec3 v, M4 dest) {
        synchronized(xform) {
            if ( dest == this ) dest = tmp;
            xform.translate(v);
            this.multiply(xform,dest);
            if ( dest == tmp ) this.set(dest);
        }   
    }

    
    public final M4 rotate(Vec3 v, float theta) {
        theta *= 0.5f;
        float s = (float)Math.sin(theta);
        Vec4 q = new Vec4(v.x * s,
                          v.y * s,
                          v.z * s,
                          (float)Math.cos(theta));
        return rotate(q);
    }


    public final void rotate(Vec3 v, float theta, M4 dest) {
        synchronized(xform) {
            if ( dest == this ) dest = tmp;
            xform.rotate(v,theta);
            this.multiply(xform,dest);
            if ( dest == tmp ) this.set(dest); 
        }   
    }

    
    /**
       Rotate by a Quarternion
       (see Mat4::MakeHRot() from SVL)
    */
    public final M4 rotate(Vec4 q) {
        //setIdentity();

        float i1  = q.x;
        float j1  = q.y;
        float k1  = q.z;
        float l1 = q.w;
        float i2 = i1 * 2f;
        float j2 = j1 * 2f;
        float k2 = k1 * 2f;
        float ij = i2 * j1;
        float ik = i2 * k1;
        float jk = j2 * k1;
        float ri = i2 * l1;
        float rj = j2 * l1;
        float rk = k2 * l1;

        i2 = i2 * i1;
        j2 = j2 * j1;
        k2 = k2 * k1;

        m[0] = 1f - (j2 + k2);
        m[1] = (ij - rk);
        m[2] = (ik + rj);
        m[3] = 0f;

        m[4] = (ij + rk);
        m[5] = 1f - (i2 + k2);
        m[6] = (jk - ri);
        m[7] = 0f;

        m[8] = (ik - rj);
        m[9] = (jk + ri);
        m[10] = 1f - (i2 + j2);
        m[11] = 0f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = 0f;
        m[15] = 1f;

        return this;
    }

    
    public final void rotate(Vec4 q, M4 dest) {
        synchronized(xform) {
            if ( dest == this ) dest = tmp;
            xform.rotate(q);
            this.multiply(xform,dest);
            if ( dest == tmp ) this.set(dest);
        }   
    }

    
    @Override
    public String toString() {
        return String.format("[%.3f %.3f %.3f %.3f\n"+
                             " %.3f %.3f %.3f %.3f\n"+
                             " %.3f %.3f %.3f %.3f\n"+
                             " %.3f %.3f %.3f %.3f]",
                             m[0], m[1], m[2], m[3],
                             m[4], m[5], m[6], m[7],
                             m[8], m[9], m[10],m[11],
                             m[12],m[13],m[14],m[15]);
    }
}
