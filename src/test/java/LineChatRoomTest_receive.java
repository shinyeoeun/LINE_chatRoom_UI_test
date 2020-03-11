import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.android_utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class LineChatRoomTest_receive {

    static ExtentTest logger;
    static ExtentReports report;

    public AndroidDriver driver = null;
    public DesiredCapabilities capabilities = new DesiredCapabilities();
    public String PACKAGE = "jp.naver.line.android";
    public String START_DEVICE_USER_NAME = "LION";
    public String JOIN_DEVICE_USER_NAME = "TIGER";
    public String SEND_TEXT = "I WILL CALL YOU";

    @BeforeClass
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

            report = new ExtentReports(System.getProperty("user.dir") + "\\LINE_TestAutomation_Report_Receive_"+android_utils.getNowDateTime()+".html");
            report.loadConfig(new File("report_config.xml"));

        }
    }

    // 수신측 토크 리스트에 쳇룸생성 확인
    @Test
    public void TC_01_chatRoomCreated_B() throws Exception {
        logger= report.startTest("Case: Device B 쳇룸 생성 확인");
        logger.log(LogStatus.INFO, "Step: 토크탭 진입 > 토크 리스트에 송신측과의 대화방이 생성됨을 확인");
        logger.log(LogStatus.INFO, "Expected Result: 토크 리스트에 송신자와의 쳇룸이 생성됨");

        driver.findElementByXPath("(//android.view.ViewGroup[@content-desc=\"@{bottomNavigationBarButtonViewModel.contentDescription\"])[2]/android.widget.TextView").click();

        logger.log(LogStatus.INFO, "Test Result: 쳇룸리스트 타이틀 > " + START_DEVICE_USER_NAME);
        Thread.sleep(1000);
        logger.log(LogStatus.INFO, logger.addScreenCapture(android_utils.getScreenshot(driver)));

        Assert.assertTrue(driver.findElementByXPath("//android.widget.TextView[@text='" + START_DEVICE_USER_NAME + "']").isDisplayed());
    }

    // 수신측 토크 리스트에 쳇룸진입
    @Test
    public void TC_02_chatRoomAccess_B() throws Exception {
        logger= report.startTest("Case：Device B 쳇룸 진입");
        logger.log(LogStatus.INFO, "Step: 토크 리스트에서 송신측과의 쳇룸을 탭하여 쳇룸 진입");
        logger.log(LogStatus.INFO, "Expected Result: 쳇룸에 진입되고 쳇룸 헤더 타이틀에 송신자 ID가 표시됨");

        boolean isDisplayed = false;
        while(isDisplayed){
            isDisplayed = android_utils.isElementDisplayed(driver, By.xpath("//android.widget.TextView[@text='" + START_DEVICE_USER_NAME + "']"));
        }

        driver.findElementByXPath("//android.widget.TextView[@text='" + START_DEVICE_USER_NAME + "']").click();
        logger.log(LogStatus.INFO, "Test Result: 쳇룸 헤더 타이틀 > " + START_DEVICE_USER_NAME);
        Thread.sleep(1000);
        logger.log(LogStatus.INFO, logger.addScreenCapture(android_utils.getScreenshot(driver)));

        // Assert.assertEquals(driver.findElementsById("jp.naver.line.android:id/header_title"), START_DEVICE_USER_NAME);

    }

    // 수신측 수신내용(메시지/시간) 확인
    @Test
    public void TC_03_valifyReceivedText_B() throws Exception {
        logger= report.startTest("Case：Device B 수신내용 확인");
        logger.log(LogStatus.INFO, "Step: 쳇룸에 표시된 메시지/수신시간을 확인");
        logger.log(LogStatus.INFO, "Expected Result: 쳇룸에 수신받은 메시지와 수신시간이 표시됨");
        Thread.sleep(1000);

        logger.log(LogStatus.INFO, "Test Result: 수신메시지 > " + SEND_TEXT + " | 수신시간 > " + android_utils.getChatRoomDisplayTime());
        logger.log(LogStatus.INFO, logger.addScreenCapture(android_utils.getScreenshot(driver)));

        Assert.assertTrue(driver.findElementByXPath("//android.widget.TextView[@text='" + SEND_TEXT + "']").isDisplayed());
    }

    @Test
    public void TC_04_call_recieve_B() throws Exception {
        logger= report.startTest("Case：Device B 보이스콜 수신");
        logger.log(LogStatus.INFO, "Step: 수신전화「수락」버튼 탭");
        logger.log(LogStatus.INFO, "Expected Result: 통화가 시작되고 화면에 통화시간이 표시됨");

        boolean isDisplayed = false;
        while(isDisplayed){
            isDisplayed = android_utils.isElementDisplayed(driver, By.id("jp.naver.line.android:id/accept_button"));
        }

        driver.findElementById("jp.naver.line.android:id/accept_button").click();
        driver.findElementById("jp.naver.line.android:id/call_mute_btn_icon").click();

        logger.log(LogStatus.INFO, "Test Result: 통화시간 > " + driver.findElementById("jp.naver.line.android:id/voicecall_status_message").getAttribute("text"));
        logger.log(LogStatus.INFO, logger.addScreenCapture(android_utils.getScreenshot(driver)));

        Assert.assertTrue(driver.findElementById("jp.naver.line.android:id/voicecall_status_message").isDisplayed());
        Thread.sleep(5000); // 5초간 통화
    }

    @AfterMethod
    public void getResult(ITestResult result) throws Exception {
        if (result.getStatus() == ITestResult.FAILURE){
            logger.log(LogStatus.FAIL, result.getName());
            logger.log(LogStatus.FAIL, result.getThrowable());
            logger.log(LogStatus.FAIL, "Test Case Fail" + logger.addScreenCapture(android_utils.getScreenshot(driver)));

        } else if (result.getStatus() == result.SUCCESS){
            logger.log(LogStatus.PASS, result.getName());
        }
        report.endTest(logger);
        report.flush();
        }

    @AfterClass
    public void endTest() {
        report.flush();
        driver.quit();
    }

}