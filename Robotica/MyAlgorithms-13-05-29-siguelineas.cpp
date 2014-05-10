/*
 *  Copyright (C) 1997-2011 JDERobot Developers Team
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 *  Authors : Maikel González <m.gonzalezbai@gmail.com>,
 *
 */

#include "API.h"


int mediaold1, mediaold2;

namespace introrob {

    void Api::RunNavigationAlgorithm() {
        //printf("\ncrear variables");
        double v, w;
        jderobot::EncodersDataPtr encoders;
        int r,g,b;
        int x, y;

        imageCameras2openCV(); //Esta función es necesario llamarla ANTES de trabajar con las imágenes de las cámaras.
        IplImage src = *this->imageCameraLeft;; //Imagen de la cámara izquierda
        
        v = this->getMotorV(); w = this->getMotorW(); // Recoger valores actuales de los motores.

        system("clear");
        printf("\nvelocidad: %f \t vel.angular: %f", v, w);
       
        //FILTRO DE LA IMAGEN: convertimos a azul oscuro la linea del suelo, y eliminamos la rejilla y las sombras.
        // El filtro se aplica a partir de la fila 120 del frame de la imagen.
        for (y=120; y<240; y++) {
            for (x=0; x<320; x++) {
                r = (int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels];
                g = (int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+1];
                b = (int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+2];
                if        ((( r == 62) && ( g == 62) && ( b == 62))) // sombras de las paredes en el suelo
                          { src.imageData[(y * src.width + x) * src.nChannels] = 132; // suelo en claro
                            src.imageData[(y * src.width + x) * src.nChannels+1] = 132;
                            src.imageData[(y * src.width + x) * src.nChannels+2] = 132; }
                if        ((( r == 72) && ( g == 72) && ( b == 72)) // linea
                        || (( r == 80) && ( g == 80) && ( b == 80)) // línea y pared a la vez.
                        || (( r == 86) && ( g == 86) && ( b == 86)) // linea
                        || (( r == 79) && ( g == 79) && ( b == 79)) // linea
                        || (( r == 95) && ( g == 95) && ( b == 95)) // linea
                        || (( r == 97) && ( g == 97) && ( b == 97)) // linea
                        || (( r == 68) && ( g == 68) && ( b == 68)) // rejilla en sombra
                        || (( r == 71) && ( g == 71) && ( b == 71)) // rejilla en sombra
                        || (( r == 98) && ( g == 98) && ( b == 98)) // rejilla en claro
                        || (( r == 104) && ( g == 104) && ( b == 104)) // rejilla en claro
                        || (( r == 153) && ( g == 153) && ( b == 153)) // rejilla en claro
                        || (( r == 121) && ( g == 121) && ( b == 121)) // rejilla en claro
                        || (( r == 115) && ( g == 115) && ( b == 115))) // linea
                          { src.imageData[(y * src.width + x) * src.nChannels] = 80; // azul oscuro
                            src.imageData[(y * src.width + x) * src.nChannels+1] = 0;
                            src.imageData[(y * src.width + x) * src.nChannels+2] = 0; } } }



        //filtro: ensanchar pixeles independientes que han quedado vivos despues del filtro anterior. i-1 pasadas.
        for (int i=0; i<2; i++) { // Pasada horizontal
                for (y=120; y<240; y++) {
                    for (x=0; x<319; x++) {
                        if      ( ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels] != 80)
                                && ((int) (unsigned char) src.imageData[(y * src.width + (x+1)) * src.nChannels] == 80) )
                                {       (src.imageData[(y * src.width + (x)) * src.nChannels] = 80);
                                        (src.imageData[(y * src.width + (x)) * src.nChannels+1] = 0);
                                        (src.imageData[(y * src.width + (x)) * src.nChannels+2] = 0); } } } }
        for (int i=0; i<2; i++) { // Pasada vertical
                for (y=120; y<239; y++) {
                    for (x=0; x<320; x++) {
                        if      ( ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels] != 80)
                                && ((int) (unsigned char) src.imageData[((y+1) * src.width + (x)) * src.nChannels] == 80) )
                                {       (src.imageData[(y * src.width + (x)) * src.nChannels] = 80);
                                        (src.imageData[(y * src.width + (x)) * src.nChannels+1] = 0);
                                        (src.imageData[(y * src.width + (x)) * src.nChannels+2] = 0); } } } }
       

        //filtro: recortar pixeles para limpiar pixeles independientes. Los convertimos a suelo. Hacemos i-1 pasadas.
        for (int i=0; i<2; i++) { // Pasada horizontal       
                for (y=120; y<240; y++) {
                    for (x=0; x<319; x++) {
                        if      (  ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels] == 80)
                                && ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+1] == 0)
                                && ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+2] == 0)
                                && ((int) (unsigned char) src.imageData[(y * src.width + (x+1)) * src.nChannels+1] != 0)
                                && ((int) (unsigned char) src.imageData[(y * src.width + (x+1)) * src.nChannels+2] != 0) )
                                {       (src.imageData[(y * src.width + x) * src.nChannels] = 132);
                                        (src.imageData[(y * src.width + x) * src.nChannels+1] = 132);
                                        (src.imageData[(y * src.width + x) * src.nChannels+2] = 132); } } } }
        for (int i=0; i<2; i++) { // Pasada vertical
                for (x=0; x<320; x++) {
                    for (y=0; y<239; y++) {
                        if      (  ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels] == 80)
                                && ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+1] == 0)
                                && ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+2] == 0)
                                && ((int) (unsigned char) src.imageData[((y+1) * src.width + x) * src.nChannels+1] != 0)
                                && ((int) (unsigned char) src.imageData[((y+1) * src.width + x) * src.nChannels+2] != 0) )
                                {       (src.imageData[(y * src.width + x) * src.nChannels] = 132);
                                        (src.imageData[(y * src.width + x) * src.nChannels+1] = 132);
                                        (src.imageData[(y * src.width + x) * src.nChannels+2] = 132); } } } }

        // Dibujamos el centro de la linea de color rojo claro, y a su vez, conseguimos varios valores sobre la linea.
        int izda[240] = {0}, centro[240]={0}, dcha[240]={0}; y=0; int inf=0, sup=0;
        for (y=120; y<240; y++) {
                for (x=0; x<320; x++) {
                        if (    ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels] == 80) &&
                                ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+1] == 0) &&
                                ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+2] == 0)   )
                                { dcha[y] = x; } }
                for (x=319; x>=0; x--) {
                        if (    ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels] == 80) &&
                                ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+1] == 0) &&
                                ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+2] == 0)   )
                                { izda[y] = x; } }
                centro[y] = izda[y] + ((dcha[y] - izda[y])/2);
                if ((centro[y] != 0)    && (((int) (unsigned char) src.imageData[(y * src.width + (centro[y])) * src.nChannels] == 80)
                                        && ((int) (unsigned char) src.imageData[(y * src.width + (centro[y])) * src.nChannels+1] == 0)
                                        && ((int) (unsigned char) src.imageData[(y * src.width + (centro[y])) * src.nChannels+2] == 0))) {
                                        src.imageData[(y * src.width + (centro[y])) * src.nChannels] = 0;
                                        src.imageData[(y * src.width + (centro[y])) * src.nChannels+1] = 0;
                                        src.imageData[(y * src.width + (centro[y])) * src.nChannels+2] = 255;
                                        inf = y; if (sup == 0) { sup = y; } } }

        // Dibujar líneas verdes que permiten ver de forma intuitiva el rango de valores que se están tomando.
        int dibujartopes = 1; if (dibujartopes == 1) {
                y = 119; for (x=0; x<320; x++) { src.imageData[(y * src.width + x) * src.nChannels+1] = 80; } // línea que marca el principio desde se procesa la imagen.
                y = sup; for (x=0; x<320; x++) { src.imageData[(y * src.width + x) * src.nChannels+1] = 80; } // línea que marca el primer valor obtenido.
                y = inf; for (x=0; x<320; x++) { src.imageData[(y * src.width + x) * src.nChannels+1] = 80; } } // línea que marca el último valor obtenido.


        /* Ajustar valores del centro de la linea de cada fila de pixeles al centro del robot. */
        centro[sup] = -(centro[sup] - 180);
        centro[inf] = -(centro[inf] - 180);


        // media aritmetica de los centros de la linea para cada fila de pixeles.
        int media=0, muestras=0, hacermedia=1;
                if (hacermedia==1) {
                        for (int i=120; i<240; i++) {
                                if (centro[i] != 0) { media = media + centro[i]; muestras++;} }
                        if (muestras != 0) { media = media / muestras; }
                        for (y=120; y<240; y++) {                
                                src.imageData[(y * src.width + media) * src.nChannels] = 0;
                                src.imageData[(y * src.width + media) * src.nChannels+1] = 255;
                                src.imageData[(y * src.width + media) * src.nChannels+2] = 0;
                                src.imageData[(y * src.width + mediaold1) * src.nChannels] = 0;
                                src.imageData[(y * src.width + mediaold1) * src.nChannels+1] = 100;
                                src.imageData[(y * src.width + mediaold1) * src.nChannels+2] = 0;
                                src.imageData[(y * src.width + mediaold2) * src.nChannels] = 0;
                                src.imageData[(y * src.width + mediaold2) * src.nChannels+1] = 80;
                                src.imageData[(y * src.width + mediaold2) * src.nChannels+2] = 0; } }
        mediaold2 = mediaold1; mediaold1 = media;
        media = media -180; printf("\nmedia: %i\tmuestras: %i", media, muestras);



        // Resolver velocidades.
        if (muestras > 10) {
                w = -(media * 0.1); // 0.1
                v = muestras * 0.75; } // 0.75
        else { inf=0; }



        // MODO SIN LINEA
        int permitirsinlinea = 1;
        if ((inf == 0) && (permitirsinlinea == 1)) {
                printf("\nLinea perdida");
                int izdasup=0, izdainf=0, dchasup=0, dchainf=0, suelo=0, cielo=0;

                // Filtro de la imagen: convertimos obstaculos en blanco, vacio en negro.
                for (y=0; y<240; y++) {
                    for (x=0; x<320; x++) {
                        r = (int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels];
                        g = (int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+1];
                        b = (int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels+2];
                        if      (((r >= 80) && (g >= 80) && (b >= 80))
                                || ((r == 80) && (g == 0) && (b == 0))
                                || ((r == 0) && (g > 0) && (b == 0))
                                || ((r == 0) && (g == 0) && (b == 255))) {
                                    src.imageData[(y * src.width + x) * src.nChannels] = 0;
                                    src.imageData[(y * src.width + x) * src.nChannels+1] = 0;
                                    src.imageData[(y * src.width + x) * src.nChannels+2] = 0; }
                        else {
                            src.imageData[(y * src.width + x) * src.nChannels] = 255;
                            src.imageData[(y * src.width + x) * src.nChannels+1] = 255;
                            src.imageData[(y * src.width + x) * src.nChannels+2] = 255; } } }

                x=160;  for (y=0; y<240; y++) {
                                if ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels] == 0) {
                                src.imageData[(y * src.width + x) * src.nChannels] = 255;
                                cielo++; } else { suelo = y; break; } }
                y = 140; x = 160; do {
                                x--; izdasup++;
                                if ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels] == 0) {
                                        src.imageData[(y * src.width + x) * src.nChannels] = 255; }
                                else { x=0; }
                                } while (x!=0);
                y = 140; x = 160; do {
                                x++; dchasup++;
                                if ((int) (unsigned char) src.imageData[(y * src.width + x) * src.nChannels] == 0) {
                                        src.imageData[(y * src.width + x) * src.nChannels] = 255; }
                                else { x=319; }
                                } while (x!=319);
                suelo = (suelo+1); x = 240-cielo; cielo = suelo-x;
                w = (izdasup - dchasup)/8; v = 30; // (cielo/3)+2;
                if ((izdasup == dchasup) && (cielo <= 20) && ((izdasup + dchasup) <= 20)) { w = -5; } }



        // Establecer velocidades.
        this->setMotorW(w);
        this->setMotorV(v);

    }

    
    void Api::RunGraphicsAlgorithm() {
        /* TODO: ADD YOUR GRAPHIC CODE HERE */
        CvPoint3D32f aa, bb;
        CvPoint3D32f a, b;
        CvPoint3D32f c, d;
        CvPoint3D32f color;
        xmlReader(&myCamA, "cameras/calibA.xml");
        xmlReader(&myCamB, "cameras/calibB.xml");

        bb.x = this->destino.x;
        bb.y = this->destino.y;
        bb.z = 0.;

        aa.x = encodersData->robotx;
        aa.y = encodersData->roboty;
        aa.z = 0;

        color.x = 1.; // Red
        color.y = 0.; // Green
        color.z = 0.; // Blue
        this->pintaSegmento(aa, bb, color); // ROJO - Pinta un segmento desde el punto "aa" hasta el punto "bb"
        this->pintaDestino(aa, bb, color); // ROJO - Marca con una estrella el destino seleccionado al hacer click con el botón central en el mundo 3D.
        this->drawSphere(bb, color);

        }
}



