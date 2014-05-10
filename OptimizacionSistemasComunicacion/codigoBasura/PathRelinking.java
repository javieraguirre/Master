package practica1_Opti_mas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.Callable;


class PathRelinking implements Callable<float[][]> //Runnable
	{
	long timeInMillis_Inicio = System.currentTimeMillis();
	
	String ruta;
	
	
	// constructor:
	
	public PathRelinking(String _ruta)
		{
		ruta = _ruta;
		}

	
	public float[][] call() throws Exception //void run()
		{
		float[][] mejorSolucion;
	    float[][] solucionCompleta;
	    float[][] solucionIntermedia;
	    
		
		// Crear matriz:
		Matriz matriz = new Matriz(ruta);
		int m = matriz.m;
		int n = matriz.n;
		
	    
      	MMDP mmdpOrigen = new MMDP(matriz.matriz, matriz.n, matriz.m);
      	float[][] solucionOrigen = Arrays.copyOf(mmdpOrigen.call(), mmdpOrigen.call().length);
      	
      	//MMDPFirstImprovement fiOrigen = new MMDPFirstImprovement(matriz, solucionOrigen[0], solucionOrigen[1][0], 1, false);
      	//solucionOrigen = fiOrigen.call();
      	
      	System.out.println(Arrays.toString(solucionOrigen[0]) + "\n" + Arrays.toString(solucionOrigen[1]));
      	
      	
      	MMDP mmdpDestino = new MMDP(matriz.matriz, matriz.n, matriz.m);
      	float[][] solucionDestino = Arrays.copyOf(mmdpDestino.call(), mmdpDestino.call().length);
      	
      	//MMDPFirstImprovement fiDestino = new MMDPFirstImprovement(matriz, solucionDestino[0], solucionDestino[1][0], 1, false);
      	//solucionOrigen = fiDestino.call();
      	
      	System.out.println(Arrays.toString(solucionDestino[0]) + "\n" + Arrays.toString(solucionDestino[1]));
      	
      	
      	solucionIntermedia = Arrays.copyOf(solucionOrigen, solucionOrigen.length);
      	mejorSolucion = Arrays.copyOf(solucionOrigen, solucionOrigen.length);
		mejorSolucion[1] = calcularMM(matriz.getMatriz(), Arrays.copyOf(mejorSolucion, mejorSolucion.length));

		
      	
      	// aqui deberiamos de hacer una busqueda local a las 2 soluciones
      	// despues, deberiamos modificar el algoritmo y crear un refset.
      	

      	
      	// ahora, vamos a crear el path:
      	
      	for (int i = 0; i < m; i++) // iterar tantas veces como la longitud de la solucion
      		{
      		
      		if (mejorSolucion[0][i] > solucionDestino[0][i]) // caso en el que el vertice destino sea menor
	      		{
	      		for (int j = (int) mejorSolucion[0][i]; j > (int) solucionDestino[0][i]; j--) // iterar tantas veces como la diferencia entre destino - origen
		      		{
	      			mejorSolucion[0][i] --;
	      			solucionIntermedia[1] = calcularMM(matriz.getMatriz(), Arrays.copyOf(mejorSolucion, mejorSolucion.length));

	      			if (solucionIntermedia[1][0] > mejorSolucion[1][0])
	      				{
	      				mejorSolucion[0] = Arrays.copyOf(mejorSolucion[0], mejorSolucion[0].length); 
	      				mejorSolucion[1] = Arrays.copyOf(solucionIntermedia[1], solucionIntermedia[1].length);
	      				//maxMin = Arrays.copyOf(mejorSolucion[1], mejorSolucion[1].length);
	      				//maxMin[0] = mejorSolucion[1][0];
	      				System.out.println("> " + i + "\t" + j);
	      				}
		      		}
	      		}
      		if (mejorSolucion[0][i] < solucionDestino[0][i]) // caso en el que el vertice destino sea mayor
	      		{
	      		for (int j = (int) mejorSolucion[0][i]; j < ((int) solucionDestino[0][i] - 0); j++) // iterar tantas veces como la diferencia entre destino - origen
		      		{
	      			mejorSolucion[0][i] ++;
	      			solucionIntermedia[1] = calcularMM(matriz.getMatriz(), Arrays.copyOf(mejorSolucion, mejorSolucion.length));

	      			if (solucionIntermedia[1][0] > mejorSolucion[1][0])
	      				{
	      				mejorSolucion[0] = Arrays.copyOf(mejorSolucion[0], mejorSolucion[0].length); 
	      				mejorSolucion[1] = Arrays.copyOf(solucionIntermedia[1], solucionIntermedia[1].length);
	      				//maxMin = Arrays.copyOf(mejorSolucion[1], mejorSolucion[1].length);
	      				//maxMin[0] = mejorSolucion[1][0];
	      				System.out.println("< " + i + "\t" + j);
	      				}
		      		}
	      		}
      		/*if ((int) mejorSolucion[0][i] == (int) solucionDestino[0][i]) // caso en el que se ha alcanzado al vertice destino
	      		{
      			solucionIntermedia[1] = calcularMM(matriz.getMatriz(), Arrays.copyOf(mejorSolucion, mejorSolucion.length));

      			if (solucionIntermedia[1][0] > mejorSolucion[1][0])
      				{
      				mejorSolucion[0] = Arrays.copyOf(mejorSolucion[0], mejorSolucion[0].length); 
      				mejorSolucion[1] = Arrays.copyOf(solucionIntermedia[1], solucionIntermedia[1].length);
      				//maxMin = Arrays.copyOf(mejorSolucion[1], mejorSolucion[1].length);
      				//maxMin[0] = mejorSolucion[1][0];
      				System.out.println("= " + i);
      				}
      			}*/
      		}

      	System.out.println("\n" + Arrays.toString(mejorSolucion[0]) + "\n" + Arrays.toString(mejorSolucion[1]));
        return mejorSolucion;
        }
	
	private float[] calcularMM(float[][] matriz, float[][] solucion) // void Run()
	{
		float[] maxMin = new float[3]; maxMin[0] = 999999;
		float f = 0;
		float[] malo = {-1, 0, 0};
		//System.out.println("solucion: " + Arrays.toString(solucion) + ";\tsolucion.length: " + solucion.length);
	
		for(int x=0; x < solucion[0].length; x++)
		{		
			for(int y=0; y < solucion[0].length; y++)
			{
				// Comprobar vertices repetidos:
				if ((x != y) && (solucion[0][x] == solucion[0][y]))
				{
					//System.out.println("repetido: " + Arrays.toString(solucion));
	
					return malo;
				}
				
				//System.out.println(x + ": " + solucion[x] + ";\t" + y + ": " + solucion[y]);
				f = matriz[(int) solucion[0][x]][(int) solucion[0][y]];
	
				if ((f < maxMin[0]) && (f != 0))
					{
					maxMin[0] = f;
					maxMin[1] = solucion[0][x];
					maxMin[2] = solucion[0][y];
					}
			}
		}
		
		if (maxMin[0] == 999999) { maxMin[0] = -1; } // caso en el que se ha evaluado un vertice con el mismo.
		return maxMin;
	}
	
	
	
	private static void Pausa()
		{
    	System.out.print("\nPulse cualquier tecla para continuar.\n\n");
        try { System.in.read();	}
        catch (IOException e) {	e.printStackTrace(); }
		}
	}