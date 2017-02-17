import de.bezier.data.*;

XlsReader reader;
int docInd = 0, docNo = 0;
int numTopics = 5;
int count = 0, docCount = 0;
float graphPos = 0, zoom = 1.0, xo, yo;
int g = 10, rectH = 120, rectW = 180, fixeda = 30, ypos = 0, a = 0;
float probData;
boolean notDone = true;

void setup ()
{
  size(1080,620);
  background(240);
  reader = new XlsReader( this, "/home/sumanta/Documents/eric_codes/dtmat.xls" );
  docNo = reader.getLastRowNum()+1;
  println("No of docs :"+docNo);
}
void draw()
{
  translate(xo,yo);
  scale(zoom);
  while(notDone){
    for(int docInd=0; docInd<docNo; docInd++){
      reader.openSheet(0);
      for(int i=0; i<numTopics; i++){
        probData = reader.getFloat(docInd, i+1);
        if(probData != 0.0){
          drawRect(count, docInd, i, probData);
          count++;
        }
      }
      count = 0;
      ypos = 0;
    }
    notDone = false;
  }
}
void drawRect(int n, int ndoc, int topicI, float prob){
  ypos = height - (n + 1) * (g + rectH);
  fill(80,235,235,(int)Math.round(prob * 100));
  a = fixeda + ndoc * (fixeda + rectW);
  rect(a, ypos, rectW, rectH);
  writeText(a, ypos, topicI, prob);
  if(n==0){
    text(reader.getString(ndoc,0), a, ypos+rectH+15);
  }
}
void writeText(int a, int ypos, int topicI, float prob){
  int topicSize = 28, wordSize = 20, ta = 10, gt = 5, gb = 15, wh = 10;
  int wordNum = 5;
  String[] words = new String[5];
  reader.openSheet(1);
  for(int j=0; j<wordNum; j++)
    words[j] = reader.getString(topicI, j);
  reader.openSheet(0);
  textSize(topicSize);
  fill(0, 102, 153);
  text("Topic "+topicI+" : "+(int)Math.round(prob * 100)+" %", a + ta, ypos + gt);
  textSize(wordSize);
  fill(100, 50, 0, 75);
  text(words[0], a + ta, ypos + gt + wh + gb);
  for(int j=1; j<wordNum; j++)
    text(words[j], a + ta, ypos + gt + wh + gb + j * (wh + gt));
  noFill();
}  
void mouseWheel(MouseEvent event){
  notDone = true;
  background(240);
  float e = event.getAmount();
  zoom = zoom - 0.1*e;
}
void mouseDragged(){
  notDone = true;
  background(240);
  xo= xo + (mouseX - pmouseX);
  yo = yo + (mouseY - pmouseY);
}
