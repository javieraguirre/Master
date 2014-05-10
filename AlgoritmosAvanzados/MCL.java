package Practica1;

import java.io.IOException;
import java.util.Arrays;


public class MCL
	{
	int cantidadVertices = 0;
	int cantidadAristas = 0;

	static int porcentaje = -1;
	
	double estimacionIteraciones = 1;
	static double iteraciones = 0;
	static long timeInMillis_Inicio = System.currentTimeMillis();
	
	static String mejorSolucion = "";
	static double mejorIteracion = 0;
	static int mejorMaxCW = 99999;
	
	boolean finalizar = false;
	boolean bloqueo = false;
	int podas = 0;
	int podasCalculo = 0;
	
	public int[] cantidadVecinos;
	public static String[] vecinos;
	
	public static int actCW = 0;

	
	public void Run(int[][] matriz, int n1, int n2) throws Exception
		{	
		System.out.println("\n\n\nAlgoritmo de Minimum cutwidth.\n\nCalculando...\n");

		// Encontrar la cantidad de arcos de cada vertice:
		cantidadVecinos = new int[n1];
		vecinos = new String[n1];
		
		for (int x=0; x<n1; x++)
			{
			vecinos[x] = "";
			for (int y=0; y<n1; y++)
				{
				if (matriz[x][y] != 0)
					{
					cantidadVecinos[x]++;
					cantidadAristas++;
					vecinos[x] += y + ",";
					}
				}
			cantidadVertices = x;
			System.out.println("x = " + x + "\t" + cantidadVecinos[x] + "; vecinos: " + vecinos[x]);
			}

		System.out.println("\nNumero total de vertices: " + (cantidadVertices+1));
		cantidadAristas = cantidadAristas / 2; // Dividimos entre 2 porque este conteo ha contado las aristas desde sus 2 vertices extremos.
		System.out.println("Numero total de aristas en el problema: " + cantidadAristas);
		
		// Calcular el LB5 o la cota inferior:
		System.out.println("\nCalculando LB5:");
		double aux = 0;
		int LB5 = 0;
		cantidadVertices++;
		
		loop: for (LB5=1; LB5<(cantidadVertices+1); LB5++)
		{
			for (int i=1; i<(LB5+1); i++)
			{
				aux += (cantidadVertices - i) / LB5;
				if (aux > cantidadAristas) { break loop; }
			}
		}
		
		System.out.println("LB5 = " + LB5);
        
        // Iniciar el algoritmo:
        permutarProfundidad("", n1, n1, matriz); // recuerda que para cuando quieras hacerlo multihilo, pasale "0,", "1,", ... y asi sucesivamente.
        
        // Finalizado el algoritmo: 
        Finalizar(matriz);

		}

	
	private static void permutarProfundidad(String act, int n, int r, int[][] matriz) throws Exception
		{
		int maxCW = 0;
		
		// Establecer un limite de tiempo, en milisegundos:
		long timeInMillis_Intermedio = System.currentTimeMillis();

    	if ((timeInMillis_Intermedio - timeInMillis_Inicio) >= 1800000) { Finalizar(matriz); }
    	else
    		{
        	// Calcular porcentaje de tiempo que queda hasta finalizar el algoritmo por limite de tiempo:
    		int tiempo = (int) (timeInMillis_Intermedio - timeInMillis_Inicio);
    		int porcentajeActual = ( ( tiempo * 100) / 1800000);
    		if (porcentajeActual != porcentaje) { System.out.println("Completado: " + porcentajeActual + "%" + "\tTiempo: " + tiempo/1000 + " seg(s)" + "\tPermutando: " + act); }
    		porcentaje = porcentajeActual;
    		}
		
		// Si tenemos una solucion nueva completa. En este bloque de codigo es cuando se tiene propiamente dicho una solucion nueva completa. Es aqui donde habra que calcular el CW de cada arista.
        if (n == 0)
        	{
        	// VERBOSE: System.out.print("Solucion nueva: " + act);
        	
        	maxCW = calcularCorte(matriz, act.split(","), -1, true);

    		if (maxCW < mejorMaxCW)
    			{
    			mejorSolucion = act;
    			mejorIteracion = iteraciones;
    			mejorMaxCW = maxCW;
    			}        	
        	
        	// VERBOSE: System.out.println("\tmaxCW: " + maxCW + "\n");
        	//Pausa();
        	
        	iteraciones++;
        	actCW = 0;
        	}
        
        // Si quedan elementos por permutar:
        else
        	{        	
            loop: for (int i=0; i<r; i++)
            	{
            		{
	            	if (!act.matches(".*\\b" + i + "\\b.*"))
	            		{
		            	// VERBOSE: System.out.print("Perm.\ti: " + i + "/" + (r-1) + "\tact: " + act + "\tactCW: " + actCW);
	            		
		            	int antiguoCW = actCW;
		            	
		            	if (act != "") { actCW = calcularUltimoCorte(matriz, act.split(","), actCW); }
		            	
		            	// Forzar el fin de las permutaciones
		            	if ((actCW >= mejorMaxCW) && (iteraciones > 0))
		            		{
		            		// VERBOSE: System.out.print("\tPoda");
		            		break loop;
		            		}
		            	
	            		// VERBOSE: System.out.println("\tn: " + n);
		            	
	            		permutarProfundidad(act + i + ",", n-1, r, matriz);
	            		
	            		actCW = antiguoCW;
	            		}
            		}
            	}
        	}
		}
	
	
	
	
	
	private static int calcularUltimoCorte(int[][] matriz, String[] act, int actCW)
		{
		int CW = 0;
		
		int corte = Integer.parseInt(act[(act.length)-1]);
		String[] vecinosLocal = new String[vecinos[corte].split(",").length];
		vecinosLocal = vecinos[corte].split(",");

		for (int x=0; x<vecinosLocal.length; x++)
			{
			if (Arrays.toString(act).matches(".*\\b" + vecinosLocal[x] + "\\b.*")) { CW--; }// vecino dentro de la solucion. Siempre hara CW--;
			else { CW++; } // vecino se sale de la solucion. Sera CW++;
			}
	
		CW = CW + actCW;
		
		return CW;
		}
	
	
	private static void Pausa()
		{
    	System.out.print("\nPulse cualquier tecla para continuar.\n\n");
        try { System.in.read();	}
        catch (IOException e) {	e.printStackTrace(); }
		}

	
	private static int calcularCorte(int[][] matriz, String[] s, int longitudCorte, boolean calcularMax)
	{
	int CW = 0, maxCW = 0, minCW = 99999;
	if (longitudCorte == -1) { longitudCorte = s.length-1; }
	
	// bucle desde el inicio de la solucion hasta el corte:
	for (int corte=0; corte<longitudCorte; corte++)
		{
		for (int x=0; x<s.length; x++)
			{
			// Cuando encontramos un vecino:
			if (matriz[(Integer.parseInt(s[corte]))][x] != 0)
				{
				// Encontrar el indice del vertice vecino:
				int indice = -1;
			    for(int i=0; i<s.length; i++) { if (s[i].equals(String.valueOf(x))) { indice = i; } }
				
				if (indice > corte) { CW++; }
				else if (indice < corte) { CW--; }
				}
			}

		if (calcularMax) { if (CW > maxCW) { maxCW = CW; } } // almacenar el corte maximo
		else { if (CW < minCW) { minCW = CW; } }
		}
	
	if (calcularMax) { return maxCW; }
	else { return minCW; }
	}
	
	
	private static void Finalizar(int[][] matriz)
		{
	    // Finalizado el algoritmo:
	    
	    System.out.println("\n\nTotal iteraciones: " + (int) Math.round(iteraciones));
		
	    System.out.println("\n\nMejor solucion:");
	    System.out.println("\tIteracion " + (mejorIteracion) + ":\t" + mejorSolucion);
	    System.out.println("\tMayor corte: " + mejorMaxCW);
	    
	    // Calcular menor corte:
	    System.out.println("\tMenor corte: " + calcularCorte(matriz, mejorSolucion.split(","), -1, false));
	    
	    long timeInMillis_Final = System.currentTimeMillis();      
	    System.out.println("\n\nTiempo empleado: " + (timeInMillis_Final - timeInMillis_Inicio) + " milisegundos.\n\n\n");
	    
		System.exit(0);
		}
	}