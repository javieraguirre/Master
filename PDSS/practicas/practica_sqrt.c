#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>

// Practica nn: Sumatorio de las raices cuadradas desde 1 a 1000, con threads.

int main(int argc, char *argv[])
	{

	printf("\n\nEste programa calcula el sumatorio de todas las raices cuadradas, desde el 1 hasta el 1000.\n\n");

	int pid = 0;
	double i = 0;
	double suma = 0;

	do
		{
		suma = suma + sqrt(i);
		i++;
		printf("\niteracion %d", i);
		}
	while (suma < 1000);

	// Sin threads:
	printf("\n\nSin threads: sumatorio: %f\n\n", suma);


	// Con threads:

	return 0;
	}
