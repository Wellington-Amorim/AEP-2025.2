# Scripts de execução

Este documento descreve os scripts que ajudam a executar a aplicação localmente em diferentes ambientes.

run.bat (Windows - cmd):
- Local: `run.bat`
- Comportamento: verifica se `target/*-shaded.jar` existe; se não, executa `mvn -DskipTests package` e, em seguida, roda `java -jar` passando as propriedades `db.url`, `db.username`, `db.password`.
- Editar as variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD` no início do arquivo para alterar as credenciais padrão.

run.ps1 (Windows - PowerShell):
- Local: `run.ps1`
- Uso: `.
un.ps1 -DbUrl "jdbc:..." -DbUser root -DbPassword secret`
- Comportamento: mesmo do `run.bat` mas com parâmetros e saída verbosa.

run.sh (Linux/macOS):
- Local: `run.sh`
- Uso: `./run.sh` ou `DB_URL=... DB_USER=... ./run.sh`
- Requisitos: `mvn` e `java` instalados no PATH.

Variáveis de ambiente suportadas:
- `DB_URL` (ex.: `jdbc:mysql://localhost:3306/doacoes?createDatabaseIfNotExist=true&serverTimezone=UTC`)
- `DB_USER`
- `DB_PASSWORD`

Dicas:
- Para desenvolvimento local, suba o MySQL via `docker-compose up -d`.
- Se quiser depurar, prefira executar pela sua IDE com a classe `org.example.Main` como entrada.
