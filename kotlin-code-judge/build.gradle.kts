plugins {
  id("java") // Java 플러그인 적용
  id("org.jetbrains.kotlin.jvm") version "1.9.24" // Kotlin JVM 플러그인 적용
  id("org.jetbrains.intellij") version "1.17.3"   // IntelliJ 플러그인 적용
}

group = "com.github.rungeun.kcj"  // 그룹 ID 설정
version = "1.0-SNAPSHOT"          // 버전 설정

repositories {
  mavenCentral()                  // Maven 중앙 저장소 사용
}

// Gradle IntelliJ 플러그인 구성
// 자세한 내용: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2023.2.6")// 사용할 IntelliJ 버전 설정
  type.set("IC") // 대상 IDE 플랫폼 설정

  plugins.set(listOf(/* Plugin Dependencies */))// 플러그인 의존성 설정
}

tasks {
  // JVM 호환성 버전 설정
  withType<JavaCompile> {
    sourceCompatibility = "17"                    // 소스 호환성 버전 설정
    targetCompatibility = "17"                    // 타겟 호환성 버전 설정
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"                // Kotlin JVM 타겟 설정
  }

  patchPluginXml {
    sinceBuild.set("232")                         // 플러그인 지원 시작 빌드 설정
    untilBuild.set("242.*")                       // 플러그인 지원 종료 빌드 설정
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))// 인증서 체인 설정
    privateKey.set(System.getenv("PRIVATE_KEY"))            // 개인 키 설정
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))     // 개인 키 비밀번호 설정
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))               // 플러그인 배포 토큰 설정
  }
}
// 추가: 종속성 설정
dependencies {
  implementation(kotlin("stdlib")) // Kotlin 표준 라이브러리 종속성 추가
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
  implementation("org.jsoup:jsoup:1.14.3")
}