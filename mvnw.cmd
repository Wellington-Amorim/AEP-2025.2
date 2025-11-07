@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, compatible with v3.2.0
@REM ----------------------------------------------------------------------------

@IF "%DEBUG%"=="" @ECHO OFF
@SETLOCAL

SET MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

SET EXEC_DIR=%CD%
SET WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%\mvnw.cmd" SET MAVEN_PROJECTBASEDIR=%WDIR%& goto endDetectBaseDir
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
SET WDIR=%CD%
goto findBaseDir

:baseDirNotFound
SET MAVEN_PROJECTBASEDIR=%EXEC_DIR%

:endDetectBaseDir
@REM Proceed even if properties check fails; handle later

SET WRAPPER_PROPERTIES="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"

IF EXIST "%WRAPPER_PROPERTIES%" (
  FOR /F "usebackq tokens=1,* delims==" %%A in ("%WRAPPER_PROPERTIES%") DO (
    IF /I "%%A"=="distributionUrl" SET DIST_URL=%%B
  )
)
IF "%DIST_URL%"=="" (
  SET DIST_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.8/apache-maven-3.9.8-bin.zip
)

SET WRAPPER_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper
SET DIST_ZIP=%WRAPPER_DIR%\maven-dist.zip

@REM Download Maven distribution if not cached
IF NOT EXIST "%DIST_ZIP%" (
  ECHO Downloading Maven from %DIST_URL%
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;" ^
    ";Invoke-WebRequest -UseBasicParsing -Uri '%DIST_URL%' -OutFile '%DIST_ZIP%'"
)
IF NOT EXIST "%DIST_ZIP%" (
  ECHO Failed to download Maven distribution.
  EXIT /B 1
)

@REM Determine Maven home folder name from zip filename
FOR %%F IN ("%DIST_ZIP%") DO SET ZIPNAME=%%~nF
SET MAVEN_HOME=%WRAPPER_DIR%\%ZIPNAME:~0,-4%

IF NOT EXIST "%MAVEN_HOME%" (
  ECHO Extracting Maven to %MAVEN_HOME%
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "Expand-Archive -LiteralPath '%DIST_ZIP%' -DestinationPath '%WRAPPER_DIR%' -Force"
)

SET MVN_CMD="%MAVEN_HOME%\bin\mvn.cmd"
IF NOT EXIST %MVN_CMD% (
  @REM Fallback: try common directory name from URL
  SET MVN_CMD="%WRAPPER_DIR%\apache-maven-3.9.8\bin\mvn.cmd"
)
IF NOT EXIST %MVN_CMD% (
  ECHO mvn.cmd not found in %MAVEN_HOME%
  EXIT /B 1
)

CALL %MVN_CMD% %*
EXIT /B %ERRORLEVEL%
