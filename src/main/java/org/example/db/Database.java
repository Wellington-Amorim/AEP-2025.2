package org.example.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {
    private static final String URL_PADRAO = "jdbc:mysql://localhost:3306/doacoes?createDatabaseIfNotExist=true&serverTimezone=UTC";
    private static final String USUARIO_PADRAO = "root";
    private static final String SENHA_PADRAO = "";

    private static HikariDataSource fonteDados;
    private static boolean schemaAplicado = false;

    private static synchronized void garantirDataSource() throws SQLException {
        if (fonteDados != null && !fonteDados.isClosed()) return;

        Properties propriedades = carregarPropriedadesAplicacao();

        String url = primeiroNaoVazio(
                System.getProperty("db.url"),
                System.getenv("DB_URL"),
                propriedades.getProperty("db.url"),
                URL_PADRAO
        );
        String usuario = primeiroNaoVazio(
                System.getProperty("db.username"),
                System.getenv("DB_USER"),
                propriedades.getProperty("db.username"),
                USUARIO_PADRAO
        );
        String senha = primeiroNaoNulo(
                System.getProperty("db.password"),
                System.getenv("DB_PASSWORD"),
                propriedades.getProperty("db.password"),
                SENHA_PADRAO
        );

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(usuario);
            config.setPassword(senha);
            // allow override of driver class via properties for testing (e.g. H2)
            String driver = primeiroNaoVazio(
                    propriedades.getProperty("db.driver"),
                    System.getProperty("db.driver"),
                    System.getenv("DB_DRIVER")
            );
            if (driver != null && !driver.isBlank()) {
                config.setDriverClassName(driver);
            } else {
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            }

            // Pool defaults (overridable via application.properties if desired)
            definirSePresenteInteiro(config::setMaximumPoolSize, propriedades.getProperty("db.pool.maximumPoolSize"), 10);
            definirSePresenteInteiro(config::setMinimumIdle, propriedades.getProperty("db.pool.minimumIdle"), 5);
            definirSePresenteLongo(config::setIdleTimeout, propriedades.getProperty("db.pool.idleTimeout"), 300_000L);
            definirSePresenteLongo(config::setMaxLifetime, propriedades.getProperty("db.pool.maxLifetime"), 600_000L);
            definirSePresenteLongo(config::setConnectionTimeout, propriedades.getProperty("db.pool.connectionTimeout"), 30_000L);
            config.setAutoCommit(true);

            fonteDados = new HikariDataSource(config);

            if (!schemaAplicado) {
                try (Connection conn = fonteDados.getConnection()) {
                    aplicarSchemaDoRecurso(conn, url);
                    initSchema(conn);
                    schemaAplicado = true;
                }
            }
        } catch (RuntimeException e) {
            throw new SQLException("Falha ao inicializar pool de conexões: " + e.getMessage(), e);
        }
    }

    public static Connection getConexao() throws SQLException {
        garantirDataSource();
        try {
            return fonteDados.getConnection();
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão do pool: " + e.getMessage());
            throw e;
        }
    }

    public static void encerrar() {
        try {
            if (fonteDados != null && !fonteDados.isClosed()) {
                fonteDados.close();
            }
        } catch (Exception ignored) {
        }
    }

    private static void initSchema(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
        st.executeUpdate("CREATE TABLE IF NOT EXISTS doadores (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "nome VARCHAR(100) NOT NULL," +
            "contato VARCHAR(120) NOT NULL UNIQUE," +
            "ativo BOOLEAN NOT NULL DEFAULT TRUE," +
            "data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
            ")");

        st.executeUpdate("CREATE TABLE IF NOT EXISTS beneficiarios (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "nome VARCHAR(100) NOT NULL," +
            "necessidade VARCHAR(200) NOT NULL," +
            "ativo BOOLEAN NOT NULL DEFAULT TRUE," +
            "data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
            ")");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS doacoes (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "doador_id INT NOT NULL," +
                    "beneficiario_id INT NULL," +
                    "item VARCHAR(120) NOT NULL," +
                    "quantidade INT NOT NULL," +
                    "status VARCHAR(20) NOT NULL," +
                    "data_doacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "data_distribuicao TIMESTAMP NULL," +
                    "FOREIGN KEY (doador_id) REFERENCES doadores(id)," +
                    "FOREIGN KEY (beneficiario_id) REFERENCES beneficiarios(id)" +
                    ")");
        }
    }

    private static void aplicarSchemaDoRecurso(Connection conn, String jdbcUrl) {
        String dialect = detectarDialect(jdbcUrl);
        String[] candidatos = new String[] {
                dialect != null ? "schema-" + dialect + ".sql" : null,
                "schema.sql"
        };

        for (String candidato : candidatos) {
            if (candidato == null) continue;
            try (InputStream is = Database.class.getClassLoader().getResourceAsStream(candidato)) {
                if (is == null) continue;
                String sql = new String(is.readAllBytes());
                // remover BOM se presente
                if (sql.startsWith("\uFEFF")) sql = sql.substring(1);
                // dividir por ';' de forma simples e robusta
                String[] blocos = sql.split(";");
                List<String> pendingFks = new ArrayList<>();
                try (Statement st = conn.createStatement()) {
                    for (String bloco : blocos) {
                        String cmd = bloco.trim();
                        if (cmd.isEmpty()) continue;
                        // pular comentários que iniciam a linha
                        String primeiro = cmd.lines().findFirst().orElse("").trim();
                        if (primeiro.startsWith("--") || primeiro.startsWith("/*")) continue;
                        if (cmd.toLowerCase().contains("foreign key")) {
                            pendingFks.add(cmd);
                            continue;
                        }
                        try {
                            st.execute(cmd);
                        } catch (Exception e) {
                            System.err.println("Falha ao executar comando de esquema: " + e.getMessage());
                        }
                    }

                    // tentar executar constraints/foreign keys no final
                    for (String fkCmd : pendingFks) {
                        try {
                            st.execute(fkCmd);
                        } catch (Exception e) {
                            System.err.println("Falha ao aplicar constraint/foreign key: " + e.getMessage());
                        }
                    }
                }
                System.out.println("Aplicado esquema a partir do recurso: " + candidato);
                return; // aplicado um arquivo de esquema, não aplicar outro
            } catch (Exception e) {
                System.err.println("Falha ao aplicar recurso de esquema '" + candidato + "': " + e.getMessage());
                // tentar próximo candidato
            }
        }
        // nenhum schema resource encontrado/aplicado; continuar com fallback programático
    }

    private static String detectarDialect(String jdbcUrl) {
        if (jdbcUrl == null) return null;
        String u = jdbcUrl.toLowerCase();
        if (u.startsWith("jdbc:h2:")) return "h2";
        if (u.startsWith("jdbc:mysql:") || u.contains("mysql")) return "mysql";
        if (u.startsWith("jdbc:postgresql:") || u.contains("postgres")) return "postgresql";
        return null;
    }

    private static Properties carregarPropriedadesAplicacao() {
        Properties props = new Properties();
        try (InputStream is = Database.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) props.load(is);
        } catch (Exception ignored) {
        }
        return props;
    }

    private static String primeiroNaoVazio(String... valores) {
        for (String v : valores) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static String primeiroNaoNulo(String... valores) {
        for (String v : valores) {
            if (v != null) return v;
        }
        return null;
    }

    private interface DefinidorInt { void set(int v); }
    private interface DefinidorLongo { void set(long v); }

    private static void definirSePresenteInteiro(DefinidorInt definidor, String valor, int padrao) {
        try {
            definidor.set(valor != null ? Integer.parseInt(valor) : padrao);
        } catch (NumberFormatException ignored) {
            definidor.set(padrao);
        }
    }

    private static void definirSePresenteLongo(DefinidorLongo definidor, String valor, long padrao) {
        try {
            definidor.set(valor != null ? Long.parseLong(valor) : padrao);
        } catch (NumberFormatException ignored) {
            definidor.set(padrao);
        }
    }
}
