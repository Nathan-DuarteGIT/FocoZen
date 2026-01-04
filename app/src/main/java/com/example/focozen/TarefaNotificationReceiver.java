package com.example.focozen;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.focozen.ui.MainActivity;

/**
 * TarefaNotificationReceiver
 * --------------------------------------------------
 * BroadcastReceiver responsável por:
 *  - Receber eventos agendados (ex: AlarmManager)
 *  - Criar e exibir notificações de lembrete de tarefas
 *
 * Este Receiver é acionado quando chega a data/hora
 * de vencimento de uma tarefa.
 */
public class TarefaNotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "tarefa_channel";
    public static final String EXTRA_TITULO = "extra_titulo";
    public static final String EXTRA_DESCRICAO = "extra_descricao";
    public static final String EXTRA_ID = "extra_id";

    /**
     * Método chamado automaticamente pelo sistema
     * quando o BroadcastReceiver é ativado.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String titulo = intent.getStringExtra(EXTRA_TITULO);
        String descricao = intent.getStringExtra(EXTRA_DESCRICAO);
        int id = intent.getIntExtra(EXTRA_ID, 0);

        // 1. Criar o canal de notificação (necessário para Android 8.0+)
        createNotificationChannel(context);

        // 2. Criar o Intent que abre a MainActivity ao clicar na notificação
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, id, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 3. Construir a Notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Use um ícone seu
                .setContentTitle(titulo)
                .setContentText(descricao)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        // 4. Exibir a Notificação
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }


    /**
     * Cria o canal de notificações (Android 8.0+)
     */
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Lembretes de Tarefas";
            String description = "Canal para notificações de tarefas com data de vencimento.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
