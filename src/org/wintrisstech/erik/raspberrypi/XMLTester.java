package org.wintrisstech.erik.raspberrypi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * This class is used to illustrate some basic functionality of dom4j
 * 
 * @author ecolban
 *
 */
public class XMLTester {

	private static final File OUTPUT_FILE = new File("/Users/ecolban/output.xml");

	public static void main(String[] args) {
		/* Create an instance of XMLTester */
		XMLTester foo = new XMLTester();
		// Create an XML document
		Document doc1 = foo.createDocument();
		// Write the document to a file
		foo.write(doc1);

		// Read the document from a file and print out some of the contents.
		foo.parse(OUTPUT_FILE);

	}
	/**
	 * Creates a document
	 * 
	 * @return the document
	 */
	public Document createDocument() {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("root");

		Element summer = root.addElement("schedule")
				.addAttribute("name", "Summer")
				.addAttribute("period", "Minute");
		summer.addElement("onoff").addAttribute("LEDs", "0, 1")
				.addAttribute("start", "0").addAttribute("duration", "1000");
		summer.addElement("onoff").addAttribute("LEDs", "2, 3")
				.addAttribute("start", "500").addAttribute("duration", "1000");
		summer.addElement("onoff").addAttribute("LEDs", "4, 5")
				.addAttribute("start", "1000").addAttribute("duration", "1000");

		return document;
	}

	/**
	 * Writes a given document to a file
	 * 
	 * @param document the given document
	 */
	public void write(Document document) {

		// write to a file
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileWriter(OUTPUT_FILE), format);
			writer.write(document);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}

	}

	/**
	 * Creates a document from an XML file and prints out some info
	 * @param file
	 */
	public void parse(File file) {
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element root = document.getRootElement();
			for (@SuppressWarnings("unchecked")
			Iterator<Element> i = root.elementIterator(); i.hasNext();) {
				Element element = i.next();
				System.out.println("schedule = " + element.attributeValue("name"));
			}
		} catch (DocumentException e) {
			System.out.println(e.getMessage());
		}
	}
}
