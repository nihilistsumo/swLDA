import de.bezier.data.*;

XlsReader reader;
int docInd = 0, docNo = 0, numTopics = 5, hueSlot = abs(360/numTopics);
int count = 0, docCount = 0, clr, slot = abs(360/numTopics);
int[] topics;
float[] mydata;
float graphPos = 0, zoom = 1.0, xo = 0, yo = 0, radius = 30;
int rectH = 0, a = 0, WIDTH = 720, HEIGHT = 640, OFFSET = 20;
float probData, start, stop, hue;
boolean notDone = true;

void setup ()
{
  size(WIDTH,HEIGHT);
  background(0xffffff);
  topics = new int[numTopics];
  mydata = new float[numTopics];
  for(int i=0; i<numTopics; i++){
    mydata[i]=0.0;
  }
  //yo=2*OFFSET;
  yo=250;
  //noStroke();
  reader = new XlsReader( this, "/home/sumanta/Documents/eric_codes/dtmat.xls" );
  docNo = reader.getLastRowNum()+1;
  println("No of docs :"+docNo);
  drawTopics();
  for(int i=0; i<docNo; i++){
    for(int j=1; j<=numTopics; j++){
      reader.openSheet(0);
      probData = reader.getFloat(i, j);
      if(probData>0.0001){
        mydata[j-1]=probData;
      }
    }
    count++;
    if((xo+radius+OFFSET)>WIDTH){
      xo=radius+OFFSET;
      yo=yo+2*OFFSET;
    }
    else{
      xo=xo+radius+OFFSET;
    }
    drawPie(radius, xo, yo, mydata);
    textSize(10);
    text(reader.getString(i,0), xo-radius, yo+radius-7);
    for(int ij=0; ij<numTopics; ij++){
      mydata[ij]=0.0;
    }
  }
}
void draw()
{
  
}
void drawPie(float radius, float xpos, float ypos, float[] data){
//data in the form: [angle1, angle2,...., anglen] where
//n is the total number of sectors in the pie
  start = 0;
  stop = 0;
  colorMode(HSB, 360, 100, 100);
  for(int i=0; i<data.length; i++){
    stop = start + data[i]*2*PI;
    hue = i*slot;
    fill(hue, 80, 80);
    arc(xpos, ypos, radius, radius, start, stop, PIE);
    start = stop;
  }
}
void drawTopics(){
  int wordNum = 10;
  float xpos=20, ypos=30, topicSlot=0, wxpos, wypos;
  for(int i=0; i<numTopics; i++){
    reader.openSheet(1);
    String[] words = new String[wordNum];
    for(int j=0; j<wordNum; j++)
      words[j] = reader.getString(i, j);
    reader.openSheet(0);
    topicSlot=(WIDTH-2*20)/numTopics;
    colorMode(HSB, 360, 100, 100);
    fill(i*slot, 80, 80);
    wxpos=xpos;
    wypos=ypos;
    rect(xpos, ypos, topicSlot-20, 150);
    xpos+=topicSlot;
    fill(0, 0, 0);
    for(int j=0; j<wordNum; j++)
      text(words[j], wxpos+10, wypos+=10);
  }
}
