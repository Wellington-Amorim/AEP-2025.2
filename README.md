# Sistema de Gerenciamento de Doações

[![CI](https://github.com/Wellington-Amorim/AEP-2025.2/actions/workflows/maven.yml/badge.svg)](https://github.com/Wellington-Amorim/AEP-2025.2/actions/workflows/maven.yml)

Aplicativo de console para gerenciar doações de materiais educacionais, conectando doadores e beneficiários. Interface organizada em submenus, persistência via MySQL/H2 (JDBC), pool de conexões com HikariCP, notificações assíncronas e validações de integridade.

## Funcionalidades

- Interface em submenus para fácil navegação:
  - Doadores (cadastro, edição, remoção segura)
  - Beneficiários (cadastro, edição, remoção segura)
  - Doações (registro, distribuição, conclusão, cancelamento)
- Proteções de integridade:
  - Soft-delete para doadores/beneficiários
  - Validação de referências antes de remoções
  - Transações ACID para distribuição
  - Confirmações de operações destrutivas
- Logs e notificações:
  - Console colorido e formatado
  - Logging via SLF4J/Logback
  - Notificações assíncronas de distribuição
- Testes automatizados (JUnit 5):
  - Testes de integração com H2
  - Validação de transações
  - Verificação de integridade

## Pré‑requisitos

- Java 21 (LTS)
- Maven 3.8+
- MySQL 8.0+ (local ou via Docker)

## Instalação

1. Clone o repositório:
   - `git clone https://github.com/Wellington-Amorim/AEP-2025.2.git`
2. Opcional: crie o banco manualmente (se não usar `createDatabaseIfNotExist=true`):
   - `CREATE DATABASE doacoes;`
3. (Opcional) Ajuste `src/main/resources/application.properties` a seu ambiente.

## Como Rodar

### Desenvolvimento
```bash
# Compilar e executar via Maven
mvn compile exec:java -Dexec.mainClass=org.example.Main

# Rodar testes (usa H2 em memória)
mvn test

# Compilar JAR com dependências
mvn clean package
```

### Execução do JAR
```bash
# Básico (MySQL local sem senha)
java -jar target/AEP-2025.2-1.0-SNAPSHOT-shaded.jar

# MySQL local com senha
java -Ddb.password=senha -jar target/*-shaded.jar

# Configuração completa
java -Ddb.url="jdbc:mysql://host:3306/doacoes" \
     -Ddb.username=user \
     -Ddb.password=pass \
     -jar target/*-shaded.jar
```

### Scripts
Windows (cmd):
```cmd
run.bat                      # compila e executa
```

Windows (PowerShell):
```powershell
./run.ps1                   # usa defaults
./run.ps1 -DbPassword pass  # configura conexão
```

Linux/macOS:
```bash
./run.sh                    # usa defaults
DB_PASSWORD=pass ./run.sh   # com env vars
```

Os scripts incluem `-Dfile.encoding=UTF-8` para acentuação correta.

## Configuração da Conexão

Ordem de precedência (da maior para a menor):

1) Propriedades da JVM: `-Ddb.url`, `-Ddb.username`, `-Ddb.password`
2) Variáveis de ambiente: `DB_URL`, `DB_USER`, `DB_PASSWORD`
3) Arquivo: `src/main/resources/application.properties`
4) Padrões: URL local, usuário `root`, senha vazia

Exemplos rápidos:

- Sem senha (MySQL local):
  - `./run.sh`
  - `./run.ps1 -DbUser root -DbPassword ""`
- Com senha (Docker Compose abaixo):
  - `DB_PASSWORD=rootpass ./run.sh`
  - `java -Ddb.password=rootpass -jar target/*-shaded.jar`

## Banco de Dados

### Configuração

O sistema usa MySQL por padrão mas também suporta H2 em memória para testes.

A ordem de precedência para configurações (da maior para a menor):

1) Propriedades JVM: `-Ddb.url`, `-Ddb.username`, `-Ddb.password`
2) Ambiente: `DB_URL`, `DB_USER`, `DB_PASSWORD`
3) Arquivo: `src/main/resources/application.properties`
4) Padrões: URL local, usuário `root`, senha vazia

### Desenvolvimento com Docker

Suba o MySQL localmente sem instalar:

```bash
docker compose up -d
# aguarde até o container estar saudável
```

Padrões do `docker-compose.yml`:

- Banco: `doacoes`
- Usuário root com senha: `rootpass`
- Porta: `3306`

Execute com: `DB_PASSWORD=rootpass ./run.sh`

### Esquema do Banco

No startup, a aplicação procura por scripts SQL na pasta `src/main/resources` e os aplica por ordem de preferência:

1. `schema-{dialect}.sql` — arquivo específico do dialect detectado pela URL JDBC (ex.: `schema-h2.sql`, `schema-mysql.sql`).
2. `schema.sql` — fallback genérico (aplicado se não houver arquivo específico de dialect).
3. Se não houver arquivo aplicável, a aplicação garante uma estrutura mínima via fallback programático.

Observações:
- Os arquivos `schema-*.sql` permitem que você mantenha DDL otimizada para cada banco (MySQL, H2, PostgreSQL, etc.).
- Para forçar que a aplicação use o fallback programático, remova/renomeie os arquivos `schema*.sql` do classpath antes do startup.
- Os arquivos de esquema do repositório foram reorganizados para criar tabelas primeiro e aplicar `FOREIGN KEY` via `ALTER TABLE` no final — isso evita mensagens de ordering/constraint durante a criação em bancos in-memory como H2.

## Problemas Comuns (Troubleshooting)

- “Access denied for user 'root'@'localhost' (using password: YES)”
  - Credenciais incorretas. Ajuste `DB_USER`/`DB_PASSWORD` ou use as do Docker (`root`/`rootpass`).
- Acentuação estranha no console
  - Scripts já usam `-Dfile.encoding=UTF-8`. Prefira Windows Terminal/PowerShell (ou `chcp 65001` no cmd) e uma fonte com suporte UTF‑8.
- Mais dicas
  - Veja `docs/TROUBLESHOOTING.md`.

## Scripts

- `run.bat` (Windows cmd): constrói se necessário e executa o JAR.
- `run.ps1` (Windows PowerShell): equivalente ao `.bat`, com parâmetros.
- `run.sh` (Linux/macOS): usa variáveis de ambiente.

## Interface e Operações

### Menu Principal

O console foi reorganizado em submenus para melhor navegação:

1. **Doadores**
   - Cadastrar novo doador
   - Editar dados existentes
   - Listar todos os doadores
   - Remover doador (com proteções)

2. **Beneficiários**
   - Cadastrar novo beneficiário
   - Editar dados existentes
   - Listar beneficiários
   - Remover beneficiário (com proteções)

3. **Doações**
   - Registrar nova doação
   - Editar detalhes
   - Listar doações
   - Gerenciar status:
     - Distribuir
     - Concluir
     - Cancelar

### Sistema de Remoção Segura

O sistema implementa proteções contra remoções acidentais:

1. **Soft Delete**
   - Registros marcados como inativos (coluna `ativo`)
   - Preserva histórico e integridade referencial
   - Permite "restaurar" registros se necessário

2. **Validações de Integridade** 
   - Bloqueio de remoção com dependências
   - Ex: doador com doações ativas não pode ser removido
   - Mensagens claras sobre ações necessárias

3. **Confirmações**
   - Operações destrutivas requerem confirmação
   - Feedback claro sobre consequências
   - Chance de cancelar operações sensíveis

## Testes Automatizados

O projeto inclui testes de integração usando JUnit 5 e H2 em memória.

### Cobertura

Os testes validam fluxos essenciais:

1. **Operações Básicas**
   - Cadastro de entidades
   - Listagem e filtros
   - Edição de registros
   - Remoção com validações

2. **Transações**
   - Distribuição de doações
   - Rollback em caso de erro
   - Validação de consistência

3. **Integridade**
   - Soft-delete de registros
   - Proteção contra remoções
   - Validação de referências

### Execução

Os testes usam H2 em memória (não requer MySQL):

```bash
mvn test                    # roda todos os testes
mvn test -Dtest=Class      # roda classe específica
```

## Licença

- MIT — veja `LICENSE.md`
