
public class Rectangle {

  public double x1, y1, x2, y2;

  public Rectangle(double d, double e, double f, double g) {
    this.x1 = d;
    this.x2 = f;
    this.y1 = e;
    this.y2 = g;
  }

  public Rectangle(Rectangle mbr) {
    x1 = mbr.x1;
    x2 = mbr.x2;
    y1 = mbr.y1;
    y2 = mbr.y2;
  }

  public double area() {
    return (x2 - x1) * (y2 - y1);
  }

  private boolean inter(Rectangle rect, boolean lowSideFirst) {
    boolean ret = false;
    if (rect.x1 >= x1 && rect.x1 < x2) {
      if (rect.y1 >= y1 && rect.y1 < y2)
        ret = true;
      else if (rect.y2 > y1 && rect.y2 <= y2)
        ret = true;
      else
        ret = lowSideFirst && rect.inter(this, false);
    } else {
      ret = lowSideFirst && rect.inter(this, false);
    }
    return ret;
  }

  public boolean intersects(Rectangle rect) {
    return this.inter(rect, true);
  }
}
