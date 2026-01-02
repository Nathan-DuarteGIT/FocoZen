package com.example.focozen.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.focozen.model.Tarefa;

import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Tarefa.
 * Define os métodos para operações CRUD na base de dados.
 */
@Dao

public interface TarefaDao {
    @Insert
    void insert(Tarefa tarefa);

    @Update
    void update(Tarefa tarefa);

    @Delete
    void delete(Tarefa tarefa);

    /**
     * Obtém todas as tarefas, ordenadas por Prioridade (descendente) e Data de Vencimento (ascendente).
     * O LiveData permite que a UI observe as mudanças na base de dados em tempo real.
     */
    @Query("SELECT * FROM tarefas ORDER BY prioridade DESC, dataVencimento ASC")
    LiveData<List<Tarefa>> getAllTarefas();

    // Query original (agora para ordenação por data)
    @Query("SELECT * FROM tarefas ORDER BY dataVencimento ASC")
    LiveData<List<Tarefa>> getAllTarefasByDate();

    // Nova Query: Ordenar por Prioridade (Prioridade 3, 2, 1)
    @Query("SELECT * FROM tarefas ORDER BY prioridade DESC")
    LiveData<List<Tarefa>> getAllTarefasByPriority();

    // Nova Query: Filtrar por Pendentes (concluida = 0)
    @Query("SELECT * FROM tarefas WHERE concluida = 0 ORDER BY dataVencimento ASC")
    LiveData<List<Tarefa>> getPendingTarefas();

    // Nova Query: Filtrar por Concluídas (concluida = 1)
    @Query("SELECT * FROM tarefas WHERE concluida = 1 ORDER BY dataVencimento ASC")
    LiveData<List<Tarefa>> getCompletedTarefas();
}
