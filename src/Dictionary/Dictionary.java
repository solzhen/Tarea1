package Dictionary;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Geometry.Rectangle;
import Serializables.*;

public class Dictionary {
  
  static int nor = 0;
  static int non = 0;
  
	public static int getIndex(SerializableNode n) {
	  if (n.index != 0) {
	    return n.index;
	  }
	  non++;
	  n.index = non;
      writeNode(non, n);
      return non;
	}
	
	public static int getIndex(Rectangle n) {
	  if (n.index != 0) {
        return n.index;
      }
	  nor++;
	  n.index = nor;
	  writeRect(nor, n);
	  return nor;
	}

	public static SerializableNode getNode(int i) {
	  if (i == 0) return null;
	  SerializableNode e;
      try {
        FileInputStream fileIn = new FileInputStream("./Node/" + i + ".txt");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        e = (SerializableNode) in.readObject();
        in.close();
        fileIn.close();
     }catch(IOException j) {
        j.printStackTrace();
        return null;
     }catch(ClassNotFoundException c) {
        System.out.println("SerializableNode class not found");
        c.printStackTrace();
        return null;
     }
      return e;
	}
	
	public static Rectangle getRect(int i) {
	  if (i == 0) return null;
	  Rectangle e;
	    try {
	      FileInputStream fileIn = new FileInputStream("./Rect/" + i + ".txt");
	      ObjectInputStream in = new ObjectInputStream(fileIn);
	      e = (Rectangle) in.readObject();
	      in.close();
	      fileIn.close();
	   }catch(IOException j) {
	      j.printStackTrace();
	      return null;
	   }catch(ClassNotFoundException c) {
	      System.out.println("Rectangle class not found");
	      c.printStackTrace();
	      return null;
	   }
		return e;
	}
	
	public static void writeNode(int i, SerializableNode n) {
	  try {
        FileOutputStream fileOut =
        new FileOutputStream("./Node/" + i + ".txt");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(n);
        out.close();
        fileOut.close();
        //System.out.println("Serialized data is saved in e.txt");
     }catch(IOException e) {
        e.printStackTrace();
     }
	}
	
	
	public static void writeRect(int i, Rectangle n) {
	  try {
	      FileOutputStream fileOut =
	      new FileOutputStream("./Rect/" + i + ".txt");
	      ObjectOutputStream out = new ObjectOutputStream(fileOut);
	      out.writeObject(n);
	      out.close();
	      fileOut.close();
	      //System.out.println("Serialized data is saved in e.txt");
	   }catch(IOException e) {
	      e.printStackTrace();
	   }
	  
	}

  
}
