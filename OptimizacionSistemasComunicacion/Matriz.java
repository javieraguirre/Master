package practica1_Opti_mas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Matriz {

	// Declarar variables:
	
	public int n, m;
	public float[][] matriz;
	
	private String[] nym = new String[2];
	private String line;

	
	Matriz(String _ruta) throws FileNotFoundException
		{
		String ruta = _ruta;
		
		try
			{
			BufferedReader br = new BufferedReader(new FileReader(new File(ruta)));
			
			// Primera linea: n y m:
			nym = br.readLine().split(" ");
			n = Integer.parseInt(nym[0]);
			m = Integer.parseInt(nym[1]);
			
			matriz = new float[n][n];
			
			//System.out.println("Seleccionar " + m + " elementos, de un conjunto de " + n + " elementos.");
			
			
			// Siguientes lineas: distancias entre vertices:
			line = br.readLine();
			
			while (line != null)
				{
				StringTokenizer st = new StringTokenizer(line);
				
				matriz[Integer.parseInt(st.nextToken())][Integer.parseInt(st.nextToken())] = Float.parseFloat(st.nextToken());
				
				line = br.readLine();
				}	
			}
		
		catch (FileNotFoundException e)
			{
			System.out.println("No puedo encontrar el fichero");
			e.printStackTrace();
			}
		catch (IOException e)
			{
			System.out.println("No puedo leer el fichero");
			e.printStackTrace();
			}
		}
	
	/*public int getN1()
		{
		return n1;
		}
	
	public int getN2()
		{
		return n2;
		}*/
	
	public float[][] getMatriz()
		{
		return matriz;
		}
	
	public void representarLista()
		{
		
		System.out.println("\nLista de adyancencias:");
		
		for (int x=0; x<n; x++)
			{
			
			System.out.print("\nVertice " + x + ":"); // System.out.print("\n" + (x+1));
			
			for (int y=0; y<n; y++)
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
			System.out.println("\n\nMatriz de adyacencia:\n");
			
			int x, y;
			
			// Representar la fila superior de las X:
			System.out.print("\t");
			for (x=0; x<n; x++) { System.out.print("x=" + x + "\t"); }
			
	
			// Representar los valores y la columna de las Y:
			for (x=0; x<n; x++) {
				System.out.print("\n");
	
				for (y=0; y<n; y++) {
					if (y==0) { System.out.print("y=" + x); }
					System.out.print("\t" + (int) matriz[x][y]);
				}
			}
			
			System.out.println("");
			
			// Representar la fila inferior de las X:
			System.out.print("\t");
			for (x=0; x<n; x++) { System.out.print("x=" + x + "\t"); }
			
			System.out.println("\n\n");
		}
	
	}
