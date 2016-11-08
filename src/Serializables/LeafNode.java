package Serializables; //32 Bytes

import java.util.ArrayList;

import Dictionary.Dictionary;
import Geometry.Rectangle;
import RTree.RTree;
import RTree.RTree.Heur;

public class LeafNode extends SerializableNode {
  
  public LeafNode(Rectangle n, int parent, int M) {
	this.M = M;
	sons = new ArrayList<Integer>(M + 1);
    mbr = new Rectangle(n.x1,n.y1,n.x2,n.y2);
    this.parent = parent;
    sons.add(Dictionary.getIndex(n));
  }
  
  public LeafNode(int M) {
    sons = new ArrayList<Integer>(M + 1);
	this.M = M;
    parent = 0;
  }
  public ArrayList<Rectangle> buscar(Rectangle rect) {
    ArrayList<Rectangle> ret = new ArrayList<Rectangle>();
    for (int i : sons) {
      Rectangle n = Dictionary.getRect(i);
      if (n.intersects(rect)) ret.add(rect);
    }   
    return ret;
  }
  public void insertar(Rectangle rect, RTree tree) {
    int currentIndex = Dictionary.getIndex(this);
    sons.add(Dictionary.getIndex(rect));
    Rectangle n = Dictionary.getRect(sons.get(0));
    double p1 = n.x1;
    double q1 = n.y1;
    double p2 = n.x2;
    double q2 = n.y2;
    for (int i : sons) {
      n = Dictionary.getRect(i);
      p1 = Math.min(p1, n.x1);
      q1 = Math.min(q1, n.y1);
      p2 = Math.max(p2, n.x2);
      q2 = Math.max(q2, n.y2);
    }
    this.mbr = new Rectangle(p1, q1, p2, q2);
    Dictionary.writeNode(currentIndex, this);
    if (parent != 0)
      Dictionary.getNode(parent).fix();
    if (tree.heur == Heur.QUADRATIC)
      quadraticSplit();
     else
      linearSplit();
  }
  public int getDepth() {
	  return 1;
  }
  public void quadraticSplit() {
    if (sons.size() <= M) return;
    int mc = (int) (0.4 * M);
    double max = 0;
    LeafNode n1 = null, n2 = null;
    for (int i : sons) {
      Rectangle n = Dictionary.getRect(i);
      for (int j : sons) {
        Rectangle m = Dictionary.getRect(j);
        double area = (Math.max(n.x2, m.x2) - Math.min(n.x1, m.x1))
            * (Math.max(n.y2, m.y2) - Math.min(n.y1, m.y1));
        double delta = area - (n.area() + m.area());
        if (max < delta) {
          max = delta;
          n1 = new LeafNode(n, parent, M);
          n2 = new LeafNode(m, parent, M);
        }
      }
    }
    commonSplit(mc, n1, n2);
    InnerNode p = (InnerNode) Dictionary.getNode(parent);
    if (p != null) p.quadraticSplit();
    
  }

  
  public void linearSplit() {
    if (sons.size() <= M) return;
    int mc = (int) (0.4 * M);
    LeafNode n1 = null, n2 = null;
    double rangex = mbr.x2 - mbr.x1;
    double rangey = mbr.y2 - mbr.y1;
    double max = 0;
    for (int i : sons) {
      Rectangle n = Dictionary.getRect(i);
      for (int j : sons) {
        Rectangle m = Dictionary.getRect(j);
        double ndistx = (n.x2 - m.x1) / rangex;
        double ndisty = (n.y2 - m.y1) / rangey;
        if (max < (Math.max(ndistx, ndisty))) {
          max = Math.max(ndistx, ndisty);
          n1 = new LeafNode(n, parent, M);
          n2 = new LeafNode(m, parent, M);
        }
      }
    }
    commonSplit(mc, n1, n2);
    InnerNode p = (InnerNode) Dictionary.getNode(parent);
    if (p != null) p.linearSplit();
  }
  
  public String display(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {sb.append("  ");}
    sb.append("{MBR:(" + Double.toString(mbr.x1) + "," + Double.toString(mbr.y1) + "),("
        + Double.toString(mbr.x2) + "," + Double.toString(mbr.y2) + ")}\n");
    for (int j : sons) {
      Rectangle n = Dictionary.getRect(j);
      for (int i = 0; i <= depth; i++) {sb.append("  ");}
      sb.append(n.display());
    }
    return sb.toString();
  }
  
  protected void commonSplit(int mc, LeafNode n1, LeafNode n2) {
    int contadorm1 = 1;
    int contadorm2 = 1;
    
    for (int i : sons) {
      if (i == n1.sons.get(0) || i == n2.sons.get(0)) continue;
      Rectangle n = Dictionary.getRect(i);
      Rectangle mbrnm1 =
          new Rectangle(Math.min(n.x1, n1.mbr.x1), Math.min(n.y1, n1.mbr.y1),
              Math.max(n.x2, n1.mbr.x2), Math.max(n.y2, n1.mbr.y2));
      Rectangle mbrnm2 =
          new Rectangle(Math.min(n.x1, n2.mbr.x1), Math.min(n.y1, n2.mbr.y1),
              Math.max(n.x2, n2.mbr.x2), Math.max(n.y2, n2.mbr.y2));
      double delta1 = mbrnm1.area() - n1.mbr.area();
      double delta2 = mbrnm2.area() - n2.mbr.area();
      if (sons.size() - contadorm2 == mc) {
        n1.sons.add(i);
        n1.mbr = mbrnm1;
        contadorm1 += 1;
      } else if (sons.size() - contadorm1 == mc) {
        n2.sons.add(i);
        n2.mbr = mbrnm2;
        contadorm2 += 1;
      } else if (delta1 < delta2) {
        n1.sons.add(i);
        n1.mbr = mbrnm1;
        contadorm1 += 1;
      } else if (delta1 > delta2) {
        n2.sons.add(i);
        n2.mbr = mbrnm2;
        contadorm2 += 1;
      } else {
        if (mbrnm1.area() < mbrnm2.area()) {
          n1.sons.add(i);
          n1.mbr = mbrnm1;
          contadorm1 += 1;
        } else if (mbrnm1.area() > mbrnm2.area()) {
          n2.sons.add(i);
          n2.mbr = mbrnm2;
          contadorm2 += 1;
        } else {
          if (contadorm1 < contadorm2) {
            n1.sons.add(i);
            n1.mbr = mbrnm1;
            contadorm1 += 1;
          } else {
            n2.sons.add(i);
            n2.mbr = mbrnm2;
            contadorm2 += 1;
          }
        }
      }
    }
    SerializableNode p = Dictionary.getNode(parent); //parent node, CAN BE NULL
    sons = n1.sons; 
    mbr = n1.mbr; //see InnerNode.commonSplit()
    Dictionary.writeNode(index, this); //write n2 as n to disk
    
    if (p != null) {
      p.sons.add(Dictionary.getIndex(n2)); //write n2 into disk, etc, add index to parent node
      Dictionary.writeNode(parent, p); //update parent
    }
    else {
      SerializableNode np = new InnerNode(this, 0); //create new parent
      int n2index = Dictionary.getIndex(n2); //write n2 to disk
      np.sons.add(n2index); //add n2 index to parent
      parent = Dictionary.getIndex(np); //write parent to disk
      n2.parent = parent; //add parent index to n2
      Dictionary.writeNode(index, this); //update the sons of parent
      Dictionary.writeNode(n2index, n2);
    }
  }
}
