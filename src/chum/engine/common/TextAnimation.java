package chum.engine.common;

import chum.engine.GameSequence;
import chum.f.Vec3;
import chum.gl.render.TextNode;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


/**
   A TextAnimation manages the animation of one or more attributes of a TextNode,
   and the associated Text that it displays.


*/
public class TextAnimation extends GameSequence.Interpolated {

    /** The TextNode being animated */
    public TextNode textNode;

    public TextAnimation(TextNode node,long duration) {
        this(node,duration,linear);
    }

    public TextAnimation(TextNode node,long duration, Interpolator interp) {
        super(duration,interp);
        this.textNode = node;
    }


    protected static Interpolator linear = new LinearInterpolator();


    /**
       Animate the scale of the text
    */
    public static class Scale extends TextAnimation {

        /** The start scale */
        public float startScale;
    
        /** The end scale */
        public float endScale;


        public Scale(TextNode node,long duration) {
            this(node,duration,linear);
        }


        public Scale(TextNode node,long duration, Interpolator interp) {
            super(node,duration,interp);
        }


        /** Set the start and end scale */
        public Scale setScale(float start, float end) {
            startScale = start;
            endScale = end;
            return this;
        }


        @Override
        protected void updateProgress() {
            super.updateProgress();
            textNode.scale = startScale + (float)((endScale - startScale) * progress);
        }
    }
        

    /**
       Animate the angle of the text
    */
    public static class Angle extends TextAnimation {

        /** The start angle (degrees) */
        public float startAngle;

        /** The end angle (degrees) */
        public float endAngle;


        public Angle(TextNode node,long duration) {
            this(node,duration,linear);
        }

        public Angle(TextNode node,long duration, Interpolator interp) {
            super(node,duration,interp);
        }


        /** Set the start and end angle */
        public Angle setAngle(float start, float end) {
            startAngle = start;
            endAngle = end;
            return this;
        }


        @Override
        protected void updateProgress() {
            super.updateProgress();
            textNode.angle = startAngle + (float)((endAngle - startAngle) * progress);
        }
    }


    /**
       Animate the position of the text
    */
    public static class Position extends TextAnimation {

        /** The start position */
        public Vec3 startPosition = new Vec3();
    
        /** The end position */
        public Vec3 endPosition = new Vec3();


        public Position(TextNode node,long duration) {
            this(node,duration,linear);
        }

        public Position(TextNode node,long duration, Interpolator interp) {
            super(node,duration,interp);
        }


        /** Set the start and end position */
        public Position setPosition(Vec3 start, Vec3 end) {
            startPosition.set(start);
            endPosition.set(end);
            return this;
        }


        @Override
        protected void updateProgress() {
            super.updateProgress();

            Vec3 pos = textNode.position;
            pos.x = startPosition.x + (float)((endPosition.x - startPosition.x) * progress);
            pos.y = startPosition.y + (float)((endPosition.y - startPosition.y) * progress);
            pos.z = startPosition.z + (float)((endPosition.z - startPosition.z) * progress);
        }
    }


    /**
       Animate the color of the text
    */
    public static class Color extends TextAnimation {

        /** The start color */
        public chum.gl.Color startColor = new chum.gl.Color();
    
        /** The end color */
        public chum.gl.Color endColor = new chum.gl.Color();


        public Color(TextNode node,long duration) {
            this(node,duration,linear);
        }

        public Color(TextNode node,long duration, Interpolator interp) {
            super(node,duration,interp);
        }


        /** Set the start and end color */
        public Color setColor(chum.gl.Color start, chum.gl.Color end) {
            startColor.set(start);
            endColor.set(end);
            return this;
        }


        @Override
        protected void updateProgress() {
            super.updateProgress();

            chum.gl.Color col = textNode.color;
            if ( col == null ) return;

            col.red = startColor.red + (float)((endColor.red - startColor.red) * progress);
            col.green = startColor.green + (float)((endColor.green - startColor.green) * progress);
            col.blue = startColor.blue + (float)((endColor.blue - startColor.blue) * progress);
            col.alpha = startColor.alpha + (float)((endColor.alpha - startColor.alpha) * progress);
        }
    }

}
