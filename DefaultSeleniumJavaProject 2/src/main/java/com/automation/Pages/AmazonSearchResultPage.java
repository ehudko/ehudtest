package com.automation.Pages;

import com.automation.helpers.DriverManager;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;

public class AmazonSearchResultPage extends DefaultPage {

    public AmazonSearchResultPage(DriverManager manager) {
        super(manager);

    }
    private By resultCount = By.xpath("//div[contains(@class,'a-section a-spacing-small a-spacing-top-small')]/span[contains(text(),'results')]");

    public String countResult( String lang)
    {
        try {
            if(!lang.isEmpty())
            {
                String xpath = "//li//span[contains(text(),'" + lang +"')]";
                manager.clickElement(By.xpath(xpath),lang);

            }
            String result = manager.getDriver().findElement(resultCount).getText();
            String formated = result.substring(result.indexOf("of") + 2, result.indexOf("result"));
            manager.saveScreenshot(LogStatus.PASS, "get result Number", "we have found" + formated + "results");
            return formated.trim();
        }
        catch (Exception e)
        {
            manager.saveScreenshot(LogStatus.FAIL, "get result Number", "we have not found the results" );
            return "";
        }

    }
    public String validateFullTerm(String term){
        By headlineLocator = By.xpath("//span[contains(@class,'a-size-medium a-color-base a-text-normal')]");
        By continueBtnLocator = By.xpath("//*[contains(@class,'pagination-next')]");
        String longestHeadline = "";
        try {
            List<WebElement> headlines = manager.getDriver().findElements(headlineLocator);
            WebElement continuBtn = manager.getDriver().findElement(continueBtnLocator);
            boolean stopLoop = continuBtn.getAttribute("class").contains("disabled");
            while (!stopLoop) {
                stopLoop = continuBtn.getAttribute("class").contains("disabled");
                for (WebElement headline :
                        headlines) {
                    String text = headline.getText();
                    if (text.toLowerCase().contains(term.toLowerCase()) &&
                            (text.length() > longestHeadline.length())) {
                        longestHeadline = text;
                    }
                }
                if(!stopLoop) {
                    manager.clickElement(continuBtn, "Continue");
                    manager.waitForElementBeDisplay(continueBtnLocator, 3);
                    continuBtn = manager.getDriver().findElement(continueBtnLocator);
                    headlines = manager.getDriver().findElements(headlineLocator);
                    System.out.println("stopLoop: " + stopLoop);
                }
            }
        }catch (Exception e){
            manager.saveScreenshot(LogStatus.FAIL,"Validating longest term","Couldn't validate");
        }
        boolean success = longestHeadline.length() <= 70;
        if (!success){
            manager.saveScreenshot(LogStatus.FAIL,"Validating longest term",longestHeadline + " is more then 70 chars");
        }
        Assert.assertTrue(success,"The longest term is " + longestHeadline + " and it's too long!!");
        manager.saveScreenshot(LogStatus.PASS,"Validating longest term",longestHeadline + " is under 70 chars");
        return longestHeadline;
    }



}
