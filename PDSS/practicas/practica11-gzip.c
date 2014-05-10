#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>


// Programa que recibe nombres de archivos como entrada, y los intenta descomprimir con gunzip.


int main(int argc, char *argv[])
	{

	execlp("gunzip", "gunzip", argv[1]);
















	return 0;

	}
