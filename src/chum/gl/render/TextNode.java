package chum.gl.render;

import chum.fp.FP;
import chum.fp.Vec3;
import chum.gl.Color;
import chum.gl.Text;
import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.gl.Text;
import chum.util.Log;


import javax.microedition.khronos.opengles.GL10;


/**
   A TextNode renders a Text string.
*/
public class TextNode extends MeshNode {
    
    /** The Text to be rendered */
    public Text text;

    /** Optional translation before drawing */
    public Vec3 position;
    
    /** Optional scaling before drawing */
    public int scale = FP.ONE;

    /** Optional color */
    public Color color;


    private boolean pushed;


    public TextNode() {
        super();
    }

    public TextNode(Text text) {
        super(text);
        this.text = text;
    }


    /**
       Set new text
    */
    public void setText(Text text) {
        this.mesh = text;
        this.text = text;
    }


    /**
       Set the color
       @param color the color to set as the current draw color before drawing the text
    */
    public void setColor(Color color) {
        this.color = color;
    }


    /**
       Set the position of the text
       @param position the vector to translate to before drawing the text
    */
    public void setPosition(Vec3 position) {
        this.position = position;
    }


    /**
       Set the scale of the text
       @param scale an FP scale value, when 1.0 means no scaling
    */
    public void setScale(int scale) {
        this.scale = scale;
    }



    /**
       Prepares the render state for drawing the text
    */
    public void renderPrefix(GL10 gl) {
        pushed = false;
        if ( position != null ) {
            gl.glPushMatrix();
            gl.glTranslatex(position.x,
                            position.y,
                            position.z);
            pushed = true;
        }

        if ( scale != FP.ONE ) {
            if ( pushed == false ) gl.glPushMatrix();
            gl.glScalex(scale,scale,scale);
            pushed = true;
        }

        if ( color != null ) {
            gl.glColor4x(color.red,color.green,color.blue,color.alpha);
        }
        
        // Super renderPrefix() is sufficient to actually draw the text mesh
        super.renderPrefix(gl);
    }


    /**
       Restore the previous drawing state after the text is drawn.
       If a translation or a scaling were a applied, restores the previous
       ModelView matrix
    */
    public void renderPostfix(GL10 gl) {
        if ( pushed ) gl.glPopMatrix();
        super.renderPostfix(gl);
    }


}
