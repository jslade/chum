package chum.fp;

import junit.framework.TestCase;


/**
 */
public class FPTests extends TestCase {

    protected void setUp() {

    }

    public void test_int_toFP_and_back() {
        int values[] = { 0, 1, -1,
                         100, -100,
                         32767, -32767 };
        for ( int i=0; i < values.length; i++ ) {
            int x1 = values[i];
            int x2 = FP.intToFP(x1);
            int x3 = FP.toInt(x2);
            assertEquals(x1, x3);
        }
    }
        
    public void test_float_toFP_and_back() {
        float values[] = { 0.0f, 1.0f, -1.0f,
                           0.1f, -0.1f,
                           0.0001f, -0.0001f,
                           100.0f, -100.0f,
                           32767.0f, -32767.0f,
                           32767.999f, -32767.999f };
        for ( int i=0; i < values.length; i++ ) {
            float x1 = values[i];
            int x2 = FP.floatToFP(x1);
            float x3 = FP.toFloat(x2);
            assertEquals(x1, x3,0.0001);
        }
    }


    public void testDiv() {
        float values[] = { 0.0f, 1.0f,
                           2.0f, 3.0f,
                           35.34f, 9.8f };
        for ( int i=0; i < values.length; i += 2 ) {
            float x = values[i];
            float y = values[i+1];
            int div = FP.div(FP.floatToFP(x),FP.floatToFP(y));
            float div_f = FP.toFloat(div);
            assertEquals(x/y,div_f,0.0001);
        }
    }


    public void testSqrt() {
        float values[] = { 0.0f, 1.0f,
                           2.0f, 3.0f, 4.0f,
                           0.1f, 100f, 5000f };
        for ( int i=0; i < values.length; i++ ) {
            float x1 = values[i];
            int fp = FP.floatToFP(x1);
            int sq = FP.sqrt(fp);
            float x2 = FP.toFloat(sq);
            assertEquals(Math.sqrt(x1),x2,0.0001);
        }
    }

    public void testConstants() {
        assertEquals(1, FP.toInt(FP.ONE));
        assertEquals(1f, FP.toFloat(FP.ONE));
        assertEquals(-1, FP.toInt(FP.NEG_ONE));
        assertEquals(-1f, FP.toFloat(FP.NEG_ONE));

        assert(FP.toFloat(FP.MAX) > 32000f);
        assert(FP.toFloat(FP.MIN) < -32000f);
    }


    public void test_rad2deg() {
        assertEquals(0f,FP.toFloat(FP.rad2deg(0)));
        assertEquals(90f,FP.toFloat(FP.rad2deg(FP.PI_OVER_2)),0.01f);
        assertEquals(180f,FP.toFloat(FP.rad2deg(FP.PI)),0.01f);
        assertEquals(-90f,FP.toFloat(FP.rad2deg(-FP.PI_OVER_2)),0.01f);
    }
}
