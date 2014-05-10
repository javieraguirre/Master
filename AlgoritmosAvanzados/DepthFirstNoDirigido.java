package Practica1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

public class DepthFirstNoDirigido
	{
	
	public void Run(int[][] matriz, int n1, int n2, int verticeAleatorio)
		{
		System.out.println("\n\nRecorrido en profundidad de un grafo dirigido:\n");
	
		int iteraciones = n1;
		int num, j=0;
		int[] array = new int[n1];
		
		int[] vertice = new int[n1];
		for (int i=0; i<n1; i++) { vertice[i] = -1; } // llenar de valores invalidos.		
		vertice[0] = verticeAleatorio;
		
		System.out.print("Vertice aleatorio: " + vertice[0] + "\n\n");
		
		String[] run = new String[iteraciones];
		for (int i=0; i<iteraciones; i++) { run[i] = ""; } // limpiar runs
		
		// vertice aleatorio a run[0]:
		for (int x=0; x<n1; x++)
			{
			if (matriz[vertice[0]][x] != 0) { run[0] += x + " "; }
			}	
	
		System.out.print("Run 0: " + vertice[0] + " { " + run[0] + "}");
		
		// explorar:
		outerloop: for (int i=1; i<iteraciones; i++)
			{
			j=0;
			// desengrano el string de run[]:
			StringTokenizer st = new StringTokenizer(run[i-1]);
	
			while (st.hasMoreElements())
				{
				array[j] = num = Integer.parseInt(st.nextToken());
				j++;
				}
			
			// elegir numero aleatorio de un run:
			vertice[i] = vertice[0];
			vertice[0] = array[(int) (Math.random()*j)];
			
			System.out.print("\t--> Elegido nuevo vertice aleatorio: " + vertice[0] + "\n");
			//System.out.println("\n\tVertices visitados: " + Arrays.toString(vertice) + "\n");
			
			
			for (int x=0; x<n1; x++)
				{
				if (matriz[vertice[0]][x] != 0) { run[i] += x + " "; }
				}
		
			System.out.print("Run " + i + ": " + vertice[0] + " { " + run[i] + "}");
			/*if (run[i].equals(""))
				{
				System.out.println("\tLLegado al fondo del arbol de exploracion: nodo " + vertice[i]);
				}*/
		
			
			// Ver si estamos al final de la exploracion:
			
			for (int k=1; k<n1; k++)
				{
				if (vertice[0] == vertice[k])
					{
					System.out.println("\n\nVertice ya explorado: " + vertice[0] + " = " + vertice[k] + "\tFin del recorrido.");
					break outerloop;
					}
				}		
			} // final de outerloop
		}
	}
