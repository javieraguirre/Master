package Practica1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class BreadthFirstNoDirigido {

	private String run[] = new String[11];
	
	
	public void Run(int[][] matriz, int n1, int n2, int verticeAleatorio) {
	
	int num = 0;
	boolean[] verticesVisitados = new boolean[100];
	for (int i=0; i<11; i++) { run[i] = "";	}
		
	System.out.println("\n\nRecorrido en anchura de un grafo no dirigido:\n");
	

	// vertice aleatorio a run[0]:
	
	for (int x=0; x<n1; x++)
		{
		if (matriz[verticeAleatorio][x] != 0) { run[0] += x + " "; }
		}
	verticesVisitados[verticeAleatorio] = true;
	
	System.out.println("Run 0:\n\t" + verticeAleatorio + " { " + run[0] + "}");
	
	// explorar run[1] y el resultado a run[2]:
	
	outerloop: for (int i=1; i<10; i++)
		{
		StringTokenizer st1 = new StringTokenizer(run[i-1]);
		run[i-1] = "";
		System.out.print("\nRun " + i + ": ");
		
		while (st1.hasMoreElements())
			{
			num = Integer.parseInt(st1.nextToken());

			if (verticesVisitados[num] == false)
				{
				verticesVisitados[num] = true;
				
				run[i-1] += num + " "; // Esta linea define el contenido final de un run[].
			
				System.out.print("\n\t" + num + " { ");
				for (int x=0; x<n1; x++)
					{
					if (matriz[num][x] != 0)
						{
						run[i] += x + " ";
						System.out.print(x + " ");
						}
					}
				System.out.print("} ");
				}
			}
		
		if (run[i].length() == 0) { System.out.println("\n\tFin del recorrido"); break outerloop; }
		
		// Ordenar lista de numeros y eliminar repetidos:
		// System.out.println("\n\t\t" + run[i]);
		StringTokenizer st2 = new StringTokenizer(run[i]);
		int[] array = new int[st2.countTokens()];
		int j=0, numactual=0;
		while (st2.hasMoreElements())
			{
			array[j] = Integer.parseInt(st2.nextToken());
			j++;
			}
		Arrays.sort(array);
		j=0;
		numactual = array[0];
		for(j=1; j<array.length; j++)
			{
			if (numactual == array[j]) { array[j] = -1;	}
			else { numactual = array[j]; }
			}
		run[i] = Arrays.toString(array).replace(" -1", "").replace(",", "").replace("[", "").replace("]",  "");
		
		// System.out.println("\t\t" + run[i]);
		}
	}
	
	public String[] getRun()
	{
		return run;
	}
	
	
}
