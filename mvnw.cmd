@echo off
if not exist .mvn\wrapper\maven-wrapper.jar (
    echo Maven Wrapper not found. Please install Maven or use a different method to build.
    exit /b 1
)
java -cp .mvn\wrapper\maven-wrapper.jar org.apache.maven.wrapper.MavenWrapperMain %*