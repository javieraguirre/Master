package practica1_Opti_mas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.text.Segment;


public class LectorFichero {

	public float main(String fichero_de_instancias) {
		// Lee un fichero de texto línea a línea, donde
		// cada línea contiene dos números separados por un espacio
		float segundoNumero = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fichero_de_instancias)));
			String line = br.readLine();
			while(line != null) {

				StringTokenizer st = new StringTokenizer(line);
				String primerNumero = st.nextToken();
				segundoNumero = Float.parseFloat(st.nextToken());
				
				System.out.println(primerNumero + "," + segundoNumero);
				
				line = br.readLine();
				
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("No puedo encontrar el fichero");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("No puedo leer el fichero");
			e.printStackTrace();
		}
		return segundoNumero;
	}
}