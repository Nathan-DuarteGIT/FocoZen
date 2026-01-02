package com.example.focozen.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.focozen.model.Tarefa;

import java.util.List;

/**
 * Repositório que abstrai o acesso à base de dados.
 * Executa operações de base de dados em background.
 */
public class TarefaRepository {

    private TarefaDao tarefaDao;
    private LiveData<List<Tarefa>> todasTarefas;
    private LiveData<List<Tarefa>> allTarefasByDate;
    private LiveData<List<Tarefa>> allTarefasByPriority;
    private LiveData<List<Tarefa>> pendingTarefas;
    private LiveData<List<Tarefa>> completedTarefas;

    public TarefaRepository(Application application) {
        TarefaDatabase database = TarefaDatabase.getInstance(application);
        tarefaDao = database.tarefaDao();
        // Inicializa o LiveData com todas as tarefas ordenadas
        todasTarefas = tarefaDao.getAllTarefas();
        // Inicializar os novos LiveData
        allTarefasByDate = tarefaDao.getAllTarefasByDate();
        allTarefasByPriority = tarefaDao.getAllTarefasByPriority();
        pendingTarefas = tarefaDao.getPendingTarefas();
        completedTarefas = tarefaDao.getCompletedTarefas();
    }

    // --- Métodos para a UI chamar ---

    public void insert(Tarefa tarefa) {
        new InsertTarefaAsyncTask(tarefaDao).execute(tarefa);
    }

    public void update(Tarefa tarefa) {
        new UpdateTarefaAsyncTask(tarefaDao).execute(tarefa);
    }

    public void delete(Tarefa tarefa) {
        new DeleteTarefaAsyncTask(tarefaDao).execute(tarefa);
    }

    public LiveData<List<Tarefa>> getAllTarefas() {
        return todasTarefas;
    }

    public LiveData<List<Tarefa>> getAllTarefasByDate() {
        return allTarefasByDate;
    }

    public LiveData<List<Tarefa>> getAllTarefasByPriority() {
        return allTarefasByPriority;
    }

    public LiveData<List<Tarefa>> getPendingTarefas() {
        return pendingTarefas;
    }

    public LiveData<List<Tarefa>> getCompletedTarefas() {
        return completedTarefas;
    }
    // --- AsyncTasks para operações em background ---

    private static class InsertTarefaAsyncTask extends AsyncTask<Tarefa, Void, Void> {
        private TarefaDao asyncTarefaDao;

        private InsertTarefaAsyncTask(TarefaDao dao) {
            asyncTarefaDao = dao;
        }

        @Override
        protected Void doInBackground(Tarefa... tarefas) {
            asyncTarefaDao.insert(tarefas[0]);
            return null;
        }
    }

    private static class UpdateTarefaAsyncTask extends AsyncTask<Tarefa, Void, Void> {
        private TarefaDao asyncTarefaDao;

        private UpdateTarefaAsyncTask(TarefaDao dao) {
            asyncTarefaDao = dao;
        }

        @Override
        protected Void doInBackground(Tarefa... tarefas) {
            asyncTarefaDao.update(tarefas[0]);
            return null;
        }
    }

    private static class DeleteTarefaAsyncTask extends AsyncTask<Tarefa, Void, Void> {
        private TarefaDao asyncTarefaDao;

        private DeleteTarefaAsyncTask(TarefaDao dao) {
            asyncTarefaDao = dao;
        }

        @Override
        protected Void doInBackground(Tarefa... tarefas) {
            asyncTarefaDao.delete(tarefas[0]);
            return null;
        }
    }
}
