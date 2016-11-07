package Main;

import RTree.RTree;
import Geometry.Rectangle;

import java.util.ArrayList;

public class Random {

  public static void main(String[] args) {
    ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
    ArrayList<Rectangle> rectsSearch = new ArrayList<Rectangle>();
    
    int n=20;
    for(int i = 0; i<n ; i++){
    	int alto = (int) (Math.random() * (99)) +1;
    	int ancho = (int) (Math.random() * (99)) + 1;
    	int x1  = (int) (Math.random() * (500000-100)) ;
    	int y1  = (int) (Math.random() * (500000-100)) ;
    	
    	int x2 = x1 + ancho;
    	int y2 = y1 + alto;
    	rects.add(new Rectangle (x1, y1, x2, y2));
    }

    for(int i = 0; i<n/10 ; i++){
    	int alto = (int) (Math.random() * (99)) +1;
    	int ancho = (int) (Math.random() * (99)) + 1;
    	int x1  = (int) (Math.random() * (500000-100)) ;
    	int y1  = (int) (Math.random() * (500000-100)) ;
    	
    	int x2 = x1 + ancho;
    	int y2 = y1 + alto;
    	rectsSearch.add(new Rectangle (x1, y1, x2, y2));
    }
    
    
    

    RTree quadraticTree = new RTree(5);
    RTree linearTree = new RTree(5, RTree.Heur.LINEAR);
    
    long time_start1, time_end1;
    time_start1 = System.currentTimeMillis();
    for (Rectangle rect : rects) {
      quadraticTree.insertar(rect);
    }
    time_end1 = System.currentTimeMillis();
    System.out.println("the task has taken quadraticTree"+ ( time_end1 - time_start1 ) +" milliseconds");
    
    long time_start, time_end;
    time_start = System.currentTimeMillis();
    for (Rectangle rect : rects) {
      linearTree.insertar(rect);
    }
    time_end = System.currentTimeMillis();
    
    
    
    long time_start2, time_end2;
    time_start2 = System.currentTimeMillis();
    for (Rectangle rect : rectsSearch) {
      quadraticTree.insertar(rect);
    }
    time_end2 = System.currentTimeMillis();
    System.out.println("the task has taken quadraticTree"+ ( time_end2 - time_start2 ) +" milliseconds");
    
    long time_start3, time_end3;
    time_start3 = System.currentTimeMillis();
    for (Rectangle rect : rectsSearch) {
      linearTree.insertar(rect);
    }
    time_end3 = System.currentTimeMillis();
    System.out.println("the task has taken quadraticTree"+ ( time_end3 - time_start3 ) +" milliseconds");
    
    
    
    
    
    
    
    
    
    System.out.println("the task has taken linear"+ ( time_end - time_start ) +" milliseconds");
    
    System.out.println(quadraticTree.display());
    System.out.println(linearTree.display());
  }

}