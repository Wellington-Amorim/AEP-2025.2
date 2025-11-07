package org.example;

import org.example.controller.MenuController;
import org.example.service.Gerenciador;
import org.example.service.ServicoNotificacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        logger.info("Iniciando aplicacao");
        
        try (Scanner scanner = new Scanner(System.in)) {
            ServicoNotificacao servicoNotificacao = new ServicoNotificacao();
            Thread t = new Thread(servicoNotificacao, "notificador");
            t.setDaemon(true);
            t.start();
            
            Gerenciador gerenciador = new Gerenciador(servicoNotificacao);
            MenuController menuController = new MenuController(scanner, gerenciador);
            
            try {
                menuController.executarMenu();
            } finally {
                logger.info("Desligando servico de notificacao");
                servicoNotificacao.desligar();

                try {
                    logger.info("Encerrando pool de conexoes");
                    org.example.db.Database.encerrar();
                } catch (Exception e) {
                    logger.error("Erro ao encerrar pool de conexoes", e);
                }
                
                logger.info("Aplicacao encerrada");
            }
        }
    }


}
