#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
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


#define rojo "\033[0;31m"
#define cyan "\033[0;36m"
#define azul "\033[0;34m"
#define reset "\033[0m"


/* Aqui se almacena el numero IP. El codigo original es ip[64] porque le tiene que 
caber una IPv6 entera. En esta implementacion solo se trabaja con IPv4, asi que
con ip[15] seria suficiente. */

char ip[15];


// Descubridor de interfaces y sus IPs por Jason Stelzer:
// http://stackoverflow.com/questions/2021549/how-do-i-output-my-hosts-ip-addresses-from-a-c-program

void imprimirConfiguracionIP()
	{

	struct ifaddrs *myaddrs, *ifa;
	int status;

	status = getifaddrs(&myaddrs);
	if (status != 0)
		{
		perror("getifaddrs failed!");
		exit(1);
		}

	printf("\n\nConfiguracion IP del cliente:\n\n");

	for (ifa = myaddrs; ifa != NULL; ifa = ifa->ifa_next)
		{
		if (NULL == ifa->ifa_addr) { continue; }
		if ((ifa->ifa_flags & IFF_UP) == 0) { continue;	}
		show_address_info(ifa);
		}
	freeifaddrs(myaddrs);
	}


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
			if (strcmp(ip, "lo"))
				{
				printf("IPv4 addr %s: %s\n", ifa->ifa_name, ip);
				}
			}
		}
	}



int main(int argc, char *argv[])
	{
	int i;
	int puerto = 42000;
	char destino[15] = "127.0.0.1";

	printf(cyan "\nComponente cliente de la practica final de PDSS\n" reset);


	// Establecer direccion IP y puerto de destino:

	//if ((argc == 5) || (argc == 7))
	if (argc > 2)
		{
		for (i=1; i<(argc-1); i++)
			{
			if ((strcmp(argv[i], "-p") == 0) /*|| (strcmp(argv[i], "-puerto") == 0)*/) { puerto = atoi(argv[(i+1)]); }
			if ((strcmp(argv[i], "-d") == 0) /*|| (strcmp(argv[i], "-direccion" == 0))*/)
				{
				int j; for (j=0; j<15; j++) { destino[j] = '\0'; }
				strcpy(destino, argv[(i+1)]);
				}
			}
		}

	else if (argc == 3) { printf("\nUsando valores por defecto"); }

	else
		{
		printf(rojo "\nError: " reset);
		printf("muy pocos o demasiados argumentos.");
		printf("\nEste programa enviara la salida multilinea de cualquier programa que se le pase como argumento hacia el componente servidor a traves del puerto 42000; y este proceso lo repite cada 3 segundos. El envio incluye la IP del cliente.\n");
		printf("\nUso: debes de proporcionar como argumento algun programa que escriba en la salida de la terminal.");
		printf("\nComandos disponibles:\n\t-p:\tDefinir puerto de comunicaciones. Por defecto, el 42000.");
		printf("\n\t-d:\tDefinir direccion IP de salida. Por defecto: 127.0.0.1.");
		printf("\n\t-app:\tAplicacion para ejecutar.");
		printf("\n\nEjemplo con un argumento simple: ./udpclient.exec date\nEjemplo con un argumento compuesto: ./udpclient.exec 'glxinfo | grep render'\n\n");

		return 0;
		}
	

	// Parte para conocer las interfaces e IPs:
	//imprimirConfiguracionIP();


	char pid[64];
	char charpuerto[5];
	//printf("\nProceso padre: %d\n", getppid());



	printf(rojo "\nEnviando a: " reset);
	printf("%s : %d\n", destino, puerto);



	// ####################################
	// #########  Bucle de envio:  ########
	// ####################################

	int proceso;
	char comando[1024];
	char buff[1024];

	int z;
	struct sockaddr_in srvaddr, cliaddr;

	memset(&srvaddr, 0, sizeof(srvaddr));
	memset(&cliaddr, 0, sizeof(cliaddr));

	int sk = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);

	cliaddr.sin_family = AF_INET;
	cliaddr.sin_addr.s_addr = inet_addr("127.0.0.1");
	cliaddr.sin_port = htons(puerto);
	bind(sk, (struct sockaddr*)&cliaddr, sizeof(cliaddr));

	srvaddr.sin_family = AF_INET;
	srvaddr.sin_addr.s_addr = inet_addr(destino);
	srvaddr.sin_port = htons(puerto);
	

	for (i=1; i<argc; i++)
		{
		if (strcmp(argv[i], "-app") == 0)
			{
			strcpy(comando, argv[(i+1)]);
			fprintf(stderr, "\nEncontrado el siguiente comando: %s\n", comando);

			for(;;)
				{
				// Enviar comando:
				int srvaddrlen = sizeof(srvaddr);
				sendto(sk, comando, strlen(comando), 0,	(struct sockaddr*)&srvaddr, srvaddrlen);

				// Limpiar buffer de recepcion:
				 for(i=0; i<sizeof(buff); i++) { buff[i] = '\0'; }

				// Esperamos a la recepcion de la salida del comando:
				recvfrom(sk, buff, sizeof(buff), 0, (struct sockaddr*)&srvaddr, &srvaddrlen);
				// Imprimir la recepcion:
				fprintf(stderr, "Recibido:\t");
				fprintf(stderr, "%s", buff);
				fprintf(stderr, "\n");
				usleep(100000); // tiempo en microsegundos, 1 seg = 1000000
				}
			}
		}
	//wait(NULL); // si el proceso padre no espera a sus forks, pierde el foco del terminal, y si se intenta terminar el programa con CTRL+C, no funcionara.

	return 0;
	}

