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

package org.wits.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 * @author FJ
 */
public class TestHandler {

    private String source = null;
    private LinkedHashMap resultMap = null;
    private String fileName = null;

    public TestHandler(String fileName, String source) {
        this.source = source;
        this.fileName = fileName;
        resultMap = new LinkedHashMap();
    }

    public void evaluateCases() {
        Iterator keys = resultMap.keySet().iterator();

        int passCount = 0;
        int failCount = 0;
        int totalCases = resultMap.size();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String result = (String) resultMap.get(key);
            if (result.equals("PASS")) {
                passCount++;
            } else {
                failCount++;
            }
        }
        int passP = (passCount * 100 / totalCases);
        int failP = (failCount * 100 / totalCases);

        System.out.println("");
        System.out.println("    Pass % " + passP);
        System.out.println("    Fail % " + failP);

    }

    public void runTestCases() {

        //run XML Validity Case
        XMLValidityCase xmlCase = new XMLValidityCase();
        xmlCase.initCase(source);
        String result = xmlCase.runCase();
        resultMap.put("XML Validity [Fatal]", result);

        //run Wiki LeftOver Case
        WikiLeftOverCase wikiCase = new WikiLeftOverCase();
        wikiCase.initCase(source);
        result = wikiCase.runCase();
        resultMap.put("Wiki LeftOver [Warning]", result);

        //run Wiki Default Content Case
        DefaultContentCase dcontCase = new DefaultContentCase();
        dcontCase.initCase(source);
        result = dcontCase.runCase();
        resultMap.put("Default Content [Warning]", result);

        //run WITS Internal Data Case
        WITSInternalDataCase widCase = new WITSInternalDataCase();
        widCase.initCase(source);
        result = widCase.runCase();
        resultMap.put("Internal Content [Warning]", result);


    }

    public void displayResults() {

        Iterator keys = resultMap.keySet().iterator();
        System.out.println("\r\nRunning WITS cases on " + fileName + "\r\n");

        int dLength = 35;

        while (keys.hasNext()) {
            String key = (String) keys.next();
            int pLength = dLength - key.length();

            System.out.print("   " + key);

            for (int i = 0; i < pLength; i++) {
                System.out.print("-");
            }

            System.out.println(resultMap.get(key));
        }

        evaluateCases();
    }
}
