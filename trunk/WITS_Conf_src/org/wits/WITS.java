/******************************************************************************
 *
 *
 * WITS - Wiki to Structured Markup Converter.
 *
 * Copyright (C) 2009 by Frank Jennings (fermatjen@yahoo.com).
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation under the terms of the GNU General Public License is hereby
 * granted. No representations are made about the suitability of this software
 * for any purpose. It is provided "as is" without express or implied warranty.
 * See the GNU General Public License for more details.
 *
 * Documents produced by WITS converter are derivative works derived from the
 * input used in their production; they are not affected by this license.
 *
 */
package org.wits;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.wits.debugger.WITSDebugger;
import org.wits.test.TestHandler;
import org.wits.writer.solbookwriter.SolChapterWriter;
import org.wits.writer.WITSFileWriter;
import org.wits.writer.docbookwriter.DocChapterWriter;

/**
 *
 * @author FJ
 */
public class WITS {

    private static boolean isDebuggingOn = false;
    private static String inputFile = "input.txt";
    private static ArrayList<String> inputFiles = null;
    private static String outputFile = ".";
    private static boolean singleInputFile = true;
    private static boolean isNullOutput = false;
    private static boolean isForceParsing = false;
    //There could be diff. brands
    //Passing brand is mandatory
    private static String configPath = null;
    private static WITSProperties props = null;
    private static boolean outputDir = false;

    private static String getOutputFile(String ext) {
        if (!singleInputFile) {
            if (ext.equals("book")) {
                return "WITS" + "." + ext;
            }
        }
        if (outputDir) {
            if (ext.equals("book")) {
                return "WITS" + "." + ext;
            }
        }
        File _file = new File(inputFile);
        String fName = _file.getName();
        int loc = fName.indexOf(".");

        String ofName = "";
        if (loc != -1) {
            ofName = fName.substring(0, loc);
            return ofName + "." + ext;
        }
        return fName + "." + ext;
    }

    private static void printUsage() {

        String WITS_UsageString1 = WITSProperties.WITS_UsageString1;
        String WITS_UsageString2 = WITSProperties.WITS_UsageString2;
        System.out.println("\r\nUsage:");
        System.out.println(WITS_UsageString1);
        System.out.println(WITS_UsageString2);
        System.out.println("");
        System.exit(0);
    }

    /**
     *
     * @param ar
     */
    public static void main(String ar[]) {

        //Read WITS Global Properties
        //Init Properties
        props = new WITSProperties();
        inputFiles = new ArrayList<String>();

        String WITS_BrandName = WITSProperties.WITS_BrandName;
        String WITS_VersionName = WITSProperties.WITS_VersionName;

        String WITS_NLString = WITSProperties.WITS_NLString;
        //Check docbookv5 output
        boolean isDocBookOutput = false;

        int arsLength = ar.length;

        System.out.println(WITS_NLString + WITS_BrandName + " " + WITS_VersionName);

        if (arsLength < 1) {
            printUsage();
        }

        if (arsLength == 1) {
            if (ar[0].equalsIgnoreCase("--outputdir") || ar[0].equalsIgnoreCase("--solbook") || ar[0].equalsIgnoreCase("--docbook") || ar[0].equalsIgnoreCase("--test") || ar[0].equalsIgnoreCase("--force") || ar[0].equalsIgnoreCase("--config")) {
                printUsage();
            }
        }

        System.out.println("Applying Wiki Filters...");
        WITSProcessor processor = null;

        double totalTimeTaken = 0;
        String bookContent = new String();
        int witsID = 0;

        //Check for null output

        for (int i = 0; i < arsLength; i++) {
            if (ar[i].equalsIgnoreCase("--test")) {
                isNullOutput = true;
                System.out.println("Output stream disabled for testing...");
                isDocBookOutput = true;
                break;
            }
        }
        //Check for force parsing

        for (int i = 0; i < arsLength; i++) {
            if (ar[i].equalsIgnoreCase("--force")) {
                isForceParsing = true;
                System.out.println("Force parsing...ON");
                break;
            }
        }



        for (int i = 0; i < arsLength; i++) {
            if (ar[i].equalsIgnoreCase("--docbook")) {
                isDocBookOutput = true;
                System.out.println("DocBook Output...Enabled");
                break;
            }
        }

        for (int i = 0; i < arsLength; i++) {
            if (ar[i].equalsIgnoreCase("--solbook")) {
                isDocBookOutput = false;
                System.out.println("SolBook Output...Enabled");
                break;
            }
        }

        //Check for config file

        for (int i = 0; i < arsLength; i++) {
            if (ar[i].equalsIgnoreCase("--config")) {
                if (i == arsLength) {
                    printUsage();
                }
                configPath = ar[i + 1];
                //Read WITS Global Properties
                props.initProperties(configPath);
                System.out.println("Reading WITS Config file: " + configPath);
                break;
            }
        }

        //Check for output path

        for (int i = 0; i < arsLength; i++) {
            if (ar[i].equalsIgnoreCase("--outputdir")) {
                if (i == arsLength) {
                    printUsage();
                }

                outputDir = true;

                File _f = new File(ar[i + 1]);
                _f.mkdirs();

                outputFile = _f.toString();
                break;
            }
        }

        //Find actual arg length
        int aLength = 0;

        for (int i = 0; i < arsLength; i++) {
            //System.out.println("Checking ARG:"+ar[i]);
            if (ar[i].equalsIgnoreCase("--solbook") || ar[i].equalsIgnoreCase("--docbook") || ar[i].equalsIgnoreCase("--force") || ar[i].equalsIgnoreCase("--test")) {
                continue;
            }
            if (ar[i].equalsIgnoreCase("--config")) {
                i++;
                continue;
            }
            if (ar[i].equalsIgnoreCase("--outputdir")) {
                i++;
                continue;
            }
            aLength++;
        }

        //System.out.println("Total Input Files:"+aLength);

        //Build the Input Files ArrayList
        for (int i = 0; i < arsLength; i++) {
            //check sub options

            if (ar[i].equalsIgnoreCase("--solbook") || ar[i].equalsIgnoreCase("--docbook") || ar[i].equalsIgnoreCase("--test") || ar[i].equalsIgnoreCase("--force")) {
                continue;
            }
            if (ar[i].equalsIgnoreCase("--config")) {
                i++;
                continue;
            }
            if (ar[i].equalsIgnoreCase("--outputdir")) {
                i++;
                continue;
            }

            //Handle dir here
            File temp = new File(ar[i]);
            if (!temp.exists()) {
                System.out.println("File not found: " + temp.getName());
                continue;
            }
            if (temp.isDirectory()) {
                System.out.println("Reading Dir. Content: " + temp.getName());
                buildDirContent(temp.getAbsolutePath());
            } else {
                //This could be an input file entry
                if (ar[i].endsWith(".txt") || ar[i].endsWith(".TXT")) {
                    System.out.println("Adding " + temp.getAbsolutePath() + "");
                    inputFiles.add(temp.getAbsolutePath());
                } else {
                    System.out.println("Ignoring " + temp.getAbsolutePath() + "");
                }
            }
        }

        //Fixed the code to directly read from the ArrayList
        if (inputFiles.size() > 1) {
            singleInputFile = false;
        }

        int totalErrors = 0;
        int totalWarnings = 0;

        try {
            System.out.println("\r\nTo Parse: " + inputFiles.size() + " files.");

            for (int i = 0; i < inputFiles.size(); i++) {
                inputFile = (String) inputFiles.get(i);

                System.out.println("\r\nReading..." + inputFile);

                processor = new WITSProcessor(isDocBookOutput, isForceParsing, isDebuggingOn, inputFile, outputFile, props);
                processor.setWitsID(witsID);


                //handle parsing error
                String cleanSGML = null;
                try {
                    cleanSGML = processor.process();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    writeParserErrorOutput(ex.getClass().getName() + "\r\n" + ex.getMessage());
                    System.out.println(WITSProperties.WITS_ParseErrorMessage);
                    System.exit(0);
                }
                long end = System.currentTimeMillis();

                witsID = processor.getWitsID();



                //check alien formats
                if (cleanSGML.startsWith("ERROR:")) {
                    System.out.println(cleanSGML.substring(6, cleanSGML.length()));
                    System.exit(0);
                }


                if (!isDocBookOutput) {
                    System.out.println("Building SolBook Content...");
                    SolChapterWriter cWriter = new SolChapterWriter(cleanSGML, props);
                    String chapterBody = cWriter.getPartialChapterBody();
                    bookContent = bookContent + chapterBody;
                } else {
                    System.out.println("Building DocBook Content...");
                    DocChapterWriter cWriter = new DocChapterWriter(cleanSGML, props);
                    String chapterBody = cWriter.getPartialChapterBody();
                    bookContent = bookContent + chapterBody;
                }

                //get debugger context
                WITSDebugger debugger = processor.getDebugger();
                String debugString = debugger.getDebugString();
                //System.out.println("DEBUG STRING:"+debugString);


                if (!isNullOutput) {
                    File chapterPath = null;

                    if (!isDocBookOutput) {
                        chapterPath = new File(outputFile, getOutputFile("sgm"));
                    } else {
                        chapterPath = new File(outputFile, getOutputFile("xml"));
                    }
                    //write individual sgml files
                    WITSFileWriter fileWriter = new WITSFileWriter(isDocBookOutput, chapterPath, null, null, props);
                    //fileWriter.writeFile(cleanSGML, debugString);
                    fileWriter.writeChapterOutput(cleanSGML);
                } else {
                    //run cases
                    String header1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<chapter version=\"5.0\"\r\n    xmlns=\"http://docbook.org/ns/docbook\"\r\n    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\r\n    xmlns:xi=\"http://www.w3.org/2001/XInclude\"\r\n    xmlns:svg=\"http://www.w3.org/2000/svg\"\r\n    xmlns:m=\"http://www.w3.org/1998/Math/MathML\"\r\n    xmlns:html=\"http://www.w3.org/1999/xhtml\"\r\n    xmlns:db=\"http://docbook.org/ns/docbook\">\r\n" + cleanSGML + "</chapter>";

                    TestHandler handler = new TestHandler(inputFile, header1);
                    handler.runTestCases();
                    handler.displayResults();
                    totalErrors += handler.getErrorCount();
                    totalWarnings += handler.getWarningCount();
                }

            }

            System.out.println("\r\nTotal Errors   : " + totalErrors);
            System.out.println("Total Warnings : " + totalWarnings);

            if (inputFiles.size() > 0) {
                if (!isNullOutput) {
                    //write the book file now
                    File bookPath = null;

                    if (!isDocBookOutput) {
                        bookPath = new File(outputFile, getOutputFile("book"));
                    } else {
                        bookPath = new File(outputFile, getOutputFile("book.xml"));
                    }
                    WITSFileWriter fileWriter = new WITSFileWriter(isDocBookOutput, null, bookPath, null, props);

                    fileWriter.writeBookOutput(bookContent);
                }
            }

            //System.out.println("Completed in: " + totalTimeTaken + " secs.");
            if (!isNullOutput) {
                //System.out.println("Note - Some placeholder strings were added during conversion. Replace them with correct text before publishing.");
            }


        } catch (Exception ex) {
            //WITSDebugger debugger = processor.getDebugger();
            //String debugString = debugger.getDebugString();
            // File debugPath = new File(outputFile, "WITS.log");
            //WITSFileWriter fileWriter = new WITSFileWriter(null, null, debugPath);
            //fileWriter.writeErrorInfo(debugString, ex.toString());
            //System.out.println("Error while parsing the wiki text.");
            //System.out.println("Cause: " + ex.toString());
            writeParserErrorOutput(ex.getMessage());
            System.out.println(WITSProperties.WITS_ParseErrorMessage);
            ex.printStackTrace();
            System.exit(0);
        }

    }

    private static void writeParserErrorOutput(String toString) {
        FileWriter writer;
        try {
            writer = new FileWriter("witserror.txt");
            writer.write(toString);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            System.out.println("Error writing to the error file.");
            System.exit(0);
        }
    }

    private static void buildDirContent(String parent) {

        File temp = new File(parent);
        String children[] = temp.list();

        for (int i = 0; i < children.length; i++) {
            File temp2 = new File(temp, children[i]);

            if (temp2.isDirectory()) {
                buildDirContent(temp2.getAbsolutePath());
            } else {
                //already a file
                if (children[i].endsWith(".txt") || children[i].endsWith(".TXT")) {
                    System.out.println("Adding " + temp2.getAbsolutePath() + "");
                    inputFiles.add(temp2.getAbsolutePath());
                } else {
                    System.out.println("Ignoring " + temp2.getAbsolutePath() + "");
                }

            }
        }

    }
}
