import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
//import java.media.j3d.Transform3d;
import org.jblas.DoubleMatrix;
import org.jblas.Geometry;


public class Main {

    private static double rotateX, rotateY, rotateZ, theta, scale, transX, transY, transZ;
    private static String objectFile, driverFile;
    public static double[]cross = new double[3];
    public static DoubleMatrix ev = new DoubleMatrix(1,3);
    public static DoubleMatrix lv = new DoubleMatrix(1,3);
    public static double[]up = new double[3];
    public static double d, left, bottom, right, top, width, height;
    public static int count = 0;
    public static int countF = 0;
    public static DoubleMatrix WV = new DoubleMatrix(1,3);
    public static DoubleMatrix UV = new DoubleMatrix(1,3);
    public static DoubleMatrix VV = new DoubleMatrix(1,3);
    public static ArrayList<DoubleMatrix> abc = new ArrayList<DoubleMatrix>();
    public static void main(String[] args) throws Exception {
        driverFile = args[0];
        File file = new File(driverFile);
        getVars(file);
    }

    public static void getVars(File file) throws Exception {
        Scanner scan = new Scanner(file);

            while (scan.hasNext()) {
                if(scan.hasNext("eye")){
                    String consume = scan.next();
                    for(int evc = 0; evc < 3; evc++){
                        ev.put(0,evc,scan.nextDouble());
                    }
                }
                if(scan.hasNext("look")){
                    String consume = scan.next();
                    for(int lvc = 0; lvc < 3; lvc++){
                        lv.put(0,lvc,scan.nextDouble());
                    }
                }

                if(scan.hasNext("up")){
                    String consume = scan.next();
                    for(int upc = 0; upc < 3; upc++){
                        up[upc] = scan.nextDouble();
                    }
                }
                if(scan.hasNext("d")){
                    String consume = scan.next();
                    d = scan.nextDouble();
                    d*= -1;
                }
                if(scan.hasNext("bounds")){
                    String consume = scan.next();
                    left = scan.nextDouble();
                    bottom = scan.nextDouble();
                    right = scan.nextDouble();
                    top = scan.nextDouble();
                }
                if(scan.hasNext("res")){
                    String consume = scan.next();
                    width = scan.nextDouble();
                    height = scan.nextDouble();
                }

                if(scan.next().equals("model")) {
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
                        if(counter.hasNext("f")){
                            countF++;
                        }
                        String con1 = counter.nextLine();

                    }
                    double[] vector = {rotateX, rotateY, rotateZ};
                    //System.out.println(Arrays.toString(ev));
                    //System.out.println(Arrays.toString(lv));
                    //System.out.println(Arrays.toString(up));
                    //System.out.println(d);
                    //System.out.printf("%f , %f, %f, %f", left, bottom, right, top);
                    //todo Re-add these methods
                    String rotate = rotate(vector);
                    String rotScale = scaler(rotate, scale);
                    String rotScaleTrans = trans(rotScale);
                    buildOutput(rotScaleTrans);
                    //DoubleMatrix vert = new DoubleMatrix(3,count);
                    //vert = vert.copy(objVertices(rotScaleTrans, vert));
                    cameraSetup();

                }

            }
    }


    public static String rotate(double[]vector) throws Exception{
        double temp = vector[0];
        int location = 0;
        double Wv [] = new double[3];
        for(int j = 0; j <3; j++){
            Wv[j] = vector[j];
        }

        Wv = norm_vector(Wv);
        System.out.println(Arrays.toString(Wv));
        for(int copy = 0; copy < 3; copy++){
            vector[copy] = Wv[copy];
        }

        for(int i = 0; i <3; i++){
            if(temp > vector[i]){
                temp = vector[i];
                location = i;
            }
        }
        vector[location] = 1;
        System.out.println(Arrays.toString(vector));
        DoubleMatrix w = new DoubleMatrix(1,3, vector);
        double [] Uv = new double[3];
        double[] Vv = new double[3];

        cross_prod(Wv, vector);
        Uv = cross.clone();
        //System.out.println("prenorm: " + Arrays.toString(Uv));

        Uv = norm_vector(Uv);
        System.out.println(Arrays.toString(Uv));

        cross_prod(Wv, Uv);
        Vv =cross.clone();
        System.out.println(Arrays.toString(Vv));


        DoubleMatrix Uvv = new DoubleMatrix(1,3, Uv);
        DoubleMatrix Vvv = new DoubleMatrix(1,3,Vv);
        DoubleMatrix Wvv = new DoubleMatrix(1,3,Wv);
        DoubleMatrix RM = new DoubleMatrix(3,3);
        RM.put(0,0, Uvv.get(0,0));
        RM.put(0,1, Uvv.get(0,1));
        RM.put(0,2, Uvv.get(0,2));
        RM.put(1,0, Vvv.get(0,0));
        RM.put(1,1, Vvv.get(0,1));
        RM.put(1,2, Vvv.get(0,2));
        RM.put(2,0, Wvv.get(0,0));
        RM.put(2,1, Wvv.get(0,1));
        RM.put(2,2, Wvv.get(0,2));


        DoubleMatrix RMt =new DoubleMatrix(3,3);
        RMt = RM.transpose();
        DoubleMatrix RMz = new DoubleMatrix(3,3);
        RMz.put(0,0,Math.cos(Math.toRadians(theta)));
        RMz.put(0,1,(Math.sin(Math.toRadians(theta)))*-1);
        RMz.put(0,2,0);
        RMz.put(1,0,Math.sin(Math.toRadians(theta)));
        RMz.put(1,1,Math.cos(Math.toRadians(theta)));
        RMz.put(1,2, 0);
        RMz.put(2,0, 0);
        RMz.put(2,1, 0);
        RMz.put(2,2, 1);
        // RMz.print();

        DoubleMatrix test = new DoubleMatrix(3,3);
        test = RMt.mmul(RMz);
        DoubleMatrix rotMat = new DoubleMatrix(3,3);
        rotMat = test.mmul(RM);




        DoubleMatrix shit = new DoubleMatrix(1,3);
        FileWriter fw = new FileWriter(new File("rotate.obj"));
        Scanner scan = new Scanner(new File(objectFile));
        while(scan.hasNext()){
            if(scan.hasNext("v")){
                String con = scan.next();
                DoubleMatrix n = new DoubleMatrix(1,3,scan.nextDouble(),scan.nextDouble(),scan.nextDouble());
                n = n.transpose();
                shit = rotMat.mmul(n);
                fw.write("v " + shit.get(0) +" " + shit.get(1) +" " + shit.get(2));
                fw.flush();
            }

            fw.write(scan.nextLine() + "\n");
            fw.flush();
        }
        return "rotate.obj";
    }
    public static String scaler(String objectFile, double sc)throws Exception{
        Scanner obj = new Scanner(new File(objectFile));
        FileWriter fw = new FileWriter("scale.obj");
        BufferedWriter bw = new BufferedWriter(fw);
        double [][] scaleMatrix = new double[3][3];

        while(obj.hasNext()) {
            String build ="";
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
                    build = build + Double.toString(result[q]) + " ";
                }
                build = "v " + build;
                bw.write(build);
                bw.flush();
            }
            bw.write(obj.nextLine() + "\n");
            bw.flush();
        }
        return "scale.obj";
    }


    public static String trans(String testFile) throws Exception {
        Scanner obj = new Scanner(new File(testFile));
        FileWriter fw = new FileWriter("trans.obj");
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
        //buildOutput("test3.obj");
        return "trans.obj";
    }

    public static void buildOutput(String file)throws Exception{
        DecimalFormat df = new DecimalFormat("00");
        int count = 0;

        String dir = driverFile.substring(0,8);
        new File(dir).mkdir();
        String fname = objectFile;
        fname = fname.substring(0,fname.lastIndexOf('.'));
        fname = fname + "_mw"+ df.format(count) + ".obj";
        String filePath = "./" +dir+"/"+fname;
        File f = new File(filePath);

        if(f.exists()) {
            while (f.exists()) {
                count++;
                fname = fname.substring(0, fname.lastIndexOf('_'));
                fname = fname + "_mw" + df.format(count) + ".obj";
                f = new File("./" + dir + "/" + fname);

            }
        }

        FileWriter fw = new FileWriter(f);
        Scanner a = new Scanner(new File(file));
        while (a.hasNextLine()){
            fw.write(a.nextLine());
            fw.write("\n");
            fw.flush();
        }

        fw.flush();

        File fileRotate = new File("rotate.obj");
        File fileScale = new File("scale.obj");
        File fileTrans = new File("trans.obj");
        fileRotate.delete();
        fileScale.delete();
       // fileTrans.delete();


    }

    public static void cameraSetup() throws Exception{
        double[] WV1 = new double[3];
        double[] UV1 = new double[3];
        double[] VV1 = new double[3];
        for(int i = 0; i < 3; i++){
            WV1[i] = ev.get(0,i) - lv.get(0,i);
        }
        WV1 = norm_vector(WV1);
        cross_prod(up,WV1);
        UV1 = cross.clone();
        UV1 = norm_vector(UV1);
        cross_prod(WV1,UV1);
        VV1 =cross.clone();
        for(int i = 0; i < 3; i++){
            WV.put(0,i,WV1[i]);
            UV.put(0,i,UV1[i]);
            VV.put(0,i,VV1[i]);
        }
        ray();
    }

    public static DoubleMatrix objVertices(String filename, DoubleMatrix vert)throws Exception{
        Scanner scan = new Scanner(new File(filename));
        int counter = 0;
        while(scan.hasNext()){
            if(scan.hasNext("v")){
                scan.next();

                for(int i = 0; i < 3; i++){
                    vert.put(i,counter,scan.nextDouble());
                }
                counter++;
            }
            if(scan.hasNext("f")){
                DoubleMatrix abcTemp = new DoubleMatrix(3);
                scan.next();
                String temp = scan.nextLine();
                int a = (int)(temp.charAt(1) - '0');
                int b = (int)(temp.charAt(6)- '0');
                int c = (int)(temp.charAt(11) - '0');
                abcTemp.put(0,a);
                abcTemp.put(1,b);
                abcTemp.put(2,c);
                abc.add(abcTemp);

                //cramer(abc);

            }else{
                String consume = scan.nextLine();
            }
        }
        return vert;
    }

    public static void cross_prod(double[] a, double [] b){
        cross[0] = a[1] * b[2] - a[2] * b[1];
        cross[1] = a[2] * b[0] - a[0] * b[2];
        cross[2] = a[0] * b[1]- a[1] * b[0];
    }

    public static double[] norm_vector(double[] preNorm){
        double magnitude = 0;
        double[] normVector = new double[3];

        for (int i = 0; i < 3; i++){
            magnitude += Math.pow(preNorm[i],2);
        }
        magnitude = Math.sqrt(magnitude);
        for(int j = 0; j < 3; j++){
            double temp = preNorm[j] / magnitude;
            normVector[j] = temp;
        }
        return normVector;
    }

    public static void ray()throws Exception {
        DoubleMatrix pixpt = new DoubleMatrix(1,3);
        DoubleMatrix shoot = new DoubleMatrix(1,3);
        Geometry norm = new Geometry();
        DoubleMatrix vert = new DoubleMatrix(3,count);
        vert = objVertices("trans.obj", vert);
        for (int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                double px = i/(width - 1) * (right - left)+ left;
                double py =j/(height -1) * (bottom - top) + top;
                pixpt = ev.add(WV.mmul(d)).add((UV.mmul(px)).add(VV.mmul(py)));
                shoot = pixpt.sub(ev);
                shoot = norm.normalize(shoot);
                cramer(shoot, vert);
                break;
            }
            break;
        }


    }

    public static void cramer(DoubleMatrix shoot, DoubleMatrix vert){
        DoubleMatrix M = new DoubleMatrix(3,3);
        DoubleMatrix M1 = new DoubleMatrix(3,3);
        DoubleMatrix M2 = new DoubleMatrix(3,3);
        DoubleMatrix M3 = new DoubleMatrix(3,3);

        shoot.print();
        for(int i = 0; i < countF; i++){
            //System.out.println(abc.get(i).get(0));
            //System.out.println(abc.get(i));
            DoubleMatrix temp = new DoubleMatrix(3,3);
            temp.put(0,0,vert.get(0,(int)Math.round(abc.get(i).get(0))-1));
            temp.put(1,0,vert.get(1,(int)Math.round(abc.get(i).get(0))-1));
            temp.put(2,0,vert.get(2,(int)Math.round(abc.get(i).get(0))-1));
            temp.put(0,1,vert.get(0,(int)Math.round(abc.get(i).get(1))-1));
            temp.put(1,1,vert.get(1,(int)Math.round(abc.get(i).get(1))-1));
            temp.put(2,1,vert.get(2,(int)Math.round(abc.get(i).get(1))-1));
            temp.put(0,2,vert.get(0,(int)Math.round(abc.get(i).get(2))-1));
            temp.put(1,2,vert.get(1,(int)Math.round(abc.get(i).get(2))-1));
            temp.put(2,2,vert.get(2,(int)Math.round(abc.get(i).get(2))-1));

            M.put(0,0,temp.get(0,0) - temp.get(0,1));
            M.put(1,0,temp.get(1,0) - temp.get(1,1));
            M.put(2,0,temp.get(3,0) - temp.get(2,1));
            M.put(0,1,temp.get(0,0) - temp.get(0,2));
            M.put(1,1,temp.get(1,0) - temp.get(1,2));
            M.put(2,1,temp.get(2,0) - temp.get(2,2));
            M.put(0,2,shoot.get(0,0));
            M.put(1,2,shoot.get(0,1));
            M.put(2,2,shoot.get(0,2));
            M.print();
            M1.copy(M);
            M2.copy(M);
            M3.copy(M);

            M1.put(0,0,(vert.get(0,(int)Math.round(abc.get(i).get(0))-1)) - ev.get(0,0));
            M1.put(1,0,(vert.get(1,(int)Math.round(abc.get(i).get(0))-1)) - ev.get(0,1));
            M1.put(2,0,(vert.get(2,(int)Math.round(abc.get(i).get(0))-1)) - ev.get(0,2));
            M1.print();

            M2.put(0,1,(vert.get(0,(int)Math.round(abc.get(i).get(0))-1)) - ev.get(0,0));
            M2.put(1,1,(vert.get(1,(int)Math.round(abc.get(i).get(0))-1)) - ev.get(0,1));
            M2.put(2,1,(vert.get(2,(int)Math.round(abc.get(i).get(0))-1)) - ev.get(0,2));
            M2.print();

            M3.put(0,2,(vert.get(0,(int)Math.round(abc.get(i).get(0))-1)) - ev.get(0,0));
            M3.put(1,2,(vert.get(1,(int)Math.round(abc.get(i).get(0))-1)) - ev.get(0,1));
            M3.put(2,2,(vert.get(2,(int)Math.round(abc.get(i).get(0))-1)) - ev.get(0,2));
            break;



        }


    }


}

