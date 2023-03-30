plugins {
	java
	id("org.springframework.boot") version "3.0.4"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.testapp"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework:spring-webflux")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
}

tasks.register<Copy>("copyJar") {
	println("Copying Built Jar")
	from("$buildDir/libs/be-ms-posts-0.0.1-SNAPSHOT.jar")
	into("build-libs")
}

tasks.named("build") { finalizedBy("copyJar") }

//tasks.withType<Test> {
//	useJUnitPlatform()
//}
