# Image Search

1. 과제 내용:
     - 카카오 'Daum 검색 - 이미지 검색' api를 사용하여 이미지 검색 앱을 만듭니다.
2. 확인사항 :
    - 카카오 Developer 계정은 개인 계정으로 만듭니다.
    -  EditText에 문자를 입력 후 1초가 지나면 자동으로 검색이 됩니다.
    -  검색어가 변경되면 목록 리셋 후 다시 데이터를 fetch 합니다.
       데이터는 30개씩 페이징 처리합니다. (최초 30개 데이터 fetch 후 스크롤 시 30개씩 추가 fetch)
   - 검색 결과 목록은 3xN 그리드 모양으로 구성합니다.
       검색 결과가 없을 경우 '검색 결과가 없습니다.' 메시지를 화면에 보여줍니다.
       검색 결과 목록 중 하나를 탭 하였을 때 전체 화면으로 이미지를 보여줍니다.
       좌우 여백이 0이고, 이미지 비율은 유지하도록 보여줍니다.
       이미지가 세로로 길 경우 스크롤 됩니다.
       response 데이터에 출처 'display_sitename', 문서 작성 시간 'datetime'이 있을 경우 전체화면 이미지 밑에 표시해 줍니다.
       이 외 UI는 자유롭게 구성합니다.
   - 오픈소스 라이브러리는 자유롭게 사용 가능합니다.
   - 결과물은 개인 github에 올려주시고,  labs-ma-aos@brandi.co.kr 으로 URL 공유 부탁드립니다.
3. 우대사항
    - RxJava 사용
    - Mvvm 패턴
    - Di(Dependency Injection)적용 - Koin,Dagger2
    - Test 코드 구현
    - Error 핸들링

## 빌드 요구사항
1. Android Studio 4.0 이상
2. SDK 29 이상
3. Kotlin 1.4 필요

## 요구사항 분석
### 1. 서버 분석
1. [카카오 이미지 검색 API](https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-image) 서버는 페이징 기능이 있고, 요구사항에도 **페이징기능**을 요구
2. 네트워크 케싱을 위해 **DB** 필요
### 2. UI 분석
1. 이미지 검색을 위해 serch bar 필요
2. 3열 그리드 리스트 필요
3. 이미지 상세 화면 필요
### 3. 앱 분석
1. 네트워크 쿼리를 통해 검색한 이미지 목록을 불러 올것
> 네트워크 쿼리를 실시간으로 하되 1초의 대기 시간 필요
2. 에러 핸들링 기능 필요
#### 4. 추가 구현
1. 이미지 상세 페이지를 갤러리 형식으로 좌우 페이징하여 리스트를 탐색할 수 있음
4. 최초 사용자 가이드 및 로딩 화면 추가
5. 이미 상세 화면으로 가는 트렌지션 구현

## 앱 구조
전체적으로 MVVM 페턴을 따르고 있습니다
네트워크로 부터 들어온 이미지 정보는 DB에 저장이 되고 이 정보는 [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel?hl=ko)
로 들어 옵니다 이렇게 들어온 데이터는
[Android Databinding](https://developer.android.com/topic/libraries/data-binding?hl=ko) 과
[LiveData](https://developer.android.com/topic/libraries/architecture/livedata?hl=ko) 를
사용해 layout과 연결 됩니다
이때 사용하는 데이터베이스 모듈과 네트워크 모듈은 [Hilt DI](https://developer.android.com/training/dependency-injection/hilt-android)
라이브러리로 관리 합니다

### 화면  페키지 구성
[ui 페키지][com.gondev.searchimage.ui] 내에
MainActivity, GalleryActivity 두개의 화면으로 구성 되어 있습니다
1. MainActivity: 이미지 검색 화면입니다
2. SearchActivity: 검색한 이미지 원본화면을 표시 합니다

### 모델  페키지 구성
1. network
   1. api: Google Book API와 통신하는 서비스를 제공합니다
   2. dto: 서버로 부터 받은 데이터를 담습니다
2. database
   1. entity: 디비 스키마를 구성합니다
   2. dao: entity를 쿼리 합니다
3. di.module: 위 두개의 페키지를 모듈화 하여 ViewModel에 전달 하기 위한 di 페키지입니다

## 오픈소스 라이브러리
1. [Android Architecture components](https://developer.android.com/jetpack/androidx/releases/lifecycle)
안드로이드 ViewModel을 지원하고 라이프사이클에 맞게 제어 하는 역할을 합니다
2. [Dagger-Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
의존성주입(Dependency Injection) 라이브러리입니다
클레스에서 사용하는 인스턴스를 외부에서 주입 받아
클레스간 의존성을 줄이고 테스트를 용의하게 해줍니다 Dagger는 컴파일 단계에서 의존성을 주입해 주는 강력한
라이브러리이지만 사용법이 간단하지 않아 제대로 사용 하려면 많은 학습이 필요합니다
hilt는 dagger의 많은 부분을 ViewModel에 맞게 재조정하여 쉽고 간편하게 사용할 수 있는 장점이 있습니다
따라서 이번 프로젝트에서는 hilt를 사용하여 의존성 주입을 해보겠습니다
    > 참고: Hilt와 데이터 바인딩을 모두 사용하는 프로젝트에는 Android 스튜디오 4.0 이상이 필요합니다.
3. [Retrofit](https://square.github.io/retrofit/)
httpClient rapper library입니다
http 프로토콜을 통해서 WAS에 접근하는 라이브러리입니다 Rest API 메소드를 제공할 뿐만 아니라
Gson과 연계해서 파싱지원을 하는 등 강력하고 유연한 API 콜을 제공합니다
4. [Room](https://developer.android.com/topic/libraries/architecture/room)
SQLite 기반의 데이터 영속화 라이브러리입니다
네트워크 데이터를 DB에 저장하고 케쉬 처럼 읽고 쓸 수 있습니다
네트워크 통신전에 DB에 데이터가 있으면, 이 데이터로 화면을 구성하고 네트워크 데이터로 수정합니다
이렇게 하면 화면 반응성이 빨라집니다
5. [Paging](https://developer.android.com/topic/libraries/architecture/paging?hl=ko)
페이징 라이브러리입니다
리스트를 분할하여 페이지 단위로 조금식 가저오기 위한 기능 입니다
이렇게 하면 사용자가 필요한 만큼만 보여 주기 때문에 성능이나 효율성 측면에서 많은 도움이 됩니다
6. [Timber](https://github.com/JakeWharton/timber)
로그 라이브러리입니다
7. [Glide](https://bumptech.github.io/glide/)
이미지 로드 라이브러리입니다
네트워크 이미지 다운로드, 이미지 케싱, 이미지 변환, 이미지 사용 메모리 관리, 화면에 이미지 출력을 위해 사용합니다