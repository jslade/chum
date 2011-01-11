package chum.gl.render;

import chum.engine.GameNode;
import chum.engine.common.HookNode;
import chum.gl.RenderContext;
import chum.util.gl.TraceGL10;
import chum.util.gl.TraceGL11;


/**
   A node to trace opengl calls generated on its subtree
*/
public class TraceNode extends HookNode {

    /** The tracer */
    public TraceGL10 traceGL;
    
    
    /** Whether to pass thru calls to the real GL instance */
    public boolean pass = true;


    /** The number of iterations to trace.  < 0 means forever */
    public int traceCount = 1;
    
    
    /** Whether to automatically detach when tracing is done */
    public boolean detachAfterTracing = true;
    
    
    public TraceNode(GameNode node) {
        super(node);
    }


    @Override
    public void attach() {
        super.attach();
        annotateTree();
    }


    @Override
    public void detach() {
        deannotateTree();
        super.detach();
    }
    
    
    protected void setTracer(RenderContext renderContext) {
        if ( this.traceGL == null ) {
            if ( renderContext.isGL11 ) this.traceGL = new TraceGL11();
            else this.traceGL = new TraceGL10();
        }
    }
    
    
    public void annotateTree() {
        // For every child node (recursively), create
        // a Annotation hook node
        GameNode.Visitor annotater = new GameNode.Visitor(){
            public void run(GameNode node) { new Annotation(node,TraceNode.this); }
        };
        
        for(int i=0; i<realNode.num_children; ++i) {
            realNode.children[i].visit(annotater,false);
        }
    }

    
    public void deannotateTree() {
        GameNode.Visitor deannotater = new GameNode.Visitor(){
            public void run(GameNode node) {
                if ( !(node instanceof Annotation) ) return;
                Annotation anno = (Annotation)node;
                if ( anno.traceNode == TraceNode.this ) anno.detach();
            }
        };
        
        for(int i=0; i<realNode.num_children; ++i) {
            realNode.children[i].visit(deannotater,false);
        }
    }

    
    @Override
    public void render(RenderContext renderContext) {
        if ( traceCount == 0 ) {
            super.render(renderContext);
            return;
        }
        
        // Swap out the GL10 in the RenderContext, do the normal rendering,
        // then swap it back after renderPostfix
        traceGL.realGL10 = renderContext.gl10;
        renderContext.gl10 = traceGL;
        if (renderContext.gl11 != null && traceGL instanceof TraceGL11) {
            TraceGL11 trace11 = (TraceGL11)traceGL;
            trace11.realGL11 = renderContext.gl11;
            renderContext.gl11 = trace11;   
        }
        
        super.render(renderContext);
        
        // Swap the original GL10 back to the RenderContext
        renderContext.gl10 = traceGL.realGL10;
        if (renderContext.gl11 != null && traceGL instanceof TraceGL11) {
            TraceGL11 trace11 = (TraceGL11)traceGL;
            renderContext.gl11 = trace11.realGL11;
        }
        
        if ( traceCount > 0 ) traceCount--;
        if ( traceCount == 0 && detachAfterTracing ) {
            detach();
        }
    }


    /**
     * Annotation nodes are spliced into the tree under a TraceNode
     * to add trace events for every node that gets visited.  That makes
     * it clear where the specific GL call traces occur.
     * 
     * TODO: this is broken now with the actual GL calls occurring
     * separate from the traversal of the render nodes in the tree
     * 
     * @author jeremy
     *
     */
    public static class Annotation extends HookNode {

        protected TraceNode traceNode;
        
        public Annotation(GameNode realNode,TraceNode traceNode) {
            super(realNode);
            this.traceNode = traceNode;
        }
        
        @Override
        public void render(RenderContext renderContext) {
            traceNode.setTracer(renderContext);
            traceNode.traceGL.trace(String.format("+++ %s '%s'",realNode,realNode.name));
            super.render(renderContext);
            traceNode.traceGL.trace(String.format("--- %s '%s'",realNode,realNode.name));
        }
    }
}
