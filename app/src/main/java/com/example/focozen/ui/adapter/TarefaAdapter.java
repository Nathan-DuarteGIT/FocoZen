package com.example.focozen.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focozen.R;
import com.example.focozen.model.Tarefa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para o RecyclerView que exibe a lista de Tarefas.
 */
public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder> {

    private List<Tarefa> tarefas = new ArrayList<>();
    private final Context context;
    private OnItemClickListener listener;

    public TarefaAdapter(Context context) {
        this.context = context;
    }

    /**
     * Obtém a tarefa numa posição específica.
     */
    public Tarefa getTarefaAt(int position) {
        return tarefas.get(position);
    }

    // Interface para lidar com cliques (para edição)
    public interface OnItemClickListener {
        void onItemClick(Tarefa tarefa);
        void onConcluidaClick(Tarefa tarefa, boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tarefa, parent, false);
        return new TarefaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TarefaViewHolder holder, int position) {
        Tarefa currentTarefa = tarefas.get(position);

        // 1. Título e Estado
        holder.textViewTitulo.setText(currentTarefa.getTitulo());
        holder.checkBoxConcluida.setChecked(currentTarefa.isConcluida());

        // NOVO CÓDIGO: Lógica Visual para Tarefa Concluída
        if (currentTarefa.isConcluida()) {
            // Riscado (Strikethrough)
            holder.textViewTitulo.setPaintFlags(holder.textViewTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            // Cor cinzenta (opcional)
            holder.textViewTitulo.setTextColor(context.getResources().getColor(R.color.gray_text));
        } else {
            // Remover Riscado
            holder.textViewTitulo.setPaintFlags(holder.textViewTitulo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            // Cor normal (opcional)
            holder.textViewTitulo.setTextColor(context.getResources().getColor(R.color.black_text));
        }

        // 2. Prioridade
        String prioridadeTexto = getPrioridadeTexto(currentTarefa.getPrioridade());
        holder.textViewPrioridade.setText(prioridadeTexto);

        // NOVO CÓDIGO: Definir o background com base na prioridade
        int backgroundResId;
        switch (currentTarefa.getPrioridade()) {
            case 3: // ALTA
                backgroundResId = R.drawable.bg_prioridade_alta;
                break;
            case 2: // MÉDIA
                backgroundResId = R.drawable.bg_prioridade_media;
                break;
            case 1: // BAIXA
                backgroundResId = R.drawable.bg_prioridade_baixa;
                break;
            default:
                backgroundResId = 0; // Sem background
        }
        if (backgroundResId != 0) {
            holder.textViewPrioridade.setBackgroundResource(backgroundResId);
        }

        // 3. Data de Vencimento
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataFormatada = sdf.format(currentTarefa.getDataVencimento());
        holder.textViewData.setText(context.getString(R.string.label_due_date) + dataFormatada);

        // 4. Comportamento de Clique (para Edição)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentTarefa);
            }
        });

        // 5. Comportamento do CheckBox (para Marcar como Concluída)
        holder.checkBoxConcluida.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onConcluidaClick(currentTarefa, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tarefas.size();
    }

    // Método para atualizar a lista de tarefas (chamado pelo LiveData na MainActivity)
    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
        notifyDataSetChanged();
    }

    // Método auxiliar para converter o int da prioridade em texto
    private String getPrioridadeTexto(int prioridade) {
        // O array de prioridades é 0-based (0, 1, 2)
        // O valor da prioridade na BD é 1-based (1, 2, 3)

        // Obter o array de strings do recurso
        String[] prioridades = context.getResources().getStringArray(R.array.prioridades_array);

        // Verificar se a prioridade está dentro do limite
        if (prioridade >= 1 && prioridade <= prioridades.length) {
            // Retorna o texto correspondente (prioridade - 1 para 0-based)
            return prioridades[prioridade - 1];
        }

        return "N/A";
    }

    // ViewHolder: Mapeia os elementos do layout para a classe Java
    class TarefaViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitulo;
        private final TextView textViewData;
        private final TextView textViewPrioridade;
        private final CheckBox checkBoxConcluida;

        public TarefaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewData = itemView.findViewById(R.id.textViewData);
            textViewPrioridade = itemView.findViewById(R.id.textViewPrioridade);
            checkBoxConcluida = itemView.findViewById(R.id.checkBoxConcluida);
        }
    }
}