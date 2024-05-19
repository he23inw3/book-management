import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.MetricType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:10.11.0")
    }
}

plugins {
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	id("com.avast.gradle.docker-compose") version "0.17.0"
	id("org.flywaydb.flyway") version "10.11.0"
	id("org.jooq.jooq-codegen-gradle") version "3.19.6"
	id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
	id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.jetbrains.kotlinx.kover") version "0.7.5"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

kotlin {
	sourceSets["main"].kotlin {
		srcDirs("build/generated/source")
	}
	sourceSets["test"].kotlin {
		srcDirs("build/generated/source")
	}
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}

	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// OpenApi
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

	// DB
	implementation("org.postgresql:postgresql")

	// JOOQ
	implementation("org.jooq:jooq:3.19.7")
	jooqCodegen("org.postgresql:postgresql")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.13.4")
}

dockerCompose {
	setProjectName("book-management")
	useComposeFiles = listOf("./docker-compose.yml")
}

flyway {
    driver = "org.postgresql.Driver"
    url = System.getenv("DATABASE_URL")
    schemas = arrayOf(System.getenv("DATABASE_SCHEMA"))
    user = System.getenv("DATABASE_USERNAME")
    password = System.getenv("DATABASE_PASSWORD")
    cleanDisabled = false
}

jooq {
	configuration {
		jdbc {
			driver = "org.postgresql.Driver"
			url = System.getenv("DATABASE_URL")
			user = System.getenv("DATABASE_USERNAME")
			password = System.getenv("DATABASE_PASSWORD")
		}
		generator {
			database {
				name = "org.jooq.meta.postgres.PostgresDatabase"
				inputSchema = System.getenv("DATABASE_SCHEMA")
				excludes = "flyway_schema_history"
			}
			generate {
				name = "org.jooq.codegen.KotlinGenerator"
				isDeprecated = false
				isTables = true
				isPojosAsKotlinDataClasses = true
                isSequences = true
			}
			target {
				packageName = "com.example.jooqexample.jooq"
				directory = "${layout.buildDirectory.get()}/generated/source"
				encoding = "UTF-8"
				isClean = true
			}
		}
	}
}

detekt {
	buildUponDefaultConfig = true
	config.setFrom("$projectDir/detekt.yml")
}

koverReport {
    filters {
        excludes {
            packages(
                "com.example.jooqexample.jooq.*", // generated source.
                "com.example.bookmanagement.openapi",
                "com.example.bookmanagement.constant",
            )
        }
        includes {
            packages("com.example.bookmanagement.*")
        }
    }

    verify {
        rule("Basic Line Coverage") {
            isEnabled = true
            bound {
                minValue = 80
                metric = MetricType.LINE
                aggregation = AggregationType.COVERED_PERCENTAGE
            }
        }

        rule("Branch Coverage") {
            isEnabled = true
            bound {
                minValue = 70
                metric = MetricType.BRANCH
                aggregation = AggregationType.COVERED_PERCENTAGE
            }
        }
    }
}

tasks {
    withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-Xjsr305=strict"
			jvmTarget = "21"
		}
	}

	withType<Test> {
		useJUnitPlatform()
	}

    bootJar {
        launchScript()
    }
}

