package org.example.service;

import org.example.dao.BeneficiarioDAO;
import org.example.dao.DoacaoDAO;
import org.example.dao.DoadorDAO;
import org.example.model.Beneficiario;
import org.example.model.Doacao;
import org.example.model.Doador;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import org.example.db.Database;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gerenciador {
    private static final Logger logger = LoggerFactory.getLogger(Gerenciador.class);
    private final DoadorDAO doadorDAO = new DoadorDAO();
    private final BeneficiarioDAO beneficiarioDAO = new BeneficiarioDAO();
    private final DoacaoDAO doacaoDAO = new DoacaoDAO();
    private final ServicoNotificacao notificador;

    private static final int LIMITE_ITENS_POR_DOADOR = 5;

    public Gerenciador(ServicoNotificacao notificador) {
        this.notificador = notificador;
    }

    public Doador cadastrarDoador(String nome, String contato) throws SQLException {
        validarNome(nome);
        validarContato(contato);
        Optional<Doador> existente = doadorDAO.buscarPorContato(contato);
        if (existente.isPresent()) {
            return existente.get();
        }
        Doador d = new Doador(null, nome.trim(), contato.trim());
        return doadorDAO.inserir(d);
    }

    public Optional<Doador> buscarDoadorPorId(int id) throws SQLException {
        return doadorDAO.buscarPorId(id);
    }

    public Optional<Beneficiario> buscarBeneficiarioPorId(int id) throws SQLException {
        return beneficiarioDAO.buscarPorId(id);
    }

    public Optional<Doacao> buscarDoacaoPorId(int id) throws SQLException {
        return doacaoDAO.buscarPorId(id);
    }

    public Beneficiario cadastrarBeneficiario(String nome, String necessidade) throws SQLException {
        validarNome(nome);
        validarNecessidade(necessidade);
        Beneficiario b = new Beneficiario();
        b.setNome(nome.trim());
        b.setNecessidade(necessidade.trim());
        return beneficiarioDAO.inserir(b);
    }

    public Doacao registrarDoacao(int doadorId, String item, int quantidade) throws SQLException {
        validarItem(item);
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        int totalAtual = doacaoDAO.somatorioItensPorDoador(doadorId);
        if (totalAtual + quantidade > LIMITE_ITENS_POR_DOADOR) {
            throw new IllegalArgumentException("Limite de 5 itens por doador excedido (atual: " + totalAtual + ")");
        }
        doadorDAO.buscarPorId(doadorId).orElseThrow(() -> new IllegalArgumentException("Doador inexistente"));

        Doacao d = new Doacao(null, doadorId, null, item.trim(), quantidade, Doacao.Status.PENDENTE);
        return doacaoDAO.inserir(d);
    }

    public List<Doador> listarDoadores() throws SQLException { return doadorDAO.listarTodosPorOrdemCadastro(); }
    public List<Beneficiario> listarBeneficiarios() throws SQLException { return beneficiarioDAO.listarTodosPorOrdemCadastro(); }
    public List<Doacao> listarDoacoes() throws SQLException { return doacaoDAO.listarTodas(); }
    public List<Doacao> listarDoacoesPorDoador(int doadorId) throws SQLException { return doacaoDAO.listarPorDoador(doadorId); }
    public List<Doacao> listarDoacoesPorStatus(Doacao.Status status) throws SQLException { return doacaoDAO.listarPorStatus(status); }

    public int distribuirDoacoes() throws SQLException {
        var beneficiarios = beneficiarioDAO.listarTodosPorOrdemCadastro();
        var pendentes = doacaoDAO.listarPendentes();

        logger.debug("Iniciando distribuicao: beneficiarios={}, pendentes={}", beneficiarios.size(), pendentes.size());
        if (beneficiarios.isEmpty() || pendentes.isEmpty()) {
            logger.debug("Nada para distribuir");
            return 0;
        }

        int distribuido = 0;
        int idxBen = 0;
        // collect notifications to send after commit
        var notificacoes = new ArrayList<String>();

        try (Connection conn = Database.getConexao()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);

                for (Doacao d : pendentes) {
                    if (idxBen >= beneficiarios.size()) break;
                    var ben = beneficiarios.get(idxBen);
                    d.setBeneficiarioId(ben.getId());
                    d.setStatus(Doacao.Status.DISTRIBUIDA);
                    // perform update using the same connection (transactional)
                    doacaoDAO.atualizarComConexao(conn, d);
                    logger.debug("Atribuida doacao {} -> beneficiario {}", d.getId(), ben.getId());
                    distribuido++;
                    idxBen++;
                    notificacoes.add("Doação #" + d.getId() + " (" + d.getItem() + ", x" + d.getQuantidade() + ") atribuída ao beneficiário " + ben.getNome());
                }

                conn.commit();
                logger.debug("Commit concluido, distribuido={}", distribuido);
            } catch (SQLException e) {
                try { conn.rollback(); } catch (Exception ignored) {}
                throw e;
            } finally {
                try { conn.setAutoCommit(originalAutoCommit); } catch (Exception ignored) {}
            }
        }

        // send notifications after successful commit
        if (notificador != null) {
            notificacoes.forEach(notificador::notificarAsync);
        }

        return distribuido;
    }

    public void concluirDoacao(int doacaoId) throws SQLException { doacaoDAO.atualizarStatus(doacaoId, Doacao.Status.DISTRIBUIDA); }

    public void cancelarDoacao(int doacaoId) throws SQLException { doacaoDAO.atualizarStatus(doacaoId, Doacao.Status.CANCELADA); }

    // --- Novas operações de edição/remoção ---
    public Doador editarDoador(int id, String nome, String contato) throws SQLException {
        validarNome(nome);
        validarContato(contato);
        Doador atual = doadorDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Doador não encontrado"));
        atual.setNome(nome.trim());
        atual.setContato(contato.trim());
        doadorDAO.atualizar(atual);
        return doadorDAO.buscarPorId(id).orElse(atual);
    }

    public void removerDoador(int id) throws SQLException {
        // impede remoção se houver doações associadas
        var doacoes = doacaoDAO.listarPorDoador(id);
        if (!doacoes.isEmpty()) throw new IllegalArgumentException("Existem doações associadas ao doador. Remova-as antes.");
        doadorDAO.remover(id);
    }

    public Beneficiario editarBeneficiario(int id, String nome, String necessidade) throws SQLException {
        validarNome(nome);
        validarNecessidade(necessidade);
        Beneficiario atual = beneficiarioDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Beneficiário não encontrado"));
        atual.setNome(nome.trim());
        atual.setNecessidade(necessidade.trim());
        beneficiarioDAO.atualizar(atual);
        return beneficiarioDAO.buscarPorId(id).orElse(atual);
    }

    public void removerBeneficiario(int id) throws SQLException {
        // impede remoção se houver doações atribuídas
        var todas = doacaoDAO.listarTodas();
        boolean existe = todas.stream().anyMatch(d -> d.getBeneficiarioId() != null && d.getBeneficiarioId() == id);
        if (existe) throw new IllegalArgumentException("Existem doações atribuídas ao beneficiário. Remova ou reatribua antes.");
        beneficiarioDAO.remover(id);
    }

    public Doacao editarDoacao(int id, String item, int quantidade, Integer beneficiarioId) throws SQLException {
        validarItem(item);
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        Doacao atual = doacaoDAO.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Doação não encontrada"));

        // valida limite por doador considerando alteração (apenas pendentes)
        if (atual.getStatus() == Doacao.Status.PENDENTE) {
            int totalAtual = doacaoDAO.somatorioItensPorDoador(atual.getDoadorId());
            int novoTotal = totalAtual - atual.getQuantidade() + quantidade;
            if (novoTotal > LIMITE_ITENS_POR_DOADOR) {
                throw new IllegalArgumentException("Limite de itens por doador excedido após alteração (atual: " + totalAtual + ")");
            }
        }

        atual.setItem(item.trim());
        atual.setQuantidade(quantidade);
        atual.setBeneficiarioId(beneficiarioId);
        doacaoDAO.atualizar(atual);
        return doacaoDAO.buscarPorId(id).orElse(atual);
    }

    public void removerDoacao(int id) throws SQLException {
        doacaoDAO.remover(id);
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome invalido");
    }

    private void validarContato(String contato) {
        if (contato == null || contato.isBlank()) throw new IllegalArgumentException("Contato invalido");
        String expressaoEmail = "^[^@\n\r\t ]+@[^@\n\r\t ]+\\.[^@\n\r\t ]+$";
        String expressaoTelefone = "^[+]?[0-9 ()-]{8,}$";
        if (!(contato.matches(expressaoEmail) || contato.matches(expressaoTelefone))) {
            throw new IllegalArgumentException("Contato deve ser e-mail ou telefone valido");
        }
    }

    private void validarItem(String item) {
        if (item == null || item.isBlank()) throw new IllegalArgumentException("Item invalido");
        if (item.trim().length() < 2) throw new IllegalArgumentException("Item muito curto");
    }

    private void validarNecessidade(String necessidade) {
        if (necessidade == null || necessidade.isBlank()) throw new IllegalArgumentException("Necessidade invalida");
        if (necessidade.trim().length() < 2) throw new IllegalArgumentException("Necessidade muito curta");
    }
}

