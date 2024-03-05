import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Color;//EXPAND METHOD IS NOT WORKING FOR SOME ODD REASON. GETS CAUGHT IN WHILE LOOPS
public class Contour{
   double scale = 3.5;
   Point[] point;
   Point CoM;
   double eVal, z;
   boolean positive = false;
   boolean isInside = false;
   Contour enclose = null;
   ArrayList<Integer> travelPoints;
   Contour(Point[] p){
      point = p;
      point = removeIdenticalPoints(point);
      Point[] centers = new Point[point.length];
      for(int i = 0; i < point.length-1; i++){
         centers[i] = p[0].scale(p[0].add(point[i],point[i+1]),0.5);
      }
      centers[centers.length-1] = p[0].scale(p[0].add(point[0],point[centers.length-1]),0.5);
      double x = 0, y = 0;
      for(Point p1: centers){
         x += p1.x;
         y += p1.y;
      }
      CoM = new Point(x/centers.length,y/centers.length);
   }
   public Point[] scaleContour(double scale){
      Point[] point1 = new Point[point.length];
      for(int i = 0; i < point.length; i++){
         point1[i] = point[i].scale(point[i], (point[i].subtract(point[i],CoM).magnitude+scale)/point[i].subtract(point[i],CoM).magnitude);
      }
      return point1;
   }
   public Point[] removeIdenticalPoints(Point[] p1){
      int index = 0;
      boolean remove = false;
      for(int i = 0; i < p1.length;i++){
         if((p1[i].x-p1[(i+1)%p1.length].x) == 0 && (p1[i].y-p1[(i+1)%p1.length].y) == 0){
            index = i;
            remove = true;
            i = p1.length;
         }
      }
      if(remove){
         Point[] p2 = new Point[p1.length-1];
         int increment = 0;
         for(int i = 0; i < p2.length; i++){
            if(i == index){
               increment++;
            }
            p2[i] = p1[i+increment];
         }
         p2 = removeIdenticalPoints(p2);
         return p2;
      }
      return p1;
   }
   public boolean containsPoint(Point point){
      int noLines = 0;
      double t1;
      double t2;
      Point[] shape = this.point;
      for(int i = 0; i < shape.length-1; i++){
         t2 = -(shape[i].y-point.y)/(shape[i+1].y-shape[i].y);
         t1 = (shape[i+1].x-shape[i].x)*t2+shape[i].x-point.x;
         if(0 <= t2 && t2 <= 1 && t1 >= 0){
            noLines++;
         }
      }
      t2 = -(shape[0].y-point.y)/(shape[shape.length-1].y-shape[0].y);
      t1 = (shape[shape.length-1].x-shape[0].x)*t2+shape[0].x-point.x;
      if(0 <= t2 && t2 <= 1 && t1 >= 0){
         noLines++;
      }
      if(noLines % 2 == 0){
         return false;
      }
      return true;
   }
   public Contour expand(double distance){
      int lines = 200;
      Point[] normal = new Point[point.length];
      //displayPoints(g2,point);
      for(int i = 0; i < point.length; i++){
         normal[i] = new Point((point[(i+1)%point.length].y-point[i].y),-(point[(i+1)%point.length].x-point[i].x));
         normal[i] = CoM.normalize(normal[i]);
         if(containsPoint(CoM.add(CoM.add(CoM.scale(CoM.subtract(point[(i+1)%point.length],point[i]),0.5),point[i]),CoM.scale(normal[i],0.0001)))){
            normal[i] = CoM.scale(normal[i],-1);
         }
         //if(normal[i].magnitude > 1){
         //   System.out.println(normal[i]+","+normal[i].magnitude);
         //}
      }
      ArrayList<Integer> indexStart = new ArrayList<Integer>(0);
      ArrayList<Integer> indexEnd = new ArrayList<Integer>(0);
      //System.out.println(point.length);
      Point[] expand = new Point[2*point.length];
      //Point[] expand2 = new Point[2*point.length];
      for(int i = 0 ; i < 2*point.length; i++){
         expand[i] = CoM.add(point[(int)(i*0.5+0.5)%(point.length)],CoM.scale(normal[(int)(i*0.5)%point.length],distance));
         //expand2[i] = point[(int)(i*0.5+0.5)%(point.length)];
         /*if(Double.isNaN(expand[i].x)){
            System.out.println(CoM.add(point[(int)(i*0.5+0.5)%(point.length)],CoM.scale(normal[(int)(i*0.5)%point.length],distance)));
            System.out.println(point[(int)(i*0.5+0.5)%(point.length)]);
            System.out.println(CoM.scale(normal[(int)(i*0.5)%point.length],distance));
            System.out.println(normal[(int)(i*0.5)%point.length]);
         }*/
      }
      //System.out.println(Arrays.toString(expand));
      //displayPoints(g2,expand);
      int increment = 0;
      for(int i = 0; i < expand.length; i++){
         /*if(testLineIntersection(CoM.add(point[i],CoM.scale(normal[i],distance)),CoM.add(point[(i+1)%point.length],CoM.scale(normal[i],distance)),CoM.add(point[(i+1)%point.length],CoM.scale(normal[(i+1)%point.length],distance)),CoM.add(point[(i+2)%point.length],CoM.scale(normal[(i+1)%point.length],distance)))){
            increment++;
            System.out.println(increment);
         }*/
         for(int j = i+2; j < expand.length-1; j++){
            /*if(testLineIntersection(CoM.add(point[i],CoM.scale(normal[i],distance)),CoM.add(point[(i+1)%point.length],CoM.scale(normal[i],distance)),CoM.add(point[j],CoM.scale(normal[j],distance)),CoM.add(point[(j+1)%point.length],CoM.scale(normal[j],distance)))){
               indexStart.add(i);
               indexEnd.add(j);
               //System.out.println(1);
            }*/
            if(testLineIntersection(expand[i],expand[(i+1)%expand.length],expand[j],expand[(j+1)%expand.length]) && (j < i+expand.length*3/4)){
               indexStart.add(i);
               indexEnd.add(j);
               /*Point[] test = new Point[i+j];
               increment = 0;
               for(int k = 0; k < i+j; k++){
                  if(k == i){
                     increment = j-i;
                  }
                  test[k] = expand[(k+increment)%expand.length];
               }
               Contour c = new Contour(test);
               if(c.containsPoint(CoM.add(CoM.add(CoM.scale(CoM.subtract(expand[1],expand[0]),0.5),expand[0]),CoM.scale(normal[0],0.0000001)))){
                  indexStart.add(i);
                  indexEnd.add(j);
                  //System.out.println(j+","+i);
               } else {
                  indexStart.add(i);
                  indexEnd.add(j);
                  //System.out.println(i+","+j);
               }*/
               //System.out.println(i+","+j+","+expand[i]+","+expand[(i+1)%expand.length]+","+expand[j]+","+expand[(j+1)%expand.length]);
            }
         }
      } 
      /*System.out.println(indexStart);//BASCIALLY FIND THE LOOP AND IF THE EXTERNAL NORMAL VECTOR IS ON THE INSIDE ITS WRONG
      System.out.println(indexEnd);
      if(indexStart.size() > 4){
         //indexStart = new ArrayList<Integer>(0);
         //indexEnd = new ArrayList<Integer>(0);
         displayPoints(g2,expand);
         Point[] n = new Point[6];
         n[0] = new Point(0,0);
         n[1] = new Point(1,0);
         n[2] = new Point(2,0);
         n[3] = new Point(3,0);
         n[4] = new Point(4,0);
         n[5] = new Point(5,0);
         
         System.out.println(Arrays.toString(expand));
         System.out.println(Arrays.toString(point));
         System.out.println(Arrays.toString(normal));
         //return new Contour(n);
      }*/
      //System.out.println(indexStart);//BASCIALLY FIND THE LOOP AND IF THE EXTERNAL NORMAL VECTOR IS ON THE INSIDE ITS WRONG
      //System.out.println(indexEnd);
      Point[] newPoint = new Point[2*point.length];
      increment = 0;
      for(int i = 0; i < newPoint.length; i++){
         int j;
         if((j = indexStart.indexOf(increment)) > -1){
            j = indexEnd.get(j);
            Point intersection = getLineIntersection(expand[increment],expand[(increment+1)%expand.length],expand[j],expand[(j+1)%expand.length]);
            /*System.out.println(increment+","+j);
            System.out.println(expand[increment]);
            System.out.println(expand[increment+1]);
            System.out.println(intersection);
            System.out.println(expand[j]);
            System.out.println(expand[(j+1)%(newPoint.length)]);
            */
            newPoint[i] = expand[increment];
            newPoint[(i+1)%(2*point.length)] = intersection;
            newPoint[(i+2)%(2*point.length)] = expand[(j+1)%(newPoint.length)];
            increment = j+1;
            i += 2;
         } else {
            newPoint[i] = expand[increment%(2*point.length)];
            //System.out.println(expand[increment%(2*point.length)]+","+increment+","+newPoint.length);
            //newPoint[(i+1)%(2*point.length)] = expand[(i+1)%(2*point.length)];
            increment++;
         }
         //System.out.println(increment);
         if(increment >= newPoint.length){
            i = newPoint.length;
         }
      } 
      int length = 0;
      boolean bool = false;
      for(int i = 0; i < newPoint.length; i++){
         //System.out.println(i+","+newPoint[i]+","+(newPoint[i] == null));
         if(newPoint[i] == null){
            length = i;
            i = newPoint.length;
            bool = true;
         }
      }
      //System.out.println(Arrays.toString(newPoint));
      if(!bool){
         double chance = 1;
         ArrayList<Integer> index = new ArrayList<Integer>(0);
         normal = new Point[newPoint.length];
         Contour c = new Contour(newPoint);
         for(int i = 0; i < newPoint.length; i++){
            normal[i] = new Point((newPoint[(i+1)%newPoint.length].y-newPoint[i].y),-(newPoint[(i+1)%newPoint.length].x-newPoint[i].x));
            normal[i] = CoM.normalize(normal[i]);
            if(c.containsPoint(CoM.add(CoM.scale(normal[i],0.001),CoM.add(CoM.scale(CoM.subtract(newPoint[(i+1)%newPoint.length],newPoint[i]),0.5),newPoint[i])))){
               normal[i] = CoM.scale(normal[i],-1);
            }
         }
         //System.out.println(normal[67]);
         while((newPoint.length - index.size()) > lines){
         //System.out.println(2);
         for(int i = 0; i < newPoint.length-1; i++){
            //System.out.println(normal[i]+","+i);
            //System.out.println(normal[(i+1)%newPoint.length]+","+(i+1)%newPoint.length);
            //System.out.println(Math.abs(CoM.dotProduct(normal[i],normal[(i+1)%newPoint.length])/5));
            if(Math.abs(CoM.dotProduct(normal[i],normal[(i+1)%newPoint.length])) > chance*Math.random() && (newPoint.length-index.size()) > newPoint.length/3){
               //boolean bool2 = false;
               /*for(int j = 0; j < point.length; j++){
                  if(testLineIntersection(newPoint[i],newPoint[(i+2)%newPoint.length],point[j],point[(j+1)%point.length])){
                     bool2 = true;
                  }
               }
               if(bool2){
                  System.out.println(bool2);
               }*/
               index.add(i);
               i++;
            }
         }
         }
         //System.out.println(index.size());
         if(index.size() != 0){
            Point[] newPoint2 = new Point[newPoint.length-index.size()];
            //System.out.println(newPoint2.length-index.size()+","+1);
            increment = 0;
            for(int i = 0; i < newPoint2.length; i++){
               if(index.get(increment%index.size()) == (i+increment)%newPoint.length){
                  increment++;
               }
                  newPoint2[i] = newPoint[(i+increment)%newPoint.length];
            }
            //System.out.println(Arrays.toString(newPoint2));
            return new Contour(newPoint2);
         } else {
            return new Contour(newPoint);
         }
      } else {
         Point[] newPoint2 = new Point[length];
         for(int i = 0; i < length; i++){
            //if(newPoint[i] == null){
            //   System.out.println(newPoint[i]+","+i+","+length+","+newPoint.length);
            //}
            newPoint2[i] = newPoint[i];
         }
         //System.out.println(Arrays.toString(newPoint));
         ArrayList<Integer> index = new ArrayList<Integer>(0);
         normal = new Point[newPoint2.length];
         Contour c = new Contour(newPoint2);
         for(int i = 0; i < newPoint2.length; i++){
            normal[i] = new Point((newPoint2[(i+1)%newPoint2.length].y-newPoint2[i].y),-(newPoint2[(i+1)%newPoint2.length].x-newPoint2[i].x));
            normal[i] = CoM.normalize(normal[i]);
            if(c.containsPoint(CoM.add(CoM.scale(normal[i],0.001),CoM.add(CoM.scale(CoM.subtract(newPoint2[(i+1)%newPoint2.length],newPoint2[i]),0.5),newPoint2[i])))){
               normal[i] = CoM.scale(normal[i],-1);
            }
         }
         while((newPoint2.length - index.size()) > lines){
         //System.out.println(1);
         for(int i = 0; i < newPoint2.length-1; i++){
            //System.out.println(normal[i]+","+i);
            //System.out.println(normal[(i+1)%newPoint.length]+","+(i+1)%newPoint.length);
            //System.out.println(Math.abs(CoM.dotProduct(normal[i],normal[(i+1)%newPoint.length])/5));
            double chance = 1;
            if(Math.abs(CoM.dotProduct(normal[i],normal[(i+1)%newPoint2.length])) > chance*Math.random() && (newPoint2.length - index.size()) > newPoint2.length/3){
               //System.out.println((newPoint2.length - index.size())+","+newPoint2.length/2);
               //boolean bool2 = false;
               /*for(int j = 0; j < point.length; j++){
                  if(testLineIntersection(newPoint[i],newPoint[(i+2)%newPoint.length],point[j],point[(j+1)%point.length])){
                     bool2 = true;
                  }
               }
               if(bool2){
                  System.out.println(bool2);
               }*/
               index.add(i);
               i++;
            }
         }
         }
         Point[] newPoint3 = new Point[newPoint2.length-index.size()];
         //System.out.println(newPoint2.length-index.size()+","+2);
         increment = 0;
         if(index.size() != 0){
            for(int i = 0; i < newPoint3.length; i++){
               if(index.get(increment%index.size()) == (i+increment)%newPoint2.length){
                  increment++;
               }
                  newPoint3[i] = newPoint2[(i+increment)%newPoint2.length];
            }
            //System.out.println(Arrays.toString(newPoint));
            //System.out.println(Arrays.toString(newPoint2));
            return new Contour(newPoint3);
         } else {
            return new Contour(newPoint2);
         }
      }
   }
    public void displayPoints(Graphics2D g2, Point[] point){
      for(int i = 0; i < point.length-1; i++){
        // g2.setColor(new Color((int)(255*Math.random()),(int)(255*Math.random()),(int)(255*Math.random())));
         /*if(i == 0){
            g2.setColor(Color.RED);
         } else if(i == 1){
            g2.setColor(Color.BLACK);
         } else if(i == 2){
            g2.setColor(Color.BLACK);
         } if(i % 10 == 0){
            g2.setColor(Color.PINK);
         }*/
         if(i == 4){
            g2.setColor(Color.RED);
         } else if(i == 96){
            g2.setColor(Color.BLUE);
         } else {
            g2.setColor(Color.RED);
         }
         g2.drawLine((int)(point[i].x*scale),(int)(point[i].y*scale),(int)(point[i+1].x*scale),(int)(point[i+1].y*scale));    
      }
      g2.drawLine((int)(point[0].x*scale),(int)(point[0].y*scale),(int)(point[point.length-1].x*scale),(int)(point[point.length-1].y*scale));
   
   }
   public boolean testLineIntersection(Point p11, Point p12, Point p21, Point p22){
      double t2 = ((p12.y-p11.y)*(p21.x-p11.x)-(p12.x-p11.x)*(p21.y-p11.y))/((p22.y-p21.y)*(p12.x-p11.x)-(p12.y-p11.y)*(p22.x-p21.x));
      double t1;
      if(p12.x-p11.x == 0){
         t1 = ((p22.y-p21.y)*t2+p21.y-p11.y)/(p12.y-p11.y);
      } else {
         t1 = ((p22.x-p21.x)*t2+p21.x-p11.x)/(p12.x-p11.x);
      }
      return (0 < t1 && t1 < 1 && 0 < t2 && t2 < 1);
   }
   public Point getLineIntersection(Point p11, Point p12, Point p21, Point p22){
      double t2 = ((p12.y-p11.y)*(p21.x-p11.x)-(p12.x-p11.x)*(p21.y-p11.y))/((p22.y-p21.y)*(p12.x-p11.x)-(p12.y-p11.y)*(p22.x-p21.x));
      double t1;
      if(p12.x-p11.x == 0){
         t1 = ((p22.y-p21.y)*t2+p21.y-p11.y)/(p12.y-p11.y);
      } else {
         t1 = ((p22.x-p21.x)*t2+p21.x-p11.x)/(p12.x-p11.x);
      }
      return new Point(t1*(p12.x-p11.x)+p11.x,t1*(p12.y-p11.y)+p11.y);
   }
}