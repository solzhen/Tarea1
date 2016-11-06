package Node;

import java.util.ArrayList;

import Geometry.Rectangle;

public class InnerNode extends Node {
  public ArrayList<Node> sons = new ArrayList<Node>();
  public InnerNode(Node n, InnerNode parent) {
    mbr = new Rectangle(n.mbr);
    this.parent = parent;
    sons.add(n);
  }
  public ArrayList<Rectangle> buscar(Rectangle rect) {
    ArrayList<Rectangle> ret = new ArrayList<Rectangle>();
    for (Node n : sons) {
      if (n.mbr.intersects(rect)) ret.addAll(n.buscar(rect));
    }
    return ret;
  }
  public LeafNode insertar(Rectangle rect) {
    Node first = sons.get(0);
    
    double p1 = Math.min(first.mbr.x1, rect.x1);
    double q1 = Math.min(first.mbr.y1, rect.y1);
    double p2 = Math.max(first.mbr.x2, rect.x2);
    double q2 = Math.max(first.mbr.y2, rect.y2);
    double newArea = (p2 - p1) * (q2 - q1);
    double currentArea = first.mbr.area();
    double minDelta = Math.max(0, newArea - currentArea); //Cambio mas pequeno
    Node winner = first;
    double minArea = currentArea; //Area mas pequena entre los candidatos (para el desempate)
    
    for (Node n : sons) {
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
    return winner.insertar(rect);
    
  }
  public void fix() {
    double p1 = sons.get(0).mbr.x1;
    double q1 = sons.get(0).mbr.y1;
    double p2 = sons.get(0).mbr.x2;
    double q2 = sons.get(0).mbr.y2;
    for (Node n : sons) {
      p1 = Math.min(p1, n.mbr.x1);
      q1 = Math.min(q1, n.mbr.y1);
      p2 = Math.max(p2, n.mbr.x2);
      q2 = Math.max(q2, n.mbr.y2);
    }
    this.mbr = new Rectangle(p1, q1, p2, q2);
    if (parent != null)
      this.parent.fix();
  }
  public void quadraticSplit(int M) {
    int mc = (int) (0.4 * M);
    double max = 0;
    InnerNode n1 = null, n2 = null;
    for (Node n : sons) {
      for (Node m : sons) {
        Rectangle mbrn = n.mbr;
        Rectangle mbrm = m.mbr;
        double area = (Math.max(mbrn.x2, mbrm.x2) - Math.min(mbrn.x1, mbrm.x1))
            * (Math.max(mbrn.y2, mbrm.y2) - Math.min(mbrn.y1, mbrm.y1));
        double delta = area - (mbrn.area() + mbrm.area());
        if (max < delta) {
          max = delta;
          n1 = new InnerNode(n, this);
          n2 = new InnerNode(m, this);
        }
      }
    }
    commonSplit(mc, n1, n2);
    if (parent.sons.size() > M) parent.quadraticSplit(M);
  }
  
  public void linearSplit(int M) {
    int mc = (int) (0.4 * M);
    InnerNode n1 = null, n2 = null;
    double rangex = mbr.x2 - mbr.x1;
    double rangey = mbr.y2 - mbr.y1;
    double max = 0;
    for (Node n : sons) {
      for (Node m : sons) {
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
    if (parent.sons.size() > M) parent.linearSplit(M);
  }
  
  public String display(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {sb.append("    ");}
    sb.append("{MBR:(" + Double.toString(mbr.x1) + "," + Double.toString(mbr.y1) + "),("
        + Double.toString(mbr.x2) + "," + Double.toString(mbr.y2) + ")}\n");
    for (Node n : sons) {
      sb.append(n.display(depth + 1));
    }
    return sb.toString();
  }
  
  protected void commonSplit(int mc, InnerNode n1, InnerNode n2) {
    int contadorm1 = 0;
    int contadorm2 = 0;
    for (Node n : sons) {
      if (n == n1.sons.get(0)) continue;
      if (n == n2.sons.get(0)) continue;
      Rectangle mbrnm1 =
          new Rectangle(Math.min(n.mbr.x1, n1.mbr.x1), Math.min(n.mbr.y1, n1.mbr.y1),
              Math.max(n.mbr.x2, n1.mbr.x2), Math.max(n.mbr.y2, n1.mbr.y2));
      Rectangle mbrnm2 =
          new Rectangle(Math.min(n.mbr.x1, n2.mbr.x1), Math.min(n.mbr.y1, n2.mbr.y1),
              Math.max(n.mbr.x2, n2.mbr.x2), Math.max(n.mbr.y2, n2.mbr.y2));
      double delta1 = mbrnm1.area() - n1.mbr.area();
      double delta2 = mbrnm2.area() - n2.mbr.area();
      if (sons.size() - contadorm2 == mc) {
        n1.sons.add(n);
        n1.mbr = mbrnm1;
        contadorm1 += 1;
      } else if (sons.size() - contadorm1 == mc) {
        n2.sons.add(n);
        n2.mbr = mbrnm2;
        contadorm2 += 1;
      } else if (delta1 < delta2) {
        n1.sons.add(n);
        n1.mbr = mbrnm1;
        contadorm1 += 1;
      } else if (delta1 > delta2) {
        n2.sons.add(n);
        n2.mbr = mbrnm2;
        contadorm2 += 1;
      } else {
        if (mbrnm1.area() < mbrnm2.area()) {
          n1.sons.add(n);
          n1.mbr = mbrnm1;
          contadorm1 += 1;
        } else if (mbrnm1.area() > mbrnm2.area()) {
          n2.sons.add(n);
          n2.mbr = mbrnm2;
          contadorm2 += 1;
        } else {
          if (contadorm1 < contadorm2) {
            n1.sons.add(n);
            n1.mbr = mbrnm1;
            contadorm1 += 1;
          } else {
            n2.sons.add(n);
            n2.mbr = mbrnm2;
            contadorm2 += 1;
          }
        }
      }  
    }
    parent.sons.remove(this);
    parent.sons.add(n1);
    parent.sons.add(n2);
  }
}
