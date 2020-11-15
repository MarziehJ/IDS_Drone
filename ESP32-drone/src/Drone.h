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
  Drone(int xJoyPin, int yJoyPin, int buttonPin, int ledPin, uint8_t lcd_addr, bool isEmulator)
  {

    this->joystick = new Joystick(xJoyPin, yJoyPin);
    this->button = new Button(buttonPin);
    this->lcd = new Lcd(lcd_addr);
    this->led = new Led(ledPin);
    this->isEmulator = isEmulator;
  }

  bool droneIsOn()
  {
    return this->led->state;
  }

  void sendCommand(String msg)
  {
    udp.writeTo((const uint8_t *)msg.c_str(),
                  msg.length(),
                  getAddress(),
                  getPort());

  
    this->lcd->WriteSendCommand(msg, getAddress().toString());
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
  bool isEmulator;

  void StopDrone()
  {
    if (isEmulator)
      sendCommand("stop");
    else //Real Drone
      sendCommand("land");
    this->led->off();
    Serial.println(led->status());
  }

  void InitDrone()
  {
    if (isEmulator)
    {
      sendCommand("init");
      sendCommand("takeoff");
    }
    else
    {
      //Real Drone
      sendCommand("command");
      sendCommand("takeoff");
    }

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
    if (isEmulator)
    {
      if (xy.first == 0)
        sendCommand("down");
      else if (xy.second == 0)
        sendCommand("left");
      else if (xy.first <= 2000)
        sendCommand("right");
      else
        sendCommand("top");
    }
    else
    {
      if (xy.first == 0)
        sendCommand("down 10");
      else if (xy.second == 0)
        sendCommand("left 10");
      else if (xy.first <= 2000)
        sendCommand("right 10");
      else
        sendCommand("up 10");
    }
  }

  IPAddress getAddress()
  {
    if (isEmulator)
      return IPAddress(192, 168, 1, 39);
    else
      return IPAddress(192, 168, 10, 1);
  }

  int getPort()
  {
    if (isEmulator)
      return 8000;
    else
      return 8890;
  }

  
};

#endif