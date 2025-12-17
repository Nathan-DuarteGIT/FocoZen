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

    /**
     * Obtém tarefas filtradas por estado (concluída ou pendente).
     */
    @Query("SELECT * FROM tarefas WHERE concluida = :status ORDER BY prioridade DESC, dataVencimento ASC")
    LiveData<List<Tarefa>> getTarefasByStatus(boolean status);
}
