package chum.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;


/**
 * A test suite containing all tests for my application.
 */
public class AllTests extends TestSuite {
    public static Test suite() {
        TestSuiteBuilder builder = new TestSuiteBuilder(AllTests.class);
        builder.includeAllPackagesUnderHere();
        builder.includePackages("chum.cfg",
                                "chum.engine",
                                "chum.examples",
                                "chum.util");
        return builder.build();
    }
}
