package chum.fp;

import junit.framework.TestCase;
import android.util.Log;


/**
 */
public class Sphere3Test extends TestCase {

    protected void setUp() {
    }


    public void test_constructor() {
        Sphere3 s = new Sphere3();
        assertEquals(0,s.r);
        assertEquals(0,s.theta);
        assertEquals(0,s.phi);

        s = new Sphere3(1f,2f,3f);
        assertEquals(FP.floatToFP(1f),s.r);
        assertEquals(FP.floatToFP(2f),s.theta);
        assertEquals(FP.floatToFP(3f),s.phi);
    }

    public void test_cart2sphere() {
        Vec3 v = new Vec3(0f,0f,0f);
        Sphere3 s = new Sphere3(v);
        assertEquals(0,s.r);
        assertEquals(0,s.theta);
        assertEquals(0,s.phi);

        // unit-x
        v.set(1f,0f,0f);
        s.set(v);
        assertEquals(FP.ONE,s.r);
        assertEquals(90f,FP.toFloat(FP.rad2deg(s.theta)),0.01f);
        assertEquals(FP.PI_OVER_2,s.phi);

        // unit-y
        v.set(0f,1f,0f);
        s.set(v);
        assertEquals(FP.ONE,s.r);
        assertEquals(0,s.theta);
        assertEquals(0,s.phi);

        // unit-z
        v.set(0f,0f,1f);
        s.set(v);
        assertEquals(FP.ONE,s.r);
        assertEquals(90f,FP.toFloat(FP.rad2deg(s.theta)),0.01f);
        assertEquals(0,s.phi);

        // neg x
        v.set(-.5f,0f,0f);
        s.set(v);
        assertEquals(FP.ONE/2,s.r);
        assertEquals(90f,FP.toFloat(FP.rad2deg(s.theta)),0.01f);
        assertEquals(-90f,FP.toFloat(FP.rad2deg(s.phi)),0.01f);

        // neg y
        v.set(0f,-2f,0f);
        s.set(v);
        assertEquals(FP.ONE*2,s.r);
        assertEquals(180f,FP.toFloat(FP.rad2deg(s.theta)),0.01f);
        assertEquals(0,s.phi);

        // unit-z
        v.set(0f,0f,-.3f);
        s.set(v);
        assertEquals(.3f,FP.toFloat(s.r),0.1f);
        assertEquals(90f,FP.toFloat(FP.rad2deg(s.theta)),0.01f);
        assertEquals(180f,FP.toFloat(FP.rad2deg(s.phi)),0.01f);


        v.set(1f,0f,1f);
        s.set(v);
        assertEquals(1.4142f,FP.toFloat(s.r),0.01);
        assertEquals(90f,FP.toFloat(FP.rad2deg(s.theta)),0.01f);
        assertEquals(45f,FP.toFloat(FP.rad2deg(s.phi)),0.01f);
    }


    public void test_sphere2cart() {
        Vec3 v1 = new Vec3(0f,0f,0f);
        Vec3 v2 = new Vec3();
        Sphere3 s = new Sphere3(v1);
        v2.set(s);
        assertEquals(v1.x,v2.x);
        assertEquals(v1.y,v2.y);
        assertEquals(v1.z,v2.z);

        v1.set(1f,2f,3f);
        s.set(v1);
        v2.set(s);
        assertEquals(FP.toFloat(v1.x),FP.toFloat(v2.x),0.01f);
        assertEquals(FP.toFloat(v1.y),FP.toFloat(v2.y),0.01f);
        assertEquals(FP.toFloat(v1.z),FP.toFloat(v2.z),0.01f);

        v1.set(38.3436f,0.0022f,32.3509f);
        s.set(v1);
        assertEquals(50f,FP.toFloat(s.r),0.2f);
        assertEquals(1.57f,FP.toFloat(s.theta),0.01f); // 90deg = pi/2
        assertEquals(.87f,FP.toFloat(s.phi),0.01f); // .87 = 50deg
        v2.set(s);
        assertEquals(FP.toFloat(v1.x),FP.toFloat(v2.x),0.01f);
        assertEquals(FP.toFloat(v1.y),FP.toFloat(v2.y),0.01f);
        assertEquals(FP.toFloat(v1.z),FP.toFloat(v2.z),0.01f);
    }


    public void test_spinAround() {
        Vec3 v = new Vec3(0f,0f,50f);
        Sphere3 sph = new Sphere3(v);

        assertEquals(0f,FP.toFloat(FP.rad2deg(sph.phi)));

        int last = sph.phi;
        for ( int i=0; i<360; ++i ) {
            sph.set(v);
            //Log.d("Test","["+i+"] = "+v+" = "+sph+" last="+FP.toInt(last));
            assertEquals(FP.toFloat(FP.rad2deg(sph.phi)),
                         FP.toFloat(last),1.0f);

            sph.phi += FP.deg2rad(FP.ONE);
            last = FP.rad2deg(sph.phi);
            v.set(sph);
        }
    }
        
}
