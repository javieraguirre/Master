package practica1_Opti_mas;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;

public class MMDPFirstImprovement implements Callable<float[][]>
{

	// Variables globales:
	
	private boolean imprimirDetalles = false;
	
	Matriz matriz;
	float[] solucion0 = null;
	float[] maxMin = new float[3];
	int m, n;
	int iteracion = 0;
	
	Random randomGenerator = new Random();
	int random;
	
	int tamañoVecindario;
	float[] mejorMaxMin = {0, 0, 0};
	float[] mejorSolucion;
	float[][] solucionCompleta = new float[2][n];
	int mejorIteracion = 0;
	int veces;
	boolean vns = false;
	
	
	// Constructor:
	
	public MMDPFirstImprovement(Matriz _matriz, float[] _solucion, float _maxMin, int _veces, boolean _vns)
	{
		matriz = _matriz;
		solucion0 = _solucion;
		maxMin[0] = _maxMin;
		m = _solucion.length;
		n = matriz.n;
		veces = _veces;
		vns = _vns;
	}
	
	@Override
	public float[][] call() throws Exception
	{		
		if (imprimirDetalles == true) { System.out.println("Solucion de partida: " + Arrays.toString(solucion0) + ", con un maxMin de: " + maxMin); }
		
		random = randomGenerator.nextInt(2); // 3
		int pos = 0; int iter = 0;
		
		float[] secuencia = new float[n];
		for (int i = 0; i < n; i++)	{ secuencia[i] = i;	}				
		
		float[] maxMin0 = new float[3]; maxMin0 = calcularMM(matriz.getMatriz(), solucion0);
		float[] maxMin1 = new float[3];
		float[] solucion1 = null;
				
		for (int i = 0; i < m; i++)  // recorro uno a uno cada elemento de la solucion de partida
		{
			
			//System.out.print("i=" + i);
			
			for (int j = 0; j < m; j++) // recorro, para cada elemento de la solucion, los elementos que podrian sustituirlo. Aquí, j representa el numero de veces que lo intento.
			{
				//System.out.println("j=" + j);

				solucion1 = Arrays.copyOf(solucion0, solucion0.length);
				for (int k=0; k < veces; k++)
					{
					if (!vns) { solucion1 = permutarAleatorio(Arrays.copyOf(solucion1, solucion1.length), i); }
					else
						{
						//random = randomGenerator.nextInt(2);
						if (random == 0) { solucion1 = permutarMenos(Arrays.copyOf(solucion1, solucion1.length), i /*randomGenerator.nextInt(m)*/); }
						else { solucion1 = permutarMas(Arrays.copyOf(solucion1, solucion1.length), i /*randomGenerator.nextInt(m)*/); }
						}
				}
				maxMin1 = calcularMM(matriz.getMatriz(), solucion1);
				
				//System.out.print("\t" + maxMin1[0] + "\t" + Arrays.toString(solucion1) + "\t");
				
				if (maxMin1[0] > maxMin0[0]) // si la nueva solucion tiene mejor maxMin, la consolido como solucion actual
					{
					solucion0 = Arrays.copyOf(solucion1, solucion1.length);
					maxMin0 = Arrays.copyOf(maxMin1, maxMin1.length);
					mejorIteracion = iteracion;
					
					//System.out.println("\t--> " + maxMin0[0]);
					
					continue;
					}
				iteracion++;
				//System.out.println("\t" + i);
			}
			//System.out.println();
			//Pausa();
		}

		mejorSolucion = Arrays.copyOf(solucion0, solucion0.length);
		mejorMaxMin = Arrays.copyOf(maxMin0, maxMin0.length);
		
		if ((imprimirDetalles == true) /*|| (1==1)*/) { System.out.println("\tMejor solucion: " + Arrays.toString(mejorSolucion) + ",\n\tcon valor: " + Arrays.toString(mejorMaxMin) + ", en la iteracion: " + mejorIteracion); }
		
		solucionCompleta[0] = Arrays.copyOf(mejorSolucion, mejorSolucion.length);
		solucionCompleta[1] = Arrays.copyOf(mejorMaxMin, mejorMaxMin.length);
		
		return solucionCompleta;
	}
	
	float[] permutarMenos(float[] _sol, int pos)
	{
		float[] sol = _sol;
		
		if (sol[pos] > 0)
		{
			sol[pos]--;
		}
		
		//System.out.println(sol);
		return sol;
	}
	
	
	float[] permutarAleatorio(float[] _sol, int pos)
	{
		// Primero, se elige un vertice aleatorio que sustituir en la solucion:
		float[] sol = _sol;
		random = randomGenerator.nextInt(n);
		boolean repetido = false;
		
		// Segundo, se comprueba que ese vertice no pertenece ya a la solucion:
		for (int i=0; i<m; i++)
		{
			if (sol[i] == random)
			{
				repetido = true;
			}	
		}
		
		// Tercero; si no esta en la solucion, colocarlo:
		if (!repetido)
			{
				sol[pos] = random;
			}
		
		//System.out.println(sol);
		return sol;
	}
	
	

	float[] permutarMas(float[] _sol, int pos)
	{
		float[] sol = _sol;
		
		if (sol[pos] < (n-1))
		{
			sol[pos]++;
		}
		
		//System.out.println(sol);
		return sol;
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

	
	private void Pausa()
	{
		System.out.print("\nPulse cualquier tecla para continuar.\n\n");
	    try { System.in.read();	}
	    catch (IOException e) {	e.printStackTrace(); }
	}
	
}
