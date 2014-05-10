#!/bin/bash

# Lector de ficheros de log del servidor Apache.

# Ordenar y mostrar los numeros IP que aparecen en el log:

echo ""; echo "Numeros IP de maquinas que han accedido al servidor:"; echo ""

while read -a word
do
echo "${word[0]}" >> $1.ips.temp
done < $1

cat $1.ips.temp | sort -n -u


# Numero de visitas por hora

echo ""; echo "Fecha y hora de solicitudes al servidor:"; echo ""

while read -a word
do
echo "${word[3]} ${word[4]}" >> $1.fechas.temp
done < $1

cat $1.fechas.temp | sort -u | while read i; do echo $i ; done
rm $1.fechas.temp


# Lista de paises que han accedido

echo ""; echo "Paises que han accedido al servidor:"; echo ""

while read line
do
then
geoiplookup $line >> $1.paises.temp
done < $1.ips.temp

cat $1.paises.temp | sort -u

rm $1.paises.temp
rm $1.ips.temp


