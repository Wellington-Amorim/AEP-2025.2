package org.example.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicoNotificacao implements Runnable {
    private final BlockingQueue<String> fila = new LinkedBlockingQueue<>();
    private volatile boolean executando = true;

    public void notificarAsync(String mensagem) {
        fila.offer(mensagem);
    }

    public void desligar() {
        executando = false;
    }

    @Override
    public void run() {
        while (executando) {
            try {
                String mensagem = fila.take();
                System.out.println("[Notificação] " + mensagem);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

