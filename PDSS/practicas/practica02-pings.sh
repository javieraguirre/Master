#!/bin/bash

# Pinger que recibe de una lista nombres o IPs de maquinas a las que hacer ping.

#echo off

# Si el argumentos es -hosts o -host:

if [ $1 = "-hosts" ] || [ $1 = "-host" ]
then
	echo ""
	echo "Haciendo ping a las serie de maquinas especificadas por linea de comando:"
	echo ""

	for i in $2 $3 $4 $5 $6 $7 $8 $9
	do ping -w 1 $i >/dev/null 2>&1
	if [ $? = 0 ]
	then
		echo ""
		echo $i " $(tput setaf 1)fuera de linea.$(tput sgr0)"
	else
		echo ""
		echo $i " $(tput setaf 2)en linea.$(tput sgr0)"
	fi
	done

# Si el argumento es -file:

elif [ $1 = "-file" ]
then
	echo ""
	echo "Haciendo ping a las maquinas especificadas en el archivo " $2 ":"
	echo ""

	ls $2 >/dev/null 2>&1
	if [ $? != 0 ]
	then
		echo "Archivo " $2 " no encontrado"
		echo ""
		exit 0
	fi

	

	while read line
	do
	ping -w 1 $line >/dev/null 2>&1
	if [ $? != 0 ]
	then
		echo ""
		echo $line " $(tput setaf 1)fuera de linea.$(tput sgr0)"
	else
		echo ""
		echo $line " $(tput setaf 2)en linea.$(tput sgr0)"
	fi
	done < $2

# Si el argumento es directamente un nombre de host o una IP:

else
	echo ""
	echo "Haciendo ping a la maquina especificadas por linea de comandos:"
	echo ""

	ping -w 1 $1 >/dev/null 2>&1
	if [ $? != 0 ]
	then
		echo ""
		echo $1 " $(tput setaf 1)fuera de linea.$(tput sgr0)"
	else
		echo ""
		echo $1 " $(tput setaf 2)en linea.$(tput sgr0)"
	fi
fi

echo ""
