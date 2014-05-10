#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>

// Practica 6:	Demonio que mantiene en ejecucion 5 programas a traves de forks.

int main(int argc, char *argv[])
{

	int pid = 0;
	int status;
	int i=0;

	int xload = 0;
	int xeyes = 0;
	int xlogo = 0;
	int xcalc = 0;
	int xclock = 0;

	do
	{
	i++;

		if (pid == 0)
			{
	
			if ((pid == 0))
				{
				xload = 1;
				printf(", ejecutando XLOAD\n");
				execlp("xload", "xload", NULL);
				exit(0);
				}

			}

		else
			{
			printf("\ni: %d\tPID: %d", i, pid);
			printf("Pulse CTRL + C aqui para detener el demonio.\n");
			wait(NULL);
			}
	}

	while (pid != 0);

	return 0;
}
