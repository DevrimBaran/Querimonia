package de.fraunhofer.iao.querimonia.response.rules;

import de.fraunhofer.iao.querimonia.utility.exception.QuerimoniaException;
import de.fraunhofer.iao.querimonia.utility.exception.XmlException;
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

/**
 * This class is used to parse rules from XML to {@link Rule} objects.
 */
public class RuleParser {

  /**
   * Parses the rules of a valid xml file.
   *
   * @param rulesXml the xml as a string. The xml must be well formatted in the correct xml schema
   *                 as given in the docs folder.
   *
   * @return the root rule, parsed from the xml string.
   *
   * @throws QuerimoniaException on parse errors.
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
        // get root node
        return getRuleFromNode((Element) root.getFirstChild());
      } else {
        // wrong root element
        throw new QuerimoniaException(HttpStatus.BAD_REQUEST, "Fehlerhaftes XML: "
            + "Root-Element muss 'Rules' heißen.", "XML Error");
      }

    } catch (ParserConfigurationException e) {
      throw new QuerimoniaException(HttpStatus.INTERNAL_SERVER_ERROR, "Unerwarter Fehler: "
          + "XML-Parser falsch konfiguriert.", "XML Error");
    } catch (SAXParseException e) {
      throw new XmlException(HttpStatus.BAD_REQUEST, "Fehler beim Parsen der Regeln!", e);
    } catch (SAXException e) {
      throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
          "Unbekannter XML-Fehler: " + e.getMessage(), "XML Error");
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unerwarteter "
          + "I/O-Fehler!");
    }
  }

  private static Rule getRuleFromNode(Element element) {
    String tag = element.getTagName();

    switch (tag) {
      case "Property":
        return new PropertyRule(element.getAttribute("name"), element.getAttribute("matches"));
      case "Sentiment":
        return getSentimentRule(element);
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
        throw new QuerimoniaException(HttpStatus.BAD_REQUEST,
            "Ungültiges XML-Format, der Tag " + tag + " ist ungültig.", "Ungültiger Tag");
    }
  }

  private static Rule getSentimentRule(Element element) {
    double max = Double.POSITIVE_INFINITY;
    double min = Double.NEGATIVE_INFINITY;

    if (element.hasAttribute("max")) {
      max = Double.parseDouble(element.getAttribute("max"));
    }
    if (element.hasAttribute("min")) {
      min = Double.parseDouble(element.getAttribute("min"));
    }
    String emotion = null;
    if (element.hasAttribute("emotion")) {
      emotion = element.getAttribute("emotion");
    }

    return new SentimentRule(min, max, emotion);
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
    if (element.hasAttribute("matches")) {
      value = element.getAttribute("matches");
    }
    int countMin = 1;
    if (element.hasAttribute("countMin")) {
      countMin = Integer.parseInt(element.getAttribute("countMin"));
    }
    int countMax = Integer.MAX_VALUE;
    if (element.hasAttribute("countMax")) {
      countMax = Integer.parseInt(element.getAttribute("countMax"));
    }
    return new EntityRule(element.getAttribute("label"), value, countMin, countMax);
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
