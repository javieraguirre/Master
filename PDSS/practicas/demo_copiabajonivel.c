#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>

// Copion a bajo nivel, que muestra el uso de algunas llamadas al sistema.
// El archivo a copiar lo recibe como primer argumento, y lo copia en el nombre de archivo especificado en el segundo argumento.


int main(int argc, char *argv[])
{
	char buff[1024];
	int n;

	if (argc != 3) {
		printf("Número de argumentos incorrecto\n\n");
		}
	else {
		printf("Argumentos: %s %s\n\n", argv[1], argv[2]);

		int fin = open(argv[1], O_RDONLY);

		if (fin == -1) {
			perror("Error al abrir el fichero: ");
			exit(1);
			}

		int fout = open(argv[2], O_CREAT|O_WRONLY|O_TRUNC, S_IRUSR|S_IWUSR);
				
		if (fout == -1) {
			perror("Error al crear el fichero: ");
			exit(1);
		}

		
		// La siguiente sentencia lee del archivo de origen, "fin",
		// y escribe en el archivo de destino: "fout":

		while ((n = read(fin, buff, BUFSIZ)) > 0)
			write(fout, buff, n);

		// Podriamos haber hecho un programa que leyera de la entrada estandar, el teclado.
		// ¿Como? cambiando "fin" por un "0"; 0 es la entrada estandar de C.

		close(fin);
		close(fout);
		}
	
	return 0;
}
