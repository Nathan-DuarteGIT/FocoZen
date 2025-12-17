package com.example.focozen.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Representa uma tarefa na base de dados.
 * Esta classe é a Entity do Room.
 */
@Entity(tableName = "tarefas")
public class Tarefa {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titulo;
    private String descricao;
    private int prioridade; // 1=Baixa, 2=Média, 3=Alta
    private long dataVencimento; // Armazenado como Timestamp (milissegundos)
    private boolean concluida;

    // Construtor
    public Tarefa(String titulo, String descricao, int prioridade, long dataVencimento, boolean concluida) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.dataVencimento = dataVencimento;
        this.concluida = concluida;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    // O Room precisa de um setter para a chave primária, mesmo que seja auto-gerada
    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public long getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(long dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }
}
