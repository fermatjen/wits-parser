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

package org.wits.test;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author FJ
 */
public class XMLValidityCase implements WITSCase{

    private String source = new String();

    public void initCase(String source) {
        this.source = source;
    }


    public String runCase() {

        try {
                        
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setCoalescing(true);
            dbf.setIgnoringComments(true);
            dbf.setXIncludeAware(true);
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource sourceXML = new InputSource(new StringReader(source));
            Document doc = db.parse(sourceXML);
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            String output = result.getWriter().toString();

                       
            return "PASS";
            
        } catch (Exception ex) {
            return "FAIL";
        }

    }
   
}
