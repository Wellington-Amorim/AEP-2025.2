@echo off
REM run.bat - constrói (se necessário) e executa o JAR shaded no Windows

:: Configurações padrão (edite se necessário)
:: Note: o caractere & é especial no cmd, por isso usamos ^& para escapar
set DB_URL=jdbc:mysql://localhost:3306/doacoes?createDatabaseIfNotExist=true^&serverTimezone=UTC
set DB_USER=root
set DB_PASSWORD=

:: Caminho do JAR shaded esperado
set JAR=target\AEP-2025.2-1.0-SNAPSHOT-shaded.jar

:checkJar
if not exist "%JAR%" (
    echo JAR nao encontrado: %JAR%
    echo Irei construir o projeto com Maven (pode demorar)...
    mvn -q -DskipTests package
    if errorlevel 1 (
        echo Erro ao construir o projeto. Verifique o Maven output.
        pause
        exit /b 1
    )
) else (
    echo JAR encontrado: %JAR%
)

:: Executa a aplicação com as variáveis de ambiente configuradas
echo Iniciando aplicacao...
set "JAVA_CMD=java -Dfile.encoding=UTF-8 -jar %JAR%"

:: Passa as variáveis de ambiente para a JVM via -D (opcional)
%JAVA_CMD% -Ddb.url="%DB_URL%" -Ddb.username="%DB_USER%" -Ddb.password="%DB_PASSWORD%"

if errorlevel 1 (
    echo Aplicacao terminou com erro.
) else (
    echo Aplicacao finalizada.
)
pause
