package org.example;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private  static  final String connectionString = "jdbc:sqlite:banco.db";
    static void main() throws Exception{
        //Criar a tabela de alunos, caso ela não exista
        criartabela();

        Scanner scanner = new Scanner(System.in);

        int opcao = 0;
        do {
            exibirMenu();
            System.out.print("Digite a opção: ");
            opcao = scanner.nextInt();

            switch (opcao){
                case 1 -> {
                    System.out.print("Digite o nome: ");
                    scanner.nextLine(); //"descarta" esse \n sobrando
                    String nome = scanner.nextLine(); //lê a linha inteira digitada (até o Enter), incluindo espaços.

                    System.out.print("Digite o email: ");
                    String email = scanner.nextLine();

                    System.out.print("Digite a idade: ");
                    int idade = scanner.nextInt();

                    inserir(nome, email, idade);
                }

                case 2 -> listarAlunos();

                case 3 -> {
                    System.out.print("Digite o nome do aluno: ");
                    scanner.nextLine();
                    String nome = scanner.nextLine();
                    buscarAlunos(nome);
                }

                case 4 -> {
                    System.out.print("Digite o nome do aluno a atualizar: ");
                    scanner.nextLine();
                    String nome = scanner.nextLine();

                    System.out.print("Digite o novo email: ");
                    String email = scanner.nextLine();

                    System.out.print("Digite a nova idade: ");
                    int idade = scanner.nextInt();

                    atualizarAlunos(nome, email, idade);
                }

                case 5 -> {
                    System.out.print("Digite o nome do aluno a excluir: ");
                    scanner.nextLine();
                    String nome = scanner.nextLine();
                    excluirAluno(nome);
                }

                case 6 -> limparBanco();
            }
        }while(opcao != 0);


    }

     private static void criartabela() {
        String sql = """
                CREATE TABLE IF NOT EXISTS Alunos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    email TEXT NOT NULL,
                    idade INTEGER
                )
             """;

        try(var connection = DriverManager.getConnection(connectionString)){
            var statement = connection.createStatement();
            statement.execute(sql);
        }catch (SQLException e){
            System.out.println(("Erro ao abrir a conexão " + e.getMessage()));
        }
    }

    public static void exibirMenu(){
        System.out.println();
        System.out.println("====================================");
        System.out.println("         SISTEMA DE ALUNOS          ");
        System.out.println("====================================");
        System.out.println("1 - Cadastrar aluno");
        System.out.println("2 - Listar alunos");
        System.out.println("3 - Buscar aluno");
        System.out.println("4 - Atualizar aluno");
        System.out.println("5 - Excluir aluno");
        System.out.println("6 - Limpar banco");
        System.out.println("0 - Sair");
        System.out.println("==================================== \n");

    }

   

    private static void inserir(String nome, String email, int idade) {
        String sql = "INSERT INTO Alunos (nome, email, idade)";
        sql += String.format(" VALUES ('%s', '%s', %d)", nome, email, idade);

        try (var connection = DriverManager.getConnection(connectionString)){
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e){
            System.out.println(("Erro ao executar a inserção " + e.getMessage()));
        }
    }

    private static void listarAlunos() {
        String sql = """
            SELECT id, nome, email, idade from Alunos
            """;
        try (var connection = DriverManager.getConnection(connectionString)) {
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var nome = resultSet.getString("nome");
                var email = resultSet.getString("email");
                var idade = resultSet.getInt("idade");

                System.out.printf("Dados do Aluno: %s %s %s %s \n", id, nome, email, idade);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar " + e.getMessage());
        }
    }

    private static void buscarAlunos(String nome) {
        String sql = "SELECT id, nome, email, idade FROM Alunos WHERE nome = ?";

        try (var connection = DriverManager.getConnection(connectionString);
        var statement = connection.prepareStatement(sql)) {

            statement.setString(1, nome);
            var resultSet = statement.executeQuery();

            if (resultSet.next()){
                var id = resultSet.getInt("id");
                var email = resultSet.getString("email");
                var idade = resultSet.getInt("idade");
                System.out.println("\n=========================================================");
                System.out.printf("Aluno Encontrado: %d %s %s %d \n" , id, nome, email, idade);
                System.out.println("=========================================================");

            }else{
                System.out.println("Aluno Não encontrado!!");
            }
        
    } catch (SQLException e) {
            System.out.println("Erro ao buscar " + e.getMessage());
    }}


    private static void atualizarAlunos(String nomeAntigo, String novoEmail, int novaIdade ) {
        String sql = "UPDATE Alunos SET email = ?, idade = ? WHERE nome = ?";

            try (var connection = DriverManager.getConnection(connectionString);
            var statement = connection.prepareStatement(sql)) {
            statement.setString(1, novoEmail);
            statement.setInt(2, novaIdade);
            statement.setString(3, nomeAntigo);

            int linhasAfetadas = statement.executeUpdate(); 
            // executeUpdate() não retorna os dados, só a quantidade de linhas
            // que foram alteradas no banco pelo UPDATE/INSERT/DELETE.
            // Se for 0, significa que o WHERE não encontrou nenhum aluno
            // com esse nome, então nada foi atualizado.


            if (linhasAfetadas > 0) {
                System.out.println("Aluno atualizado com sucesso!!");
            }else{
                System.out.println("Aluno não encontrado!!");
            }

             } catch (SQLException e) {
            System.out.println("Erro ao atualizar " + e.getMessage());}
    }

    private static void excluirAluno(String nome){
        String sql = "DELETE FROM Alunos WHERE nome = ?";

        try (var connection = DriverManager.getConnection(connectionString);
        var statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);

            int linhasAfetadas = statement.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Aluno excluído com sucesso.");
            } else {
                System.out.println("Aluno não encontrado.");
            }
        

         } catch (SQLException e) {
        System.out.println("Erro ao excluir " + e.getMessage());
        }

    }

    private static void limparBanco() {
    String sql = "DELETE FROM Alunos";
    try (var connection = DriverManager.getConnection(connectionString)) {
        var statement = connection.createStatement();
        statement.executeUpdate(sql);
        System.out.println("Banco limpo com sucesso.");
    } catch (SQLException e) {
        System.out.println("Erro ao limpar " + e.getMessage());
    }
}
}