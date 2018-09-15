import java.io.*;
import java.util.Scanner;

public class Main {

    private static double rotateX, rotateY, rotateZ, theta, scale, transX, transY, transZ;
    private static String objectFile;
    public static void main(String[] args) throws Exception{
        String test = args [0];

        File file = new File(test);
        getVars(file);



    }

    public static void getVars(File file)throws Exception{
        Scanner scan = new Scanner(file);
        //System.out.println(scan.nextLine());
       // while(scan.hasNext()){
            String con = scan.next();
            System.out.println(con);
            rotateX = scan.nextDouble();
            rotateY = scan.nextDouble();
            rotateZ = scan.nextDouble();
            theta = scan.nextInt();
            scale = scan.nextDouble();
            transX = scan.nextDouble();
            transY = scan.nextDouble();
            transZ = scan.nextDouble();
            objectFile = scan.next();
       // }
        File object = new File(objectFile);
        Scanner objFile = new Scanner(object);
        //FileWriter fw = new FileWriter("test1.txt");
        PrintWriter pw = new PrintWriter(new FileWriter("test1.txt", true));

       while(objFile.hasNext()){
            //System.out.println(objFile.next());
           //pw.println(objFile.nextLine());

            if(objFile.next().equals("v")){
                for(int i = 0; i < 3; i++){
                    pw.print("HIT, ");

                }
                pw.println();
                System.out.println(objFile.next());
            }
        }
        pw.close();

    }
}
