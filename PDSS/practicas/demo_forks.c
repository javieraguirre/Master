#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>

int main(int argc, char *argv[])
{

	int pid = 0;
	int i=0;

	for (i=0; i<2; i++) 
	{
		pid = fork();
		if (pid == 0)
		{
			printf("\nHijo: PID del proceso : %d\n", pid);
			execlp("xeyes", "xeyes", NULL);
			exit(0);
		}
		else
		{
			printf("\nPadre: PID del proceso de mi hijo: %d\n", pid);
			wait(NULL);
		}	
	}
	
	return 0;
}
