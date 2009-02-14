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

import org.wits.WITSProperties;
import org.wits.debugger.WITSDebugger;
import org.wits.parsers.WITSParser;
import org.wits.patterns.StringHandler;

/**
 *
 * @author FJ
 */
public class TableParser implements WITSParser{

    private String uncleanSGML = null;
    private boolean isDocBookOutput = false;
    private WITSDebugger debugger = null;
    private WITSProperties props = null;

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
    public TableParser(boolean isDocBookOutput, String uncleanSGML, WITSProperties props) {
        this.uncleanSGML = uncleanSGML;
        this.isDocBookOutput = isDocBookOutput;
        this.props = props;
    }

    /**
     *
     * @return
     */
    public String getProcessedText() {
        debugger.addLineBreak();
        debugger.showDebugMessage("TableIC", 0, "TableIC Invoked.");

        int offset = 0;
        StringBuilder _handle = new StringBuilder();
        //First retreive the Table block. Then process the entries

        while (true) {
            int l_loc = uncleanSGML.indexOf("<LB>\r\n||", offset);
            int r_loc = uncleanSGML.indexOf("<LB>\r\n<LB>", l_loc);

            if (l_loc == -1) {
                //No warnings
                _handle.append(uncleanSGML.substring(offset, uncleanSGML.length()));
                break;
            }
            //End of table

            String candidateTable = uncleanSGML.substring(l_loc + 6, r_loc);
            //System.out.println("TableEndMarkup:" + candidateTable);
            debugger.showDebugMessage("TableIC", l_loc, "Processing Table");

            debugger.showDebugMessage("TableIC", l_loc, "Detecting Table Title...");
            //detect title
            String tableTitle = new String();

            char _c1, _c2;

            int tpointer = l_loc;
            int tableTitleLoc = 0;

            // System.out.println("L_LOC:"+l_loc);

            if (l_loc != 0) {
                while (tpointer != 0) {
                    //System.out.println(uncleanSGML.length()+":"+tpointer);
                    _c1 = uncleanSGML.charAt(tpointer);
                    _c2 = uncleanSGML.charAt(tpointer - 1);

                    if (_c1 == '\n' && _c2 == '\r') {
                        tableTitleLoc = tpointer + 1;
                        break;
                    }
                    tpointer--;
                }
            }
            //System.out.println("TLOC:"+tableTitleLoc+":"+l_loc);
            //System.out.println("TABLE TITLE:"+uncleanSGML.substring(tableTitleLoc, l_loc));
            if (tableTitleLoc == l_loc) {
                //could not detect a title. Insert placeholder
                debugger.showDebugMessage("TableIC", l_loc, "Detecting Table Title..Done (No Title)..");
                tableTitle = props.WITS_DanglerTableTitle;
                String processedTableText = processTableMarkup(candidateTable, tableTitle);

                //clean notes inside cells
                StringHandler handler = new StringHandler();
                handler.setDebugger(debugger);
                processedTableText = handler.replace(processedTableText, "<note><para>", "", 0);
                processedTableText = handler.replace(processedTableText, "<caution><para>", "", 0);
                processedTableText = handler.replace(processedTableText, "<tip><para>", "", 0);
                processedTableText = handler.replace(processedTableText, "</para></tip>", "", 0);
                processedTableText = handler.replace(processedTableText, "</para></caution>", "", 0);
                processedTableText = handler.replace(processedTableText, "</para></note>", "", 0);


                _handle.append(uncleanSGML.substring(offset, l_loc));
                _handle.append(processedTableText);
            } else {
                debugger.showDebugMessage("TableIC", l_loc, "Detecting Table Title..Done.");
                tableTitle = uncleanSGML.substring(tableTitleLoc, l_loc);
                String processedTableText = processTableMarkup(candidateTable, tableTitle);

                //clean notes inside cells
                StringHandler handler = new StringHandler();
                handler.setDebugger(debugger);
                processedTableText = handler.replace(processedTableText, "<note><para>", "", 0);
                processedTableText = handler.replace(processedTableText, "<caution><para>", "", 0);
                processedTableText = handler.replace(processedTableText, "<tip><para>", "", 0);
                processedTableText = handler.replace(processedTableText, "</para></tip>", "", 0);
                processedTableText = handler.replace(processedTableText, "</para></caution>", "", 0);
                processedTableText = handler.replace(processedTableText, "</para></note>", "", 0);

                _handle.append(uncleanSGML.substring(offset, tableTitleLoc));
                _handle.append(processedTableText);
            }



            offset = r_loc;

        }

        debugger.showDebugMessage("TableIC", uncleanSGML.length(), "Updating Tree...Done.");
        uncleanSGML = _handle.toString();
        return uncleanSGML;
    }

    /**
     *
     * @param tableBlock
     * @param tableTitle
     * @return
     */
    public String processTableMarkup(String tableBlock, String tableTitle) {

        //Supporting confluence specific regression code
        //Add || to mark the end of the table header.
        //In confluence it is mandatory.
        //System.out.println("TBLOCK1:"+tableBlock);


        int marker_loc = tableBlock.indexOf("<LB>\r\n|");
        StringBuilder buffer = new StringBuilder();
        buffer.append(tableBlock.substring(0, marker_loc));
        buffer.append(" ||<LB>\r\n|");
        buffer.append(tableBlock.substring(marker_loc + 7, tableBlock.length()));
        buffer.append(" |<LB>\r\n");
        tableBlock = buffer.toString();

        //add table padding
        //tableBlock ="<!-- Start of Table-->\r\n"+tableBlock+"<LB>\r\n<LB>\r\n<!-- End of Table-->";

        //System.out.println("TBLOCK2:"+tableBlock);
        //first set the inner structure right        
        StringBuilder _handle = new StringBuilder();
        //System.out.println("TABLE:"+tableBlock);
        //detect the columns
        int cols = -1;
        int offset2 = 0;
        while (true) {
            int loc = tableBlock.indexOf("||", offset2);
            if (loc == -1) {
                break;
            }
            cols++;
            offset2 = loc + 1;
        }
        //System.out.println("COLS:" + cols);

        //retreive the table header
        int r_loc = tableBlock.indexOf("<LB>\r\n|", 0);

        if (r_loc == -1) {
            //something wrong with the markup
            //End table processing.
            return tableBlock;
        }

        //Build the header block
        String headBlock = tableBlock.substring(0, r_loc);
        //System.out.println("TABLE HEADER:" + headBlock);

        int offset = 0;

        StringBuilder cleanedHeadBlock = new StringBuilder();

        cleanedHeadBlock.append("<thead><row>");




        while (true) {

            int hl_loc = headBlock.indexOf("||", offset);
            int hr_loc = headBlock.indexOf("||", hl_loc + 2);

            if (hl_loc == -1 || hr_loc == -1) {
                break;
            }

            String headerName = headBlock.substring(hl_loc + 2, hr_loc);
            cleanedHeadBlock.append("<entry><para>");
            StringHandler handler = new StringHandler();
            handler.setDebugger(debugger);
            headerName = handler.replace(headerName, "<LB>\r\n", " ", 0);

            cleanedHeadBlock.append(headerName);
            cleanedHeadBlock.append("</para></entry>");
            offset = hr_loc;
        }

        cleanedHeadBlock.append("</row></thead>");
        // System.out.println("-----CLEANED HEAD BLOCK----\r\n"+cleanedHeadBlock.toString());
        debugger.showDebugMessage("TableIC", tableBlock.length(), "Processing Table Header..Done..");
        //Build the body block
        String bodyBlock = tableBlock.substring(r_loc + 6, tableBlock.length());

        offset = 0;
        StringBuilder cleanedBodyBlock = new StringBuilder();

        cleanedBodyBlock.append("<tbody><row>\r\n");

        int cellCount = 1;

        while (true) {

            int hl_loc = bodyBlock.indexOf("|", offset);
            int hr_loc = bodyBlock.indexOf("|", hl_loc + 1);

            if (hl_loc == -1 || hr_loc == -1) {
                break;
            }

            String colName = bodyBlock.substring(hl_loc + 1, hr_loc);
            //System.out.println("CCOUNT:"+cellCount+" COLS:"+cols);

            //Increment cellcount;
            cellCount++;
            boolean rowEndReached = false;

            if (cellCount > cols) {
                rowEndReached = true;
                cellCount = 0;
            }
            //System.out.println("\r\n\r\n"+colName);
            //close the record
            StringHandler handler = new StringHandler();
            handler.setDebugger(debugger);
            colName = handler.replace(colName, "<LB>\r\n", " ", 0);

            if (rowEndReached) {

                cleanedBodyBlock.append("<entry><para>");
                cleanedBodyBlock.append(colName);
                //System.out.println("COLNAME:" + colName);
                cleanedBodyBlock.append("</para></entry>");
                cleanedBodyBlock.append("</row><row>");
            } else {
                cleanedBodyBlock.append("<entry><para>");
                cleanedBodyBlock.append(colName);
                //System.out.println("COLNAME:" + colName);
                cleanedBodyBlock.append("</para></entry>");
            }
            offset = hr_loc;
        }
        cleanedBodyBlock.append("</row></tbody>");
        //System.out.println("-----CLEANED BODY BLOCK----\r\n" + cleanedBodyBlock.toString());
        debugger.showDebugMessage("TableIC", tableBlock.length(), "Processing Table Body...Done..");
        //Parsed table header and table data. Prepare table body

        StringBuilder cleanedTableBlock = new StringBuilder();
        cleanedTableBlock.append("\r\n<informaltable>\r\n");
        //cleanedTableBlock.append("<title>" + tableTitle + "</title>\r\n");
        cleanedTableBlock.append("<tgroup cols=\"" + cols + "\" colsep=\"0\" rowsep = \"0\">\r\n");
        //cleanedTableBlock.append("<?PubTbl tgroup dispwid=\"600.00px\">\r\n");

        //Nasty hack
        if (!isDocBookOutput) {
            for (int i = 0; i < cols; i++) {
                cleanedTableBlock.append("<colspec colwidth=\"1.00*\">");
            }
        }
        cleanedTableBlock.append(cleanedHeadBlock.toString() + "\r\n");
        cleanedTableBlock.append(cleanedBodyBlock.toString() + "\r\n");
        cleanedTableBlock.append("</tgroup></informaltable>\r\n");

        String ctableBlock = cleanedTableBlock.toString();
        //clean the empty last row
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);

        ctableBlock = handler.replace(ctableBlock, "<row></row>", "", 0);
        ctableBlock = handler.replace(ctableBlock, "<entry><para><LB>\r\n</para></entry>", "", 0);
        ctableBlock = handler.replace(ctableBlock, "<thead><row></row></thead>", "", 0);
        //System.out.println("CLEANED:"+cleanedTableBlock.toString());
        return ctableBlock;
    }
}
