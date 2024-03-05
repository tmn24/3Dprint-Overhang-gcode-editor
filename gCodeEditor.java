import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import java.util.Scanner;
public class gCodeEditor{
   File gCode;
   File newFile;
   double nozzleDiameter, layerHeight;
   double[] eVal, z;
   ArrayList<Integer> layers = new ArrayList<Integer>(0);
   public gCodeEditor(File gCode, double nozzleDiameter, double layerHeight){
      this.gCode = gCode;
      this.layerHeight = layerHeight;
      this.nozzleDiameter = nozzleDiameter;
   }
   public void replaceGCode(Contour[][] contourMaps, ArrayList<Integer> layers){
      this.layers = layers;
      eVal = new double[contourMaps.length];
      z = new double[contourMaps.length];
      for(int i = 0; i < contourMaps.length; i++){
         eVal[i] = contourMaps[i][0].eVal;
         z[i] = contourMaps[i][0].z;
      }
      FileEditor fe = new FileEditor(); //To set up the file writer
      JFileChooser jfc = new JFileChooser();
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      jfc.showDialog(null,"Choose where to save results");
      try{
         if(jfc.getSelectedFile().getAbsolutePath() == null){
            System.exit(0);
         }
      } catch (Exception e){
         System.exit(0);
      }
      Scanner scanner = null;//To read gCode file
         try{
            scanner = new Scanner(gCode);
         } catch (Exception e){
            e.printStackTrace();
            System.exit(0);
         }
      String path = "";
      String name = gCode.getPath().substring(gCode.getPath().lastIndexOf("\\")+1,gCode.getPath().length());
      System.out.println(name);
      path = jfc.getSelectedFile().getAbsolutePath()+"\\OVERHANG-"+name;
      System.out.println(path);
      fe.createNewFile(path);
      String line = "";
      //fe.addWriting(path, "I like playing chess!");
      int increment = 0;
      System.out.println(layers);
      while(scanner.hasNext() && increment != contourMaps.length){
         while(scanner.hasNext() && !line.equals(";LAYER:"+(layers.get(increment)-1))){
            line = scanner.next();
            if(line.equals(";LAYER:"+(layers.get(increment)-1))){
               fe.addWriting(path, "\n"+line);
            } else {
               String number = line.substring(1);
               boolean integer = false;
               try{
                  Integer.parseInt(number);
                  integer = true;
               } catch(Exception e){
                  integer = false;
               }
               if(line.charAt(0) == ';'||(line.charAt(0) == 'G' && integer)||(line.charAt(0) == 'M'&& integer)){
                  
                  line = "\n"+line;
               }
               fe.addWriting(path, line+" ");
            }
         }
         System.out.println(line);
         while(scanner.hasNext()){
               line = scanner.next();
               if(line.length() > 9){
                  if(line.substring(0,10).equals(";TYPE:WALL")){
                     break;
                  }
               }
               String number = line.substring(1);
               boolean integer = false;
               try{
                  Integer.parseInt(number);
                  integer = true;
               } catch(Exception e){
                  integer = false;
               }
               if(line.charAt(0) == ';'||(line.charAt(0) == 'G' && integer)||(line.charAt(0) == 'M'&& integer)){
                  
                  line = "\n"+line;
               }
               fe.addWriting(path, line+" ");
         }
         while(scanner.hasNext() && (!(line.charAt(0) == 'G') || line.length() != 2)){
            line = scanner.next();
            String number = line.substring(1);
            boolean integer = false;
            try{
               Integer.parseInt(number);
               integer = true;
            } catch(Exception e){
               integer = false;
            }
            if(line.charAt(0) == 'G' && integer){
            } else {
               if(line.charAt(0) == ';'){
                  line = "\n"+line;
               }
               fe.addWriting(path, line+" ");
            }
         }
         fe.addWriting(path, "\n");
         //System.out.println(line);
         //fe.addWriting(path,"ur mom");
         //fe.addWriting(path,"GAY!!!!");
         fe.addWriting(path,"M106 S255\r\n");
         for(int j = 0; j < contourMaps[increment].length; j++){
            fe.addWriting(path, contourToGCode(contourMaps[increment][j],eVal,z,increment));
         }
         while(scanner.hasNext() && !line.equals(";MESH:NONMESH")){
            line = scanner.next();
         }
         increment++;
         fe.addWriting(path,"M106 S128\r\n");
         fe.addWriting(path, line);
      }
      while(scanner.hasNext()){
         line = scanner.next();
         String number = line.substring(1);
         boolean integer = false;
         try{
            Integer.parseInt(number);
            integer = true;
         } catch(Exception e){
            integer = false;
         }
         if(line.charAt(0) ==';'||(line.charAt(0)=='G'&&integer)||(line.charAt(0)=='M'&&integer)){
            line = "\n"+line;
         }
         fe.addWriting(path, line+" ");
      }
      fe.closeFile(path);
      //while(!done){
         
      //}
   }
   
   public String contourToGCode(Contour c,double[] eVal, double[] z, int index){
      String writeLine = "";
      int speed = 20*60;
      String space = Character.toString((char) 32);
      boolean previousTravel = false;
      writeLine += "G1 F3000 E"+(eVal[index] -= 3)+"\r\nG0 Z"+(z[index]+0.3)+"\r\nG0"+space+"F9000 "+"X"+(int)(1000*c.point[0].x)/1000.+space+"Y"+(int)(1000*c.point[0].y)/1000.+"\r\nG0 Z"+z[index]+"\r\nG1 F3000 E"+(eVal[index] += 3)+"\r\nG0 F300\r\n";
      previousTravel = true;
      for(int i = 1; i < c.point.length; i++){
         if(c.travelPoints.indexOf(i) > -1){
            writeLine += "G1 F3000 E"+(eVal[index] -= 3)+"\r\nG0 Z"+(z[index]+0.3)+"\r\nG0"+space+"F9000 "+"X"+(int)(1000*c.point[i].x)/1000.+space+"Y"+(int)(1000*c.point[i].y)/1000.+"\r\nG0 Z"+z[index]+"\r\nG1 F3000 E"+(eVal[index] += 3)+"\r\nG0 F300\r\n";
            previousTravel = true;
         } else {
            if(previousTravel){
               eVal[index] += c.point[0].subtract(c.point[i],c.point[i-1]).magnitude*nozzleDiameter*layerHeight*Math.PI/(1.75);//There are TWO of these
               //System.out.println(c.point[0].subtract(c.point[i],lastExtrusion).magnitude);
               writeLine += "G1"+space+"F"+speed+" "+"X"+(int)(1000*c.point[i].x)/1000.+space+"Y"+(int)(1000*c.point[i].y)/1000.+space+"E"+eVal[index]+"\r\n";
               //lastExtrusion = c.point[i];
            } else {
            eVal[index] += c.point[0].subtract(c.point[i],c.point[i-1]).magnitude*nozzleDiameter*layerHeight*Math.PI/(1.75);//There are TWO of these
            //System.out.println(c.point[0].subtract(c.point[i],lastExtrusion).magnitude);
            writeLine += "G1"+space+"X"+(int)(1000*c.point[i].x)/1000.+space+"Y"+(int)(1000*c.point[i].y)/1000.+space+"E"+eVal[index]+"\r\n";
            }
            previousTravel = false;
            //lastExtrusion = c.point[i];
         }   
      }
      if(c.travelPoints.indexOf(0) > -1){
            writeLine += "G1 F3000 E"+(eVal[index] -= 3)+"\r\nG0 Z"+(z[index]+0.3)+"\r\nG0"+space+"F300 "+"X"+(int)(1000*c.point[0].x)/1000.+space+"Y"+(int)(1000*c.point[0].y)/1000.+"\r\nG0 Z"+(z[index])+"\r\nG1 F3000 E"+(eVal[index] += 3)+"\r\nG0 F300\r\n";
         } else {
            if(previousTravel){
               eVal[index] += c.point[0].subtract(c.point[0],c.point[c.point.length-1]).magnitude*nozzleDiameter*layerHeight*Math.PI/(1.75);
               //System.out.println(c.point[0].subtract(c.point[i],lastExtrusion).magnitude);
               writeLine += "G1"+space+"F"+speed+" "+"X"+(int)(1000*c.point[0].x)/1000.+space+"Y"+(int)(1000*c.point[0].y)/1000.+space+"E"+eVal[index]+"\r\n";
               //lastExtrusion = c.point[i];
            } else {
               eVal[index] += c.point[0].subtract(c.point[0],c.point[c.point.length-1]).magnitude*nozzleDiameter*layerHeight*Math.PI/(1.75);
               //System.out.println(c.point[0].subtract(c.point[i],lastExtrusion).magnitude);
               writeLine += "G1"+space+"X"+(int)(1000*c.point[0].x)/1000.+space+"Y"+(int)(1000*c.point[0].y)/1000.+space+"E"+eVal[index]+"\r\n";
            }
            //lastExtrusion = c.point[i];
      } 
      //System.out.println(writeLine);
      //System.out.println("------------");
      return writeLine;
   }
}