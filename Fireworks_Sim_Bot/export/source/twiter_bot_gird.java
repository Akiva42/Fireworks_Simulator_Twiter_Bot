import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import gifAnimation.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class twiter_bot_gird extends PApplet {


GifMaker gifExport;
//----------------------
int gridDim = 10;
String[][] cells = new String[gridDim][gridDim];
String[][] lastCells = new String[gridDim][gridDim];
PImage rock;
PImage firework;
PImage firework_active;
PImage match_lite;
PImage match;
PImage block;
PImage nothing;
PImage img;
//------------------
public void setup() {
  rock = loadImage("rock.png");
  firework = loadImage("firework.png");
  firework_active = loadImage("firework_active.png");
  match_lite = loadImage("match_lite.png");
  match = loadImage("match.png");
  block = loadImage("block.png");
  nothing = loadImage("nothing.png");
  img = loadImage("nothing.png");

  size(500, 500);
  background(255);
  //frameRate(2);
  gifExport = new GifMaker(this, "export.gif");
  gifExport.setRepeat(0);             // make it an "endless" animation
  gifExport.setTransparent(255, 255, 255);    // black is transparent
  //--------------------
  gen();
}
//------------------
public void draw() {
  background(255);
  //-------------------------
  int cellSize = width/gridDim;
  strokeWeight(cellSize);
  translate(cellSize/2, cellSize/2);
  for (int x = 0; x<gridDim; x++) {
    for (int y = 0; y<gridDim; y++) {
      if (cells[x][y] == "air") {
        img = nothing;
      }
      if (cells[x][y] == "rock") {
        img = rock;
      }
      if (cells[x][y] == "block") {
        img = block;
      }
      if (cells[x][y] == "match") {
        img = match;
      }
      if (cells[x][y] == "fire") {
        img = match_lite;
      }
      if (cells[x][y] == "fireworks") {
        img = firework;
      }
      if (cells[x][y] == "fireworks_active") {
        img = firework_active;
      }
      int pointX = x*cellSize - cellSize/2;
      int pointY = y*cellSize - cellSize/2;
      image(img, pointX, pointY, cellSize, cellSize);
    }
  }
  gifExport.setDelay(1);
  gifExport.addFrame();
  delay(1000/10);
  step();
}
//------------------
public void saveGif() {
  gifExport.finish();                 // write file
}
//------------------
public void keyPressed() {
  if (key=='a') {
    step();
  }
  if (key == 'r') {
    gen();
  }
}
//------------------
public void step() {
  for (int x = 0; x<gridDim; x++) {
    for (int y = 0; y<gridDim; y++) {
      update(x, y);
    }
  }
  reset();
}
//------------------
public void update(int x, int y) {
  if (cells[x][y] == "rock") {
    if (y<gridDim-1) {
      if (cells[x][y+1]=="air") {
        fall(x, y);
      }
    }
  }

  if (cells[x][y] == "fireworks") {
    if (x>0) {
      if (cells[x-1][y] == "fire") {
        cells[x][y] = "fireworks_active";
      }
    }
    if (x<gridDim-1) {
      if (cells[x+1][y] == "fire") {
        cells[x][y] = "fireworks_active";
      }
    }
    if (y>0) {
      if (cells[x][y-1] == "fire") {
        cells[x][y] = "fireworks_active";
      }
    }
    if (y<gridDim-1) {
      if (cells[x][y+1] == "fire") {
        cells[x][y] = "fireworks_active";
      }
    }
  }

  if (cells[x][y] == "fireworks_active") {
    fly(x, y);
  }
}
//------------------
public void gen() {
  for (int x = 0; x<gridDim; x++) {
    for (int y= 0; y<gridDim; y++) {
      float rand = random(1);
      if (rand>0.8f && rand<0.94f) {
        cells[x][y] = "rock";
      } else if (rand>0.72f && rand <0.8f) {
        cells[x][y] = "block";
      } else if (rand>0.71f && rand<0.72f) {
        cells[x][y] = "match";
      } else if (rand>0.7f && rand<0.71f) {
        if (y>gridDim/2) {
          cells[x][y] = "fireworks";
          for (int i = y-1; i>=0; i--) {
            cells[x][i] = "air";
          }
        }
      } else {
        cells[x][y] = "air";
      }
    }
  }
  boolean haveAMatch = false;
  boolean haveAFireworks = false;
  boolean haveARock = false;
  for (int x = 0; x<gridDim; x++) {
    for (int y = 0; y<gridDim; y++) {
      if (cells[x][y] == "fireworks") {
        haveAFireworks = true;
      }
      if (cells[x][y] == "match") {
        haveAMatch = true;
      }
      if (cells[x][y] == "rock") {
        haveARock = true;
      }
    }
  }
  if (haveAFireworks==false || haveARock==false || haveAMatch==false) {
    gen();
  }
}
//------------------
public void reset() {
  for (int x = 0; x<gridDim; x++) {
    for (int y = 0; y<gridDim; y++) {
      if (cells[x][y]=="rock_") {
        cells[x][y]="rock";
      }
    }
  }
  boolean same = true;
  for (int x=0; x<gridDim; x++) {
    for (int y=0; y<gridDim; y++) {
      if (lastCells[x][y]!=cells[x][y]) {
        same = false;
      }
    }
  }
  if (same==true) {
    simDone();
  }
  for (int x=0; x<gridDim; x++) {
    for (int y=0; y<gridDim; y++) {
      lastCells[x][y]=cells[x][y];
    }
  }
}
//------------------
public void fall(int x, int y) {
  cells[x][y]="air";
  cells[x][y+1]="rock_";
  fireStart(x, y);
  if (y<gridDim) {
    fireStart(x, y+1);
  }
  if (y>0) {
    update(x, y-1);
  }
}
//-------------------
public void fly(int x, int y) {
  if (y>0) {
    if (cells[x][y-1]=="air") {
      cells[x][y]="air";
      cells[x][y-1]="fireworks_active";
    }
  }
  if (y>1) {
    update(x, y-2);
  } else {
    cells[x][y]="air";
  }
}
//-------------------
public void fireStart(int x, int y) {
  if (x<gridDim-1) {
    if (cells[x+1][y] == "match") {
      cells[x+1][y] = "fire";
    }
  }
  if (x>0) {
    if (cells[x-1][y] == "match") {
      cells[x-1][y] = "fire";
    }
  }
}
//-------------------
public void simDone() {
  saveGif();
  println("saved");
  exit();
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "twiter_bot_gird" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
