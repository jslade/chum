package chum.cfg;

import chum.util.DefaultExceptionHandler;


import android.test.AndroidTestCase;


public class ConfigTests extends AndroidTestCase {


    protected void setUp() throws Exception {
        super.setUp();

    }



    protected void tearDown() throws Exception {

        super.tearDown();
    }


    public void testCreate() {
        Config config = new Config(getContext());
    }


    public void testGetGlobalConfig() {
        Config config = Config.getConfig(getContext());
    }

    public void testDefaultExceptionHandler() {
        Config config = Config.getConfig(getContext());
        DefaultExceptionHandler deh = config.defaultExceptionHandler();
        assertTrue( deh != null );
    }

}
