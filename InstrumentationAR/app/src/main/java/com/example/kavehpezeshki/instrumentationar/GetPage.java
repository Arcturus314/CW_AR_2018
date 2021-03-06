/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.kavehpezeshki.instrumentationar;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author kaveh
 */
public class GetPage {

    public static String flightDataUrl = "http://134.173.61.245/flights2.txt";

    /*
    Returns the contents of the web page at url url as a String
    */
    public static String getWebPage(String urlToRead) throws Exception{
        String textFromWebpage = "";

        System.out.println("starting download from url " + urlToRead);
        URL url = new URL(urlToRead);

        BufferedReader s = new BufferedReader(
                new InputStreamReader(url.openStream()));

        String line = "";
        while ((line = s.readLine()) != null) {
            textFromWebpage += line + "\n";
        }
        s.close();
        return textFromWebpage;
    }

    /*
    Given a String representation of a flights webpage given in standard form, returns a String representation of the webpage formatted as follows:
    [
        [Flight, Alt, Speed, Heading, Lat, Long, Sig, Msgs],
        [Flight, Alt, Speed, Heading, Lat, Long, Sig, Msgs],
        ...
    ]

    Example input flight data:
Hex     Mode  Sqwk  Flight   Alt    Spd  Hdg    Lat      Long   Sig  Msgs   Ti/
-------------------------------------------------------------------------------
0D0A78  S     3310           35000  492  145   34.220 -117.774    6    89    2
A5C6A1  S     7322  UPS966    9575  294  308   34.113 -117.835    6   391    1
    */

    public static ArrayList<String[]> getFlights(String webPage) {
        String[] lines = webPage.split("\n");
        //System.out.println(lines);
        //String[][] planeData = new String[lines.length-2][8];
        ArrayList<String[]> planeData = new ArrayList<String[]>();
        //ignoring the first and second lines, creating a new string array for every line after the two headers
        //TODO: rewrite after we have a better idea of string parsing limits. Don't want to miss a digit if fields have different sizes than expected
        for(int i = 2; i < lines.length; i++) {
            System.out.println(lines[i]);
            String[] lineData = new String[8];
            lineData[0] = lines[i].substring(20,27);
            lineData[1] = lines[i].substring(27,35);
            lineData[2] = lines[i].substring(35,40);
            lineData[3] = lines[i].substring(40,45);
            lineData[4] = lines[i].substring(45,54);
            lineData[5] = lines[i].substring(54,63);
            lineData[6] = lines[i].substring(63,68);
            lineData[7] = lines[i].substring(68,74);

            boolean completePlaneData = true;
            //now strip spaces from each string
            for (int j = 0; j < lineData.length; j++) {
                lineData[j] = lineData[j].replaceAll(" ","");
                if (lineData[j].equals("")) {
                    completePlaneData = false;
                }
            }

            if (completePlaneData) {
                planeData.add(lineData);
            }
            //planeData[i-2] = lineData;
        }

        return planeData;
    }

}
