package com.example.focozen.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.focozen.model.Tarefa;

/**
 * Classe de configuração da base de dados Room.
 * Implementa o padrão Singleton.
 */
@Database(entities = {Tarefa.class}, version = 1, exportSchema = false)
public abstract class TarefaDatabase extends RoomDatabase {

    // O Room irá gerar a implementação desta interface
    public abstract TarefaDao tarefaDao();

    // Singleton instance
    private static volatile TarefaDatabase INSTANCE;

    /**
     * Retorna a instância única da base de dados.
     * @param context O contexto da aplicação.
     * @return A instância da base de dados.
     */
    public static TarefaDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (TarefaDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    TarefaDatabase.class,
                                    "tarefa_database" // Nome do ficheiro da base de dados
                            )
                            .fallbackToDestructiveMigration() // Opção para migração simples (apenas para desenvolvimento)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
