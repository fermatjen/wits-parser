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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 *
 * @author FJ
 */
public class WITSProperties {

    public static String WITS_BrandName = "WITS [JSPWiki Parser] - fermatjen@yahoo.com";
    public static String WITS_VersionName = "V0.3.3_SVN";
    public static String WITS_AllienBrandMessage = "This is WITS - JSPWiki Parser. Your input file[s] may have alien wiki content. Use the correct WITS Parser or use --force option.";
    public static String WITS_MailingList = "fermatjen@yahoo.com";
    public static String WITS_UsageString1 = "\r\njava -jar WITS.jar <inputFile> or <inputDir>\r\n\r\nOther Options:\r\n\r\n[--docbook]    - Enable DocBook Output.\r\n[--test]    - Do not write output to file.\r\n[--force]    - Force conversion for alien wiki formats.\r\n[--outputdir <path>]    - Provide the path to output Dir.\r\n[--config <configFile>]    - Provide a configuration file.\r\n";
    public static String WITS_UsageString2 = "\r\nExample 1: Create SolBook Output.\r\n java -jar WITS.jar input1.txt\r\n java -jar WITS.jar inputdir\r\n java -jar WITS.jar input1.txt input2.txt input3.txt\r\n java -jar WITS.jar --config WITS.props input1.txt\r\n java -jar WITS.jar --config WITS.props --outputdir /MyDir input1.txt\r\n\r\nExample 2: Create DocBook Output.\r\n java -jar WITS.jar --docbook input1.txt\r\n java -jar WITS.jar --docbook inputdir\r\n java -jar WITS.jar --docbook input1.txt input2.txt input3.txt\r\n java -jar WITS.jar --docbook --config WITS.props input1.txt\r\n java -jar WITS.jar --docbook --config WITS.props --outputdir /MyDir input1.txt";

    public static String WITS_NLString = "\r\n";
    public static String WITS_ParseErrorMessage = "Parsing Error - Please check the error file witserror.txt. Send your source wiki file and witserror.txt file to: " + WITS_MailingList;
    public String WITS_BookTitle = "Enter your book title";
    public String WITS_ChapterTitle = "Enter your chapter title";
    public String WITS_ChapterHighlights = "Enter chapter highlights here";
    public String WITS_AuthorName = "Author";
    public String WITS_PubsNumber = "1234";
    public String WITS_ReleaseInfo = "1.0";
    public String WITS_PubDate = "2009";
    public String WITS_PubName = "Some Company Inc.";
    public String WITS_CopyrightYear = "2009";
    public String WITS_BookAbstract = "Enter abstract here";
    public String WITS_LegalNotice = "Copyright 2009 Some Company, Inc.</para><para>DOCUMENTATION IS PROVIDED &ldquo;AS IS&rdquo; AND ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE DISCLAIMED, EXCEPT TO THE EXTENT THAT SUCH DISCLAIMERS ARE HELD TO BE LEGALLY INVALID.";
    
    public String WITS_DanglerSectionSummary = "Enter section summary here";
    public String WITS_DanglerSectionTitle = "Enter section title here";
    public String WITS_DanglerTableTitle = "Enter table title here";
    public String WITS_WIKISiteBaseURL = "http//wikis.company.com/mywiki/";

    public  void initProperties(String configPath) {
        //if there is wits.config, read from it. should be called by WITSProcessor
        File f = new File(configPath);
        if (!f.exists()) {
            System.out.println("WITS Configuration File: " + configPath + " not found");
            System.exit(0);
        }

        BufferedReader in = null;
        String body = new String("<LB>\r\n");
        String str = new String();

        int lbTol = 0;
        try {
            in = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException ex) {
            ex.getMessage();
            System.exit(0);
        }
        try {
            HashMap <String, String> witsKeys = new HashMap <String, String> ();

            while ((str = in.readLine()) != null) {
                if (str.startsWith("WITS_") && str.indexOf("=") != -1) {
                    StringTokenizer stok = new StringTokenizer(str, "=");
                    String key = stok.nextToken().trim();
                    String value = stok.nextToken().trim();
                    witsKeys.put(key,value);
                    //System.out.println("Storing key:"+key+" value:"+value);
                }
            }

            //populate values
            if (witsKeys.get("WITS_BookTitle") != null) {
                WITS_BookTitle = (String) witsKeys.get("WITS_BookTitle");
            }

            if (witsKeys.get("WITS_ChapterTitle") != null) {
                WITS_ChapterTitle = (String) witsKeys.get("WITS_ChapterTitle");
            }
            if (witsKeys.get("WITS_ChapterHighlights") != null) {
                WITS_ChapterHighlights = (String) witsKeys.get("WITS_ChapterHighlights");
            }
            if (witsKeys.get("WITS_AuthorName") != null) {
                WITS_AuthorName = (String) witsKeys.get("WITS_AuthorName");
            }
            if (witsKeys.get("WITS_PubsNumber") != null) {
                WITS_PubsNumber = (String) witsKeys.get("WITS_PubsNumber");
            }
            if (witsKeys.get("WITS_ReleaseInfo") != null) {
                WITS_ReleaseInfo = (String) witsKeys.get("WITS_ReleaseInfo");
            }
            if (witsKeys.get("WITS_PubDate") != null) {
                WITS_PubDate = (String) witsKeys.get("WITS_PubDate");
            }
            if (witsKeys.get("WITS_PubName") != null) {
                WITS_PubName = (String) witsKeys.get("WITS_PubName");
            }
            if (witsKeys.get("WITS_CopyrightYear") != null) {
                WITS_CopyrightYear = (String) witsKeys.get("WITS_CopyrightYear");
            }
            if (witsKeys.get("WITS_BookAbstract") != null) {
                WITS_BookAbstract = (String) witsKeys.get("WITS_BookAbstract");
            }
            if (witsKeys.get("WITS_LegalNotice") != null) {
                WITS_LegalNotice = (String) witsKeys.get("WITS_LegalNotice");
            }
            if (witsKeys.get("WITS_DanglerSectionSummary") != null) {
                WITS_DanglerSectionSummary = (String) witsKeys.get("WITS_DanglerSectionSummary");
            }
            if (witsKeys.get("WITS_DanglerSectionTitle") != null) {
                WITS_DanglerSectionTitle = (String) witsKeys.get("WITS_DanglerSectionTitle");
            }
            if (witsKeys.get("WITS_DanglerTableTitle") != null) {
                WITS_DanglerTableTitle = (String) witsKeys.get("WITS_DanglerTableTitle");
            }
            if (witsKeys.get("WITS_WIKISiteBaseURL") != null) {
                WITS_WIKISiteBaseURL = (String) witsKeys.get("WITS_WIKISiteBaseURL");
            }



        } catch (IOException ex) {
            ex.getMessage();
            System.exit(0);
        }
    }
}
