package com.productivity.cloudtaskdo.tests;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by JuanCarlos on 20/01/2015.
 */
public class fullTestSuite extends TestSuite {

    public fullTestSuite() {
        super();
    }

    public static Test suite() {
        return new TestSuiteBuilder(fullTestSuite.class).includeAllPackagesUnderHere().build();
    }

}




