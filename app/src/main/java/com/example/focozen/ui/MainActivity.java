package com.example.focozen.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
// Certifique-se de que tem o import para Toast
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.focozen.model.Tarefa;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.focozen.R;
import com.example.focozen.data.TarefaRepository;
import com.example.focozen.ui.adapter.TarefaAdapter;

import java.util.List;
import java.util.Locale;

/**
 * MainActivity
 * --------------------------------------------------
 * Activity principal da aplicação.
 * Responsável por:
 *  - Mostrar a lista de tarefas
 *  - Ordenar e filtrar tarefas
 *  - Adicionar, editar, concluir e eliminar tarefas
 *  - Alterar o idioma da aplicação
 */
public class MainActivity extends AppCompatActivity {

    // Repositório responsável pelo acesso aos dados (Room / DB)
    private TarefaRepository tarefaRepository;
    // Adapter do RecyclerView
    private TarefaAdapter tarefaAdapter;
    // Componentes da UI
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdicionarTarefa;
    // Referências para controlo do LiveData atual
    private LiveData<List<Tarefa>> currentTarefasLiveData;
    private Observer<List<Tarefa>> currentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * Método responsável por inicializar:
     *  - Views
     *  - Adapter
     *  - Repositório
     *  - Listeners
     *  - Observadores do LiveData
     */
    private void init() {
        // 1 - Bindings / Associações
        recyclerView = findViewById(R.id.recyclerViewTarefas);
        fabAdicionarTarefa = findViewById(R.id.fabAdicionarTarefa);

        // 2 - Inicialização do Repositório e Adapter
        tarefaRepository = new TarefaRepository(getApplication());
        tarefaAdapter = new TarefaAdapter(this); // O Adapter será criado no próximo passo


        recyclerView.setAdapter(tarefaAdapter);
        // O LayoutManager já está definido no XML, mas pode ser definido aqui:
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        /**
         * Listener para cliques nos itens do RecyclerView
         */
        tarefaAdapter.setOnItemClickListener(new TarefaAdapter.OnItemClickListener() {
            /**
             * Clique normal no item → abrir Activity de edição
             */
            @Override
            public void onItemClick(Tarefa tarefa) {
                // Lógica para iniciar a AdicionarEditarActivity em modo de Edição
                Intent intent = new Intent(MainActivity.this, AdicionarEditarActivity.class);

                // Passar os dados da tarefa para a Activity de Edição
                intent.putExtra(AdicionarEditarActivity.EXTRA_TAREFA_ID, (int) tarefa.getId());
                intent.putExtra(AdicionarEditarActivity.EXTRA_TAREFA_TITULO, tarefa.getTitulo());
                intent.putExtra(AdicionarEditarActivity.EXTRA_TAREFA_DESCRICAO, tarefa.getDescricao());
                intent.putExtra(AdicionarEditarActivity.EXTRA_TAREFA_PRIORIDADE, tarefa.getPrioridade());
                intent.putExtra(AdicionarEditarActivity.EXTRA_TAREFA_DATA_VENCIMENTO, tarefa.getDataVencimento());
                intent.putExtra(AdicionarEditarActivity.EXTRA_TAREFA_CONCLUIDA, tarefa.isConcluida());

                startActivity(intent);
            }
            /**
             * Clique na checkbox de "concluída"
             */
            @Override
            public void onConcluidaClick(Tarefa tarefa, boolean isChecked) {
                // Lógica para o Passo 2.11 (Marcar como Concluída)

                // 1. Atualizar o estado da tarefa
                tarefa.setConcluida(isChecked);

                // 2. Chamar o Repository para atualizar a DB
                tarefaRepository.update(tarefa);

                // Nota: O LiveData irá notificar o adapter, atualizando a lista.
            }
        });

        // 3 - Observar os dados do LiveData
        observeNewLiveData(tarefaRepository.getAllTarefasByDate());

        /**
         * Swipe para eliminar tarefa (esquerda ou direita)
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Não queremos arrastar e soltar
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Obter a tarefa a ser eliminada
                int position = viewHolder.getAdapterPosition();
                Tarefa tarefa = tarefaAdapter.getTarefaAt(position);

                // Eliminar da base de dados
                tarefaRepository.delete(tarefa);

                // Mostrar mensagem de confirmação
                Toast.makeText(MainActivity.this, getString(R.string.toast_task_deleted), Toast.LENGTH_SHORT).show();

                // Nota: O LiveData irá notificar o adapter da mudança na DB, atualizando a lista automaticamente.
            }
        }).attachToRecyclerView(recyclerView);

        /**
         * Clique no FAB → adicionar nova tarefa
         */
        fabAdicionarTarefa.setOnClickListener(v -> {
            // Lógica para iniciar a AdicionarEditarActivity
            Intent intent = new Intent(MainActivity.this, AdicionarEditarActivity.class);
            startActivity(intent);
        });

    }
    /**
     * Criação do menu (ordenar, filtrar e idiomas)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Tratamento dos cliques no menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_by_date) {
            observeNewLiveData(tarefaRepository.getAllTarefasByDate());
            Toast.makeText(this, getString(R.string.sort_by_date), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sort_by_priority) {
            observeNewLiveData(tarefaRepository.getAllTarefasByPriority());
            Toast.makeText(this, getString(R.string.sort_by_priority), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.filter_all) {
            // O filtro "Todas" é o mesmo que ordenar por data
            observeNewLiveData(tarefaRepository.getAllTarefasByDate());
            Toast.makeText(this, getString(R.string.filter_all), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.filter_pending) {
            observeNewLiveData(tarefaRepository.getPendingTarefas());
            Toast.makeText(this, getString(R.string.filter_pending), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.filter_completed) {
            observeNewLiveData(tarefaRepository.getCompletedTarefas());
            Toast.makeText(this, getString(R.string.filter_completed), Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.lang_pt) {
            saveLocale("pt");
            changeLocaleAndRecreate("pt");
            Toast.makeText(this, "Mudar para Português", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.lang_en) {
            saveLocale("en");
            changeLocaleAndRecreate("en");
            Toast.makeText(this, "Change to English", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Remove o observer antigo e observa um novo LiveData
     */
    private void observeNewLiveData(LiveData<List<Tarefa>> newLiveData) {
        // 1. Remover o observador antigo (se existir)
        if (currentTarefasLiveData != null && currentObserver != null) {
            currentTarefasLiveData.removeObserver(currentObserver);
        }

        // 2. Definir o novo LiveData e Observer
        currentTarefasLiveData = newLiveData;
        currentObserver = tarefas -> tarefaAdapter.setTarefas(tarefas);

        // 3. Observar o novo LiveData
        currentTarefasLiveData.observe(this, currentObserver);
    }
    /**
     * Carrega o idioma guardado nas SharedPreferences
     */
    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "pt"); // "pt" como padrão se não houver escolha

        // 1. Criar um objeto Locale com o código do idioma (ex: "pt", "en")
        Locale locale = new Locale(language);
        Locale.setDefault(locale); // Define o idioma padrão

        // 2. Obter a configuração atual dos recursos
        android.content.res.Configuration config = getResources().getConfiguration();

        // 3. Definir o novo idioma na configuração
        config.setLocale(locale);

        // 4. Aplicar a nova configuração aos recursos
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // NÃO CHAMAR RECREATE AQUI!
    }
    /**
     * Altera o idioma e recria a Activity
     */
    private void changeLocaleAndRecreate(String languageCode) {
        // 1. Guardar a escolha
        saveLocale(languageCode);

        // 2. Aplicar a nova configuração (igual ao loadLocale)
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // 3. Recriar a Activity
        recreate();
    }
    /**
     * Guarda o idioma escolhido nas SharedPreferences
     */
    private void saveLocale(String languageCode) {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("My_Lang", languageCode);
        editor.apply();
    }


}
