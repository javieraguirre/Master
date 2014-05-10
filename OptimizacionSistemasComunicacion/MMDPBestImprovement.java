package practica1_Opti_mas;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

class MMDPBestImprovement implements Callable<float[][]>
{

	// Variables globales:
	private static boolean imprimirDetalles = false;
	
	Matriz matriz;
	static float[] solucion = null; //{1, 1, 1}; //null;
	float maxMin;
	int m, n;
	int iteracion = 0;
	
	int tamañoVecindario;
	float[][] vecindario;
	float[] mejorMaxMin = {0, 0, 0};
	float[] mejorSolucion;
	
	// Escritor de ficheros en sustitucion del array para el vecindario.
	PrintWriter out = new PrintWriter("c:\\mmdp-bi.txt");
    int numberOfLines = 0;
    
    
	// Constructor:
	
	public MMDPBestImprovement(Matriz _matriz, float[] _solucion, float _maxMin) throws Exception
	{
		matriz = _matriz;
		solucion = _solucion;
		maxMin = _maxMin;
		m = _solucion.length;
		n = matriz.n;
		//vecindario = new float[(int) (Math.pow(2, m)*2)-1][m];
		//vecindario = new float[1][m];
	}


	@Override
	public float[][] call() throws Exception
	{
		if (imprimirDetalles == true) { System.out.println("Solucion de partida: " + Arrays.toString(solucion) + ", con un maxMin de: " + maxMin); }

		// Crear vecindario:
		
		// Primera solucion: ningun elemento permutado:
		//vecindario[0] = Arrays.copyOf(solucion, m);
		out.println(Arrays.toString(solucion));

		// PERMUTACION ADITIVA:
		// Todas las demas soluciones menos la ultima: permutacion ordenada y alternada:
		permutarMas(Arrays.copyOf(solucion, m), 1, 1);
		
		// Ultima solucion: todos los elementos incrementados:
		//out.println();
		for (int i=0; i < m; i++)
		{
			if (solucion[i]+1 < n) { /*vecindario[(iteracion+1)][i] = solucion[i]+1;*/ out.print((solucion[i]+1) + ", "); }
			else { /*vecindario[(iteracion+1)][i] = solucion[i];*/ out.print(solucion[i] + ", "); }
		}
		out.println();
		
		// PERMUTACION SUSTRACTIVA:
		iteracion++;
		// Crear nuevas soluciones para el vecindario, pero esta vez restando 1:
		permutarMenos(Arrays.copyOf(solucion, m), 0, 1);
		
		// Ultima solucion: todos los elementos decrementados:
		//out.print("\n");
		for (int i = 0; i < m; i++)
		{
			if (solucion[i]-1 >= 0) { /*vecindario[(iteracion+1)][i] = solucion[i]-1;*/ out.print((solucion[i]-1) + ", "); }
			else { /*vecindario[(iteracion+1)][i] = solucion[i];*/ out.print(solucion[i] + ", "); }
		}
		
		out.close();
		
		
	    FileReader file_to_read = new FileReader ("c:\\mmdp-bi.txt");
	    BufferedReader bf = new BufferedReader (file_to_read);

	    String aLine;
	    numberOfLines = 0;

	    while (( aLine = bf.readLine()) != null )
	    {
	        numberOfLines ++;
	    }

	    bf.close ();
	    
		
		// Imprimir las soluciones y sus maximos minimos en pantalla:
		if (imprimirDetalles == true) { System.out.println("\nVecindario:"); }
		/*float[][] maxMinVecindario = new float[vecindario.length][3];
		for (int i=0; i < vecindario.length; i++)
		{
			maxMinVecindario[i] = calcularMM(matriz.getMatriz(), vecindario[i]);
			if (imprimirDetalles == true) { System.out.println(Arrays.toString(vecindario[i]) + ": " + Arrays.toString(maxMinVecindario[i])); }
		}*/
		
		
		float[][] maxMinVecindario = new float[numberOfLines][3];
		for (int i=0; i < numberOfLines; i++)
		{
			maxMinVecindario[i] = calcularMM(matriz.getMatriz(), vecindario(i));
			if (imprimirDetalles == true) { System.out.println(Arrays.toString(vecindario(i)) + ": " + Arrays.toString(maxMinVecindario[i])); }
		}

		
		// Elegir la mejor solucion del vecindario:
		/*for (int i=0; i < vecindario.length; i++)
		{
			if (maxMinVecindario[i][0] > mejorMaxMin[0])
			{
				mejorMaxMin = maxMinVecindario[i];
				mejorSolucion = vecindario[i];
			}
		}
		
		if (imprimirDetalles == true) { System.out.println("\nMejor solucion del vecindario: " + Arrays.toString(mejorSolucion) + "\n\tMaxMin y sus vertices: " + Arrays.toString(mejorMaxMin)); }
		*/
		
		for (int i=0; i < numberOfLines; i++)
		{
			if (maxMinVecindario[i][0] > mejorMaxMin[0])
			{
				mejorMaxMin = maxMinVecindario[i];
				mejorSolucion = vecindario(i);
			}
		}
		
		if (imprimirDetalles == true) { System.out.println("\nMejor solucion del vecindario: " + Arrays.toString(mejorSolucion) + "\n\tMaxMin y sus vertices: " + Arrays.toString(mejorMaxMin)); }
		
		
		
		
		float solucionCompleta[][] = new float[2][3];
		solucionCompleta[0] = mejorSolucion;
		solucionCompleta[1] = mejorMaxMin;
		return solucionCompleta;
	}


	private void permutarMas(float[] act, int j, int r) throws Exception
	{
		if (r >= 0)
		{
			for (int i = 0; i < m; i++)
			{				
				if ( /*(i != j) && (i >= 0) &&*/ (act[i] < n-1) && ((act[i]+1) - solucion[i] == 1) ) // falta añadir condicion de no repeticion de elementos.
				{
					act[i] += 1;
								
					boolean repetido = false;
					for (int k = 0; k < numberOfLines; k++) //for (int k = 0; k < vecindario.length; k++)
					{
						//if (Arrays.toString(vecindario[k]).equals(Arrays.toString(act))) { repetido = true;	}
						if (Arrays.toString(vecindario(k)).equals(Arrays.toString(act))) { repetido = true;	}
					}
					if (!repetido)
					{
						iteracion++;
						//vecindario[iteracion] = Arrays.copyOf(act, m);
						out.println(Arrays.toString(act));
					}
					repetido = false;
					permutarMas(Arrays.copyOf(act, m), i, r-1);
					act[i] -= 1;
				}
			}
		}
	}

	private void permutarMenos(float[] act, int j, int r) throws Exception
	{
		if (r >= 0)
		{
			for (int i = j; i < m; i++)
			{
				//System.out.println("aqui: " + act[i]);
				if ( /*(i != j) &&*/ (solucion[i] > 0) && ((solucion[i] - (act[i]-1)) == 1)  )
				{
					//System.out.print("i: " + i + "\tsol" + Arrays.toString(solucion) + "\tact" + Arrays.toString(act));
					act[i] -= 1;
					//System.out.println("\tact: " + Arrays.toString(act));
					boolean repetido = false;
					for (int k = 0; k < numberOfLines; k++) // for (int k = 0; k < vecindario.length; k++)
					{
						// if (Arrays.toString(vecindario[k]).equals(Arrays.toString(act))) { repetido = true;	}
						if (Arrays.toString(vecindario(k)).equals(Arrays.toString(act))) { repetido = true;	}
					}
					if (!repetido)
					{
						iteracion++;
						//vecindario[iteracion] = Arrays.copyOf(act, m);
						out.println(Arrays.toString(act));
					}
					repetido = false;
					permutarMenos(Arrays.copyOf(act, m), i, r-1);
					act[i] += 1;
				}
			}
		}
	}

	
	private float[] calcularMM(float[][] matriz, float[] solucion) // void Run()
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

	
	private float[] vecindario(int k) throws Exception
	{
		float[] s = new float[m];
		
	    FileReader file_to_read = new FileReader ("c:\\mmdp-bi.txt");
	    BufferedReader bf = new BufferedReader (file_to_read);
	    for(int i=0; i<(k-1); i++) { bf.readLine(); }
	    StringTokenizer st = new StringTokenizer(bf.readLine());
	    
	    for(int i=0; i<m; i++)
	    {
	    s[i] = Float.parseFloat(st.nextToken().replace("[", "").replace("]", "").replace(",", ""));
	    }
	    bf.close ();
		
		return s;
	}
	
}
