import java.util.ArrayList;

public class Experiment {

  public static void main(String[] args) {
    ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
    rects.add(new Rectangle(0, 0, 1, 1));
    rects.add(new Rectangle(2, 2, 4, 4));
    rects.add(new Rectangle(0.5, 0.5, 1.5, 1.5));
    rects.add(new Rectangle(0, 1, 1, 2));
    rects.add(new Rectangle(0.5, 1, 1, 2));
    rects.add(new Rectangle(3, 3, 4, 4));
    rects.add(new Rectangle(0, 0.5, 1, 2));
    rects.add(new Rectangle(0, 0, 4, 1));
    rects.add(new Rectangle(3,3,5,5));
    rects.add(new Rectangle(3.5,3.5,5,5));
    rects.add(new Rectangle(2,2,5,4.5 ));
    RTree quadraticTree = new RTree(5);
    RTree linearTree = new RTree(5, RTree.Heur.LINEAR);
    
    for (Rectangle rect : rects) {
      quadraticTree.insertar(rect);
      linearTree.insertar(rect);
    }
    
    System.out.println(quadraticTree.display());
    System.out.println(linearTree.display());
  }

}
