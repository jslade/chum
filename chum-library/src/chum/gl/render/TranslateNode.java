package chum.gl.render;

import chum.f.Vec3;
import chum.gl.RenderContext;
import chum.gl.render.primitive.Translate;


/**
   A node to set the active color
*/
public class TranslateNode extends TransformNode {

    /** The translation vector */
    public Vec3 position = new Vec3();
    
    /** The a phase render node for translation */
    protected Translate translateA = new Translate();

    /** The b phase render node for translation */
    protected Translate translateB = new Translate();

    public TranslateNode(Vec3 v,boolean push) {
        super(push);
        position.set(v);
    }


    public TranslateNode(Vec3 v) {
        this(v,true);
    }


    public TranslateNode() {
        this(Vec3.ORIGIN);
    }


    @Override
    public void renderTransform(RenderContext renderContext) {
        Translate xlat = renderContext.phase ? translateA : translateB;
        xlat.position.set(position);
        renderContext.add(xlat);
    }

}