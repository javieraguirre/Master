package practica1_Opti_mas;

import java.util.Arrays;

public class CalcularMM {

	public static float[] maxMin = new float[3];

	// Constructor:
	public CalcularMM(float[][] _matriz, float[] _solucion)
		{		
		calcularMM(_matriz, _solucion);
		}
	
	
	public float[] call() throws Exception
		{
		return maxMin;
		}
	
	
	private float[] calcularMM(float[][] matriz, float[] solucion) // void Run()
	{
	maxMin[0] = 999999;
	float f = 0;
	
	
	//System.out.println("solucion: " + Arrays.toString(solucion) + ";\tsolucion.length: " + solucion.length);

	for(int x=0; x < solucion.length; x++)
		{
		for(int y=0; y < solucion.length; y++)
			{

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
	
	if (maxMin[0] == 999999) { maxMin[0] = -1; } // -1 significa que los vertices son inaccesibles entre si.
	return maxMin;
	}
}
