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
import org.wits.patterns.StringHandler;
import java.util.ArrayList;
import org.wits.parsers.WITSParser;

/**
 *
 * @author FJ
 */
public class ParaParser implements WITSParser{

    private String uncleanSGML = null;
    private ArrayList restrictedSection = new ArrayList();
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
    public ParaParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    //build the restricted section.

    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("ParaIC", 0, "ParaIC Invoked.");
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);

        StringBuilder _handle = new StringBuilder();
        int offset = 0;

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n", offset);
            int r_loc = uncleanSGML.indexOf("<LB>\r\n", l_loc + 1);
            //System.out.println("L_LOC:"+l_loc+" R_LOC:"+r_loc);
            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            //System.out.println("L_LOC:"+l_loc+" R_LOC:"+r_loc);
            String paraCandidate = uncleanSGML.substring(l_loc + 6, r_loc);

            //allow special cases here the line ends with <screen>
            //System.out.println("PC:"+paraCandidate);
            if (paraCandidate.endsWith("</noparse>") && paraCandidate.indexOf("<noparse>") != -1) {
                int npIndex = paraCandidate.indexOf("<noparse>");

                String correctedParaCandidate = paraCandidate.substring(0, npIndex);

                _handle.append(uncleanSGML.substring(offset, l_loc + 4));
                debugger.showDebugMessage("ParaIC", l_loc, "Adding para tags.");
                _handle.append("<para>");
                _handle.append(correctedParaCandidate);
                _handle.append("</para>");
                _handle.append(paraCandidate.substring(npIndex, paraCandidate.length()));
                offset = r_loc;
                continue;
            }

            if (paraCandidate.indexOf("caution>") != -1 || paraCandidate.indexOf("note>") != -1  || paraCandidate.indexOf("tip>") != -1 || paraCandidate.indexOf("<LB>") != -1 || paraCandidate.indexOf("noparse>") != -1 || paraCandidate.trim().equals("") || paraCandidate.indexOf("row>") != -1 || paraCandidate.indexOf("entry>") != -1 || paraCandidate.indexOf("listitem>") != -1 || paraCandidate.indexOf("<sect") != -1 || paraCandidate.indexOf("</sect") != -1 || paraCandidate.indexOf("<informaltable") != -1 || paraCandidate.indexOf("</informaltable") != -1 || paraCandidate.indexOf("itemizedlist>") != -1 || paraCandidate.indexOf("orderedlist>") != -1 || paraCandidate.indexOf("<LB>") != -1 || paraCandidate.startsWith("|") || paraCandidate.endsWith("|") || paraCandidate.indexOf("blockquote>") != -1) {
                //don't mess with the block
                //System.out.println("-----------------PC:" + paraCandidate);
                _handle.append(uncleanSGML.substring(offset, r_loc));
                offset = r_loc;
                continue;
            }
            //System.out.println("+++++++--------PC:"+paraCandidate);
            _handle.append(uncleanSGML.substring(offset, l_loc + 4));
            debugger.showDebugMessage("ParaIC", l_loc, "Adding para tags.");
            _handle.append("<para>");
            _handle.append(uncleanSGML.substring(l_loc + 4, r_loc));
            _handle.append("</para>");

            offset = r_loc;
        }
        debugger.showDebugMessage("ParaIC", uncleanSGML.length(), "Updating Tree...Done.");
        uncleanSGML = _handle.toString();

        
        return uncleanSGML;
    }
}
