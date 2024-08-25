# Kotlin-Code-Judge-KCJ
 
## 변경사항

### 2024.08.07
- 완료
    - 변수명 전면 개편 
    - CopyActionListener.kt에서 CopyTextActionListener.kt로 변경
    - MyToolWindowUI.kt: 에서 UTC를 분리
    - AddTestCaseActionListener.kt: AddTestCase 버튼을 눌렀을 때 새로운 테스트 케이스 패널을 생성하고 메인 패널에 추가하는 기능을 구현
    - New TestCase구현 완료

### 2024.08.08
- 완료  
    - TC IN/OUT/ERROR COPY 기능
    - Delete 버튼 기능 구현
    - Give coffee 버튼 기능 구현 (GiveCoffeeActionListener.kt)
    - BOJ 연동 UI 구현 및 기능 구현
- 진행중
    - Copy TestCase 버튼 기능 구현

### 2024.08.09
- 완료
    - Delete 버튼 UI와 기능 구현 부분 분리
- 진행중
    - TC창 축소 구현(시작)

### 2024.08.10
- 완료
    - 컴파일이 메인 스레드가 아닌 백그라운드 스레드에서 실행하도록 함
    - 컴파일러 연동
    - TC 전체 실행
    - 실행 후 결과 출력
    - 그래들 파일수정
    - 그 외 파일들 수정

### 2024.08.14
- 완료
    - 잘못된 실행 결과가 출력되는 버그 수정(WA,AC,RE,CE)

### 2024.08.15
- 완료
    - 체크 박스 UI 구현
    - TC 일부 실행 기능 구현

### 2024.08.16
- 완료
    - StopActionListener.kt: Stop 버튼 기능 구현
    - 실행 결과의 가독성을 높이기 위해 색상을 변경
    - 일부 변수명 변경

### 2024.08.17
- 완료
    - UIStateManager.kt: UI 상태 관리(UIState)

### 2024.08.18
- 완료
    - 컴파일 방식 변경(ms측정 방식도 변경)
    - 코드를 복사해서 컴파일 하는 방식에서 코드 복사 없이 원본코드를    사용할 수 있도록 수정
    - 현재 열려있는 파일 추적 방식 변경

### 2024.08.20
- 완료
    -  UIState 버그 수정
       -  실행 전-후에 따른 UI 상태 변화
### 2024.08.21
- 완료
    - UTC 리넘버링 버그 수정
  
### 2024.08.23
- Trunk-based 방식에서 GitFlow 방식으로 변경
- 리팩토링: MVS, DSL

### 2024.08.24
- 리팩토링

### 2024.08.25
- 리팩토링
    - feature: refactoring 진행중
    - Guide 버튼 정상 작동.
    - Give Coffee 버튼 정상 작동.
    - All 버튼 정상 작동.
    - Clear 버튼 정상 작동.
    - New TestCase 버튼 정상 작동.
    - Fetch Test Cases 버튼 정상 작동.
    - Copy 버튼 정상 작동.
    - Run 버튼 정상 작동.
    - Some Run 버튼 정상 작동.
## 예정
- ~~TC창 UI 상태 관리~~
    - ~~0-1. 기능은 파일을 분리해서 만듬~~
    - ~~0-2. 전체 적용이 아닌 각 테스트 케이스 마다 적용이 된다.~~
    - 
    - ~~1-1. TC레이아웃은 UiFolded상태, UiMidway상태, UiExpanded상태인 3가지 상태가 있다~~
    - 
    - ~~2-1. UiFolded상태 (실행전후에 가능, 실행 후 결과가 AC일 경우 기본 상태)~~
    - ~~2-2. Delete레이아웃 까지만 존재 (display)~~
    - ~~2-3. In탭, Out탭, Answer탭, Cerr탭을 비활성화 해둠(display: none)~~
    - ~~2-4. 실행후 실행 결과가 AC 일 경우UiFolded상태로 변경~~
    - ~~2-5. 실행전  uiStateButton을 누르면  UiMidway상태로 변경할 수 있다.~~
    - ~~2-6. 실행후  uiStateButton을 누르면  UiExpanded상태로 변경할 수 있다.~~
    - 
    - ~~3-1. 실행전 UiMidway상태 (실행전에만 가능, 실행전 기본 상태)~~
    - ~~3-2. In,Out 레이아웃까지만 존재 (display)~~
    - ~~3-3. Answer탭, Cerr탭을 비활성화 해둠(display: none)~~
    - ~~3-4. uiStateButton을 누르면 UiFolded상태로 변경할 수 있다.~~
    - 
    - ~~4-1. UiExpanded상태 (실행후에만 가능)~~
    - ~~4-2. 모든 레이아웃 존재 (display)~~
    - ~~4-3. 실행후 실행 결과가 WA,CE,TLE,RE,Unknown Error등 AC가 아닐 경우 UiExpanded상태로 변경~~
    - ~~4-4. uiStateButton을 누르면 UiFolded상태로 변경할 수 있다.UI 상태 (UIState)~~
- 아이콘 변경
- 설정 파일
    - 입출력창 크기
    - 시간초과 최댓값
- ~~유저가 만든 테스트 케이스(이하 TC) 등록~~
- ~~BOJ 연동하여 TC 등록~~ (가능 하다면 Codeforces, AtCoder도 연동 가능하게 구현)
    - ~~TC 부분에 대해서만 웹 스크래핑을 해야함~~
    - ~~너무 많은 리퀘스트가 생기지 않도록 해야함~~
- ~~프로젝트 도구창(또는 실행 도구 창)에 UI 구성~~
    - ~~최대한 간결하게 하며 화면 크기 변화에 대한 대응을 할 수 있어야함~~
- ~~KOTLIN 컴파일러 연동~~
    - ~~유저에게 보이지 않고 내부에서 돌아게할 방법 찾아야함~~
- TC 저장 파일
    - json으로 저장
    - TC의 정보들 저장(등록된TC,TC결과, 선택한TC,디파인 제외 체크 등)
    - 각 객체의 In,Out,Answer,Cerr 등을 json으로 저장
    - 파일 트리를 깔끔하게 유지하기 위해 TC파일을 만들어서 보관
- ~~TC 등록~~
- ~~TC 삭제~~
- ~~TC 전체 실행~~
- ~~TC 일부 실행(체크 박스로 선택 가능하게)~~
    - ~~전체 선택 & 전체 해제 버튼 필요~~
- ~~TC 전체 중지~~
- ~~TC IN/OUT/ERROR COPY 기능~~
- ~~Give coffee 버튼~~
- ~~실행 후 결과 출력~~
    - ~~judging (채점 중)~~
    - ~~AC: Accepted (맞았습니다)~~
    - TLE : Time Limit Exceeded (시간 초과)
    - ~~WA : Wrong Answer (틀렸습니다)~~
    - ~~CE : Compilation Error (컴파일 에러)~~