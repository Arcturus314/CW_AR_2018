/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adsbdownloadparse;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author kaveh
 */
public class AdsbDownloadParse {

    /**
     * @param args the command line arguments
     */
    
    static String urlToFetch = "http://134.173.61.245/flights_parsed.txt";
    public static void main(String[] args) throws MalformedURLException, IOException {
        // Simple demo of downloading and printing a file from a given URL
        System.out.println("Fetching data...");
        fetchData();
        
    }
    
    public static void fetchData() throws MalformedURLException, IOException {
        urlFetcher fetcher = new urlFetcher(urlToFetch);
        System.out.println(fetcher.fetchURLData());
    }
    
}
