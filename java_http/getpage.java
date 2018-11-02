import java.net.*;
import java.io.*;

public class getpage {

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
            textFromWebpage += line;
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
    */
    /* CURRENTLY NONFUNCTIONAL
    public String[][] getFlights(String webPage) {
        String[] lines = webpage.split("\n");
        //ignoring the first and second lines, creating a new string array for every line after the two headers
        for(int i = 2, i < lines.length(), i++ {

        }
    }
    */

    public static void main(String args[]) throws Exception{
        String flightWebpageText = getWebPage(flightDataUrl);

        System.out.println("\nFull Webpage\nxxxxxxxxxxxxxxxxx\n"+flightWebpageText+"\nxxxxxxxxxxxxxxxxx");
    }
}