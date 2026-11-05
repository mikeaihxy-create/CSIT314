package idp.test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AllTests.class);
        System.out.println("Is the test successful?: " + result.wasSuccessful());
        for (Failure failure : result.getFailures()) {
            System.out.println("Failure: " + failure.toString());
        }
        System.out.println("Tests run: " + result.getRunCount()
                + ", failures: " + result.getFailureCount()
                + ", ignored: " + result.getIgnoreCount());
    }
}
