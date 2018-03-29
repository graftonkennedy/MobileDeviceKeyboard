import java.io.*;
import java.util.*;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * TestRunner runs all tests defined in MobileDeviceKeyboardTest.
 * It requires that all of the MobileDeviceKeyboard*.java files be
 * compiled into .class files.
 */
public class TestRunner {
    /**
     * TestRunner.main directs the JUnitCore unit testing module to run
     * the tests from MobileDeviceKeyboardTest.  It prints the results
     * of all failed tests followed by "false" or simply "true" if no tests fail.
     * @param args - The standard main method arguments.  Not used.
     */
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(MobileDeviceKeyboardTest.class);
		
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
		
        System.out.println(result.wasSuccessful());
    }
}  	
