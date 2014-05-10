String comdata = "\0";
int sensor0=0;
int valor0=0;
int sensor1=0;
int valor1=0;
int diferencia=0;

void setup(){
  pinMode(2,OUTPUT);
  pinMode(3,OUTPUT);
  pinMode(4,OUTPUT);
  pinMode(5,OUTPUT);
  pinMode(6,OUTPUT);
  delay(100);
  Serial.begin(115200);
  
  Serial.println("set bt pagemode 3 2000 1"); // disp descubrible y conectable
  Serial.println("set bt name arduinobt10");
  Serial.println("set bt class 011100"); // tipo de disp: uno creado por nosotros
  Serial.println("set bt role 0 f 7d00");
  Serial.println("set control echo 0");
  Serial.println("set bt auth * 12345");
  Serial.println("set control escape - 00 1");
  Serial.println("set control baud 115200,8n1");
  Serial.println("set profile spp aguirrexu");
  }

void loop(){
  
// Cast del dato del puerto serie
while (Serial.available() > 0)  
   {
   comdata += char(Serial.read());
   delay(100);
   }
  
  int sizecomdataArr = comdata.length() + 1;
  char comdataArr[sizecomdataArr];
  comdata.toCharArray(comdataArr,sizecomdataArr);
    
  // Controlar unos cuantos leds directamente:
  if (strcmp(comdataArr, "@41")  == 0) { digitalWrite(4,HIGH); }
  if (strcmp(comdataArr, "@40")  == 0) { digitalWrite(4,LOW);  }
  if (strcmp(comdataArr, "@51")  == 0) { digitalWrite(5,HIGH); }
  if (strcmp(comdataArr, "@50")  == 0) { digitalWrite(5,LOW);  }
  if (strcmp(comdataArr, "@61")  == 0) { digitalWrite(6,HIGH); }
  if (strcmp(comdataArr, "@60")  == 0) { digitalWrite(6,LOW);  }
      
  // Solicitar datos en bruto de los sensores:
  if (strcmp(comdataArr, "@s1")  == 0) { sensor0 = analogRead(0); Serial.println(sensor0); }  
  if (strcmp(comdataArr, "@s2")  == 0) { sensor1 = analogRead(1); Serial.println(sensor1); }
      
  // Solicitar diferencia de temperatura entre los 2 sensores. Hace una media de 5 lecturas.
  if (strcmp(comdataArr, "@d")  == 0)
      {
      valor0 = 0;
      valor1 = 0;
      for (int i=0; i<5; i++) {
          sensor0 = analogRead(0);
          valor0 = (valor0 + sensor0)/(i+1);
          }
      valor0 = (int)valor0 * (500/1024);
      for (int i=0; i<5; i++) {
          sensor1 = analogRead(1);
          valor1 = (valor1 + sensor1)/(i+1);
          }
      valor1 = (int)valor1 * (500/1024);
      diferencia = valor1 - valor0;
      Serial.println(diferencia);
      }
      
  // Solicitar valor de temperatura del sensor conectado al pin 0. Hace una media de 5 lecturas.
  if (strcmp(comdataArr, "@v1")  == 0)
      {
      valor0 = 0;
      for (int i=0; i<5; i++) {
          sensor0 = analogRead(0);
          valor0 = (valor0 + sensor0)/(i+1);
          }
      valor0 = (int)valor0 * (500/1024);     
      Serial.println(valor0);
      }
   
  // Solicitar valor de temperatura del sensor conectado al pin 1. Hace una media de 5 lecturas.
  if (strcmp(comdataArr, "@v2")  == 0)
      {
      valor1 = 0;
      for (int i=0; i<5; i++) {
          sensor1 = analogRead(1);
          valor1 = (valor1 + sensor1)/(i+1);
          }
      valor1 = (int)valor1 * (500/1024);     
      Serial.println(valor1);
      }   

  // Solicitar estado de los puertos digitales del 4 a 6.
  if (strcmp(comdataArr, "@p")  == 0) { for (int i= 4; i<=6; i++) { Serial.print(digitalRead(i)); } Serial.println(); }

  // Vaciamos la variable que dispara 
  comdata = "";
}

