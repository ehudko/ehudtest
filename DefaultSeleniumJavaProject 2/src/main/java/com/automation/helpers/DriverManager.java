package com.automation.helpers;


import com.google.common.base.Predicate;
import com.relevantcodes.extentreports.LogStatus;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class DriverManager {

    public WebDriverWait wait = null;
    public ReportManager htmlReporter = new ReportManager();
    public Logger logger = Logger.getLogger(this.getClass());
    private WebDriver driver;


    public WebDriver getDriver() {
        return this.driver;
    }

    public void setCurrentDriver(DriverManager manager,String devicename) {
        this.driver = manager.getDriver();
    }

    public void closeTestReport() {
        htmlReporter.closeTestReport();
    }

    public void startReportFile(String phoneDir, String suiteDir) {
        htmlReporter.startReportFile(phoneDir, suiteDir);
    }

    public void saveScreenshot(LogStatus logStatus, String stepName, String details) {
        htmlReporter.saveScreenshot(this.driver, logStatus, stepName, details);
    }

    public void saveScreenshot(WebDriver driver, LogStatus logStatus, String stepName, String details) {
        htmlReporter.setScreenshotDriver(driver);
        try {
            htmlReporter.saveScreenshot(logStatus, stepName, details);
        } catch (NullPointerException e) {
            e.printStackTrace();
            htmlReporter.saveScreenshot(logStatus, stepName, details);
        }
    }

    public void startTest(String suiteName, String methodeName, String category) {
        htmlReporter.startTest(suiteName, methodeName, category);
    }


    public void startDriver() {
        try {
            setUpSelenium();
            logger.info("The driver started.");
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            saveScreenshot(LogStatus.PASS, "The driver started.", "Success");
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            wait = new WebDriverWait(driver, 10 * 60);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't start the driver");
        }
    }

    private void setUpSelenium() {
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/main/resources/chromedriver_2");
    }

    public void navigateTo(String url) {
        driver.get(url);
        saveScreenshot(LogStatus.PASS, "Navigating to: " + url, "Success");
    }

    public void closeTest() {
        htmlReporter.closeTest();
    }

    public void closeDriver() {
        try {
           driver.quit();
        } catch (Exception e) {
        }
        logger.info("Selenium driver were stopped ");
    }


    public void reportException(Exception e) {
        htmlReporter.reportException(e);
    }

    public void reportException(Exception e, boolean throwException) {
        htmlReporter.reportException(e, throwException);
    }

    public String executecommand(String command) {
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                logger.info("Result line: " + line + "\n");
                output.append(line + "\n");
            }
            p.destroy();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public void JSsenddKeys(String value, WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;;
        executor.executeScript("arguments[0].setAttribute('value','" + value + "'", element);
    }

    protected void sendKeyboardKeys(int number, String description) {

        try {
            ((AndroidDriver) driver).pressKeyCode(number);
            logger.info("Press on the " + description + " key on the keyboard.");
            saveScreenshot(LogStatus.PASS, "Press on the " + description + " key on the keyboard.", "True");


        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Fail to press on the key" + description);
            saveScreenshot(LogStatus.FAIL, "Fail to press on the key" + description, "False");
        }
    }


    public WebElement waitUntilWithCondition(String expectedConditions, By locator) {
        try {
            switch (expectedConditions.toLowerCase()) {
                case "clickable":
                    return wait.until(ExpectedConditions.elementToBeClickable((WebElement) driver.findElement(locator)));
            }
        } catch (Exception e) {
            logger.info("Timeout waiting to element to be " + expectedConditions);
        }
        return null;
    }

    public WebElement waitUntilWithCondition(String expectedConditions, WebElement elem) {
        try {
            switch (expectedConditions.toLowerCase()) {
                case "clickable":
                    return wait.until(ExpectedConditions.elementToBeClickable(elem));
            }
        } catch (Exception e) {
            logger.info("Timeout waiting to element to be " + expectedConditions);
        }
        return null;
    }



    public void clickElement(WebElement element, String description) // clicking element
    {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            highlight(element);
            element.click();
            System.out.println("Clicked on " + description + " element");
            saveScreenshot(LogStatus.PASS, "Clicked on " + description + " element", "Clicked succeeded");
        } catch (Exception msg) {
            msg.printStackTrace();
            saveScreenshot(LogStatus.FAIL, "Clicked on " + description + " element", "Click were not succeeded");
            htmlReporter.clickFailed = true;
            logger.info("click Failed value: " + htmlReporter.clickFailed);
        }
    }


    public void scrollToElement(By by){
        WebElement webElement = driver.findElement(by);
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", webElement);
    }

    public void scrollToElement(WebElement element){
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", element);
    }

    // This function send keys to input, and verify that this keys appear in
    // input
    public void sendKeysToWebElementInput(WebElement web_element, String target_input) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(web_element));
            highlight(web_element);
            web_element.clear();
            web_element.sendKeys(target_input);
            logger.info("Target keys sent to WebElement: " + target_input);
            saveScreenshot(LogStatus.PASS, "Entered and sent the string: " + target_input, "Target keys sent.");

        } catch (Exception msg) {
            msg.printStackTrace();
            logger.info("Fail to sent target keys: " + target_input);
            saveScreenshot(LogStatus.FAIL, "Entered and sent the string: " + target_input, "String were NOT sent");
            throw new RuntimeException("String were NOT sent");
        }
    }

    public void sendKeysToWebElementInput(By by, String target_input) {
        WebElement web_element = driver.findElement(by);
        sendKeysToWebElementInput(web_element, target_input);
    }

    public void verifyThatTheTextOfTheElementIsAsExpected(WebElement element, String... params) {
        String orignalName = element.getText();
        if (orignalName != null) {
            for (String name : params) {
                if (orignalName.equals(name)) {
                    logger.info("The text of the element is: " + orignalName + " as expected.");
                    saveScreenshot(LogStatus.PASS, "The text of the element is: " + orignalName + " as expected.", "True");

                    return;
                }
            }
        }
        logger.info("The text of the element is: " + orignalName + " not as expected.");
        saveScreenshot(LogStatus.FAIL, "The text of the element is: " + orignalName + "not as expected.", "False");

    }

    public Boolean verifyTextPresence(By elementLocator, String wantedText) {
        Boolean passed = false;
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(elementLocator, wantedText));
            WebElement element = driver.findElement(elementLocator);

            if (element.isDisplayed()) {
                String text = element.getText();
                if (text.contains(wantedText)) {
                    passed = true;
                    logger.info("The wanted text appeaerd in the wanted element. The text is:  " + text);
                    saveScreenshot(LogStatus.PASS, "The wanted text appeaerd in the wanted element. The text is:  " + text, "Success");

                } else {
                    logger.info("Can't find the  wanted text " + wantedText + " found the text: " + text);
                    saveScreenshot(LogStatus.FAIL, "Can't find the  wanted text " + wantedText + " found the text: " + text, "Failed");

                }
            } else {
                logger.info("Can't find the wanted element and text,He is not displayed");
                saveScreenshot(LogStatus.FAIL, "Can't find the wanted element and text,He is not displayed", "Failed");

            }
        } catch (Exception e) {
            logger.info("Can't find the wanted element and text,He is not displayed");
            saveScreenshot(LogStatus.FAIL, "Can't find the wanted element and text,He is not displayed", "Failed");

        }

        return passed;
    }

    public boolean tryToCompareAttributeValue(WebElement element, String attributName, String wantedValue) {
        try {
            return element.getAttribute(attributName).contains(wantedValue);
        } catch (Exception e) {
            return false;
        }
    }

    public void highlight(WebElement element) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element);
    }

    void waitForVisibility(WebElement element) {

        try {
            Thread.sleep(500);
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (org.openqa.selenium.TimeoutException e) {
            logger.info("Waiting for element visibiliy failed");
            e.printStackTrace();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            logger.info("Waiting for element visibiliy failed");
            e.printStackTrace();
        } catch (Exception e) {
            logger.info("Waiting for element visibiliy failed");
            e.printStackTrace();
        }
    }

    public void longTapOnLocation(int x, int y, int duration, String description) // clicking element
    {

        try {

               TouchAction action = new TouchAction((AppiumDriver) driver);
                action.press(x, y).waitAction(500).release().perform();
        } catch (Exception msg) {
            saveScreenshot(LogStatus.FAIL, "Long Tap was NOT done on location: x: " + x + ",y: " + y + " , duration: " + duration, "Tap  did not succeeded");
            htmlReporter.clickFailed = true;
            logger.info("click Failed value: " + htmlReporter.clickFailed);
            return;
        }
        logger.info("Long Tap was done on location: x: " + x + ",y: " + y + " , duration: " + duration + ", for " + description);
        saveScreenshot(LogStatus.PASS, "Long Tap was done on location: x: " + x + ",y: " + y + " , duration: " + duration + ", for " + description, "Clicked succeeded");

    }

    private boolean isElementDisplayed(WebElement element) {

        boolean isDisplay = false;
        try {
            if (element.isDisplayed()) {
                isDisplay = true;
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            isDisplay = false;
        }
        return isDisplay;
    }

    public boolean isElementDisplayed(By by) {
        boolean isDisplay = false;

        try {
            WebElement element = driver.findElement(by);
            if (element.isDisplayed()) {
                isDisplay = true;
            }
        } catch (WebDriverException e) {
            isDisplay = false;
        }
        return isDisplay;
    }

    public void verifyElementIsDisplayed(WebElement element, String description) {

        if (isElementDisplayed(element)) {
            logger.info("The element: " + description + " is displayed.");
            saveScreenshot(LogStatus.PASS, "Element is displayed.", "True");

        } else {
            logger.info("The element: " + description + " is not displayed.");
            saveScreenshot(LogStatus.FAIL, "Element is NOT displayed.", "Element is NOT displayed.");

        }

    }

    public void waitForElementBeDisplay(By by, int timeOutInSec) {
        if (timeOutInSec == 0) {
            throw new NoSuchElementException("Failed to located :" + by);
        }
        try {
            Thread.sleep(1000);
            timeOutInSec--;
            WebElement element = driver.findElement(by);
            if (element.isDisplayed()) {
                return;
            } else {
                waitForElementBeDisplay(by, timeOutInSec);
            }
        } catch (Exception e) {
            logger.info("element doesn't exist, trying again. iteration number: " + timeOutInSec + " exception message: " + e.getMessage());
            waitForElementBeDisplay(by, timeOutInSec);
        }
    }

    public boolean waitForElementBeDisplayWithoutThrowingTimeoutException(By by, int timeOutInSec) {
        if (timeOutInSec == 0) {
            return false;
        }
        try {
            Thread.sleep(1000);
            timeOutInSec--;
            WebElement element = driver.findElement(by);
            if (element.isDisplayed()) {
                return true;
            } else {
                waitForElementBeDisplayWithoutThrowingTimeoutException(by, timeOutInSec);
                return false;
            }
        } catch (Exception e) {
            logger.info("element doesn't exist, trying again. iteration number: " + timeOutInSec + " exception message: " + e.getMessage());
            waitForElementBeDisplayWithoutThrowingTimeoutException(by, timeOutInSec);
            return false;
        }
    }

    protected boolean waitUntilTextIsPresent(By by, String regex, long timeoutSeconds, long pollingMilliseconds) {
        return waitUntilTextIsPresent(by, regex, timeoutSeconds, pollingMilliseconds, true);
    }

    protected boolean waitUntilTextIsPresent(By by, String regex, long timeoutSeconds, long pollingMilliseconds, boolean reportFail) {
        final String[] text = {""};
        final String[] lasttext = {""};
        boolean foundText = false;
        try {
            final WebElement webElement = driver.findElement(by);
            new FluentWait<AppiumDriver>((AppiumDriver) driver)
                    .withTimeout(timeoutSeconds, TimeUnit.SECONDS)
                    .pollingEvery(pollingMilliseconds, TimeUnit.MILLISECONDS)
                    .until((Predicate<AppiumDriver>) d -> {
                        Pattern p = Pattern.compile(regex);
                        lasttext[0] = webElement.getText();
                        if (lasttext[0] != null) {
                            text[0] = lasttext[0];
                            Matcher m = p.matcher(text[0]);
                            boolean found = m.find();
                            if (found) {
                                logger.info("Found string: " + text[0]);
                            } else {
                                logger.info("Current string: " + text[0]);
                            }
                            return (found);
                        } else return false;
                    });

            saveScreenshot(LogStatus.PASS, "Verifying message appeared", "Found message: " + text[0]);
            foundText = true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Did NOT Find the message with regex: " + regex + " ,last text is: " + lasttext[0]);
            if (reportFail) {
                saveScreenshot(LogStatus.FAIL, "Verifying message appeared", "Did NOT Find the message with regex: " + regex + " ,last text is: " + text[0]);
            }
            foundText = false;
        }
        return foundText;
    }

    private boolean iosIsExists(By elementIdentifier, boolean isHiddenElement) {
        try {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            WebElement element = driver.findElement(elementIdentifier);
            if (isHiddenElement) {
                logger.info("IsExists returning true because its an ios hidden element");
                return true;
            } else {
                boolean displayed = element.isDisplayed();
                logger.info("IsExists returning is displayed: " + displayed);
                return displayed;
            }
        } catch (Exception e) {
            logger.info("IsExists didn't find the elementIdentifier " + elementIdentifier);
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        }
    }

    // [M.E] - Get element's parent
    public WebElement getElementParent(WebElement element) {
        return element.findElement(By.xpath(".."));
    }

    public boolean checkIfElementConditionMetWithoutTrowingAnError(By by, int timeoutMili, int poolingMili, String description, Conditions condition) {
        return checkIfElementConditionMetWithoutTrowingAnError(by, timeoutMili, poolingMili, description, condition, "");
    }

    public boolean checkIfElementConditionMetWithoutTrowingAnError(By by, int timeoutMili, int poolingMili, String description, Conditions condition, String text) {
        try {

            Wait<WebDriver> customWait = new FluentWait<WebDriver>(driver)
                    .withTimeout(timeoutMili, MILLISECONDS)
                    .pollingEvery(poolingMili, MILLISECONDS)
                    .ignoring(NoSuchElementException.class);
            if (condition == Conditions.IS_CLICKABLE)
                ((FluentWait<WebDriver>) customWait).until(ExpectedConditions.elementToBeClickable(by));
            else if (condition == Conditions.IS_VISIBLE)
                ((FluentWait<WebDriver>) customWait).until(ExpectedConditions.visibilityOfElementLocated(by));
            else if (condition == Conditions.TEXT_IS_PRESENT)
                ((FluentWait<WebDriver>) customWait).until(ExpectedConditions.textToBePresentInElementLocated(by, text));


            saveScreenshot(LogStatus.PASS, description == "" ? description : "Verifying element condition", "Element condition was met");
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void switchWindowByName(String name) {
        for (String handle :
                driver.getWindowHandles()) {
            String title = driver.switchTo().window(handle).getTitle();
            logger.info("handle" + handle + " ,title:" + title);
            if (title.toLowerCase().contains(name.toLowerCase()))
                break;
        }
    }

    private void clickbyjavascript(WebDriver chromDriver, WebElement alreadyAwazer) {
        JavascriptExecutor executor = (JavascriptExecutor) chromDriver;
        executor.executeScript("arguments[0].click();", alreadyAwazer);
    }
    public boolean clickElement(By by, String description) // clicking element
    {
        WebElement element = null;
        try {
            element = driver.findElement(by);
        } catch (Exception e) {
            saveScreenshot(LogStatus.FAIL, "Trying to click element " + description, "Couldn't find the element");
            htmlReporter.clickFailed = true;
            System.out.println("click Failed value: " + htmlReporter.clickFailed);
            return false;
        }
        return clickElement(element, description, true);
    }
    public boolean clickElement(WebElement element, String description, boolean reportException) // clicking element
    {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            System.out.println("Clicked on " + description + " element");
            saveScreenshot(LogStatus.PASS, "Clicked on " + description + " element", "Clicked succeeded");
            return true;
        } catch (Exception msg) {
            if (reportException) {
                msg.printStackTrace();
                saveScreenshot(LogStatus.FAIL, "Clicked on " + description + " element", "Click were not succeeded");
                htmlReporter.clickFailed = true;
                System.out.println("click Failed value: " + htmlReporter.clickFailed);
            }
            return false;
        }
    }

}







