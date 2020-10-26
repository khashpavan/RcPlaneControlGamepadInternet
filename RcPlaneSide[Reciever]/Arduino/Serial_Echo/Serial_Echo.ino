
char a[4];

void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
}
void loop() {
  char incomingByte;
   // If there is a data stored in the serial receive buffer, read it and print it to the serial port as human-readable ASCII text.
  if(Serial.available()){ 
    int i=0;
    while(i<3){     
      incomingByte = Serial.read();
      if (incomingByte == ','){
        i++;
        continue;
      }
      if (incomingByte == -1 ) break;
      a[i]=incomingByte;
      Serial.println(a[i]);
    }  
  }
}
