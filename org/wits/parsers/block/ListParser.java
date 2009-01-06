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
import org.wits.parsers.WITSParser;

/**
 *
 * @author FJ
 */
public class ListParser implements WITSParser{

    private String uncleanSGML = null;
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
     * @param uncleanSGML
     */
    public ListParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("ListIC", 0, "Invoking IListIC...");
        // uncleanSGML = new ItemizedListParser(uncleanSGML).getProcessedText();
        debugger.showDebugMessage("ListIC", 0, "Invoking IListIC...Done.");
        debugger.showDebugMessage("ListIC", 0, "Invoking OListIC...");
        
        //System.out.println("BEFORE HANDLER:\r\n"+uncleanSGML+"\r\n-----------");
        //System.out.println("TOPARSER:\r\n"+uncleanSGML);
        ListHandler lhandler1 = new ListHandler(uncleanSGML, "*");
        lhandler1.setDebugger(debugger);
        uncleanSGML = lhandler1.getProcessedText();
        
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);
                
        
        //System.out.println("LIST HANDLER1:\r\n"+uncleanSGML+"\r\n-----------");
            

        uncleanSGML = handler.replace(uncleanSGML, "<o_listitem>", "<listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "</o_listitem>", "</listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "<o_listitem2>", "<listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "</o_listitem2>", "</listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "<o_listitem3>", "<listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "</o_listitem3>", "</listitem>");

          
        ListHandler lhandler2 = new ListHandler(uncleanSGML, "#");
        lhandler2.setDebugger(debugger);
        uncleanSGML = lhandler2.getProcessedText();
        //System.out.println("LIST HANDLER2:\r\n"+uncleanSGML+"\r\n-----------");

        uncleanSGML = handler.replace(uncleanSGML, "<o_listitem>", "<listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "</o_listitem>", "</listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "<o_listitem2>", "<listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "</o_listitem2>", "</listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "<o_listitem3>", "<listitem>");
        uncleanSGML = handler.replace(uncleanSGML, "</o_listitem3>", "</listitem>");

        //System.out.println("FROMPARSER:\r\n"+uncleanSGML);


        debugger.showDebugMessage("ListIC", 0, "Cleaning Pseudo XML...Done.");
        return uncleanSGML;
    }
}
