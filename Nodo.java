import java.util.ArrayList;


interface Nodo {
  ArrayList<Rectangle> buscar(Rectangle rect);

  InnerNodo insertar(Rectangle rect);

  boolean isLeaf();

  double area();

  Rectangle mbr();

  void fix();

  String display(int depth);

  InnerNodo parent();

  void setParent(InnerNodo innerNodo);
}


class InnerNodo implements Nodo {
  InnerNodo parent;
  ArrayList<Nodo> sons = new ArrayList<Nodo>();
  Rectangle mbr;

  public InnerNodo() {}

  public InnerNodo(Nodo m) {
    mbr = new Rectangle(m.mbr());
    parent = m.parent();
  }

  public double area() {
    return mbr.area();
  }

  public ArrayList<Rectangle> buscar(Rectangle rect) {
    ArrayList<Rectangle> ret = new ArrayList<Rectangle>();
    if (mbr.intersects(rect)) {
      for (Nodo n : sons) {
        ret.addAll(n.buscar(rect));
      }
    }
    return ret;
  }

  public InnerNodo insertar(Rectangle rect) {
    if (sons.isEmpty()) {
      sons.add(new Leaf(rect, this));
      return this;
    } else if (sons.get(0).isLeaf()) {
      sons.add(new Leaf(rect, this));
      fix();
      return this;
    } else {
      Nodo m = sons.get(0);
      double min;
      double minarea = 25115;
      Nodo winner;
      double area = new Rectangle(Math.min(m.mbr().x1, rect.x1), Math.min(m.mbr().y1, rect.y1),
          Math.max(m.mbr().x2, rect.x2), Math.max(m.mbr().y2, rect.y2)).area();
      double narea = m.area();
      min = narea - area;
      winner = m;
      minarea = Math.min(minarea, narea);
      for (Nodo n : sons) {
        double p1 = Math.min(n.mbr().x1, rect.x1);
        double q1 = Math.min(n.mbr().y1, rect.y1);
        double p2 = Math.max(n.mbr().x2, rect.x2);
        double q2 = Math.max(n.mbr().y2, rect.y2);
        area = new Rectangle(p1, q1, p2, q2).area();
        narea = n.area();
        if (narea - area < min) {
          min = narea - area;
          winner = n;
          minarea = Math.min(minarea, narea);
        }
        if (narea - area == min) {
          if (minarea > narea) {
            minarea = narea;
            winner = n;
          }
        }
      }
      return winner.insertar(rect);
    }
  }

  public void fix() {
    double p1 = sons.get(0).mbr().x1;
    double q1 = sons.get(0).mbr().y1;
    double p2 = sons.get(0).mbr().x2;
    double q2 = sons.get(0).mbr().y2;
    for (Nodo n : sons) {
      p1 = Math.min(p1, n.mbr().x1);
      q1 = Math.min(q1, n.mbr().y1);
      p2 = Math.max(p2, n.mbr().x2);
      q2 = Math.max(q2, n.mbr().y2);
    }
    this.mbr = new Rectangle(p1, q1, p2, q2);
    if (parent != null)
      this.parent.fix();
  }

  public boolean isLeaf() {
    return false;
  }

  public Rectangle mbr() {
    return mbr;
  }

  public int size() {
    return sons.size();
  }

  public void quadraticSplit(int M) {
    int mc = (int) (0.4 * M);
    double max = 0;
    InnerNodo n1 = null, n2 = null;
    for (Nodo n : sons) {
      for (Nodo m : sons) {
        Rectangle mbrn = n.mbr();
        Rectangle mbrm = m.mbr();
        double area = (Math.max(mbrn.x2, mbrm.x2) - Math.min(mbrn.x1, mbrm.x1))
            * (Math.max(mbrn.y2, mbrm.y2) - Math.min(mbrn.y1, mbrm.y1));
        double delta = area - (n.area() + m.area());
        if (max < delta) {
          max = delta;
          n1 = new InnerNodo(n);
          n2 = new InnerNodo(m);
        }
      }
    }
    int contadorm1 = 0;
    int contadorm2 = 0;

    for (Nodo n : sons) {
      Rectangle mbrnm1 =
          new Rectangle(Math.min(n.mbr().x1, n1.mbr().x1), Math.min(n.mbr().y1, n1.mbr().y1),
              Math.max(n.mbr().x2, n1.mbr().x2), Math.max(n.mbr().y2, n1.mbr().y2));
      Rectangle mbrnm2 =
          new Rectangle(Math.min(n.mbr().x1, n2.mbr().x1), Math.min(n.mbr().y1, n2.mbr().y1),
              Math.max(n.mbr().x2, n2.mbr().x2), Math.max(n.mbr().y2, n2.mbr().y2));
      double delta1 = mbrnm1.area() - n1.area();
      double delta2 = mbrnm2.area() - n2.area();
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
      n.setParent(this);
    }
    this.sons = new ArrayList<Nodo>();
    sons.add(n1);
    sons.add(n2);
    this.mbr = new Rectangle(Math.min(n1.mbr().x1, n2.mbr().x1), Math.min(n1.mbr().y1, n2.mbr().y1),
        Math.max(n1.mbr().x2, n2.mbr().x2), Math.max(n1.mbr().y2, n2.mbr().y2));
  }

  public void linearSplit(int M) {
    int mc = (int) (0.4 * M);
    InnerNodo n1 = null, n2 = null;
    double rangex = mbr.x2 - mbr.x1;
    double rangey = mbr.y2 - mbr.y1;
    double max = 0;
    for (Nodo n : sons) {
      for (Nodo m : sons) {
        double ndistx = (n.mbr().x2 - m.mbr().x1) / rangex;
        double ndisty = (n.mbr().y2 - m.mbr().y1) / rangey;
        if (max < (Math.max(ndistx, ndisty))) {
          max = Math.max(ndistx, ndisty);
          n1 = new InnerNodo(n);
          n2 = new InnerNodo(m);
        }

      }
    }
    int contadorm1 = 0;
    int contadorm2 = 0;

    for (Nodo n : sons) {
      Rectangle mbrnm1 =
          new Rectangle(Math.min(n.mbr().x1, n1.mbr().x1), Math.min(n.mbr().y1, n1.mbr().y1),
              Math.max(n.mbr().x2, n1.mbr().x2), Math.max(n.mbr().y2, n1.mbr().y2));
      Rectangle mbrnm2 =
          new Rectangle(Math.min(n.mbr().x1, n2.mbr().x1), Math.min(n.mbr().y1, n2.mbr().y1),
              Math.max(n.mbr().x2, n2.mbr().x2), Math.max(n.mbr().y2, n2.mbr().y2));
      double delta1 = mbrnm1.area() - n1.area();
      double delta2 = mbrnm2.area() - n2.area();
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
    this.sons = new ArrayList<Nodo>();
    sons.add(n1);
    sons.add(n2);
    this.mbr = new Rectangle(Math.min(n1.mbr().x1, n2.mbr().x1), Math.min(n1.mbr().y1, n2.mbr().y1),
        Math.max(n1.mbr().x2, n2.mbr().x2), Math.max(n1.mbr().y2, n2.mbr().y2));
  }

  public String display(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {sb.append("    ");}
    sb.append("{(" + Double.toString(mbr.x1) + "," + Double.toString(mbr.y1) + "),("
        + Double.toString(mbr.x2) + "," + Double.toString(mbr.y2) + ")}\n");
    for (Nodo n : sons) {
      sb.append(n.display(depth + 1));
    }
    return sb.toString();
  }

  @Override
  public InnerNodo parent() {
    return parent;
  }

  @Override
  public void setParent(InnerNodo innerNodo) {
    parent = innerNodo;
  }


}


class Leaf implements Nodo {
  InnerNodo parent;
  Rectangle mbr;

  public Leaf(Rectangle rect, InnerNodo innerNodo) {
    mbr = rect;
    parent = innerNodo;
  }

  public double area() {
    return mbr.area();
  }

  @Override
  public ArrayList<Rectangle> buscar(Rectangle rect) {
    ArrayList<Rectangle> ret = new ArrayList<Rectangle>();
    if (this.mbr.intersects(rect))
      ret.add(this.mbr);
    return ret;
  }

  @Override
  public InnerNodo insertar(Rectangle rect) {
    return null;
  }

  @Override
  public boolean isLeaf() {
    return true;
  }

  public Rectangle mbr() {
    return mbr;
  }

  public void fix() {}

  public String display(int depth) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < depth; i++) {sb.append("    ");}
    sb.append("<{(" + Double.toString(mbr.x1) + "," + Double.toString(mbr.y1) + "),("
        + Double.toString(mbr.x2) + "," + Double.toString(mbr.y2) + ")}>\n");
    return sb.toString();
  }

  public InnerNodo parent() {
    return parent;
  }

  @Override
  public void setParent(InnerNodo innerNodo) {
    parent = innerNodo;
  }
}
