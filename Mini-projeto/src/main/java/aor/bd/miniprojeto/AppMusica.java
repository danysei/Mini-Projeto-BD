package aor.bd.miniprojeto;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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
        String sql = "INSERT INTO musica (id,titulo, anoMusica, autor_nome) VALUES (?, ?, ?, ?)";

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

    public static void associarAlbum(int musicaId, String nomeAlbum, int anoAlbum, int ordemNoAlbum) throws SQLException {
        // 1. Verificar se o álbum já existe pelo nome
        Integer albumId = null;
        String sqlBusca = "SELECT id FROM album WHERE nome = ?";

        try (PreparedStatement stm = conn.prepareStatement(sqlBusca)) {
            stm.setString(1, nomeAlbum);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    albumId = rs.getInt("id");
                }
            }
        }

        // 2. Se não existir, cria o álbum (Garante a existência)
        if (albumId == null) {
            albumId = gerarProximoIdAlbum();
            String sqlInsAlbum = "INSERT INTO album (id, nome, anoalbum) VALUES (?, ?, ?)";
            try (PreparedStatement stm = conn.prepareStatement(sqlInsAlbum)) {
                stm.setInt(1, albumId);
                stm.setString(2, nomeAlbum);
                stm.setInt(3, anoAlbum);
                stm.executeUpdate();
                System.out.println("Novo álbum '" + nomeAlbum + "' criado.");
            }
        }

        // 3. Atualizar a coluna 'ordem' na tabela 'musica' (que você já possui)
        String sqlUpdateOrdem = "UPDATE musica SET ordem = ? WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(sqlUpdateOrdem)) {
            stm.setInt(1, ordemNoAlbum);
            stm.setInt(2, musicaId);
            stm.executeUpdate();
        }

        // 4. Criar a ligação na tabela musica_album
        String sqlLigacao = "INSERT INTO musica_album (musica_id, album_id) VALUES (?, ?)";
        try (PreparedStatement stm = conn.prepareStatement(sqlLigacao)) {
            stm.setInt(1, musicaId);
            stm.setInt(2, albumId);
            stm.executeUpdate();
            System.out.println("Música associada ao álbum '" + nomeAlbum + "' na posição " + ordemNoAlbum + ".");
        }
    }

    // Metodo auxiliar para IDs de álbuns
    public static int gerarProximoIdAlbum() throws SQLException {
        String sql = "SELECT MAX(id) FROM album";
        try (PreparedStatement stm = conn.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            return rs.next() ? rs.getInt(1) + 1 : 1;
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

    // Metodo para verificar se o genero existe. Se não existir, ele cria.
    private static void garantirExistenciaGenero(String nomeGenero) throws SQLException {
        String sql = "INSERT INTO genero (nome) VALUES (?) ON CONFLICT (nome) DO NOTHING";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, nomeGenero);
            stm.executeUpdate();
        }
    }

    public static Map<String, Object> buscarMusicaPorId(int idBusca) throws SQLException {
        // SQL com JOIN para buscar tudo de uma vez
        String sql = "SELECT m.*, g.genero_nome, alb.nome AS album_nome " +
                "FROM musica m " +
                "LEFT JOIN genero_musica g ON m.id = g.musica_id " +
                "LEFT JOIN musica_album ma ON m.id = ma.musica_id " +
                "LEFT JOIN album alb ON ma.album_id = alb.id " +
                "WHERE m.id = ?";

        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, idBusca);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> registro = new HashMap<>();
                    registro.put("id", rs.getInt("id"));
                    registro.put("titulo", rs.getString("titulo").trim()); // .trim() para limpar espaços do CHAR
                    registro.put("ano", rs.getInt("anomusica"));
                    registro.put("autor", rs.getString("autor_nome"));
                    registro.put("ordem", rs.getInt("ordem"));

                    // Informações extras pedidas no enunciado
                    registro.put("genero", rs.getString("genero_nome"));
                    registro.put("album", rs.getString("album_nome"));

                    return registro;
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

    public static void deletarMusica(int idMusica) throws SQLException {
        // 1. Verificar se a música pertence a um álbum antes de apagar
        Integer idAlbum = null;
        String sqlCheck = "SELECT album_id FROM musica_album WHERE musica_id = ?";
        try (PreparedStatement stm = conn.prepareStatement(sqlCheck)) {
            stm.setInt(1, idMusica);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) idAlbum = rs.getInt("album_id");
        }

        // 2. Apagar primeiro as ligações (FKs) para não dar erro
        try (PreparedStatement stmG = conn.prepareStatement("DELETE FROM genero_musica WHERE musica_id = ?");
             PreparedStatement stmA = conn.prepareStatement("DELETE FROM musica_album WHERE musica_id = ?")) {
            stmG.setInt(1, idMusica); stmG.executeUpdate();
            stmA.setInt(1, idMusica); stmA.executeUpdate();
        }

        // 3. Apaga música
        String sqlDel = "DELETE FROM musica WHERE id = ?";
        try (PreparedStatement stm = conn.prepareStatement(sqlDel)) {
            stm.setInt(1, idMusica);
            if (stm.executeUpdate() > 0) {
                System.out.println("Música removida.");

                // 4. Se pertencia a álbum, verifica se ele ficou vazio
                if (idAlbum != null) {
                    String sqlCount = "SELECT COUNT(*) FROM musica_album WHERE album_id = ?";
                    try (PreparedStatement stmC = conn.prepareStatement(sqlCount)) {
                        stmC.setInt(1, idAlbum);
                        ResultSet rsC = stmC.executeQuery();
                        if (rsC.next() && rsC.getInt(1) == 0) {
                            try (PreparedStatement stmDA = conn.prepareStatement("DELETE FROM album WHERE id = ?")) {
                                stmDA.setInt(1, idAlbum);
                                stmDA.executeUpdate();
                                System.out.println("Álbum vazio removido.");
                            }
                        }
                    }
                }
            }
        }
    }

    public static void exibirDetalhesMusica(int idMusica) throws SQLException {
        // Reutiliza o metodo de busca
        Map<String, Object> musica = buscarMusicaPorId(idMusica);

        System.out.println("\n=== DETALHES DA MÚSICA ===");
        if (musica != null) {
            System.out.println("ID:        " + musica.get("id"));
            System.out.println("Título:    " + musica.get("titulo"));
            System.out.println("Ano:       " + musica.get("ano"));
            System.out.println("Autor:     " + musica.get("autor"));
            System.out.println("Gênero:    " + (musica.get("genero") != null ? musica.get("genero") : "N/A"));
            System.out.println("Álbum:     " + (musica.get("album") != null ? musica.get("album") : "N/A"));
            System.out.println("Ordem:     " + musica.get("ordem"));
            System.out.println("==========================\n");
        } else {
            System.out.println("Erro: Música com ID " + idMusica + " não encontrada na base de dados.");
            System.out.println("==========================\n");
        }
    }

    public static int gerarProximoId() throws SQLException {
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


