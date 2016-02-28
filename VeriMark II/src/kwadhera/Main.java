package kwadhera;

import ij.IJ;
import ij.io.Opener;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {
        ArrayList<String> files = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            for (int j = 1; j < 9; j++) {
                String start = "";
                if (i == 1) {
                    start = "R";
                } else {
                    start = "L";
                }
                files.add(start + "Thumb" + j);
            }
        }
        while (true) {
            File folder = new File("C:\\Images");
            File[] listOfFiles = folder.listFiles();

            String newPrintFileName = "";
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile() && !(files.contains(listOfFiles[i].getName().split(".bmp")[0]))) {
                    newPrintFileName = listOfFiles[i].getName().split(".bmp")[0];
                    files.add(listOfFiles[i].getName().split(".bmp")[0]);
                }
            }
            if (!(newPrintFileName.equals(""))) {

                if ((compareFingerprints("RThumb1", newPrintFileName) || compareFingerprints("RThumb2", newPrintFileName) || compareFingerprints("RThumb3", newPrintFileName) || compareFingerprints("RThumb4", newPrintFileName) || compareFingerprints("RThumb5", newPrintFileName) || compareFingerprints("RThumb6", newPrintFileName) || compareFingerprints("RThumb7", newPrintFileName) || compareFingerprints("RThumb8", newPrintFileName))) {
                    JOptionPane.showMessageDialog(null, "Hello, Mr. R. Thumb! Your Fingerprint has been validated!", "Valid Print", JOptionPane.INFORMATION_MESSAGE);
                    sendWebDatabase("Mr R.Thumb");
                } else if ((compareFingerprints("LThumb1", newPrintFileName) || compareFingerprints("LThumb2", newPrintFileName) || compareFingerprints("LThumb3", newPrintFileName) || compareFingerprints("LThumb4", newPrintFileName) || compareFingerprints("LThumb5", newPrintFileName) || compareFingerprints("LThumb6", newPrintFileName) || compareFingerprints("LThumb7", newPrintFileName) || compareFingerprints("LThumb8", newPrintFileName))) {
                    JOptionPane.showMessageDialog(null, "Hello, Mr. L. Thumb! Your Fingerprint has been validated", "Valid Print", JOptionPane.INFORMATION_MESSAGE);
                    sendWebDatabase("Mr L.Thumb");
                } else {
                    JOptionPane.showMessageDialog(null, "Your Fingerprint does not exist in our records!", "Invalid Print", JOptionPane.INFORMATION_MESSAGE);
                    sendWebDatabase(null);
                }

            }
        }

    }

    //true if match, else false
    public static boolean compareFingerprints (String base, String check){
        Opener opener = new Opener();
        opener.open("C:\\Images\\"+base+".bmp");
        opener.open("C:\\Images\\"+check+".bmp");

        //Grab console output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        IJ.run("Extract SIFT Correspondences", "source_image="+base+".bmp target_image="+check+".bmp initial_gaussian_blur=1.60 steps_per_scale_octave=3 minimum_image_size=64 maximum_image_size=1024 feature_descriptor_size=4 feature_descriptor_orientation_bins=8 closest/next_closest_ratio=0.92 filter maximal_alignment_error=25 minimal_inlier_ratio=0.05 minimal_number_of_inliers=7 expected_transformation=Translation");

        System.out.flush();
        System.setOut(old);

        String[] stuff = (baos.toString().split("No"));
        System.out.println(stuff[stuff.length-1]);
        String[] stuff2 = stuff[stuff.length-1].split("found");

        if((stuff2[0].equals(" correspondences "))){
            System.out.println("Not equal");
            return false;
        }
        System.out.println("Seems equal");
        return true;
    }

    //Send Attempt to online Database to secure signatures
    public static void sendWebDatabase(String name) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String time = (dateFormat.format(date));
        String toSend = "";
        if(name == null)
        {
            toSend = "Someone failed authentication on " + time + " in Pasadena,CA";
        }
        else
        {
            toSend = name + " passed authentication on " + time + " in Pasadena,CA";
        }

        URL url = new URL("http://requestbinwin.herokuapp.com/v1ma8uv1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(toSend);
        wr.flush();
        InputStream is = conn.getInputStream();
        // Can verify and such in the future
    }

}
