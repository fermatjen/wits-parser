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

package org.wits.cloud;

import java.util.HashMap;
import org.wits.debugger.WITSDebugger;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class OvercastParser {

    private String uncleanSGML = null;
    private HashMap <String, String> overcastMap = null;
    private int overcastID = 1;
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
     */
    public OvercastParser() {
    }

    private String cleanLBs(String content) {
        //System.out.println("To clean:"+ content);
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);

        content = handler.replace(content, "&lt;LB>\r\n&lt;LB>\r\n", "&lt;LB>\r\n", 0);
        //System.out.println("Clean:"+ content);

        return content;
    }

    private String forceLBs(String content){
        //Force a LB at 50th char.
        int maxPos = 50;
        int maxTolLimit = 15;

        int LBIndex = content.indexOf("&lt;LB>",1);

        if(LBIndex <= maxPos){
            return content;
        }
        else{
            maxTolLimit = LBIndex - maxPos;
        }
        
        int length = content.length();

        //System.out.println("S:"+content);

        if(length <= maxPos){
            return content;
        }

        int breakAt = maxPos;

        breakAt = content.indexOf(" ",breakAt);
        int tbreakAt = breakAt;

        if(breakAt >= maxPos + maxTolLimit){
            breakAt = maxPos;
        }
        else{
            breakAt = tbreakAt;
        }

        if(breakAt == -1){
            breakAt = maxPos;
        }
        //System.out.println(breakAt+":"+length);

        return content.substring(0, breakAt)+"&lt;LB>\r\n"+content.substring(breakAt+1, length);
    }

    /**
     *
     * @param uncleanSGML
     * @return
     */
    public String getOvercastContent(String uncleanSGML) {

        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("<noparse>", offset);
            int r_loc = uncleanSGML.indexOf("</noparse>", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            //System.out.println("NP:" + l_loc + ":" + r_loc);

            //System.out.println("CONTRO STRING:"+uncleanSGML.substring(l_loc, r_loc));

            String noparseID = uncleanSGML.substring(l_loc + 9, r_loc);
            //System.out.println("NOPARSEID:" + noparseID);
            String overcastText = (String) overcastMap.get(noparseID);
            //System.out.println("NOPARSETEXT:" + overcastText);
            if (overcastText.endsWith("<LB>")) {
                overcastText = overcastText.substring(0, overcastText.length() - 4);
            }
            _handle.append(uncleanSGML.substring(offset, l_loc));

            //We need to clean the overcast text to remove <
            StringHandler handler = new StringHandler();
            handler.setDebugger(debugger);

            debugger.showDebugMessage("PostProcessor", uncleanSGML.length(), "Removing Special Chars in Code.");

            overcastText = handler.replace(overcastText, "<", "&lt;", 0);

            _handle.append("<LB>\r\n<screen>");
            
            overcastText = forceLBs(overcastText);
            overcastText = cleanLBs(overcastText);
            
            _handle.append(overcastText);
            _handle.append("</screen><LB>\r\n");

            offset = r_loc + 10;
        }
        uncleanSGML = _handle.toString();

        offset = 0;
        _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("<noparsi>", offset);
            int r_loc = uncleanSGML.indexOf("</noparsi>", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            //System.out.println("NP:" + l_loc + ":" + r_loc);

            //System.out.println("CONTRO STRING:"+uncleanSGML.substring(l_loc, r_loc));

            String noparseID = uncleanSGML.substring(l_loc + 9, r_loc);
            //System.out.println("NOPARSEID:" + noparseID);
            String overcastText = (String) overcastMap.get(noparseID);
            //System.out.println("NOPARSETEXT:" + overcastText);
            if (overcastText.endsWith("<LB>")) {
                overcastText = overcastText.substring(0, overcastText.length() - 4);
            }
            _handle.append(uncleanSGML.substring(offset, l_loc));

            //We need to clean the overcast text to remove <
            StringHandler handler = new StringHandler();
            handler.setDebugger(debugger);

            debugger.showDebugMessage("PostProcessor", uncleanSGML.length(), "Removing Special Chars in Code.");

            overcastText = handler.replace(overcastText, "<", "&lt;", 0);
            
            //System.out.println("NOPARSETEXT:" + overcastText);
            
            //if (overcastText.indexOf("&lt;LB>\r\n") != -1) {
                //maybe screen?
                //_handle.append("<screen>");
                //overcastText = cleanLBs(overcastText);
                //_handle.append(overcastText);
                //_handle.append("</screen>");
            //} else {
                _handle.append("<literal>");
                overcastText = cleanLBs(overcastText);
                _handle.append(overcastText);
                _handle.append("</literal>");
            //}

            offset = r_loc + 10;
        }
        uncleanSGML = _handle.toString();
        uncleanSGML = getNoParseTextForLink(uncleanSGML);

        return uncleanSGML;
    }

    /**
     *
     * @return
     */
    public HashMap getOvercastMap() {
        return overcastMap;
    }

    /**
     *
     * @param overCastMap
     */
    public void setOvercastMap(HashMap <String, String> overCastMap) {
        this.overcastMap = overCastMap;
    }

    /**
     *
     * @param uncleanSGML
     */
    public OvercastParser(String uncleanSGML) {
        this.uncleanSGML = uncleanSGML;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {

        debugger.addLineBreak();
        debugger.showDebugMessage("OvercastIC", 0, "OvercastIC Invoked.");

        //Initialize overcast map
        overcastMap = new HashMap <String, String>();

        StringBuilder _handle = new StringBuilder();
        int offset = 0;

        while (true) {
            //check for code occurence
            int l_loc = uncleanSGML.indexOf("{code", offset);
            int r_loc = uncleanSGML.indexOf("{code}", l_loc + 1);

            //System.out.println(l_loc + ":" + r_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            //code present
            debugger.showDebugMessage("OvercastIC", l_loc, "Marking {CODE} text for 'NOPARSE.");
            int l_loc_dup = uncleanSGML.indexOf("}", l_loc);

            String overcastText = uncleanSGML.substring(l_loc_dup + 1, r_loc);
            //System.out.println("Overcast:" + overcastText);

            String noparseID = "```" + overcastID + " ";
            overcastMap.put(noparseID, overcastText);

            //System.out.println("------------------```"+ overcastID+"-"+overcastText);

            //check if literal or plain code
            char _c1 = uncleanSGML.charAt(l_loc - 1);
            char _c2 = uncleanSGML.charAt(l_loc - 2);

            if (_c1 != '\n' && _c2 != '\r') {
                //literal text
                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append("<LB>\r\n");
                _handle.append("<noparse>```" + overcastID + " ");
                _handle.append("</noparse>");
                _handle.append("<LB>\r\n");
                overcastID++;
            } else {
                //could be a block?
                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append("<LB>\r\n");
                _handle.append("<noparse>```" + overcastID + " ");
                _handle.append("</noparse>");
                _handle.append("<LB>\r\n");
                overcastID++;
            }



            offset = r_loc + 6;
        }

        uncleanSGML = _handle.toString();

        _handle = new StringBuilder();
        offset = 0;

        while (true) {
            //check for code occurence
            int l_loc = uncleanSGML.indexOf("{noformat", offset + 1);
            int r_loc = uncleanSGML.indexOf("{noformat}", l_loc + 9);

            //System.out.println(l_loc + ":" + r_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            //code present
            debugger.showDebugMessage("OvercastIC", l_loc, "Marking {NOFORMAT} text for 'NOPARSE.");
            int l_loc_dup = uncleanSGML.indexOf("}", l_loc);

            String overcastText = uncleanSGML.substring(l_loc_dup + 1, r_loc);
            //System.out.println("Overcast:" + overcastText);

            String noparseID = "```" + overcastID + " ";
            overcastMap.put(noparseID, overcastText);

            //check if literal or plain code
            char _c1 = uncleanSGML.charAt(l_loc - 1);
            char _c2 = uncleanSGML.charAt(l_loc - 2);

            if (_c1 != '\n' && _c2 != '\r') {
                //literal text
                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append("<LB>\r\n<LB>\r\n");
                _handle.append("<noparse>```" + overcastID + " ");
                _handle.append("</noparse>");
                _handle.append("<LB>\r\n<LB>\r\n");
                overcastID++;
            } else {
                //could be a block?
                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append("<LB>\r\n<LB>\r\n");
                _handle.append("<noparse>```" + overcastID + " ");
                _handle.append("</noparse>");
                _handle.append("<LB>\r\n<LB>\r\n");
                overcastID++;
            }


            offset = r_loc + 10;
        }

        uncleanSGML = _handle.toString();


        _handle = new StringBuilder();
        offset = 0;

        while (true) {
            //check for code occurence
            int l_loc = uncleanSGML.indexOf("{{", offset + 1);
            int r_loc = uncleanSGML.indexOf("}}", l_loc + 1);

            //System.out.println(l_loc + ":" + r_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            //code present
            debugger.showDebugMessage("OvercastIC", l_loc, "Marking {NOFORMAT} text for 'Literal Block.");
            // int l_loc_dup = uncleanSGML.indexOf("}<LB>", l_loc);

            String overcastText = uncleanSGML.substring(l_loc + 2, r_loc);
            //System.out.println("Overcast:" + overcastText);

            String noparseID = "```" + overcastID + " ";
            overcastMap.put(noparseID, overcastText);

            //System.out.println("------------------```"+ overcastID+"-"+overcastText);
            //check if literal or plain code
            char _c1 = uncleanSGML.charAt(l_loc - 1);
            char _c2 = uncleanSGML.charAt(l_loc - 2);

            char _c3 = uncleanSGML.charAt(r_loc + 1);

            if (_c1 != '\n' && _c2 != '\r') {
                //literal text
                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append("<noparsi>```" + overcastID + " ");
                _handle.append("</noparsi>");
                overcastID++;
            } else {
                
                    _handle.append(uncleanSGML.substring(offset, l_loc));
                    _handle.append("<noparsi>```" + overcastID + " ");
                    _handle.append("</noparsi>");
                    overcastID++;
                
            }

            offset = r_loc + 2;
        }

        uncleanSGML = _handle.toString();


        return uncleanSGML;
    }

    private String getNoParseTextForLink(String textBlock) {
        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = textBlock.indexOf("```", offset);
            int r_loc = textBlock.indexOf(" ", l_loc);
            int r_loc_dup = textBlock.indexOf("</", l_loc);

            //whichever is earlier
            if (r_loc_dup != -1) {
                if (r_loc_dup < r_loc && r_loc_dup > l_loc) {
                    r_loc = r_loc_dup;
                }
            }

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(textBlock.substring(offset, textBlock.length()));
                break;
            }

            String noParseID = textBlock.substring(l_loc, r_loc + 1);
            //System.out.println("NOPARSETEXT--:" + noParseID);
            String overcastText = (String) overcastMap.get(noParseID);

            if (overcastText != null) {
                if (overcastText.endsWith("<LB>")) {
                    overcastText = overcastText.substring(0, overcastText.length() - 4);
                }
            } else {
                overcastText = "";
            }

            _handle.append(textBlock.substring(offset, l_loc));
            _handle.append(overcastText);

            offset = r_loc;
        }

        return _handle.toString();
    }
}
