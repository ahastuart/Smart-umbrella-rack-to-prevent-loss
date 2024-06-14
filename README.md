# 2024 사물인터넷 캡스톤 디자인 프로젝트

주제: Smart-umbrella-stand-to-prevent-loss <br>
      분실방지 스마트 우산꽂이


## 개요

+ 고객들이 식당이나 공공 장소에서 우산을 사용할 때 발생하는 분실 문제를 해결하기 위해 개발된 시스템 <br>
+ 사용자가 우산을 넣으면 시스템이 자동으로 사용자와 우산을 매칭시켜 우산을 인식하고 보관

## 주요 기능 및 시스템 구성

1.우산-사용자 매칭
>* object tracking 사용 <br>
>* 식당 문 안쪽에 카메라(CCTV)를 설치하여 기술 사용 <br>
>![image](https://github.com/ahastuart/Smart-umbrella-stand-to-prevent-loss/assets/117139643/4d8078cf-6654-4096-8e94-090f33a85c90)
>+ 사용자가 우산을 들고 식당에 들어오면 Object tracking 기술을 통해서 사람과 우산만 인식하고 tracking함.
>+ 우산과 사람 사이의 중심 좌표로 최단거리를 계산하여 이 우산의 소유자가 이 사람이다라는 것을 매칭함.
>+ A라는 사람이 우산꽂이에 우산을 넣으면, A의 우산이 우산꽂이 내부 몇번 칸에 보관되고 있는지 그 정보가 저장됨.
그 후 A가 식당을 다 사용하고 나가기 전, 계산을 하기 위해 계산대 주변 bounding box 내부에 들어오게 되면, A의 우산이 잠금해제 되어 우산을 가지고 나갈 수 있도록 함.

2. 우산 보관
>* 초음파 센서로 칸별 우산의 여부를 인식 <br>
>* 모터를 이용한 잠금 및 해제 기능

3. 모니터링
>* 로그인 : 여러 식당에서 사용가능하도록 상용화 목적 <br>
>* 자동 알림 기능 : 잠금 해제 후, 일정 시간이 지났음에도 우산을 가져가지 않을 경우 식당 주인에게 알림 전송 <br>
>* 우산꽂이 보관 정보 : 우산꽂이의 현황, 우산 보관 시간, 우산 사용자 이미지 저장

4. 시스템 구성

## 기술 및 하드웨어 소개

## 시연 영상
https://youtube.com/watch?v=cqWXhCVbndw&si=qRCqfZIuVNtB75iX
<br>
## 참고 자료
* object tracking <br>
https://github.com/RizwanMunawar/yolov8-object-tracking?tab=readme-ov-file <br>
* Firebase <br>
https://firebase.google.com/docs/auth/android/email-link-auth?hl=ko&_gl=1*u2revp*_up*MQ..*_ga*MTcyODIzODIwNS4xNzE4MTA5NDgz*_ga_CW55HF8NVT*MTcxODEwOTQ4Mi4xLjAuMTcxODEwOTQ4Mi4wLjAuMA..​ <br>
https://firebase.google.com/docs/cloud-messaging/android/client?hl=ko&_gl=1*1g8882u*_up*MQ..*_ga*MzY5ODY2NjM4LjE3MTgxMDk1NDE.*_ga_CW55HF8NVT*MTcxODEwOTU0MC4xLjAuMTcxODEwOTU0MC4wLjAuMA..​ <br>
* RasberryPi <br>
https://bradheo.tistory.com/entry/%EB%9D%BC%EC%A6%88%EB%B2%A0%EB%A6%AC%ED%8C%8C%EC%9D%B4-%EB%AA%A8%EB%8B%88%ED%84%B0-%EC%97%86%EC%9D%B4-%EC%97%B0%EA%B2%B0​ <br>
https://security-jeong.tistory.com/97 ​<br>
https://splendidlolli.tistory.com/474​ <br>
https://dalseobi.tistory.com/72 <br>

