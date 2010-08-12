package chum.gl.render;

import chum.engine.common.TextAnimation;
import chum.f.Vec3;
import chum.gl.Color;
import chum.gl.Text;
import chum.gl.RenderContext;

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
    public float scale = 1f;

    /** The rotation angle (degrees 0-360) -- always around z-axis */
    public float angle = 0f;

    /** Optional color */
    public Color color;


    private boolean pushed;


    /**
       Create a TextNode, initially not displaying any text
    */
    public TextNode() {
        super();
        blend = true;
    }


    /**
       Create a TextNode to display the given text
    */
    public TextNode(Text text) {
        super();
        blend = true;
        setText(text);
    }

    
    /**
       Create a TextNode to displace the given string.

       A Text instance is created, but it is created without reference
       to a specific Font.  The Font must be set before any text will
       actually be rendered.
    */
    public TextNode(String str) {
        super();
        blend = true;
        setText(new Text(str));
    }


    /**
       Set new text
    */
    public void setText(Text text) {
        setMesh(text);
        this.text = text;
    }


    /**
       Set the color
       @param color the color to set as the current draw color before drawing the text
    */
    public void setColor(Color color) {
        if ( this.color == null ) this.color = new Color();
        this.color.set(color);
    }


    /**
       Set the position of the text
       @param position the vector to translate to before drawing the text
    */
    public void setPosition(Vec3 position) {
        if ( this.position == null ) this.position = new Vec3();
        this.position.set(position);
    }


    /**
       Set the scale of the text
       @param scale with 1.0 meaning no scaling
    */
    public void setScale(float scale) {
        this.scale = scale;
    }


    /**
       Set the angle of the text
       @param angle from 0-360
    */
    public void setAngle(float angle) {
        this.angle = angle;
    }



    /** When the surface is created, ensure that the mesh is setup to render */
    public void onSurfaceCreated(RenderContext renderContext) {
        if ( this.texture == null ) {
            if ( text.font != null )
                this.texture = text.font.texture;

            if ( this.texture == null )
                throw new IllegalStateException("Text for TextNode has no Texture");
        }

        super.onSurfaceCreated(renderContext);
    }


    /**
       Prepares the render state for drawing the text
    */
    public void renderPrefix(GL10 gl) {
        pushed = false;

        if ( position != null ) {
            if ( pushed == false ) gl.glPushMatrix();
            gl.glTranslatef(position.x,
                            position.y,
                            position.z);
            pushed = true;
        }

        if ( angle != 0f ) {
            if ( pushed == false ) gl.glPushMatrix();
            gl.glRotatef(angle,0f,0f,1f);
            pushed = true;
        }
            
        if ( scale != 1f ) {
            if ( pushed == false ) gl.glPushMatrix();
            gl.glScalef(scale,scale,scale);
            pushed = true;
        }

            
        if ( color != null ) {
            gl.glColor4f(color.red,color.green,color.blue,color.alpha);
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




    /**
       Scale the text smoothly
       @param start the starting scale factor
       @param end the ending scale factor
       @param duration the duration for the animation (millis)
       @return the new {@link TextAnimation.Scale instance}
    */
    public TextAnimation.Scale animateScale(float start, float end, long duration) {
        TextAnimation.Scale anim = new TextAnimation.Scale(this,duration);
        anim.setScale(start,end);
        anim.removeOnEnd = true;
        this.addNode(anim);
        return anim;
    }


    public TextAnimation.Scale animateScale(float end,long duration) {
        return this.animateScale(this.scale,end,duration);
    }

        

    /**
       Rotate the text smoothly
       @param start the starting angle (degrees)
       @param end the ending angle (degrees)
       @param duration the duration for the animation (millis)
       @return the new {@link TextAnimation.Angle instance}
    */
    public TextAnimation.Angle animateAngle(float start, float end, long duration) {
        TextAnimation.Angle anim = new TextAnimation.Angle(this,duration);
        anim.setAngle(start,end);
        anim.removeOnEnd = true;
        this.addNode(anim);
        return anim;
    }


    public TextAnimation.Angle animateAngle(float end,long duration) {
        return this.animateAngle(this.angle,end,duration);
    }


    /**
       Move the text smoothly
       @param start the starting position
       @param end the ending ending postion
       @param duration the duration for the animation (millis)
       @return the new {@link TextAnimation.Position instance}
    */
    public TextAnimation.Position animatePosition(Vec3 start, Vec3 end, long duration) {
        if ( this.position == null ) this.position = new Vec3();
        TextAnimation.Position anim = new TextAnimation.Position(this,duration);
        anim.setPosition(start,end);
        anim.removeOnEnd = true;
        this.addNode(anim);
        return anim;
    }


    public TextAnimation.Position animatePosition(Vec3 end,long duration) {
        if ( this.position == null ) this.position = new Vec3();
        return this.animatePosition(this.position,end,duration);
    }


    /**
       Change the text color smoothly
       @param start the starting color
       @param end the ending ending color
       @param duration the duration for the animation (millis)
       @return the new {@link TextAnimation.Color instance}
    */
    public TextAnimation.Color animateColor(Color start, Color end, long duration) {
        if ( this.color == null ) this.color = new Color();
        TextAnimation.Color anim = new TextAnimation.Color(this,duration);
        anim.setColor(start,end);
        anim.removeOnEnd = true;
        this.addNode(anim);
        return anim;
    }


    public TextAnimation.Color animateColor(Color end,long duration) {
        if ( this.color == null ) this.color = new Color();
        return this.animateColor(this.color,end,duration);
    }


    /**
       Change the text alpha smoothly
       @param start the starting alpha
       @param end the ending alpha
       @param duration the duration for the animation (millis)
       @return the new {@link TextAnimation.Color instance}
    */
    public TextAnimation.Color animateAlpha(float start, float end, long duration) {
        if ( this.color == null ) this.color = new Color(Color.BLACK);
        TextAnimation.Color anim = new TextAnimation.Color(this,duration);
        Color startColor = new Color(this.color);
        Color endColor = new Color(this.color);
        startColor.alpha = start;
        endColor.alpha = end;
        anim.setColor(startColor,endColor);
        anim.removeOnEnd = true;
        this.addNode(anim);
        return anim;
    }


    public TextAnimation.Color animateAlpha(float end,long duration) {
        if ( this.color == null ) this.color = new Color();
        return this.animateAlpha(this.color.alpha,end,duration);
    }



}
