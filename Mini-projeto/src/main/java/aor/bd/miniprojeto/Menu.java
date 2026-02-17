package aor.bd.miniprojeto;

import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

public class Menu implements AutoCloseable {

    public void MenuPrincipal(Scanner teclado) throws SQLException {
        int opcao = -1;
        do {
            System.out.println("\n---- APP MUSIC ----");
            System.out.println("1 - Adicionar Música");
            System.out.println("2 - Editar Título");
            System.out.println("3 - Remover Música");
            System.out.println("4 - Consultar Detalhes");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");

            try {
                opcao = Integer.parseInt(teclado.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Insira um número válido.");
                continue;
            }

            switch (opcao) {
                case 1 -> MenuAdicionarMusica(teclado);
                case 2 -> MenuEditarMusica(teclado);
                case 3 -> MenuRemoverMusica(teclado);
                case 4 -> MenuExibirDetalhes(teclado);
                case 0 -> System.out.println("A encerrar...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    public static void MenuAdicionarMusica(Scanner teclado) throws SQLException {
        System.out.println("\n----- Adicionar Música -----");
        System.out.print("Título: ");
        String titulo = teclado.nextLine();
        System.out.print("Ano: ");
        int ano = 0;
        // Robustez: Garantir que o ano é um número
        try {
            ano = Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ano inválido. Operação cancelada.");
            return;
        }



        System.out.print("Autor: ");
        String autor = teclado.nextLine();

        // Género
        int opG = 0;
        while (opG != 1 && opG != 2) {
            System.out.print("Adicionar género? (1-Sim, 2-Não): ");
            try {
                opG = Integer.parseInt(teclado.nextLine());
            } catch (NumberFormatException e) { continue; }

            if (opG == 1) {
                System.out.print("Género: ");
                String genero = teclado.nextLine();
                AppMusica.adicionarMusicaComGenero(titulo, ano, autor, genero);
            } else if (opG == 2) {
                AppMusica.adicionarMusica(titulo, ano, autor);
            }
        }

        int idMusica = AppMusica.gerarProximoId() - 1;

        // Álbum [cite: 201]
        int opA = 0;
        while (opA != 1 && opA != 2) {
            System.out.print("Associar a álbum? (1-Sim, 2-Não): ");
            try {
                opA = Integer.parseInt(teclado.nextLine());
            } catch (NumberFormatException e) { continue; }

            int anoA = 0;

            if (opA == 1) {
                System.out.print("Nome do Álbum: ");
                String album = teclado.nextLine();
                while (anoA <1) {
                    System.out.print("Ano do Álbum: ");

                    // Robustez: Garantir que o ano é um número
                    try {
                        anoA = Integer.parseInt(teclado.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Ano inválido.");
                    }
                }

                System.out.print("Ordem no álbum: ");
                int ordem = Integer.parseInt(teclado.nextLine());
                AppMusica.associarAlbum(idMusica, album, anoA, ordem);
            }
        }
    }

    public static void MenuEditarMusica(Scanner teclado) throws SQLException {
        System.out.print("ID da música a editar: ");
        int id = Integer.parseInt(teclado.nextLine());
        Map<String, Object> musica = AppMusica.buscarMusicaPorId(id);

        if (musica != null) {
            System.out.println("Título atual: " + musica.get("titulo"));
            System.out.print("Novo título: ");
            String novo = teclado.nextLine();
            AppMusica.editarTituloMusica(id, novo);
        } else {
            System.out.println("Música não encontrada.");
        }
    }

    public static void MenuRemoverMusica(Scanner teclado) throws SQLException {
        System.out.print("ID da música a remover: ");
        int id = Integer.parseInt(teclado.nextLine());
        if (AppMusica.buscarMusicaPorId(id) != null) {
            AppMusica.deletarMusica(id);
        } else {
            System.out.println("Música não encontrada.");
        }
    }

    public static void MenuExibirDetalhes(Scanner teclado) throws SQLException {
        System.out.print("ID da música: ");
        int id = Integer.parseInt(teclado.nextLine());

        AppMusica.exibirDetalhesMusica(id);
    }

    @Override public void close() throws Exception {}
}