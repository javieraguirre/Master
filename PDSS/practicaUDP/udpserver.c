#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>

#include <string.h>

#include <string.h>
#include <time.h>
#include <errno.h>
#include <ifaddrs.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <net/if.h>
#include <pthread.h>


#define rojo "\033[0;31m"
#define cyan "\033[0;36m"
#define azul "\033[0;34m"
#define reset "\033[0m"


char ip[15];

void *RxInterno(void *puerto);
//void *RxExterno(void *puerto);


// Descubridor de interfaces y sus IPs por Jason Stelzer:
// http://stackoverflow.com/questions/2021549/how-do-i-output-my-hosts-ip-addresses-from-a-c-program

// Se trata de una version del ejemplo del manual de C de Linux: #man getifaddrs

void show_address_info( struct ifaddrs *ifa )
	{
	struct sockaddr_in *s4;

	if (AF_INET == ifa->ifa_addr->sa_family)
		{
		s4 = (struct sockaddr_in *)(ifa->ifa_addr);
		if (NULL == inet_ntop(ifa->ifa_addr->sa_family, (void *)&(s4->sin_addr), ip, sizeof(ip)))
			{
			printf("%s: inet_ntop failed!\n", ifa->ifa_name);
			}
		else
			{
			printf("IPv4 addr %s: %s\n", ifa->ifa_name, ip);
			}
		}
	}



int main(int argc, char *argv[])
	{

	printf(cyan "\nComponente servidor de la practica final de PDSS\n" reset);

	int i, z, pid;
	char buff[1024];


	// Parte para conocer las interfaces e IPs:

	struct ifaddrs *myaddrs, *ifa;
	int status = getifaddrs(&myaddrs); // #man getifaddrs
	if (status != 0) { perror("getifaddrs failed!"); exit(1); }

	printf("\n\nConfiguracion IP:\n\n");

	for (ifa = myaddrs; ifa != NULL; ifa = ifa->ifa_next)
		{
		if (NULL == ifa->ifa_addr) { continue; }
		if ((ifa->ifa_flags & IFF_UP) == 0) { continue;	} // flags: #man netdeviceIFF_UP: interface running.
		show_address_info(ifa);
		}
	freeifaddrs(myaddrs);



	// Establecer direccion IP y puertos por defecto:

	int puerto = 42000;

	if (argc == 3) { if (strcmp(argv[1], "-p") == 0) { puerto = atoi(argv[2]); } }
	else if ((argc == 2) || (argc >= 4))
		{
		printf(rojo "Error: " reset);
		printf("muy pocos o demasiados argumentos.");
		printf("\nEste programa recibe lineas enviadas desde su cliente, a traves del protocolo UDP\n\n");
		printf("Uso: /udpserver.exec <puertos>\n\n");
		printf("\nAtiende en el bucle local 127.0.0.1 y en la IP externa.\n");
		printf("Los puertos se pueden especificar opcionalmente:\n");
		printf("por defecto es el 42000 para el interfaz interno y el 42001 para el externo.\n");
		printf("\nPara cambiar el puerto hay que saber que:\n");
		printf("- puerto para el interfaz interno: el que se especifique.\n");
		printf("- puerto para el interfaz externo: el especificado + 1.\n");
		printf("\nEjemplo: ./udpserver.exec 20000\n\n");
		printf("Esto hara que el servidor atienda en: 127.0.0.1:20000 y (por ej.) 192.168.1.1:20001.\n\n");
		return 0;
		}
	else { printf("\nUsando valores por defecto.\n"); }


	printf("\nIniciando:\n");

	
	// Crear threads:
	//fprintf(stderr, "pthread_t\n");	
	pthread_t hiloRxInterno;//, hiloRxExterno;

	//fprintf(stderr, "pthread_create\n");
	pthread_create(&hiloRxInterno, NULL, RxInterno, (void*) puerto);
	//pthread_create(&hiloRxExterno, NULL, RxExterno, (void*) (puerto+1));

	//fprintf(stderr, "pthread_join\n");
	pthread_join(hiloRxInterno, NULL);
	//pthread_join(hiloRxExterno, NULL);
	
	//fprintf(stderr, "exit(0)\n");
	exit(0);
	}


/*void *RxExterno(void *puerto)
	{
	int *pto, i;
	char buff[1024];
	pto = (int *) puerto;

	printf("Rx externo. PID: %d. ", getpid());
	printf("Escuchando: %s : %d.\n\n", ip, pto);

	// #man getaddrinfo

	int sk1;
	struct sockaddr_in srvaddr, cli1addr;

	memset(&srvaddr, 0, sizeof(srvaddr));
	memset(&cli1addr, 0, sizeof(cli1addr));

	sk1 = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);

	srvaddr.sin_family = AF_INET;
	srvaddr.sin_addr.s_addr = inet_addr(ip);
	srvaddr.sin_port = htons(pto);

	bind(sk1, (struct sockaddr*)&srvaddr, sizeof(srvaddr));
	int cl1 = sizeof(cli1addr);

	for(;;)
		{
		for (i=0; i<1024; i++) { buff[i] = '\0'; }
		recvfrom(sk1, buff, 255, 0, (struct sockaddr*)&cli1addr, &cl1);
		fprintf(stderr, buff);
		}
	}*/

void *RxInterno(void *puerto)
	{
	//fprintf(stderr, "int i, fd[2], pid\n");
	int i, fd[2], pid;

	//fprintf(stderr, "char recepcion[1024]\n");
	char recepcion[1024];
	
	//fprintf(stderr, "int * pto = (int *) puerto\n");
	int *pto;
	pto = (int *) puerto;

	fprintf(stderr, "\nRx interno. PID: %d. ", getpid());
	fprintf(stderr, "Escuchando: 127.0.0.1 : %d.\n", pto);

	//fprintf(stderr, "struct sockaddr_in svraddr, cliaddr\n");
	struct sockaddr_in srvaddr, cliaddr;

	//fprintf(stderr, "memset(&srvaddr...\n");
	// Reservar espacio de memoria para albergar las direcciones IP que manejaremos.
	memset(&srvaddr, 0, sizeof(srvaddr));
	memset(&cliaddr, 0, sizeof(cliaddr));
	
	//fprintf(stderr, "int sk = socket(PF_INET...\n");
	int sk = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);

	//fprintf(stderr, "srvadd.sin_family = AF_INET...\n"); 
	srvaddr.sin_family = AF_INET;
	srvaddr.sin_addr.s_addr = inet_addr("127.0.0.1");
	srvaddr.sin_port = htons(42000);

	//fprintf(stderr, "bind(sk, (struct sockaddr....\n");
	// Abrir Socket:
	bind(sk, (struct sockaddr*)&srvaddr, sizeof(srvaddr));
	

	for(;;)
		{
		// Limpiar buffer de recepcion:
		for (i=0; i<1024; i++) { recepcion[i] = '\0'; }

		// Recibir comando:
		int cliaddrlen = sizeof(cliaddr);

		//fprintf(stderr, "recvfrom(sk, recepcion...\n");
		recvfrom(sk, recepcion, sizeof(recepcion), 0, (struct sockaddr*)&cliaddr, &cliaddrlen);

		// Ejecutar y recoger la salida del comando:

		// La ejecucion se hace en un Fork.
		// La salida del comando se envia a traves de un Pipe al proceso padre.
		pipe(fd);
		pid = fork();

		// Ejecutar comando en el proceso hijo:
		if (pid == 0)
			{
			close(fd[0]);
			dup2(fd[1],1);
			close(fd[1]);

			fprintf(stderr, "Comando recibido:\t");
			fprintf(stderr, "%s", recepcion);
			fprintf(stderr, "\n");

			execlp(recepcion, recepcion, NULL);
				
			exit(0);
			}

		// Recoger la salida del comando en el proceso padre:
		else
			{
			close(fd[1]);
			while ((i = read(fd[0],recepcion,sizeof(recepcion))) != 0)
				{
				fprintf(stderr, "Salida del comando:\t");
				write(2,recepcion,i);
				fprintf(stderr, "\n");
				}
			close(fd[0]);
			wait(NULL);
			}

		// Enviar salida del comando:
		// usleep(1000000);
		sendto(sk, recepcion, sizeof(recepcion), 0, (struct sockaddr*)&cliaddr, cliaddrlen);
		// Cerrar socket:
		// close(sk);

		// Volver al inicio del bucle.
		//return 0;
		}
	}

