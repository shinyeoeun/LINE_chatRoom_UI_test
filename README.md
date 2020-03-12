# Parallel TestAutomation for LINE App
쳇룸,통화 등 두대 이상의 디바이스간의 커뮤니케이션이 필요한 테스트케이스를 자동화하기 위해 
병렬로 여러 디바이스를 동시에 테스트하는 스크립트를 구현해보았다.

* Using Tools
    + Selenium Grid, Appium, TestNG, Maven, ExtentReports, uiautomator

## About Test

### Scenario
    1. Device A: Device B 에게 텍스트 메시지 송신
    2. Device B: 수신메시지 확인(텍스트 내용 & 송신시간)
    3. Device A: Device B 에게 음성통화 발신
    4. Device B: 통화 수락 후 5초간 대기(통화상태)
    5. Device A: 통화 종료

### Demo
![chatroom](https://user-images.githubusercontent.com/25470405/76373396-a3e85780-6383-11ea-9269-d100f22d626a.gif)

### Test Devices
|Name|Device|OS version|
|------|------|------|
|Device A|Pixel 4|10.0|
|Device B|Galaxy S10|9.0|


## Test Result
* 테스트 결과 리포트

![2020-03-11_10h25_15](https://user-images.githubusercontent.com/25470405/76374294-1f4b0880-6386-11ea-8d1b-3524916f4860.gif)



## Description

### 1. test suite xml파일

* suite
    
    ```xml
    <suite name="Suite" verbose="5" parallel="tests" thread-count="2">
    ```
    
    + parallel <br/>
      병행처리 기준이 되는 단위. tests / classes / methods / suites 지정 가능
    
    + thread-count <br/>
      동시에 시행되는 테스트 최대수 (고속도로의 차선같은 개념)
      
    + verbose <br/>
      콘솔로그의 세부정보 레벨. 숫자가 클수록 상세한 로그를 출력

* test
    ```xml
        <test name="Galaxy_S10">
            <parameter name="platform" value="Android" />
            <parameter name="appl" value="app" />
            <parameter name="port" value="4700" />
            <parameter name="platformVersion" value="9.0" />
            <parameter name="deviceName" value="Galaxy S10" />
            <parameter name="udid" value="xxxxx"/>
            <parameter name="automationName" value="uiautomator2"/>
            <parameter name="systemPort" value="8201"/>
            <classes>
                <class name="LineChatRoomTest_receive"/>
            </classes>
        </test>
    ```
    + parameter <br/> 
      각 디바이스의 capability 값들을 정의 <br/>
      ※ systemPort와 port값은 각 디바이스마다 다르게 설정해야함 (systemPort: 8201~ / port: 4724~) 아래는 예시

        |Name|port|systemPort|
        |------|------|------|
        |Device A|4600|8202|
        |Device B|4700|8201|

    + classes <br/> 
      테스트 실행 단위 <br/>
      class가 아닌 package,method 단위로도 구성할수 있음
      
      ```xml
        <!-- method 단위로 작성한 경우 샘플 -->
        <classes>
            <class name="LineChatRoomTest_receive">
                <methods>
                    <include name="method_01" />
                    <include name="method_02" />
                </methods>
            </class>   
        </classes>
        ```
    
     + preserve-order <br/> 
       같은 스크립트를 여러 디바이스에서 동시에 실행하려면 preserve-order를 "false"로 지정 <br/> 
       본 테스트는 test별로 각각 다른 스크립트를 사용하므로 default(true)로 지정  
        ```xml
        <!-- 무작위 순서로 테스트 -->
        <test name="pixel_4" preserve-order="false">
        ```
### 2. 테스트 스크립트

* @BeforeClass

    테스트케이스를 실행하기 전에 사전에 필요한 설정들을 정의하는 메소드 <br/>
    @parameter 어노테이션에 Appium서버로 넘길 capabilities를 정의해 놓으면<br/>
    테스트 실행시 test suite xml에 정의된 값들로 매핑됨

    ```java
    @BeforeClass
    @Parameters({"platform", "platformName", "udid", "deviceName", "platformVersion", "automationName", "systemPort"})
    public void setUp(String platformName, String port, String udid, String deviceName, String platformVersion, String automationName, String systemPort) throws MalformedURLException {

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
    ```
* @Test

    테스트 케이스 작성 <br/>
    Test 생성(for extent reports) > 실제 앱 조작 > 결과판정 순으로 동작

    ```java
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
    ```
    
* @AfterMethod

    각 메소드(테스트케이스) 종료시에 수행할 작업들을 정의<br/>
    여기서는 assert문 결과값 기반으로 테스트결과리포트 출력처리를 수행

    ```java
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
 
    ```       
    
* @AfterClass
    
    모든 테스트케이스 종료 후 수행할 작업들을 정의 
    
    ```java
    @AfterClass
    public void endTest() {
        driver.quit();
    }
    ```

### 3. 공통 유틸
스크립트 공통부분 모듈화<br/>
모듈 중 다른 프로젝트에서도 쓸만한 일부모듈을 소개함

* getScreenshot

    디바이스 화면을 캡쳐하여 png로 저장해주고 그 경로를 반화하는 메소드<br/>
    이 경로는 테스트결과리포트 html img 태그에 매핑되어 스크린샷이 첨부된 결과리포트 작성 가능! 

    ```java
        static public String getScreenshot(WebDriver driver) throws IOException {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            String dest = System.getProperty("user.dir")+"/Screenshot/"+System.currentTimeMillis()+".png";
            File target = new File(dest);
            FileUtils.copyFile(src, target);

            return dest;
        }
    ```

* isElementDisplayed

    특정요소가 화면에 표시되었는지를 판단하여 boolean값을 반환하는 메소드<br/>
    By 타입을 파라미터로 넘기기 때문에 xpath, id 등 다양한 요소타입 대응 가능!

    ```java
        static public boolean isElementDisplayed(AndroidDriver driver, By by) {
            try {
                driver.findElement(by);
                return true;
            } catch (NoSuchElementException e) {
                return false;
            }
        }
    ```
    
* API

    개인프로젝트고 API 남길만한 코드는 아니지만 intelij 사용법 연습겸 공통모듈한정으로 javadoc을 작성해봄
![2020-03-12_17h35_23](https://user-images.githubusercontent.com/25470405/76503986-26f3d580-648a-11ea-8401-2a2aa9c1e55d.png)

## Directory Structure
![2020-03-11_10h29_32](https://user-images.githubusercontent.com/25470405/76504044-42f77700-648a-11ea-9ddf-526d8c9ee2cd.png)


## References
* https://appiumpro.com/editions/5
* https://blog.naver.com/wisestone2007/221321329618
