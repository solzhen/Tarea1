package Serializables;

import java.util.ArrayList;

import Geometry.Rectangle;
import RTree.RTree;
import RTree.RTree.Heur;
import Serializables.*;
import Dictionary.*;

public class InnerNode extends SerializableNode {

  public InnerNode(SerializableNode n, int parent) {
    M = n.M;
    sons = new ArrayList<Integer>(M + 1);
    mbr = new Rectangle(n.mbr);
    this.parent = parent;
    sons.add(Dictionary.getIndex(n));
  }

  public ArrayList<Rectangle> buscar(Rectangle rect) {
    ArrayList<Rectangle> ret = new ArrayList<Rectangle>();
    for (int i : sons) {
      if (Dictionary.getNode(i).mbr.intersects(rect))
        ret.addAll(Dictionary.getNode(i).buscar(rect));
    }
    return ret;
  } //

  public void insertar(Rectangle rect, RTree tree) {
    SerializableNode first = Dictionary.getNode(sons.get(0));

    double p1 = Math.min(first.mbr.x1, rect.x1);
    double q1 = Math.min(first.mbr.y1, rect.y1);
    double p2 = Math.max(first.mbr.x2, rect.x2);
    double q2 = Math.max(first.mbr.y2, rect.y2);
    double newArea = (p2 - p1) * (q2 - q1);
    double currentArea = first.mbr.area();
    double minDelta = Math.max(0, newArea - currentArea); // Cambio mas pequeno
    SerializableNode winner = first;
    double minArea = currentArea; // Area mas pequena entre los candidatos (para el desempate)

    for (int i : sons) {
      SerializableNode n = Dictionary.getNode(i);
      p1 = Math.min(n.mbr.x1, rect.x1);
      q1 = Math.min(n.mbr.y1, rect.y1);
      p2 = Math.max(n.mbr.x2, rect.x2);
      q2 = Math.max(n.mbr.y2, rect.y2);
      newArea = (p2 - p1) * (q2 - q1);
      currentArea = n.mbr.area();
      if (Math.max(0, newArea - currentArea) < minDelta) {
        minDelta = Math.max(0, newArea - currentArea);
        winner = n;
        minArea = Math.min(currentArea, minArea);
      }
      if (Math.max(0, newArea - currentArea) == minDelta) {
        if (currentArea < minArea) {
          minArea = currentArea;
          winner = n;
        }
      }
    }
    winner.insertar(rect, tree);
    if (tree.heur == Heur.QUADRATIC)
      quadraticSplit();
     else
      linearSplit();

  }

  public void fix() {
    int currentIndex = Dictionary.getIndex(this);
    double p1 = Dictionary.getNode(sons.get(0)).mbr.x1;
    double q1 = Dictionary.getNode(sons.get(0)).mbr.y1;
    double p2 = Dictionary.getNode(sons.get(0)).mbr.x2;
    double q2 = Dictionary.getNode(sons.get(0)).mbr.y2;
    for (int i : sons) {
      SerializableNode n = Dictionary.getNode(i);
      p1 = Math.min(p1, n.mbr.x1);
      q1 = Math.min(q1, n.mbr.y1);
      p2 = Math.max(p2, n.mbr.x2);
      q2 = Math.max(q2, n.mbr.y2);
    }
    this.mbr = new Rectangle(p1, q1, p2, q2);
    Dictionary.writeNode(currentIndex, this);
    if (Dictionary.getNode(parent) != null)
      Dictionary.getNode(parent).fix();
  }

  public void quadraticSplit() {
    if (sons.size() <= M) return;
    int mc = (int) (0.4 * M);
    double max = 0;
    InnerNode n1 = null, n2 = null;
    for (int i : sons) {
      SerializableNode n = Dictionary.getNode(i);
      for (int j : sons) {
        SerializableNode m = Dictionary.getNode(j);
        Rectangle mbrn = n.mbr;
        Rectangle mbrm = m.mbr;
        double area = (Math.max(mbrn.x2, mbrm.x2) - Math.min(mbrn.x1, mbrm.x1))
            * (Math.max(mbrn.y2, mbrm.y2) - Math.min(mbrn.y1, mbrm.y1));
        double delta = area - (mbrn.area() + mbrm.area());
        if (max < delta) {
          max = delta;
          n1 = new InnerNode(n, parent);
          n2 = new InnerNode(m, parent);
        }
      }
    }
    commonSplit(mc, n1, n2);
    InnerNode p = (InnerNode) Dictionary.getNode(parent);
    if (p != null) {
      p.quadraticSplit();
    }
  }

  public void linearSplit() {
    if (sons.size() <= M) return;
    int mc = (int) (0.4 * M);
    InnerNode n1 = null, n2 = null;
    double rangex = mbr.x2 - mbr.x1;
    double rangey = mbr.y2 - mbr.y1;
    double max = 0;
    for (int i : sons) {
      SerializableNode n = Dictionary.getNode(i);
      for (int j : sons) {
        SerializableNode m = Dictionary.getNode(j);
        double ndistx = (n.mbr.x2 - m.mbr.x1) / rangex;
        double ndisty = (n.mbr.y2 - m.mbr.y1) / rangey;
        if (max < (Math.max(ndistx, ndisty))) {
          max = Math.max(ndistx, ndisty);
          n1 = new InnerNode(n, parent);
          n2 = new InnerNode(m, parent);
        }

      }
    }
    commonSplit(mc, n1, n2);
    InnerNode p = (InnerNode) Dictionary.getNode(parent);
    if (p != null) p.linearSplit();
  }

  public int getDepth() {
    return Dictionary.getNode(sons.get(0)).getDepth() + 1;
  }

  public String display(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {
      sb.append("  "); 
    }
    sb.append("{MBR:(" + Double.toString(mbr.x1) + "," + Double.toString(mbr.y1) + "),("
        + Double.toString(mbr.x2) + "," + Double.toString(mbr.y2) + ")}\n");
    for (int i : sons) {
      SerializableNode n = Dictionary.getNode(i);
      sb.append(n.display(depth + 1));
    }
    return sb.toString();
  }

  protected void commonSplit(int mc, InnerNode n1, InnerNode n2) {
    int contadorm1 = 1;
    int contadorm2 = 1;
    
    for (int i : sons) {
      if (i == n1.sons.get(0) || i == n2.sons.get(0)) continue;
      SerializableNode n = Dictionary.getNode(i);
      Rectangle mbrnm1 = 
          new Rectangle(Math.min(n.mbr.x1, n1.mbr.x1), Math.min(n.mbr.y1, n1.mbr.y1),
              Math.max(n.mbr.x2, n1.mbr.x2), Math.max(n.mbr.y2, n1.mbr.y2));
      Rectangle mbrnm2 = 
          new Rectangle(Math.min(n.mbr.x1, n2.mbr.x1), Math.min(n.mbr.y1, n2.mbr.y1),
              Math.max(n.mbr.x2, n2.mbr.x2), Math.max(n.mbr.y2, n2.mbr.y2));
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
    SerializableNode p = Dictionary.getNode(parent); //parent node, WARNING: CAN BE NULL
    //no need to save 2 new nodes when I can use -this- node. n <- n1.
    sons = n1.sons; 
    mbr = n1.mbr;
    //index same
    //parent same
    //M same
    Dictionary.writeNode(index, this); //update this node in disk
    if (p == null) {      //if parent node is null, we create a new one
      SerializableNode np = new InnerNode(this, 0); //np "new_parent".
      int n2index = Dictionary.getIndex(n2); //write n2 to disk, receiving the new index
      np.sons.add(n2index); //add index to parent
      parent = Dictionary.getIndex(np); //write np "new_parent" into disk and return index
      n2.parent = parent;
      Dictionary.writeNode(index, this); //update this node with new parent
      Dictionary.writeNode(n2index, n2); //update n2 with new parent
    } else {
      p.sons.add(Dictionary.getIndex(n2)); //parent already has index of this (n1) node, write n2 into disk
      n2.parent = parent; //not neccesary, but whatever
      Dictionary.writeNode(parent, p); //update 
    }
  }

  public int countSelf() {
    int ret = 1;
    for (int i : sons) {
      SerializableNode n = Dictionary.getNode(i);
      ret += n.countSelf();
    }
    return ret;
  }

  public int countLeaf() {
    int ret = 0;
    for (int i : sons) {
      SerializableNode n = Dictionary.getNode(i);
      ret += n.countLeaf();
    }
    return ret;
  }

}
