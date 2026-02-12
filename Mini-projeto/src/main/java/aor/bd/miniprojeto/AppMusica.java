package aor.bd.miniprojeto;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AppMusica implements AutoCloseable {

    private final static String URL = "jdbc:postgresql://localhost:5432/projeto_musicas";
    private final static String USER = "postgres";
    private final static String PASSWORD = "Catara100.";
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

    public static void adicionarMusica(String titulo, int ano, String autor) throws SQLException{

        garantirExistenciaAutor(autor);

        // O ? serve como placeholder para os valores reais
        String sql = "INSERT INTO musica (titulo, anoMusica, autor_nome) VALUES (?, ?, ?)";

        int novoId = gerarProximoId();

        try(PreparedStatement stm = conn.prepareStatement(sql)){
            // Define os valores nos lugares dos pontos de interrogação
            stm.setInt(1, novoId);
            stm.setString(2, titulo);
            stm.setInt(3, ano);
            stm.setString(4, autor);

            int linhasAfetadas = stm.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Música adicionada com sucesso!");
            }
        }
    }

    public static void adicionarMusicaComGenero(String titulo, int ano, String nomeAutor, String nomeGenero) throws SQLException {
        int novoId = gerarProximoId(); // Usando o metodo que criamos antes

        // 1. Garante que as entidades pai existem para não dar erro de FK
        garantirExistenciaAutor(nomeAutor);
        garantirExistenciaGenero(nomeGenero);

        // 2. Insere na tabela 'musica'
        String sqlMusica = "INSERT INTO musica (id, titulo, anomusica, autor_nome) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sqlMusica)) {
            stm.setInt(1, novoId);
            stm.setString(2, titulo);
            stm.setInt(3, ano);
            stm.setString(4, nomeAutor);
            stm.executeUpdate();
        }
        // 3. Cria a ligação na tabela 'genero_musica'
        String sqlLigacao = "INSERT INTO genero_musica (genero_nome, musica_id) VALUES (?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sqlLigacao)) {
            stm.setString(1, nomeGenero);
            stm.setLong(2, novoId);
            stm.executeUpdate();
        }

        System.out.println("Música e Género associados com sucesso!");
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

    // Metodo para verificar se o genero existe. Se não existir, ele cria.
    private static void garantirExistenciaGenero(String nomeGenero) throws SQLException {
        String sql = "INSERT INTO genero (nome) VALUES (?) ON CONFLICT (nome) DO NOTHING";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, nomeGenero);
            stm.executeUpdate();
        }
    }

    public static Map<String, Object> buscarMusicaPorId(int idBusca) throws SQLException {
        String sql = "SELECT id, titulo, anoMusica, autor_nome FROM musica WHERE id = ?";

        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, idBusca);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> registro = new HashMap<>();
                    // Guardamos cada coluna no Map
                    registro.put("id", rs.getInt("id"));
                    registro.put("titulo", rs.getString("titulo"));
                    registro.put("ano", rs.getInt("anoMusica"));
                    registro.put("autor", rs.getString("autor_nome"));


                    return registro  ;
                }
            }
        }
        System.out.println("Música não encontrada");
        return null;
        // Caso não encontre nada
    }

    public static void editarTituloMusica(int idMusica, String novoTitulo) throws SQLException {
        // SQL para atualizar apenas o título de um ID específico
        String sql = "UPDATE musica SET titulo = ? WHERE id = ?";

        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, novoTitulo);
            stm.setInt(2, idMusica);

            int linhasAfetadas = stm.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Sucesso: O título da música ID " + idMusica + " foi atualizado para: " + novoTitulo);
            } else {
                System.out.println("Aviso: Nenhuma música encontrada com o ID " + idMusica);
            }
        }
    }

    public void deletarMusica(int idMusica) throws SQLException {
        String sql = "DELETE FROM musica WHERE id = ?";

        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, idMusica);

            int linhasAfetadas = stm.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Sucesso: A música com ID " + idMusica + " foi removida.");
            } else {
                System.out.println("Aviso: Nenhuma música encontrada com o ID " + idMusica + " para remover.");
            }
        }
    }

    public static void exibirDetalhesMusica(int idMusica) throws SQLException {
        // Reutiliza o metodo de busca criado antes
        Map<String, Object> musica = buscarMusicaPorId(idMusica);

        System.out.println("\n=== DETALHES DA MÚSICA ===");
        if (musica != null) {
            System.out.println("ID:        " + musica.get("id"));
            System.out.println("Título:    " + musica.get("titulo"));
            System.out.println("Ano:       " + musica.get("ano"));
            System.out.println("Autor:     " + musica.get("autor"));
            System.out.println("==========================\n");
        } else {
            System.out.println("Erro: Música com ID " + idMusica + " não encontrada na base de dados.");
            System.out.println("==========================\n");
        }
    }

    private static int gerarProximoId() throws SQLException {
        // Busca o maior ID atual na tabela musica
        String sql = "SELECT MAX(id) FROM musica";

        try (PreparedStatement stm = conn.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            if (rs.next()) {
                int maxId = rs.getInt(1); // Pega o valor da primeira coluna (MAX)
                return maxId + 1; // Incrementa 1
            }
        }
        // Se a tabela estiver vazia, retornamos 1 como o primeiro ID
        return 1;
    }
}


