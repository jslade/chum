package chum.gl.render;

import chum.fp.*;
import chum.gl.*;


/**
 */
public class RotateNodeTests extends MockGLTestCase {
    

    protected void setUp() {
        super.setUp();
    }


    public void testNoRotation() {
        RotateNode rot = new RotateNode();

        rot.onSurfaceCreated(mockContext);
        assertEquals(0,mockGL.numCommands());

        rot.update(0);
        assertEquals(0,mockGL.numCommands());
    }

        
    public void testRotation() {
        RotateNode rot = new RotateNode(FP.intToFP(90),Vec3.Z_AXIS);

        rot.onSurfaceCreated(mockContext);
        assertEquals(0,mockGL.numCommands());

        rot.update(0);
        assertEquals(1,mockGL.numCommands());
        assert(mockGL.contains("glRotatex"));

        rot.push = true;
        mockGL.clear();
        rot.update(0);
        assertEquals(3,mockGL.numCommands());
        assert(mockGL.contains("glPushMatrix",
                               "glPopMatrix"));

    }

        
}
