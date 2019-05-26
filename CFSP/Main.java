import java.io.FileNotFoundException;
import java.util.Collections;

class Main{
    public static void main(String args[]) throws FileNotFoundException{

//        ConnectionDB db = new ConnectionDB("instances/problem1.txt");
    	ConnectionDB clearDB = new ConnectionDB();
    	clearDB.limparDB();
    	clearDB.conexaoOFF();
    	
    	int[] prodQtd = {1, 1, 4, 0, 1, 1, 0, 0, 2, 1, 0, 0, 1, 1, 0, 2, 1, 1, 3, 0};
		ConnectionDB db = new ConnectionDB("instances/problem1.txt");
		db.inserirPedido("Jorge", "2019-05-21", "2019-05-27", prodQtd);
		CFSP cfsp = new CFSP();
	    db.getDados(cfsp);
	    db.getPedido(cfsp);
	    System.out.println(cfsp);
	    SH sh = new SH(cfsp, 50, 10);
	    sh.run();
	    db.conexaoOFF();
//      db.limparDB();

    }
}