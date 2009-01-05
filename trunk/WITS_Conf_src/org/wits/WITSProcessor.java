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

package org.wits;

/**
 *
 * @author FJ
 */
import org.wits.cloud.ExcludeParser;
import org.wits.reader.WITSFileReader;
import org.wits.patterns.StringHandler;
import org.wits.parsers.block.ListParser;
import org.wits.parsers.block.HeadingParser;
import org.wits.processors.PreProcessor;
import org.wits.processors.PostProcessor;
import org.wits.parsers.block.ParaParser;
import org.wits.parsers.inline.LiteralParser;
import org.wits.parsers.inline.LinkParser;
import org.wits.parsers.inline.EmphasisParser;
import org.wits.cloud.OvercastParser;
import org.wits.debugger.WITSDebugger;
import org.wits.parsers.block.BlockQuoteParser;
import org.wits.parsers.block.ColorParser;
import org.wits.parsers.block.PanelParser;
import org.wits.parsers.block.TableParser;
import org.wits.parsers.inline.ScriptParser;

/**
 *
 * @author FJ
 */
public class WITSProcessor {

    private boolean isDebuggingOn = false;
    private boolean isForceParsing = false;
    //Warning always on. User can't turn off.
    private boolean isWarningOn = true;
    private String inputFile = null;
    private String outputFile = null;
    private WITSDebugger debugger = null;
    private String inputText = null;
    private int witsID = 0;
    private WITSProperties props = null;
    private boolean hideID = false;

    /**
     *
     * @return
     */
    public int getWitsID() {
        return witsID;
    }

    /**
     *
     * @param witsID
     */
    public void setWitsID(int witsID) {
        this.witsID = witsID;
    }

    /**
     *
     * @return
     */
    public String getInputText() {
        return inputText;
    }

    /**
     *
     * @param inputText
     */
    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    /**
     *
     * @param isDebuggingOn
     * @param inputFile
     * @param outputFile
     */
    public WITSProcessor(boolean hideID, boolean isForceParsing, boolean isDebuggingOn, String inputFile, String outputFile, WITSProperties props) {
        this.hideID = hideID;
        this.isForceParsing = isForceParsing;
        this.isDebuggingOn = isDebuggingOn;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        debugger = new WITSDebugger(isDebuggingOn, isWarningOn);
        this.props = props;
    }

    /**
     *
     * @return
     */
    public WITSDebugger getDebugger() {
        return debugger;
    }

    /**
     *
     * @return
     */
    public String process() {

        String uncleanSGML = null;

        if (inputFile != null) {
            WITSFileReader reader = new WITSFileReader(inputFile, props);
            uncleanSGML = reader.readFile();

        } else {
            uncleanSGML = inputText;
        }
        StringHandler handler = new StringHandler();
        handler.setDebugger(debugger);
        //System.out.println("UNCLEAN:"+uncleanSGML);




        //run overcast parser before parsing the text.
        OvercastParser overParser = new OvercastParser(uncleanSGML);
        overParser.setDebugger(debugger);
        uncleanSGML = overParser.getProcessedText();
        //System.out.println("OVERCAST OP:\r\n"+uncleanSGML);

        //we support only confluence.
        if (!isForceParsing) {
            if (uncleanSGML.indexOf("<LB>\r\n!!") != -1) {
                //maybe jspwiki text
                //return "ERROR:Alien Wiki Format Detected. Only Confluence Markup is Supported!";
                return "ERROR:JSPWiki Text? " + WITSProperties.WITS_AllienBrandMessage;
            }
            if (uncleanSGML.indexOf("<LB>\r\n==") != -1) {
                //maybe mediawiki text
                //return "ERROR:Alien Wiki Format Detected. Only Confluence Markup is Supported!";
                return "ERROR:MediaWiki Text? " + WITSProperties.WITS_AllienBrandMessage;
            }
        }

        uncleanSGML = handler.replace(uncleanSGML, "<LB>", "%%LB%%", 0);
        uncleanSGML = handler.replace(uncleanSGML, "<noparse>", "%%noparse%%", 0);
        uncleanSGML = handler.replace(uncleanSGML, "<noparsi>", "%%noparsi%%", 0);
        uncleanSGML = handler.replace(uncleanSGML, "</noparse>", "%%/noparse%%", 0);
        uncleanSGML = handler.replace(uncleanSGML, "</noparsi>", "%%/noparsi%%", 0);
        uncleanSGML = handler.replace(uncleanSGML, "<", "&lt;", 0);
        uncleanSGML = handler.replace(uncleanSGML, ">", "&gt;", 0);
        uncleanSGML = handler.replace(uncleanSGML, "%%LB%%", "<LB>", 0);
        uncleanSGML = handler.replace(uncleanSGML, "%%noparse%%", "<noparse>", 0);
        uncleanSGML = handler.replace(uncleanSGML, "%%noparsi%%", "<noparsi>", 0);
        uncleanSGML = handler.replace(uncleanSGML, "%%/noparse%%", "</noparse>", 0);
        uncleanSGML = handler.replace(uncleanSGML, "%%/noparsi%%", "</noparsi>", 0);

        //Remove excess baggage
        ExcludeParser exICParser = new ExcludeParser(uncleanSGML);
        exICParser.setDebugger(debugger);
        uncleanSGML = exICParser.getProcessedText();

        //Remove excess baggage
        ScriptParser scriptParser = new ScriptParser(uncleanSGML);
        scriptParser.setDebugger(debugger);
        uncleanSGML = scriptParser.getProcessedText();

        //Call Pre Processor        
        PreProcessor preICParser = new PreProcessor(uncleanSGML);
        preICParser.setDebugger(debugger);
        uncleanSGML = preICParser.getProcessedText();


        //Fix sections      
        HeadingParser headICParser = new HeadingParser(hideID, uncleanSGML, props);
        headICParser.setDebugger(debugger);
        headICParser.setID(witsID);
        uncleanSGML = headICParser.getProcessedText();
        witsID = headICParser.getID();
        //System.out.println("LAST ID:"+witsID);

        //Fix bqs      
        BlockQuoteParser bqParser = new BlockQuoteParser(uncleanSGML);
        bqParser.setDebugger(debugger);
        uncleanSGML = bqParser.getProcessedText();

        //Fix bqs      
        ColorParser colorParser = new ColorParser(uncleanSGML);
        colorParser.setDebugger(debugger);
        uncleanSGML = colorParser.getProcessedText();

        //Fix panels    
        PanelParser panelParser = new PanelParser(uncleanSGML);
        panelParser.setDebugger(debugger);
        uncleanSGML = panelParser.getProcessedText();


        //LiteralParser litICParser = new LiteralParser(uncleanSGML);
        //litICParser.setDebugger(debugger);
        //uncleanSGML = litICParser.getProcessedText();

        //Handle Inline elements                
        EmphasisParser empICParser = new EmphasisParser(hideID, uncleanSGML);
        empICParser.setDebugger(debugger);
        uncleanSGML = empICParser.getProcessedText();

        LinkParser linkICParser = new LinkParser(hideID, uncleanSGML, props);
        linkICParser.setDebugger(debugger);
        uncleanSGML = linkICParser.getProcessedText();

        //Handle Block elements        
        TableParser tableICParser = new TableParser(hideID, uncleanSGML);
        tableICParser.setDebugger(debugger);
        uncleanSGML = tableICParser.getProcessedText();

        //System.out.println("--------To List Parser------\r\n"+uncleanSGML);
        ListParser listICParser = new ListParser(uncleanSGML);
        listICParser.setDebugger(debugger);
        uncleanSGML = listICParser.getProcessedText();
        //System.out.println("--------From List Parser------\r\n"+uncleanSGML);

        //NoteParser noteICParser = new NoteParser(uncleanSGML);
        //noteICParser.setDebugger(debugger);
        //uncleanSGML = noteICParser.getProcessedText();

        //Fix para. order of parsing is important      
        ParaParser paraICParser = new ParaParser(uncleanSGML);
        paraICParser.setDebugger(debugger);
        uncleanSGML = paraICParser.getProcessedText();

        //retreive overcast text
        uncleanSGML = overParser.getOvercastContent(uncleanSGML);

        //Call Post Processor       
        PostProcessor postICParser = new PostProcessor(hideID, uncleanSGML, props);
        postICParser.setDebugger(debugger);
        uncleanSGML = postICParser.getProcessedText();

        //System.out.println("CLEAN:"+uncleanSGML);
        return uncleanSGML;


    }
}
