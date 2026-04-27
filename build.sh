#!/bin/bash
# FakkaFlow Build Script
# Requirements: Java 17+, Maven 3.6+

echo "=== FakkaFlow Build Script ==="

# Option 1: Maven (recommended for students with internet access)
if command -v mvn &> /dev/null; then
    echo "Building with Maven..."
    mvn clean package -DskipTests
    echo "Run with: java -jar target/fakkaflow-1.0-SNAPSHOT-shaded.jar"
    exit 0
fi

# Option 2: Direct javac (if Maven unavailable)
echo "Maven not found. Building with javac..."
JAVA_HOME_BIN=$(dirname $(readlink -f $(which java)))

# Set JavaFX path (adjust to your OS)
# Linux (apt): /usr/share/java/
# Windows (SDK): C:/Program Files/Java/javafx-sdk-21/lib/
# macOS (Homebrew): /usr/local/opt/openjfx/libexec/lib/

JFXBASE="/usr/share/java"  # Change this for your OS
CP="$JFXBASE/javafx-base-11.jar:$JFXBASE/javafx-controls-11.jar:$JFXBASE/javafx-fxml-11.jar:$JFXBASE/javafx-graphics-11.jar:/usr/share/java/sqlite-jdbc.jar:/usr/share/java/jbcrypt.jar"

mkdir -p target/classes
find src/main/java -name "*.java" > /tmp/sources.txt
cp src/main/resources/styles.css target/classes/

$JAVA_HOME_BIN/javac -source 17 -target 17 -cp "$CP" -d target/classes @/tmp/sources.txt
echo "Build done. Run: java -cp target/classes:$CP com.fakkaflow.MainApp"
