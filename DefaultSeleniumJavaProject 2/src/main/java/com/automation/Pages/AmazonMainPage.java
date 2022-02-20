package com.automation.Pages;

import com.automation.helpers.DriverManager;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

public class AmazonMainPage extends DefaultPage {
    public AmazonMainPage(DriverManager manager) {
        super(manager);
    }
    private By searchBtn = By.id("nav-search-submit-button");
    private By searchText = By.id("twotabsearchtextbox");
    private By filter = By.xpath("//select[contains(@name,'url')]");


    public void search (String str,String filterValue)
    {
        try {

            manager.sendKeysToWebElementInput(searchText, str);
            Select select = new Select(manager.getDriver().findElement(filter));
            select.selectByVisibleText(filterValue);
            manager.clickElement(searchBtn, "search");
        }
        catch (Exception e)
        {
            manager.saveScreenshot(LogStatus.FAIL,"search","we failed to do the search");
        }

    }

}

