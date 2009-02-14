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

/**
 *
 * @author FJ
 */
public class WITSFileReader {

    private String _f = null;
    private boolean inTable = false;
    private boolean orphanedTable = false;

    /**
     *
     * @param _f
     */
    public WITSFileReader(String _f) {
        this._f = _f;
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
            boolean inTable = false;
            boolean orphanedTable = true;

            while ((str = in.readLine()) != null) {
                str = str.trim();
                //simple fix to a bug that causes
                //docbook to fail when notes and screens are inline and not block
                //but do not touch the lists.                
                if (!str.startsWith("*") && !str.trim().startsWith("#")) {
                    str = replace(str, "{{{", "<LB>\r\n{{{", 0);
                }
                //check escaped []
                if (str.indexOf("\\[") != -1) {
                    str = replace(str, "\\[", "(", 0);
                }
                if (str.indexOf("\\]") != -1) {
                    str = replace(str, "\\]", ")", 0);
                }
                if (str.indexOf("[[{") != -1) {
                    str = replace(str, "[[{", "[{", 0);
                }
                if (str.indexOf("{{[[") != -1) {
                    str = replace(str, "{{[[", "{{[", 0);
                }

                //check for content between %% and %%
                if (str.indexOf("%%") != -1) {
                    if(str.equals("%%")){
                        continue;
                    }
                    if(str.startsWith("%%") && str.endsWith(")") && str.indexOf("(") != -1){
                        continue;
                    }
                    if(str.startsWith("%%") && str.endsWith("%%")){
                        continue;
                    }
                }

                //Maybe HR. Ignore this line
                if (str.trim().equals("----")) {
                    continue;
                }

                //force LBs before markups
                if (str.startsWith("!")) {
                    body += "<LB>\r\n";
                }

                if (str.indexOf("*+[") != -1 && str.indexOf("]+*") != -1) {
                    str = replace(str, "*+[", "[", 0);
                    str = replace(str, "]+*", "]", 0);
                }

                if (str.toLowerCase().indexOf("table of content") != -1) {
                    continue;
                }
                //no emphasizing links
                if (str.indexOf("''[") != -1 && str.indexOf("]''") != -1) {
                    str = replace(str, "''[", "[", 0);
                    str = replace(str, "]''", "]", 0);
                }
                if (str.indexOf("__[") != -1 && str.indexOf("]__") != -1) {
                    str = replace(str, "__[", "[", 0);
                    str = replace(str, "]__", "]", 0);
                }

                //handle empty cells
                if (str.indexOf("| |") != -1) {
                    str = replace(str, "| |", "|-|", 0);
                }
                if (str.indexOf("|&nbsp;|") != -1) {
                    str = replace(str, "|&nbsp;|", "|-|", 0);
                }
                //handle case {{[ldapmodify]}}
                //if (str.indexOf("{{[") != -1 && str.indexOf("]}}") != -1) {
                    //str = replace(str, "{{[", "{{", 0);
                    //str = replace(str, "]}}", "}}", 0);
                //}

                //force LBs before markups
                if (str.trim().startsWith("||") || str.trim().startsWith("!")) {
                    body += "<LB>\r\n";
                }

                str = str.trim();
                body = LBCheck(body, str);

                //Handle special table formats like
                //||col1|col2                
                str = replace(str,"||","$ROWM",0);
                str = replace(str,"|","$COLM",0);

                if (str.startsWith("$ROWM") && str.indexOf("$COLM") != -1) {
                    str = replace(str, "$ROWM", "$COLM", 0);
                }

                str = replace(str,"$ROWM","||",0);
                str = replace(str,"$COLM","|",0);

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
                    //System.out.println("--------------------------"+body);

                    body += "<LB>\r\n";
                }

                if (cleanLBLimit != -1) {
                    if (str.trim().equals("")) {
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
            return body;

        } catch (Exception ex) {
            //ex.printStackTrace();
            writeParserErrorOutput("Error while reading from the input file.\r\n" + ex.getMessage());
            System.out.println(WITSProperties.WITS_ParseErrorMessage);
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
