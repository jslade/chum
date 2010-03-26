package chum.gl;

import chum.util.Log;

import chum.fp.FP;
import java.nio.IntBuffer;
import java.util.regex.*;


/**
 * Representation of a color
 * <p>
 * Each of the color components are represented using fixed-point
 * values, in the range [0,1].  Fixed-point representation is used
 * because these colors are primarily intended to be paired with
 * vertexes for glDrawElements()
 *
 * Adapted from Android ApiDemos 'kube' sample (GLColor.java).p
 */
public class GLColor {

    public static GLColor BLACK = new GLColor(0,0,0);
    public static GLColor WHITE = new GLColor(0x10000,0x10000,0x10000);
    public static GLColor RED = new GLColor(0x10000,0,0);
    public static GLColor GREEN = new GLColor(0,0x10000,0);
    public static GLColor BLUE = new GLColor(0,0,0x10000);

    public int red = 0;
    public int green = 0;
    public int blue = 0;
    public int alpha = 0x10000;
	
    public GLColor() {
    }

    public GLColor(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public GLColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = 0x10000;
    }
	
    public GLColor(float red, float green, float blue) {
        this.red = FP.floatToFP(red);
        this.green = FP.floatToFP(green);
        this.blue = FP.floatToFP(blue);
        this.alpha = 0x10000;
    }

    public GLColor(float red, float green, float blue, float alpha) {
        this.red = FP.floatToFP(red);
        this.green = FP.floatToFP(green);
        this.blue = FP.floatToFP(blue);
        this.alpha = FP.floatToFP(alpha);
    }

    public GLColor(GLColor cp) {
        this.red = cp.red;
        this.green = cp.green;
        this.blue = cp.blue;
        this.alpha = cp.alpha;
    }

    public GLColor(String str) {
        set(str);
    }

    public void set(String str) {
        Pattern pat1 = Pattern.compile("#(....)(....)(....)");
        Matcher m = pat1.matcher(str);
        if ( m.matches() ) {
            this.red = FP.floatToFP(Integer.parseInt(m.group(1),16)/65535f);
            this.green = FP.floatToFP(Integer.parseInt(m.group(2),16)/65535f);
            this.blue = FP.floatToFP(Integer.parseInt(m.group(3),16)/65535f);
        } else {
            Pattern pat2 = Pattern.compile("#(..)(..)(..)");
            m = pat2.matcher(str);
            if ( m.matches() ) {
                this.red = FP.floatToFP(Integer.parseInt(m.group(1),16)/255f);
                this.green = FP.floatToFP(Integer.parseInt(m.group(2),16)/255f);
                this.blue = FP.floatToFP(Integer.parseInt(m.group(3),16)/255f);
            }
        }
    }

	
    public boolean equals(Object other) {
        if (other instanceof GLColor) {
            GLColor color = (GLColor)other;
            return (red == color.red && green == color.green &&
                    blue == color.blue && alpha == color.alpha);
        }
        return false;
    }


    /**
       Store this color into a color buffer, to be used with
       glColorPointer().
       <p>
       e.g. gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);
    */
    public final void put(IntBuffer colorBuffer) {
        colorBuffer.put(red);
        colorBuffer.put(green);
        colorBuffer.put(blue);
        colorBuffer.put(alpha);
    }

    
    @Override
    public String toString() {
        return "[" + (int)(255 * FP.toFloat(red)) +
            "," + (int)(255 * FP.toFloat(green)) +
            "," + (int)(255 * FP.toFloat(blue)) +
            "," + (int)(255 * FP.toFloat(alpha)) +
            "]";
    }
}