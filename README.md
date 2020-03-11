# parallel_TestAutomation_Line_App
TestNG와 Selenium Grid를 이용하여 여러 디바이스를 동시에 테스트하는 스크립트 

## 테스트 시나리오
2대의 디바이스에서 두명의 라인유저가 채팅과 통화를 주고받는 시나리오로 구성

* 디바이스 구성: 

Device A(Galayxy S10), Device B(Pixel 4)

* 시나리오: 
1. Device A: Device B 에게 텍스트 메시지 송신
2. Device B: 수신메시지 확인(텍스트 내용 & 송신시간)
3. Device A: Device B 에게 음성통화 발신
4. Device B: 통화 수락 후 5초간 대기(통화상태)
4. Device A: 통화 종료

* ↓ 동작영상
![chatroom](https://user-images.githubusercontent.com/25470405/76373396-a3e85780-6383-11ea-9269-d100f22d626a.gif)


## 테스트 결과
* 테스트 결과 리포트
![2020-03-11_10h25_15](https://user-images.githubusercontent.com/25470405/76374294-1f4b0880-6386-11ea-8d1b-3524916f4860.gif)




## 동작 설명


## 파일 구조


## 사용 예제


## 참고자료
* https://appiumpro.com/editions/5
* https://blog.naver.com/wisestone2007/221321329618
