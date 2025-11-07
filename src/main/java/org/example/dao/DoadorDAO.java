package org.example.dao;

import org.example.db.Database;
import org.example.model.Doador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoadorDAO {
    public Doador inserir(Doador d) throws SQLException {
        String sql = "INSERT INTO doadores (nome, contato, ativo) VALUES (?, ?, TRUE)";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNome());
            ps.setString(2, d.getContato());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) d.setId(rs.getInt(1));
            }
            return d;
        }
    }

    public Optional<Doador> buscarPorContato(String contato) throws SQLException {
    String sql = "SELECT id, nome, contato FROM doadores WHERE contato = ? AND ativo=TRUE";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, contato);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Doador(rs.getInt("id"), rs.getString("nome"), rs.getString("contato")));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Doador> buscarPorId(int id) throws SQLException {
    String sql = "SELECT id, nome, contato FROM doadores WHERE id = ? AND ativo=TRUE";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Doador(rs.getInt("id"), rs.getString("nome"), rs.getString("contato")));
                }
            }
        }
        return Optional.empty();
    }

    public List<Doador> listarTodosPorOrdemCadastro() throws SQLException {
        List<Doador> lista = new ArrayList<>();
    String sql = "SELECT id, nome, contato FROM doadores WHERE ativo=TRUE ORDER BY id";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Doador(rs.getInt("id"), rs.getString("nome"), rs.getString("contato")));
            }
        }
        return lista;
    }

    public void atualizar(Doador d) throws SQLException {
        String sql = "UPDATE doadores SET nome=?, contato=? WHERE id=?";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, d.getNome());
            ps.setString(2, d.getContato());
            ps.setInt(3, d.getId());
            ps.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
    String sql = "UPDATE doadores SET ativo=FALSE WHERE id=?";
        try (Connection c = Database.getConexao();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
