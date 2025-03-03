/*
 *  Copyright 2021 Collate
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.openmetadata.catalog.selenium.pages.common;

import com.github.javafaker.Faker;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openmetadata.catalog.selenium.events.Events;
import org.openmetadata.catalog.selenium.properties.Property;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

@Order(17)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommonTests {

  private static final Logger LOG = Logger.getLogger(CommonTests.class.getName());

  static WebDriver webDriver;
  static Actions actions;
  static Faker faker = new Faker();
  static String tagCategoryDisplayName = faker.name().firstName();
  static String enterDescription = "//div[@data-testid='enterDescription']/div/div[2]/div/div/div/div/div/div";
  static WebDriverWait wait;
  Integer waitTime = Property.getInstance().getSleepTime();
  static String url = Property.getInstance().getURL();
  static String urlTag = "/api/v1/tags/";
  String tableName = "dim_address";

  @BeforeEach
  public void openMetadataWindow() {
    System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/linux/chromedriver");
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--window-size=1280,800");
    webDriver = new ChromeDriver(options);
    actions = new Actions(webDriver);
    wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
    webDriver.manage().window().maximize();
    webDriver.get(url);
  }

  public void openHomePage() throws InterruptedException {
    Events.click(webDriver, By.cssSelector("[data-testid='closeWhatsNew']")); // Close What's new
    Thread.sleep(waitTime);
  }

  @Test
  @Order(1)
  public void tagDuplicationCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='tables']")); // Tables
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='searchBox']"), "dim_location");
    Events.click(webDriver, By.cssSelector("[data-testid='data-name']"));
    Thread.sleep(waitTime);
    actions.moveToElement(webDriver.findElement(By.xpath("//div[@data-testid='tag-conatiner']//span"))).perform();
    Events.click(webDriver, By.xpath("//div[@data-testid='tag-conatiner']//span"));
    Events.click(webDriver, By.cssSelector("[data-testid='associatedTagName']"));
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='associatedTagName']"), "PersonalData.Personal");
    Events.click(webDriver, By.cssSelector("[data-testid='list-item']"));
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='associatedTagName']"), "User.FacePhoto");
    Events.click(webDriver, By.cssSelector("[data-testid='list-item']"));
    Events.click(webDriver, By.cssSelector("[data-testid='saveAssociatedTag']"));
    Thread.sleep(2000);
    Object tagCount =
        webDriver.findElements(By.xpath("//*[text()[contains(.,'" + "#PersonalData.Personal" + "')]] ")).size();
    Assert.assertEquals(tagCount, 2);
  }

  @Test
  @Order(2)
  public void addTagWithSpaceCheck() throws InterruptedException, IOException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='menu-button'][id='menu-button-Settings']")); // Setting
    Events.click(webDriver, By.cssSelector("[data-testid='menu-item-Tags']")); // Setting/Tags
    Events.click(webDriver, By.cssSelector("[data-testid='add-category']"));
    wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement(By.name("name"))));
    Events.sendKeys(webDriver, By.name("name"), tagCategoryDisplayName);
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='saveButton']"));
    webDriver.navigate().refresh();
    Events.click(webDriver, By.xpath("//*[text()[contains(.,'" + tagCategoryDisplayName + "')]] "));
    Events.click(webDriver, By.cssSelector("[data-testid='add-new-tag-button']"));
    wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
    Events.sendKeys(webDriver, By.name("name"), "Testing Tag");
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='saveButton']"));
    URL tagUrl = new URL(url + urlTag + tagCategoryDisplayName + "/");
    HttpURLConnection http = (HttpURLConnection) tagUrl.openConnection();
    http.setRequestMethod("HEAD");
    http.connect();
    Assert.assertEquals(http.getResponseCode(), 200);
  }

  @Test
  @Order(3)
  public void addTagCategoryWithSpaceCheck() throws InterruptedException, IOException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='menu-button'][id='menu-button-Settings']")); // Setting
    Events.click(webDriver, By.cssSelector("[data-testid='menu-item-Tags']")); // Setting/Tags
    Events.click(webDriver, By.cssSelector("[data-testid='add-category']"));
    wait.until(ExpectedConditions.elementToBeClickable(webDriver.findElement(By.name("name"))));
    Events.sendKeys(webDriver, By.name("name"), "Space Tag");
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='saveButton']"));
    webDriver.navigate().refresh();
    URL tagUrl = new URL(url + urlTag);
    HttpURLConnection http = (HttpURLConnection) tagUrl.openConnection();
    http.setRequestMethod("HEAD");
    http.connect();
    Assert.assertEquals(http.getResponseCode(), 200);
  }

  @Test
  @Order(4)
  public void onlySpaceAsNameForServiceCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='service']")); // Service
    Thread.sleep(2000);
    List<WebElement> webElementList = webDriver.findElements(By.cssSelector("[data-testid='add-new-user-button']"));
    if (webElementList.isEmpty()) {
      Events.click(webDriver, By.cssSelector("[data-testid='add-service-button']"));
    } else {
      Events.click(webDriver, By.cssSelector("[data-testid='add-new-user-button']"));
    }
    Events.click(webDriver, By.cssSelector("[data-testid='selectService']"));
    Events.click(webDriver, By.cssSelector("[value='MySQL']"));
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='name']"), " ");
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='url']"), "localhost:3306");
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='database']"), "openmetadata_db");
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='save-button']"));
    webDriver.navigate().refresh();
    try {
      Events.click(webDriver, By.cssSelector("[data-testid='delete-service-" + " " + "']"));
    } catch (TimeoutException exception) {
      LOG.info("Success");
    }
  }

  @Test
  @Order(5)
  public void addMultipleTagsCheck() throws InterruptedException {
    openHomePage();
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='searchBox']"), "raw_product_catalog");
    Events.click(webDriver, By.cssSelector("[data-testid='data-name'][id='bigquery_gcpshopifyraw_product_catalog']"));
    Events.click(webDriver, By.xpath("//div[@data-testid='tag-conatiner']//span"));
    Events.click(webDriver, By.cssSelector("[data-testid='associatedTagName']"));
    for (int i = 0; i <= 10; i++) {
      Events.sendKeys(webDriver, By.cssSelector("[data-testid='associatedTagName']"), "P");
      Events.click(webDriver, By.cssSelector("[data-testid='list-item']"));
      Thread.sleep(waitTime);
    }
    Events.click(webDriver, By.cssSelector("[data-testid='saveAssociatedTag']"));
    webDriver.navigate().refresh();
    Thread.sleep(2000);
    Object tagCount = webDriver.findElements(By.xpath("//div[@data-testid='tag-conatiner']/div/div")).size();
    Assert.assertEquals(tagCount, 11);
  }

  @Test
  @Order(6)
  public void sameNameTagCategoryUIMessageCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='menu-button'][id='menu-button-Settings']")); // Setting
    Events.click(webDriver, By.cssSelector("[data-testid='menu-item-Tags']")); // Setting/Tags
    Events.click(webDriver, By.cssSelector("[data-testid='add-category']"));
    wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
    Events.sendKeys(webDriver, By.name("name"), "personalData");
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='saveButton']"));
    String errorMessage =
        webDriver.findElement(By.cssSelector("[data-testid='error-message']")).getAttribute("innerHTML");
    Thread.sleep(2000);
    Assert.assertEquals(errorMessage, "Name already exists");
  }

  @Test
  @Order(7)
  public void sameNameTagUIMessageCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='menu-button'][id='menu-button-Settings']")); // Setting
    Events.click(webDriver, By.cssSelector("[data-testid='menu-item-Tags']")); // Setting/Tags
    Events.click(webDriver, By.xpath("//*[text()[contains(.,'" + "PersonalData" + "')]] "));
    Events.click(webDriver, By.cssSelector("[data-testid='add-new-tag-button']"));
    wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
    Events.sendKeys(webDriver, By.name("name"), "personal");
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='saveButton']"));
    String errorMessage =
        webDriver.findElement(By.cssSelector("[data-testid='error-message']")).getAttribute("innerHTML");
    Thread.sleep(2000);
    Assert.assertEquals(errorMessage, "Name already exists");
  }

  @Test
  @Order(8)
  public void shortTagCategoryNameUIMessageCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='menu-button'][id='menu-button-Settings']")); // Setting
    Events.click(webDriver, By.cssSelector("[data-testid='menu-item-Tags']")); // Setting/Tags
    Events.click(webDriver, By.cssSelector("[data-testid='add-category']"));
    wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
    Events.sendKeys(webDriver, By.name("name"), "P");
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='saveButton']"));
    String errorMessage =
        webDriver.findElement(By.cssSelector("[data-testid='error-message']")).getAttribute("innerHTML");
    Thread.sleep(2000);
    Assert.assertEquals(errorMessage, "Name size must be between 2 and 25");
  }

  @Test
  @Order(9)
  public void longTagCategoryNameUIMessageCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='menu-button'][id='menu-button-Settings']")); // Setting
    Events.click(webDriver, By.cssSelector("[data-testid='menu-item-Tags']")); // Setting/Tags
    Events.click(webDriver, By.cssSelector("[data-testid='add-category']"));
    wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
    Events.sendKeys(webDriver, By.name("name"), "PersonalData-DataPlatform-PersonalData");
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='saveButton']"));
    String errorMessage =
        webDriver.findElement(By.cssSelector("[data-testid='error-message']")).getAttribute("innerHTML");
    Thread.sleep(2000);
    Assert.assertEquals(errorMessage, "Name size must be between 2 and 25");
  }

  @Test
  @Order(10)
  public void shortTagNameUIMessageCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='menu-button'][id='menu-button-Settings']")); // Setting
    Events.click(webDriver, By.cssSelector("[data-testid='menu-item-Tags']")); // Setting/Tags
    Events.click(webDriver, By.xpath("//*[text()[contains(.,'" + "PersonalData" + "')]] "));
    Events.click(webDriver, By.cssSelector("[data-testid='add-new-tag-button']"));
    wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
    Events.sendKeys(webDriver, By.name("name"), "P");
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='saveButton']"));
    String errorMessage =
        webDriver.findElement(By.cssSelector("[data-testid='error-message']")).getAttribute("innerHTML");
    Thread.sleep(2000);
    Assert.assertEquals(errorMessage, "Name size must be between 2 and 25");
  }

  @Test
  @Order(11)
  public void longTagNameUIMessageCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='menu-button'][id='menu-button-Settings']")); // Setting
    Events.click(webDriver, By.cssSelector("[data-testid='menu-item-Tags']")); // Setting/Tags
    Events.click(webDriver, By.xpath("//*[text()[contains(.,'" + "PersonalData" + "')]] "));
    Events.click(webDriver, By.cssSelector("[data-testid='add-new-tag-button']"));
    wait.until(ExpectedConditions.elementToBeClickable(By.name("name")));
    Events.sendKeys(webDriver, By.name("name"), "P");
    Events.sendKeys(webDriver, By.xpath(enterDescription), faker.address().toString());
    Events.click(webDriver, By.cssSelector("[data-testid='saveButton']"));
    String errorMessage =
        webDriver.findElement(By.cssSelector("[data-testid='error-message']")).getAttribute("innerHTML");
    Thread.sleep(2000);
    Assert.assertEquals(errorMessage, "Name size must be between 2 and 25");
  }

  @Test
  @Order(12)
  public void searchMatchesCountCheck() throws InterruptedException {
    openHomePage();
    Events.sendKeys(webDriver, By.cssSelector("[id='searchBox']"), "address"); // Search bar/dim
    Events.sendEnter(webDriver, By.cssSelector("[id='searchBox']"));
    Thread.sleep(2000);
    Object tagCount =
        webDriver.findElements(By.xpath("(//div[@data-testid='description-text'][1])/div/p/span")).size() - 1;
    Thread.sleep(2000);
    String matchesInDescription =
        webDriver.findElement(By.xpath("(//div[@data-testid='matches-stats'][1])/span[3]")).getAttribute("innerHTML");
    Assert.assertEquals((tagCount + " in Description,"), matchesInDescription);
  }

  @Test
  @Order(13)
  public void overviewLinksAfterTour() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='tour']"));
    webDriver.navigate().back();
    Events.click(webDriver, By.cssSelector("[data-testid='tables']")); // Tables
    String tablesUrl = webDriver.getCurrentUrl();
    Assert.assertEquals(tablesUrl, url + "/explore/tables/");
  }

  @Test
  @Order(14)
  public void tourStepSkippingCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='tour']"));
    for (int i = 0; i < 2; i++) {
      Events.click(webDriver, By.cssSelector("[data-tour-elem='right-arrow']"));
    }
    Events.sendKeys(webDriver, By.cssSelector("[id='searchBox']"), "dim_a"); // Search bar/dim
    Events.sendEnter(webDriver, By.cssSelector("[id='searchBox']"));
  }

  @Test
  @Order(15)
  public void tagFilterCountCheck() throws InterruptedException {
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='searchBox']"), tableName);
    Events.click(webDriver, By.cssSelector("[data-testid='data-name']"));
    Thread.sleep(waitTime);
    actions.moveToElement(webDriver.findElement(By.xpath("//div[@data-testid='tag-conatiner']//span"))).perform();
    Events.click(webDriver, By.xpath("//div[@data-testid='tag-conatiner']//span"));
    Events.click(webDriver, By.cssSelector("[data-testid='associatedTagName']"));
    for (int i = 0; i <= 8; i++) {
      Events.sendKeys(webDriver, By.cssSelector("[data-testid='associatedTagName']"), "P");
      Events.click(webDriver, By.cssSelector("[data-testid='list-item']"));
    }
    for (int i = 0; i <= 5; i++) {
      Events.sendKeys(webDriver, By.cssSelector("[data-testid='associatedTagName']"), "U");
      Events.click(webDriver, By.cssSelector("[data-testid='list-item']"));
    }
    Events.click(webDriver, By.cssSelector("[data-testid='saveAssociatedTag']"));
    Thread.sleep(2000);
    Object tagsCount = webDriver.findElements(By.xpath("//div[@data-testid='tag-conatiner']/div/div")).size() - 1;
    Thread.sleep(2000);
    webDriver.navigate().back();
    Thread.sleep(2000);
    Object tagsFilterCount = webDriver.findElements(By.xpath("//div[@data-testid='filter-containers-2']/div")).size();
    Assert.assertEquals(tagsFilterCount.toString(), tagsCount.toString());
  }

  @Test
  @Order(16)
  public void differentSearchDifferentResultCheck() throws InterruptedException {
    openHomePage();
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='searchBox']"), "!");
    Events.sendEnter(webDriver, By.cssSelector("[id='searchBox']"));
    Thread.sleep(2000);
    String search1 =
        webDriver.findElement(By.cssSelector("[data-testid='no-search-results']")).getAttribute("innerHTML");
    Assert.assertEquals(search1, "No matching data assets found for !");
    webDriver.navigate().back();
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='searchBox']"), "{");
    Events.sendEnter(webDriver, By.cssSelector("[id='searchBox']"));
    Thread.sleep(2000);
    try {
      String search2 =
          webDriver.findElement(By.cssSelector("[data-testid='no-search-results']")).getAttribute("innerHTML");
      Assert.assertEquals(search2, "No matching data assets found for {");
    } catch (NoSuchElementException exception) {
      LOG.info("Search results are not similar for no data found!");
    }
  }

  @Test
  @Order(17)
  public void missingMatchesForSearchCheck() throws InterruptedException {
    openHomePage();
    Events.click(webDriver, By.cssSelector("[data-testid='dashboards']")); // Dashboards
    Events.sendKeys(webDriver, By.cssSelector("[data-testid='searchBox']"), "sales");
    Events.sendEnter(webDriver, By.cssSelector("[id='searchBox']"));
    String resultsCount =
        webDriver.findElement(By.xpath("//div[@data-testid='search-container']/div")).getAttribute("innerHTML");
    Object matchesCount = webDriver.findElements(By.cssSelector("[data-testid='matches-stats']")).size();
    Assert.assertEquals(matchesCount + " results", resultsCount);
  }

  @AfterEach
  public void closeTabs() {
    ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
    String originalHandle = webDriver.getWindowHandle();
    for (String handle : webDriver.getWindowHandles()) {
      if (!handle.equals(originalHandle)) {
        webDriver.switchTo().window(handle);
        webDriver.close();
      }
    }
    webDriver.switchTo().window(tabs.get(0)).close();
  }
}
