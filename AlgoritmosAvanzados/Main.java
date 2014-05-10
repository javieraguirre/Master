package Practica1;


public class Main {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		
		String ruta = "C:\\Users\\Javi\\Android workspace\\JAguirrePracticasAA\\src\\Instancias\\harwellboeing\\impcol_b";
		Matriz miMatriz = new Matriz();
		miMatriz.generar(ruta);
		
		int n1 = miMatriz.getN1();
		int n2 = miMatriz.getN2();
		int[][] matriz = miMatriz.getMatriz();

		// MCLA:
		MCL algoritmoMCL = new MCL();
		algoritmoMCL.Run(matriz, n1, n2);
		

		

	}
}