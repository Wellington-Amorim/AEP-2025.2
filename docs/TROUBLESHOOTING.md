# Dicas de Configuração e Troubleshooting

- Precedência de configuração do banco (da maior para a menor):
  - Propriedades da JVM: `-Ddb.url`, `-Ddb.username`, `-Ddb.password`
  - Variáveis de ambiente: `DB_URL`, `DB_USER`, `DB_PASSWORD`
  - Arquivo: `src/main/resources/application.properties`
  - Padrões: URL local, usuário `root`, senha vazia
- Docker Compose: a senha padrão configurada para `root` é `rootpass`. Execute com `DB_PASSWORD=rootpass` ou `-Ddb.password=rootpass` quando usar o container MySQL.
- Acentuação: scripts de execução forçam `UTF-8` via `-Dfile.encoding=UTF-8` e o `logback.xml` também usa `UTF-8`.
- Erro comum: `Access denied for user 'root'@'localhost' (using password: YES)` indica credenciais incorretas. Ajuste `DB_USER`/`DB_PASSWORD` ou crie um usuário válido.
