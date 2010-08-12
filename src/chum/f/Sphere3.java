package chum.f;


/** 
    Spherical coords representing a point in 3D space, using float values
    (r,theta,phi):
    r = radial distance from the original
    theta = inclination angle (deflection relative to y-axis)
    phi = angle of rotation around y-axis, with 
 */
public class Sphere3 implements java.io.Serializable {

    public float r;
    public float theta; // angle of deflection from y-axis, radians
    public float phi; // angle of rotation around y-axis, radians

    public static Sphere3 ORIGIN = new Sphere3();

    public Sphere3() {
        this.r = 0f;
        this.theta = 0f;
        this.phi = 0f;
    }

    
    public Sphere3(Sphere3 o) {
        set(o);
    }


    public Sphere3(float r, float theta, float phi) {
        this.r = r;
        this.theta = theta;
        this.phi = phi;
    }


    public Sphere3(Vec3 vec) {
        set(vec);
    }


    public final Sphere3 set(Sphere3 o) {
        this.r = o.r;
        this.theta = o.theta;
        this.phi = o.phi;
        return this;
    }


    public final Sphere3 set(float r,float theta,float phi) {
        this.r = r;
        this.theta = theta;
        this.phi = phi;
        return this;
    }


    public static final float PI = 3.14159265f;
    public static final float PI_OVER_2 = PI / 2f;
    public static final float PI_TIMES_2 = PI * 2f;


    /**
       Convert a Vec3 (Cartesian coords) to spherical coords.
       This assumes a right-handed coordinate system, and
       positive y-axis up (reference for the theta angle)
    */
    public final Sphere3 set(Vec3 vec) {
        this.r = vec.length();
        if ( this.r == 0f ) {
            this.theta = 0f;
            this.phi = 0f;
            return this;
        }

        final float dy = vec.y/this.r;
        if ( dy == 0f ) {
            this.theta = PI_OVER_2;
        }
        else if ( dy < 0 ) {
            this.theta = (float)Math.acos(-dy) + PI;
        } else {
            this.theta = (float)Math.acos(dy);
        }
        if ( this.theta > PI )
            this.theta = -1f * (PI - this.theta);

        if ( vec.z == 0f ) {
            if ( vec.x == 0f )
                this.phi = 0f;
            else if ( vec.x > 0f )
                this.phi = PI_OVER_2;
            else
                this.phi = -PI_OVER_2;
        } else {
            final float xz = vec.x/vec.z;
            final float p = (float)Math.atan(Math.abs(xz));
           
            // which quadrant?
            if ( vec.z > 0f ) {
                if ( vec.x < 0f ) this.phi = PI_TIMES_2 - p; // quad IV
                else this.phi = p; // quad I
            } else {
                if ( vec.x < 0f ) this.phi = PI + p; // quad III
                else this.phi = PI - p; // quad II
            }
        }

        return this;
    }

    /**
       Add a deflection angle to theta.  Ensures that theta stays in range
       of [-pi,pi]
    */
    public void addDeflection(float delta_theta) {
        theta += delta_theta;
        normalize();
    }

    /**
       Add a rotation angle to phi.  Ensures that phi stays in range
       of [0,2pi]
    */
    public void addRotation(float delta_phi) {
        phi += delta_phi;
        normalize();
    }


    public void normalize() {
        while ( theta < 0f ) theta += PI_TIMES_2;
        while ( theta > PI_TIMES_2 ) theta -= PI_TIMES_2;

        while ( phi < 0f ) phi += PI_TIMES_2;
        while ( phi > PI_TIMES_2 ) phi -= PI_TIMES_2;
    }


    @Override
    public String toString() {
        return String.format("[%.3f,%d,%d]", r,theta,phi);
    }	


}

