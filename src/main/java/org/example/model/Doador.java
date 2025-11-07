package org.example.model;

public class Doador extends Pessoa {
    private String contato;

    public Doador() {}

    public Doador(Integer id, String nome, String contato) {
        super(id, nome);
        this.contato = contato;
    }

    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }

    @Override
    public String toString() {
        return "Doador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", contato='" + contato + '\'' +
                '}';
    }
}

