package aor.bd.miniprojeto;

import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

public class Menu implements AutoCloseable {

public void MenuPrincipal(Scanner teclado) throws SQLException {


    int opcao = -1;

    do {

        System.out.println("----APP MUSIC----");
        System.out.println("O que pretende fazer?");
        System.out.println("1 - Adicionar Música");
        System.out.println("2 - Editar Música");
        System.out.println("3 - Remover Música");
        System.out.println("4 - Consultar detalhes de uma Música");
        System.out.println("0 - Sair");
        System.out.print("Opção: ");
        opcao = teclado.nextInt();

        switch (opcao){

            case 1 -> { MenuAdicionarMusica(teclado);
            }
            case 2 -> { MenuEditarMusica(teclado);
            }
            case 3 -> { // Remover
            }
            case 4 -> { //Consultar
            }
            case 0 -> System.out.println("A encerrar...");
            default -> System.out.println("Opção inválida! Tente novamente!");
        }

    }while (opcao != 0);
}

    @Override
    public void close() throws Exception {

    }

    public static void MenuAdicionarMusica(Scanner teclado) throws SQLException {

        teclado.nextLine();
        System.out.println("-----Adicionar Música-----");
        System.out.print("Titulo: ");
        String titulo = teclado.nextLine();
        System.out.print("Ano: ");
        int ano = teclado.nextInt();
        teclado.nextLine();
        System.out.print("Autor: ");
        String autor = teclado.nextLine();
        System.out.println("Pretende adicionar género?");
        System.out.println("1-Sim\n" + "2-Não");
        System.out.print("Opção: ");
        int opcao = teclado.nextInt();
        teclado.nextLine();
        if (opcao==1){
            System.out.print("Género: ");
            String genero = teclado.nextLine();
            AppMusica.adicionarMusicaComGenero(titulo,ano,autor,genero);
        }else {
            AppMusica.adicionarMusica(titulo,ano,autor);
        }

    }

    public static void MenuEditarMusica(Scanner teclado) throws SQLException {

        System.out.println("----Editar Música----");
        System.out.println("Qual o ID da música a editar?");
        System.out.print("ID: ");
        int id = teclado.nextInt();
        teclado.nextLine();
        System.out.println("Detalhes da música selecionada");
        Map<String,Object> musica = AppMusica.buscarMusicaPorId(id);

        if (musica != null){
            System.out.println("Titúlo atual: ->" +musica.get("titulo"));
            System.out.print("Novo titúlo: ");
            String tituloNovo = teclado.nextLine();
            AppMusica.editarTituloMusica(id,tituloNovo);
        }
    }
}
