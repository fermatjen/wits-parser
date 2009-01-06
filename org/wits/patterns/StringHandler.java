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

package org.wits.patterns;

import org.wits.debugger.WITSDebugger;
import org.wits.*;

/**
 *
 * @author FJ
 */
public class StringHandler {
    private WITSDebugger debugger = null;
    
    /**
     *
     * @param debugger
     */
    public void setDebugger(WITSDebugger debugger){
        this.debugger = debugger;
    }

    /**
     *
     */
    public StringHandler() {
    }
    
    /**
     *
     * @param source
     * @param pattern
     * @param replace
     * @return
     */
    public String replace(String source, String pattern, String replace) {
        return replace(source,pattern,replace,0);
    }

    /**
     *
     * @param source
     * @param pattern
     * @param replace
     * @param startAt
     * @return
     */
    public String replace(String source, String pattern, String replace, int startAt) {
        
        //debugger.showDebugMessage("PatternMatchingIC", startAt, "Pattern Matching...In progress");
        if(pattern.endsWith("\r\n")){            
            debugger.showDebugMessage("PatternMatchingIC", startAt, "Searching pattern..."+pattern.substring(0, pattern.length()-2));
        }
        else if(pattern.startsWith("\r\n")){
            debugger.showDebugMessage("PatternMatchingIC", startAt, "Searching pattern..."+pattern.substring(2, pattern.length()));
        }else if(pattern.indexOf("\r\n") != -1){           
            debugger.showDebugMessage("PatternMatchingIC", startAt, "Searching pattern..."+pattern.substring(0,2)+"...");
        }
        else{
            debugger.showDebugMessage("PatternMatchingIC", startAt, "Searching pattern..."+pattern);
        }
       
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
