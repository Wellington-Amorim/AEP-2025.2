package org.example.util;

public class ConsoleUtils {
    // Cores
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";

    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void imprimirTitulo(String titulo) {
        System.out.println(BOLD + BLUE + "\n=== " + titulo + " ===" + RESET);
    }

    public static void imprimirSecao(String titulo) {
        System.out.println(CYAN + "\n" + titulo + RESET);
    }

    public static void imprimirSucesso(String mensagem) {
        System.out.println(GREEN + "\n✓ " + mensagem + RESET);
    }

    public static void imprimirErro(String mensagem) {
        System.out.println(RED + "\n✗ " + mensagem + RESET);
    }

    public static void imprimirAviso(String mensagem) {
        System.out.println(YELLOW + "\n⚠ " + mensagem + RESET);
    }

    public static String formatarMenu() {
        StringBuilder menu = new StringBuilder();
        menu.append(BOLD + BLUE + "\n=== Sistema de Doações Educacionais ===\n" + RESET);
        
        // Menu Principal
        menu.append(CYAN + "\nMenu Principal:" + RESET);
        menu.append("\n 1. Gestão de Doadores");
        menu.append("\n 2. Gestão de Beneficiários");
        menu.append("\n 3. Gestão de Doações");
        menu.append("\n 0. Sair");
        
        menu.append(YELLOW + "\n\nEscolha uma opção: " + RESET);
        return menu.toString();
    }
    
    public static String formatarMenuDoadores() {
        StringBuilder menu = new StringBuilder();
        menu.append(BOLD + BLUE + "\n=== Gestão de Doadores ===\n" + RESET);
        menu.append("\n 1. Cadastrar Novo Doador");
        menu.append("\n 2. Editar Doador");
        menu.append("\n 3. Listar Doadores");
        menu.append("\n 4. Remover Doador");
        menu.append("\n 0. Voltar ao Menu Principal");
        menu.append(YELLOW + "\n\nEscolha uma opção: " + RESET);
        return menu.toString();
    }
    
    public static String formatarMenuBeneficiarios() {
        StringBuilder menu = new StringBuilder();
        menu.append(BOLD + BLUE + "\n=== Gestão de Beneficiários ===\n" + RESET);
        menu.append("\n 1. Cadastrar Novo Beneficiário");
        menu.append("\n 2. Editar Beneficiário");
        menu.append("\n 3. Listar Beneficiários");
        menu.append("\n 4. Remover Beneficiário");
        menu.append("\n 0. Voltar ao Menu Principal");
        menu.append(YELLOW + "\n\nEscolha uma opção: " + RESET);
        return menu.toString();
    }
    
    public static String formatarMenuDoacoes() {
        StringBuilder menu = new StringBuilder();
        menu.append(BOLD + BLUE + "\n=== Gestão de Doações ===\n" + RESET);
        menu.append("\n 1. Registrar Nova Doação");
        menu.append("\n 2. Listar Todas as Doações");
        menu.append("\n 3. Distribuir Doações");
        menu.append("\n 4. Concluir Doação");
        menu.append("\n 5. Cancelar Doação");
        menu.append("\n 6. Relatório: Doações por Doador");
        menu.append("\n 7. Relatório: Doações por Status");
        menu.append("\n 0. Voltar ao Menu Principal");
        menu.append(YELLOW + "\n\nEscolha uma opção: " + RESET);
        return menu.toString();
    }
}