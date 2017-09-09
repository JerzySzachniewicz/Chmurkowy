package main.java;

import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by TroskliwyMis on 2017-09-08.
 */
public class Start {

    public static void main(String[] args) throws IOException {
        String fileName = "";
        File folder = new File("/home/oem/test/");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
                translateFile(listOfFiles[i].getName());
            }
        }
    }

    private static void translateFile(String fileName) throws IOException {
        File file = new File("/home/oem/test/"+fileName);
        PDDocument document =  replaceText(PDDocument.load(file),"Cecha", "TSET");
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
//        System.out.print(text);
        document.save("/home/oem/test/Translations/"+fileName);
        document.close();
    }

    public static PDDocument replaceText(PDDocument document, String searchString, String replacement) throws IOException {
        PDPageTree pages = document.getDocumentCatalog().getPages();
        String world = "";
        LinkedList<COSString> worldLetters = new LinkedList<>();
        for (PDPage page : pages) {
            PDFStreamParser parser = new PDFStreamParser(page);
            parser.parse();
            List tokens = parser.getTokens();
            for (int j = 0; j < tokens.size(); j++) {
                Object next = tokens.get(j);
                if (next instanceof Operator) {
                    Operator op = (Operator) next;
                    //Tj and TJ are the two operators that display strings in a PDF
                    if (op.getName().equals("Tj")) {
                        // Tj takes one operator and that is the string to display so lets update that operator
                        COSString previous = (COSString) tokens.get(j - 1);
                        String string = previous.getString();
                        if (string.isEmpty()) {
                            continue;
                        }
                        String asciiHex = previous.toHexString();
                        if (asciiHex.length()>2) {
                            String replace = Utils.changeSpecialCharacter(asciiHex);
                            if (!replace.isEmpty()) {
                                string = replace;
                            }
                        }
                        if (Character.isUpperCase(string.charAt(0)) || Character.isSpaceChar(string.charAt(0))) {
                            Utils.translate(world, worldLetters);
                            world = "";
                            worldLetters.clear();
                            if (Character.isUpperCase(string.charAt(0))) {
                                world += string.toLowerCase();
                                worldLetters.add(previous);
                            }
                        }
                        if (Character.isLowerCase(string.charAt(0))) {
                            world += string;
                            worldLetters.add(previous);
                        }
                    } else if (op.getName().equals("TJ")) {
                        COSArray previous = (COSArray) tokens.get(j - 1);
                        for (int k = 0; k < previous.size(); k++) {
                            Object arrElement = previous.getObject(k);
                            if (arrElement instanceof COSString) {
                                COSString cosString = (COSString) arrElement;
                                String string = cosString.getString();
                                string = string.replaceAll(searchString, replacement);
                                cosString.setValue(string.getBytes());
                            }
                        }
                    }
                }
            }
            // now that the tokens are updated we will replace the page content stream.
            PDStream updatedStream = new PDStream(document);
            OutputStream out = updatedStream.createOutputStream();
            ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
            tokenWriter.writeTokens(tokens);
            page.setContents(updatedStream);
            out.close();
        }
        return document;
    }
}
