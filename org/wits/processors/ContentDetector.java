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

import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class ContentDetector {

    private String cleanSGML = null;

    public ContentDetector(String cleanSGML) {
        this.cleanSGML = cleanSGML;
        //System.out.println("-----------"+cleanSGML);
    }

    public String getChapterTitle() {

        //Find the first section title and use it for chapter title.
        
        int offset = 0;
        String titleString = new String("Enter title here");

        while (true) {
            int l_loc = cleanSGML.indexOf("<title>", offset);
            int r_loc = cleanSGML.indexOf("</title>", l_loc);            

            if (l_loc == -1 || r_loc == -1) {                
                break;
            }
            
            String ptitleString = (cleanSGML.substring(l_loc+7, r_loc)).trim();
            
            if(ptitleString.indexOf("Enter") != -1 && ptitleString.indexOf("title") != -1){
                offset = r_loc;
                continue;
            }
            
           titleString = ptitleString;           
            break;            
        }        
        
        return titleString;
    }
    
    public String replace(String source, String pattern, String replace, int startAt) {

        if (source != null) {
            final int len = pattern.length();
            StringBuilder sb = new StringBuilder();
            int found = -1;
            int start = startAt;

            while ((found = source.indexOf(pattern, start)) != -1) {

                sb.append(source.substring(start, found));
                sb.append(replace);
                start = found + len;
            }

            sb.append(source.substring(start));
            //debugger.showDebugMessage("PatternMatchingIC", startAt, "Pattern Matching...Done");


            return sb.toString();
        } else {
            return "";
        }
    }
}
