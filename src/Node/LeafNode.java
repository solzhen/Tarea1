package Node;

import java.util.ArrayList;

import Geometry.Rectangle;

public class LeafNode extends Node {
  public ArrayList<Rectangle> sons = new ArrayList<Rectangle>();
  
  public LeafNode(Rectangle n, InnerNode parent) {
    mbr = new Rectangle(n.x1,n.y1,n.x2,n.y2);
    this.parent = parent;
    sons.add(n);
  }
  public LeafNode() {
    parent = null;
  }
  public ArrayList<Rectangle> buscar(Rectangle rect) {
    ArrayList<Rectangle> ret = new ArrayList<Rectangle>();
    for (Rectangle n : sons) {
      if (n.intersects(rect)) ret.add(rect);
    }
    return ret;
  }
  public LeafNode insertar(Rectangle rect) {
    sons.add(rect);
    fixMbr();
    return this;
  }
  public int getDepth() {
	  return 1;
  }
  private void fixMbr() {
    double p1 = sons.get(0).x1;
    double q1 = sons.get(0).y1;
    double p2 = sons.get(0).x2;
    double q2 = sons.get(0).y2;
    for (Rectangle n : sons) {
      p1 = Math.min(p1, n.x1);
      q1 = Math.min(q1, n.y1);
      p2 = Math.max(p2, n.x2);
      q2 = Math.max(q2, n.y2);
    }
    this.mbr = new Rectangle(p1, q1, p2, q2);
    if (parent != null)
      this.parent.fix();
  }
  public void quadraticSplit(int M) {
    int mc = (int) (0.4 * M);
    double max = 0;
    LeafNode n1 = null, n2 = null;
    for (Rectangle n : sons) {
      for (Rectangle m : sons) {
        double area = (Math.max(n.x2, m.x2) - Math.min(n.x1, m.x1))
            * (Math.max(n.y2, m.y2) - Math.min(n.y1, m.y1));
        double delta = area - (n.area() + m.area());
        if (max < delta) {
          max = delta;
          n1 = new LeafNode(n, parent);
          n2 = new LeafNode(m, parent);
        }
      }
    }
    commonSplit(mc, n1, n2);
    if (parent.sons.size() > M) parent.quadraticSplit(M);
  }

  
  public void linearSplit(int M) {
    int mc = (int) (0.4 * M);
    LeafNode n1 = null, n2 = null;
    double rangex = mbr.x2 - mbr.x1;
    double rangey = mbr.y2 - mbr.y1;
    double max = 0;
    for (Rectangle n : sons) {
      for (Rectangle m : sons) {
        double ndistx = (n.x2 - m.x1) / rangex;
        double ndisty = (n.y2 - m.y1) / rangey;
        if (max < (Math.max(ndistx, ndisty))) {
          max = Math.max(ndistx, ndisty);
          n1 = new LeafNode(n, parent);
          n2 = new LeafNode(m, parent);
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
    for (Rectangle n : sons) {
      for (int i = 0; i <= depth; i++) {sb.append("    ");}
      sb.append("<(" + Double.toString(n.x1) + "," + Double.toString(n.y1) + "),("
          + Double.toString(n.x2) + "," + Double.toString(n.y2) + ")>\n");
    }
    return sb.toString();
  }
  
  protected void commonSplit(int mc, LeafNode n1, LeafNode n2) {
    int contadorm1 = 0;
    int contadorm2 = 0;
    
    for (Rectangle n : sons) {
      if (n == n1.sons.get(0)) continue;
      if (n == n2.sons.get(0)) continue;
      Rectangle mbrnm1 =
          new Rectangle(Math.min(n.x1, n1.mbr.x1), Math.min(n.y1, n1.mbr.y1),
              Math.max(n.x2, n1.mbr.x2), Math.max(n.y2, n1.mbr.y2));
      Rectangle mbrnm2 =
          new Rectangle(Math.min(n.x1, n2.mbr.x1), Math.min(n.y1, n2.mbr.y1),
              Math.max(n.x2, n2.mbr.x2), Math.max(n.y2, n2.mbr.y2));
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
    if (parent != null) {
      parent.sons.remove(this);
      parent.sons.add(n1);
      parent.sons.add(n2);
    }
    else {
      parent = new InnerNode(n1, null);
      parent.sons.add(n2);
      n1.parent = parent;
      n2.parent = parent;
    }
  }
}
