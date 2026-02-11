package aor.bd.miniprojeto;

import java.sql.*;

public class AppMusica implements AutoCloseable {

    private final static String URL = "jdbc:postgresql://localhost:5455/projeto_musicas";
    private final static String USER = "postgres";
    private final static String PASSWORD = "postgres";
    private static Connection conn;


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
            adicionarMusica(3, "Deslocado", 1978, "Napa");

            //adicionarAutor("Roberto Carlos");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void queryAutor  () throws SQLException {
        String sql = "SELECT * FROM autor";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {

            try (ResultSet rs1 = stm.executeQuery()) {
                while (rs1.next()) {
                    System.out.println("Nome: " + rs1.getString("nome"));
                }
            }
        }

    }

    public static void adicionarMusica(int id,String titulo, int ano, String autor) throws SQLException{

        garantirExistenciaAutor(autor);

        // O ? serve como placeholder para os valores reais
        String sql = "INSERT INTO musica (id ,titulo, anoMusica, autor_nome) VALUES (?,?, ?, ?)";

        try(PreparedStatement stm = conn.prepareStatement(sql)){
            // Define os valores nos lugares dos pontos de interrogação
            stm.setInt(1, id);
            stm.setString(2, titulo);
            stm.setInt(3, ano);
            stm.setString(4, autor);

            int linhasAfetadas = stm.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Música adicionada com sucesso!");
            }
        }
    }

    // Metodo para verificar se o autor existe. Se não existir, ele cria.
    public static void garantirExistenciaAutor(String nomeAutor) throws SQLException {
        String sqlBusca = "SELECT nome FROM autor WHERE nome = ?";

        try (PreparedStatement stmBusca = conn.prepareStatement(sqlBusca)) {
            stmBusca.setString(1, nomeAutor);
            ResultSet rs = stmBusca.executeQuery();

            if (!rs.next()) {
                // Se o ResultSet estiver vazio, o autor não existe. Vamos inserir:
                String sqlInsertAutor = "INSERT INTO autor (nome) VALUES (?)";
                try (PreparedStatement stmInsert = conn.prepareStatement(sqlInsertAutor)) {
                    stmInsert.setString(1, nomeAutor);
                    stmInsert.executeUpdate();
                    System.out.println("Autor '" + nomeAutor + "' adicionado.");
                }
            }
        }
    }


}


