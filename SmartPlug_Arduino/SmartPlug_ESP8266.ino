// Import Libraries
#include <dht.h>
#include <Arduino.h>
#include <PubSubClient.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266HTTPClient.h>

#define USE_SERIAL Serial     // Set Macro
ESP8266WiFiMulti WiFiMulti;   // Create Object of ESP8266WiFiMulti
WiFiClient espClient;
PubSubClient pubclient(espClient);

const char* mqttServer = "mqtt.thingspeak.com";  // MQTT伺服器位址
const char* mqttUserName = "Thingspeak username";  // 使用者名稱，隨意設定。
const char* mqttPwd = "Thingspeak password";  // MQTT密碼
const char* clientID = "cliend ID";      // 用戶端ID，隨意設定。
const char* topic = "channels/634553/publish/6B29OOAWVGNOG6OF";
unsigned long prevMillis = 0;  // 暫存經過時間（毫秒）
const long interval = 5 * 1000;  // 上傳資料的間隔時間，20秒。
String msgStr = "";      // 暫存MQTT訊息字串
int temp;  // 暫存溫度
int hum;   // 暫存濕度


/*
Measuring AC Current Using ACS712
*/
const int sensorIn = A0;
int RELAYpin = 16;
int mVperAmp = 185; // use 100 for 20A Module and 66 for 30A Module
double Voltage = 0;
double VRMS = 0;
double AmpsRMS = 0;

dht DHT;
#define DHT11_PIN 5

void setup() {
  // put your setup code here, to run once:
    USE_SERIAL.begin(115200);       // NodeMCU to PC communication Baud Rate=115200 
    pinMode(RELAYpin, OUTPUT); 
    digitalWrite(RELAYpin, LOW);
    
    USE_SERIAL.println();
    USE_SERIAL.println();
    USE_SERIAL.println();

    for(uint8_t t = 4; t > 0; t--) {                  
        USE_SERIAL.printf("[SETUP] WAIT %d...\n", t); // display msg on PC
        USE_SERIAL.flush();  // wait for a serial string to be finished sending
        delay(1000);         // wait for 1 sec
    }

    WiFiMulti.addAP("Robin", "0928424677"); // connect to WiFi
    pubclient.setServer(mqttServer, 1883);
}

void loop() {
  // wait for WiFi connection
    if((WiFiMulti.run() == WL_CONNECTED)) {     // If WiFi is connected
          if (!pubclient.connected()) {
              reconnect();
          }
          pubclient.loop();
          if (millis() - prevMillis > interval) {
              prevMillis = millis();
              sendmqtt();
              talkback();
          }
    }
}

void reconnect() {
  while (!pubclient.connected()) {
    if (pubclient.connect(clientID, mqttUserName, mqttPwd)) {
      Serial.println("MQTT connected");
    } else {
      Serial.print("failed, rc=");
      Serial.print(pubclient.state());
      Serial.println(" try again in 5 seconds");
      delay(1000);  // 等5秒之後再重試
    }
  }
}

void sendmqtt(){
    int chk = DHT.read11(DHT11_PIN);
        switch (chk)
        {
          case DHTLIB_OK:  
         Serial.print("OK,\t"); 
          break;
          case DHTLIB_ERROR_CHECKSUM: 
          Serial.print(""); 
          break;
          case DHTLIB_ERROR_TIMEOUT: 
          Serial.print("Time out error,\t"); 
          break;
          case DHTLIB_ERROR_CONNECT:
              Serial.print("Connect error,\t");
              break;
          case DHTLIB_ERROR_ACK_L:
              Serial.print("Ack Low error,\t");
              break;
          case DHTLIB_ERROR_ACK_H:
              Serial.print("Ack High error,\t");
              break;
          default: 
          Serial.print("Unknown error,\t"); 
          break;
        }
        
         Voltage = getVPP();
         VRMS = (Voltage/2.0) *0.707; 
         //1MA
         AmpsRMS = ((VRMS * 1000)/mVperAmp)*1000;
        
        // DISPLAY DATA
        temp = DHT.temperature;
        hum = DHT.humidity;
        msgStr=msgStr+"field1="+temp+"&field2="+hum+"&field3="+AmpsRMS;

        // 宣告字元陣列
        byte arrSize = msgStr.length() + 1;
        char msg[arrSize];
     
        Serial.print("Publish message: ");
        Serial.println(msgStr);
        msgStr.toCharArray(msg, arrSize); // 把String字串轉換成字元陣列格式
        pubclient.publish(topic, msg);       // 發布MQTT主題與訊息
        msgStr = "";
}

void talkback(){
        HTTPClient http;                        // Create Object of HTTPClient
        USE_SERIAL.print("[HTTP] begin...\n");  // Display msg on PC

        // Replace <TalkBack_ID>, <Command_ID> and <API_Key> by actual values here
        http.begin("http://api.thingspeak.com/talkbacks/30134/commands/last?api_key=VCBK2KBMJLQUJCEM"); //HTTP //LED_ON Get a TalkBack Command
        USE_SERIAL.print("[HTTP] GET...\n");    // Display msg on PC
        
        int httpCode = http.GET();              // start connection and send HTTP header
        // httpCode will be negative on error

        if(httpCode > 0) {
            // HTTP header has been send and Server response header has been handled
            USE_SERIAL.printf("[HTTP] GET... code: %d\n", httpCode);
            // file found at server
            if(httpCode == HTTP_CODE_OK){          // check in ESP8266HTTPClient.h . //HTTP_CODE_OK=200
                String payload = http.getString(); // payload = Response from server
                USE_SERIAL.println(payload);       // Display payload on PC 
                if(payload=="OFF"){             // If Responcse from server = "LED_OFF"
                  USE_SERIAL.println("OFF");
                  digitalWrite(RELAYpin, LOW);
                }
                if(payload=="ON"){             // If Responcse from server = "LED_ON"
                  USE_SERIAL.println("ON");
                  digitalWrite(RELAYpin, HIGH);
                }
            }
        }
        else{
            USE_SERIAL.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());  // Display Error msg to PC
        } 
        delay(1000);     // Wait for 10 Sec
        http.end();   // Close Connection
}

float getVPP()
{
  float result;
  
  int readValue;             //value read from the sensor
  int maxValue = 0;          // store max value here
  int minValue = 1024;          // store min value here
  
   uint32_t start_time = millis();
   while((millis()-start_time) < 1000) //sample for 1 Sec
   {
       readValue = analogRead(sensorIn);
       // see if you have a new maxValue
       if (readValue > maxValue) 
       {
           /*record the maximum sensor value*/
           maxValue = readValue;
       }
       if (readValue < minValue) 
       {
           /*record the maximum sensor value*/
           minValue = readValue;
       }
   }
   
   // Subtract min from max
   result = ((maxValue - minValue) * 5.0)/1024.0;
      
   return result;
 }
