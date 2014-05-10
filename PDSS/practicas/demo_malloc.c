/*
Este programa muestra la equivalencia de la reserva de memoria implícita al crear un array de int,
y la reserva explícita al usar la función malloc para reservar memoria para otro array de ints.
*/

#include<stdio.h>

int main(int argc, char argv[])
{

	int a[5]; int i=0;

	int *b;
	b = (int*)malloc(5*sizeof(int));
	
	for(i=0; i<5; i++) 
	{
		a[i]=i;	
		b[i]=i*10;
	printf("%d\n", a[i]);
	printf("%d\n", b[i]); 
	}
	printf("%d %d\n", *a, *(a+3));
}
