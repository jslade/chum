package chum.engine.common;

import chum.engine.GameSequence;
import chum.fp.Vec3;
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

        /** The start scale (FP) */
        public int startScale;
    
        /** The end scale (FP) */
        public int endScale;


        public Scale(TextNode node,long duration) {
            this(node,duration,linear);
        }


        public Scale(TextNode node,long duration, Interpolator interp) {
            super(node,duration,interp);
        }


        /** Set the start and end scale */
        public Scale setScale(int start, int end) {
            startScale = start;
            endScale = end;
            return this;
        }


        @Override
        protected void updateProgress() {
            super.updateProgress();
            textNode.scale = startScale + (int)((endScale - startScale) * progress);
        }
    }
        

    /**
       Animate the angle of the text
    */
    public static class Angle extends TextAnimation {

        /** The start angle (FP degrees) */
        public int startAngle;

        /** The end angle (FP degrees) */
        public int endAngle;


        public Angle(TextNode node,long duration) {
            this(node,duration,linear);
        }

        public Angle(TextNode node,long duration, Interpolator interp) {
            super(node,duration,interp);
        }


        /** Set the start and end angle */
        public Angle setAngle(int start, int end) {
            startAngle = start;
            endAngle = end;
            return this;
        }


        @Override
        protected void updateProgress() {
            super.updateProgress();
            textNode.angle = startAngle + (int)((endAngle - startAngle) * progress);
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
            pos.x = startPosition.x + (int)((endPosition.x - startPosition.x) * progress);
            pos.y = startPosition.y + (int)((endPosition.y - startPosition.y) * progress);
            pos.z = startPosition.z + (int)((endPosition.z - startPosition.z) * progress);
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

            col.red = startColor.red + (int)((endColor.red - startColor.red) * progress);
            col.green = startColor.green + (int)((endColor.green - startColor.green) * progress);
            col.blue = startColor.blue + (int)((endColor.blue - startColor.blue) * progress);
            col.alpha = startColor.alpha + (int)((endColor.alpha - startColor.alpha) * progress);
        }
    }

}
