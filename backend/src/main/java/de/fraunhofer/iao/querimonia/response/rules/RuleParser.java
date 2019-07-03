package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.exception.QuerimoniaException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RuleParser {

  /**
   * Parses the rules of a valid xml file.
   *
   * @param rulesXml the xml as a string. The xml must be well formatted in the correct xml schema
   *                 as given in the docs folder.
   * @return the root rule, parsed from the xml string.
   */
  public static Rule parseRules(String rulesXml) {
    // allow empty rules
    if (rulesXml.isEmpty()) {
      return Rule.TRUE;
    }
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document doc = builder.parse(new InputSource(new StringReader(rulesXml)));

      Element root = doc.getDocumentElement();

      if (root.getTagName().equals("Rules") && root.hasChildNodes()) {
        return getRuleFromNode((Element) root.getFirstChild());
      } else {
        throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Fehlerhaftes XML: "
            + "Root-Element muss 'Rules' heißen.");
      }
    } catch (ParserConfigurationException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Unerwarter Fehler: "
          + "XML-Parser falsch konfiguriert.");
    } catch (SAXParseException e) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Zeile: " + e.getLineNumber() + "; "
          + "Index " + (e.getColumnNumber() - 1) + ": XML-Fehler: " + e.getMessage());
    } catch (SAXException e) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
          "Unbekannter XML-Fehler: " + e.getMessage());
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unerwarteter "
          + "I/O-Fehler!");
    }
  }

  private static Rule getRuleFromNode(Element element) {
    String tag = element.getTagName();

    switch (tag) {
      case "Subject":
        return new SubjectRule(element.getAttribute("value"));
      case "Sentiment":
        return new SentimentRule(element.getAttribute("value"));
      case "UploadDate":
        return getUploadDateRule(element);
      case "UploadTime":
        return getUploadTimeRule(element);
      case "EntityAvailable":
        return getEntityRule(element);
      case "Predecessor":
        return getPredecessorRule(element);
      case "PredecessorCount":
        return getPredecessorCountRule(element);
      case "Not":
        Rule childRule = getRuleFromNode((Element) element.getFirstChild());
        return new NotRule(childRule);
      case "Or":
        List<Rule> orChildRules = extractChildRules(element);
        return new OrRule(orChildRules);
      case "And":
        List<Rule> andChildRules = extractChildRules(element);
        return new AndRule(andChildRules);
      default:
        throw new IllegalStateException("Malformed XML");
    }
  }

  private static Rule getPredecessorCountRule(Element element) {
    int max = Integer.MAX_VALUE;
    if (element.hasAttribute("max")) {
      max = Integer.parseInt(element.getAttribute("max"));
    }
    int min = 0;
    if (element.hasAttribute("min")) {
      min = Integer.parseInt(element.getAttribute("min"));
    }
    return new PredecessorCountRule(min, max);
  }

  private static Rule getPredecessorRule(Element element) {
    String position = "before";
    if (element.hasAttribute("position")) {
      position = element.getAttribute("position");
    }
    String template = element.getAttribute("matches");
    return new PredecessorRule(template, position);
  }

  private static Rule getEntityRule(Element element) {
    String value = null;
    if (element.hasAttribute("value")) {
      value = element.getAttribute("value");
    }
    return new EntityRule(element.getAttribute("label"), value);
  }

  private static Rule getUploadTimeRule(Element element) {
    LocalTime uploadTimeMin = null;
    LocalTime uploadTimeMax = null;
    if (element.hasAttribute("min")) {
      uploadTimeMin = LocalTime.parse(element.getAttribute("min"));
    }
    if (element.hasAttribute("max")) {
      uploadTimeMax = LocalTime.parse(element.getAttribute("max"));
    }
    return new UploadTimeRule(uploadTimeMin, uploadTimeMax);
  }

  private static Rule getUploadDateRule(Element element) {
    LocalDate uploadDateMin = null;
    LocalDate uploadDateMax = null;
    if (element.hasAttribute("min")) {
      uploadDateMin = LocalDate.parse(element.getAttribute("min"));
    }
    if (element.hasAttribute("max")) {
      uploadDateMax = LocalDate.parse(element.getAttribute("max"));
    }
    return new UploadDateRule(uploadDateMin, uploadDateMax);
  }

  private static List<Rule> extractChildRules(Element element) {
    List<Rule> childRules = new ArrayList<>();
    NodeList childNodes = element.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      childRules.add(getRuleFromNode((Element) childNodes.item(i)));
    }
    return childRules;
  }
}
