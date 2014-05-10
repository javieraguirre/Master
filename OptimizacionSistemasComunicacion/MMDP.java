package practica1_Opti_mas;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Callable;
import practica1_Opti_mas.CalcularMM;


/******************************************************************************
 * Constructivo aleatorio para el problema de Maximum Minimum Diversity Problem
 * ----------------------------------------------------------------------------
 * 
 * Este programa construye una solucion inicial a partir de una solucion vacia.
 * De n elementos, escoge m, calcula su valor y la devuelve codificada en un float[][].
 ******************************************************************************/


class MMDP implements Callable<float[][]>
	{
	
	// Declarar variables:
	private boolean imprimirDetalles = false;
		
	private int n, m, random;
	private float[][] matriz;
	
	float[] solucionParcial;
	
	boolean[] presencia;
	
	Random randomGenerator = new Random();
	
	// Constructores:
	
	// Este constructor esta pensado para iniciar el constructivo desde una solucion vacia.
	public MMDP(float[][] _matriz, int _n, int _m)
		{
		matriz = _matriz;
		n = _n;
		m = _m;
		random = randomGenerator.nextInt(n);
		}
	
	// Este otro constructor sirve para iniciar el constructivo desde una solucion parcial.
	public MMDP(float[][] _matriz, int _n, int _m, float[] _solucionParcial)
		{
		matriz = _matriz;
		n = _n;
		m = _m;
		random = randomGenerator.nextInt(n);
		solucionParcial = new float[m];
		solucionParcial = Arrays.copyOf(_solucionParcial, _solucionParcial.length);
		}

	
	public float[][] call() throws Exception
		{
		
		if (imprimirDetalles == true) { System.out.println("\n\nSemilla: " + random); }
		
        // Iniciar el algoritmo:
		float[] solucion = new float[m];
		int inicio;
		presencia = new boolean[n];
		
		if (solucionParcial == null)
			{
		    solucion[0] = random;
		    inicio = 1;
			}
		else
			{
			inicio = solucionParcial.length;
			solucion = Arrays.copyOf(solucionParcial, solucion.length);
			
			for(int i=0; i<m; i++)
				{
				presencia[(int) solucion[i]] = true;
				}
			}
	    
	    int x;
	    boolean numPresente = false;
	    
	    for (int i=inicio; i<m; i++)
	    	{
	    	do
	    		{
	    		numPresente = false;
	    		x = randomGenerator.nextInt(n);
	    		
	    		/*if (presencia[x] != true)
	    			{
	    			solucion[i] = x;
	    			presencia[x] = true;
	    			}*/
	    		
	    		
	    		for (int j=0; j<i; j++)
	    			{
	    			if (solucion[j] == x) { numPresente = true; }
	    			}
	    		if (!numPresente) { solucion[i] = x; }
	    		
	    		}
	    	while (numPresente);
	    	//while (presencia[x] != true);
	    	}

	    // Metodo CalcularMM en clase separada. No usar, porque da problemas con el multihilo:
	    /*CalcularMM c = new CalcularMM(matriz, solucion);
	    float[] valoresSolucion = c.call();*/

	    float[] valoresSolucion = calcularMM_OLD(solucion, matriz);
	    
	    if (imprimirDetalles == true) {  System.out.println("\n" + "Solucion: " + Arrays.toString(solucion) + "\n\n\tminimo: " + valoresSolucion[0] + "\n\tentre los vertices " + (int) valoresSolucion[1] + " y " + (int) valoresSolucion[2]); }

	    float[][] solucionCompleta = new float[2][solucion.length];
	    //solucionCompleta[1] = valoresSolucion;
	    
	    /*for(int i=0; i<solucion.length; i++)
	    	{
	    	solucionCompleta[0][i] = solucion[i];
	    	}*/
	    solucionCompleta[0] = Arrays.copyOf(solucion, solucion.length);
	    solucionCompleta[1] = Arrays.copyOf(valoresSolucion, valoresSolucion.length);
	    
	    if (imprimirDetalles == true) {  System.out.println("\nsolucionCompleta: " + Arrays.toString(solucionCompleta[0]) + "\n" + Arrays.toString(solucionCompleta[1])); }
	    
	    return solucionCompleta;
        }

	
	private static float[] calcularMM_OLD(float[] solucion, float[][] matriz)
	{
		float[] maxMin = new float[3];
		maxMin[0] = 99999;
		float f = 0;
	
		for(int x=0; x<solucion.length; x++)
			{
			for(int y=0; y<solucion.length; y++)
				{
				f = matriz[(int) solucion[x]][(int) solucion[y]];
				
				if ((f < maxMin[0]) && (f != 0))
					{
					maxMin[0] = f;
					maxMin[1] = solucion[x];
					maxMin[2] = solucion[y];
					}
				}
			}
		
		return maxMin;
	}

	
	
	private static void Pausa()
		{
    	System.out.print("\nPulse cualquier tecla para continuar.\n\n");
        try { System.in.read();	}
        catch (IOException e) {	e.printStackTrace(); }
		}
	}