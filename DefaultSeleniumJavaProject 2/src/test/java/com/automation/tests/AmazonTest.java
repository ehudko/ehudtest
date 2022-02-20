package com.automation.tests;

import com.automation.Pages.AmazonMainPage;
import com.automation.Pages.AmazonSearchResultPage;
import com.automation.helpers.DefaultTestClass;
import org.testng.annotations.Test;

public class AmazonTest extends DefaultTestClass {
    @Test
    public void Test1() throws InterruptedException {

        driverManager.navigateTo("https://www.amazon.com/");
        AmazonMainPage amazonMainPage = new AmazonMainPage(driverManager);
        amazonMainPage.search("the Lost World by Arthur Conan Doyle","Books");
        AmazonSearchResultPage amazonSearchResultPage = new AmazonSearchResultPage(driverManager);

       String str= amazonSearchResultPage.countResult("");
        String str1= amazonSearchResultPage.countResult("English");
       System.out.println(str);
        System.out.println(str1);

       String str2= amazonSearchResultPage.validateFullTerm("the Lost World by Arthur Conan Doyle");
       System.out.println(str2);



    }
}
