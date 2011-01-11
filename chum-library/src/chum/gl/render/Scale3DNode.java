package chum.gl.render;

import chum.gl.RenderContext;
import chum.gl.render.primitive.Scale3D;


/**
   A node to set the scale factor
*/
public class Scale3DNode extends TransformNode {

    /** The x-axis scale factor */
    public float x;

    /** The y-axis scale factor */
    public float y;

    /** The z-axis scale factor */
    public float z;

    /** The a phase render node for translation */
    public Scale3D scaleA = new Scale3D();

    /** The b phase render node for translation */
    public Scale3D scaleB = new Scale3D();


    public Scale3DNode(float x,float y,float z,boolean push) {
        super(push);
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public Scale3DNode(float x, float y, float z) {
        this(x,y,z,true);
    }
    

    public Scale3DNode() {
        this(1f,1f,1f);
    }
    

    @Override
    public void renderTransform(RenderContext renderContext) {
        Scale3D prim = renderContext.phase ? scaleA : scaleB;
        prim.scaleX = x;
        prim.scaleY = y;
        prim.scaleZ = z;
        renderContext.add(prim);
    }

}