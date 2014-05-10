#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>


// Programa que crea pipes entre todos los programas que se le pasan como argumentos.


int main(int argc, char *argv[])
	{

	int i;
	char c;
	int p[2];
	int pid;

	pipe(p);
	pid = fork();

	//for (i=1; i<argc; i++)
		{
		if (pid == 0)
			{
			close(p[0]);
			//dup2(p[1],1);
			//close(p[1]);
			execlp(argv[1], argv[1], NULL);
			exit(0);
			}

		else
			{
			close(p[1]);
			execlp(argv[2], argv[2], NULL);
			//close(p[0]);
			//wait(NULL);
			}


		}
	return 0;
	}
