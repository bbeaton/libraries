import processing.serial.*;
import mindset.*;

MindSet mindset;

void setup(){
  mindset = new MindSet(this);
  //Replace "/dev/cu.MindSet..." with whatever serial device you connect to your mindset through.  
  //On Mac/Linux it will be a /dev, and on windows, a COM#
  mindset.connect("/dev/cu.MindSet-DevB");  
}

void draw(){
}


void mindSetEvent(MindSet ms){
  println(ms.data.delta + " " + ms.data.theta + " " + ms.data.alpha1 + " " + ms.data.alpha2 + " " + ms.data.beta1 + " " + ms.data.beta2 + " " + ms.data.gamma1 + " " + ms.data.gamma2);
}

void mindSetRawEvent(MindSet ms){
  print(ms.getCurrentRawData());
}

void mindSetAttentionEvent(MindSet ms){
  println("Attention: " + ms.data.attention);
}
void mindSetMeditationEvent(MindSet ms){
  println("Meditation: " + ms.data.meditation);
}

