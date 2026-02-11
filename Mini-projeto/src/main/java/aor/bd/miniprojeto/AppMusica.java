package aor.bd.miniprojeto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppMusica implements AutoCloseable {

    private final static String URL = "jdbc:postgresql://localhost:5432/postgres";
    private final static String USER = "postgres";
    private final static String PASSWORD = "Catara100.";
    private Connection conn;


    public AppMusica() throws SQLException {
        this.conn = DriverManager.getConnection(AppMusica.URL, AppMusica.USER, AppMusica.PASSWORD);
    }


    @Override
    public void close() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }

    public static void main(String[] args) {
        try (AppMusica app = new AppMusica()) {
            System.out.println("Ligação com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


