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
package org.wits.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.wits.WITSProperties;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class WITSFileReader {

    private String _f = null;
    private WITSProperties props = null;
    private boolean inTable = false;
    private boolean orphanedTable = false;

    /**
     *
     * @param _f
     */
    public WITSFileReader(String _f, WITSProperties props) {
        this._f = _f;
        this.props = props;
    }
    private static int cleanLBLimit = -1;

    /**
     *
     * @param source
     * @param pattern
     * @param replace
     * @param startAt
     * @return
     */
    public String replace(String source, String pattern, String replace, int startAt) {

        if (source != null) {
            final int len = pattern.length();
            StringBuilder sb = new StringBuilder();
            int found = -1;
            int start = startAt;

            while ((found = source.indexOf(pattern, start)) != -1) {

                sb.append(source.substring(start, found));
                sb.append(replace);
                start = found + len;
            }

            sb.append(source.substring(start));
            //debugger.showDebugMessage("PatternMatchingIC", startAt, "Pattern Matching...Done");


            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     *
     * @param textBlock
     * @return
     */
    public String processInputText(String textBlock) {
        FileWriter writer = null;

        String processedBlock = textBlock;
        //processedBlock = replace(processedBlock, "<", "&lt;", 0);
        //processedBlock = replace(processedBlock, ">", "&gt;", 0);
        //processedBlock = processedBlock.trim();
        //processedBlock = replace(textBlock, "\r\n", "<LB>\r\n", 0);
        //processedBlock = processedBlock + "<LB>\r\n";
        File tFile = new File("tfile.txt");

        try {
            writer = new FileWriter(tFile);

            writer.write(processedBlock);
            writer.close();
        } catch (IOException ex) {
        }

        _f = tFile.getAbsolutePath();

        return readFile();


    }

    private String quickClean(String str) {
        str = replace(str, "&ndash;", "-", 0);
        str = replace(str, "&mdash;", "--", 0);
        str = replace(str, "&nbsp;", " ", 0);
        str = replace(str, "&", "&amp;", 0);
        str = str.trim();

        return str;
    }

    private int findCellCount(String str) {
        char[] strArray = str.toCharArray();

        int count = 0;
        int linkHitCount = 0;

        for (int i = 0; i < strArray.length; i++) {
            char c = strArray[i];
            if (c == '[') {
                linkHitCount++;
            }
            if (c == '|') {
                count++;
            }
        }
        return count - linkHitCount;
    }

    private String AIBreakLine(String str) {
        str = AIBreakLineHelper(str, "{noformat");
        str = AIBreakLineHelper(str, "{info");
        str = AIBreakLineHelper(str, "{warning");
        str = AIBreakLineHelper(str, "{note");
        str = AIBreakLineHelper(str, "{tip");
        return str;
    }

    private String AIBreakLineHelper(String str, String pattern) {
        int l_loc = str.indexOf(pattern);

        if (l_loc == -1) {
            return str;
        }
        StringBuilder _handle = new StringBuilder();
        _handle.append(str.substring(0, l_loc));
        //_handle.append("<LB>\r\n");
        _handle.append(str.substring(l_loc, str.length()));

        return _handle.toString();
    }

    private String LBCheck(String body, String str) {
        if (str.startsWith("|") && str.endsWith("|") && inTable) {
            inTable = true;
            orphanedTable = false;
        }
        if (!str.startsWith("|") && !str.endsWith("|") && !inTable) {
            inTable = false;
            orphanedTable = false;
            //System.out.println("Breaking at: " + str);
            if (!str.startsWith("{")) {
                //body += "<LB>\r\n";
            }
        }
        if (!str.startsWith("|") && !str.endsWith("|") && inTable) {
            inTable = false;
            orphanedTable = false;
            if (!str.startsWith("{")) {
                //body += "<LB>\r\n";
            }
        }
        if (str.startsWith("|") && str.endsWith("|") && !inTable) {
            inTable = true;
            orphanedTable = true;
        //System.out.println("Breaking at: " + str);
        //body += "<LB>\r\n";
        }

        return body;
    }

    /**
     *
     * @return
     */
    public String readFile() {

        File file = new File(_f);
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String body = new String("<LB>\r\n");
            String str = new String();



            int lbTol = 0;
            boolean listStarted = false;

            while ((str = in.readLine()) != null) {

                str = str.trim();


                if (str.indexOf("*Note:*") != -1) {
                    str = replace(str, "*Note:*", "Note - ", 0);
                }
                if (str.indexOf("*Tip:*") != -1) {
                    str = replace(str, "*Tip:*", "Tip - ", 0);
                }
                if (str.indexOf("*Caution:*") != -1) {
                    str = replace(str, "*Caution:*", "Caution - ", 0);
                }

                //protect wiki markups from causal literal texts
                if (str.indexOf("{{code}}") != -1) {
                    str = replace(str, "{{code}}", "{{ code }}", 0);
                }
                if (str.indexOf("{{info}}") != -1) {
                    str = replace(str, "{{info}}", "{{ info }}", 0);
                }
                if (str.indexOf("{{tip}}") != -1) {
                    str = replace(str, "{{tip}}", "{{ tip }}", 0);
                }
                if (str.indexOf("{{note}}") != -1) {
                    str = replace(str, "{{note}}", "{{ note }}", 0);
                }
                if (str.indexOf("{{noformat}}") != -1) {
                    str = replace(str, "{{noformat}}", "{{ noformat }}", 0);
                }

                //only for conf handle table cells
                //fix *some emphasised text* only confluence
                if (str.startsWith("|") && !str.endsWith("|") && !str.endsWith("||")) {
                    str = str + "|";
                //System.out.println("STRING:"+str);
                }


                //check escaped []
                if (str.indexOf("\\[") != -1) {
                    str = replace(str, "\\[", "(", 0);
                }
                if (str.indexOf("\\]") != -1) {
                    str = replace(str, "\\]", ")", 0);
                }


                //simple fix to a bug that causes
                //docbook to fail when notes and screens are inline and not block
                //but do not touch the lists.                
                if (!str.trim().startsWith("*") && !str.trim().startsWith("#") && !str.trim().startsWith("|")) {
                    str = replace(str, "{info", "<LB>\r\n{info", 0);
                    str = replace(str, "{warning", "<LB>\r\n{warning", 0);
                    str = replace(str, "{note", "<LB>\r\n{note", 0);
                    str = replace(str, "{tip", "<LB>\r\n{tip", 0);
                    str = replace(str, "{noformat}", "<LB>\r\n{noformat}", 0);
                //str = replace(str, "{code}", "<LB>\r\n{code}", 0);
                }
                if (str.indexOf("*+[") != -1 && str.indexOf("]+*") != -1) {
                    str = replace(str, "*+[", "[", 0);
                    str = replace(str, "]+*", "]", 0);
                }
                //Maybe HR. Ignore this line
                if (str.equals("----")) {
                    continue;
                }
                if (str.indexOf("{section") != -1) {
                    continue;
                }
                if (str.indexOf("{column") != -1) {
                    continue;
                }
                if (str.indexOf("{livesearch") != -1) {
                    continue;
                }
                if (str.indexOf("{panel") != -1) {
                    continue;
                }
                if (str.toLowerCase().indexOf("table of content") != -1) {
                    continue;
                }


                //no emphasizing links
                if (str.indexOf("_[") != -1 && str.indexOf("]_") != -1) {
                    str = replace(str, "_[", "[", 0);
                    str = replace(str, "]_", "]", 0);
                }
                if (str.indexOf("*[") != -1 && str.indexOf("]*") != -1) {
                    str = replace(str, "*[", "[", 0);
                    str = replace(str, "]*", "]", 0);
                }
                //fix *some emphasised text* only confluence
                if (str.startsWith("*") && str.endsWith("*")) {
                    str = replace(str, "*", "_", 0);
                }
                //handle empty cells
                if (str.indexOf("| |") != -1) {
                    str = replace(str, "| |", "|-|", 0);
                }
                if (str.indexOf("|&nbsp;|") != -1) {
                    str = replace(str, "|&nbsp;|", "|-|", 0);
                }
                //handle case {{[ldapmodify]}}
                if (str.indexOf("{{[") != -1 && str.indexOf("]}}") != -1) {
                    str = replace(str, "{{[", "{{", 0);
                    str = replace(str, "]}}", "}}", 0);
                }
                //force LBs before markups
                if (str.startsWith("h7.") || str.startsWith("h6.") || str.startsWith("||") || str.startsWith("h4.") || str.startsWith("h5.") || str.startsWith("h1.") || str.startsWith("h2.") || str.startsWith("h3.")) {
                    body += "<LB>\r\n";
                }
                //BUG Handle FB or Lists sep.
                if (str.startsWith("#") || str.startsWith("*")) {
                    if (!listStarted) {
                        //not in list first item
                        //body += "<LB>\r\n";
                        listStarted = true;
                    } else {
                        listStarted = true;
                    }
                } else {
                    listStarted = false;
                }


                //str = AIBreakLine(str);
                //Handle special table formats like
                //||col1|col2
                str = replace(str,"||","$ROWM",0);
                str = replace(str,"|","$COLM",0);

                if (str.startsWith("$ROWM") && str.indexOf("$COLM") != -1) {
                    str = replace(str, "$ROWM", "$COLM", 0);
                }

                str = replace(str,"$ROWM","||",0);
                str = replace(str,"$COLM","|",0);

                body = LBCheck(body, str);

                if (str.startsWith("||") && str.endsWith("||")) {
                    inTable = true;
                    orphanedTable = false;
                    str = quickClean(str);
                    body += str;
                    body += "<LB>\r\n";
                    str = in.readLine();
                    str = str.trim();
                    body = LBCheck(body, str);
                }

                //This is a bug fix to handle no heading table.
                str = str.trim();

                if (str.startsWith("|") && str.endsWith("|") && orphanedTable) {
                    //count the no. of cells here.
                    int count = 0;
                    inTable = true;

                    count = findCellCount(str);
                    char[] strArray = str.toCharArray();

                    body += "<LB>\r\n";
                    for (int i = 0; i < count; i++) {
                        body += "|| ";
                    }
                    //System.out.println("---------------------"+body);
                    body += "<LB>\r\n";
                }

                if (cleanLBLimit != -1) {
                    if (str.equals("")) {
                        //System.out.println("BLANK LINE:"+lbTol);

                        if (lbTol == cleanLBLimit) {
                            continue;
                        } else {
                            lbTol++;
                        }
                    } else {
                        lbTol = 0;
                    }

                }
                //clean the string
                //System.out.println("CLEANING");
                //str = replace(str, "<", "&lt;", 0);
                //str = replace(str, ">", "&gt;", 0);
                str = quickClean(str);

                body += str;
                body += "<LB>\r\n";

            }
            in.close();
            body = body + "<LB>\r\n";
            //System.out.println(body);
            return body;

        } catch (Exception ex) {
            writeParserErrorOutput("Error while reading from the input file.\r\n" + ex.getMessage());
            System.out.println(props.WITS_ParseErrorMessage);
            System.exit(0);
        }
        return null;
    }

    private static void writeParserErrorOutput(String toString) {
        FileWriter writer;
        try {
            writer = new FileWriter("witserror.txt");
            writer.write(toString);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
        }

    }
}
