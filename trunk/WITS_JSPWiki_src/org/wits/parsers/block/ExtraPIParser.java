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
public class ExtraPIParser implements WITSParser{

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
    public ExtraPIParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("ExtraPIIC", 0, "ExtraPIIC Invoked.");

        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n%%", offset);
            int r_loc = uncleanSGML.indexOf("%%", l_loc + 8);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            String PICandidate = uncleanSGML.substring(l_loc + 8, r_loc);
            //String PIToken = null;
            String PIValue = null;


            if (PICandidate.indexOf("<LB>") != -1) {
                //find the first LB
                int firstLB = PICandidate.indexOf("<LB>");
                PIValue = PICandidate.substring(firstLB, PICandidate.length());
            } else {
                //find the first whitespace
                int firstWP = PICandidate.indexOf(" ");
                PIValue = PICandidate.substring(firstWP, PICandidate.length());
            }

            _handle.append(uncleanSGML.substring(offset, l_loc + 6));
            _handle.append(PIValue);

            offset = r_loc+2;
        }
        uncleanSGML = _handle.toString();


        return uncleanSGML;
    }
}
