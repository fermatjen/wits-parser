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
import org.wits.*;

/**
 *
 * @author FJ
 */
public class ListHandler {

    private String uncleanSGML = null;
    private String listStartOfRecord = null;
    private String listStartTag = null;
    private String listEndTag = null;
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
     * @param listStartOfRecord
     */
    public ListHandler(String uncleanSGML, String listStartOfRecord) {
        this.uncleanSGML = uncleanSGML;
        this.listStartOfRecord = listStartOfRecord;
        if (listStartOfRecord.equals("*")) {
            listStartTag = "<itemizedlist>";
            listEndTag = "</itemizedlist>";
        }
        if (listStartOfRecord.equals("#")) {
            listStartTag = "<orderedlist>";
            listEndTag = "</orderedlist>";
        }
    }

    /**
     *
     * @return
     */
    public String handleNOLists() {
        //NESTED itimezedlist
        StringBuilder _handle = new StringBuilder();
        StringHandler _handler = new StringHandler();
        _handler.setDebugger(debugger);

        int offset = 0;
        //second level
        while (true) {
            int l_loc = uncleanSGML.indexOf("<o_listitem2>", offset);
            int r_loc = uncleanSGML.indexOf("<o_listitem>", l_loc);
            int r_loc_dup = uncleanSGML.indexOf(listEndTag, l_loc);

            if (r_loc == -1) {
                if (r_loc_dup != -1) {
                    r_loc = r_loc_dup;
                }
            }
            if (r_loc_dup < r_loc) {
                r_loc = r_loc_dup;
            }

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            //System.out.println("" + l_loc + " " + r_loc + " " + offset);

            String listBlock = uncleanSGML.substring(l_loc, r_loc);
            //System.out.println("LIST BLOCK");
            //System.out.println(listBlock);

            String handledListBlock = _handler.replace(listBlock, "<o_listitem2>", "<o_listitem>");
            handledListBlock = _handler.replace(handledListBlock, "</o_listitem2>", "</o_listitem>");

            _handle.append(uncleanSGML.substring(offset, l_loc));
            _handle.append("<o_listitem>" + listStartTag);

            //listitem can't have screens
            StringHandler handler = new StringHandler();
            handler.setDebugger(debugger);
            handledListBlock = handler.replace(handledListBlock, "<noparse>", "<noparsi>");
            handledListBlock = handler.replace(handledListBlock, "</noparse>", "</noparsi>");

            _handle.append(handledListBlock);
            _handle.append(listEndTag + "</o_listitem>");
            //_handle.append(uncleanSGML.substring(r_loc, uncleanSGML.length()));
            offset = r_loc;

        }
        //update in-memory copy
        debugger.showDebugMessage("OListIC", uncleanSGML.length(), "Nested OList - Updating Tree...Done");
        uncleanSGML = _handle.toString();

        _handle = new StringBuilder();
        _handler = new StringHandler();
        _handler.setDebugger(debugger);

        offset = 0;
        //second level
        while (true) {
            int l_loc = uncleanSGML.indexOf("<o_listitem3>", offset);
            int r_loc = uncleanSGML.indexOf("<o_listitem>", l_loc);
            int r_loc_dup = uncleanSGML.indexOf(listEndTag, l_loc);

            if (r_loc == -1) {
                if (r_loc_dup != -1) {
                    r_loc = r_loc_dup;
                }
            }
            if (r_loc_dup < r_loc) {
                r_loc = r_loc_dup;
            }

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            //System.out.println("" + l_loc + " " + r_loc + " " + offset);

            String listBlock = uncleanSGML.substring(l_loc, r_loc);
            //System.out.println("LIST BLOCK");
            //System.out.println(listBlock);

            String handledListBlock = _handler.replace(listBlock, "<o_listitem3>", "<o_listitem>");
            handledListBlock = _handler.replace(handledListBlock, "</o_listitem3>", "</o_listitem>");

            _handle.append(uncleanSGML.substring(offset, l_loc));
            _handle.append("<o_listitem>" + listStartTag);
            //listitem can't have screens
            StringHandler handler = new StringHandler();
            handler.setDebugger(debugger);
            handledListBlock = handler.replace(handledListBlock, "<noparse>", "<noparsi>");
            handledListBlock = handler.replace(handledListBlock, "</noparse>", "</noparsi>");

            _handle.append(handledListBlock);
            _handle.append(listEndTag + "</o_listitem>");
            //_handle.append(uncleanSGML.substring(r_loc, uncleanSGML.length()));
            offset = r_loc;

        }
        debugger.showDebugMessage("OListIC", uncleanSGML.length(), "Nested OList - Updating Tree...Done");
        uncleanSGML = _handle.toString();
        return uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String handleOLists() {

        StringBuilder _handle = new StringBuilder();
        StringHandler _handler = new StringHandler();
        _handler.setDebugger(debugger);

        int offset = 0;
        boolean appendStartTag = true;

        while (true) {
            int l_loc = uncleanSGML.indexOf("<o_listitem", offset);
            int r_loc = uncleanSGML.indexOf("<LB>", l_loc);
            int er1_loc = uncleanSGML.indexOf("<o_listitem>", r_loc);
            int er2_loc = uncleanSGML.indexOf("<o_listitem2>", r_loc);
            int er3_loc = uncleanSGML.indexOf("<o_listitem3>", r_loc);

            int er_loc = 0;
            if (er1_loc == -1) {
                //some freaking number
                er1_loc = 999999999;
            }
            if (er2_loc == -1) {
                //some freaking number
                er2_loc = 999999999;
            }
            if (er3_loc == -1) {
                //some freaking number
                er3_loc = 999999999;
            }

            if ((er1_loc < er2_loc) && (er1_loc < er3_loc)) {
                er_loc = er1_loc;
            }
            if (er2_loc < er1_loc && er2_loc < er3_loc) {
                er_loc = er2_loc;
            }
            if (er3_loc < er2_loc && er3_loc < er1_loc) {
                er_loc = er3_loc;
            }
            //System.out.println("HIL:" + l_loc + " " + r_loc + " " + er_loc+" - "+er1_loc+"?"+er2_loc+"?"+er3_loc+"");


            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            if (er_loc != r_loc + 6) {
                debugger.showDebugMessage("OListIC", r_loc, "Nested OList - Adding close tag.");
                _handle.append(uncleanSGML.substring(offset, r_loc));
                _handle.append(listEndTag);
                appendStartTag = true;
            } else {
                //System.out.println("HERE");
                _handle.append(uncleanSGML.substring(offset, l_loc));
                if (appendStartTag) {
                    debugger.showDebugMessage("OListIC", l_loc, "Nested OList - Adding open tag.");
                    _handle.append(listStartTag);
                    //System.out.println("-----APPENDING-----");
                    appendStartTag = false;
                }
                _handle.append(uncleanSGML.substring(l_loc, r_loc));
            }

            offset = r_loc;
        }
        uncleanSGML = _handle.toString();
        debugger.showDebugMessage("ListIC", uncleanSGML.length(), "Nested List - Processing third stage...");
        //System.out.println("SECONDSTAGE:\r\n" + uncleanSGML);
        uncleanSGML = handleNOLists();
        debugger.showDebugMessage("ListIC", uncleanSGML.length(), "Nested List - Processing third stage...Done.");

        return uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        //System.out.println("UNCLEAN:"+uncleanSGML);
        //debugger.showDebugMessage("OListIC", 0, "OrderedListIC Invoked.");
        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf(listStartOfRecord, offset);
            int r_loc = uncleanSGML.indexOf(listStartOfRecord, l_loc + 1);

            int dup_loc = r_loc;

            int r_loc_dup = uncleanSGML.indexOf("<LB>", l_loc);

            if (r_loc == -1) {
                if (r_loc_dup != -1) {
                    r_loc = r_loc_dup;
                }
            }
            if (r_loc_dup < r_loc) {
                r_loc = r_loc_dup;
            }


            if (l_loc == -1 || r_loc == -1) {
                debugger.showDebugMessage("OListIC", 0, "No Ordered Lists.");
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            //check if it is a valid list
            char _c1 = uncleanSGML.charAt(l_loc - 1);
            char _c2 = uncleanSGML.charAt(l_loc - 2);

            if (_c1 != '\n' && _c2 != '\r') {
                _handle.append(uncleanSGML.substring(offset, r_loc));
                offset = r_loc;
                continue;
            }



            //check if it is nested ordered list
            if (l_loc + 1 == r_loc) {
                //handle nested list item
                //Check third level
                //check if it is nested ordered list
                debugger.showDebugMessage("OListIC", l_loc, "Checking if OList is nested...");
                boolean isDoubleNested = false;
                boolean isTripleNested = false;
                int roc3 = uncleanSGML.indexOf(listStartOfRecord, r_loc + 1);
                //System.out.println("CHECK"+l_loc + ":" + r_loc+":"+roc3);
                if (roc3 == r_loc + 1) {
                    debugger.showDebugMessage("OListIC", l_loc, "Triple nested OList found.");
                    isTripleNested = true;
                } else {
                    debugger.showDebugMessage("OListIC", l_loc, "Double nested OList found.");
                    isDoubleNested = true;
                }
                if (isDoubleNested) {
                    //System.out.println(l_loc + ":" + r_loc_dup);
                    String targetString = uncleanSGML.substring(l_loc + 2, r_loc_dup);

                    //System.out.println("NESTEDTS:" + targetString);
                    _handle.append(uncleanSGML.substring(offset, l_loc));
                    debugger.showDebugMessage("OListIC", l_loc, "Double nested OList - Producing Pseudo XML.");

                    //listitem can't have screens
                    //StringHandler handler = new StringHandler();
                    //handler.setDebugger(debugger);
                    //targetString = handler.replace(targetString, "<noparse>", "<noparsi>");
                    //targetString = handler.replace(targetString, "</noparse>", "</noparsi>");

                    _handle.append("<o_listitem2>");
                    _handle.append(listItemCleaner(targetString));
                    _handle.append("</o_listitem2>");
                    offset = r_loc + targetString.length() + 1;
                    continue;
                }
                if (isTripleNested) {
                    //System.out.println("TRIPLE");
                    String targetString = uncleanSGML.substring(l_loc + 3, r_loc_dup);
                    //System.out.println("NESTEDTS:" + targetString);
                    debugger.showDebugMessage("OListIC", l_loc, "Triple nested OList - Producing Pseudo XML.");
                    _handle.append(uncleanSGML.substring(offset, l_loc));

                    //listitem can't have screens
                    //StringHandler handler = new StringHandler();
                    //handler.setDebugger(debugger);
                    //targetString = handler.replace(targetString, "<noparse>", "<noparsi>");
                    //targetString = handler.replace(targetString, "</noparse>", "</noparsi>");

                    _handle.append("<o_listitem3>");
                    _handle.append(listItemCleaner(targetString));
                    _handle.append("</o_listitem3>");
                    offset = r_loc + targetString.length() + 2;
                    continue;
                }

            }

            String targetString = uncleanSGML.substring(l_loc + 2, r_loc);
            //System.out.println("TS:" + targetString);


            //could be an orderedlist
            //System.out.println("ordered list-----------");
            //System.out.println(targetString);
            //System.out.println("End ordered list-----------");
            _handle.append(uncleanSGML.substring(offset, l_loc));

            //listitem can't have screens
            //StringHandler handler = new StringHandler();
            //handler.setDebugger(debugger);
            //targetString = handler.replace(targetString, "<noparse>", "<noparsi>");
            //targetString = handler.replace(targetString, "</noparse>", "</noparsi>");

            _handle.append("<o_listitem>");
            _handle.append(listItemCleaner(targetString));
            //System.out.println("TOP# "+targetString);
            _handle.append("</o_listitem>");

            offset = r_loc;

        }
        //update in-memory copy
        debugger.showDebugMessage("OListIC", uncleanSGML.length(), "Updating Tree...Done");
        uncleanSGML = _handle.toString();
        //Before returning clean the ilist tree
        debugger.showDebugMessage("OListIC", uncleanSGML.length(), "Nested OList - Processing second stage...");

        uncleanSGML = handleOLists();
        debugger.showDebugMessage("OListIC", uncleanSGML.length(), "Nested OList - Processing second stage...Done");

        //correct orphan entry
        //System.out.println("Correcting Orphan...");
        uncleanSGML = correctOrphanListItem(uncleanSGML);

        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);





        return uncleanSGML;
    }

    private String listItemCleaner(String listItem) {

        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);
        listItem = "<para>" + listItem;
        listItem = listItem + "</para>";
        listItem = handler.replace(listItem, "<noparse>", "</para><noparse>");
        listItem = handler.replace(listItem, "</noparse>", "</noparse><para>");

        listItem = handler.replace(listItem, "<note>", "</para><note>");
        listItem = handler.replace(listItem, "</note>", "</note><para>");

        listItem = handler.replace(listItem, "<tip>", "</para><tip>");
        listItem = handler.replace(listItem, "</tip>", "</tip><para>");

        listItem = handler.replace(listItem, "<caution>", "</para><caution>");
        listItem = handler.replace(listItem, "</caution>", "</caution><para>");

        listItem = handler.replace(listItem, "<para></para>", "");

        

        return listItem;
    }

    private String correctOrphanListItem(String textBlock) {
        //You need to start list tag for a single entry
        //System.out.println("TOORPHAN:" + textBlock);
        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = textBlock.indexOf("<o_listitem>", offset + 1);

            if (l_loc == -1) {
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }
            int r_loc = textBlock.indexOf(listEndTag, l_loc);

            if (r_loc == -1) {
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }

            //System.out.println("RATIO:" + l_loc + ":" + r_loc);

            String orphanCandidate = textBlock.substring(l_loc, r_loc);

            if (l_loc - 13 < 0) {
                //Maybe somewhere in the top?
                _handle.append(textBlock.substring(offset, r_loc));
                offset = r_loc;
                continue;
            }

            String prevSnippet = textBlock.substring(l_loc - 13, l_loc);
            //System.out.println("ORPHAN:" + orphanCandidate);
            //System.out.println("PSNIPPET:" + prevSnippet);

            if (orphanCandidate.substring(12, orphanCandidate.length()).indexOf("<o_listitem>") != -1) {
                _handle.append(textBlock.substring(offset, r_loc));
                offset = r_loc;
                continue;
            }
            if (prevSnippet.indexOf("o_listitem") != -1) {
                _handle.append(textBlock.substring(offset, r_loc));
                offset = r_loc;
                continue;
            }

            //System.out.println("INSERTING START TAG AT:"+l_loc );
            _handle.append(textBlock.substring(offset, l_loc));
            _handle.append(listStartTag);


            offset = l_loc;
        }
        //System.out.println("----------------CORRECTED----------\r\n" + _handle.toString());
        return _handle.toString();
    }
}
