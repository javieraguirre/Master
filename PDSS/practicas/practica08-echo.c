#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int trazas = 0;

int traza(const char *format, ...)
{
	if (trazas == 1) { printf(format); }
}


int main(int argc, char *argv[])
{
	int i=0, j=0;
	int saltolinea = 1;
	int corchetes = 0;

	if (argv[1] == NULL)
		{
		traza("\nHaciendo eco de nada\n\n");
		exit(0);
		}

	if (argv[2] == NULL)
		{
		argv[2] = "valorquenoseveranunca";
		//argc--;
		}

	traza("\ntraza 1.0\n");
	if ((strcmp(argv[1], "-n") == 0) || (strcmp(argv[2], "-n") == 0))
		{
		traza("traza 1.1\n");
		saltolinea = 0;
		}
	
	traza("traza 2.0\n");
	if ((strcmp(argv[1], "-v") == 0) || (strcmp(argv[2], "-v") == 0))
		{
		traza("traza 2.1\n");
		corchetes = 1;
		}

	traza("traza 3.0\n");
	/*if (corchetes == 1)
		{
		for (i=1; i<argc; i++)
			{
			printf("\ntraza 3.1");
			printf("[%s] ", argv[i]);
			}
		}
	else
		{
		for (i=1; i<argc; i++)
			{
			printf("\ntraza 3.2");
			printf("%s ", argv[i]);
			}
		}*/

	if ((saltolinea == 0) && (corchetes == 1)) { j = 3; }
	else if (((saltolinea == 0) && (corchetes == 0)) ||
		((saltolinea == 1) && (corchetes == 1))) { j = 2; }
	else { j = 1; }

	traza("traza 3.1, j = %d\n", j);
	for (i=j; i<argc; i++)
		{
		traza("traza 3.2\n");
		if (corchetes == 1) { printf("["); }
		printf("%s", argv[i]);
		if (corchetes == 1) { printf("]"); }
		printf(" ");
		}		

	traza("traza 4.0\n");
	if (saltolinea == 1)
		{
		traza("traza 4.1\n");
		printf("Insertando salto de linea\n");
		}

	return 0;
}
