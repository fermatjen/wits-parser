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

import org.wits.debugger.WITSDebugger;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class PreProcessor {

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
    public PreProcessor(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }

    private String handleOuterSectContainer(String textBlock) {
        //System.out.println("TBLOC:" + textBlock);
        boolean handleOrphan = true;

        int r_loc = textBlock.indexOf("!!!");
        if (r_loc == -1) {
            //no h1 at all. Let the post processor handle.
            return textBlock;
        }
        String orphanText = textBlock.substring(0, r_loc);
        String trimmedOrphanText = orphanText;

        trimmedOrphanText = orphanText.trim();
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);
        trimmedOrphanText = handler.replace(trimmedOrphanText, "\r\n", "");
        trimmedOrphanText = handler.replace(trimmedOrphanText, "<LB>", "");

        //System.out.println("TorphanText:"+trimmedOrphanText);
        if (trimmedOrphanText.length() == 0) {
            //no text before h1
            handleOrphan = false;
        }
        StringBuilder _handler = new StringBuilder(textBlock);
        if (handleOrphan) {
            _handler.insert(0, "<LB>\r\n!!! Enter heading1 title here.\r\n");
            //System.err.println("SEEEEEEEEEE:" + _handler.toString());
            return _handler.toString();
        }
        return textBlock;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("PreProcessor", 0, "Pre-Processor Invoked.");

        uncleanSGML = handleOuterSectContainer(uncleanSGML);

        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);

        debugger.showDebugMessage("PreProcessor", 0, "Marking forced line breaks.");
        //Identify Forced LB
        uncleanSGML = handler.replace(uncleanSGML, "\\\\", "");
        uncleanSGML = handler.replace(uncleanSGML, " -- ", " - ");

        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n#***", "<LB>\r\n####");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n#**", "<LB>\r\n###");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n#*", "<LB>\r\n##");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n*###", "<LB>\r\n****");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n*##", "<LB>\r\n***");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n*#", "<LB>\r\n**");

        //No Link Markup
        uncleanSGML = handler.replace(uncleanSGML, " ~", " ");
        
        if(uncleanSGML.indexOf("<LB>\r\n<LB>\r\n<noparse>") != -1){
            uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n<LB>\r\n<noparse>", "<noparse>");
        }

        //We don't support terms.
        //uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n;", "<LB>\r\n");
        //uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n;:", "<LB>\r\n");

        debugger.showDebugMessage("PreProcessor", 0, "Cleaning leading whitespaces.");
        //clean leading whitespaces
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n ", "<LB>\r\n");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n  ", "<LB>\r\n");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n   ", "<LB>\r\n");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n    ", "<LB>\r\n");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n     ", "<LB>\r\n");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n      ", "<LB>\r\n");

        debugger.showDebugMessage("PreProcessor", 0, "Removing excess line breaks before list items.");
        //tolerate 3 new lines for listitems

        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n<LB>\r\n*", "<LB>\r\n<LB>\r\n*");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n<LB>\r\n<LB>\r\n*", "<LB>\r\n<LB>\r\n*");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n<LB>\r\n<LB>\r\n<LB>\r\n*", "<LB>\r\n<LB>\r\n*");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n<LB>\r\n#", "<LB>\r\n<LB>\r\n#");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n<LB>\r\n<LB>\r\n#", "<LB>\r\n<LB>\r\n#");
        uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n<LB>\r\n<LB>\r\n<LB>\r\n#", "<LB>\r\n<LB>\r\n#");

        //ListParser eats one white space. compensate here
        //uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n*", "<LB>\r\n* ");
        //uncleanSGML = handler.replace(uncleanSGML, "<LB>\r\n#", "<LB>\r\n# ");
        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n*", offset);

            //System.out.println("HIT" + l_loc);

            if (l_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            //There is a list item
            char nextChar = uncleanSGML.charAt(l_loc + 7);


            if (nextChar == '*') {
                //nested list don't mess
                _handle.append(uncleanSGML.substring(offset, l_loc + 7));
                offset = l_loc + 7;
                continue;
            }


            _handle.append(uncleanSGML.substring(offset, l_loc + 7));
            _handle.append(" ");


            offset = l_loc + 7;
        }

        offset = 0;
        uncleanSGML = _handle.toString();

        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n#", offset);

            //System.out.println("HIT" + l_loc);

            if (l_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            //There is a list item
            char nextChar = uncleanSGML.charAt(l_loc + 7);


            if (nextChar == '#') {
                //nested list don't mess
                _handle.append(uncleanSGML.substring(offset, l_loc + 7));
                offset = l_loc + 7;
                continue;
            }


            _handle.append(uncleanSGML.substring(offset, l_loc + 7));
            _handle.append(" ");


            offset = l_loc + 7;
        }

        offset = 0;
        uncleanSGML = _handle.toString();

        debugger.showDebugMessage("PreProcessor", 0, "Scrapping warning/info/note end markups.");
        //do not honour warning, info, note end tags. Fix in later versions
        //uncleanSGML = handler.replace(uncleanSGML, "\r\n{warning}", "\r\n");
        //uncleanSGML = handler.replace(uncleanSGML, "\r\n{info}", "\r\n");
        //uncleanSGML = handler.replace(uncleanSGML, "\r\n{note}", "\r\n");

        uncleanSGML = handler.replace(uncleanSGML, "\\-", "-", 0);
        uncleanSGML = handler.replace(uncleanSGML, "\\[", "[", 0);
        uncleanSGML = handler.replace(uncleanSGML, "\\]", "]", 0);
        uncleanSGML = handler.replace(uncleanSGML, "\\|", "|", 0);
        uncleanSGML = handler.replace(uncleanSGML, "\\*", "*", 0);
        uncleanSGML = handler.replace(uncleanSGML, "\\#", "#", 0);

        debugger.showDebugMessage("PreProcessor", 0, "Pre-Processing Done.");
        return uncleanSGML;
    }
}
