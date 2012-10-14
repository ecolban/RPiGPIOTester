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

public class XMLTester {

	public static final File OUTPUT_FILE = new File("/Users/ecolban/output.xml");

	public static void main(String[] args) {
		/* Create an instance of XMLTester */
		XMLTester foo = new XMLTester();
		// Create an XML document
		Document doc1 = foo.createDocument();
		// Write the document to a file
		try {
			foo.write(doc1);
		} catch (IOException e) {
			return;
		}

		// Read the document from a file and print out some of the info.
		try {
			foo.parse(OUTPUT_FILE);
		} catch (DocumentException e) {
			System.out.println(e.getMessage());
		}

	}

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

	public void write(Document document) throws IOException {

		// lets write to a file
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileWriter(OUTPUT_FILE), format);
		writer.write(document);
		writer.close();

	}

	public void parse(File file) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		// iterate through child elements of root
		Element root = document.getRootElement();
		for (@SuppressWarnings("unchecked")
		Iterator<Element> i = root.elementIterator(); i.hasNext();) {
			Element element = i.next();
			System.out.print(element.getText() + " ");
			System.out.println(element.attributeValue("location"));
		}
	}
}
