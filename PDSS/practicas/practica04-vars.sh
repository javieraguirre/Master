#!/bin/bash

# Script que imprime en pantalla variables del entorno.

# -h: devuelve la ruta al home.

if [ $1 == "-h" ]
then
	echo ""; echo "La ruta hacia su directorio HOME es: "
	echo $HOME
fi

# -u: devuelve el nombre de usuario.

if [ $1 == "-u" ]
then
	echo ""; echo "El usuario actual es: "
	echo $USER
fi

# -v NAME: devuelve la variable de entorno de la clave NAME.

if [ $1 == "-v" ]
then
	echo ""; echo "La variable de entorno " $2 " es: "
	echo $2
fi

