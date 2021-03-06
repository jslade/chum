package chum.fp;

import junit.framework.TestCase;
import android.util.Log;


/**
 */
public class Vec3FPTest extends TestCase {

    protected void setUp() {
        Vec3FP zero = new Vec3FP();
    }


    public void test_add() {
        Vec3FP v1 = new Vec3FP(1f,2f,3f);
        Vec3FP v2 = new Vec3FP(-1f,-2f,-3f);
        Vec3FP v3 = new Vec3FP(1f,1f,1f);
        Vec3FP t = new Vec3FP();

        assertEquals("[1.000,2.000,3.000]",v1.toString());
        
        v1.add(v2,t);
        assertEquals("[0.000,0.000,0.000]",t.toString());

        v2.add(v3,t);
        assertEquals("[0.000,-1.000,-2.000]",t.toString());
    }


    public void test_length() {
        Vec3FP v0 = new Vec3FP(0f,0f,0f);
        Vec3FP v1 = new Vec3FP(1f,0f,0f);
        Vec3FP v2 = new Vec3FP(0f,1f,0f);
        Vec3FP v3 = new Vec3FP(0f,0f,1f);
        Vec3FP v4 = new Vec3FP(1f,1f,1f);

        Log.d("Vec3FPTest","v0="+v0);
        assertEquals(FP.floatToFP(0.0f),v0.length());
        assertEquals(FP.floatToFP(1.0f),v1.length());
        assertEquals(FP.floatToFP(1.0f),v2.length());
        assertEquals(FP.floatToFP(1.0f),v3.length());
        assertEquals(FP.floatToFP(1.73205f),v4.length());
    }


    public void test_dot() {
        Vec3FP v0 = new Vec3FP(0f,0f,0f);
        Vec3FP v1 = new Vec3FP(1f,0f,0f);
        Vec3FP v2 = new Vec3FP(0f,1f,0f);
        Vec3FP v3 = new Vec3FP(0f,0f,1f);
        Vec3FP v4 = new Vec3FP(1f,1f,1f);

        assertEquals(FP.intToFP(0),v0.dot(v0));
        assertEquals(FP.intToFP(1),v1.dot(v1));
        assertEquals(FP.intToFP(1),v2.dot(v2));
        assertEquals(FP.intToFP(1),v3.dot(v3));
        assertEquals(FP.intToFP(3),v4.dot(v4));
        assertEquals(FP.intToFP(0),v1.dot(v2));
        assertEquals(FP.intToFP(0),v2.dot(v3));
        assertEquals(FP.intToFP(1),v3.dot(v4));
    }
 

    public void test_cross() {
    }


    public void test_scale_1() {
        Vec3FP x1 = new Vec3FP(1f,2f,-3f);
        Vec3FP x2 = new Vec3FP();
        int t = FP.floatToFP(1f);

        x1.scale(t,x2);
        
        assertEquals(FP.floatToFP(1f),x2.x);
        assertEquals(FP.floatToFP(2f),x2.y);
        assertEquals(FP.floatToFP(-3f),x2.z);
    }
        

    public void test_scale_up() {
        Vec3FP x1 = new Vec3FP(1f,2f,-3f);
        Vec3FP x2 = new Vec3FP();
        int t = FP.floatToFP(10f);

        x1.scale(t,x2);
        
        assertEquals(FP.floatToFP(10f),x2.x);
        assertEquals(FP.floatToFP(20f),x2.y);
        assertEquals(FP.floatToFP(-30f),x2.z);
    }
        

    public void test_scale_dn() {
        Vec3FP x1 = new Vec3FP(1f,2f,-3f);
        Vec3FP x2 = new Vec3FP();
        int t = FP.floatToFP(.001f);

        x1.scale(t,x2);
        
        assertEquals(.001f,FP.toFloat(x2.x),0.0001f);
        assertEquals(.002f,FP.toFloat(x2.y),0.0001f);
        assertEquals(-.003f,FP.toFloat(x2.z),0.0001f);
    }
        

    public void test_scale_velo() {
        Vec3FP velo = new Vec3FP(0f,4.68f,15f);
        Vec3FP velo_t = new Vec3FP();
        int t = FP.floatToFP(0.032f);

        velo.scale(t,velo_t);
        Log.d("Vec3FPTest","velo="+velo+" velo_t="+velo_t+" t="+t);
        
        assertEquals(0f,FP.toFloat(velo_t.x));
        assertEquals(0.1497f,FP.toFloat(velo_t.y),0.0001);
        assertEquals(0.48f,FP.toFloat(velo_t.z),0.001);
    }
        

}
