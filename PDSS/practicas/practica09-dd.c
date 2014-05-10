#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>

// Programa similar a dd, que lee nosecuantos bytes desde el offset de un fichero de entrada, y los escribe en un fichero de salida.

// argv[1]: in_file; si es un "-", es la entrada std.
// argv[2]: out_file; si es un "-", es la salida std.
// argv[3]: offset.
// argv[4]: tamano en bytes.


#define rojo "\033[0;31m"
#define cyan "\033[1;36m"
#define azulsub "\033[4;34m"
#define azul "\033[0;34m"
#define reset "\033[0m"
#define gris "\033[1;37m"

/* 0: normal, 1: negrita, 4: subrayado, 9: tachado.
 * 31: rojo, 36: cyan, 32: verde, 34: azul, 37: gris */


int trazas = 1;
int traza(const char *traza) { if (trazas == 1) { printf(traza); } }

int main(int argc, char *argv[])
{

	int size = atoi(argv[4]);
	int offset = atoi(argv[3]);
	char buffer[256];
	int n=0, i=0, j=0;


	if (argc != 5)
		{
		printf(cyan "\nClon tonto del DD de J.Aguirre." reset);
		printf("\n\nExtraer los bytes que queramos desde una posicion elegida de un fichero de origen especificado, y guardarlo en otro fichero nuevo.");
		printf(azulsub "\n\nSintaxis:\n" reset);
		printf("\t./practica09-dd <fichero_origen> <fichero_destino> <posicion> <tamano>");
		printf("\n\nDonde:\t<posicion> es la posicion desde donde leer,");
		printf("\n\ty <tamano> el tamano en bytes que queremos leer.");

		printf("\n\n");
		exit(0);
		}


	printf(azul "\nVamos a hacer lo siguiente:" reset);
	printf("\n\tAbriremos el archivo " gris "%s" reset ",\n\tleeremos " gris "%d" reset " bytes\n\tdesde la posicion " gris "%d" reset ",\n\ty lo que leamos lo guardaremos en el archivo " gris "%s" reset ".\n", argv[1], size, offset, argv[2]);



	traza("\nAbriendo fichero de entrada...");
	
	int filein;
	char linea[size+1];
	char linea2[size+1];
	
	if (strcmp(argv[1], "-") == 0)
		{
		traza("\nUsaremos la entrada estandar.");
		printf(azul "\nEscribe lo que sea: " reset);
		if (fgets(linea, offset+size+1, stdin)) {
			printf("\nEscribiremos: ");
			puts(linea);
			//filein = linea;
			}

		traza("\nPosicionandonos en el offset de la cadena...");
		for(i=0; i<size; i++)
			{
			linea2[i] = linea[(offset+i)];
			traza(".");
			}
		traza("Ok");
		}

	else
		{
		filein = open(argv[1], O_RDONLY);
	
		if (filein == -1)
			{
			printf(rojo "\nError al abrir %s\n\n" reset, argv[1]);
			exit(1);
			}
	
		else	{ traza("Ok"); }

		traza("\nPosicionandonos en el offset del fichero...");
		lseek(filein, offset, SEEK_SET);
		traza("Ok");
		}


	traza("\nCreando fichero de salida...");
	int fileout = open(argv[2], O_CREAT|O_WRONLY|O_TRUNC, S_IRUSR|S_IWUSR);

	if (fileout == -1)
		{
		printf(rojo "\nError al crear %s\n\n" reset, argv[2]);
		exit(1);
		}
	else { traza("Ok"); }


	traza("\nLeyendo y escribiendo...");

	if (strcmp(argv[1], "-") == 0)
		{
		if (strcmp(argv[2], "-") == 0)
			{ printf("\nLinea de salida:\n\t" gris "%s " reset, linea2); }
		else
			{
			printf("\nLinea a escribir: %s ", linea2);
			write(fileout, linea2, sizeof(linea2));
			}
		}

	else	
		{
		if (strcmp(argv[2], "-") == 0)
			{
			traza("\nEscribiendo en la salida estandar...");
			printf("\n");
			for (i=0; i<size; i++)
				{
				n = read(filein, buffer, 1);
				printf(gris "%s" reset, buffer);
				}
			printf("\n");
			}

		else
			{
			traza("\nEscribiendo en disco...");
			for (i=0; i<size; i++)
				{
				traza(".");
				n = read(filein, buffer, 1);
				write(fileout, buffer, n);
				}
			}
		}

	if (n == -1) { traza(rojo "Error al leer.\n\n" reset); exit(1); }
	else { traza("Ok"); }

	traza("\nCerrando todos los ficheros...");
	close(filein);
	close(fileout);
	traza("Ok");

	printf("\n\n");
	return 0;
}
