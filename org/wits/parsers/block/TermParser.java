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

/**
 *
 * @author FJ
 */
public class TermParser implements WITSParser{

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
    public TermParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("TermIC", 0, "TermIC Invoked.");

        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n;", offset);
            int r_loc = uncleanSGML.indexOf(":", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            //handle term.
            String bqCandidate = uncleanSGML.substring(l_loc + 7, r_loc);
            if (bqCandidate.trim().equals("")) {
                bqCandidate = "Comment";
            }
            //System.out.println("BQ:"+bqCandidate);
            _handle.append(uncleanSGML.substring(offset, l_loc + 6));

            //solbook says emphasis can't have literals.
            if (bqCandidate.indexOf("noparsi>") == -1) {
                _handle.append("<emphasis role=\"strong\">");
                _handle.append(bqCandidate + " - ");
                _handle.append("</emphasis>");
            }
            else{
                _handle.append(bqCandidate + " - ");
            }

            offset = r_loc + 1;
        }
        uncleanSGML = _handle.toString();


        return uncleanSGML;
    }
}
