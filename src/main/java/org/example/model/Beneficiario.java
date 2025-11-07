package org.example.model;

import java.time.LocalDateTime;

public class Beneficiario extends Pessoa {
    private String necessidade;
    private LocalDateTime dataCadastro;

    public Beneficiario() {}

    public Beneficiario(Integer id, String nome, String necessidade, LocalDateTime dataCadastro) {
        super(id, nome);
        this.necessidade = necessidade;
        this.dataCadastro = dataCadastro;
    }

    public String getNecessidade() { return necessidade; }
    public void setNecessidade(String necessidade) { this.necessidade = necessidade; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public String toString() {
        return "Beneficiario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", necessidade='" + necessidade + '\'' +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}

