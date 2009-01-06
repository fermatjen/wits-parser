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
public class EmphasisParser implements WITSParser{

    private String uncleanSGML = null;
    private WITSDebugger debugger = null;
    private boolean isDocBookOutput = true;

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
    public EmphasisParser(boolean isDocBookOutput, String uncleanSGML) {
        this.isDocBookOutput = isDocBookOutput;
        this.uncleanSGML = uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {

        debugger.addLineBreak();
        debugger.showDebugMessage("EmphasisIC", 0, "EmphasisIC Invoked.");

        int offset = 0;
        StringBuilder _handle = new StringBuilder();
        debugger.showDebugMessage("EmphasisIC", 0, "[BOLD] Checking Occurence...");

        while (true) {
            int l_loc = uncleanSGML.indexOf("__", offset);
            int r_loc = uncleanSGML.indexOf("__", l_loc + 1);


            if ((l_loc == -1) || (r_loc == -1)) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            String listString = uncleanSGML.substring(l_loc + 1, r_loc);
            //System.out.println("TS:" + listString);
            //ignore alien list
            if (listString.indexOf("<LB>") != -1) {
                //leave lists
                _handle.append(uncleanSGML.substring(offset, r_loc));
                offset = r_loc;
                //System.out.println("IGNORING LIST:" + l_loc + ":" + r_loc + ":" + offset);
                continue;
            }
            if(listString.trim().equals("")){
                 _handle.append(uncleanSGML.substring(offset, r_loc));
                offset = r_loc;
                //System.out.println("IGNORING LIST:" + l_loc + ":" + r_loc + ":" + offset);
                continue;
             }

            //dont support long emphasis
            if(listString.indexOf("|") != -1 || listString.indexOf(", ") != -1 || listString.indexOf(". ") != -1){
                _handle.append(uncleanSGML.substring(offset, r_loc));
                offset = r_loc;
                //System.out.println("IGNORING LIST:" + l_loc + ":" + r_loc + ":" + offset);
                continue;
            }
            
            //list check
            char _c1, _c2;

            if (l_loc != 0) {
                _c1 = uncleanSGML.charAt(l_loc - 1);
                _c2 = uncleanSGML.charAt(l_loc - 2);

                if (_c1 == '\n' && _c2 == '\r' && listString.indexOf("<LB>") != -1) {
                    _handle.append(uncleanSGML.substring(offset, r_loc));
                    offset = r_loc;
                    continue;
                }
            }
            
            debugger.showDebugMessage("EmphasisIC", l_loc, "[BOLD] Found Occurence.");

            String targetString = uncleanSGML.substring(l_loc + 2, r_loc);
            //System.out.println("TS:"+targetString);

            boolean canParse = true;

            if (targetString.indexOf("<LB>") != -1) {
                canParse = false;
                debugger.showDebugMessage("EmphasisIC", l_loc, "Parsing suppressed due to Line Break.");
            }
            if (targetString.endsWith("<LB>")) {
                //special case. allow only this case.
                canParse = true;
            }


            if (canParse && (targetString.indexOf("}}") == -1)) {
                debugger.showDebugMessage("EmphasisIC", l_loc, "Checking parsing rules....HIT!.");
                // This is an emphasized element
                // fetch the block                
                _handle.append(uncleanSGML.substring(offset, l_loc));

                if (targetString.indexOf("noparse>") == -1 && targetString.indexOf("noparsi>") == -1) {
                    if (!isDocBookOutput) {
                        _handle.append("<emphasis role=\"strong\">");
                    } else {
                        _handle.append("<emphasis>");
                    }
                    _handle.append(targetString);
                    _handle.append("</emphasis>");
                } else {
                    _handle.append(targetString);
                }

                debugger.showDebugMessage("EmphasisIC", l_loc, "Markup Text transformed.");
            } else {
                debugger.showDebugMessage("EmphasisIC", l_loc, "Checking parsing rules....MISMATCH!");

                int poffset = offset;

                if (offset == 0) {
                    poffset = 1;
                }

                _handle.append(uncleanSGML.substring(poffset, r_loc));
            }

            offset = r_loc + 2;
        }

        debugger.showDebugMessage("EmphasisIC", uncleanSGML.length(), "Updating Tree...Done");

        // update in-memory copy
        uncleanSGML = _handle.toString();

        offset = 0;
        _handle = new StringBuilder();

        debugger.showDebugMessage("EmphasisIC", 0, "[ITALICS] Checking Occurence...");

        while (true) {
            int l_loc = uncleanSGML.indexOf("''", offset);
            int r_loc = uncleanSGML.indexOf("''", l_loc + 1);



            if ((l_loc == -1) || (r_loc == -1)) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            debugger.showDebugMessage("EmphasisIC", l_loc, "[ITALICS] Found Occurence.");

            String targetString = uncleanSGML.substring(l_loc + 2, r_loc);
            
            //dont support long emphasis
            if(targetString.indexOf("|") != -1 || targetString.indexOf(", ") != -1 || targetString.indexOf(". ") != -1){
                _handle.append(uncleanSGML.substring(offset, r_loc));
                offset = r_loc;
                //System.out.println("IGNORING LIST:" + l_loc + ":" + r_loc + ":" + offset);
                continue;
            }
            
                        //list check
            char _c1, _c2;

            if (l_loc != 0) {
                _c1 = uncleanSGML.charAt(l_loc - 1);
                _c2 = uncleanSGML.charAt(l_loc - 2);

                if (_c1 == '\n' && _c2 == '\r') {
                    _handle.append(uncleanSGML.substring(offset, r_loc));
                    offset = r_loc;
                    continue;
                }
            }

            if ((targetString.indexOf("<LB>") == -1) && (targetString.indexOf("}}") == -1)) {
                debugger.showDebugMessage("EmphasisIC", l_loc, "Checking parsing rules....HIT!.");
                // This is an emphasized element
                // fetch the block
                _handle.append(uncleanSGML.substring(offset, l_loc));

                if (targetString.indexOf("noparse>") == -1 && targetString.indexOf("noparsi>") == -1) {
                    _handle.append("<emphasis>");
                    _handle.append(targetString);
                    _handle.append("</emphasis>");
                } else {
                    _handle.append(targetString);
                }
                debugger.showDebugMessage("EmphasisIC", l_loc, "Markup Text transformed.");
            } else {
                int poffset = offset;

                if (offset == 0) {
                    poffset = 1;
                }
                debugger.showDebugMessage("EmphasisIC", l_loc, "Checking parsing rules....MISMATCH!");
                _handle.append(uncleanSGML.substring(poffset - 1, r_loc));
            }

            offset = r_loc + 2;
        }

        // handle citation
        // update in-memory copy
        debugger.showDebugMessage("EmphasisIC", uncleanSGML.length(), "Updating Tree...Done");
        uncleanSGML = _handle.toString();

        /*
         *Not known in JSPWiki
        // third parse
        offset = 0;
        _handle = new StringBuilder();
        debugger.showDebugMessage("EmphasisIC", 0, "[CITATION] Checking Occurence...");
        
        while (true) {
        int l_loc = uncleanSGML.indexOf("??", offset);
        int r_loc = uncleanSGML.indexOf("??", l_loc + 2);
        
        if ((l_loc == -1) || (r_loc == -1)) {
        _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
        
        break;
        }
        debugger.showDebugMessage("EmphasisIC", l_loc, "[CITATION] Found Occurence.");
        
        String targetString = uncleanSGML.substring(l_loc + 2, r_loc);
        
        if ((targetString.indexOf("<LB>") == -1) && (targetString.indexOf("}}") == -1)) {
        // This is an emphasized element
        // fetch the block
        debugger.showDebugMessage("EmphasisIC", l_loc, "Checking parsing rules....HIT!.");
        _handle.append(uncleanSGML.substring(offset, l_loc));
        _handle.append("<emphasis>");
        _handle.append(targetString);
        _handle.append("</emphasis>");
        debugger.showDebugMessage("EmphasisIC", l_loc, "Markup Text transformed.");
        } else {
        int poffset = offset;
        debugger.showDebugMessage("EmphasisIC", l_loc, "Checking parsing rules....MISMATCH!");
        if (offset == 0) {
        poffset = 1;
        }
        
        _handle.append(uncleanSGML.substring(poffset - 1, r_loc));
        }
        
        offset = r_loc + 2;
        }
        uncleanSGML = _handle.toString();
        debugger.showDebugMessage("EmphasisIC", uncleanSGML.length(), "Updating Tree...Done");
        
        //System.out.println("FROM EMPHASIS:" + uncleanSGML);
         */

        return uncleanSGML;
    }
}



