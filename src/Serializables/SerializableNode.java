package Serializables;

import java.io.Serializable;
import java.util.ArrayList;

import Geometry.Rectangle;
import RTree.RTree;

public class SerializableNode implements Serializable{
  //Serializable index
  public int index = 0;
	//Serializable pointer indexex
	public int parent;
	public ArrayList<Integer> sons;
	//Atributos
	public Rectangle mbr; 
	public int M;
	//Metodos and shit
	 public ArrayList<Rectangle> buscar(Rectangle rect) {
		    return null;
		  }
		  public void insertar(Rectangle rect, RTree rTree) {
		  }
		  public void fix() {
		  }
		  public String display(int i) {
		    return null;
		  }
		  public int getDepth() {return 0;}
		  public int countSelf() { return 1;}
		  public int countLeaf() { return 1;}
	
}
