package org.example.controller;


import org.example.command.Command;
import org.example.model.Beneficiario;
import org.example.model.Doacao;
import org.example.model.Doador;
import org.example.service.Gerenciador;
import org.example.util.ConsoleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class MenuController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
    private static final Pattern PADRAO_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PADRAO_TELEFONE = Pattern.compile("^\\(?[1-9]{2}\\)? ?(?:[2-8]|9[1-9])[0-9]{3}\\-?[0-9]{4}$");
    
    private final Scanner scanner;
    private final Gerenciador gerenciador;
    private final Map<String, Command> comandos;

    public MenuController(Scanner scanner, Gerenciador gerenciador) {
        this.scanner = scanner;
        this.gerenciador = gerenciador;
        this.comandos = new HashMap<>();
        inicializarComandos();
    }

    private void inicializarComandos() {
        // Comandos para Doadores
        comandos.put("cadastrarDoador", () -> {
            logger.info("Iniciando cadastro de doador");
            ConsoleUtils.imprimirTitulo("Cadastro de Doador");
            System.out.print(ConsoleUtils.BOLD + "Nome: " + ConsoleUtils.RESET);
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) throw new IllegalArgumentException("Nome não pode estar vazio");
            System.out.print(ConsoleUtils.BOLD + "Contato (e-mail ou telefone): " + ConsoleUtils.RESET);
            String contato = scanner.nextLine().trim();
            if (!contatoValido(contato)) throw new IllegalArgumentException("Contato inválido. Forneça um e-mail ou telefone válido");
            Doador d = gerenciador.cadastrarDoador(nome, contato);
            ConsoleUtils.imprimirSucesso("Doador cadastrado com sucesso: " + d);
            logger.info("Doador cadastrado: {}", d);
        });

        comandos.put("editarDoador", () -> {
            logger.info("Editando doador");
            ConsoleUtils.imprimirTitulo("Editar Doador");
            System.out.print(ConsoleUtils.BOLD + "ID do Doador: " + ConsoleUtils.RESET);
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print(ConsoleUtils.BOLD + "Novo Nome: " + ConsoleUtils.RESET);
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) throw new IllegalArgumentException("Nome não pode estar vazio");
            System.out.print(ConsoleUtils.BOLD + "Novo Contato: " + ConsoleUtils.RESET);
            String contato = scanner.nextLine().trim();
            if (!contatoValido(contato)) throw new IllegalArgumentException("Contato inválido");
            gerenciador.editarDoador(id, nome, contato);
            ConsoleUtils.imprimirSucesso("Doador atualizado com sucesso!");
        });

        comandos.put("listarDoadores", () -> {
            logger.info("Listando doadores");
            // Primeiro obtemos os doadores (isso vai disparar a inicialização do HikariCP)
            List<Doador> doadores = gerenciador.listarDoadores();
            
            // Agora imprimimos o título e os dados
            ConsoleUtils.imprimirTitulo("Lista de Doadores");
            if (doadores.isEmpty()) {
                ConsoleUtils.imprimirAviso("Nenhum doador cadastrado");
                return;
            }
            System.out.printf(ConsoleUtils.BOLD + "%-5s %-30s %-30s%n" + ConsoleUtils.RESET, "ID", "Nome", "Contato");
            System.out.println(ConsoleUtils.CYAN + "-".repeat(65) + ConsoleUtils.RESET);
            doadores.forEach(d -> System.out.printf("%-5d %-30s %-30s%n", d.getId(), d.getNome(), d.getContato()));
        });

        comandos.put("removerDoador", () -> {
            logger.info("Removendo doador");
            ConsoleUtils.imprimirTitulo("Remover Doador");
            System.out.print(ConsoleUtils.BOLD + "ID do Doador: " + ConsoleUtils.RESET);
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print(ConsoleUtils.YELLOW + "Tem certeza? (s/N): " + ConsoleUtils.RESET);
            if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                ConsoleUtils.imprimirAviso("Operação cancelada");
                return;
            }
            gerenciador.removerDoador(id);
            ConsoleUtils.imprimirSucesso("Doador removido com sucesso!");
        });

        // Comandos para Beneficiários
        comandos.put("cadastrarBeneficiario", () -> {
            logger.info("Cadastrando beneficiário");
            ConsoleUtils.imprimirTitulo("Cadastro de Beneficiário");
            System.out.print(ConsoleUtils.BOLD + "Nome: " + ConsoleUtils.RESET);
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) throw new IllegalArgumentException("Nome não pode estar vazio");
            System.out.print(ConsoleUtils.BOLD + "Necessidade: " + ConsoleUtils.RESET);
            String necessidade = scanner.nextLine().trim();
            Beneficiario b = gerenciador.cadastrarBeneficiario(nome, necessidade);
            ConsoleUtils.imprimirSucesso("Beneficiário cadastrado com sucesso: " + b);
        });

        comandos.put("editarBeneficiario", () -> {
            logger.info("Editando beneficiário");
            ConsoleUtils.imprimirTitulo("Editar Beneficiário");
            System.out.print(ConsoleUtils.BOLD + "ID do Beneficiário: " + ConsoleUtils.RESET);
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print(ConsoleUtils.BOLD + "Novo Nome: " + ConsoleUtils.RESET);
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) throw new IllegalArgumentException("Nome não pode estar vazio");
            System.out.print(ConsoleUtils.BOLD + "Nova Necessidade: " + ConsoleUtils.RESET);
            String necessidade = scanner.nextLine().trim();
            gerenciador.editarBeneficiario(id, nome, necessidade);
            ConsoleUtils.imprimirSucesso("Beneficiário atualizado com sucesso!");
        });

        comandos.put("listarBeneficiarios", () -> {
            logger.info("Listando beneficiários");
            ConsoleUtils.imprimirTitulo("Lista de Beneficiários");
            List<Beneficiario> beneficiarios = gerenciador.listarBeneficiarios();
            if (beneficiarios.isEmpty()) {
                ConsoleUtils.imprimirAviso("Nenhum beneficiário cadastrado");
                return;
            }
            System.out.printf(ConsoleUtils.BOLD + "%-5s %-30s %-30s%n" + ConsoleUtils.RESET, "ID", "Nome", "Necessidade");
            System.out.println(ConsoleUtils.CYAN + "-".repeat(65) + ConsoleUtils.RESET);
            beneficiarios.forEach(b -> System.out.printf("%-5d %-30s %-30s%n", b.getId(), b.getNome(), b.getNecessidade()));
        });

        comandos.put("removerBeneficiario", () -> {
            logger.info("Removendo beneficiário");
            ConsoleUtils.imprimirTitulo("Remover Beneficiário");
            System.out.print(ConsoleUtils.BOLD + "ID do Beneficiário: " + ConsoleUtils.RESET);
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print(ConsoleUtils.YELLOW + "Tem certeza? (s/N): " + ConsoleUtils.RESET);
            if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                ConsoleUtils.imprimirAviso("Operação cancelada");
                return;
            }
            gerenciador.removerBeneficiario(id);
            ConsoleUtils.imprimirSucesso("Beneficiário removido com sucesso!");
        });

        // Comandos para Doações
        comandos.put("novaDoacao", () -> {
            logger.info("Registrando nova doação");
            ConsoleUtils.imprimirTitulo("Nova Doação");
            System.out.print(ConsoleUtils.BOLD + "ID do Doador: " + ConsoleUtils.RESET);
            int idDoador = Integer.parseInt(scanner.nextLine().trim());
            System.out.print(ConsoleUtils.BOLD + "Item: " + ConsoleUtils.RESET);
            String item = scanner.nextLine().trim();
            if (item.isEmpty()) throw new IllegalArgumentException("Item não pode estar vazio");
            System.out.print(ConsoleUtils.BOLD + "Quantidade: " + ConsoleUtils.RESET);
            int quantidade = Integer.parseInt(scanner.nextLine().trim());
            if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero");
            Doacao d = gerenciador.registrarDoacao(idDoador, item, quantidade);
            ConsoleUtils.imprimirSucesso("Doação registrada com sucesso: " + d);
        });

        comandos.put("listarDoacoes", () -> {
            logger.info("Listando doações");
            ConsoleUtils.imprimirTitulo("Lista de Doações");
            List<Doacao> doacoes = gerenciador.listarDoacoes();
            if (doacoes.isEmpty()) {
                ConsoleUtils.imprimirAviso("Nenhuma doação registrada");
                return;
            }
            System.out.printf(ConsoleUtils.BOLD + "%-5s %-8s %-20s %-8s %-12s%n" + ConsoleUtils.RESET, "ID", "Doador", "Item", "Qtd", "Status");
            System.out.println(ConsoleUtils.CYAN + "-".repeat(55) + ConsoleUtils.RESET);
            doacoes.forEach(d -> {
                String status = switch (d.getStatus()) {
                    case PENDENTE -> ConsoleUtils.YELLOW + "PENDENTE" + ConsoleUtils.RESET;
                    case DISTRIBUIDA -> ConsoleUtils.GREEN + "DISTRIBUÍDA" + ConsoleUtils.RESET;
                    case CANCELADA -> ConsoleUtils.RED + "CANCELADA" + ConsoleUtils.RESET;
                };
                System.out.printf("%-5d %-8d %-20s %-8d %s%n", d.getId(), d.getDoadorId(), d.getItem(), d.getQuantidade(), status);
            });
        });

        comandos.put("distribuirDoacoes", () -> {
            logger.info("Distribuindo doações");
            ConsoleUtils.imprimirTitulo("Distribuir Doações");
            int total = gerenciador.distribuirDoacoes();
            if (total > 0) {
                ConsoleUtils.imprimirSucesso(total + " doação(ões) distribuída(s) com sucesso!");
            } else {
                ConsoleUtils.imprimirAviso("Nenhuma doação distribuída");
            }
        });

        comandos.put("concluirDoacao", () -> {
            logger.info("Concluindo doação");
            ConsoleUtils.imprimirTitulo("Concluir Doação");
            System.out.print(ConsoleUtils.BOLD + "ID da Doação: " + ConsoleUtils.RESET);
            int id = Integer.parseInt(scanner.nextLine().trim());
            gerenciador.concluirDoacao(id);
            ConsoleUtils.imprimirSucesso("Doação concluída com sucesso!");
        });

        comandos.put("cancelarDoacao", () -> {
            logger.info("Cancelando doação");
            ConsoleUtils.imprimirTitulo("Cancelar Doação");
            System.out.print(ConsoleUtils.BOLD + "ID da Doação: " + ConsoleUtils.RESET);
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print(ConsoleUtils.YELLOW + "Tem certeza? (s/N): " + ConsoleUtils.RESET);
            if (!scanner.nextLine().trim().equalsIgnoreCase("s")) {
                ConsoleUtils.imprimirAviso("Operação cancelada");
                return;
            }
            gerenciador.cancelarDoacao(id);
            ConsoleUtils.imprimirSucesso("Doação cancelada com sucesso!");
        });

        comandos.put("doacoesPorDoador", () -> {
            logger.info("Listando Doações por Doador");
            ConsoleUtils.imprimirTitulo("Doações por Doador");
            System.out.print(ConsoleUtils.BOLD + "ID do Doador: " + ConsoleUtils.RESET);
            int idDoador = Integer.parseInt(scanner.nextLine().trim());
            List<Doacao> doacoes = gerenciador.listarDoacoesPorDoador(idDoador);
            if (doacoes.isEmpty()) {
                ConsoleUtils.imprimirAviso("Nenhuma doação encontrada para este doador");
                return;
            }
            System.out.printf(ConsoleUtils.BOLD + "%-5s %-20s %-8s %-12s%n" + ConsoleUtils.RESET, "ID", "Item", "Qtd", "Status");
            System.out.println(ConsoleUtils.CYAN + "-".repeat(47) + ConsoleUtils.RESET);
            doacoes.forEach(d -> {
                String status = switch (d.getStatus()) {
                    case PENDENTE -> ConsoleUtils.YELLOW + "PENDENTE" + ConsoleUtils.RESET;
                    case DISTRIBUIDA -> ConsoleUtils.GREEN + "DISTRIBUÍDA" + ConsoleUtils.RESET;
                    case CANCELADA -> ConsoleUtils.RED + "CANCELADA" + ConsoleUtils.RESET;
                };
                System.out.printf("%-5d %-20s %-8d %s%n", d.getId(), d.getItem(), d.getQuantidade(), status);
            });
        });

        comandos.put("doacoesPorStatus", () -> {
            logger.info("Listando Doações por Status");
            ConsoleUtils.imprimirTitulo("Doações por Status");
            System.out.println("Status disponíveis:");
            System.out.println(ConsoleUtils.YELLOW + "PENDENTE" + ConsoleUtils.RESET + " - Aguardando distribuição");
            System.out.println(ConsoleUtils.GREEN + "DISTRIBUIDA" + ConsoleUtils.RESET + " - Atribuída a um beneficiário");
            System.out.println(ConsoleUtils.RED + "CANCELADA" + ConsoleUtils.RESET + " - Cancelada pelo doador");
            System.out.print(ConsoleUtils.BOLD + "\nStatus: " + ConsoleUtils.RESET);
            String status = scanner.nextLine().trim().toUpperCase();
            status = Normalizer.normalize(status, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
            if (status.startsWith("DISTRIBU")) status = "DISTRIBUIDA";
            Doacao.Status st = Doacao.Status.valueOf(status);
            List<Doacao> doacoes = gerenciador.listarDoacoesPorStatus(st);
            if (doacoes.isEmpty()) {
                ConsoleUtils.imprimirAviso("Nenhuma doação encontrada com status " + st);
                return;
            }
            System.out.printf(ConsoleUtils.BOLD + "%-5s %-8s %-20s %-8s%n" + ConsoleUtils.RESET, "ID", "Doador", "Item", "Qtd");
            System.out.println(ConsoleUtils.CYAN + "-".repeat(43) + ConsoleUtils.RESET);
            doacoes.forEach(d -> System.out.printf("%-5d %-8d %-20s %-8d%n", d.getId(), d.getDoadorId(), d.getItem(), d.getQuantidade()));
        });
    }

    public void executarMenu() {
        logger.info("Iniciando sistema");
        boolean executando = true;
        while (executando) {
            try {
                limparTela();
                System.out.print(ConsoleUtils.formatarMenu());
                String opcao = scanner.nextLine().trim();
                
                switch (opcao) {
                    case "0":
                        logger.info("Encerrando sistema");
                        executando = false;
                        ConsoleUtils.imprimirSucesso("Sistema finalizado com sucesso!");
                        break;
                    case "1":
                        menuDoadores();
                        break;
                    case "2":
                        menuBeneficiarios();
                        break;
                    case "3":
                        menuDoacoes();
                        break;
                    default:
                        throw new IllegalArgumentException("Opção inválida");
                }

            } catch (IllegalArgumentException e) {
                logger.warn("Erro de validação: {}", e.getMessage());
                ConsoleUtils.imprimirErro("Erro: " + e.getMessage());
                aguardarEnter();
            } catch (SQLException e) {
                logger.error("Erro de banco: {}", e.getMessage());
                ConsoleUtils.imprimirErro("Erro de banco de dados: " + e.getMessage());
                aguardarEnter();
            } catch (Exception e) {
                logger.error("Erro inesperado", e);
                ConsoleUtils.imprimirErro("Erro inesperado: " + e.getMessage());
                aguardarEnter();
            }
        }
    }

    private void menuDoadores() throws SQLException {
        boolean noMenu = true;
        while (noMenu) {
            limparTela();
            System.out.print(ConsoleUtils.formatarMenuDoadores());
            String opcao = scanner.nextLine().trim();
            
            try {
                switch (opcao) {
                    case "0":
                        noMenu = false;
                        break;
                    case "1":
                        comandos.get("cadastrarDoador").execute();
                        break;
                    case "2":
                        comandos.get("editarDoador").execute();
                        break;
                    case "3":
                        comandos.get("listarDoadores").execute();
                        break;
                    case "4":
                        comandos.get("removerDoador").execute();
                        break;
                    default:
                        throw new IllegalArgumentException("Opção inválida");
                }
                if (noMenu) aguardarEnter();
            } catch (Exception e) {
                logger.error("Erro no menu de doadores", e);
                ConsoleUtils.imprimirErro("Erro: " + e.getMessage());
                aguardarEnter();
            }
        }
    }

    private void menuBeneficiarios() throws SQLException {
        boolean noMenu = true;
        while (noMenu) {
            limparTela();
            System.out.print(ConsoleUtils.formatarMenuBeneficiarios());
            String opcao = scanner.nextLine().trim();
            
            try {
                switch (opcao) {
                    case "0":
                        noMenu = false;
                        break;
                    case "1":
                        comandos.get("cadastrarBeneficiario").execute();
                        break;
                    case "2":
                        comandos.get("editarBeneficiario").execute();
                        break;
                    case "3":
                        comandos.get("listarBeneficiarios").execute();
                        break;
                    case "4":
                        comandos.get("removerBeneficiario").execute();
                        break;
                    default:
                        throw new IllegalArgumentException("Opção inválida");
                }
                if (noMenu) aguardarEnter();
            } catch (Exception e) {
                logger.error("Erro no menu de beneficiários", e);
                ConsoleUtils.imprimirErro("Erro: " + e.getMessage());
                aguardarEnter();
            }
        }
    }

    private void menuDoacoes() throws SQLException {
        boolean noMenu = true;
        while (noMenu) {
            limparTela();
            System.out.print(ConsoleUtils.formatarMenuDoacoes());
            String opcao = scanner.nextLine().trim();
            
            try {
                switch (opcao) {
                    case "0":
                        noMenu = false;
                        break;
                    case "1":
                        comandos.get("novaDoacao").execute();
                        break;
                    case "2":
                        comandos.get("listarDoacoes").execute();
                        break;
                    case "3":
                        comandos.get("distribuirDoacoes").execute();
                        break;
                    case "4":
                        comandos.get("concluirDoacao").execute();
                        break;
                    case "5":
                        comandos.get("cancelarDoacao").execute();
                        break;
                    case "6":
                        comandos.get("doacoesPorDoador").execute();
                        break;
                    case "7":
                        comandos.get("doacoesPorStatus").execute();
                        break;
                    default:
                        throw new IllegalArgumentException("Opção inválida");
                }
                if (noMenu) aguardarEnter();
            } catch (Exception e) {
                logger.error("Erro no menu de doações", e);
                ConsoleUtils.imprimirErro("Erro: " + e.getMessage());
                aguardarEnter();
            }
        }
    }

    private boolean contatoValido(String contato) {
        return PADRAO_EMAIL.matcher(contato).matches() ||
               PADRAO_TELEFONE.matcher(contato).matches();
    }

    private void limparTela() {
        ConsoleUtils.limparTela();
    }

    private void aguardarEnter() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}