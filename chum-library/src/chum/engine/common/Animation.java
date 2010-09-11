package chum.engine.common;

import chum.engine.GameSequence;
import chum.f.Vec3;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


/**
   A NodeAnimation manages the animation of one or more attributes of a Node.
*/
public abstract class Animation extends GameSequence.Interpolated {

    protected Animation(long duration) {
        this(duration, linear);
    }
    
    protected Animation(long duration, Interpolator interp) {
        super(duration,interp);
    }
    

    protected static Interpolator linear = new LinearInterpolator();


    /**
       Animate the scale of the node.  The node type must have a 'scale' member
    */
    public static class Scale extends Animation {

        public Scalable scalable;
        
        /** The start scale */
        public float startScale;
    
        /** The end scale */
        public float endScale;


        public Scale(Scalable scalable,long duration) {
            this(scalable,duration,linear);
        }


        public Scale(Scalable scalable,long duration, Interpolator interp) {
            super(duration,interp);
            this.scalable = scalable;
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
            if ( scalable == null ) return;
            scalable.setScale(startScale + ((endScale - startScale) * progress));
        }
        
        
        private static Scale first_avail;
        
        public static Scale obtain() {
            if ( first_avail == null ) first_avail = new Scale(null,0);
            Scale scale = first_avail;
            first_avail = (Scale)first_avail.next_avail;
            scale.resetAll();
            return scale;
        }

        @Override
        public void recycle() {
            this.next_avail = first_avail;
            first_avail = this;
        }
    }
        

    /**
       Animate the angle of the text
    */
    public static class Angle extends Animation {

        /** The node being animated */
        public Rotatable rotatable;
        
        /** The start angle (degrees) */
        public float startAngle;

        /** The end angle (degrees) */
        public float endAngle;


        public Angle(Rotatable rotatable,long duration) {
            this(rotatable,duration,linear);
        }

        public Angle(Rotatable rotatable,long duration, Interpolator interp) {
            super(duration,interp);
            this.rotatable = rotatable;
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
            if (rotatable == null) return;
            rotatable.setAngle(startAngle + ((endAngle - startAngle) * progress));
        }
        

        private static Angle first_avail;
        
        public static Angle obtain() {
            if ( first_avail == null ) first_avail = new Angle(null,0);
            Angle angle = first_avail;
            first_avail = (Angle)first_avail.next_avail;
            angle.resetAll();
            return angle;
        }

        @Override
        public void recycle() {
            this.next_avail = first_avail;
            first_avail = this;
        }
        
    }


    /**
       Animate the position of the text
    */
    public static class Position extends Animation {

        /** The node being animated */
        public Movable movable;
        
        /** The start position */
        public Vec3 startPosition = new Vec3();
    
        /** The end position */
        public Vec3 endPosition = new Vec3();


        public Position(Movable movable,long duration) {
            this(movable,duration,linear);
        }

        public Position(Movable movable,long duration, Interpolator interp) {
            super(duration,interp);
            this.movable = movable;
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
            if ( movable == null) return;
            
            Vec3 pos = movable.getPosition();
            if ( pos == null ) return;
            
            pos.x = startPosition.x + ((endPosition.x - startPosition.x) * progress);
            pos.y = startPosition.y + ((endPosition.y - startPosition.y) * progress);
            pos.z = startPosition.z + ((endPosition.z - startPosition.z) * progress);
        }
        

        private static Position first_avail;
        
        public static Position obtain() {
            if ( first_avail == null ) first_avail = new Position(null,0);
            Position position = first_avail;
            first_avail = (Position)first_avail.next_avail;
            position.resetAll();
            return position;
        }

        @Override
        public void recycle() {
            this.next_avail = first_avail;
            first_avail = this;
        }
        
        
    }


    /**
       Animate the color of the text
    */
    public static class Color extends Animation {

        /** The node being animated */
        public Colorable colorable;
        
        /** The start color */
        public chum.gl.Color startColor = new chum.gl.Color();
    
        /** The end color */
        public chum.gl.Color endColor = new chum.gl.Color();


        public Color(Colorable colorable,long duration) {
            this(colorable,duration,linear);
        }

        public Color(Colorable colorable,long duration, Interpolator interp) {
            super(duration,interp);
            this.colorable = colorable;
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
            if ( colorable == null ) return;

            chum.gl.Color col = colorable.getColor();
            if ( col == null ) return;

            col.red = startColor.red + ((endColor.red - startColor.red) * progress);
            col.green = startColor.green + ((endColor.green - startColor.green) * progress);
            col.blue = startColor.blue + ((endColor.blue - startColor.blue) * progress);
            col.alpha = startColor.alpha + ((endColor.alpha - startColor.alpha) * progress);
        }
        

        private static Color first_avail;
        
        public static Color obtain() {
            if ( first_avail == null ) first_avail = new Color(null,0);
            Color c = first_avail;
            first_avail = (Color)first_avail.next_avail;
            c.resetAll();
            return c;
        }

        @Override
        public void recycle() {
            this.next_avail = first_avail;
            first_avail = this;
        }
        
    }

}
