package practica1_Opti_mas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class Main {
	
	private static boolean imprimirDetalles = false;
	
    static int instancias = 20; // Representa el numero de instancias del problema de MMDP que se proponen.
    static int mejorIteracion;
    static float[][][] soluciones = new float[instancias][][];
    static float[] bestValues = new float[instancias];
    
	static long timeInMillis_Inicio = System.currentTimeMillis();
    static long timeInMillis_Intermedio;
    
    static String ruta;
    

    
	
	public static void main(String[] args) throws Exception {

		System.out.println("\n********************************************************" +	
							"\nPracticas de Optimizacion de Sistemas de Comunicaciones" +
							"\n------------------------------------------------------" +
							"\nProblema del Maximum Minimum Diversity Problem" +
							"\n*******************************************************\n");
		
		
		bestValues = Arrays.copyOf(lectorBestValues("instancias\\best_values.txt"), instancias);

		//System.out.println(Arrays.toString(bestValues[0]));
		

		
		/*********************
		 *  ITERATED GREEDY
		 *********************/

		// Iniciar el servicio de ejecucion de hilos:
		ExecutorService executorServiceIG = Executors.newCachedThreadPool();
        ExecutorCompletionService<float[][]> service = new ExecutorCompletionService<float[][]>(executorServiceIG);

        // Declarar hilos:
        IteratedGreedy[] ig = new IteratedGreedy[instancias];
        Future<float[][]>[] futureIG = new Future[instancias];
        
        // Poner en marcha los hilos:
        for (int i=0; i<instancias; i++)
        	{
        	System.out.println("\nLanzando Iterated Greedy con el problema numero " + (i+1));
        	
			ruta = "instancias\\GKD-Ic_" + (i+1) + "_n500_m50.txt";
        	ig[i] = new IteratedGreedy(ruta);
        	futureIG[i] = service.submit(ig[i]);
        	
	        soluciones[i] = futureIG[i].get();
	        
        	//bestValues[i][1] = (100 - ((soluciones[i][1][0] * 100) / bestValues[i][0]));
        	System.out.println("\nInstancia " + (i+1) + ":\tMinimo y sus vertices: " + Arrays.toString(soluciones[i][1]) + "\n\t\tMejor minimo conocido: " + bestValues[i] + "\tDesviacion: " + (100 - ((soluciones[i][1][0] * 100) / bestValues[i])) + " %\n\t\tSolucion: " + Arrays.toString(soluciones[i][0]));
	        }
        
        executorServiceIG.shutdown();
        
        // Recuperar soluciones de los hilos:
/*		for (int i=1; i<instancias; i++)
	        {
        	soluciones[i] = futureIG[i].get();
        	bestValues[i][1] = (soluciones[i][1][0] * 100) / bestValues[i][0];
        	System.out.println("\nInstancia " + (i+1) + ":\tMinimo y sus vertices: " + Arrays.toString(soluciones[i][0]) + "\nMejor minimo conocido: " + bestValues[i][0] + "\tDesviacion: " + bestValues[i][1] + "\n\t\tSolucion: " + Arrays.toString(soluciones[i][1]));
	        }
*/


        
        
		/*****************************************************
		*  Metaheuristica Variable Neighborhood Search
		* ***************************************************/
        
		
		// Iniciar el servicio de ejecucion de hilos:
		ExecutorService executorServiceVNS = Executors.newCachedThreadPool();
        ExecutorCompletionService<float[][]> serviceVNS = new ExecutorCompletionService<float[][]>(executorServiceVNS);

        // Declarar hilos:
        VNS[] vns = new VNS[instancias];
        Future<float[][]>[] futureVNS = new Future[instancias];
        
        // Poner en marcha los hilos:
        for (int i=0; i<instancias; i++)
        	{
        	System.out.println("\nLanzando VNS basico con el problema numero " + (i+1));
        	
			ruta = "instancias\\GKD-Ic_" + (i+1) + "_n500_m50.txt";
        	vns[i] = new VNS(ruta);
        	futureVNS[i] = serviceVNS.submit(vns[i]);
        	
	        soluciones[i] = futureVNS[i].get();
        	//bestValues[i][1] = 100 - (soluciones[i][1][0] * 100) / bestValues[i][0];
        	System.out.println("\nInstancia " + (i+1) + ":\tMinimo y sus vertices: " + Arrays.toString(soluciones[i][1]) + "\n\t\tMejor minimo conocido: " + bestValues[i] + "\tDesviacion: " + (100 - ((soluciones[i][1][0] * 100) / bestValues[i])) + " %\n\t\tSolucion: " + Arrays.toString(soluciones[i][0]));
        	}
        
        executorServiceVNS.shutdown();
        
        // Recuperar soluciones de los hilos:
/*        for (int i=0; i<instancias; i++)
	        {
	        soluciones[i] = futureVNS[i].get();
        	bestValues[i][1] = 100 - (soluciones[i][1][0] * 100) / bestValues[i][0];
        	System.out.println("\nInstancia " + (i+1) + ":\tMinimo y sus vertices: " + Arrays.toString(soluciones[i][1]) + "\n\t\tMejor minimo conocido: " + bestValues[i][0] + "\tDesviacion: " + bestValues[i][1] + "\n\t\tSolucion: " + Arrays.toString(soluciones[i][0]));
        	}
*/        
		
		
	    timeInMillis_Intermedio = System.currentTimeMillis();
	    System.out.println("\n\nTiempo empleado: " + ((int) (timeInMillis_Intermedio - timeInMillis_Inicio))/1000 + " seg(s)");

	}


	private static float[] lectorBestValues(String file)
		{
		float[] segundoNumero = new float[instancias];
		int i = 0;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String line = br.readLine();
			
			while(line != null)
				{

				StringTokenizer st = new StringTokenizer(line);
				String primerNumero = st.nextToken();
				segundoNumero[i] = Float.parseFloat(st.nextToken());
				
				//System.out.println(primerNumero + ", " + segundoNumero[i]);
				
				i++;
				line = br.readLine();
				}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("No puedo encontrar el fichero");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("No puedo leer el fichero");
			e.printStackTrace();
		}
		
		return segundoNumero;
	}


	private static void Pausa()
	{
	System.out.print("\nPulse cualquier tecla para continuar.\n\n");
    try { System.in.read();	}
    catch (IOException e) {	e.printStackTrace(); }
	}
	
}