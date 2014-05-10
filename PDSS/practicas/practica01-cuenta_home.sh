#!/bin/bash

# Practica 1:
# Script de practicas para la asignatura de PDSS del Master de Sistemas Telematicos.

echo ""
echo "1. Espacio usado en tu directorio HOME:"
echo ""

du -s -h ~

echo ""
echo "2. Los 5 directorios que mas ocupan:"
echo ""

du ~ | sort -r -n | head -6 | tail -5

echo ""
echo "3. Los 3 archivos más pesados:"
echo ""

du ~/ | sort -n | tail -4 | head -3

echo ""
echo "4. Scripts en tu directorio HOME:"
echo ""

find -iname "*.sh"
echo ""
echo "Total numero de scripts: "
find -iname "*.sh" | wc -l

echo ""
echo "Contando lineas de scripts...:"

find -iname "*.sh" > salida.txt


let A=0
while read line
do
cat $line | wc -l
A=$((A+$(cat $line | wc -l)))
done < salida.txt
rm salida.txt

echo ""
echo "Numero total de lineas de script: $A"
echo ""

