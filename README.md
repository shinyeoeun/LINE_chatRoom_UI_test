# parallel TestAutomation for LINE App
Selenium Grid와 TestNG 프레임워크로 여러 디바이스를 동시에 테스트하는 스크립트 

## About Test
2대의 디바이스에서 두명의 라인유저가 채팅과 통화를 주고받는 시나리오로 구성

### Demo
![chatroom](https://user-images.githubusercontent.com/25470405/76373396-a3e85780-6383-11ea-9269-d100f22d626a.gif)

### Scenario
> 1. Device A: Device B 에게 텍스트 메시지 송신
> 2. Device B: 수신메시지 확인(텍스트 내용 & 송신시간)
> 3. Device A: Device B 에게 음성통화 발신
> 4. Device B: 통화 수락 후 5초간 대기(통화상태)
> 4. Device A: 통화 종료

### Test Devices
|Name|Device|OS version|
|------|------|------|
|Device A|Pixel 4|10.0|
|Device B|Galaxy S10|9.0|



## Test Result
* 테스트 결과 리포트

![2020-03-11_10h25_15](https://user-images.githubusercontent.com/25470405/76374294-1f4b0880-6386-11ea-8d1b-3524916f4860.gif)



## Description

### 1. test suite용 xml파일을 작성

* suite
    
    ```xml
    <suite name="Suite" verbose="5" parallel="tests" thread-count="2">
    ```
    
    + parallel 
    
      병행처리 기준이 되는 단위. tests / classes / methods / suites 지정 가능
    
    + thread-count 
    
      동시에 시행되는 테스트 최대수 (고속도로의 차선같은 개념)
      
    + verbose
    
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
    + parameter
    
      각 디바이스의 capability 값들을 정의
      ※ systemPort와 port값은 각 디바이스마다 다르게 설정해야함 (systemPort: 8201~ / port: 4724~) 아래는 예시

        |Name|port|systemPort|
        |------|------|------|
        |Device A|4600|8202|
        |Device B|4700|8201|

    + classes
      
      테스트 실행 단위. class가 아닌 package,method 단위로도 구성할수 있음
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
    
     + preserve-order
       같은 스크립트를 여러 디바이스에서 동시에 실행하려면를 "false"로 지정 <br/> 
       본 테스트는 test별로 각각 다른 스크립트를 사용하므로 default(true)로 지정  
        ```xml
        <!-- 무작위 순서로 테스트 -->
        <test name="pixel_4" preserve-order="false">
        ```
### 2. 테스트 스크립트 작성

```java

```

## Directory Structure
![2020-03-11_10h29_32](https://user-images.githubusercontent.com/25470405/76376558-e877f100-638b-11ea-84c9-280291c78fc5.png)

## Usage
1. 병렬테스트환경 셋업
링크참조

2. TestNG

## References
* https://appiumpro.com/editions/5
* https://blog.naver.com/wisestone2007/221321329618
