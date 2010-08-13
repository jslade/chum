package chum.gl.render;

import chum.fp.*;
import chum.gl.*;


/**
 */
public class TranslateNodeTests extends MockGLTestCase {
    

    protected void setUp() {
        super.setUp();
    }


    public void testNoTranslation() {
        TranslateNode xlat = new TranslateNode();
        
        xlat.onSurfaceCreated(mockContext);
        assertEquals(0,mockGL.numCommands());

        // Translate always happens, even if vector is [0,0,0]
        xlat.update(0);
        assertEquals(1,mockGL.numCommands());
        assert(mockGL.contains("glTranslatex"));

        // Make it non-visible to have it do nothing
        xlat.visible = false;
        mockGL.clear();
        xlat.update(0);
        assertEquals(0,mockGL.numCommands());
    }

        
    public void testTranslation() {
        TranslateNode xlat = new TranslateNode(Vec3.Z_AXIS);

        xlat.onSurfaceCreated(mockContext);
        assertEquals(0,mockGL.numCommands());

        xlat.update(0);
        assertEquals(1,mockGL.numCommands());
        assert(mockGL.contains("glTranslatex"));

        xlat.push = true;
        mockGL.clear();
        xlat.update(0);
        assertEquals(3,mockGL.numCommands());
        assert(mockGL.contains("glPushMatrix",
                               "glTranslatex",
                               "glPopMatrix"));

    }

        
}
