package com.example.focozen.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.focozen.R;
import com.example.focozen.data.TarefaRepository;
import com.example.focozen.model.Tarefa;

import java.util.Calendar;
import java.util.Date;
/**
 * AdicionarEditarActivity
 * --------------------------------------------------
 * Activity responsável por:
 *  - Criar novas tarefas
 *  - Editar tarefas existentes
 *  - Definir título, descrição, prioridade e data de vencimento
 *
 * Esta Activity funciona em dois modos:
 *  - Modo Adicionar (sem EXTRA_TAREFA_ID)
 *  - Modo Editar (com EXTRA_TAREFA_ID)
 */
public class AdicionarEditarActivity extends AppCompatActivity {

    public static final String EXTRA_TAREFA_ID = "com.example.focozen.EXTRA_TAREFA_ID";
    public static final String EXTRA_TAREFA_TITULO = "com.example.focozen.EXTRA_TAREFA_TITULO";
    public static final String EXTRA_TAREFA_DESCRICAO = "com.example.focozen.EXTRA_TAREFA_DESCRICAO";
    public static final String EXTRA_TAREFA_PRIORIDADE = "com.example.focozen.EXTRA_TAREFA_PRIORIDADE";
    public static final String EXTRA_TAREFA_DATA_VENCIMENTO = "com.example.focozen.EXTRA_TAREFA_DATA_VENCIMENTO";
    public static final String EXTRA_TAREFA_CONCLUIDA = "com.example.focozen.EXTRA_TAREFA_CONCLUIDA";

    private EditText editTextTitulo;
    private EditText editTextDescricao;
    private Spinner spinnerPrioridade;
    private TextView textViewDataVencimento;
    private TarefaRepository tarefaRepository;

    private long dataVencimentoTimestamp = 0; // Armazena o timestamp da data selecionada
    private int tarefaId = -1; // -1 para nova tarefa, >0 para edição

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        // Ativa o botão de voltar (Up button) na barra de topo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
        /**
         * Verifica se a Activity foi aberta em modo de edição
         */
        if (getIntent().hasExtra(EXTRA_TAREFA_ID)) {
            setTitle(getString(R.string.title_edit_task));

            // 1. Obter o ID da Tarefa
            tarefaId = getIntent().getIntExtra(EXTRA_TAREFA_ID, -1);

            // 2. Carregar os restantes dados para os campos
            editTextTitulo.setText(getIntent().getStringExtra(EXTRA_TAREFA_TITULO));
            editTextDescricao.setText(getIntent().getStringExtra(EXTRA_TAREFA_DESCRICAO));

            // 3. Carregar Prioridade
            int prioridade = getIntent().getIntExtra(EXTRA_TAREFA_PRIORIDADE, 1);
            spinnerPrioridade.setSelection(prioridade - 1);

            // 4. Carregar Data de Vencimento
            dataVencimentoTimestamp = getIntent().getLongExtra(EXTRA_TAREFA_DATA_VENCIMENTO, 0);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(dataVencimentoTimestamp);
            // Chama a função que atualiza o TextView da data
            atualizarDataVencimentoUI(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        } else {
            setTitle(getString(R.string.title_add_task));
        }
    }
    /**
     * Trata o clique no botão de voltar (Up Button)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Este código é executado quando o utilizador clica no botão de voltar (seta)
            onBackPressed(); // Simula o clique no botão de voltar do sistema
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Inicializa todos os componentes da Activity
     */
    private void init() {
        // 1 - Bindings
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDescricao = findViewById(R.id.editTextDescricao);
        spinnerPrioridade = findViewById(R.id.spinnerPrioridade);
        textViewDataVencimento = findViewById(R.id.textViewDataVencimento);
        //associa o click do botão à função "guardar tarefa"
        findViewById(R.id.buttonGuardar).setOnClickListener(v -> guardarTarefa());

        tarefaRepository = new TarefaRepository(getApplication());

        /**
         * Configuração do Spinner de Prioridade
         */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.prioridades_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(adapter);

        /**
         * Configuração do DatePicker
         */
        textViewDataVencimento.setOnClickListener(v -> mostrarDatePicker());

        // Inicializar com a data de hoje
        Calendar c = Calendar.getInstance();
        dataVencimentoTimestamp = c.getTimeInMillis();
        atualizarDataVencimentoUI(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }
    /**
     * Mostra o DatePickerDialog para escolha da data
     */
    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int ano = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    atualizarDataVencimentoUI(year, monthOfYear, dayOfMonth);
                }, ano, mes, dia);
        datePickerDialog.show();
    }
    /**
     * Atualiza o TextView da data e o timestamp
     */
    private void atualizarDataVencimentoUI(int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth, 23, 59, 59); // Define a hora para o final do dia
        dataVencimentoTimestamp = c.getTimeInMillis();

        String dataFormatada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
        textViewDataVencimento.setText(dataFormatada);
    }
    /**
     * Valida os campos e guarda a tarefa na base de dados
     */
    private void guardarTarefa() {
        String titulo = editTextTitulo.getText().toString().trim();
        String descricao = editTextDescricao.getText().toString().trim();
        int prioridade = spinnerPrioridade.getSelectedItemPosition() + 1; // 0=Baixa(1), 1=Média(2), 2=Alta(3)

        if (titulo.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_title_required), Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar o objeto Tarefa
        Tarefa tarefa = new Tarefa(titulo, descricao, prioridade, dataVencimentoTimestamp, false);

        if (tarefaId != -1) {
            // Edição
            tarefa.setId(tarefaId);
            // Manter o estado de concluída original (se for edição)
            tarefaRepository.update(tarefa);
            Toast.makeText(this, getString(R.string.toast_task_updated), Toast.LENGTH_SHORT).show();
        } else {
            // Nova Tarefa
            tarefaRepository.insert(tarefa);
            Toast.makeText(this, getString(R.string.toast_task_saved), Toast.LENGTH_SHORT).show();
        }

        finish(); // Fecha a Activity e volta para a MainActivity
    }
}
