package org.example.model;

public class Doacao {
    public enum Status { PENDENTE, DISTRIBUIDA, CANCELADA }

    private Integer id;
    private Integer doadorId;
    private Integer beneficiarioId;
    private String item;
    private int quantidade;
    private Status status;

    public Doacao() {}

    public Doacao(Integer id, Integer doadorId, Integer beneficiarioId, String item, int quantidade, Status status) {
        this.id = id;
        this.doadorId = doadorId;
        this.beneficiarioId = beneficiarioId;
        this.item = item;
        this.quantidade = quantidade;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getDoadorId() { return doadorId; }
    public void setDoadorId(Integer doadorId) { this.doadorId = doadorId; }
    public Integer getBeneficiarioId() { return beneficiarioId; }
    public void setBeneficiarioId(Integer beneficiarioId) { this.beneficiarioId = beneficiarioId; }
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return "Doacao{" +
                "id=" + id +
                ", doadorId=" + doadorId +
                ", beneficiarioId=" + beneficiarioId +
                ", item='" + item + '\'' +
                ", quantidade=" + quantidade +
                ", status=" + status +
                '}';
    }
}


