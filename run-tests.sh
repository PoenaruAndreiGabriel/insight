#!/bin/bash

# Compile main code
javac -d out -cp "lib/*" src/main/java/com/yourapp/*.java

# Compile test code
javac -d out -cp "lib/*:out" src/test/java/com/yourapp/*.java

# Run tests
java -cp "lib/*:out" org.junit.runner.JUnitCore com.yourapp.MyGuiAppTest
