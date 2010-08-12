package chum.gl;

import chum.fp.FP;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.util.regex.*;


/**
 * Representation of a color
 * <p>
 * Each of the color components are represented using floating-point
 * values, in the range [0,1].
 */
public class Color {

    public static final Color BLACK = new Color(0f,0f,0f);
    public static final Color WHITE = new Color(1f,1f,1f);
    public static final Color RED = new Color(1f,0f,0f);
    public static final Color GREEN = new Color(0f,1f,0f);
    public static final Color BLUE = new Color(0f,0f,1f);

    public float red = 0f;
    public float green = 0f;
    public float blue = 0f;
    public float alpha = 1f;
	
    public Color() {
    }

    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = 1f;
    }
	
    public Color(Color cp) {
        set(cp);
    }

    public Color(String str) {
        set(str);
    }

    public void set(Color cp) {
        this.red = cp.red;
        this.green = cp.green;
        this.blue = cp.blue;
        this.alpha = cp.alpha;
    }

    public void set(String str) {
        Pattern pat1 = Pattern.compile("#(....)(....)(....)");
        Matcher m = pat1.matcher(str);
        if ( m.matches() ) {
            this.red = Integer.parseInt(m.group(1),16)/65535f;
            this.green = Integer.parseInt(m.group(2),16)/65535f;
            this.blue = Integer.parseInt(m.group(3),16)/65535f;
        } else {
            Pattern pat2 = Pattern.compile("#(..)(..)(..)");
            m = pat2.matcher(str);
            if ( m.matches() ) {
                this.red = Integer.parseInt(m.group(1),16)/255f;
                this.green = Integer.parseInt(m.group(2),16)/255f;
                this.blue = Integer.parseInt(m.group(3),16)/255f;
            }
        }
    }

	
    public boolean equals(Object other) {
        if (other instanceof Color) {
            Color color = (Color)other;
            return (red == color.red && green == color.green &&
                    blue == color.blue && alpha == color.alpha);
        }
        return false;
    }


    /**
       Store this color into a color buffer, to be used with
       glColorPointer().
       <p>
       e.g. gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
    */
    public final void put(FloatBuffer colorBuffer) {
        colorBuffer.put(red);
        colorBuffer.put(green);
        colorBuffer.put(blue);
        colorBuffer.put(alpha);
    }

    
    /**
    Store this color into a color buffer, to be used with
    glColorPointer().
    <p>
    e.g. gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);
 */
 public final void put(IntBuffer colorBuffer) {
     colorBuffer.put(FP.floatToFP(red));
     colorBuffer.put(FP.floatToFP(green));
     colorBuffer.put(FP.floatToFP(blue));
     colorBuffer.put(FP.floatToFP(alpha));
 }

 
    @Override
    public String toString() {
        return "[" + (int)(255f * red) +
            "," + (int)(255f * green) +
            "," + (int)(255f * blue) +
            "," + (int)(255f * alpha) +
            "]";
    }
}
