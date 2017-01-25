/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package watchdir;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author hbakiewicz
 */
public class parseEdi {

    String edi_file;

    parseEdi(String  pathToEdi) {
        this.edi_file = pathToEdi;
    }

    public String read() throws UnsupportedEncodingException, FileNotFoundException, IOException {
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(this.edi_file), "Windows-1250"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                if (line.contains("NrDok:")) {
                    String[] b = line.split(":");
                    return b[1];
                }
                line = br.readLine();
            }
            String everything = sb.toString();
        }
        return "@empty";
    }

}
