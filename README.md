# 📸 SnapLog

> 일상의 순간을 사진과 함께 기록하는 메모 앱

<br>

## 📱 주요 기능

| 화면 | 설명 |
|------|------|
| 홈 | 저장된 기록 목록 조회, 태그별 필터링 |
| 기록 저장 | 사진 촬영 또는 갤러리에서 선택, 태그 및 메모 입력 |
| 카메라 | CameraX 기반 커스텀 카메라 (플래시, 격자, 핀치 줌, 탭 포커스) |
| 기록 상세 | 저장된 사진, 태그, 메모, 날짜 조회 |
| 기록 수정 | 사진, 태그, 메모 수정 및 기록 삭제 |

<br>

## 🛠 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM, Clean Architecture |
| DI | Hilt |
| Database | Room |
| Camera | CameraX |
| Image | Coil |
| Navigation | Jetpack Navigation Compose |
| Async | Coroutines |

<br>

## 🏗 아키텍처

com.example.snaplog  
├── data  
│   ├── local (Room DB, DAO, Entity)  
│   └── repository  
├── domain  
│   ├── model  
│   ├── repository  
│   └── usecase  
└── presentation  
├── camera  
├── capture  
├── detail  
├── home  
├── navigation  
└── update  
  
<br>

## 🔍 기술적 고민

### CameraX 기반 커스텀 카메라 구현
- `ProcessCameraProvider`로 카메라 생명주기 관리
- `ScaleGestureDetector`로 핀치 줌, `FocusMeteringAction`으로 탭 포커스 구현
- `ImageCapture` UseCase로 `FileProvider`를 통한 안전한 이미지 저장

### savedStateHandle을 활용한 화면 간 상태 유지
- 카메라 촬영 후 복귀 시 기존 입력(태그, 메모) 유지
- `ViewModel` 생명주기 특성을 활용해 백스택에 상태 보존
- `savedStateHandle`로 화면 간 이미지 경로 전달
