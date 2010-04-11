package chum.fp;

/** 
 * A 4x4 Fixed-point matrix
 * <p>
 * Adapted from Android ApiDemos 'kube' sample (M4.java),
 * And from JCollada (Mat4.java)
 *
 * todo: convert to int[16] instad of int[4][4]
 */
public class M4 {
    public int[][] m = new int[4][4];
    public static M4 xform = new M4();
	
    public M4() {
    }
	
    public M4(M4 other) {
        copy(other);
    }

    public final M4 copy(M4 other) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                m[i][j] = other.m[i][j];
            }
        }
        return this;
    }

    public final void multiply(Vec3 src, Vec3 dest) {
        // x = src.x * m[0][0] + src.y * m[0][1] + src.z * m[0][2] + m[0][3]
        // y = src.x * m[1][0] + src.y * m[1][1] + src.z * m[1][2] + m[1][3]
        // z = src.x * m[2][0] + src.y * m[2][1] + src.z * m[2][2] + m[2][3]
        long lx = src.x, ly = src.y, lz = src.z;
        dest.x = (int) ( ((lx * (long)m[0][0]) >> 16) +
                         ((ly * (long)m[0][1]) >> 16) +
                         ((lz * (long)m[0][2]) >> 16) +
                         ((     (long)m[0][3])      ) );
        dest.y = (int) ( ((lx * (long)m[1][0]) >> 16) +
                         ((ly * (long)m[1][1]) >> 16) +
                         ((lz * (long)m[1][2]) >> 16) +
                         ((     (long)m[1][3])      ) );
        dest.z = (int) ( ((lx * (long)m[2][0]) >> 16) +
                         ((ly * (long)m[2][1]) >> 16) +
                         ((lz * (long)m[2][2]) >> 16) +
                         ((     (long)m[2][3])      ) );
    }
	
    public final void multiply(M4 other, M4 dest) {
        int[][] m1 = m;
        int[][] m2 = other.m;
        
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                // m1[i][0]*m2[0][j] +
                // m1[i][1]*m2[1][j] +
                // m1[i][2]*m2[2][j] +
                // m1[i][3]*m2[3][j];
                dest.m[i][j] =
                    (int) ( (((long)m1[i][0] * (long)m2[0][j]) >> 16) +
                            (((long)m1[i][1] * (long)m2[1][j]) >> 16) +
                            (((long)m1[i][2] * (long)m2[2][j]) >> 16) +
                            (((long)m1[i][3] * (long)m2[3][j]) >> 16) );
            }
        }
    }
	

    public final M4 setIdentity() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                m[i][j] = (i == j ? FP.ONE : 0);
            }
        }
        return this;
    }


    public final M4 clear() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                m[i][j] = 0;
            }
        }
        return this;
    }


    public final void transpose(M4 dest) {
        for (int i=0; i<4; ++i) {
            for (int j=0; j<4; ++j) {
                dest.m[j][i] = m[i][j];
            }
        }
    }

    
    public final M4 scale(int x, int y, int z) {
        clear();
        m[0][0] = x;
        m[1][1] = y;
        m[2][2] = z;
        m[3][3] = FP.ONE;
        return this;
    }

    
    public final M4 scale(int scale) {
        clear();
        m[0][0] = scale;
        m[1][1] = scale;
        m[2][2] = scale;
        m[3][3] = FP.ONE;
        return this;
    }

    
    public final M4 translate(Vec3 v) {
        setIdentity();
        m[0][3] = v.x;
        m[1][3] = v.y;
        m[2][3] = v.z;
        return this;
    }

    
    public final M4 rotate(Vec3 v, int theta) {
        theta >>= 1; // * .5
        int s = FP.sin(theta);
        Vec4 q = new Vec4((int)(((long)v.x * (long)s) >> 16),
                          (int)(((long)v.y * (long)s) >> 16),
                          (int)(((long)v.z * (long)s) >> 16),
                          FP.cos(theta));
        return rotate(q);
    }


    /**
       Rotate by a Quarternion
       (see Mat4::MakeHRot() from SVL)
    */
    public final M4 rotate(Vec4 q) {
        setIdentity();

        int i2 = q.x << 1;
        int j2 = q.y << 1;
        int k2 = q.z << 1;
        int ij = (int)(((long)i2 * (long)q.y) >> 16);
        int ik = (int)(((long)i2 * (long)q.z) >> 16);
        int jk = (int)(((long)j2 * (long)q.z) >> 16);
        int ri = (int)(((long)i2 * (long)q.w) >> 16);
        int rj = (int)(((long)j2 * (long)q.w) >> 16);
        int rk = (int)(((long)k2 * (long)q.w) >> 16);

        i2 = (int)(((long)i2 * (long)q.x) >> 16);
        j2 = (int)(((long)j2 * (long)q.y) >> 16);
        k2 = (int)(((long)k2 * (long)q.z) >> 16);

//         m[0][0] = FP.ONE - j2 - k2;
//         m[0][1] = ij + rk;
//         m[0][2] = ik - rj;

//         m[1][0] = ij - rk;
//         m[1][1] = FP.ONE - i2 - k2;
//         m[1][2] = jk + ri;

//         m[2][0] = ik + rj;
//         m[2][1] = jk - ri;
//         m[2][2] = FP.ONE - i2 - j2;

        m[0][0] = FP.ONE - j2 - k2;
        m[0][1] = ij - rk;
        m[0][2] = ik + rj;

        m[1][0] = ij + rk;
        m[1][1] = FP.ONE - i2- k2;
        m[1][2] = jk - ri;
        
        m[2][0] = ik - rj;
        m[2][1] = jk + ri;
        m[2][2] = FP.ONE - i2 - j2;

        return this;
    }

    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[ ");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                builder.append(FP.toFloat(m[i][j]));
				builder.append(" ");
            }
            if (i < 3)
                builder.append("\n  ");
        }
        builder.append(" ]");
        return builder.toString();
    }
}
