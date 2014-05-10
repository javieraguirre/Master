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



// REVISAR EL PASO DE POLARES A CARTESIANAS
// PRIMERO PRUEBA CON UN SOLO VECTOR DE REPULSION
// DIBUJA TAMBIEN LA FUERZA DE DESTINO.

// destino: Variable que almacena las coordenadas de la pocición seleccionada en el mundo 3D con el botón central del ratón.


#include "API.h"
#include "math.h"


namespace introrob {

CvPoint3D32f repulsiva;
CvPoint3D32f repulsiva1[180];
CvPoint3D32f destinoTrabajo;
CvPoint3D32f resultante;
CvPoint3D32f oldDestino;
CvPoint3D32f destino3D;
int distarray[180];
bool cavidades = false;         // Técnica para ocultar huecos y caminos muy estrechos; no usar, no funciona bien.
bool verObstaculos = false;
bool destTrunc = false;
bool departamental;             // Se puede forzar el modo con un true o un false.
int iteraciones;


    void Api::RunNavigationAlgorithm() {

        jderobot::LaserDataPtr laser = getLaserData(); // Get the laser info
        jderobot::EncodersDataPtr myPosition = this->getEncodersData();
        this->robotx = myPosition->robotx; this->roboty = myPosition->roboty; this->robottheta = myPosition->robottheta;
        double v = this->getMotorV(), w = this->getMotorW();

        double anguloRepulsivo;
        int muestrasCampoAlfrente = 0; double campoAlfrente; // TODO: éstas variables no tienen uso.

        int distancia=0, muestras=0, colision=0;

        double longitud;
        int desplazamiento;
        int truncar;
        double multiplicador;

        system("clear");



        // Determinar en qué tipo de escenario estamos:

        for (int i=0; i<180; i++) {
                distancia = laser->distanceData[i];
                if ((distancia >= 1) && (distancia <= 3000)) { muestras++; }
                if (distancia < 100) { colision = 1; } }
        printf("\nNº de muestras del láser: %i", muestras);

        if (muestras >= 40) { iteraciones++; } else { iteraciones--; }
        if (iteraciones >= 70) { iteraciones = 70; }
        if (iteraciones <= 0) { iteraciones = 0; }

        printf("\niteraciones: %i\n", iteraciones);

        if ((iteraciones >= 10) || (departamental == true)) {   // Modo entorno cerrado
                departamental = true;
                longitud = 1000;
                truncar = 1500;
                desplazamiento = 0;
                multiplicador = 2; }

        if ((iteraciones <= 0) || (departamental == false)) {   // Modo campo abierto
                departamental = false;
                longitud = 2000;
                truncar = 3000;
                desplazamiento = 0;
                multiplicador = 1; }

        if (departamental == true) { printf("\nModo entorno cerrado\n"); }
        else if (departamental == false) { printf("\nModo campo abierto\n"); }



        // DESTINO:

        destinoTrabajo.x = destino.x; destinoTrabajo.y = destino.y; destinoTrabajo.z = 0;

        this->absolutas2relativas(destinoTrabajo, &destinoTrabajo);

        double distObjetivo = sqrt(pow(destinoTrabajo.x,2) + pow(destinoTrabajo.y,2)); // obtener distancia al objetivo: la hipotenusa del triangulo de x e y.
        double anguloDestino = acos(destinoTrabajo.x / distObjetivo); if (destinoTrabajo.y < 0) { anguloDestino = - anguloDestino; } if (anguloDestino != anguloDestino) { anguloDestino = 0; }

        printf("\nDestino real: [%f, %f], angulo: %f", (double) destino.x, (double) destino.y, (double) anguloDestino);



        // Truncamos el vector destino a una distancia máxima de [truncar] por componente:

        if ((fabs(destinoTrabajo.x) > truncar) || (fabs(destinoTrabajo.y) > truncar)) {
                oldDestino.x = destinoTrabajo.x;        oldDestino.y = destinoTrabajo.y;
                if (fabs(destinoTrabajo.x) >= fabs(destinoTrabajo.y)) {
                        destinoTrabajo.y = oldDestino.y * (truncar / fabs(oldDestino.x));
                        destinoTrabajo.x = truncar * (oldDestino.x / fabs(oldDestino.x)); }
                else {  destinoTrabajo.x = oldDestino.x * (truncar / fabs(oldDestino.y));
                        destinoTrabajo.y = truncar * (oldDestino.y / fabs(oldDestino.y)); }
                destTrunc = true; }

        else {  destTrunc = false; }

        printf("\nDestino trabajo: [%f, %f], angulo: %f", (double) destinoTrabajo.x, (double) destinoTrabajo.y, (double) anguloDestino);
        if (destTrunc = true) { printf(", truncado"); }
        printf("\n");




        // Rellenar cavidades de la percepcion láser: TODO: mejorar este proceso
/*        if (cavidades == true) {

                verObstaculos = true;

                muestras = 0;

                for (int i=91; i<180; i++) {
                        distarray[i] = laser->distanceData[i];
                        if ((distarray[i] <= 9000) && (distarray[i-1] <= 9000)) {
                                if (((distarray[i] - distarray[i-1]) > 100)) {
                                        muestras++;
                                        distarray[i] = distarray[i-1] + 10 * muestras;
                                        if (muestras > 20) { muestras = 0; continue; } } } }

                muestras = 0;

                for (int i=90; i>=0; i--) {
                        if ((distarray[i-1] <= 9000) && (distarray[i] <= 9000)) {
                                if (((distarray[i-1] - distarray[i]) > 100)) {
                                        muestras++;
                                        distarray[i-1] = distarray[i] + 10 * muestras;
                                        if (muestras > 20) { muestras = 0; continue; } } } } } */




        // REPULSION

        muestras = 0;
        for (int i=0; i<180; i++) {

                repulsiva1[i].x = 10000; repulsiva1[i].y = 10000; // Necesario para ver los vectores repulsivos evaluados en el mundo 3D.

                if (cavidades == true) { distancia = distarray[i]; }
                else { distancia = laser->distanceData[i]; }

//                if ((i >= 70) && (i < 110)) { campoAlfrente = campoAlfrente + distancia; muestrasCampoAlfrente++; }

                if (distancia <= longitud) {

                        if (distancia <= 200) { colision = 1; multiplicador = multiplicador * 2; }

                        anguloRepulsivo = (i * (PI/180)) - PI/2; // convierte de grados a radianes y alinea con el robot.

                        repulsiva.x = repulsiva.x + (cos(anguloRepulsivo) * (longitud-distancia+desplazamiento)*(pow(2,distancia*0.001)*multiplicador));
                        repulsiva.y = repulsiva.y + (sin(anguloRepulsivo) * (longitud-distancia+desplazamiento)*(pow(2,distancia*0.001)*multiplicador));

                        repulsiva1[i].x = (cos(anguloRepulsivo) * distancia); repulsiva1[i].y = (sin(anguloRepulsivo) * distancia); 

                        muestras++; } }
        
        repulsiva.x = (repulsiva.x / muestras);
        repulsiva.y = (repulsiva.y / muestras);

        anguloRepulsivo = acos(repulsiva.x / sqrt(pow(repulsiva.x,2) + pow(repulsiva.y,2))); if (anguloRepulsivo != anguloRepulsivo) { anguloRepulsivo = 0; }       

        if (muestras <= 2) { repulsiva.x = 0; repulsiva.y = 0; anguloRepulsivo = 0;}

        printf("\nIntegral repulsivo: [%f, %f], angulo: %f", (double) repulsiva.x, (double) repulsiva.y, (double) anguloRepulsivo);
        printf("\n");




        // RESULTANTE:

        resultante.x = destinoTrabajo.x - repulsiva.x;
        resultante.y = destinoTrabajo.y - repulsiva.y;

        if (muestras == 0) { resultante.x = destinoTrabajo.x; resultante.y = destinoTrabajo.y; }

        double anguloResultante = acos(resultante.x / sqrt(pow(resultante.x,2) + pow(resultante.y,2)));
        if (anguloResultante != anguloResultante) { anguloResultante = 0; }
        if (resultante.y < 0) { anguloResultante = - anguloResultante; }

        printf("\nResultante = [%f, %f], angulo: %f", (double) resultante.x, (double) resultante.y, (double) anguloResultante);
        printf("\n");




        //Orientar el robot:


        w = resultante.y * 0.01;
        
        if (fabs(resultante.y) < 300) { v = fabs(resultante.x) * 0.01; }

        if ((fabs(resultante.y) < 300) && (resultante.x < 0)) { v = 0; w = - w; printf("\nDando la vuelta"); }

        if ((resultante.x < truncar*0.333) && (resultante.y < truncar*0.333)) { v = v * 0.5; printf("\nAproximándose al destino"); }
        else if ((resultante.x < truncar*0.033) && (resultante.y < truncar*0.033)) { w = 0; printf("\nJunto al destino"); }

/*        if (resultante.y >= 500) { v = 0; w = 3; printf("\nGirando hacia la izda"); }
        else if ((resultante.y >= 200) && (resultante.y < 500)) { v = 15; w = 1; printf("\nVirando hacia la izda sin detenerse"); }
        else if ((resultante.y <= 200) && (resultante.y > 100) && (resultante.x > 0)) { v = 20; w = 0.5; printf("\nLlendo de frente corrigiendo hacia la izda"); }
        else if ((resultante.y <= 100) && (resultante.y >= -100) && (resultante.x > 0)) { v = 30; w = (resultante.x/fabs(resultante.x))*0.25; printf("\nDe frente"); }
        else if ((resultante.y >= -200) && (resultante.y > -200) && (resultante.x > 0)) { v = 20; w = -0.5; printf("\nLlendo de frente escorandose a la dcha"); }
        else if ((resultante.y <= -200) && (resultante.y > -500)) { v = 15; w = -1; printf("\nVirando hacia la dcha"); }
        else if (resultante.y <= -500) { v = 0; w = -3; printf("\nGirando hacia la dcha"); }

//        if ((fabs(resultante.x) < 300) && (resultante.y < 0)) { w = - w; } // Dar la vuelta.
        if ((resultante.y <= 100) && (resultante.y >= -100) && (resultante.x < 0)) { w = - w; printf("\nDar la vuelta"); }

        if (resultante.x < longitud*0.333) { v = v * 0.5; printf("\nJunto al destino"); }*/




        // ACTUAR:

        this->setMotorW(w);
        this->setMotorV(v);

        printf("\nvelocidad: %f \tvel.angular: %f", v, w);
    }

 

   
    void Api::RunGraphicsAlgorithm() {

        CvPoint3D32f vect, color;
        CvPoint3D32f origen; origen.x = encodersData->robotx; origen.y = encodersData->roboty; origen.z = 0;
        CvPoint3D32f objetivo; objetivo.x = destino.x; objetivo.y = destino.y; objetivo.z = 0;
        xmlReader(&myCamA, "cameras/calibA.xml"); xmlReader(&myCamB, "cameras/calibB.xml");


        // Destino absoluto:

        color.x = 0.5; color.y = 0.5; color.z = 0.5; // rojo, verde, azul
        this->pintaSegmento(origen, objetivo, color); // ROJO - Pinta un segmento desde el punto "aa" hasta el punto "bb"
        this->pintaDestino(origen, objetivo, color); // ROJO - Marca con una estrella el destino seleccionado al hacer click con el botón central en el mundo 3D.
        this->drawSphere(objetivo, color);


        // Destino de trabajo relativo:

        color.x = 1.; color.y = 1.; color.z = 1.; // blanco
        this->relativas2absolutas(destinoTrabajo, &destinoTrabajo);
        this->pintaSegmento(origen, destinoTrabajo, color);
        this->drawSphere(destinoTrabajo, color);


        // Repulsion media:

        repulsiva.z = 0;
        color.x = 0.; color.y = 0.; color.z = 0.; // negro
        this->relativas2absolutas(repulsiva, &vect);
        this->pintaSegmento(origen, vect, color);


        // Vectores relevantes del láser:

        color.x = 1.; color.y = 0.; color.z = 0.; // rojo
        for (int i=0; i<180; i++) {
                if ((repulsiva1[i].x != 10000) || (repulsiva1[i].y != 10000)) {
                        relativas2absolutas(repulsiva1[i], &vect);
                        this->pintaSegmento(origen, vect, color); } }


        // Resultante:

        resultante.z = 0;
        color.x = 0.; color.y = 0.; color.z = 1.; // azul
        this->relativas2absolutas(resultante, &vect);
        this->pintaSegmento(origen, vect, color);


        // Obstáculos leídos por el láser:

        if (verObstaculos == true) {
        CvPoint3D32f distarray2; double angulo;
        color.x = 0.8; color.y = 0.; color.z = 0.; // rojo

        for (int i=0; i<180; i++) {
                        angulo = (i * (PI/180)) - PI/2; // convierte de grados a radianes y alinea con el robot.
                        distarray2.x = (cos(angulo) * distarray[i]);
                        distarray2.y = (sin(angulo) * distarray[i]);
                        relativas2absolutas(distarray2, &vect);
                        this->drawSphere(vect, color); } }
        }
}


