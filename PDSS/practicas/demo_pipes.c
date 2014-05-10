#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>

int main(int argc, char *argv[])
{

	int n;
	char buffer[1024];
	int fd[2];
	int pid;

	pipe(fd);				// Creo un Pipe para la variable fd.
	pid = fork();

	if (pid == 0)				// Si el proceso es el hijo:
		{
		close(fd[0]);			// Cierro el extremo de lectura del Pipe, porque lo que voy a hacer es escribir.
		dup2(fd[1],1);			// Duplico el extremo de escritura de fd.
		close(fd[1]);			// Cuando termina de leer, cierro el extremo de escritura de fd.
		execlp("ps", "ps", NULL);
		exit(0);
		}

	else					// Si el proceso es el padre:
		{
		close(fd[1]);			// Cierro el extremo de escritura, que lo que vamos a hacer es leer.

		while ((n = read(fd[0],buffer,1024)) != 0)	// Mientras haya algun dato en n, (que no sea 0)...
			{
			write(1,buffer,n);	// ...escribo.
			}

		close(fd[0]);			// Cierro el extremo de lectura del pipe.
		wait(NULL);
		}
	
	return 0;
}
