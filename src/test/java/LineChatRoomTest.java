import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.relevantcodes.extentreports.LogStatus;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.android_utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;


public class LineChatRoomTest {

    public ExtentTest logger; //Report Logger
    public ExtentReports reports; // Default
    public ExtentHtmlReporter htmlReporter; //html Report

    public AndroidDriver driver = null;
    public DesiredCapabilities capabilities = new DesiredCapabilities();
    public String PACKAGE = "jp.naver.line.android";
    public String START_DEVICE_USER_NAME = "申";
    public String JOIN_DEVICE_USER_NAME = "SHIN";
    public String SEND_TEXT = "I WILL CALL YOU";

    @BeforeClass(alwaysRun = true)
    @Parameters({"platform", "port", "udid", "device", "ver", "autoname", "systemp"})
    public void setUp(/* String ip, */ String platform, String port, String udid, String device, String ver, String autoname, String systemp) throws MalformedURLException, InterruptedException {

        if (platform == "window") {
            capabilities.setCapability("platformName", platform);
            capabilities.setCapability("deviceName", device);
            capabilities.setCapability("systemPort", systemp);
        } else {
            capabilities.setCapability("newCommandTimeout", 10000); // appium timeout
            capabilities.setCapability("platformName", platform);
            capabilities.setCapability("platformVersion", ver);
            capabilities.setCapability("noReset", true);
            capabilities.setCapability("appPackage", PACKAGE);
            capabilities.setCapability("deviceName", device);// Galaxy Note5
            capabilities.setCapability("udid", udid);
            capabilities.setCapability("appActivity", ".activity.SplashActivity");
            capabilities.setCapability("unicodeKeyboard", false);
            capabilities.setCapability("autoGrantPermissions", true);
            capabilities.setCapability("automationName", autoname);
            capabilities.setCapability("systemPort", systemp);

            driver = new AndroidDriver(new URL("http://127.0.0.1:" + port + "/wd/hub"), capabilities);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // Test Report
            htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/LINE_chat_room_test.html");
            htmlReporter.config().setDocumentTitle("LINE Delivery Regression Test");
            htmlReporter.config().setReportName("LINE Delivery Regression Test");
            htmlReporter.config().setTheme(Theme.DARK);
            htmlReporter.config().setReportName("Group Call");
            htmlReporter.config().setEncoding("UTF-8");
            htmlReporter.config().setTimeStampFormat("yyyy/MM/dd/ hh:mm:ss a");

            reports = new ExtentReports();
            reports.attachReporter(htmlReporter);
            reports.setSystemInfo("Report by", "SHIN");
            reports.setSystemInfo("Device", "송신: Galaxy S10(9.0) / 수신: Galaxy Note7(6.0.1)");
            reports.setSystemInfo("Package", driver.getCurrentPackage());
            reports.setSystemInfo("Build ver.", "10.0.0");
            reports.setSystemInfo("Test Env", "Beta");
        }
    }

    // 송신측 신규 대화 개시 > 쳇룸 생성
    @Test
    public void TC_01_chatStart_A() throws Exception {
        logger = reports.createTest("Case1: LINE 신규 대화 개시");
        logger.log(Status.INFO, "Current Activity: " + driver.currentActivity());

        logger.log(Status.INFO, "LINE 앱 실행");

        // 하단 메뉴에서 홈 탭 선택
        //driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\"@{bottomNavigationBarButtonViewModel.contentDescription\"])[1]/android.widget.TextView")).click();
        logger.log(Status.INFO, "하단 메뉴에서 홈 탭 선택");
        logger.log(Status.INFO, "Screenshot: "+ logger.addScreenCaptureFromPath(android_utils.getScreenshot(driver)));

        // 친구 리스트에서 대화상대 ID 탭
        driver.findElement(By.xpath("//android.widget.TextView[@text='" + JOIN_DEVICE_USER_NAME + "']")).click();
        logger.log(Status.INFO, "친구 리스트에서 대화상대 ID(" + JOIN_DEVICE_USER_NAME + ") 탭");
        logger.log(Status.INFO, "Screenshot: "+ logger.addScreenCaptureFromPath(android_utils.getScreenshot(driver)));

        // 대화상대 프로필 화면에서 토크 버튼 탭
        android_utils.findString(driver, "\"トーク\"").click();
        logger.log(Status.INFO, "대화상대 프로필 화면에서 토크 버튼 탭");
        logger.log(Status.INFO, "Screenshot: "+ logger.addScreenCaptureFromPath(android_utils.getScreenshot(driver)));

        // 쳇룸 헤더타이틀에 상대방 ID가 표시되는지 확인
        Assert.assertEquals(android_utils.getHeaderTitle(driver), JOIN_DEVICE_USER_NAME);
        logger.log(Status.INFO, "쳇룸 헤더타이틀:" + android_utils.getHeaderTitle(driver));
        logger.log(Status.INFO, "Screenshot: "+ logger.addScreenCaptureFromPath(android_utils.getScreenshot(driver)));

        logger.log(Status.PASS, "신규 대화 개시 > 쳇룸 생성 성공");

    }
/*

    // 송신측 메시지 전송 및 내용확인
    @Test
    public void TC_03_sendTextMessage_A() throws Exception {
        logger = reports.createTest("LINE 쳇룸: 텍스트 메시지 송신");
        logger.log(Status.INFO, "Now Activity: " + driver.currentActivity());
        logger.log(Status.INFO, "Now Activity: " + driver.currentActivity());


        // 텍스트 메시지 송신
        driver.findElementById("jp.naver.line.android:id/chathistory_message_edit").clear();
        driver.findElementById("jp.naver.line.android:id/chathistory_message_edit").sendKeys(SEND_TEXT);
        driver.findElementByAccessibilityId("送信").click();
        Thread.sleep(3000);
        String timestamp = android_utils.getTime();

        // 송신메시지 표시 확인
        Assert.assertTrue(driver.findElementByAndroidUIAutomator("new UiSelector().text('" + SEND_TEXT + "')").isDisplayed());
        logger.log(Status.INFO, "송신 텍스트: "+SEND_TEXT);

        // 송신시각 표시 확인
        Assert.assertTrue(driver.findElementByAndroidUIAutomator("new UiSelector().text('" + timestamp + "')").isDisplayed());


        logger.log(Status.INFO, "송신 시각: "+timestamp);
    }

    // 수신측 쳇룸 진입
    @Test
    public void TC_04_chatStart_B() throws Exception {
        logger = reports.createTest("LINE 쳇룸: 쳇룸 생성 테스트");
        logger.log(Status.INFO, "Now 패키지: " + driver.getCurrentPackage() + "// Now 엑티비티: " + driver.currentActivity());

        // 토크 버튼 탭
        driver.findElementByXPath("(//android.view.ViewGroup[@content-desc=\"@{bottomNavigationBarButtonViewModel.contentDescription\"])[2]/android.widget.TextView").click();
        // 헤더타이틀에 トーク출력되는지 확인
        //logger.log(Status.INFO, "헤더타이틀:"+driver.findElementsById("jp.naver.line.android:id/header_title"));
        Thread.sleep(3000);
        // 친구목록에서 대화상대 탭
        driver.findElementByXPath("//android.widget.TextView[@text='" + START_DEVICE_USER_NAME + "']").click();
        // 헤더타이틀에 본인 ID가 표시되는지 확인
        //Assert.assertEquals(driver.findElementsById("jp.naver.line.android:id/header_title"), START_DEVICE_USER_NAME);
        //logger.log(Status.INFO, "헤더타이틀:"+driver.findElementsById("jp.naver.line.android:id/header_title"));
    }

    // 수신측 메시지 수신 및 수신확인
    @Test
    public void TC_05_receiveTextMessage_B() throws Exception {
        logger = reports.createTest("Chat Room: 텍스트 수신 확인");
        logger.log(Status.INFO, "Now 패키지: " + driver.getCurrentPackage() + "// Now 엑티비티: " + driver.currentActivity());
        Thread.sleep(10000);
        String timestamp = android_utils.getTime();
        //Assert.assertTrue(driver.findElementByAndroidUIAutomator("new UiSelector().text('" + SEND_TEXT + "')").isDisplayed());
        //Assert.assertTrue(driver.findElementByAndroidUIAutomator("new UiSelector().text('" + timestamp + "')").isDisplayed());
        logger.log(Status.INFO, "송신텍스트:"+SEND_TEXT + "//송신시간:"+timestamp);
    }

*/

    @AfterMethod
    public void getResult(ITestResult result) throws Exception {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.log(Status.FAIL, result.getName() + "Fail");
            logger.log(Status.FAIL, MarkupHelper.createLabel("Fail", ExtentColor.RED));
            logger.log(Status.FAIL, result.getThrowable());
            logger.log(Status.FAIL, "Test Case Fail" + logger.addScreenCaptureFromPath(android_utils.getScreenshot(driver)));
        } else if (result.getStatus() == result.SUCCESS) {
            logger.log(Status.PASS, result.getName() + "Pass");
            logger.log(Status.PASS, MarkupHelper.createLabel("Pass", ExtentColor.GREEN));
            logger.log(Status.PASS, "Test Case Success! " + logger.addScreenCaptureFromPath(android_utils.getScreenshot(driver)));
        }
        reports.flush();
    }


    @AfterTest
    public void endReport() {
        driver.quit();
   }
}



