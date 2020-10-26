#include <Servo.h>//Using servo library to control ESC

Servo esc; //Creating a servo class with name as esc
void setup()

{
Serial.begin(9600);
esc.attach(9); //Specify the esc signal pin,Here as D8

esc.writeMicroseconds(1000); //initialize the signal to 1000

Serial.println("Completed Setup");
}

void loop()

{
  int val;
  while(Serial.available()>0){
    val=Serial.parseInt();
    if (val >0)
      break;
  }
    Serial.println(val);
    delay(100);
    val= map(val, 0, 100,1000,2000); //mapping val to minimum and maximum(Change if needed)

    esc.writeMicroseconds(val); //using val as the signal to esc
}
