Library Management System - Java 21 Upgrade

This project has been updated to target Java 21 (LTS).

Requirements

- JDK 21 installed and JAVA_HOME set
- Maven 3.8+

Install JDK 21 on Windows (example using Adoptium/Eclipse Temurin):

1. Download and install Temurin JDK 21 from https://adoptium.net
2. Set JAVA_HOME and add to PATH in PowerShell (run as Administrator):

$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21'
[Environment]::SetEnvironmentVariable('JAVA_HOME', $env:JAVA_HOME, 'Machine')
$env:Path = $env:JAVA_HOME + '\\bin;' + $env:Path
[Environment]::SetEnvironmentVariable('Path', $env:Path, 'Machine')

Verify:

java -version
mvn -v

Build and run tests

mvn -U -DskipTests=false test

Notes

- The project uses the Maven Compiler plugin with <release>21 and the Maven Enforcer plugin to require Java 21.
- If you don't have the Copilot upgrade tooling available, the upgrade was performed manually by updating build configuration and adding enforcement rules.
