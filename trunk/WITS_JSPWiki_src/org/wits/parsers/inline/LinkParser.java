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
import java.util.StringTokenizer;
import org.wits.WITSInstance;
import org.wits.WITSProperties;
import org.wits.parsers.WITSParser;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class LinkParser implements WITSParser{

    private String uncleanSGML = null;
    private WITSDebugger debugger = null;
    private WITSProperties props = null;
    private WITSInstance witsInstance = null;

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
    public LinkParser(WITSInstance witsInstance, String uncleanSGML, WITSProperties props) {
        this.witsInstance = witsInstance;
        this.uncleanSGML = uncleanSGML;
        this.props = props;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("LinkIC", 0, "LinkIC Invoked.");

        int offset = 0;
        StringBuilder _handle = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf("[", offset);
            int r_loc = uncleanSGML.indexOf("]", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            //In JSPWiki [[ should not be processed as link.

            char nextChar = uncleanSGML.charAt(l_loc + 1);
            if (nextChar == '[') {
                _handle.append(uncleanSGML.substring(offset, l_loc + 2));
                offset = l_loc + 2;
                continue;
            }

            String linkText = uncleanSGML.substring(l_loc + 1, r_loc);
            
            //honour escape char
            char prevChar = uncleanSGML.charAt(l_loc - 1);
            if (prevChar == '\\') {
                _handle.append(uncleanSGML.substring(offset, r_loc));
                offset = r_loc;
                continue;
            }
            
            //if the link text contains escape chars...do not link
            if(linkText.indexOf("\\") != -1  || linkText.indexOf("[") != -1){
               _handle.append(uncleanSGML.substring(offset, r_loc));
               offset = r_loc;
                continue;
            }
            //Refuse to handle any links that got literal elements {{}}
            //if the link text contains escape chars...do not link
            if (linkText.indexOf("<noparse>") != -1 || linkText.indexOf("<noparsi>") != -1) {
                _handle.append(uncleanSGML.substring(offset, r_loc));
                offset = r_loc;
                continue;
            }
            
            //we don't support internal page links since we don't have the page reference.
            //System.out.println("LTEXT:"+linkText);
            
            if(linkText.trim().startsWith("#")){
                _handle.append(uncleanSGML.substring(offset, l_loc-1));
                _handle.append(linkText.substring(1, linkText.length()));
                offset = r_loc+1;
                continue;
            }
            //if (linkText.contains("#") || linkText.contains("|")) {
            if (!linkText.contains("<LB>")) {
                //Could be a link?

                //proceed with the link

                //System.out.println("LINK:" + linkText);
                boolean hasLabel = false;
                String linkLabel = null;
                String linkRef = null;

                //System.out.println("LINKTEXT:"+linkText);

                //handle special case where the linkText ends with |
                if(linkText.endsWith("|")){
                    linkText = linkText + linkText.substring(0, linkText.length()-1);
                }


                if (linkText.indexOf("|") != -1) {
                    //has label and url
                    StringTokenizer stok = new StringTokenizer(linkText, "|");
                    linkLabel = stok.nextToken();
                    linkRef = stok.nextToken();
                    if (linkRef.indexOf("http:") == -1) {
                        if (props.WITS_WIKISiteBaseURL.endsWith("/")) {
                            linkRef = props.WITS_WIKISiteBaseURL + linkRef;
                        } else {
                            linkRef = props.WITS_WIKISiteBaseURL + "/" + linkRef;
                        }
                    }
                    hasLabel = true;
                } else {
                    //only ref
                    linkLabel = linkText;
                    //Add site base URL if it is already not an URL
                    if (linkText.indexOf("http:") == -1) {
                        if (props.WITS_WIKISiteBaseURL.endsWith("/")) {
                            linkRef = props.WITS_WIKISiteBaseURL + linkText;
                        } else {
                            linkRef = props.WITS_WIKISiteBaseURL + "/" + linkText;
                        }
                    } else {
                        linkRef = linkText;
                    }
                }

                //TODO. CLEAN # in linkRef
                StringHandler handler = new StringHandler();
                handler.setDebugger(debugger);
                linkRef = handler.replace(linkRef, "#", "%%%%%");

               // int sloc_left = linkRef.indexOf("#");

                //if (sloc_left != -1) {
                    //#in the link
                    //linkRef = (linkRef.substring(0, sloc_left)) + (linkRef.substring(sloc_left + 1, linkRef.length()));

                //}


                _handle.append(uncleanSGML.substring(offset, l_loc));
                
                //clean href
                StringHandler handle = new StringHandler();
                handle.setDebugger(debugger);
                linkRef=linkRef.trim();
                linkRef = handle.replace(linkRef, " ", "");

                if (witsInstance.getOutputType().equals("solbook")) {
                    _handle.append(" <ulink url=\"" + linkRef + "\" type=\"text\">");
                }
                if (witsInstance.getOutputType().equals("docbook")) {
                    _handle.append(" <link xlink:href=\"" + linkRef + "\">");
                }

                if (hasLabel) {
                    _handle.append(linkLabel);
                } else {
                    _handle.append(linkRef);
                }
                if (witsInstance.getOutputType().equals("solbook")) {
                    _handle.append("</ulink>");
                }
                if (witsInstance.getOutputType().equals("docbook")) {
                    _handle.append("</link>");
                }

            } else {
                //suspicious...ignore
                debugger.showDebugMessage("LinkIC", l_loc, "Ignoring link.");
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            offset = r_loc + 1;
        }

        uncleanSGML =
                _handle.toString();
        //remove all tags inside ulink body

        uncleanSGML =
                removeOtherTags(uncleanSGML);

        return uncleanSGML;
    }

    private String removeOtherTags(String uncleanSGML) {

        int offset = 0;
        StringBuilder _handler = new StringBuilder();
        while (true) {
            int l_loc = uncleanSGML.indexOf("<ulink", offset);
            int r_loc = uncleanSGML.indexOf("</ulink>", l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handler.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;

            }



            String linkString = uncleanSGML.substring(l_loc, r_loc);
            StringHandler handle = new StringHandler();
            handle.setDebugger(debugger);
            debugger.showDebugMessage("LinkIC", l_loc, "Cleaning ULink content.");

            
            linkString =
                    handle.replace(linkString, "<emphasis>", "");
            linkString =
                    handle.replace(linkString, "</emphasis>", "");
            linkString =
                    handle.replace(linkString, "</literal>", "");
            linkString =
                    handle.replace(linkString, "<literal>", "");
            linkString =
                    handle.replace(linkString, "</para>", "");
            linkString =
                    handle.replace(linkString, "<para>", "");
            linkString =
                    handle.replace(linkString, "<noparsi>", "");
            linkString =
                    handle.replace(linkString, "</noparsi>", "");
            linkString =
                    handle.replace(linkString, "<emphasis role=\"strong\">", "");

            _handler.append(uncleanSGML.subSequence(offset, l_loc));
            _handler.append(linkString);
            offset =
                    r_loc;
        }

        return _handler.toString();
    }
}
