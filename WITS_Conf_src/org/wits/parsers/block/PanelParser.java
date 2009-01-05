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
public class PanelParser implements WITSParser{

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
    public PanelParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("PanelIC", 0, "PanelIC Invoked.");

        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("{panel:", offset);
            int r_loc = uncleanSGML.indexOf("{panel}", l_loc + 1);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            int ll_loc = uncleanSGML.indexOf("}", l_loc);

            //handle panel block
            String bqCandidate = uncleanSGML.substring(ll_loc + 1, r_loc);
            //System.out.println("ColorQ:" + bqCandidate);
            _handle.append(uncleanSGML.substring(offset, l_loc));
            _handle.append(bqCandidate);

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);
        uncleanSGML = handler.replace(uncleanSGML, "{panel}", "");

        return uncleanSGML;
    }
}
