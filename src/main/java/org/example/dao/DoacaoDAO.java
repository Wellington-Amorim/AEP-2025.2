package org.example.dao;

import org.example.db.Database;
import org.example.model.Doacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoacaoDAO {
    public Doacao inserir(Doacao d) throws SQLException {
        String sql = "INSERT INTO doacoes (doador_id, beneficiario_id, item, quantidade, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getDoadorId());
            if (d.getBeneficiarioId() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, d.getBeneficiarioId());
            ps.setString(3, d.getItem());
            ps.setInt(4, d.getQuantidade());
            ps.setString(5, d.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) d.setId(rs.getInt(1));
            }
            return d;
        }
    }

    public void atualizar(Doacao d) throws SQLException {
        String sql = "UPDATE doacoes SET doador_id=?, beneficiario_id=?, item=?, quantidade=?, status=? WHERE id=?";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, d.getDoadorId());
            if (d.getBeneficiarioId() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, d.getBeneficiarioId());
            ps.setString(3, d.getItem());
            ps.setInt(4, d.getQuantidade());
            ps.setString(5, d.getStatus().name());
            ps.setInt(6, d.getId());
            ps.executeUpdate();
        }
    }

    public void atualizarStatus(int doacaoId, Doacao.Status status) throws SQLException {
        String sql = "UPDATE doacoes SET status=? WHERE id=?";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, doacaoId);
            ps.executeUpdate();
        }
    }

    // Transactional update using an existing connection (does not commit/rollback)
    public void atualizarComConexao(Connection c, Doacao d) throws SQLException {
        String sql = "UPDATE doacoes SET doador_id=?, beneficiario_id=?, item=?, quantidade=?, status=? WHERE id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, d.getDoadorId());
            if (d.getBeneficiarioId() == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, d.getBeneficiarioId());
            ps.setString(3, d.getItem());
            ps.setInt(4, d.getQuantidade());
            ps.setString(5, d.getStatus().name());
            ps.setInt(6, d.getId());
            ps.executeUpdate();
        }
    }

    public void atualizarStatusComConexao(Connection c, int doacaoId, Doacao.Status status) throws SQLException {
        String sql = "UPDATE doacoes SET status=? WHERE id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, doacaoId);
            ps.executeUpdate();
        }
    }

    public List<Doacao> listarTodas() throws SQLException {
        List<Doacao> lista = new ArrayList<>();
        String sql = "SELECT id, doador_id, beneficiario_id, item, quantidade, status FROM doacoes ORDER BY id";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Optional<Doacao> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, doador_id, beneficiario_id, item, quantidade, status FROM doacoes WHERE id = ?";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM doacoes WHERE id = ?";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Doacao> listarPendentes() throws SQLException {
        List<Doacao> lista = new ArrayList<>();
        String sql = "SELECT id, doador_id, beneficiario_id, item, quantidade, status FROM doacoes WHERE status='PENDENTE' ORDER BY id";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Doacao> listarPorDoador(int doadorId) throws SQLException {
        List<Doacao> lista = new ArrayList<>();
        String sql = "SELECT id, doador_id, beneficiario_id, item, quantidade, status FROM doacoes WHERE doador_id=? ORDER BY id";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doadorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Doacao> listarPorStatus(Doacao.Status status) throws SQLException {
        List<Doacao> lista = new ArrayList<>();
        String sql = "SELECT id, doador_id, beneficiario_id, item, quantidade, status FROM doacoes WHERE status=? ORDER BY id";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public int somatorioItensPorDoador(int doadorId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantidade),0) AS total FROM doacoes WHERE doador_id=? AND status='PENDENTE'";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doadorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }

    private Doacao mapear(ResultSet rs) throws SQLException {
        Doacao d = new Doacao();
        d.setId(rs.getInt("id"));
        d.setDoadorId(rs.getInt("doador_id"));
        int ben = rs.getInt("beneficiario_id");
        d.setBeneficiarioId(rs.wasNull() ? null : ben);
        d.setItem(rs.getString("item"));
        d.setQuantidade(rs.getInt("quantidade"));
        d.setStatus(Doacao.Status.valueOf(rs.getString("status")));
        return d;
    }
}
