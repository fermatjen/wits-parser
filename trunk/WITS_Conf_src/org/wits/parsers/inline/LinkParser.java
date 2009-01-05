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
    private boolean isDocBookOutput = false;

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
    public LinkParser(boolean isDocBookOutput, String uncleanSGML, WITSProperties props) {
        this.isDocBookOutput = isDocBookOutput;
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



            String linkText = uncleanSGML.substring(l_loc + 1, r_loc);
            //System.out.println("LINK:"+linkText);

            //honour escape char
            char prevChar = uncleanSGML.charAt(l_loc - 1);
            if (prevChar == '\\') {
                _handle.append(uncleanSGML.substring(offset, r_loc));
                offset = r_loc;
                continue;
            }

            //if the link text contains escape chars...do not link
            if (linkText.indexOf("\\") != -1 || linkText.indexOf("[") != -1) {
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

            if (linkText.trim().startsWith("#")) {
                _handle.append(uncleanSGML.substring(offset, l_loc - 1));
                _handle.append(linkText.substring(1, linkText.length()));
                offset = r_loc + 1;
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




                if (linkText.indexOf("|") != -1) {
                    //has label
                    StringTokenizer stok = new StringTokenizer(linkText, "|");
                    linkLabel = stok.nextToken();
                    linkRef = stok.nextToken();
                    //System.out.println("LINKREF:"+linkRef);

                    //strip space name
                    //confluence specific link processing
                    boolean hasSpaceName = false;
                    if (linkRef.indexOf("http:") == -1) {
                        if (linkRef.indexOf(":") != -1) {
                            //space name is in the URL. remove it as it will come from the base URL.
                            hasSpaceName = true;
                        }
                    }
                    if (hasSpaceName) {
                        //System.out.println("" + linkRef);
                        StringTokenizer stok1 = new StringTokenizer(linkRef, ":");
                        stok1.nextToken();
                        linkRef = stok1.nextToken();
                    }
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

                    //confluence specific link processing
                    boolean hasSpaceName = false;
                    if (linkText.indexOf("http:") == -1) {
                        if (linkText.indexOf(":") != -1) {
                            //space name is in the URL. remove it as it will come from the base URL.
                            hasSpaceName = true;
                        }
                    }
                    //strip space name
                    if (hasSpaceName) {
                        StringTokenizer stok1 = new StringTokenizer(linkText, ":");
                        stok1.nextToken();
                        linkText = stok1.nextToken();
                    }

                    //Add site base URL
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

                //int sloc_left = linkRef.indexOf("#");

                //if (sloc_left != -1) {
                //#in the link
                //linkRef = (linkRef.substring(0, sloc_left)) + (linkRef.substring(sloc_left + 1, linkRef.length()));

                //}

                _handle.append(uncleanSGML.substring(offset, l_loc));

                //clean href
                StringHandler handle = new StringHandler();
                handle.setDebugger(debugger);
                linkRef = linkRef.trim();
                //Replace portions of the link ref before # to +
                //and after # to nochar.

                if (linkRef.indexOf("%%%%%") != -1) {
                    StringTokenizer stok = new StringTokenizer(linkRef, "%%%%%");
                    String part1 = stok.nextToken();
                    String part2 = stok.nextToken();

                    part1 = handle.replace(part1, " ", "+");
                    part2 = handle.replace(part2, " ", "");
                    linkRef = part1 + "%%%%%" + part2;
                } else {
                    linkRef = handle.replace(linkRef, " ", "+");
                }

                if (!isDocBookOutput) {
                    _handle.append(" <ulink url=\"" + linkRef + "\" type=\"text\">");
                } else {
                    _handle.append(" <link xlink:href=\"" + linkRef + "\">");
                }

                if (hasLabel) {
                    _handle.append(linkLabel);
                } else {
                    _handle.append(linkRef);
                }
                if (!isDocBookOutput) {
                    _handle.append("</ulink>");
                } else {
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
        uncleanSGML = _handle.toString();
        //remove all tags inside ulink body

        if (!isDocBookOutput) {
            uncleanSGML = removeOtherTags(uncleanSGML, "<ulink", "</ulink>");
        } else {
            uncleanSGML = removeOtherTags(uncleanSGML, "<link", "</link>");
        }

        return uncleanSGML;
    }

    private String removeOtherTags(String uncleanSGML, String prefix, String suffix) {

        int offset = 0;
        StringBuilder _handler = new StringBuilder();

        while (true) {
            int l_loc = uncleanSGML.indexOf(prefix, offset);
            int r_loc = uncleanSGML.indexOf(suffix, l_loc);

            if (l_loc == -1 || r_loc == -1) {
                _handler.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }

            String linkString = uncleanSGML.substring(l_loc, r_loc);
            StringHandler handle = new StringHandler();
            handle.setDebugger(debugger);
            debugger.showDebugMessage("LinkIC", l_loc, "Cleaning ULink content.");


            linkString = handle.replace(linkString, "<emphasis>", "");
            linkString = handle.replace(linkString, "</emphasis>", "");
            linkString = handle.replace(linkString, "</literal>", "");
            linkString = handle.replace(linkString, "<literal>", "");
            linkString = handle.replace(linkString, "</para>", "");
            linkString = handle.replace(linkString, "<para>", "");
            linkString = handle.replace(linkString, "<noparsi>", "");
            linkString = handle.replace(linkString, "</noparsi>", "");
            linkString = handle.replace(linkString, "<emphasis role=\"strong\">", "");

            _handler.append(uncleanSGML.subSequence(offset, l_loc));
            _handler.append(linkString);
            offset = r_loc;
        }

        return _handler.toString();
    }
}
