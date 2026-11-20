plugins {
    application
}

val jettyVersion = "12.0.13"
val slf4jVersion = "2.0.13"
val logbackVersion = "1.5.6"

java {
    toolchain {
        // JVM 21 still has region pinning issues. 25 has them fixed (pinned regions don't block G1 GC)
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    implementation("org.eclipse.jetty.ee10:jetty-ee10-servlet:$jettyVersion")
    implementation("com.github.luben:zstd-jni:1.5.6-3")
    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
}

application {
    mainClass.set("com.dynatrace.debugdemo.backendGcIssues.BackendApp")
}

tasks.named<JavaExec>("run") {
    doFirst { mkdir("logs") }
    jvmArgs(
        "-Xlog:gc*,gc+humongous=debug,gc+jni=debug:file=logs/gc-backend.log:time,uptime,level,tags:filecount=1,filesize=50m",
        "-Xmx9000M",
        "-XX:G1HeapRegionSize=210M",
        "-XX:G1ReservePercent=15"
    )
}
