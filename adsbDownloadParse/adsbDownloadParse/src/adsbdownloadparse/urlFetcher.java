/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adsbdownloadparse;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 * @author kaveh
 */
public class urlFetcher {
    private URL urlToFetch;
    
    public urlFetcher (String url) throws MalformedURLException {
        urlToFetch = new URL(url);
    }
    
    public String fetchURLData() throws IOException {
        URLConnection connection = urlToFetch.openConnection();
        InputStream input = connection.getInputStream();
        Scanner s = new Scanner(input).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        return result;
    }    
    
}
