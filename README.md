# SmartPlug-ESP8266

Created by [Chien Kuan-Chen](goodnana1224@gmail.com), [Lai Po-Hsuan](s1101b046@gmail.com), [Lin Chia-Jen](jared1997519@gmail.com), [Chang Chun-Hsuan](s1310634027@gms.nutc.edu.tw), [Suag Cheng-Han](richardsung97@yahoo.com.tw)

The SmartPlug that we developed is based on the **ESP8266 NodeMCU chip**, which will allow users to regulate and mitigate the power consumption from wall sockets. The entire project consists of a single ESP8266 chip, an **ACS712 5A electric current sensor**, and a **DHT11 temperature & moisture sensor**. The ESP8266 will retrieve data from the ACS712 and DHT11 sensors, then upload these data via the **MQTT protocol** to **ThingSpeak**, an online platform for processing, analyzing and storing IoT data. 

Users can monitor the **temperature, humidity, real time power consumption** of a specific wall socket and remotely power on or off the connected appliance through a custom written application using any android device.

![](https://i.imgur.com/eeIJwb0.png)

## Platform
[ThingSpeak](https://thingspeak.com/)
1. ThingSpeak Private Channel
2. ThingSpeak Talkback

## Material
1. ESP8266 NodeMCU Wifi Development Board
2. ACS712 Current Sensor
3. DHT11 Humidity and Temperature Sensor
4. 5V Relay Module
5. Socket box and Socket

## Protocol
1. Mqtt
2. Http

---
# Demo
[Demo](https://photos.google.com/share/AF1QipMq7t7CK7KSJ9EuPBp0EXyoE-DWmmEJPY7nUskG1NK1BvOwuA_IJRvWfY5mZ5fK-g?key=MExTNmpzSTJHOG5Ea1FrWGVZVFU2dVBtd1JWeUdR)
