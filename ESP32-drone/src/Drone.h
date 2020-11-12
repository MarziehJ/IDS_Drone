#ifndef Drone_h
#define Drone_h

#include "Arduino.h"
#include "Joystick.h"
#include "Button.h"
#include "Lcd.h"
#include "led.h"
#include "AsyncUDP.h"

class Drone
{
public:
  Drone(int xJoyPin, int yJoyPin, int buttonPin, int ledPin, uint8_t lcd_addr)
  {

    this->joystick = new Joystick(xJoyPin, yJoyPin);
    this->button = new Button(buttonPin);
    this->lcd = new Lcd(lcd_addr);
    this->led = new Led(ledPin);
  }
  bool droneIsOn() { return this->led->state; }

  void sendCommand(String msg)
  {
    ////PixelEmulator
    udp.writeTo((const uint8_t *)msg.c_str(),
                msg.length(),
                IPAddress(192, 168, 1, 2),
                8000);

    //Real Drone
    // udp.writeTo((const uint8_t *)msg.c_str(),
    //             msg.length(),
    //             IPAddress(192, 168, 10, 1),
    //             7000);

    this->lcd->WriteMessage(msg);
    Serial.println(msg);
  }

  void ChangeStatus()
  {
    if (droneIsOn())
    {
      StopDrone();
    }
    else
    {
      InitDrone();
    }
  }

  void Move(std::pair<int, int> xy)
  {
    bool isMoving = this->joystick->isInDeadzone() == false;
    if (isMoving)
    {
      Serial.println("Joystick Moving");
      MoveDrone(xy);
    }
  }

  Joystick *joystick;
  Button *button;

private:
  Lcd *lcd;
  Led *led;
  AsyncUDP udp;

  void StopDrone()
  {
    //PixelEmulator
    sendCommand("stop");

    //Real Drone
    //sendCommand("land");
    this->led->off();
    Serial.println(led->status());
  }

  void InitDrone()
  {
    //PixelEmulator
    sendCommand("init");
    sendCommand("takeoff");

    //Real Drone
    //sendCommand("command");
    //sendCommand("takeoff");

    this->led->on();
    Serial.println(led->status());
  }
  void MoveDrone(std::pair<int, int> xy)
  {
    //PixelEmulator

    Serial.print("X: ");
    Serial.println(xy.first);
    Serial.print("Y: ");
    Serial.println(xy.second);
    if (xy.first == 0)
      sendCommand("down");
    else if  (xy.second == 0)
      sendCommand("left");
    else if (xy.first  <= 2000)
       sendCommand("right");
    else sendCommand("top");
   
      //PixelEmulator

      //Real Drone
      //sendCommand("right 1");
  }
};

#endif