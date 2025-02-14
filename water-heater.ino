#include <DHT.h>
#include <Firebase.h>
#include <FirebaseESP32.h>
#include "WiFi.h"
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <string.h>

//set wifi
const char* ssid = "VIETTEL-NOIGIUA";//name wifi connect
const char* pass = "0975777530";//password
int relay = 25;
float value_tem=23;
float value_hum=68;
const int DHTPIN = 27;       //Đọc dữ liệu từ DHT11 ở chân 2 trên mạch Arduino
const int DHTTYPE = DHT11;  //Khai báo loại cảm biến, có 2 loại là DHT11 và DHT22 
DHT dht(27, DHT11);
String get_time;
String get_date;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");
FirebaseData firebaseData;
void setup() {
  Serial.begin(9600);
  pinMode(relay, OUTPUT);
//ket noi voi wifi  
  WiFi.begin(ssid,pass);
  while(WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
    }
Serial.println("");
Serial.println("WiFi connected");

timeClient.begin();
//ket noi firebase
Firebase.begin("https://iot-20211-default-rtdb.firebaseio.com/","puLIxVQFlByNuFEgLdqz0PdZ3g0DzYEgs8wEwRD6");
Firebase.reconnectWiFi("true");
dht.begin();
}

void loop() {
  // put your main code here, to run repeatedly:
  get_time=gettime();
  get_date=getdate();
  dht1();
  delay(500);
}
//kiểm tra chuỗi có bắt đầu bằng một chuỗi khác
boolean startWith(String a,String b){
  for(int i=0;i<b.length();i++){
    if(!(a[i]==b[i])){return false;}
    }
    return true;
  }
//lấy ngày theo thời gian thực  GTM+7
String getdate(){
  timeClient.update();
  timeClient.setTimeOffset(25200);
  unsigned long epochTime = timeClient.getEpochTime();
  struct tm *ptm = gmtime ((time_t *)&epochTime);
  int monthDay = ptm->tm_mday;
  int currentMonth = ptm->tm_mon+1;
  int currentYear = ptm->tm_year+1900;
  String currentDate = String(String(currentYear)+"-0"+String(+currentMonth)+"-"+String(monthDay));
  return currentDate;
}
//lấy giờ theo thời gian thực  GTM+7
String gettime(){
  timeClient.update();
  timeClient.setTimeOffset(25200);
 return timeClient.getFormattedTime();
 }
//cam bien nhiet do do am
void dht1(){
  String date = String(get_date);
  // lấy giá trị nhiệt độ, độ ẩm và đưa lên firebase
  int h = dht.readHumidity();    
  int t = dht.readTemperature(); 
   if (isnan(h) || isnan(t)) {

    Serial.println("error!");

    return;
  }
  String path_temp = "/sang230799@gmailcom/"+date+"/0temperature";
  String path_hum = "/sang230799@gmailcom/"+date+"/0humidity";
  Firebase.pushString(firebaseData,path_temp,String(t));
  Firebase.pushString(firebaseData,path_hum,String(h));
  Serial.print("Nhiet do: ");
  Serial.println(t);               
  Serial.print("Do am: ");
  Serial.println(h);
//Lấy giá trị nhiệt độ auto
 String path_auto="/sang230799@gmailcom/"+date+"/tem_auto";
 int temp_auto;
 Firebase.getInt(firebaseData,path_auto,&temp_auto);
 Serial.print("temp auto: ");
 Serial.println(temp_auto);
 // lấy trạng thái của bình( tắt/ bật)
 String path = "/sang230799@gmailcom/"+date+"/status";
 Firebase.getString(firebaseData,path);
 String stt = firebaseData.stringData();
 Serial.print("status: ");
 Serial.println(stt);
 // lấy trạng thái auto/ bật bằng tay
 String path_mode="/sang230799@gmailcom/"+date+"/auto";
 bool mod;
 Firebase.getBool(firebaseData,path_mode,&mod);
 Serial.print("mode: ");
 Serial.println(mod);
 String time_on ="/sang230799@gmailcom/"+date+"/auto/timer/on";
 Firebase.getString(firebaseData,time_on);
 String t_on = firebaseData.stringData();
 String time_off ="/sang230799@gmailcom/"+date+"/auto/timer/off";
 Firebase.getString(firebaseData,time_off);
 String t_off = firebaseData.stringData();
 
 if( mod == false){
  Serial.println("không auto");
    if(startWith(stt , "\"1\"")){
      digitalWrite(relay, HIGH);
      Serial.println("Đang bật");
   }else{
      digitalWrite(relay, LOW);
      Serial.println("Đang tắt");
 }
 }else if(mod == true){
  Serial.println("auto");
  Serial.println(t_on);
  Serial.println(get_time);
 if(startWith(t_on,"null")){
    if(t <= temp_auto){
      digitalWrite(relay, HIGH);
      Serial.println("auto-Đang bật");
    }else {
      digitalWrite(relay, LOW);
      Serial.println("auto-Đang tắt");
    }
  }
  else{
    if( ( (get_time[0]-48)*10*3600+(get_time[1]-48)*3600+(get_time[3]-48)*600+(get_time[4]-48)*60)>=
        ((t_off[1]-48)*10*3600+(t_off[2]-48)*3600+(t_off[4]-48)*600+(t_off[5]-48)*60)){
          digitalWrite(relay, LOW);
          Serial.println("time-Đang tắt");
          }
    else if(((get_time[0]-48)*10*3600+(get_time[1]-48)*3600+(get_time[3]-48)*600+(get_time[4]-48)*60)>=
        ((t_on[1]-48)*10*3600+(t_on[2]-48)*3600+(t_on[4]-48)*600+(t_on[5]-48)*60)){
          digitalWrite(relay, HIGH);
          Serial.println("time-Đang bật");
          }
      }
 }
 
 
 delay(1000);
 Serial.println(); 
}
//test
