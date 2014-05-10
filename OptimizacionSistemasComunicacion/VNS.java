package practica1_Opti_mas;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VNS implements Callable<float[][]>
{

	// Variables globales:
	
	private boolean imprimirDetalles = false;
	
	Matriz matriz;
	float[][] solucionInicial;// = null;
	float maxMin0 = 0;
	int m, n;
	int iteracion = 0;
	int veces; // = 10;
	int hilos = 1;
	int iteraciones = 75;
	int[] mejorIteracion = new int[hilos];
	int iteracionesSinCambios = 0;
	
	Random randomGenerator = new Random();
	int random;
	
	long timeInMillis_Inicio = System.currentTimeMillis();
    long timeInMillis_Intermedio;

	float[] mejorMaxMin = {0, 0, 0};
	float[] mejorSolucion;
	float[][] solucionCompleta = new float[2][3];
    float[][][] mejoresSoluciones = new float[hilos][2][];
	
    String ruta;
    
	// constructor
	public VNS(String _ruta)
		{
		ruta = _ruta;
		}
	
	@Override
	public float[][] call() throws Exception
	{		
		// VNS version 2 multihilo:
		
		// constructivo:
		// Crear matriz:
		matriz = new Matriz(ruta);
		m = matriz.m;
		n = matriz.n;
		solucionInicial = new float[2][m];
	    
	    float[][] solucionCompleta;
	    
      	MMDP mmdp = new MMDP(matriz.matriz, matriz.n, matriz.m);
      	solucionInicial = Arrays.copyOf(mmdp.call(), mmdp.call().length);
		
		
		if (imprimirDetalles == true)
			{
			System.out.println("Solucion de partida: " + Arrays.toString(solucionInicial[0]) + "\ncon un maxMin y vertices de: " + Arrays.toString(solucionInicial[1])/*maxMin*/ + "\n");
			System.out.println("ruta: " + ruta + "\nn: " + n + "\nm: " + m);
			}
		
		
		float[][][] soluciones = new float[hilos][2][m];
	    float[][] maxMin2 = new float[hilos][3];
		int[] iteracionHilos = new int[hilos];
	    
		for (int i=0; i<hilos; i++)
			{
			soluciones[i] = Arrays.copyOf(solucionInicial, solucionInicial.length);
			mejoresSoluciones[i] = Arrays.copyOf(solucionInicial, solucionInicial.length);
			}
		solucionCompleta = Arrays.copyOf(solucionInicial,  solucionInicial.length);


		ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorCompletionService<float[][]> service = new ExecutorCompletionService<float[][]>(executorService);

        MMDPFirstImprovement[] fi = new MMDPFirstImprovement[hilos];
		Future<float[][]>[] futureFI = new Future[hilos];
		
		// este bloque habra que iterarlo en un bucle que controle la variable "veces"
		//bucleExterno: for (int x = 1; /*x < veces*/; x++) // sin restriccion de parada: x puede sobrepasar el numero de elementos que tiene la solucion. Esto significa que permutara mas veces que elementos tiene la solucion.
		int x = 1;
		bucleExterno: do
		{
			bucleInterno: for (int j = 0; /*j < iteraciones*/; j++) // la variable j controla cuantas veces se repite el heuristico
			{
				// arrancar hilos:
				for (int i=0; i<hilos; i++)
		    		{
			    	fi[i] = new MMDPFirstImprovement(matriz, Arrays.copyOf(mejoresSoluciones[i][0], mejoresSoluciones[i][0].length), mejoresSoluciones[i][1][0], x, true);
			    	futureFI[i] = service.submit(fi[i]);
		    		}
		        
		        // recuperar soluciones de los hilos:
		        for (int i=0; i<hilos; i++)
		        	{
		        	soluciones[i] = futureFI[i].get();
		        	if (imprimirDetalles == true) { System.out.println("\n\nHl. " + i + ", itr. " + j + ":\tMinimo y sus vertices: " + Arrays.toString(soluciones[i][1]) + "\n\t\tSolucion: " + Arrays.toString(soluciones[i][0]) + "\n" + x); }
		    		}
		        
		        // Crear array con la mejor solucion de cada hilo, y obtener la mejor de ellas:
		        for (int i=0; i<hilos; i++)
		       		{
			        iteracion++;
		        	
		        	if (soluciones[i][1][0] > mejoresSoluciones[i][1][0])
		        		{
		        		mejoresSoluciones[i] = Arrays.copyOf(soluciones[i], soluciones[i].length);
		        		mejorIteracion[i] = j;
		        		iteracionesSinCambios = 0;
		        		}
		        	
		        	// Detectar estancamiento en algun optimo:
		        	else if(soluciones[i][1][0] == mejoresSoluciones[i][1][0])
			        	{
			        	iteracionesSinCambios++;
			        	if (iteracionesSinCambios >= 5)
				        	{
			        			if (imprimirDetalles == true)
				        			{ 
				        			System.out.println("\nEstancamiento en el hilo " + i + ", despues de " + iteracionesSinCambios + " iteraciones sin cambios");
				        			}
			        			
			        			iteracionesSinCambios = 0;
			        			
			        			if (x < n) { x++; }
			        			else { x = n; }
				        	}
			        	}
		       		}

				iteracion++;
				timeInMillis_Intermedio = System.currentTimeMillis();
				if ((((int) (timeInMillis_Intermedio - timeInMillis_Inicio)) / 1000) >= 60)
				{
					break bucleExterno;
				}
			}
		}
		while (1==1);
		
		executorService.shutdown();
		
		// Imprimir la mejor solucion de cada hilo:
		if (imprimirDetalles == true)
		{
			System.out.println();
			for (int i=0; i<hilos; i++)
			{
				System.out.println("\nMejor solucion del hilo " + i + ": " + "iteracion " + mejorIteracion[i] + "\n\tSolucion: " + Arrays.toString(mejoresSoluciones[i][0]) + "\n\tMaxMin y sus vertices:" + Arrays.toString(mejoresSoluciones[i][1]) + "\n");
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
		
		return solucionCompleta;
	}
	
	
	private void Pausa()
	{
	System.out.print("\nPulse cualquier tecla para continuar.\n\n");
    try { System.in.read();	}
    catch (IOException e) {	e.printStackTrace(); }
	}
	
}
