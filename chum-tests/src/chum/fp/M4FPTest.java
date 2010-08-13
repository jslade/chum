package chum.fp;

import junit.framework.TestCase;
import android.util.Log;


/**
 */
public class M4Test extends TestCase {

    protected void setUp() {
    }


    public void test_translate1() {
        Vec3FP v = new Vec3FP(0f,0f,0f);
        M4 m = new M4();
        m.translate(new Vec3FP(1f,-1f,2f));
        m.multiply(v,v);
        
        assertEquals(1f,FP.toFloat(v.x),0.01);
        assertEquals(-1f,FP.toFloat(v.y),0.01);
        assertEquals(2f,FP.toFloat(v.z),0.01);
    }
        

    public void test_translate2() {
        Vec3FP v = new Vec3FP(0f,0f,0f);
        M4 m1 = new M4(), m2 = new M4();
        m1.translate(new Vec3FP(1f,-1f,2f));
        m2.translate(new Vec3FP(-1f,1f,-2f));
        m1.multiply(m2,m2);
        m2.multiply(v,v);
        
        assertEquals(0f,FP.toFloat(v.x),0.01);
        assertEquals(0f,FP.toFloat(v.y),0.01);
        assertEquals(0f,FP.toFloat(v.z),0.01);
    }
        

    public void test_rotate_around_y() {
        // Rotate a point ccw around the y axis
        Vec3FP v = new Vec3FP(1f,0f,0f);
        M4 m = new M4();
        m.rotate(new Vec3FP(0f,1f,0f), FP.PI_OVER_2);
        //Log.d("Test","test_rotate: m=\n"+m);
        m.multiply(v,v);
        
        assertEquals(0f,FP.toFloat(v.x),0.01f);
        assertEquals(0f,FP.toFloat(v.y),0.01f);
        assertEquals(-1f,FP.toFloat(v.z),0.01f);
    }


    public void test_rotate_around_x() {
        // Rotate a point cw around the x axis
        Vec3FP v = new Vec3FP(0f,0f,1f);
        M4 m = new M4();
        m.rotate(new Vec3FP(1f,0f,0f), -FP.PI_OVER_2);
        Log.d("Test","test_rotate: v="+v+" m=\n"+m);
        m.multiply(v,v);
        Log.d("Test","test_rotate: v'="+v);
        
        assertEquals(0f,FP.toFloat(v.x),0.01f);
        assertEquals(1f,FP.toFloat(v.y),0.01f);
        assertEquals(0f,FP.toFloat(v.z),0.01f);
    }

}
