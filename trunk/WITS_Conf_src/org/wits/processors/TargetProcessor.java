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
package org.wits.processors;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author FJ
 */
public class TargetProcessor {

    private String inputText = null;
    private String targetAttrs = null;
    private ArrayList<String> sourceTargetAttrs = null;
    private ArrayList<String> matchTargetAttrs = null;

    public TargetProcessor(String inputText, String targetAttrs) {
        this.inputText = inputText;
        this.targetAttrs = targetAttrs;
        matchTargetAttrs = new ArrayList<String>();
        sourceTargetAttrs = new ArrayList<String>();
    }

    public void buildMatchTargetAttrs() {
        if (targetAttrs.indexOf(",") == -1) {
            //single attr
            //System.out.println("MATCHTRGT:"+targetAttrs);
            matchTargetAttrs.add(targetAttrs);
        } else {
            //multiple targets
            StringTokenizer stok = new StringTokenizer(targetAttrs, ",");

            while (stok.hasMoreTokens()) {
                String matchAttr = stok.nextToken();
                //System.out.println("MATCHTRGT:"+matchAttr);
                matchTargetAttrs.add(matchAttr);
            }
        }
    }

    public void buildSourceTargetAttrs(String stargetAttrs) {
        //Flush the source targets.
        sourceTargetAttrs.clear();
        
        if (stargetAttrs.indexOf(",") == -1) {
            //single attr
            //System.out.println("SRCTRGT:"+stargetAttrs);
            sourceTargetAttrs.add(stargetAttrs);
        } else {
            //multiple targets
            StringTokenizer stok = new StringTokenizer(stargetAttrs, ",");

            while (stok.hasMoreTokens()) {
                String sourceAttr = stok.nextToken();
                //System.out.println("SRCTRGT:"+sourceAttr);
                sourceTargetAttrs.add(sourceAttr);
            }
        }
    }

    public String getOutputText() {

        //First build the target list
        buildMatchTargetAttrs();

        //System.out.println("----------------------\r\n"+inputText+"\r\n------------------");

        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        int matchCount = 0;
        
        while (true) {
            int l_loc = inputText.indexOf("<WITSTarget id=", offset);
            int r_loc = inputText.indexOf("</WITSTarget>", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(inputText.substring(offset, inputText.length()));
                break;
            }

            boolean isBlockVisible = false;

            String targetSnippet = inputText.substring(l_loc,r_loc);
            //Check if sections are in the snippet
            if(targetSnippet.indexOf("</sect") != -1 || targetSnippet.indexOf("<sect") != -1){
                System.out.println("   FATAL ERROR: Sections inside the WITS Target blocks!\r\n");
                System.out.println("PRINTING OFFENDING BLOCK------------------------------------------------");
                System.out.println(targetSnippet);
                System.out.println("END OF OFFENDING BLOCK--------------------------------------------------");
                System.exit(0);
            }

            int ll_loc = inputText.indexOf(">", l_loc);

            String sTargetAttrs = inputText.substring(l_loc + 16, ll_loc - 1);

            //System.out.println("MATCH TARGET ATTRS:"+sTargetAttrs);
            //System.out.println("SOURCE TARGET ATTRS:"+targetAttrs);

            buildSourceTargetAttrs(sTargetAttrs);

            //check if any of the match target attr matches with
            //the source target attrs            

            for (int i = 0; i < sourceTargetAttrs.size(); i++) {
                //even if atleast one source target matches the pattern, display it
                String sourceAttr = sourceTargetAttrs.get(i);

                if (matchTargetAttrs.contains(sourceAttr)) {
                    //System.out.println("M....."+matchTargetAttrs.toString());
                    //System.out.println("S....."+sourceTargetAttrs.toString());
                    //display the snippet.                    
                    //System.out.println("   Target Match..."+sourceAttr);
                    isBlockVisible = true;
                    break;
                }
            }

            //override for all target
            if(targetAttrs.equals("all")){                
                isBlockVisible = true;
            }

            if (isBlockVisible) {
                matchCount++;
                _handle.append(inputText.substring(offset, l_loc));
                _handle.append(inputText.substring(ll_loc+1, r_loc));
                offset = r_loc + 13;
            }
            else{
                //No match targets match the source target.
                //Hide the text
                _handle.append(inputText.substring(offset, l_loc));
                offset = r_loc + 13;
            }

        }

        System.out.println("   WITS Targets [Match]: "+matchCount);
        return _handle.toString();
    }
}
