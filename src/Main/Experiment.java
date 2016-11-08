package Main;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import Dictionary.Dictionary;
import Geometry.Rectangle;
import Serializables.*;
import RTree.RTree;

public class Experiment {

  public static void main(String[] args) throws InterruptedException {
    Rectangle n = new Rectangle(0,0,1,1);
    
    int i = Dictionary.getIndex(n);
    
    Rectangle e = Dictionary.getRect(i);
    System.out.println(e.x1 + "," + e.y1 + ":" + e.x2 + "," + e.y2);
    
  }
}
