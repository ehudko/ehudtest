package com.automation.Pages;


import com.automation.helpers.DriverManager;
import org.apache.log4j.Logger;
import com.automation.helpers.DriverManager;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import java.awt.*;

public class DefaultPage {

    public DriverManager manager;

    public Logger logger = Logger.getLogger(this.getClass());

    public DefaultPage(DriverManager manager) {
        this.manager = manager;
    }
    public void selectMenuOption(String main,String second){
        try {
            WebElement firstMenuItem = manager.getDriver().findElement(By.partialLinkText(main.toUpperCase()));
            if (!second.isEmpty()) {
                Actions act = new Actions(manager.getDriver());
                act.moveToElement(firstMenuItem).perform();//mouseover
                boolean found = manager.clickElement(By.partialLinkText(second), "Selecting secondary menu item: " + second);
                if (found)
                    manager.saveScreenshot(LogStatus.PASS, "Selecting secondary menu item: " + second, "The secondary menu item were selected");
                else
                    manager.saveScreenshot(LogStatus.FAIL, "Selecting secondary menu item: " + second, "The secondary menu item were NOT selected");
            } else {
                manager.clickElement(firstMenuItem, "the menu item: " + main);
                manager.saveScreenshot(LogStatus.PASS, "Selecting main menu item: " + main, "The main menu item were selected");
            }
        }catch (Exception e){
            manager.saveScreenshot(LogStatus.FAIL, "Selecting menu item: " + main + "->" + second, "Couldn't select menu item, exception: "  + e.getMessage());
            Assert.assertTrue(false,"Couldn't select menu item, exception: "  + e.getMessage());
        }
    }

}




