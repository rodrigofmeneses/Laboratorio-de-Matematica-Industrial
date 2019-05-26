

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionDB {
	
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	
	public int qtdProdutos;
	public int qtdMaquinas;
	private Scanner sc;
	
	// conexoes ----
	
	public ConnectionDB() {
		connect();
	}
	
	public ConnectionDB(String path) throws FileNotFoundException {
		this();
		sc = new Scanner(new FileInputStream(path));
        sc.useLocale(Locale.US);

        qtdProdutos = sc.nextInt();
        qtdMaquinas = sc.nextInt();
        
        inserirProduto();
        inserirMaquina();
        inserirProducao();
        
	}
	
	public void connect() {
		String DRIVER = "com.mysql.cj.jdbc.Driver";
		String SERVER = "jdbc:mysql://localhost:3306/db_cfsp?useTimezone=true&serverTimezone=UTC";
		String USER = "root";
		String PASS = "Hux084207599";
		try {
			Class.forName(DRIVER);
			this.connection = DriverManager.getConnection(SERVER, USER, PASS);
			this.statement = this.connection.createStatement();
			
			
		} catch (Exception e) {
			System.out.println("ERRO: " + e.getMessage());
			
		}
		
		qtdProdutos = 0;
		try {
			String query = "SELECT * FROM produto;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			while(this.resultSet.next()) {
				qtdProdutos++;
			}
			
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
		
		qtdMaquinas = 0;
		try {
			String query = "SELECT * FROM maquina;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			while(this.resultSet.next()) {
				qtdMaquinas++;
			}
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	
	public void getDados(CFSP cfsp) {
		cfsp.M = qtdMaquinas;
        cfsp.N = qtdProdutos;
        cfsp.p = new int[cfsp.N][cfsp.M];
        getProducao(cfsp.p);
	}
	
	public void getPedido(CFSP cfsp) {
		int idPedido = getQtdPedidos();
		try {
			cfsp.prodQtd = new int[qtdProdutos];
			Arrays.fill(cfsp.prodQtd, 0);
			String query = "SELECT * FROM descricao WHERE idPedido = '" + idPedido + "' ORDER BY idProduto;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			while(this.resultSet.next()) {
				cfsp.prodQtd[this.resultSet.getInt("idProduto") - 1] = this.resultSet.getInt("quantidade");
				
			}								
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	public int getQtdPedidos() {
		int qtdPedidos = 0;
		try{
			String query = "SELECT * FROM pedido;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			while(this.resultSet.next()) {
				qtdPedidos++;
			}	
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
		return qtdPedidos;
	}
	
	
	public void getProducao(int [][] p) {
		try {
			String query = "SELECT * FROM producao ORDER BY idMaquina;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			for(int i = 0; i < qtdMaquinas; i++) {
				for(int j = 0; j < qtdProdutos; j++) {
		            this.resultSet.next();
		           	p[j][i] = resultSet.getInt("custo");
		        }
			}					
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	
	

	
	
	public void inserirMaquina() {
		
		for(int i = 1; i <= qtdMaquinas; i++) {
			try {
				String query = "INSERT INTO maquina (nome) VALUES ('maquina " + i + "');";
				this.statement.executeUpdate(query);
			}catch(Exception e) {
				System.out.println("ERRO: " + e.getMessage());
			}
		}
		
		
	}
	
	public void inserirProduto() {
		int[] idx = new int[qtdMaquinas];

		  for(int i = 0; i < qtdMaquinas; i++) {
		      idx[i] = i;
		  }
		for(int i = 1; i <= qtdProdutos; i++) {
			try {
				Utils.shuffler(idx);
				String query = "INSERT INTO produto (nome , sequencia) VALUES ('Produto " + i + "', '" + Arrays.toString(idx) + "');";
				this.statement.executeUpdate(query);
				
			}catch(Exception e) {
				System.out.println("ERRO: " + e.getMessage());
			}
		}
	}
	
	public void inserirProducao() {
            		
		for(int i = 1; i <= qtdProdutos; i++) {
			for(int j = 1; j <= qtdMaquinas; j++) {
				try {
					
					String query = "INSERT INTO producao (idProduto, idMaquina, custo) VALUES ('" + i + "', '" + j + "', '" + sc.nextInt() + "');";
					this.statement.executeUpdate(query);
				}catch(Exception e) {
					System.out.println("ERRO: " + e.getMessage());
				}
			}
			
		}
		
		sc.close();
	}
	
	
	public void inserirPedido(String cliente, String dataPedido, String dataEntrega, int idProdutoQuantidade[]) {
		int idPedidos = 0;
		try {
			String query = "INSERT INTO pedido (cliente, dataPedido, dataEntrega, fluxo, tempoTotal, atendeuPedido) VALUES ('" + cliente + "', '" + dataPedido + "', '" + dataEntrega + "', '" + 0 + "', '" + 0 + "', '" + 0 + "' );";
			this.statement.executeUpdate(query);
			query = "SELECT * FROM pedido;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			while(this.resultSet.next()) {
				idPedidos++;
			}
			for(int i = 0; i < idProdutoQuantidade.length; i++){
				if(idProdutoQuantidade[i] > 0) {
					query = "INSERT INTO descricao (idPedido, idProduto, quantidade) VALUES ('" + idPedidos + "', '" + (i+1) + "', '" + idProdutoQuantidade[i] + "');";
					this.statement.executeUpdate(query);
				}
			}
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
		
	}
	
	public void atualizarPedido(Sol s) {
		int atendeu = 0;
		int tempoTotal = s.calcDias();
		try {
			String query = "SELECT DATEDIFF(dataEntrega, dataPedido) FROM pedido where idPedido = '" + getQtdPedidos() + "';";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			this.resultSet.next();
			if(tempoTotal <= this.resultSet.getInt(1)) {
				atendeu = 1;
			}
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
		
		try {
			String query = "UPDATE pedido SET fluxo = '" + Arrays.toString(s.s) + "', tempoTotal = '" + tempoTotal + "', atendeuPedido = '" + atendeu + "' WHERE idPedido = '" + getQtdPedidos() + "';";
			this.statement.executeUpdate(query);
		}catch(Exception e) {
			System.out.println("AEWWW");
			System.out.println("ERRO: " + e.getMessage());
		}
	}	
	
	
	public void listarProdutos() {
		
		try {
			String query = "SELECT * FROM produto;";
			this.resultSet = this.statement.executeQuery(query);
			this.statement = this.connection.createStatement();
			while(this.resultSet.next()) {
				System.out.println("ID: " + this.resultSet.getString("idProduto") + "nome: " + this.resultSet.getString("nome") + "sequencia: " + this.resultSet.getString("sequencia"));
			}
			
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}	
	
	
	public void editarProduto(int idProduto, String nome, int sequencia[]) {
		try {
			String query = "UPDATE produto SET nome = '" + nome + "', sequencia = '" + sequencia + "' WHERE idProduto = " + idProduto + ";";
			this.statement.executeUpdate(query);
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	public void deletarProduto(int idProduto) {
		try {
			String query = "DELETE FROM produto WHERE idProduto = " + idProduto + ";";
			this.statement.executeUpdate(query);
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	

	public void limparDB() {
		try {
			
			String query = "TRUNCATE TABLE produto";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE producao";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE pedido";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE maquina";
			this.statement.executeUpdate(query);
			query = "TRUNCATE TABLE descricao";
			this.statement.executeUpdate(query);
			
		}catch(Exception e){
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
	
	public boolean ConexaoON() {
		if(this.connection != null) {
			return true;
		}else {
			return false;
		}
	}
	
	public void conexaoOFF() {
		try {
			this.connection.close();
		}catch(Exception e) {
			System.out.println("ERRO: " + e.getMessage());
		}
	}
	
}
	
	
	
	