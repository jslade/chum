package chum.gl;

import chum.fp.FP;
import chum.util.Log;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;


import java.util.HashMap;


/**
   Font manages bitmap fonts for rendering text.

   Fonts call be loaded from TTF files, or directly specified via a bitmap image
   (e.g. a drawable resource).  When using a bitmap image, the locations of each
   character in the bitmap must also be given explicity
*/
public class Font {

    /** The rendering context */
    public RenderContext renderContext;

    /** The Painter used to create the glyphs */
    public Painter painter;

    /** The spacing to use between characters (FP) */
    public int spacing = FP.ONE;

    /** The Texture the manages the bitmap */
    public Texture texture;

    /** The character metrics for common characters */
    protected Glyph[] commonGlyphs;

    /** The common characters */
    protected static final String commonChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz`~!@#$%^&*()-_=+[]\\{}|;':\",./<>? ";

    /** The character metrics for less-common characters (extended unicode) */
    protected HashMap<Character,Glyph> moreGlyphs;
       

    /** Create a new Font, empty font */
    public Font(RenderContext renderContext) {
        init(renderContext);
    }


    /** Create a new Font, loading the font from an asset that is a font file */
    public Font(RenderContext renderContext, String assetName) {
        init(renderContext);
        loadFromAsset(assetName);
    }


    /** Create a new Font, loading the font from the given typeface */
    public Font(RenderContext renderContext, Typeface typeface, int size) {
        init(renderContext);
        loadFromTypeface(typeface,size);
    }


    /** Initialize */
    protected void init(RenderContext renderContext) {
        this.renderContext = renderContext;
        texture = new Texture();
        commonGlyphs = new Glyph[commonChars.length()];
        moreGlyphs = new HashMap<Character,Glyph>();
    }


    /**
       Set the painter instance
    */
    public void setPainter(Painter painter) {
        this.painter = painter;
    }


    /**
       Load the font from an asset that is a font file, using the default font size

       @param asset the filename as it is found in the assets folder
    */
    public void loadFromAsset(String asset) {
        loadFromAsset(asset,20);
    }


    /**
       Load the font from an asset that is a font file

       @param asset the filename as it is found in the assets folder
       @param size the font size
    */
    public void loadFromAsset(String asset, int size) {
        Typeface typeface = Typeface.createFromAsset(renderContext.appContext.getAssets(),asset);
        loadFromTypeface(typeface,size);
    }


    /**
       Load the font from an existing typeface
    */
    public void loadFromTypeface(Typeface typeface, int size) {
        painter = new Painter(typeface,size);
        paintCharacters(commonChars);
    }


    /**
       Load the font from an existing bitmap.  In this case, the locations of each
       of the characters in the bitmap must be defined by calling defineCharacter()
    */
    public void loadFromBitmap(Bitmap bitmap) {
        painter = new Painter(bitmap);
    }


    /**
       Paint all the characters into the font texture image, and create
       the Glyph data for each one.
    */
    public void paintCharacters(String chars) {
        if ( painter == null )
            throw new IllegalStateException("No painter defined");

        for(int i=0; i<chars.length(); ++i) {
            char ch = chars.charAt(i);
            Glyph glyph = painter.paintCharacter(ch);
            putGlyph(glyph);
        }

        // After characters are added to the texture, it needs to be
        // pushed to the GPU.
        texture.load(renderContext.gl10, painter.bitmap);
    }


    /**
       Define the location of a character that already exists in the font's bitmap.
       This is intended to be used in conjunction with loadFromBitmap()
       @param ch the character
       @param x the x location of the left edge of the character in the bitmap
       @param y the y location of the bottom edge of the character in the bitmap
       @param baseline the y offset from the bottom edge to the baseline of the character
       @param width the width of the character on the bitmap
       @param height the height of the character on the bitmap
    */
    public void defineCharacter(char ch, int x, int y, int baseline, int width, int height) {
        Glyph glyph = painter.defineCharacter(ch, x, y, baseline, width, height);
        putGlyph(glyph);
    }



    /** Get the width of a string, as a FP value */
    public int getStringWidth(String str) {
        int width = 0;
        for (int i=0, len=str.length(); i<len; ++i) {
            char ch = str.charAt(i);
            Glyph glyph = getGlyph(ch);
            width += glyph.width;
        }
        return width;
    }


    /** Get the height of a string, as a FP value */
    public int getStringHeight(String str) {
        int height = 0;
        for (int i=0, len=str.length(); i<len; ++i) {
            char ch = str.charAt(i);
            Glyph glyph = getGlyph(ch);
            height += glyph.height;
        }
        return height;
    }


    /**
       Construct a Text mesh to display the given string
    */
    public Text buildText(String str) {
        int len = str.length();
        Glyph[] glyphs = getGlyphs(len);
        getGlyphs(str,glyphs);
        return buildText(glyphs,0,len);
    }

    
    /**
       Construct a Text mesh to display the given characters
    */
    public Text builtText(char[] chars) {
        int len = chars.length;
        Glyph[] glyphs = getGlyphs(len);
        for (int i=0; i<len; ++i ) {
            Glyph glyph = getGlyph(chars[i]);
            glyphs[i] = glyph;
        }
        return buildText(glyphs,0,len);
    }


    /**
       Construct a Text mesh to display the given glyphs
    */
    public Text buildText(Glyph[] glyphs) {
        return buildText(glyphs, 0, glyphs.length);
    }


    /**
       Populate the given Text mesh with the given glyphs
    */
    public Text buildText(Glyph[] glyphs, int offset, int count) {
        Text text = new Text(count,spacing);
        text.font = this;
        return buildText(glyphs, offset, count, text);
    }


    /**
       Construct a Text designed to display the given glyphs.
    */
    public Text buildText(Glyph[] glyphs, int offset, int count, Text text) {
        text.setGlyphs(glyphs, offset,count);
        return text;
    }


    /**
       Store the glyph for future use.
    */
    public void putGlyph(Glyph glyph) {
        int pos = glyph.ch - '0';
        if ( pos >= 0 && pos <= commonGlyphs.length ) {
            commonGlyphs[pos] = glyph;
        } else {
            moreGlyphs.put(new Character(glyph.ch),glyph);
        }
    }


    /**
       Get the glyph corresponding to a specific character.  If the
       glyph for that character has not been defined in the font,
       returns the ' '(space) glyph.
    */
    public Glyph getGlyph(char ch) {
        Glyph glyph = null;
        int pos = ch - '0';
        if ( pos >= 0 && pos <= commonGlyphs.length ) {
            // fast lookup for the common chars
            glyph = commonGlyphs[pos];
        } else {
            // slower hash lookup for other chars
            glyph = moreGlyphs.get(new Character(ch));
        }

        if ( glyph == null ) {
            if ( ch == ' ' ) {
                throw new IllegalStateException("No ' ' glyph defined for font");
            } else {
                glyph = getGlyph(' ');
            }
        }

        return glyph;
    }


    /** Get a Glyph array big enough to hold the given number of
        characters.  The returned array may be bigger than the minimum
        requested.
    */
    protected Glyph[] getGlyphs(int length) {
        if ( reusableGlyphs == null ||
             reusableGlyphs.length < length )
            reusableGlyphs = new Glyph[length];
        return reusableGlyphs;
    }
    
    private Glyph[] reusableGlyphs;


    /** Populate a Glyph array with the glyphs for a given string */
    public void getGlyphs(String str, Glyph[] glyphs) {
        for (int i=0, len=str.length(); i<len; ++i ) {
            char ch = str.charAt(i);
            Glyph glyph = getGlyph(ch);
            glyphs[i] = glyph;
        }
    }

    

    /**
       Class for representing metrics per character in a font
    */
    public static class Glyph implements java.io.Serializable {

        /** The represented character */
        public char ch;

        /** x location of the glyph in the bitmap, in pixels */
        public int x;

        /** x location of the glyph in the bitmap, in pixels */
        public int y;

        /** The baseline location (y offset), in pixels */
        public int baseline;

        /** width of the glyph in the bitmap, in pixels */
        public int width;
        
        /** height of the glyph in the bitmap, in pixels */
        public int height;

        /** u Location of the glyph in the texture, in FP */
        public int texU;

        /** v Location of the glyph in the texture, in FP */
        public int texV;

        /** width of the glyph in the texture, in FP */
        public int texWidth;

        /** height of the glyph in the texture, in FP */
        public int texHeight;


        protected Glyph() {
            instance_count++;
        }

        protected void finalize() {
            instance_count--;
        }


        private static Glyph first_avail;
        private transient Glyph next_avail;
        private static Object sync = new Object();
        private static int instance_count;

        public static int instanceCount() { return instance_count; }


        public static Glyph obtain() {
            synchronized(sync) {
                if ( first_avail == null )
                    first_avail = new Glyph();
                Glyph g = first_avail;
                first_avail = g.next_avail;
                return g;
            }
        }


        public void recycle() {
            synchronized(sync) {
                next_avail = first_avail;
                first_avail = this;
            }
        }


        public Glyph set(char ch, int x, int y, int b, int w, int h,
                        int u, int v, int uw, int vh) {
            this.ch = ch;
            this.x = x;
            this.y = y;
            this.baseline = b;
            this.width = w;
            this.height = h;
            this.texU = u;
            this.texV = v;
            this.texWidth = uw;
            this.texHeight = vh;
            return this;
        }


        /** String representation (mainly for debugging/logging) */
        @Override
        public String toString() {
            return String.format("['%c'|%d,%d %dx%d %d|%.3f,%.3f %.3fx%.3f]",
                                 ch, x, y, width, height, baseline,
                                 FP.toFloat(texU),
                                 FP.toFloat(texV),
                                 FP.toFloat(texWidth),
                                 FP.toFloat(texHeight));
        }
    }


    /**
       Helper clas for painting glyphs into the font texture
    */
    public static class Painter {
        
        /** Typeface to generate characters from */
        public Typeface typeface;

        /** The font size */
        public int size;

        /** The bitmap for storing characters */
        public Bitmap bitmap;

        /** The canvas for painting to the bitmap */
        public Canvas canvas;

        /** The Paint for drawing characters */
        public Paint paint;
        

        /** The x location for the next character */
        protected int nextX;
        
        /** The y location for the next character */
        protected int nextY;

        protected char[] chars = new char[1];
        protected Rect charBounds = new Rect();
        protected int maxw, maxh;

        
        /**
           Construct a new painter instance
        */
        public Painter(Typeface typeface,int size) {
            this.typeface = typeface;
            this.size = size;
            createBitmap();
            createCanvas();
            createPaint();

            nextX = 0;
            nextY = bitmap.getHeight() - (int)Math.ceil(paint.descent());
        }

        /**
           Construct a new painter instance, using an existing bitmap.
           This is really a pseudo-painter, in that it is expected the
           characters are already in the bitmap
        */
        public Painter(Bitmap bitmap) {
            this.bitmap = bitmap;
            createCanvas();
            createPaint();
            clearCanvas();
        }

        protected void createBitmap() {
            int bitmapSize = 256; // todo: dynamically size bitmap as needed
            bitmap = Bitmap.createBitmap(bitmapSize,bitmapSize, Bitmap.Config.ARGB_8888);
        }

        protected void createCanvas() {
            canvas = new Canvas(bitmap);
        }

        protected void createPaint() {
            paint = new Paint();
            paint.setTypeface(typeface);
            paint.setTextSize(size);
            paint.setColor(0xffffffff);
            paint.setAntiAlias(true);
        }


        protected void clearCanvas() {
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            canvas.drawColor(0xffff0000);
        }


        /**
           Paint a character on the bitmap, and return the corresponding Glyph representing
           its location and size
        */
        public Glyph paintCharacter(char ch) {
            if ( typeface == null )
                throw new IllegalArgumentException("Can't paint into this bitmap");
                
            // Get the size needed for the char
            chars[0] = ch;
            paint.getTextBounds(chars,0,1,charBounds);
            int width = charBounds.right - charBounds.left;
            int height = (int)Math.ceil(paint.descent() - paint.ascent());
//             Log.d("paint '%c' x=%d y=%d bounds=[%d,%d %d,%d] w=%d h=%d asc=%.1f des=%.1f",
//                   ch, nextX, nextY,
//                   charBounds.left, charBounds.bottom,
//                   charBounds.right, charBounds.top,
//                   width, height,
//                   paint.ascent(), paint.descent());

            // Keep track of max width seen, which will be reserved for space
            if ( width == 0 ) width = maxw /2; // for space
            if ( width > maxw ) maxw = width;
            if ( height > maxh ) maxh = height;

            // Advance to next line if no space left on current
            int advance = width+2;
            if ( nextX + advance > bitmap.getWidth() ) {
                if ( nextY - maxh < 0 )
                    throw new IllegalStateException("Exceded capacity of font texture bitmap");
                nextY -= maxh;
                nextX = 0;
            }

            

            // Paint the character.  The coord given to drawText() is where
            // the baseline of the character goes.
            int baseline = (int)Math.ceil(paint.descent());
            canvas.drawText(chars, 0, 1, (float)(nextX-charBounds.left), (float)(nextY-baseline),
                            paint);

            // Fill in the Glyph
            Glyph glyph = defineCharacter(ch, nextX, nextY, baseline, width, height);
            nextX += advance;
                                          
            return glyph;
        }


        /**
           Define a glyph for a character that already exists in the bitmap.
        */
        public Glyph defineCharacter(char ch, int x, int y, int baseline,
                                     int width, int height) {
            int u = FP.floatToFP(x / (float)bitmap.getWidth());
            int v = FP.floatToFP(y / (float)bitmap.getHeight());
            int uw = FP.floatToFP(width / (float)bitmap.getWidth());
            int vh = FP.floatToFP(height / (float)bitmap.getHeight());

            Glyph glyph = Glyph.obtain();
            glyph.set(ch,
                      x, y, baseline, width, height,
                      u, v, uw, vh);

            return glyph;
        }
        
    }



}
   