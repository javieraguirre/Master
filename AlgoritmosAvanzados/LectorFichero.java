package Practica1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class LectorFichero {

	public class java {



	public void main(String fichero_de_instancias) {
		// Lee un fichero de texto línea a línea, donde
		// cada línea contiene dos números separados por un espacio
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fichero_de_instancias)));
			String line = br.readLine();
			while(line != null) {

				StringTokenizer st = new StringTokenizer(line);
				int primerNumero = Integer.parseInt(st.nextToken());
				int segundoNumero = Integer.parseInt(st.nextToken());
				
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
		
	}

}
}