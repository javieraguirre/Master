#include <stdio.h>

// Demostracion de un programa que falle, para su uso con el depurador.

int *b;

int funcion()
	{
	//int *b;
	*b = 7;
	}

int main (int argc, char *argv[])
	{
	funcion();
	}
