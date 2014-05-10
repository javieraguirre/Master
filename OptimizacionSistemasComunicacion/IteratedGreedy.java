package practica1_Opti_mas;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class IteratedGreedy implements Callable<float[][]>
{

	// Variables globales:
	
	private boolean imprimirDetalles = false;
	
	// Variables propias del problema:
	String ruta;
    //static Matriz miMatriz;
	Matriz matriz;
	int m, n;
	
	// Variables de los hilos y las iteraciones:
	int iteracion = 0;
	static int hilos = 1;
	int iteraciones = 100;
	int[] mejorIteracion = new int[hilos];
	int iteracionesSinCambios = 0;
	
	// Random y tiempo:
	Random randomGenerator = new Random();
	int random;
	long timeInMillis_Inicio = System.currentTimeMillis();
    long timeInMillis_Intermedio;	

    // variables donde almacenar las soluciones intermedias y finales:
	float[][] solucionLocal;
	float[][] solucionCompleta;
	float[][] solucionInicial = null;
    float[][][] soluciones = new float[hilos][2][];
    float[][][] mejoresSoluciones = new float[hilos][2][];

	
    //MMDP mmdp;
    
	// Constructor:
    
    //public IteratedGreedy(Matriz _matriz, float[][] _solucion, String _ruta)
    public IteratedGreedy(String _ruta)
	{
		/*matriz = _matriz;
		solucionInicial = Arrays.copyOf(_solucion, _solucion.length);
		m = _solucion[0].length;
		n = matriz.n;*/
		ruta = _ruta;
		}
	
    
	@Override
	public float[][] call() throws Exception
	{		
		
		// Algoritmo constructivo:
		//****************************************
		
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
		
		
		// ************************************
		// Iterated greedy version 2 multihilo:
		// ************************************
		// Este algoritmo deconstruye las soluciones con el metodo de agujereo.
		// Escapa de los optimos locales aplicando el metodo del recorte.
		
		// Iniciar los arrays con las soluciones iniciales:
		for (int i=0; i<hilos; i++)
		{
			soluciones[i] = Arrays.copyOf(solucionInicial, solucionInicial.length);
			mejoresSoluciones[i] = Arrays.copyOf(solucionInicial, solucionInicial.length);
		}
		solucionCompleta = Arrays.copyOf(solucionInicial,  solucionInicial.length);
		
		
		// Iniciar el servicio de ejecucion de hilos:
		ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorCompletionService<float[][]> service = new ExecutorCompletionService<float[][]>(executorService);

        // Declarar hilos:
        MMDPFirstImprovement[] fi = new MMDPFirstImprovement[hilos];
		Future<float[][]>[] futureFI = new Future[hilos];
        
		bucleExterno: for (int j = 0; /*j<3 /*j < iteraciones*/; j++) // la variable j controla cuantas veces se repite el heuristico
		{
	        // Poner en marcha los hilos:
	        for (int i=0; i<hilos; i++)
	        	{
	        	random = randomGenerator.nextInt(m-1); random++; // Esto lo hacemos para evitar el 0.
	        	//System.out.println(random);
	        	fi[i] = new MMDPFirstImprovement(matriz, Arrays.copyOf(mejoresSoluciones[i][0], mejoresSoluciones[i][0].length), mejoresSoluciones[i][1][0], random, false);
	        	futureFI[i] = service.submit(fi[i]);
	        	}
	        
			// Recuperar soluciones de los hilos:
	        for (int i=0; i<hilos; i++)
		        {
	        	soluciones[i] = futureFI[i].get();
	        	if (imprimirDetalles == true) { System.out.println("\n\nHl. " + i + ", itr. " + j + ":\tMinimo y sus vertices: " + Arrays.toString(soluciones[i][1]) + "\n\t\tSolucion: " + Arrays.toString(soluciones[i][0])); }
		        }
	        
	        // Crear array con la mejor solucion de cada hilo, y obtener la mejor de ellas:
	        for (int i=0; i<hilos; i++)
	       		{
		        iteracion++;
	        	
	        	if (soluciones[i][1][0] > mejoresSoluciones[i][1][0])
	        		{
	        		mejoresSoluciones[i][0] = Arrays.copyOf(soluciones[i][0], soluciones[i][0].length);
	        		mejoresSoluciones[i][1] = Arrays.copyOf(soluciones[i][1], soluciones[i][1].length);
	        		mejorIteracion[i] = j;
	        		iteracionesSinCambios = 0;
	        		}
	        	
	        	// Detectar estancamiento en algun optimo:
	        	else if(soluciones[i][1][0] == mejoresSoluciones[i][1][0])
		        	{
		        	iteracionesSinCambios++;
		        	if (iteracionesSinCambios >= 20)
			        	{
		        			random = randomGenerator.nextInt(m-1); random++;

		        			if (mejoresSoluciones[i][1][0] > solucionCompleta[1][0])
			        			{
			        			solucionCompleta = Arrays.copyOf(mejoresSoluciones[i], mejoresSoluciones[i].length);
			        			}
		        			
		        			mmdp = new MMDP(matriz.matriz, matriz.n, matriz.m, Arrays.copyOf(soluciones[i][0], random));
		        			mejoresSoluciones[i] = Arrays.copyOf(mmdp.call(), mmdp.call().length);

		        			if (imprimirDetalles == true)
			        			{ 
			        			System.out.println("\nEstancamiento en el hilo " + i + ", despues de " + iteracionesSinCambios + " iteraciones sin cambios" + "\n" + //);
			        			/*System.out.println(*/ "Destruiremos con un random de " + random + ":\n" + Arrays.toString(soluciones[i][0]) + "\n" + Arrays.toString(soluciones[i][1]) + "\n" + //);
			        			/*System.out.println(*/ "Reconstruido:\n" + Arrays.toString(mejoresSoluciones[i][0]) + "\n" + Arrays.toString(mejoresSoluciones[i][1]));
			        			}
		        			
		        			iteracionesSinCambios = 0;
			        	}
		        	}
	       		}
	        
	        
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
 
		
		if (imprimirDetalles == true) { System.out.println("Iteraciones: " + iteracion + " iteraciones * " + hilos + " hilos"); }

		
		
		// ***********************************
		// Iterated Greedy version 1 monohilo:
		// ***********************************
		
		/*for (int i = 0; i < 50; i++)
		{
			MMDPFirstImprovement firstImp = new MMDPFirstImprovement(matriz, Arrays.copyOf(solucion0, solucion0.length), maxMin[0]);
			solucionCompleta = firstImp.call();
			
			if (solucionCompleta[1][0] > maxMin[0])
			{
				solucion0 = Arrays.copyOf(solucionCompleta[0],  solucionCompleta[0].length);
				maxMin = Arrays.copyOf(solucionCompleta[1], solucionCompleta[1].length);
			}
		}*/
		
		return solucionCompleta;
	}
	

	
	
/*	float[] permutarAleatorio(float[] _solucion1, int pos, float[] _maxMin0)
	{
		float[] solucion1 = _solucion1;
//		float[] maxMin0 = _maxMin0;
//		float[] maxMin1;
		boolean repetido = false;
//		int posVerticeRepetido;
//		int verticeRepetido;

		// Primero, se elige un vertice aleatorio que sustituir en la solucion:
		random = randomGenerator.nextInt(n);
		
		// Segundo, se comprueba que ese vertice no pertenece ya a la solucion:
		for (int i=0; i<m; i++)
		{
			if (solucion1[i] == random)
			{
				repetido = true;
			}	
		}
		
		// Tercero; si no esta en la solucion, colocarlo:
		if (!repetido)
		{
			solucion1[pos] = random;
		}
		
		//System.out.println(Arrays.toString(solucion1));
		return solucion1;
	}
	*/
	

/*	private float[] calcularMM(float[][] matriz, float[] solucion) // void Run()
	{
	float[] maxMin = new float[3]; maxMin[0] = 999999;
	float f = 0;
	float[] malo = {-1, 0, 0};
	//System.out.println("solucion: " + Arrays.toString(solucion) + ";\tsolucion.length: " + solucion.length);

	for(int x=0; x < solucion.length; x++)
	{		
		for(int y=0; y < solucion.length; y++)
		{
			// Comprobar vertices repetidos:
			if ((x != y) && (solucion[x] == solucion[y]))
			{
				//System.out.println("repetido: " + Arrays.toString(solucion));

				return malo;
			}
			
			//System.out.println(x + ": " + solucion[x] + ";\t" + y + ": " + solucion[y]);
			f = matriz[(int) solucion[x]][(int) solucion[y]];

			if ((f < maxMin[0]) && (f != 0))
				{
				maxMin[0] = f;
				maxMin[1] = solucion[x];
				maxMin[2] = solucion[y];
				}
		}
	}
	
	if (maxMin[0] == 999999) { maxMin[0] = -1; } // caso en el que se ha evaluado un vertice con el mismo.
	return maxMin;
	}
*/
	
	private static void Pausa()
	{
		System.out.print("\nPulse cualquier tecla para continuar.\n\n");
	    try { System.in.read();	}
	    catch (IOException e) {	e.printStackTrace(); }
	}
	
}
