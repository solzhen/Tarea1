package RTree;

import java.util.ArrayList;

import Geometry.Rectangle;
import Node.*;

public class RTree {
  
  private final static int DEFAULT = 5;
  public static enum Heur {
    QUADRATIC, LINEAR
  };
  
  Node root;
  public int M;
  public Heur heur;
  
  public RTree(int M, Heur heur) {
    root = new LeafNode();
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
    LeafNode n = root.insertar(rect);
    if (n.sons.size() > M) {
      overflowHandler(n);
    }
  }
  private void overflowHandler(LeafNode n) {
    if (heur == Heur.QUADRATIC)
        n.quadraticSplit(M);
    else
        n.linearSplit(M);
    updateRoot();
  }
  private void updateRoot() {
    while (root.parent != null) {
      root = root.parent;
    }
  }
  public String display() {
    return root.display(0);
  }
  public int getDepth() {
	  return root.getDepth();
  }
}
