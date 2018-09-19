import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
//import java.media.j3d.Transform3d;


public class Main {

    private static double rotateX, rotateY, rotateZ, theta, scale, transX, transY, transZ;
    private static String objectFile;

    //public static ArrayList<ArrayList<String>> obj= new ArrayList<ArrayList<String>>;
    public static void main(String[] args) throws Exception {
        String test = args[0];

        File file = new File(test);
        getVars(file);


    }

    public static void getVars(File file) throws Exception {
        Scanner scan = new Scanner(file);
        int count = 0;
        while (scan.hasNext()) {
            String con = scan.next();
            System.out.println(con);
            rotateX = scan.nextDouble();
            rotateY = scan.nextDouble();
            rotateZ = scan.nextDouble();
            theta = scan.nextDouble();
            scale = scan.nextDouble();
            transX = scan.nextDouble();
            transY = scan.nextDouble();
            transZ = scan.nextDouble();
            objectFile = scan.next();

            Scanner counter = new Scanner(new File(objectFile));
            while (counter.hasNext()) {
                if (counter.hasNext("v")) {
                    count++;
                }
                String con1 = counter.nextLine();
            }
            //double [][] objPoint = new double[3][count];
            //buildFileTrans(objectFile);
            buildObjPoint(objectFile);
        }
    }


    public static void buildObjPoint(String objectFile) throws Exception {
        Scanner obj = new Scanner(new File(objectFile));
        //int j = 0;

        FileWriter fw = new FileWriter("test.obj");
        BufferedWriter bw = new BufferedWriter(fw);

        theta = Math.toRadians(theta);
        double[][] x = {{1, 0, 0}, {0, Math.cos(theta), (Math.sin(theta) * -1)}, {0, Math.sin(theta), Math.cos(theta)}};
        double[][] y = {{Math.cos(theta), 0, (Math.sin(theta) * -1)}, {0, 1, 0}, {Math.sin(theta), 0, Math.cos(theta)}};
        double[][] z = {{Math.cos(theta), (Math.sin(theta) * 1), 0}, {Math.sin(theta), Math.cos(theta), 0}, {0, 0, 1}};

        while (obj.hasNext()) {
            double pVector[] = new double[3];
            double result[] = new double[3];

            if (obj.hasNext("v")) {
                String build = "";

                String con = obj.next();
                for (int i = 0; i < 3; i++) {
                    double temp = Double.parseDouble(obj.next());
                    pVector[i] = temp;
                }


                if (rotateZ == 1) {
                    for (int q = 0; q < 3; q++) {
                        for (int w = 0; w < 3; w++) {
                            result[q] += z[q][w] * pVector[w];
                        }

                        //System.out.print(result[q] + " ");
                        build = build + Double.toString(result[q]) + " ";
                    }
                    build = "v " + build;
                    bw.write(build);
                    System.out.println("\n");
                }

                if (rotateY == 1) {
                    for (int q = 0; q < 3; q++) {
                        for (int w = 0; w < 3; w++) {
                            result[q] += y[q][w] * pVector[w];
                        }

                       // System.out.print(result[q] + " ");
                        build = build + Double.toString(result[q]) + " ";
                    }
                    build = "v " + build;
                    bw.write(build);
                    System.out.println("\n");
                }
                if (rotateX == 1) {
                    for (int q = 0; q < 3; q++) {
                        for (int w = 0; w < 3; w++) {
                            result[q] += x[q][w] * pVector[w];
                        }

                        build = build + Double.toString(result[q]) + " ";
                    }
                    build = "v " + build;
                    bw.write(build);
                    System.out.println("\n");
                }


            }
            bw.write(obj.nextLine() + "\n");
            bw.flush();
        }
        scaler("test.obj", scale);

        buildFileTrans("test.obj");
    }

    public static void scaler(String objectFile, double sc)throws Exception{
        Scanner obj = new Scanner(new File(objectFile));
        FileWriter fw = new FileWriter("test.obj");
        BufferedWriter bw = new BufferedWriter(fw);
        double [][] scaleMatrix = new double[3][3];

        while(obj.hasNext()) {
            double[] pVector = new double[3];
            double[] result = new double[3];

            if(obj.hasNext("v")) {
                String con = obj.next();
                for(int j = 0; j < 3; j++){
                    double temp = Double.parseDouble(obj.next());
                    pVector[j] = temp;
                }

                for (int i = 0; i < 3; i++) {
                    scaleMatrix[i][i] = sc;
                }
                for (int q = 0; q < 3; q++) {
                    for (int w = 0; w < 3; w++) {
                        result[q] += scaleMatrix[q][w] * pVector[w];
                    }
                }
            }
        }


    }


    public static void buildFileTrans(String objectFile) throws Exception {

        Scanner obj = new Scanner(new File(objectFile));
        FileWriter fw = new FileWriter("test2.obj");
        BufferedWriter bw = new BufferedWriter(fw);
        String build = "";
        while (obj.hasNext()) {
            if (obj.hasNext("v")) {
                String consume = obj.next();
                double temp1 = (Double.parseDouble(obj.next()) + transX);
                double temp2 = (Double.parseDouble(obj.next()) + transY);
                double temp3 = (Double.parseDouble(obj.next()) + transZ);
                build = "v " + Double.toString(temp1) + " " + Double.toString(temp2) + " " + Double.toString(temp3);
                bw.write(build);
            }
            bw.write(obj.nextLine() + "\n");
            bw.flush();
        }

        bw.close();

    }
}



