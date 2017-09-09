package main.java;

import org.apache.pdfbox.cos.COSString;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

import static java.lang.System.in;

/**
 * Created by oem on 9/9/17.
 */
public class Utils {
    private static HashMap<String,String> specialCharacters;
    static {
        try {
            FileReader fileReader = new FileReader("/home/oem/test/Res/slowka.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            specialCharacters = new HashMap<>();
            while((line = bufferedReader.readLine()) != null) {
                String[] string = line.split(" ");
                specialCharacters.put(string[0],string[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String changeSpecialCharacter(String asciiHex) {
        if (specialCharacters.containsKey(asciiHex)) {
            return specialCharacters.get(asciiHex);
        } else {
            return "";
        }
    }

    public static void translate(String world, LinkedList<COSString> worldLetters) {
        if (world.equals("blacha")) {
            for (COSString cosString : worldLetters) {
                cosString.setValue("".getBytes());
            }
            worldLetters.getFirst().setValue("DUPADUPA".getBytes());
        }
    }
}
