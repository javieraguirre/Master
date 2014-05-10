package codigoBasura;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;

public class SimulatedAnnealing implements Callable<float[][]>
{

	// Variables globales:
	
	private static boolean imprimirDetalles = true;
	
	Matriz matriz;
	float[] solucion0 = null;
	float maxMin0 = 0;
	int m, n;
	int iteracion = 0;
	
	Random randomGenerator = new Random();
	int random;
	
	float temperatura;
	double probabilidad;
	float[] mejorMaxMin = {0, 0, 0};
	float[] mejorSolucion;
	float[][] solucionCompleta = new float[2][3];
	int mejorIteracion = 0;
	
	
	// constructor
	
	public SimulatedAnnealing(Matriz _matriz, float[] _solucion, float _maxMin)
	{
		matriz = _matriz;
		solucion0 = _solucion;
		maxMin0 = _maxMin;
		m = _solucion.length;
		n = matriz.n;
	}
	
	@Override
	public float[][] call() throws Exception
	{
		temperatura = 1000;
		
		do
		{
			/*MMDPBestImprovement bestImp = new MMDPBestImprovement(matriz, solucion0, maxMin0);
			solucionCompleta = bestImp.call();
			//bestImp = null; // Destruir objeto.*/
			
			MMDPFirstImprovement firstImp = new MMDPFirstImprovement(matriz, solucion0, maxMin0, 1);
			solucionCompleta = firstImp.call();
			firstImp = null;
			
			System.out.println("Sol0\t" + Arrays.toString(solucion0) + "\n\t" + maxMin0 +
			"\n" + "Sol1\t" + Arrays.toString(solucionCompleta[0]) + "\n\t" + solucionCompleta[1][0]);
			
			
			if (solucionCompleta[1][0] > maxMin0)
			{
				System.out.println("\tSolucion mejor");
				
				solucion0 = Arrays.copyOf(solucionCompleta[0], solucionCompleta[0].length);
				maxMin0 = solucionCompleta[1][0];
			}
			
			else //if (solucionCompleta[1][0] < maxMin0)
			{
				System.out.print("\tSolucion igual o peor: ");
				
				probabilidad = Math.exp((-1)*(maxMin0 - solucionCompleta[1][0]) / temperatura);
				//System.out.print("\tprobabilidad: " + probabilidad);
				probabilidad = (probabilidad * 100);
				System.out.print("\tprobabilidad: " + probabilidad);
				
				random = randomGenerator.nextInt(100);
				System.out.print("\trandom: " + random);
				
				if (random < probabilidad)
				{
					System.out.print("\tEmpeoramiento controlado");
					
					solucion0 = Arrays.copyOf(solucionCompleta[0], solucionCompleta[0].length);
					maxMin0 = solucionCompleta[1][0];
				}
				
				//Pausa();
			}
			
			/*else
			{
				System.out.println("\tSolucion repetida");
			}*/
			
			temperatura --;
			
			System.out.println("\n" + "temperatura: " + temperatura + "\n");
			
			//Pausa();
		}
		while ((temperatura > 0) || (probabilidad == 0.0));
		
		/*MMDPBestImprovement bestImp = new MMDPBestImprovement(matriz, solucion0, maxMin);
		solucionCompleta = bestImp.call();
		
		System.out.println(Arrays.toString(solucionCompleta[0]) + "\n" + Arrays.toString(solucionCompleta[1]));
		
		System.out.println(Math.exp((-1)*(solucionCompleta[1][0] - maxMin) / temperatura));
		 */
		
		
		return solucionCompleta;
	}
	
	
	private static void Pausa()
	{
	System.out.print("\nPulse cualquier tecla para continuar.\n\n");
    try { System.in.read();	}
    catch (IOException e) {	e.printStackTrace(); }
	}
	
}
