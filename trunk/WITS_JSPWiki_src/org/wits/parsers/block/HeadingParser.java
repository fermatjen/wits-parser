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
import java.util.Iterator;
import java.util.TreeMap;
import org.wits.WITSProperties;
import org.wits.parsers.WITSParser;
import org.wits.patterns.StringHandler;

/**
 * In JSPWiki !!!-h1 !!-h2 !-h3 We support only that.
 * @author FJ
 */
public class HeadingParser implements WITSParser{

    private String uncleanSGML = null;
    private WITSDebugger debugger = null;
    private int ID = 1;
    private WITSProperties props = null;
    private boolean hideID = false;

    /**
     *
     * @param debugger
     */
    public void setDebugger(WITSDebugger debugger) {
        this.debugger = debugger;
    }

    /**
     *
     * @return
     */
    public int getID() {
        return ID;
    }

    /**
     *
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }
    
    

    /**
     *
     * @param uncleanSGML
     */
    public HeadingParser(boolean hideID, String uncleanSGML, WITSProperties props) {
        this.hideID = hideID;
        this.uncleanSGML = uncleanSGML;
        this.props = props;
    }

    /**
     *
     * @return
     */
    public String fixHierarchy() {
        int offset1 = 0;
        int offset2 = 0;
        int offset3 = 0;
        int bufferInsertOffset = 0;
        debugger.showDebugMessage("HeadingIC", uncleanSGML.length(), "Fixing Heading Hierarchy...");
        TreeMap <Integer, String> sectPositions = new TreeMap <Integer, String>();
        StringBuilder _handle = new StringBuilder(uncleanSGML);

        while (true) {
            int sect1_loc = uncleanSGML.indexOf("<sect1>", offset1);
            int sect2_loc = uncleanSGML.indexOf("<sect2>", offset2);
            int sect3_loc = uncleanSGML.indexOf("<sect3>", offset3);

            if (sect1_loc != -1) {
                offset1 = sect1_loc + 7;
                //System.out.println("SECT1@"+sect1_loc);
                //quick hack - hashmap rejects duplicate keys. 
                debugger.showDebugMessage("HeadingIC", sect1_loc, "Appending tree with heading1...");
                sectPositions.put(new Integer(sect1_loc), "<sect1>");
            }
            if (sect2_loc != -1) {
                offset2 = sect2_loc + 7;
                //System.out.println("SECT2@"+sect2_loc); 
                debugger.showDebugMessage("HeadingIC", sect2_loc, "Appending tree with heading2...");
                sectPositions.put(new Integer(sect2_loc), "<sect2>");
            }
            if (sect3_loc != -1) {
                offset3 = sect3_loc + 7;
                //System.out.println("SECT3@"+sect3_loc);
                debugger.showDebugMessage("HeadingIC", sect3_loc, "Appending tree with heading3...");
                sectPositions.put(new Integer(sect3_loc), "<sect3>");
            }
            if (sect1_loc == -1 && sect2_loc == -1 && sect3_loc == -1) {
                //No more headings
                debugger.showDebugMessage("HeadingIC", uncleanSGML.length(), "No more headings...");
                break;
            }
        }
        //Read from HashMap
        Iterator keys = sectPositions.keySet().iterator();
        //System.out.println("KEYS COUNT:" + sectPositions.size());
        debugger.showDebugMessage("HeadingIC", uncleanSGML.length(), "Tree Node Count:" + sectPositions.size());

        String currentSection = null;
        int currentOccurence = -1;
        Integer key = null;

        if (keys.hasNext()) {
            key = (Integer) keys.next();
            currentSection = (String) sectPositions.get(key);
            currentOccurence = key.intValue();
            currentSection = (String) sectPositions.get(key);
            currentOccurence = key.intValue();
        } else {
            uncleanSGML = _handle.toString();
            return uncleanSGML;
        }

        while (keys.hasNext()) {

            //System.out.println("Key:" + key.intValue() + " Sect:" + sectPositions.get(key));

            //get next occurence
            String nextSection = null;
            String prevSection = null;
            int nextOccurence = -1;

            if (keys.hasNext()) {
                Integer nextKey = (Integer) keys.next();
                //System.out.println("Key:" + nextKey.intValue() + " Sect:" + sectPositions.get(nextKey));
                nextSection = (String) sectPositions.get(nextKey);
                nextOccurence = nextKey.intValue();
                //System.out.println("WEIGHTS: (next) - " + getWeight(nextSection) + " (current) -  " + getWeight(currentSection));
                debugger.showDebugMessage("HeadingIC", nextOccurence, "Retreiving heading weight..." + getWeight(currentSection) + ":" + getWeight(nextSection));

                if (getWeight(nextSection) >= getWeight(currentSection)) {
                    //greater tag...close current tag.
                    debugger.showDebugMessage("HeadingIC", nextOccurence, "Next Occurence - Greater tag");

                    //System.out.println("WEIGHTS:" + getWeight(nextSection) + " " + getWeight(currentSection));
                    //System.out.println("CLOSE TAG:" + currentSection + " AT:" + nextOccurence);
                    debugger.showDebugMessage("HeadingIC", nextOccurence + bufferInsertOffset, "Inserting matching end tag.");
                    _handle.insert((nextOccurence + bufferInsertOffset), getMatchingEndTag(currentSection));
                    bufferInsertOffset = bufferInsertOffset + getMatchingEndTag(currentSection).length();
                    //current tag closed. close prev tags too
                    if (currentSection.equals("<sect3>")) {
                        //sect3 already closed. close sect2
                        if (nextSection.equals("<sect2>")) {
                            debugger.showDebugMessage("HeadingIC", nextOccurence + bufferInsertOffset, "Closing heading2.");
                            _handle.insert(nextOccurence + bufferInsertOffset, "</sect2>");
                            bufferInsertOffset = bufferInsertOffset + 8;
                        }
                        //sect3 already closed. close 2 and sect1
                        if (nextSection.equals("<sect1>")) {
                            debugger.showDebugMessage("HeadingIC", nextOccurence + bufferInsertOffset, "Closing heading2.");
                            _handle.insert(nextOccurence + bufferInsertOffset, "</sect2>");
                            bufferInsertOffset = bufferInsertOffset + 8;
                            debugger.showDebugMessage("HeadingIC", nextOccurence + bufferInsertOffset, "Closing heading3.");
                            _handle.insert(nextOccurence + bufferInsertOffset, "</sect1>");
                            bufferInsertOffset = bufferInsertOffset + 8;
                        }
                    }
                    if (currentSection.equals("<sect2>")) {
                        //sect2 already closed. close sect1
                        if (nextSection.equals("<sect1>")) {
                            debugger.showDebugMessage("HeadingIC", nextOccurence + bufferInsertOffset, "Closing heading1.");
                            _handle.insert(nextOccurence + bufferInsertOffset, "</sect1>");
                            bufferInsertOffset = bufferInsertOffset + 8;
                        }
                    }
                }

                //Auto flatteining
                if (getWeight(nextSection) - getWeight(currentSection) == 0 ||
                        getWeight(nextSection) - getWeight(currentSection) == 1 ||
                        getWeight(nextSection) - getWeight(currentSection) == -1) {
                    //Looks quite normal                    
                } else {
                    //warn
                    //System.out.println("NEED TO FLATTEN: "+nextSection);
                    if (currentSection.equals("<sect1>")) {
                        //sect2 already closed. close sect1
                        if (nextSection.equals("<sect3>")) {
                            debugger.showDebugMessage("HeadingIC", nextOccurence + bufferInsertOffset, "Closing heading1.");

                            //bufferInsertOffset = bufferInsertOffset + getMatchingEndTag(nextSection).length();
                            //String orphanText = "<sect2><title>Enter section2 title here</title><LB>\r\n<para>\r\nEnter section2 summary here.</para>\r\n<LB>";
                            String orphanText = "<sect2> "+props.WITS_DanglerSectionTitle+"<LB>\r\n"+props.WITS_DanglerSectionSummary+"<LB>\r\n";
                            //System.out.println("LENGTH:"+orphanText.length());

                            _handle.insert(nextOccurence + bufferInsertOffset, orphanText);
                            bufferInsertOffset = bufferInsertOffset + orphanText.length();

                        }
                    }
                    debugger.showWarningMessage("HeadingIC", nextOccurence, "Invalid Heading Hierarchy. Flattening Headings...Done..");
                }
                prevSection = nextSection;
            }
            currentSection = nextSection;
            currentOccurence = nextOccurence;

        }
        //System.out.println("LAST TAG:" + currentSection);
        //close last tag
        debugger.showDebugMessage("HeadingIC", _handle.length(), "Approaching document end. Cleaning up tags...");
        if (currentSection.equals("<sect3>")) {
            debugger.showDebugMessage("HeadingIC", _handle.length(), "Closing heading3,heading2, and heading1.");
            _handle.insert(_handle.length(), "</sect3></sect2></sect1>");
        }
        if (currentSection.equals("<sect2>")) {
            debugger.showDebugMessage("HeadingIC", _handle.length(), "Closing heading2, and heading1.");
            _handle.insert(_handle.length(), "</sect2></sect1>");
        }
        if (currentSection.equals("<sect1>")) {
            debugger.showDebugMessage("HeadingIC", _handle.length(), "heading1.");
            _handle.insert(_handle.length(), "</sect1>");
        }
        uncleanSGML = _handle.toString();
        debugger.showDebugMessage("EmphasisIC", _handle.length(), "Updating Tree...Done");
        return uncleanSGML;
    }

    private String getMatchingEndTag(String tag) {
        debugger.showDebugMessage("EmphasisIC", uncleanSGML.length(), "Finding matching end tag...");
        if (tag.equals("<sect1>")) {
            return "</sect1>";
        }
        if (tag.equals("<sect2>")) {
            return "</sect2>";
        }
        if (tag.equals("<sect3>")) {
            return "</sect3>";
        }
        debugger.showDebugMessage("EmphasisIC", uncleanSGML.length(), "Finding matching end tag...Done");
        return "</sect3>";
    }

    private int getWeight(String section) {
        debugger.showDebugMessage("EmphasisIC", uncleanSGML.length(), "Retreiving weights...");
        if (section.equals("<sect1>")) {
            return 3;
        }
        if (section.equals("<sect2>")) {
            return 2;
        }
        if (section.equals("<sect3>")) {
            return 1;
        }
        debugger.showDebugMessage("EmphasisIC", uncleanSGML.length(), "Retreiving weights...Done");
        return 0;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        StringBuilder _handle = new StringBuilder();
        int offset = 0;
        debugger.showDebugMessage("HeadingIC", 0, "HeadingIC Invoked.");
        
        //System.out.println("TO HEADING PROCESSOR:");
        //System.out.println(uncleanSGML);
        

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n!!!", offset);
            if (l_loc == -1) {
                debugger.showDebugMessage("HeadingIC", 0, "No heading1.");
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            //System.out.println("FOUND h1 at:"+uncleanSGML.charAt(l_loc));
            
            debugger.showDebugMessage("HeadingIC", l_loc, "Heading1 found.");

            _handle.append(uncleanSGML.substring(offset, l_loc));
            _handle.append("<LB>\r\n<sect1>");

            debugger.showDebugMessage("HeadingIC", l_loc, "Markup Text transformed.");

            offset = l_loc + 9;

        }
        uncleanSGML = _handle.toString();
        //handle sect2
        _handle = new StringBuilder();
        offset = 0;

        while (true) {
            
            int l_loc = uncleanSGML.indexOf("<LB>\r\n!!", offset);            
            
            if (l_loc == -1) {
                debugger.showDebugMessage("HeadingIC", 0, "No heading2.");
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            
            //System.out.println("FOUND h2 at:"+uncleanSGML.charAt(l_loc));
            //You need to check if it is not !!!(h1)
            //System.out.println("CHAR:"+uncleanSGML.charAt(l_loc+1));
            if(uncleanSGML.charAt(l_loc+8) == '!'){
                //this is h1 ignore
                //System.out.println("IGNORING");
                _handle.append(uncleanSGML.substring(offset, l_loc+8));
                offset =l_loc+8;
                continue;
            }
            //System.out.println("Heading at:" + l_loc);
            debugger.showDebugMessage("HeadingIC", l_loc, "Heading2 found.");
            _handle.append(uncleanSGML.substring(offset, l_loc));
            _handle.append("<LB>\r\n<sect2>");

            debugger.showDebugMessage("HeadingIC", l_loc, "Markup Text transformed.");

            offset = l_loc + 8;

        }
        uncleanSGML = _handle.toString();

        _handle = new StringBuilder();
        offset = 0;
        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n!", offset);         
            
            
            if (l_loc == -1) {
                debugger.showDebugMessage("HeadingIC", 0, "No heading3.");
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            
            //System.out.println("FOUND h3 at:"+uncleanSGML.charAt(l_loc));
            //You need to check if it is not !!!(h1) and !!(h2)
            //System.out.println("CHAR:"+uncleanSGML.charAt(l_loc+1));
            if(uncleanSGML.charAt(l_loc+7) == '!'){
                //this is h1 or h2 ignore
               // System.out.println("IGNORING");
                _handle.append(uncleanSGML.substring(offset, l_loc+7));
                offset =l_loc+7;
                continue;
            }
            
            debugger.showDebugMessage("HeadingIC", l_loc, "Heading3 found.");

            _handle.append(uncleanSGML.substring(offset, l_loc));
            _handle.append("<LB>\r\n<sect3>");
            debugger.showDebugMessage("HeadingIC", l_loc, "Markup Text transformed.");
            offset = l_loc + 7;

        }

        uncleanSGML = _handle.toString();
        debugger.showDebugMessage("HeadingIC", uncleanSGML.length(), "Fixing Hierarchy...");
        fixHierarchy();
        debugger.showDebugMessage("HeadingIC", uncleanSGML.length(), "Fixing Hierarchy...Done");
        debugger.showDebugMessage("HeadingIC", uncleanSGML.length(), "Retreiving Heading Info...");
        retrieveSectInfo();
        debugger.showDebugMessage("HeadingIC", uncleanSGML.length(), "Retreiving Heading Info...Done");
        return uncleanSGML;
    }

    /**
     *
     */
    public void retrieveSectInfo() {
        StringBuilder _handle = new StringBuilder();
        int offset = 0;
        while (true) {
            int l_loc = uncleanSGML.indexOf("<sect", offset);
            int r_loc = uncleanSGML.indexOf("<LB>", l_loc);
            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            String sectCandidate = uncleanSGML.substring(l_loc, r_loc);
           // System.out.println("SC:"+sectCandidate);

            String sectTitle = null;
            String sectID = null;
            String sectPrefix = null;

            sectPrefix = sectCandidate.substring(0, 6);

            //Legacy confluence method
            int ll_loc = sectCandidate.indexOf("{anchor:", 0);
            if (ll_loc == -1) {
                //No anchor
                debugger.showDebugMessage("HeadingIC", l_loc, "Heading Info - No Anchor...");
                sectID = "WITS" + ID;
                ID++;
                sectTitle = sectCandidate.substring(7, sectCandidate.length());
            } else {
                //anchor present
                debugger.showDebugMessage("HeadingIC", ll_loc, "Heading Info - Found anchor...");
                int rr_loc = sectCandidate.indexOf("}", ll_loc);
                sectID = "WITS" + ID + "-" + sectCandidate.substring(ll_loc + 8, rr_loc);
                sectTitle = sectCandidate.substring(rr_loc + 1, sectCandidate.length());
            }

            //clean format from sect title
            StringHandler handler = new StringHandler();
            handler.setDebugger(debugger);

            sectTitle = handler.replace(sectTitle, "[", "", 0);
            sectTitle = handler.replace(sectTitle, "]", "", 0);
            sectTitle = handler.replace(sectTitle, "|", "", 0);

            //_handle.append
            debugger.showDebugMessage("HeadingIC", ll_loc, "Heading Info - Appending ID and Title...");
            _handle.append(uncleanSGML.substring(offset, l_loc));
            
           if (!hideID) {
                _handle.append(sectPrefix + " id=\"" + sectID + "\"><title>" + sectTitle + "</title>");
            } else {
                _handle.append(sectPrefix + "><title>" + sectTitle + "</title>");
            }

            offset = r_loc;
        }
        debugger.showDebugMessage("HeadingIC", uncleanSGML.length(), "Heading Processing Done");
        uncleanSGML = _handle.toString();

    }
}
