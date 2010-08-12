package chum.fp;


/** 
    Spherical coords representing a point in 3D space, using FP values
    (r,theta,phi):
    r = radial distance from the original
    theta = inclination angle (deflection relative to y-axis)
    phi = angle of rotation around y-axis, with 
 */
public class Sphere3FP implements java.io.Serializable {

    public int r;
    public int theta; // angle of deflection from y-axis, radians
    public int phi; // angle of rotation around y-axis, radians

    public static Sphere3FP ORIGIN = new Sphere3FP();

    public Sphere3FP() {
        this.r = 0;
        this.theta = 0;
        this.phi = 0;
    }

    
    public Sphere3FP(Sphere3FP o) {
        set(o);
    }


    public Sphere3FP(float r, float theta, float phi) {
        this.r = FP.floatToFP(r);
        this.theta = FP.floatToFP(theta);
        this.phi = FP.floatToFP(phi);
    }


    public Sphere3FP(Vec3FP vec) {
        set(vec);
    }


    public final Sphere3FP set(Sphere3FP o) {
        this.r = o.r;
        this.theta = o.theta;
        this.phi = o.phi;
        return this;
    }


    public final Sphere3FP set(int r,int theta, int phi) {
        this.r = r;
        this.theta = theta;
        this.phi = phi;
        return this;
    }

        
    /**
       Convert a Vec3FP (Cartesian coords) to spherical coords.
       This assumes a right-handed coordinate system, and
       positive y-axis up (reference for the theta angle)
    */
    public final Sphere3FP set(Vec3FP vec) {
        this.r = vec.length();
        if ( this.r == 0 ) {
            this.theta = 0;
            this.phi = 0;
            return this;
        }

        final int dy = FP.div(vec.y,this.r);
        if ( dy == 0 ) {
            this.theta = FP.PI_OVER_2;
        }
        else if ( dy < 0 ) {
            this.theta = FP.acos(-dy) + FP.PI;
        } else {
            this.theta = FP.acos(dy);
        }
        if ( this.theta > FP.PI )
            this.theta = -1 * (FP.PI - this.theta);

        if ( vec.z == 0 ) {
            if ( vec.x == 0 )
                this.phi = 0;
            else if ( vec.x > 0 )
                this.phi = FP.PI_OVER_2;
            else
                this.phi = -FP.PI_OVER_2;
        } else {
            final int xz = FP.div(vec.x,vec.z);
            final int p = FP.atan(Math.abs(xz));
           
            // which quadrant?
            if ( vec.z > 0 ) {
                if ( vec.x < 0 ) this.phi = FP.PI_TIMES_2 - p; // quad IV
                else this.phi = p; // quad I
            } else {
                if ( vec.x < 0 ) this.phi = FP.PI + p; // quad III
                else this.phi = FP.PI - p; // quad II
            }
        }

        return this;
    }

    /**
       Add a deflection angle to theta.  Ensures that theta stays in range
       of [-pi,pi]
    */
    public void addDeflection(int delta_theta) {
        theta += delta_theta;
        normalize();
    }

    /**
       Add a rotation angle to phi.  Ensures that phi stays in range
       of [0,2pi]
    */
    public void addRotation(int delta_phi) {
        phi += delta_phi;
        normalize();
    }


    public void normalize() {
        while ( theta < 0 ) theta += FP.PI_TIMES_2;
        while ( theta > FP.PI_TIMES_2 ) theta -= FP.PI_TIMES_2;

        while ( phi < 0 ) phi += FP.PI_TIMES_2;
        while ( phi > FP.PI_TIMES_2 ) phi -= FP.PI_TIMES_2;
    }


    @Override
    public String toString() {
        return String.format("[%.3f,%d,%d]",
                             FP.toFloat(r),
                             FP.toInt(FP.rad2deg(theta)),
                             FP.toInt(FP.rad2deg(phi)));
    }


}

