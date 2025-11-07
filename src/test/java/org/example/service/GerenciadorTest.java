package org.example.service;

import org.example.model.Beneficiario;
import org.example.model.Doacao;
import org.example.model.Doador;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciadorTest {
    private static Gerenciador gerenciador;

    @BeforeAll
    public static void setup() throws SQLException {
        // configure DB for tests (H2 in-memory)
        System.setProperty("db.url", "jdbc:h2:mem:doacoes;DB_CLOSE_DELAY=-1");
        System.setProperty("db.username", "sa");
        System.setProperty("db.password", "");
        System.setProperty("db.driver", "org.h2.Driver");

        // create Gerenciador without notificador for tests
        gerenciador = new Gerenciador(null);
    }

    @BeforeEach
    public void limparBanco() throws SQLException {
        // limpa tabelas entre testes para garantir isolamento
        try (var conn = org.example.db.Database.getConexao();
             var st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM doacoes");
            st.executeUpdate("DELETE FROM beneficiarios");
            st.executeUpdate("DELETE FROM doadores");
        }
    }

    @AfterAll
    public static void teardown() {
        // nothing special; Database pool will be closed on JVM exit
    }

    @Test
    public void testCadastroEListaBasico() throws SQLException {
        Doador d = gerenciador.cadastrarDoador("Test Doador", "+5511999999999");
        assertNotNull(d.getId());
        Beneficiario b = gerenciador.cadastrarBeneficiario("Test Ben", "Alimentos");
        assertNotNull(b.getId());
        Doacao doacao = gerenciador.registrarDoacao(d.getId(), "Arroz", 2);
        assertNotNull(doacao.getId());

        assertTrue(gerenciador.listarDoadores().stream().anyMatch(x -> x.getId().equals(d.getId())));
        assertTrue(gerenciador.listarBeneficiarios().stream().anyMatch(x -> x.getId().equals(b.getId())));
        assertTrue(gerenciador.listarDoacoes().stream().anyMatch(x -> x.getId().equals(doacao.getId())));
    }

    @Test
    public void testDistribuicaoTransacional() throws SQLException {
        Doador d = gerenciador.cadastrarDoador("D2", "d2@example.com");
        Beneficiario b = gerenciador.cadastrarBeneficiario("Ben2", "Roupas");
        Doacao doc = gerenciador.registrarDoacao(d.getId(), "Camiseta", 1);

        // valida beneficiário e doação antes da distribuição
        assertNotNull(b.getId(), "Beneficiário deve ter ID");
        assertEquals(Doacao.Status.PENDENTE, doc.getStatus(), "Doação deve iniciar como pendente");
        
        int distribuidas = gerenciador.distribuirDoacoes();
        assertTrue(distribuidas >= 1);

        var lista = gerenciador.listarDoacoesPorDoador(d.getId());
        assertTrue(lista.stream().anyMatch(x -> x.getId().equals(doc.getId()) && 
            x.getStatus() == Doacao.Status.DISTRIBUIDA), "Doação específica deve estar distribuída");
        
        var doacaoAtualizada = gerenciador.buscarDoacaoPorId(doc.getId()).orElseThrow();
        assertEquals(b.getId(), doacaoAtualizada.getBeneficiarioId(), "Beneficiário deve estar atribuído");
    }

    @Test
    public void testRemocaoSegura() throws SQLException {
        Doador d = gerenciador.cadastrarDoador("D3", "d3@example.com");
        Doacao doc = gerenciador.registrarDoacao(d.getId(), "Arroz", 1);
        
        // verifica se a doação foi registrada
        assertNotNull(doc.getId(), "Doação deve ter ID");
        assertTrue(gerenciador.listarDoacoesPorDoador(d.getId()).stream()
            .anyMatch(x -> x.getId().equals(doc.getId())), "Doação deve estar na lista do doador");
        
        // remover doador com doações deve lançar exceção
        var ex = assertThrows(IllegalArgumentException.class, 
            () -> gerenciador.removerDoador(d.getId()));
        assertTrue(ex.getMessage().contains("doações associadas"), 
            "Mensagem de erro deve indicar doações associadas");
    }

    @Test
    public void testSoftDeleteBeneficiario() throws SQLException {
        Beneficiario b = gerenciador.cadastrarBeneficiario("Ben3", "Material escolar");
        assertTrue(gerenciador.buscarBeneficiarioPorId(b.getId()).isPresent());
        // remover sem doações associadas
        gerenciador.removerBeneficiario(b.getId());
        assertTrue(gerenciador.buscarBeneficiarioPorId(b.getId()).isEmpty());
    }
}
