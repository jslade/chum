package chum.gl.render;

import chum.engine.common.Rotatable;
import chum.f.Vec3;
import chum.gl.RenderContext;
import chum.gl.render.primitive.Rotate;


/**
   A node to set the active color
*/
public class RotateNode extends TransformNode
    implements Rotatable
{

    /** The axis of the rotation */
    public Vec3 rotation;

    /** The angle, expressed in degrees */
    public float degrees;

    /** The a phase render node for translation */
    public Rotate rotateA = new Rotate();

    /** The b phase render node for translation */
    public Rotate rotateB = new Rotate();


    public RotateNode(float deg,Vec3 x,boolean push) {
        super(push);
        rotation = new Vec3(x);
        degrees = deg;
    }


    public RotateNode(float deg,Vec3 x) {
        this(deg,x,true);
    }

    
    public RotateNode() {
        this(0,Vec3.ORIGIN);
    }

    
    @Override
    public void renderTransform(RenderContext renderContext) {
        if ( degrees != 0f ) {
            Rotate rot = renderContext.phase ? rotateA : rotateB;
            rot.rotation.set(rotation);
            rot.degrees = degrees;
            renderContext.add(rot);
        }   
    }

    
    @Override
    public float getAngle() {
        return degrees;
    }


    @Override
    public Vec3 getAxis() {
        return rotation;
    }


    @Override
    public void setAngle(float angle) {
        degrees = angle;
    }


    @Override
    public void setAxis(Vec3 axis) {
        rotation.set(axis);        
    }
}
