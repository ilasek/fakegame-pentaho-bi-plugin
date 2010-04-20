package cz.ilasek.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Trida obsahuje nekolik uzitecnych metod pro praci se soubory.
 * 
 * @author Ivo lasek
 *
 */
public class FileUtil {
    public static final int BUFFER_SIZE = 512;

    /**
     * Nacte cely obsah souboru do retezce.
     * 
     * @param file Soubor, ktery se ma nacist.
     * @return Obsah ancteneho souboru.
     */
    public static String readFileIntoString(File file)
    {
        StringBuilder fileContent = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            
            byte [] buffer = new byte[BUFFER_SIZE];
            int length = 0;
            while ((length = fis.read(buffer)) > 0)
            {
                fileContent.append(new String(buffer, 0, length)); 
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  
        
        return fileContent.toString();
    }

    /**
     * Pripoji obsah jednoho souboru na konec druheho.
     * 
     * @param targetFile Cilovy soubor.
     * @param sourceFile Zdrojovy (pripojovany) soubor.
     * @throws IOException
     */
    public static void appendToFile(File targetFile, File sourceFile) throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(targetFile, true));
        BufferedReader in = new BufferedReader(new FileReader(sourceFile));
        
        char [] buffer = new char[BUFFER_SIZE];
        int length = 0;
        while ((length = in.read(buffer)) > 0)
        {
            out.append(new String(buffer, 0, length));
        }
        
        in.close();
        out.close();
    }
    
    /**
     * Vymaze obsah souboru.
     * 
     * @param file Soubor, jehoz obsah ma byt smazan.
     * @throws IOException
     */
    public static void clearFile(File file) throws IOException
    {
        FileOutputStream erasor = new FileOutputStream(file);
        erasor.write((new String()).getBytes());
        erasor.close();
    }

}
