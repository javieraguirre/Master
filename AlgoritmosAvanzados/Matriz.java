package Practica1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.math.*;

public class Matriz {

	private int n1, n2, n3;
	private int[][] matriz;
	
	
	public int[][] generar(String ruta)
	{
		
		int x=0, y=0, count=0;
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(new File(ruta)));
			
			// Primera linea: nombre del problema			
			String line = br.readLine();
			System.out.println(line);
			
			// Logica para detectar un problema de tipo Grid:
			StringTokenizer st2 = new StringTokenizer(line);
			String[] g = (st2.nextToken()).split("");
			
			
			if (g[1].equals("G"))
				{
				// Estamos ante un problema que nos pasa la matriz directamente.
				line = br.readLine();
				
				// Siguientes líneas: matriz
				System.out.println("Generando array:\n");
				
				StringTokenizer stX = new StringTokenizer(line);
				
				n1 = stX.countTokens();
				n2 = n1;
				
				System.out.println("Tamaño del array: " + n1 + " por " + n2 + "\n");
				
				matriz = new int[n1][n2];
				
				for (y=0; y<n2; y++)
					{
					stX = new StringTokenizer(line);
					
					for (x=0; x<n1; x++)
						{
						matriz[x][y] = Integer.parseInt(stX.nextToken());
						}
					line = br.readLine();
					}
				
				br.close();
				
				}

			else // Estamos ante un problema al que se nos pasa una lista de adyacencias desordenada.
				{
				// Segunda linea: parámetros de la matriz
				line = br.readLine();
				StringTokenizer st0 = new StringTokenizer(line);
	
				n1 = Integer.parseInt(st0.nextToken());
				n2 = Integer.parseInt(st0.nextToken());
				n3 = Integer.parseInt(st0.nextToken());
	
	
				System.out.println("Tamaño del array: " + n1 + " por " + n2 + "; número de aristas: " + n3 + "\n");
				matriz = new int[n1][n2];	
				
				line = br.readLine();
				
				// Siguientes líneas: matriz
				System.out.println("Generando array:\n");
				while(line != null) {
	
					StringTokenizer st1 = new StringTokenizer(line);
					x = Integer.parseInt(st1.nextToken()); System.out.print("x: " + x + "\t");
					y = Integer.parseInt(st1.nextToken()); System.out.println("y: " + y);
					matriz[x-1][y-1] = 1;
					count++;
					line = br.readLine();
					}
	
				System.out.println("\nNúmero de aristas posicionadas: " + count);
				br.close();
				}
			
			// Señalando adyacentes no dirigidos:
			
			if (!g[1].equals("G"))
				{
				for(y=0; y<n1; y++)
					{
					for(x=0; x<n2; x++)
						{
						if (matriz[x][y] == 1)
							{
							matriz[y][x] = 2;
							}
						}
					}
				}
			
		} catch (FileNotFoundException e) { // Resolución de excepciones.
			System.out.println("No puedo encontrar el fichero");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("No puedo leer el fichero");
			e.printStackTrace();
			}

		return matriz;
	}
	
	public int getN1()
		{
		return n1;
		}
	
	public int getN2()
		{
		return n2;
		}
	
	public int[][] getMatriz()
		{
		return matriz;
		}
	
	public void representarLista()
		{
		
		System.out.println("\nLista de adyancencias:");
		
		for (int x=0; x<n2; x++)
			{
			
			System.out.print("\nVertice " + x + ":"); // System.out.print("\n" + (x+1));
			
			for (int y=0; y<n1; y++)
				{
				if (matriz[x][y] != 0)
					{
					System.out.print(" -> " + (y)); // System.out.print(" -> " + (y+1));
					}
				}
			}
		}
	

	public void representarMatriz()
		{
			System.out.println("\n\nMatriz de adyacentes:\n");
			
			int x, y;
			
			// Representar la fila superior de las X:
			System.out.print("\t");
			for (x=0; x<n1; x++) { System.out.print("x=" + x + "\t"); }
			
	
			// Representar los valores y la columna de las Y:
			for (x=0; x<n1; x++) {
				System.out.print("\n");
	
				for (y=0; y<n2; y++) {
					if (y==0) { System.out.print("y=" + x); }
					System.out.print("\t" + matriz[x][y]);
				}
			}
			
			System.out.println();
			
			// Representar la fila inferior de las X:
			System.out.print("\t");
			for (x=0; x<n1; x++) { System.out.print("x=" + x + "\t"); }

			
			System.out.println("\n\nAristas marcadas con 1: aristas señaladas directamente.");
			System.out.println("Aristas marcadas con 2: aristas deducidas inversamente.\n\n");
			
		}
	
	}
