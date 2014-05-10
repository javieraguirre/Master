#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>

// librerias incluidas para la tonteria del miprintf():
#include <stdarg.h>

#define ANSI_COLOR_RED "\x1b[31m"
#define ANSI_COLOR_CYAN "\x1b[36m"
#define ANSI_COLOR_RESET "\x1b[0m"

/* Escritor de archivos, byte a byte:

argv[1] = valor a escribir.
argv[2] = posicion del archivo a escribir.
argv[3] = fichero de entrada.
argc[4] = fichero de salida (opcional). */


// Activar o desactivar las trazas de debug en la consola con esta variable. 1=activado. 0=desactivado.
int trazas = 1;

// La clase que escribe las trazas es esta. Es una copia del printf original.
int traza(const char *format, ...)
{
	if (trazas == 1) {

		va_list arg;
		int done;
		va_start (arg, format);
		done = vfprintf (stdout, format, arg);
		va_end (arg);
		
		return done;
		}
}

int main(int argc, char *argv[])
{
	char buff[1024];
	int n;

	// int trazas = 1; // Activar o desactivar trazas de depuracion; 1 = activadas, 0 = desactivadas.
	traza(ANSI_COLOR_CYAN "\nTrazas de debug activadas\n" ANSI_COLOR_RESET);

	if (argc != 5) {
		printf(ANSI_COLOR_RED "\nERROR:" ANSI_COLOR_RESET "\tnumero de argumentos incorrecto.\n");
		printf("Uso:\t./<nombre-programa> <valor> <posicion> <origen> <destino>\n\n");
		printf("Ejemplo:\t./a.out 1 400 mifichero.txt ficheronuevo.txt\n\n");
		}
	else {
		traza("\nHaremos la siguiente operacion:\n\t- abriremos el fichero %s,\n\t- lo copiaremos con el nombre %s,\n\t- y en la posicion %s\n\t- escribiremos el valor %s.\n\n", argv[3], argv[4], argv[2], argv[1]);


		// Abrir el archivo de entrada:
		
		traza("\nAbriendo fichero de entrada...");

		int fin = open(argv[3], O_RDONLY);

		traza("OK");

		if (fin == -1) {
			perror("\nError al abrir el fichero: ");
			exit(1);
			}


		// Crear el archivo de salida:

		traza("\nCreando fichero de salida");

		int fout = open(argv[4], O_CREAT|O_WRONLY|O_TRUNC, S_IRUSR|S_IWUSR);
			
		traza("...");
	
		if (fout == -1) {
			perror("\nError al crear el fichero: ");
			exit(1);
			}

		traza("OK");

		// Copiar el fichero original:

		traza("\nCopiando contenido del fichero de entrada en el nuevo de salida...");

		while ((n = read(fin, buff, BUFSIZ)) > 0)
			{
			traza(".");
			write(fout, buff, n);
			}

		traza("OK");


		// Escribir en el fichero de salida:

		traza("\nEscribiendo en el fichero de salida:");

		traza("\nPosicionandonos sobre la posicion seleccionada, la %s...", argv[2]);
		lseek(fout, atoi(argv[2]), SEEK_SET); // lseek solo trabaja con enteros, y argv[] es un char.
		traza("OK");

		traza("\nEscribiendo el valor %s...", argv[1]);
		write(fout, argv[1], sizeof argv[1] -1); // restamos -1 a la ultima operacion para descartar el caracter de terminacion ^@ que introduce "sizeof".
		traza("OK");

		// Terminar el programa cerrando los ficheros usados:

		traza("\nCerrando ficheros...");

		close(fin);
		close(fout);

		traza("OK\n\n");
		}
	
	return 0;
}
