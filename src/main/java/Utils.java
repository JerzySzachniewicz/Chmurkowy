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
    private static HashMap<String,String> translations;

    static {
        preferSpecificCharactersMap();
        preperTranslationsMap();
    }

    private static void preperTranslationsMap() {
        try {
            FileReader fileReader = new FileReader("/home/oem/test/Res/slownik.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            translations = new HashMap<>();
            while((line = bufferedReader.readLine()) != null) {
                String[] string = line.split(",");
                translations.put(string[0],string[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void preferSpecificCharactersMap() {
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
//        System.out.println(world + " " + translations.containsKey(world));
        if (translations.containsKey(world)) {
            for (COSString cosString : worldLetters) {
                cosString.setValue("".getBytes());
            }
            worldLetters.getFirst().setValue(translations.get(world).getBytes());
        }
    }
}
