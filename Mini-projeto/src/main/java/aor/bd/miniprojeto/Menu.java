package aor.bd.miniprojeto;

import java.util.Scanner;

public class Menu implements AutoCloseable {

public void MenuPrincipal(Scanner teclado){


    int opcao = -1;

    do {

        System.out.println("Bem vindo!");
        System.out.println("O que pretende fazer?");
        System.out.println("1 - Adicionar Música");
        System.out.println("2 - Editar Música");
        System.out.println("3 - Remover Música");
        System.out.println("4 - Consultar detalhes de uma Música");
        System.out.println("0 - Sair");
        System.out.print("Opção: ");
        opcao = teclado.nextInt();

        switch (opcao){

            case 1 -> { // adicionar musica
            }
            case 2 -> { //AppMusica.editarTituloMusica(2, teclado);// Editar
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
}
