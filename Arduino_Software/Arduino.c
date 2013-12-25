/*
2013 Copyright (c) Mostafa Mokhtar and Hossam Mahmoud  All right reserved.
 
Author: Mostafa Mokhtar, Hossam Mahmoud and Marwan Salem
 
This code is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.
  
This code has used some open source libraries from arduino 
and all rights of these libraries are reserved for arduino 
and authors
 
*/
 
 
/* Upload this sketch into Seeeduino and press reset*/
 
#include <SoftwareSerial.h>   //Software Serial Port
#define enable1 10
#define enable2 11
#define motor11 7
#define motor12 8
#define motor21 12
#define motor22 13
#define RxD 6
#define TxD 3
 
#define DEBUG_ENABLED  1
 
SoftwareSerial blueToothSerial(RxD,TxD);
char left = 'l';
char right = 'r';
int led = 13;
void setup() 
{ 
  Serial.begin(9600);
  pinMode(RxD, INPUT);
  pinMode(TxD, OUTPUT);
   pinMode(led,OUTPUT);
   pinMode(enable1,OUTPUT);
   pinMode(enable2,OUTPUT);
   pinMode(motor11,OUTPUT);
   pinMode(motor12,OUTPUT);
   pinMode(motor21,OUTPUT);
   pinMode(motor22,OUTPUT);
  setupBlueToothConnection();
  
 
}
// digital 7 8 12 13
// enable 11 10

 boolean isRight;
 boolean isLeft;
 int direction;    // 0 = stop // 1 = forward // 2 = right-forward
 int tempX = 0;
 int tempY = 0;
 byte x = 7 ;
 byte y = 7;
void loop() 
{ 
  
  byte recvChar;
  while(1){
    if(blueToothSerial.available()){//check if there's any data sent from the remote bluetooth shield
      recvChar = blueToothSerial.read();
       x = (recvChar&15);
       y = (recvChar>>4);
    //   Serial.print(x);
     // Serial.print(y);
      
      blueToothSerial.write(x);
      blueToothSerial.write(y);
       
    }
    if(Serial.available()){//check if there's any data sent from the local serial terminal, you can add the other applications here
      recvChar  = Serial.read();
      blueToothSerial.print(recvChar);
    }
    
    tempX = x - 7;
    tempY = y - 7;
    if(y>=x){
    if(y>9){
      moveFW();
    }else if(y<5){
      moveBW();
    }else if(x>9){
      moveLeft();
    }else if(x<5){
      moveRight();
    }else{
      Brake();
    }
    }else{
    if(x>9){
      moveLeft();
    }else if(x<5){
      moveRight();
    }else if(y>9){
      moveFW();
    }else if(y<5){
      moveBW();
    }else{
      Brake();
    }
    }
      
    
  }
} 
 
void setupBlueToothConnection()
{
  blueToothSerial.begin(38400); //Set BluetoothBee BaudRate to default baud rate 38400
  blueToothSerial.print("\r\n+STWMOD=0\r\n"); //set the bluetooth work in slave mode
  blueToothSerial.print("\r\n+STNA=SeeedBTSlave\r\n"); //set the bluetooth name as "SeeedBTSlave"
  blueToothSerial.print("\r\n+STOAUT=1\r\n"); // Permit Paired device to connect me
  blueToothSerial.print("\r\n+STAUTO=0\r\n"); // Auto-connection should be forbidden here
  delay(2000); // This delay is required.
  blueToothSerial.print("\r\n+INQ=1\r\n"); //make the slave bluetooth inquirable 
  Serial.println("The slave bluetooth is inquirable!");
  delay(2000); // This delay is required.
  blueToothSerial.flush();
}

void moveFW ()
{digitalWrite(enable1,HIGH);
 digitalWrite(enable2,HIGH);
 digitalWrite(motor11,HIGH);
 digitalWrite(motor12,LOW);
 digitalWrite(motor21,HIGH);
 digitalWrite(motor22,LOW);
  Serial.println("Forward");
}

void moveBW ()
{digitalWrite(enable1,HIGH);
 digitalWrite(enable2,HIGH);
 digitalWrite(motor11,LOW);
 digitalWrite(motor12,HIGH);
 digitalWrite(motor21,LOW);
 digitalWrite(motor22,HIGH);
  Serial.println("Backward");
}
void moveLeft ()
{digitalWrite(enable1,HIGH);
 digitalWrite(enable2,HIGH);
 digitalWrite(motor11,HIGH);
 digitalWrite(motor12,LOW);
 digitalWrite(motor21,LOW);
 digitalWrite(motor22,HIGH);
    Serial.println("Left");

}
void moveRight ()
{digitalWrite(enable1,HIGH);
 digitalWrite(enable2,HIGH);
 digitalWrite(motor11,LOW);
 digitalWrite(motor12,HIGH);
 digitalWrite(motor21,HIGH);
 digitalWrite(motor22,LOW);
    Serial.println("Right");

}
void Brake ()
{digitalWrite(enable1,HIGH);
 digitalWrite(enable2,HIGH);
 digitalWrite(motor11,HIGH);
 digitalWrite(motor12,HIGH);
 digitalWrite(motor21,HIGH);
 digitalWrite(motor22,HIGH);
  Serial.println("Brake");

 }

