/**
 * LINE 앱 테스트 자동화 스크립트 유틸(Android)
 * @author Shin
 */


package utils;

import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class android_utils {

    /**
     * 현재시간을 라인 쳇룸 표시형식으로 변환
     * @return 현재시간(format: h:mm 午前/午後)
     */
    static public String getChatRoomDisplayTime(){
        Date today = new Date();
        SimpleDateFormat ampm = new SimpleDateFormat("a", Locale.JAPAN);
        SimpleDateFormat time = new SimpleDateFormat("h:mm");

        return ampm.format(today) + time.format(today);
    }

    /**
     * 디바이스 화면을 캡쳐하여 png로 저장
     * @param driver 안드로이드 드라이버
     * @return 스크린샷 저장 경로
     * @exception IOException 입출력 에러
     */
    static public String getScreenshot(WebDriver driver) throws IOException {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File src = ts.getScreenshotAs(OutputType.FILE);
        String dest = System.getProperty("user.dir")+"/report/screenshot/"+System.currentTimeMillis()+".png";
        File target = new File(dest);
        FileUtils.copyFile(src, target);

        return dest;
    }

    /**
     * 라인 앱 화면의 헤더타이틀 텍스트 취득
     * @param driver 안드로이드 드라이버
     * @return 헤더타이틀 텍스트
     */
    static public String getHeaderTitle(AndroidDriver driver){
        String headerTitle;
        headerTitle = driver.findElement(By.id("jp.naver.line.android:id/header_title")).getText();
        return headerTitle;
    }

    /**
     * 화면을 스캔하여 특정텍스트를 지닌 요소를 반환
     * @param driver 안드로이드 드라이버
     * @param targetString 검색 텍스트
     * @return 텍스트 검출 요소
     */
    static public WebElement findString(AndroidDriver driver, String targetString){
        WebElement element;
        element = driver.findElementByAndroidUIAutomator("new UiSelector().text(" + targetString + ")");
        return element;
    }

    /**
     * 파일 네이밍용 현재날짜
     * @return 현재날짜(포맷: yyyy-MM-dd_HHmm)
     */
    static public String getNowDateTime(){
        Date today = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HHmm");

        String NowDateTime = time.format(today);

        return NowDateTime;
    }

    /**
     * 특정요소가 화면에 표시되어있는지 여부 확인
     * @param driver 안드로이드 드라이버
     * @param by 요소 타입(xpath,id..)
     * @return 요소 화면 표시 여부
     */
    static public boolean isElementDisplayed(AndroidDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
