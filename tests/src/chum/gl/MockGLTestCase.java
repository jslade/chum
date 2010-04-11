package chum.gl;

import chum.gl.*;

import android.test.AndroidTestCase;


/**
 */
public class MockGLTestCase extends AndroidTestCase {
    
    protected MockGL10 mockGL;
    protected MockRenderContext mockContext;

    protected void setUp() {
        mockGL = new MockGL10();
        mockContext = new MockRenderContext(getContext(),mockGL);
    }

}
