import javax.swing.JOptionPane;
import java.util.Scanner;
import java.util.Arrays;
import javax.swing.JFileChooser;
import java.io.File;
import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
public class OverhangRunner{
   ArrayList<Integer> layers = new ArrayList<Integer>(0);
   File gCode;
   double layerHeight = 0;
   double nozzleDiameter = 0;
   public OverhangRunner(){
      nozzleDiameter = Double.parseDouble(JOptionPane.showInputDialog(null,"Input nozzle Diameter:"));
      String choice = JOptionPane.showInputDialog(null, "Manual or Automatic?\nManual (M):Put in the layers that have overhangs\nAutomatic (A): Program will attempt to find all instances of an overhang \n(Takes longer)");
      ArrayList<Contour> contour = new ArrayList<Contour>(0);
      Contour[] contours;
      if(choice.equals("M")){
         boolean done = false;
         while(!done){
            String layer = JOptionPane.showInputDialog(null,"Input next layer that has overhang (lowest layer to highest layer). If done, type done");  
            if(layer.equals("done")){
               done = true;
            } else {
               layers.add(Integer.parseInt(layer));
               contour.add(getContour(Integer.parseInt(layer)));
            }
         }
         contours = new Contour[contour.size()];
         for(int i = 0; i < contours.length; i++){
            contours[i] = contour.get(i);
         }
         //System.out.println(contours.length+","+layers.size());
         Contour[][] contourMaps = new Contour[contours.length][];
         for(int i = 0; i < contours.length; i++){
            contourMaps[i] = getContourMap(contours[i],getContour(layers.get(i)-1),nozzleDiameter);
         }
         System.out.println(contourMaps[0].length);
         gCodeEditor gce = new gCodeEditor(gCode,nozzleDiameter, layerHeight);
         gce.replaceGCode(contourMaps,layers);
      } else {
         
      } 
   }
   public static void main(String[] args){
      OverhangRunner or = new OverhangRunner();
   }
   public Contour[] getContourMap(Contour initialMain, Contour initialSub, double distance){
      ArrayList<Contour> contours = new ArrayList<Contour>(0);
      int increment = 0;
      initialSub.travelPoints = new ArrayList<Integer>(0);
      contours.add(initialSub);
      Contour sub = initialSub.expand(distance);
      boolean firstContact = false;
      boolean done = false;
      while(!done){
        /* if(increment == 2){
            distance = distance*1.4;
         }*/
         //System.out.println(1);
         Contour temp = mergeContoursNoRepeat(initialMain,sub);
         if(temp != null){
            contours.add(temp);
            firstContact = true;
         }
         //System.out.println((""+increment++)+temp);
         //System.out.println(firstContact);
         //System.out.println(temp+","+increment++);
         if(firstContact){
            //System.out.println(1);
            sub = mergeContours(initialMain,sub);
            //System.out.println(sub);
            //System.out.println(sub+","+increment++);
            //System.out.println(testLineIntersectionContour(initialMain,sub,))
            if(sub == null){
               done = true;
            } else {
               //System.out.println(3);
               sub = sub.expand(distance);
               //System.out.println(4);
            }
         } else {
            sub = sub.expand(distance);
         }
         //System.out.println("w");
         //System.out.println((double)Duration.between(start,end).toMillis()/1000+","+c.point.length+","+i);
      }
      Contour[] c = new Contour[contours.size()];
      for(int i = 0; i < c.length; i++){
         c[i] = contours.get(i);
      }
      c[0].eVal = initialSub.eVal;
      c[0].z = initialSub.z;
      return c;
   }
   public Contour[] getContours(int layerOfInterest){
      layerOfInterest--;
      ArrayList<Contour> contours = new ArrayList<Contour>(0);
      double eVal = 0;
      double z = 0;
      Scanner scanner = null;
      if(gCode == null){
         FileEditor fe = new FileEditor();
         JFileChooser jfc = new JFileChooser();
         jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
         jfc.showDialog(null,"Choose gCode File");
         gCode = jfc.getSelectedFile();
         try{
            scanner = new Scanner(jfc.getSelectedFile());
         } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
         }
      } else {
         try{
            scanner = new Scanner(gCode);
         } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
         }
      }
      Point[] point = new Point[40];
      //String path = jfc.getSelectedFile().getAbsolutePath();
      boolean done = false;
      int increment = 0;
      while(scanner.hasNext() && !done){
         String line = scanner.next();
         //System.out.println(line);
         if(line.equals("height:")){
            done = true;
            line = scanner.next();
            layerHeight = Double.parseDouble(line);
            System.out.println(layerHeight);
         }
      }
      done = false;
      while(scanner.hasNext() && !done){
         String line = scanner.next();
         if(line.charAt(0) == 'Z'){
            z = Double.parseDouble(line.substring(1));
         }
         if(line.equals(";LAYER:"+layerOfInterest)){
            done = true;
         }
      }
      done = false;
      while(scanner.hasNext() && !done){
         String line = scanner.next();
         if(line.equals(";TYPE:WALL-OUTER")){
            done = true;
         }
      }
      done = false;
      boolean done2 = false;
      while(scanner.hasNext() && !done2){
         done = false;
         increment = 0;
         while(scanner.hasNext() && !done){
            String line = scanner.next();
            if(line.equals("G1")){
               if(increment > point.length-1){
                  Point[] sub = new Point[point.length+40];
                  for(int i = 0; i < point.length;i++){
                     sub[i] = point[i];
                  }
                  point = sub;
               }
               while(line.charAt(0) != 'X'){
                  line = scanner.next();
               }
               //System.out.println(line);
               double x = Double.parseDouble(line.substring(1));
               while(line.charAt(0) != 'Y'){
                  line = scanner.next();
               }
               double y = Double.parseDouble(line.substring(1));
               while(line.charAt(0) != 'E'){//Eval weridness going on
                  line = scanner.next();
               }
               eVal = Double.parseDouble(line.substring(1,line.length()));
               //System.out.println(line);
               point[increment] = new Point(x,y);
               //System.out.println(point[increment]);
               increment++;
            } else if(line.equals("G0")){
               Point[] sub = new Point[increment];
               for(int i = 0; i < increment; i++){
                  sub[i] = point[i];
               }
               Contour c = new Contour(sub);
               c.eVal = eVal;
               c.z = z;
               contours.add(c);
               done = true;
            } else if(line.indexOf(";") > -1){
               Point[] sub = new Point[increment];
               for(int i = 0; i < increment; i++){
                  sub[i] = point[i];
               }
               Contour c = new Contour(sub);
               c.eVal = eVal;
               c.z = z;
               contours.add(c);
               done = true;
               done2  = true;
            }
         }
      }
      Object[] sub = contours.toArray();
      Contour[] newContours = new Contour[sub.length];
      for(int i = 0; i < sub.length; i++){
         newContours[i] = (Contour)sub[i];
      }
      for(int i = 0; i < newContours.length; i++){
         for(int j = 0; j < newContours.length; i++){
            if(i != j && newContours[i].containsPoint(newContours[j].point[0])){
               if(newContours[i].isInside){
                  newContours[j].isInside = false;
                  newContours[j].enclose = null;
               } else {
                  newContours[j].isInside = true;
                  newContours[j].enclose = newContours[i];
               }
            }
         }
      }
      return newContours;
   }
  public Contour getContour(int layerOfInterest){//Layer of interest is found by using the preview function and putting in the layer number with the overhang
      layerOfInterest--;
      double eVal = 0;
      double z = 0;
      Scanner scanner = null;
      if(gCode == null){
         FileEditor fe = new FileEditor();
         JFileChooser jfc = new JFileChooser();
         jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
         jfc.showDialog(null,"Choose gCode File");
         gCode = jfc.getSelectedFile();
         try{
            scanner = new Scanner(jfc.getSelectedFile());
         } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
         }
      } else {
         try{
            scanner = new Scanner(gCode);
         } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
         }
      }
      Point[] point = new Point[40];
      //String path = jfc.getSelectedFile().getAbsolutePath();
      boolean done = false;
      int increment = 0;
      while(scanner.hasNext() && !done){
         String line = scanner.next();
         //System.out.println(line);
         if(line.equals("height:")){
            done = true;
            line = scanner.next();
            layerHeight = Double.parseDouble(line);
            System.out.println(layerHeight);
         }
      }
      done = false;
      while(scanner.hasNext() && !done){
         String line = scanner.next();
         if(line.charAt(0) == 'Z'){
            z = Double.parseDouble(line.substring(1));
         }
         if(line.equals(";LAYER:"+layerOfInterest)){
            done = true;
         }
      }
      done = false;
      while(scanner.hasNext() && !done){
         String line = scanner.next();
         if(line.equals(";TYPE:WALL-OUTER")){
            done = true;
         }
      }
      done = false;
      while(scanner.hasNext() && !done){
         String line = scanner.next();
         if(line.equals("G1")){
            if(increment > point.length-1){
               Point[] sub = new Point[point.length+40];
               for(int i = 0; i < point.length;i++){
                  sub[i] = point[i];
               }
               point = sub;
            }
            while(line.charAt(0) != 'X'){
               line = scanner.next();
            }
            //System.out.println(line);
            double x = Double.parseDouble(line.substring(1));
            while(line.charAt(0) != 'Y'){
               line = scanner.next();
            }
            double y = Double.parseDouble(line.substring(1));
            while(line.charAt(0) != 'E'){//Eval weridness going on
               line = scanner.next();
            }
            eVal = Double.parseDouble(line.substring(1,line.length()));
            //System.out.println(line);
            point[increment] = new Point(x,y);
            //System.out.println(point[increment]);
            increment++;
         } else if(line.equals("G0")){
            done = true;
         }
      }
      Point[] sub = new Point[increment];
      for(int i = 0; i < increment; i++){
         sub[i] = point[i];
      }
      Contour c = new Contour(sub);
      c.eVal = eVal;
      c.z = z;
      return c;
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
   public Contour mergeContours(Contour main, Contour sub){
      Point[] newPoint = new Point[main.point.length+sub.point.length];
      boolean main1 = false;
      boolean done = false;
      int mainIndex= 0;
      int subIndex = 0;
      int subEndIndex = 0;
      while(!done){
         if(subIndex == sub.point.length){
            return null;
         }
         if(main.containsPoint(sub.point[subIndex])){
            done = true;
         } else {
            subIndex++;
            subEndIndex++;
         }
      }
      for(int i = 0; i < newPoint.length; i++){
         if(subEndIndex == subIndex%sub.point.length && i != 0){
            i = newPoint.length;
         } else if(main1){
            if((subIndex = testLineIntersectionContour(main.point[mainIndex%main.point.length],main.point[(mainIndex+1)%main.point.length],sub)) > -1){
               if(main.containsPoint(sub.point[(subIndex+1)%sub.point.length])){
                  Point intersection = getLineIntersection(main.point[mainIndex%main.point.length],main.point[(mainIndex+1)%main.point.length],sub.point[subIndex%sub.point.length],sub.point[(subIndex+1)%sub.point.length]);
                  newPoint[i] = main.point[(mainIndex++)%main.point.length];
                  i++;
                  newPoint[i%newPoint.length] = intersection;
                  main1 = !main1;
                  subIndex++;
               } else {
                   newPoint[i] = main.point[(mainIndex++)%main.point.length];   
               }
            } else {
               newPoint[i] = main.point[(mainIndex++)%main.point.length];
            }
            
         } else {
            if((mainIndex = testLineIntersectionContour(sub.point[subIndex%sub.point.length],sub.point[(subIndex+1)%sub.point.length],main)) > -1){
               Point intersection = getLineIntersection(sub.point[subIndex%sub.point.length],sub.point[(subIndex+1)%sub.point.length],main.point[mainIndex%main.point.length],main.point[(mainIndex+1)%main.point.length]);
               newPoint[i] = sub.point[(subIndex++)%sub.point.length];
               i++;
               newPoint[i % newPoint.length] = intersection;
               main1 = !main1;
               mainIndex++;
            } else {
               newPoint[i] = sub.point[(subIndex++)%sub.point.length];
            }
         }
      }
      //System.out.println(Arrays.toString(newPoint));
      int length = 0;
      for(int i = 0; i < newPoint.length; i++){
         if(newPoint[i] == null){
            length = i;
            break;
         }
      }
      Point[] newPoint2 = new Point[length];
      for(int i = 0; i < newPoint2.length; i++){
         newPoint2[i] = newPoint[i];
      }
      //System.out.println(Arrays.toString(newPoint2));
      if(length != 0){
         //System.out.println("good");
         return new Contour(newPoint2);
      } else {
         //System.out.println("wtf");
         //return new Contour(newPoint);
         return null;
      }
   }
   public Contour mergeContoursNoRepeat(Contour main, Contour sub){
      ArrayList<Integer> travelPoints = new ArrayList<Integer>(0);
      Point[] newPoint = new Point[main.point.length+sub.point.length];
      boolean main1 = false;
      boolean done = false;
      int mainIndex= 0;
      int subIndex = 0;
      int subEndIndex = 0;
      while(!done){
         if(subIndex == sub.point.length){
            return null;
         }
         if(main.containsPoint(sub.point[subIndex])){
            done = true;
         } else {
            subIndex++;
            subEndIndex++;
         }
      }
      //System.out.println(2);
      for(int i = 0; i < newPoint.length; i++){
         if(subEndIndex == subIndex%sub.point.length && i != 0){
            i = newPoint.length;
         } else if(main1){
            while(main1){
            //System.out.println(3);
               if((subIndex = testLineIntersectionContour(main.point[mainIndex%main.point.length],main.point[(mainIndex+1)%main.point.length],sub)) > -1){
                  if(main.containsPoint(sub.point[(subIndex+1)%sub.point.length])){
                     Point intersection = getLineIntersection(main.point[mainIndex%main.point.length],main.point[(mainIndex+1)%main.point.length],sub.point[subIndex%sub.point.length],sub.point[(subIndex+1)%sub.point.length]);
                     //newPoint[i] = main.point[(mainIndex++)%main.point.length];
                     //i++;
                     newPoint[i] = intersection;
                     main1 = !main1;
                     subIndex++;
                  } else {
                     mainIndex++;
                  }
               } else {
                  mainIndex++;
                  //newPoint[i] = main.point[(mainIndex++)%main.point.length];
               }
            }
            
         } else {
            if((mainIndex = testLineIntersectionContour(sub.point[subIndex%sub.point.length],sub.point[(subIndex+1)%sub.point.length],main)) > -1){
               Point intersection = getLineIntersection(sub.point[subIndex%sub.point.length],sub.point[(subIndex+1)%sub.point.length],main.point[mainIndex%main.point.length],main.point[(mainIndex+1)%main.point.length]);
               newPoint[i] = sub.point[(subIndex++)%sub.point.length];
               i++;
               newPoint[i%newPoint.length] = intersection;
               travelPoints.add(i+1);
               main1 = !main1;
               mainIndex++;
            } else {
               newPoint[i] = sub.point[(subIndex++)%sub.point.length];
            }
         }
      }
      //System.out.println(Arrays.toString(newPoint));
      int length = 0;
      for(int i = 0; i < newPoint.length; i++){
         if(newPoint[i] == null){
            length = i;
            break;
         }
      }
      Point[] newPoint2 = new Point[length];
      for(int i = 0; i < newPoint2.length; i++){
         newPoint2[i] = newPoint[i];
      }
      //System.out.println(Arrays.toString(newPoint2));
      if(length != 0){
         Contour c1 = new Contour(newPoint2);
         c1.travelPoints = travelPoints;
         return c1;
      } else {
         //Contour c1 = new Contour(newPoint);
         //c1.travelPoints = travelPoints;
         //return c1;
         return null;
      }
   }
   public int testLineIntersectionContour(Point p11, Point p12, Contour c){
      for(int i = 0; i < c.point.length; i++){
         if(testLineIntersection(p11,p12,c.point[i],c.point[(i+1)%c.point.length])){
            return i;
         }
      }
      return -1;
   }  
}