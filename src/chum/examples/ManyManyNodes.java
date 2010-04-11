package chum.examples;

import chum.engine.*;
import chum.engine.common.*;
import chum.gl.RenderNode;
import chum.util.Log;

import android.os.Bundle;


/**
   Test which creates a GameTree with a large number of nodes, primarily for the
   purpose of testing the raw overhead per node.
*/
public class ManyManyNodes extends GameActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected GameTree createGameTree() {
        return (new GameTree() {
                protected GameNode createLogicTree() {
                    return new FPSNode();
                }

                protected RenderNode createRenderTree() {
                    RenderNode node = new RenderNode();
                    for( int i=0; i<10; ++i ) {
                        GameNode inode = new RenderNode();
                        node.addNode(inode);
                        for( int j=0; j<5; ++j ) {
                            GameNode jnode = new RenderNode();
                            inode.addNode(jnode);
                            for( int k=0; k<5; ++k ) {
                                GameNode knode = new RenderNode();
                                jnode.addNode(knode);
                            }
                        }
                    }
                    return node;
                }
            });
    }

}

