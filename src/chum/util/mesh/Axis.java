package chum.util.mesh;

import chum.fp.FP;
import chum.fp.Vec3;
import chum.gl.Color;
import chum.gl.Mesh;
import chum.gl.MeshBuilder;
import chum.gl.VertexAttribute;
import chum.gl.VertexAttributes.Usage;


import javax.microedition.khronos.opengles.GL10;


/**
   A Mesh that draws the three axes as lines in different colors.  Mostly useful as a debugging
   aide to display orientation.  Build the mesh then use a MeshNode to display it.
*/
public class Axis extends Mesh {

    /** Create the axis with the given colors.

        @param x The color for the x-axis
        @param y The color for the y-axis
        @param z The color for the z-axis
        @param scale The scale of the axis (FP int)
     */
    public Axis(Color x, Color y, Color z, int scale) {
        super(true, true, false, 6, 6,
              new VertexAttribute(Usage.Position),
              new VertexAttribute(Usage.Color));

        this.type = GL10.GL_LINES;

        MeshBuilder builder = new MeshBuilder(this);
        Vec3 axis = new Vec3();
        if ( x != null ) {
            int v = builder.getNumVertices();
            axis.set(Vec3.X_AXIS);
            axis.scale(scale,axis);
            builder.addVertex(Vec3.ORIGIN, x);
            builder.addVertex(axis, x);
            builder.addIndex(v, v+1);
        }
        if ( y != null ) {
            int v = builder.getNumVertices();
            axis.set(Vec3.Y_AXIS);
            axis.scale(scale,axis);
            builder.addVertex(Vec3.ORIGIN, y);
            builder.addVertex(axis, y);
            builder.addIndex(v, v+1);
        }
        if ( z != null ) {
            int v = builder.getNumVertices();
            axis.set(Vec3.Z_AXIS);
            axis.scale(scale,axis);
            builder.addVertex(Vec3.ORIGIN, z);
            builder.addVertex(axis, z);
            builder.addIndex(v, v+1);
        }

        builder.build();
    }


    /** Create a default 3D axis with the given scale
        @param scale The scale of the axis (FP int)
    */
    public static Axis create3D(int scale) {
        return new Axis(Color.RED, Color.GREEN, Color.BLUE, scale);
    }
        

    /** Create a default 3D axis with the given scale
        @param scale The scale of the axis (float)
    */
    public static Axis create3D(float scale) {
        return new Axis(Color.RED, Color.GREEN, Color.BLUE, FP.floatToFP(scale));
    }
        

    /** Create a default 2D axis with the given scale
        @param scale The scale of the axis (FP int)
    */
    public static Axis create2D(int scale) {
        return new Axis(Color.RED, Color.GREEN, null, scale);
    }
        
    /** Create a default 2D axis with the given scale
        @param scale The scale of the axis (float)
    */
    public static Axis create2D(float scale) {
        return new Axis(Color.RED, Color.GREEN, null, FP.floatToFP(scale));
    }
        
}
