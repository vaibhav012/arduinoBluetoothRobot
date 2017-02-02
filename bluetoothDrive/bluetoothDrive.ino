#define LeftMotor0 5
#define LeftMotorV 6
#define RightMotor0 10
#define RightMotorV 11
int carSpeed = 205;
char state;

void setup() {
  Serial.begin(9600);
  pinMode(LeftMotor0, OUTPUT);
  pinMode(LeftMotorV, OUTPUT);
  pinMode(RightMotor0, OUTPUT);
  pinMode(RightMotorV, OUTPUT);
  stopCar();
}

void loop() {
  
  if(Serial.available()>0)
  {
    state=Serial.read();
    Serial.print(state);
    Serial.print("\n");
  }
  else
  {
    //some code if required
  }
  
  if(state == 'H')
  {
    carSpeed = 255;
  }

  if(state == 'I')
  {
    if(carSpeed <= 205)
    {
      carSpeed = carSpeed + 50;
      state = 'S';
      Serial.print(carSpeed);
    }
    else
      carSpeed = 255;
  }

  if(state == 'D')
  {
    if(carSpeed >= 50)
    {
      carSpeed = carSpeed - 50;
      state = 'S';
      Serial.print(carSpeed);
    }
    else
      carSpeed = 0;
  }

  if(state=='F')
  {
    forward();
  }

  if(state=='S')
  {
    stopCar();
  }

  if(state=='B')
  {
    reverse();
  }

  if(state=='R')
  {
    right();
  }

  if(state=='L')
  {
    left();
  }
}

void forward(){
  analogWrite(LeftMotor0,0);
  analogWrite(LeftMotorV,carSpeed);
  analogWrite(RightMotor0,0);
  analogWrite(RightMotorV,carSpeed);
}

void left(){
  analogWrite(LeftMotor0,carSpeed);
  analogWrite(LeftMotorV,0);
  analogWrite(RightMotor0,0);
  analogWrite(RightMotorV,carSpeed);
}

void right(){
  analogWrite(LeftMotor0,0);
  analogWrite(LeftMotorV,carSpeed);
  analogWrite(RightMotor0,carSpeed);
  analogWrite(RightMotorV,0);
}

void stopCar(){
  analogWrite(LeftMotor0,0);
  analogWrite(LeftMotorV,0);
  analogWrite(RightMotor0,0);
  analogWrite(RightMotorV,0);
}

void reverse(){
  analogWrite(LeftMotor0,carSpeed);
  analogWrite(LeftMotorV,0);
  analogWrite(RightMotor0,carSpeed);
  analogWrite(RightMotorV,0);
}
