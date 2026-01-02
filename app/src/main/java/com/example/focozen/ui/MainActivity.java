package com.example.focozen.ui;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private TarefaRepository tarefaRepository;
    private TarefaAdapter tarefaAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdicionarTarefa;

    private LiveData<List<Tarefa>> currentTarefasLiveData;
    private Observer<List<Tarefa>> currentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        // 1 - Bindings / Associações
        recyclerView = findViewById(R.id.recyclerViewTarefas);
        fabAdicionarTarefa = findViewById(R.id.fabAdicionarTarefa);

        // 2 - Inicialização do Repositório e Adapter
        tarefaRepository = new TarefaRepository(getApplication());
        tarefaAdapter = new TarefaAdapter(this); // O Adapter será criado no próximo passo

        // NOVO CÓDIGO: Ligar o Listener para Edição


        recyclerView.setAdapter(tarefaAdapter);
        // O LayoutManager já está definido no XML, mas pode ser definido aqui:
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tarefaAdapter.setOnItemClickListener(new TarefaAdapter.OnItemClickListener() {
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

        // NOVO CÓDIGO: 5 - Configurar o Swipe para Eliminar
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
                Toast.makeText(MainActivity.this, "Tarefa eliminada!", Toast.LENGTH_SHORT).show();

                // Nota: O LiveData irá notificar o adapter da mudança na DB, atualizando a lista automaticamente.
            }
        }).attachToRecyclerView(recyclerView);

        // 4 - Comportamento (Listener)
        fabAdicionarTarefa.setOnClickListener(v -> {
            // Lógica para iniciar a AdicionarEditarActivity
            Intent intent = new Intent(MainActivity.this, AdicionarEditarActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // MainActivity.java

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_by_date) {
            observeNewLiveData(tarefaRepository.getAllTarefasByDate());
            Toast.makeText(this, "Ordenar por Data", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sort_by_priority) {
            observeNewLiveData(tarefaRepository.getAllTarefasByPriority());
            Toast.makeText(this, "Ordenar por Prioridade", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.filter_all) {
            // O filtro "Todas" é o mesmo que ordenar por data
            observeNewLiveData(tarefaRepository.getAllTarefasByDate());
            Toast.makeText(this, "Mostrar Todas", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.filter_pending) {
            observeNewLiveData(tarefaRepository.getPendingTarefas());
            Toast.makeText(this, "Mostrar Pendentes", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.filter_completed) {
            observeNewLiveData(tarefaRepository.getCompletedTarefas());
            Toast.makeText(this, "Mostrar Concluídas", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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


}
