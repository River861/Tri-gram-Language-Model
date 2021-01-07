package Util;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;

import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

public class XMLParser {
    /**
     * 从document中提取<contenttitle/>和<content/>中的内容
     * */
    public static void parse(String document, StringBuilder title, StringBuilder content) {
        try {
            XMLStreamReader reader =
                    XMLInputFactory.newInstance().createXMLStreamReader(new
                            ByteArrayInputStream(document.replaceAll("&", "&amp;").getBytes()));
            String cur_ele = "";
            while (reader.hasNext()) {
                int code = reader.next();
                switch (code) {
                    case START_ELEMENT:
                        cur_ele = reader.getLocalName();
                        break;
                    case CHARACTERS:
                        if (cur_ele.equalsIgnoreCase("contenttitle")) {
                            title.append(reader.getText());
                        } else if (cur_ele.equalsIgnoreCase("content")) {
                            content.append(reader.getText());
                        }
                        break;
                }
            }
            reader.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
