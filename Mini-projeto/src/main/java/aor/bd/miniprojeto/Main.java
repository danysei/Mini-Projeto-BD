package aor.bd.miniprojeto;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static final Scanner teclado= new Scanner(System.in);

    public static void main(String[] args) {

        try (AppMusica app = new AppMusica()) {
            try(Menu menu = new Menu()){
                menu.MenuPrincipal(teclado);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        teclado.close();
    }

}
