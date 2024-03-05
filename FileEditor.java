import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.nio.file.*;
import java.io.BufferedWriter;
import static java.nio.file.StandardCopyOption.*;
public class FileEditor{
   File file;
   FileWriter fw;
   BufferedWriter bw;
   FileEditor(){
   }
   public void createNewFile(String pathname){
      file = new File(pathname);
      try{
      file.createNewFile();
      } catch (IOException e){  
         e.printStackTrace();   
      }
   }
   public void writeToFile(String pathname, String text){
      try{
      fw = new FileWriter(pathname);
      fw.write(text);
      fw.close();
      } catch (IOException e){
         System.out.println(1);     
      }
   }
   public String readFile(String pathname){
      try{
      file = new File(pathname);  
      Scanner scanner = new Scanner(file);
      return scanner.nextLine();
      } catch (FileNotFoundException e){
         return "";
      }
   }
   public void copyTo(String filePathname, String targetPathname){
      file = new File(filePathname);
      File file2 = new File(targetPathname);
      try{
      Files.copy(file.toPath(),file2.toPath(),REPLACE_EXISTING);
      } catch (Exception e){
         
      }
   }
   public boolean exists(String pathname){
      file = new File(pathname);
      return file.exists();
   }
   public String[] listFiles(String pathname){
      file = new File(pathname);
      File[] files = file.listFiles();
      String[] fileNames = new String[files.length];
      for(int i = 0; i < files.length; i++){
         fileNames[i] = files[i].getAbsolutePath();
         fileNames[i] = fileNames[i].replace('\\', '/');
      }
      return fileNames;
   }
   public void addWriting(String pathname, String text){
      try{
      if(fw == null){
         fw = new FileWriter(pathname);
         bw = new BufferedWriter(fw);
      }
      //System.out.println(text);
      fw.write(text);
      } catch (IOException e){
         e.printStackTrace();     
      }
   }
   public void closeFile(String pathname){
      try{
         if(fw == null){
         fw = new FileWriter(pathname);
         }
         fw.close();
      } catch (IOException e){
         System.out.println(1);
      }
   }
}