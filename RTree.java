import java.util.ArrayList;

public class RTree {
  InnerNodo root;
  int M;

  static enum Heur {
    QUADRATIC, LINEAR
  };

  Heur heur;
  
  public RTree() {
    this(20, Heur.QUADRATIC);
  }
  
  public RTree(int M, Heur heur) {
    root = new InnerNodo();
    this.M = M;
    this.heur = heur;
  }
  
  public RTree(int M) {
    this(M, Heur.QUADRATIC);
  }
  
  public RTree(Heur heur) {
    this(20, heur);
  }

  public void setM(int M) {
    this.M = M;
  }

  public void setHeur(Heur HEUR) {
    this.heur = HEUR;
  }

  public String display() {
    return root.display(0);
  }

  public ArrayList<Rectangle> buscar(Rectangle rect) {
    return root.buscar(rect);
  }

  public void insertar(Rectangle rect) {
    InnerNodo n = root.insertar(rect);
    if (n.size() > M) {
      overflowHandler(n);
    }
  }

  private void overflowHandler(InnerNodo n) {
    if (heur == Heur.QUADRATIC)
        n.quadraticSplit(M);
    else
        n.linearSplit(M);
  }
}
