package RTree;

import java.util.ArrayList;

import Dictionary.Dictionary;
import Geometry.Rectangle;
import Serializables.*;

public class RTree {
  
  private final static int DEFAULT = 5;
  public static enum Heur {
    QUADRATIC, LINEAR
  };
  
  SerializableNode root;
  public int M;
  public Heur heur;
  
  public RTree(int M, Heur heur) {
    root = new LeafNode(M);
    this.M = M;
    this.heur = heur;
  }
  public RTree(int M) {
    this(M, Heur.QUADRATIC);
  }  
  public RTree(Heur heur) {
    this(DEFAULT, heur);
  }
  public RTree() {
    this(DEFAULT, Heur.QUADRATIC);
  }
  public ArrayList<Rectangle> buscar(Rectangle rect) {
    return root.buscar(rect);
  }
  public void insertar(Rectangle rect) {
    root.insertar(rect, this);
    updateRoot();
  }
  private void updateRoot() {
    root = Dictionary.getNode(root.index);
    int rootin;
    while (root.parent != 0) {
      rootin = root.parent;
      root = Dictionary.getNode(rootin);
    }
  }
  public String display() {
    return root.display(0);
  }
  public int countNodos() {
	return root.countSelf();
  }
  public int countLeaf() {
	return root.countLeaf();
  }
  public int getDepth() {
	return root.getDepth();
  }
}
