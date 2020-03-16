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

public class LineChatRoomTest_send {

    static ExtentTest test;
    static ExtentReports report;

    public AndroidDriver driver = null;
    public DesiredCapabilities capabilities = new DesiredCapabilities();
    public String PACKAGE = "jp.naver.line.android";
    public String JOIN_DEVICE_USER_NAME = "TIGER";
    public String SEND_TEXT = "I WILL CALL YOU";

    @BeforeClass
    @Parameters({"platform", "port", "udid", "device", "ver", "autoname", "systemp"})
    public void setUp(String platformName, String port, String udid, String deviceName, String platformVersion, String automationName, String systemPort) throws MalformedURLException {

        // Capability 설정
        if (platformName == "window") {
            capabilities.setCapability("platformName", platformName);
            capabilities.setCapability("deviceName", deviceName);
            capabilities.setCapability("systemPort", systemPort);
        } else {
            capabilities.setCapability("newCommandTimeout", 10000); // appium timeout
            capabilities.setCapability("platformName", platformName);
            capabilities.setCapability("platformVersion", platformVersion);
            capabilities.setCapability("noReset", true);
            capabilities.setCapability("appPackage", PACKAGE);
            capabilities.setCapability("deviceName", deviceName);
            capabilities.setCapability("udid", udid);
            capabilities.setCapability("appActivity", ".activity.SplashActivity");
            capabilities.setCapability("unicodeKeyboard", false);
            capabilities.setCapability("autoGrantPermissions", true);
            capabilities.setCapability("automationName", automationName);
            capabilities.setCapability("systemPort", systemPort);

            driver = new AndroidDriver(new URL("http://127.0.0.1:" + port + "/wd/hub"), capabilities);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // 테스트 결과 리포트 설정
            report = new ExtentReports(System.getProperty("user.dir") + "\\LINE_TestAutomation_Report_Send_"+android_utils.getNowDateTime()+".html");
            report.loadConfig(new File("report_config.xml"));
        }
    }

    // 송신측 신규 대화 개시
    @Test
    public void TC_01_chatStart_A() throws Exception {
        test = report.startTest("Case: Device A 신규 대화 개시");
        test.log(LogStatus.INFO, "Step: LINE 앱 실행");
        test.log(LogStatus.INFO, "Step: 친구 리스트에서 대화상대 ID 탭");
        test.log(LogStatus.INFO, "Step: 대화상대 프로필 화면에서 토크 버튼 탭");
        test.log(LogStatus.INFO, "Expected Result: 대화상대와의 쳇룸이 생성되고 쳇룸타이틀이 대화상대 ID로 표시될 것");

        driver.findElement(By.xpath("//android.widget.TextView[@text='" + JOIN_DEVICE_USER_NAME + "']")).click();
        android_utils.findString(driver, "\"トーク\"").click();
        Thread.sleep(1000);
        test.log(LogStatus.INFO, test.addScreenCapture(android_utils.getScreenshot(driver)));

        test.log(LogStatus.INFO, "Test Result: 쳇룸 타이틀> " + android_utils.getHeaderTitle(driver) + "| 대화상대 ID> " + JOIN_DEVICE_USER_NAME);
        Assert.assertEquals(android_utils.getHeaderTitle(driver), JOIN_DEVICE_USER_NAME);
    }

    // 송신측 메시지 전송
    @Test
    public void TC_02_sendTextMessage_A() throws Exception {
        test = report.startTest("Case：Device A 텍스트 메시지 송신");
        test.log(LogStatus.INFO, "Step: 텍스트 입력 후 송신버튼 탭");
        test.log(LogStatus.INFO, "Expected Result: 쳇룸에 송신한 텍스트와 송신시간이 표시됨");

        driver.findElementById("jp.naver.line.android:id/chathistory_message_edit").clear();
        driver.findElementById("jp.naver.line.android:id/chathistory_message_edit").sendKeys(SEND_TEXT);
        driver.findElementById("jp.naver.line.android:id/chathistory_send_button_image").click();
        Thread.sleep(1000);
        test.log(LogStatus.INFO, test.addScreenCapture(android_utils.getScreenshot(driver)));

        test.log(LogStatus.INFO, "Test Result: 텍스트 > " + SEND_TEXT + " | 송신시간 > " + android_utils.getChatRoomDisplayTime());
        Assert.assertTrue(driver.findElementByAndroidUIAutomator("new UiSelector().text(\"I WILL CALL YOU\")").isDisplayed());

    }

    // 송신측 메시지 읽음 표시 확인
    @Test
    public void TC_03_msg_readed() throws Exception {
        test = report.startTest("Case：Device A 메시지읽음 표시");
        test.log(LogStatus.INFO, "Step: 읽은 메시지 표시 확인");
        test.log(LogStatus.INFO, "Expected Result: 읽은 메시지에 ”既読(읽음)” 표시됨");

        boolean isDisplayed = false;
        while(isDisplayed){isDisplayed = android_utils.isElementDisplayed(driver, By.id("jp.naver.line.android:id/chathistory_row_read_count"));}
        test.log(LogStatus.INFO, test.addScreenCapture(android_utils.getScreenshot(driver)));

        test.log(LogStatus.INFO, "Test Result: 읽음표시 텍스트 > " + driver.findElementById("jp.naver.line.android:id/chathistory_row_read_count").getAttribute("text"));
        Assert.assertEquals(driver.findElementById("jp.naver.line.android:id/chathistory_row_read_count").getAttribute("text"), "既読");
    }

    // 송신측 보이스콜 발신
    @Test
    public void TC_04_call_send_A() throws Exception {
        test = report.startTest("Case：Device A 보이스콜 발신");
        test.log(LogStatus.INFO, "Step: 쳇룸 상단의「통화」 버튼 탭");
        test.log(LogStatus.INFO, "Step:「무료통화」버튼 탭");
        test.log(LogStatus.INFO, "Expected Result: 발신이 시작되고 통화화면에 대화상대의 ID가 표시됨");

        //todo 베타빌드 debug 노티표시 방해 우회 처리
        Thread.sleep(5000);
        driver.findElementByXPath("//android.widget.FrameLayout[@content-desc='発信 ボタン']/android.widget.ImageView").click();
        android_utils.findString(driver, "\"無料通話\"").click();
        driver.findElementById("jp.naver.line.android:id/call_mute_btn_icon").click(); // 하울링방지 Mute 버튼 클릭
        test.log(LogStatus.INFO, test.addScreenCapture(android_utils.getScreenshot(driver)));

        test.log(LogStatus.INFO, "Test Result: 대화상대 ID > " + driver.findElementById("jp.naver.line.android:id/voicecall_target_name").getAttribute("text"));
        Assert.assertTrue(driver.findElementById("jp.naver.line.android:id/voicecall_target_name").isDisplayed());
    }

    // 송신측 보이스콜 종료
    @Test
    public void TC_05_call_finish_A() throws Exception {
        test = report.startTest("Case：Device A 보이스콜 종료");
        test.log(LogStatus.INFO, "Step:5초간 통화후「통화종료」버튼 탭");
        test.log(LogStatus.INFO, "Expected Result: 통화가 종료되고 쳇룸에 발신이력이 표시됨");

        Thread.sleep(5000); // 5초간 통화
        boolean isDisplayed = false;
        while(isDisplayed){isDisplayed = android_utils.isElementDisplayed(driver, By.id("jp.naver.line.android:id/call_end_btn"));}

        driver.findElementById("jp.naver.line.android:id/call_end_btn").click();
        Thread.sleep(1000);
        test.log(LogStatus.INFO, test.addScreenCapture(android_utils.getScreenshot(driver)));
    }

    @AfterMethod
    public void getResult(ITestResult result) throws Exception {
        if (result.getStatus() == ITestResult.FAILURE){
            test.log(LogStatus.FAIL, result.getName());
            test.log(LogStatus.FAIL, result.getThrowable());
            test.log(LogStatus.FAIL, "Test Case Fail" + test.addScreenCapture(android_utils.getScreenshot(driver)));
        } else if (result.getStatus() == result.SUCCESS){
            test.log(LogStatus.PASS, result.getName());
        }
        report.endTest(test);
        report.flush();
        }

    @AfterClass
    public void endTest() {
        driver.quit();
    }

}