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

package org.wits.parsers.block;

import org.wits.debugger.WITSDebugger;
import org.wits.parsers.WITSParser;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class BlockQuoteParser implements WITSParser{

    private String uncleanSGML = null;
    private WITSDebugger debugger = null;

    /**
     *
     * @param debugger
     */
    public void setDebugger(WITSDebugger debugger) {
        this.debugger = debugger;
    }

    /**
     *
     * @param uncleanSGML
     */
    public BlockQuoteParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("QuoteIC", 0, "QuoteIC Invoked.");

        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\nbq.", offset);
            int r_loc = uncleanSGML.indexOf("<LB>\r\n<LB>", l_loc+1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            
            //handle block quote.
           String bqCandidate = uncleanSGML.substring(l_loc+9,r_loc);
           //System.out.println("BQ:"+bqCandidate);
           _handle.append(uncleanSGML.substring(offset,l_loc+6));
           _handle.append("<blockquote><para>");
           _handle.append(bqCandidate);
           _handle.append("</para></blockquote>");
           
           offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        
        //handle {quote} section
        _handle = new StringBuilder();
        offset = 0;
        while (true) {
            int l_loc = uncleanSGML.indexOf("{quote}", offset);
            int r_loc = uncleanSGML.indexOf("{quote}", l_loc+1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            
            //handle block quote.
           String bqCandidate = uncleanSGML.substring(l_loc+13,r_loc-6);
           //System.out.println("BQQQQ:"+bqCandidate);
           _handle.append(uncleanSGML.substring(offset,l_loc+7));
           _handle.append("<blockquote><para>");
           _handle.append(bqCandidate);
           _handle.append("</para></blockquote>");
           
           offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);
        uncleanSGML = handler.replace(uncleanSGML, "{quote}", "");
        
        return uncleanSGML;
    }
}
