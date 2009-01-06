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

package org.wits.parsers.inline;

import org.wits.debugger.WITSDebugger;
import org.wits.parsers.WITSParser;

/**
 *
 * @author FJ
 */
public class LiteralParser implements WITSParser{
    
    private String uncleanSGML  = null;
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
    public LiteralParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }
    
    
    /**
     *
     * @return
     */
    public String getProcessedText(){
        debugger.addLineBreak();
        //System.out.println("UNCLEAN:"+uncleanSGML);
        debugger.showDebugMessage("LiteralIC", 0, "LiteralIC Invoked.");
        int offset = 0;
        StringBuilder _handle =  new StringBuilder();
        
        while(true){
            int l_loc = uncleanSGML.indexOf("{{",offset);
            int r_loc = uncleanSGML.indexOf("}}",l_loc+2);
            //System.out.println(l_loc+":"+r_loc);
            
            if(l_loc == -1 || r_loc == -1){                
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            debugger.showDebugMessage("LiteralIC", l_loc, "Processing literal markup.");
            
            String targetString = uncleanSGML.substring(l_loc+2, r_loc);
            //System.out.println("TS:"+targetString);
            
            if(targetString.indexOf("<LB>") == -1){
                debugger.showDebugMessage("LiteralIC", l_loc, "Adding literal tags.");
                //fetch the block
                _handle.append(uncleanSGML.substring(offset,l_loc));
                _handle.append("<literal>");
                _handle.append(targetString);
                _handle.append("</literal>");
            } 
            else{
                debugger.showDebugMessage("LiteralIC", l_loc, "Ignoring element. Line break found.");
                int poffset = offset;
                if(offset == 0){poffset=1;}
                _handle.append(uncleanSGML.substring(poffset-1, r_loc));
            }
            offset = r_loc+2;
            
        }
        //update in-memory copy
        debugger.showDebugMessage("LiteralIC", uncleanSGML.length(), "Updating Tree...Done.");
        uncleanSGML = _handle.toString();
        
        return uncleanSGML;
    }
}
