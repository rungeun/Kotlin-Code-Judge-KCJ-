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


## 예정
- TC창 축소
    - 테스트케이스 생성시 Answer탭과 Cerr탭을 비활성화 해둠(display: none)
    - Run버튼, Some Run버튼을 눌렀을 경우 활성화, (display)
    - Run버튼, Some Run버튼을 눌렀을 경우 `#실행 후 결과`로 화면을 완전히 축소, In,Out,Answer,Cerr탭은 폴드 상태이며 버튼을 통해 펼칠 수 있도록 
    - 1-1. TC레이아웃의 UiFolded상태, UiMidway상태, UiExpanded상태
    - 1-2. 기본값은 실행전 UiMidway상태이다.
    - 
    - 2-1 UiFolded상태 (실행전후에 가능)
    - 2-2 Delete레이아웃 까지만 존재
    - 
    - 3-1. 실행전 UiMidway상태 (실행전에만 가능)
    - 3-2. In,Out 레이아웃까지만 존재
    - 3-2 UiFolded상태로 넘길 수 있다.
    - 
    - 4-1. UiExpanded상태 (실행후에만 가능)
    - 4-2. 모든 레이아웃 존재
    - 4-3. 실행후 실행 결과가 WA,CE,TLE 중에 하나일 경우 UiExpanded상태로 변경
    - 4-4. UiFolded상태로 넘길 수 있다.
    - 
    - 5-1 . 실행후 실행 결과가 CA 일 경우 UiFolded상태로 변경


- 각 객체의 In,Out,Answer,Cerr를 json으로 저장
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
- KOTLIN 컴파일러 연동
    - 유저에게 보이지 않고 내부에서 돌아게할 방법 찾아야함
- TC 저장 파일
    - json으로 저장
    - TC의 정보들 저장(등록된TC,TC결과, 선택한TC,디파인 제외 체크 등)
    - 파일 트리를 깔끔하게 유지하기 위해 TC파일을 만들어서 보관
- ~~TC 등록~~
- ~~TC 삭제~~
- TC 전체 실행
- TC 일부 실행(체크 박스로 선택 가능하게)
    - 전체 선택 & 전체 해제 버튼 필요
- TC 전체 중지
- ~~TC IN/OUT/ERROR COPY 기능~~
- ~~Give coffee 버튼~~
- 디파인 컴파일 제외 기능 체크 박스로 구현 
- 실행 후 결과 출력
    - judging (채점 중)
    - AC: Accepted (맞았습니다)
    - TLE : Time Limit Exceeded (시간 초과)
    - WA : Wrong Answer (틀렸습니다)
    - CE : Compilation Error (컴파일 에러)
- 컴파일 횟수(정답률 기록)
    - 보류
