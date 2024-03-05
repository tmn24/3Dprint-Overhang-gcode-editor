public class Point{
   double x, y, z, magnitude;
   int javaX, javaY, dim;
   Point(double x, double y){
      this.x = x;
      this.y = y;
      dim = 2;
      magnitude = Math.sqrt(x*x+y*y);
   }
   Point(double x, double y, double z){
      this.x = x;
      this.y = y;
      this.z = z;
      dim = 3;
      magnitude = Math.sqrt(x*x+y*y+z*z);
   }
   void toJava(int cw, int ch){
      javaX = (int)(cw/2+x);
      javaY = (int)(ch/2-y);
   }
   Point add(Point p1, Point p2){
      if(dim == 2){
         return new Point(p1.x+p2.x,p1.y+p2.y);
      } else if(dim == 3){
         return new Point(p1.x+p2.x,p1.y+p2.y,p1.z+p2.z);
      }
      return null;
   }
   double dotProduct(Point p1, Point p2){
      if(dim == 2){
         return p1.x*p2.x+p1.y*p2.y;
      } else if(dim == 3){
         return p1.x*p2.x+p1.y*p2.y+p1.z*p2.z;
      }
      return 1;
   }
   Point normalize(Point p1){
      return p1.scale(p1,1/p1.magnitude);
   }
   Point subtract(Point p1, Point p2){
      if(dim == 2){
         //System.out.println(new Point(p1.x-p2.x,p1.y-p2.y,p1.z-p2.z));
         return new Point(p1.x-p2.x,p1.y-p2.y);
      } else if(dim == 3){
         return new Point(p1.x-p2.x,p1.y-p2.y,p1.z-p2.z);
      }
      return null;
   }
   double crossProductMagnitude(Point p1, Point p2){
      return Math.sqrt((p1.y*p2.z-p1.z*p2.y)*(p1.y*p2.z-p1.z*p2.y)+(p1.z*p2.x-p1.x*p2.z)*(p1.z*p2.x-p1.x*p2.z)+(p1.x*p2.y-p2.x*p1.y)*(p1.x*p2.y-p2.x*p1.y));
   }
   Point crossProduct(Point p1, Point p2){
      /*System.out.println(p1);
      System.out.println(p2);
      System.out.println(new Point(p1.y*p2.z-p1.z*p2.y,p1.z*p2.x-p1.x*p2.z,p1.x*p2.y-p2.x*p1.y));
      System.out.println((p1.y*p2.z-p1.z*p2.y)+","+(p1.z*p2.x-p1.x*p2.z)+","+(p1.x*p2.y-p2.x*p1.y));
      System.out.println("|");
      */
      return new Point(p1.y*p2.z-p1.z*p2.y,p1.z*p2.x-p1.x*p2.z,p1.x*p2.y-p2.x*p1.y);
   }
   Point scale(Point p, double scale){
      if(dim == 2){
         return new Point(p.x*scale,p.y*scale);
      } else if(dim == 3){
         return new Point(p.x*scale,p.y*scale,p.z*scale);
      }
      return null;
   }
   public double scalarProject(Point p1, Point p2){
      return p1.dotProduct(p1,p2)/p2.magnitude;
   }
   public Point projectOnto(Point p1, Point p2){
      return scale(p2, dotProduct(p1,p2)/(p2.magnitude*p2.magnitude));
   }
   public String toString(){
      return "("+x+","+y+","+z+")";
   }
   public boolean equals(Point p){
      if(this.subtract(p,this).magnitude < 0.00001){
         return true;
      }
      return false;
   }
}