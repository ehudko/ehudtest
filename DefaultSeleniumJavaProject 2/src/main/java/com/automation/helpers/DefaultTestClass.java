package com.automation.helpers;

import com.relevantcodes.extentreports.ExtentTestInterruptedException;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.lang.reflect.Method;


public class DefaultTestClass {

    public DriverManager driverManager = new DriverManager();
    public ExtentTestInterruptedException testexception;
    public Logger logger = Logger.getLogger(this.getClass());


    @AfterClass()
    public void afterClass() {
        logger.info("closing the report(AfterClass)" + Thread.currentThread().getName());
        driverManager.closeTestReport();
    }

    @BeforeMethod()
    public void beforeMethod(Method method) throws JSONException, IOException, InterruptedException {
        logger.info("beforeMethod started on thread " + Thread.currentThread().getName());
        driverManager.startReportFile(method.getName(), "Default");
        driverManager.startTest("Selenium template", method.getName(), "Functional test");
        driverManager.startDriver();
        AssertManager.driverManager = driverManager;
    }


    @AfterMethod()
    public void closeAppiumSession() {
        driverManager.closeDriver();
        driverManager.closeTest();
        logger.info("quited the driver on thread: " + Thread.currentThread().getName());
    }

    public void finelizeTest() {
        logger.info("finelizeTest on thread " + Thread.currentThread().getName());

        if (driverManager.htmlReporter.clickFailed) {
            logger.info("Click failed(finelizeTest) on thread: " + Thread.currentThread().getName());
            throw new RuntimeException("test failed");
        } else {
            driverManager.htmlReporter.saveScreenshot(LogStatus.INFO, "Test finished", "Done");
            logger.info("Done on thread: " + Thread.currentThread().getName());
        }
    }



}
