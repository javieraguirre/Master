package practica1_Opti_mas;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IteratedGreedyRecorte implements Callable<float[][]>
{

	// Variables globales:
	
	private static boolean imprimirDetalles = false;
	
	// Variables propias del problema:
	Matriz matriz;
	int m, n;
	
	// Variables de los hilos y las iteraciones:
	int iteracion = 0;
	static int hilos = 1;
	int iteraciones = 100;
	int[] mejorIteracion = new int[hilos];
	
	// Random y tiempo:
	Random randomGenerator = new Random();
	int random;
	static long timeInMillis_Inicio = System.currentTimeMillis();
    static long timeInMillis_Intermedio;	

    // variables donde almacenar las soluciones intermedias y finales:
	float[] mejorSolucion;
	float[][] solucionCompleta;
	float[][] solucionInicial = null;
	//float[] maxMin = new float[3];
    static float[][][] soluciones = new float[hilos][2][];
    float[][][] mejoresSoluciones = new float[hilos][2][];
	
    
	// Constructor:
	
    /*public IteratedGreedyRecorte(Matriz _matriz, float[] _solucion, float _maxMin)
	{ // constructor antiguo. el float _maxMin nos lo podemos ahorrar.
		matriz = _matriz;
		solucionInicial = _solucion;
		maxMin[0] = _maxMin;
		m = _solucion.length;
		n = matriz.n;
		solucionCompleta = new float[2][n];
		}*/
    
    public IteratedGreedyRecorte(Matriz _matriz, float[][] _solucion)
	{
		matriz = _matriz;
		solucionInicial = Arrays.copyOf(_solucion, _solucion.length);
		m = _solucion[0].length;
		n = matriz.n;
		solucionCompleta = new float[2][n];
		}
    
	
	@Override
	public float[][] call() throws Exception
	{		
		if (imprimirDetalles == true) { System.out.println("Solucion de partida: " + Arrays.toString(solucionInicial[0]) + "\ncon un maxMin y vertices de: " + Arrays.toString(solucionInicial[1])/*maxMin*/ + "\n"); }
		
		// ************************************
		// Iterated greedy version 1 multihilo:
		// ************************************
		
		// Iniciar los arrays con las soluciones iniciales:
		for (int i=0; i<hilos; i++)
		{
			soluciones[i] = Arrays.copyOf(solucionInicial, solucionInicial.length);
			mejoresSoluciones[i] = Arrays.copyOf(solucionInicial, solucionInicial.length);
		}
		solucionCompleta = Arrays.copyOf(solucionInicial,  solucionInicial.length);
		//solucionCompleta[1][0] = maxMin[0];
		
		// Iniciar el servicio de ejecucion de hilos:
		ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorCompletionService<float[][]> service = new ExecutorCompletionService<float[][]>(executorService);

        // Declarar hilos:
        MMDP[] mmdp = new MMDP[hilos];
		Future<float[][]>[] futureMMDP = new Future[hilos];
        
		bucleExterno: for (int j = 0; /*j < 3 /*iteraciones*/; j++) // la variable j controla cuantas veces se repite el heuristico
		{
	        // Poner en marcha los hilos:
	        for (int i=0; i<hilos; i++)
	        	{
	        	random = randomGenerator.nextInt(m-1); random++; // Arbitrario que evita el 0.
	        	mmdp[i] = new MMDP(matriz.matriz, matriz.n, matriz.m, Arrays.copyOf(mejoresSoluciones[i][0], random));
	        	futureMMDP[i] = service.submit(mmdp[i]);
	        	}
	        
			// Recuperar soluciones de los hilos:
	        for (int i=0; i<hilos; i++)
		        {
	        	soluciones[i] = futureMMDP[i].get();
	        	if (imprimirDetalles == true) { System.out.println("\n\nHl. " + i + ", itr. " + j + ":\tMinimo y sus vertices: " + Arrays.toString(soluciones[i][1]) + "\n\t\tSolucion: " + Arrays.toString(soluciones[i][0])); }
		        }
	        
	        // Crear array con la mejor solucion de cada hilo, y obtener la mejor de ellas:
	        for (int i=0; i<hilos; i++)
	       		{
	        	if (soluciones[i][1][0] > mejoresSoluciones[i][1][0])
	        		{
	        		mejoresSoluciones[i][0] = Arrays.copyOf(soluciones[i][0], soluciones[i][0].length);
	        		mejoresSoluciones[i][1] = Arrays.copyOf(soluciones[i][1], soluciones[i][1].length);
	        		mejorIteracion[i] = j;
	        		}
	       		}
	        
	        iteracion++;
	        
			timeInMillis_Intermedio = System.currentTimeMillis();
			if ((((int) (timeInMillis_Intermedio - timeInMillis_Inicio)) / 1000) >= 60)
			{
				break bucleExterno;
			}
	        
		}
		// Cerrar el servicio de ejecucion de hilos:
		executorService.shutdown();	
		
		// Imprimir la mejor solucion de cada hilo:
		if (imprimirDetalles == true)
		{ 
			for (int i=0; i<hilos; i++)
			{
				System.out.println("\n\nMejor solucion del hilo " + i + ": " + "iteracion " + mejorIteracion[i] + "\n\tSolucion: " + Arrays.toString(mejoresSoluciones[i][0]) + "\n\tMaxMin y sus vertices:" + Arrays.toString(mejoresSoluciones[i][1]) + "\n");
			}
		}
		
		// Elegir la mejor solucion de todas:
		for (int i=0; i<hilos; i++)
		{
			if (mejoresSoluciones[i][1][0] > solucionCompleta[1][0])
			{
				solucionCompleta[0] = Arrays.copyOf(mejoresSoluciones[i][0],  mejoresSoluciones[i][0].length);
				solucionCompleta[1] = Arrays.copyOf(mejoresSoluciones[i][1], mejoresSoluciones[i][1].length);
			}
		}
 
		
		if (imprimirDetalles == true) { System.out.println("Iteraciones: " + iteracion + " iteraciones * " + hilos + " hilos"); }

		
		return solucionCompleta;
	}
	


	
	private static void Pausa()
	{
		System.out.print("\nPulse cualquier tecla para continuar.\n\n");
	    try { System.in.read();	}
	    catch (IOException e) {	e.printStackTrace(); }
	}
	
}
