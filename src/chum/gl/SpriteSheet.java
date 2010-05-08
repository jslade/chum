package chum.gl;

import chum.fp.FP;


import android.graphics.Bitmap;
import javax.microedition.khronos.opengles.GL10;


/**
   A SpriteSheet is a special form of {@link Texture} containing
   {@link Sprite} images, as well as info defining the locations
   of each sprite image.

   SpriteSheet is used both to create the sprites (using a certain index
   into the sheet), as well drawing the sprites (by having the sprites
   reference into the sheet)

*/
public class SpriteSheet extends Texture {

    /** The location of each Sprite on the sheet */
    public ImageData[] data;

    /** The width of the texture image, in pixels */
    public int width;

    /** The height of the texture image, in pixels */
    public int height;
      


    /**
       Create a new SpriteSheet
    */
    public SpriteSheet() {
        this(1);
    }


    /**
       Create a new SpriteSheet sized for a specific number of sprites
       @param count the number of sprites / ImageData to be defined
       on the sheet
    */
    public SpriteSheet(int count) {
        super(1);
        this.data = new ImageData[count];
    }


    /**
       Load the texture image data and filters onto the GPU
    */
    public void load(GL10 gl, int num, Bitmap bmp) {
        super.load(gl,num,bmp);
        this.width = bmp.getWidth();
        this.height = bmp.getHeight();
    }


    /**
       Define the location of a sprite on the sheet
    */
    public void define(int index,
                       int left, int top,
                       int right, int bottom) {

        // Increase the storage for the ImageData if needed
        if ( data.length < index+1 ) {
            ImageData[] old = data;
            data = new ImageData[old.length * 2];
            for( int i=0; i<old.length; ++i ) data[i] = old[i];
        }

        data[index] = new ImageData(this, left, top, right, bottom,
                                    this.width, this.height);
    }


    /**
       Create a Sprite using the specified image data
    */
    public Sprite createSprite(int index, int x, int y) {
        if ( index >= data.length )
            throw new IllegalArgumentException("index out of range");

        ImageData image = data[index];
        if ( image == null )
            throw new IllegalArgumentException("image data not defined at index "+index);

        Sprite sprite = Sprite.obtain();
        sprite.update(image,x,y);

        return sprite;
    }


    /**
       Location of each sprite image on a SpriteSheet
    */
    public static class ImageData {
        /** The SpriteSheet this corresponds to */
        public SpriteSheet sheet;

        /** left edge location, in pixels */
        public int left; 

        /** top edge location, in pixels */
        public int top;

        /** right edge location, in pixels */
        public int right;
        
        /** bottom edge location, in pixels */
        public int bottom;

        /** left edge location, in FP texels */
        public int u1;
       
        /** top edge location, in FP texels */
        public int v1;
       
        /** right edge location, in FP texels */
        public int u2;
       
        /** bottom edge location, in FP texels */
        public int v2;


        public ImageData(SpriteSheet sheet,
                         int left, int top,
                         int right, int bottom,
                         int sheetWidth, int sheetHeight) {
            this.sheet = sheet;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            updateTexels(sheetWidth,sheetHeight);
        }

        
        protected void updateTexels(int width, int height) {
            if ( width == 0 || height == 0 ) {
                u1 = v1 = u2 = v2 = 0;
                return;
            }

            int w = FP.intToFP(width);
            int h = FP.intToFP(height);

            u1 = FP.div(FP.intToFP(left),w);
            v1 = FP.div(FP.intToFP(top),h);
            u2 = FP.div(FP.intToFP(right),w);
            v2 = FP.div(FP.intToFP(bottom),h);
        }
        
                         
    }


}


   