package de.fraunhofer.iao.querimonia.complaint;

import de.fraunhofer.iao.querimonia.nlp.NamedEntity;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "fraunhoferTextDocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComplaintXml {

  @XmlRootElement(name = "fraunhoferTextDocuments")
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class ComplaintXmls {
    List<ComplaintXml> fraunhoferTextDocument;

    private ComplaintXmls(){
    }

    public ComplaintXmls(List<ComplaintXml> complaints) {
      fraunhoferTextDocument = complaints;
    }
  }

  private Input input;

  private Output output;

  private ComplaintXml() {
  }

  /**
   * Creating a ComplaintXml out of a Complaint and mapping all relevant properties.
   * @param complaint Complaint to create an Xml of
   */
  public ComplaintXml(Complaint complaint) {
    input = new Input();
    input.text = complaint.getText();

    input.metadata = new Metadata();

    input.metadata.date = complaint.getReceiveDate().atTime(complaint.getReceiveTime()).toString();
    input.metadata.dateType = "creation";

    input.metadata.metadataItems = new LinkedList<>();
    MetadataItem state = new MetadataItem();
    state.key = "state";
    state.value = complaint.getState().toString();
    input.metadata.metadataItems.add(state);


    output = new Output();
    output.extractor = new Extractor();
    output.extractor.step = 1;
    output.extractor.software = "Querimonia-Extraction";
    output.extractor.type = "extractor";

    output.extractor.annotation = new LinkedList<>();
    for (NamedEntity entity : complaint.getEntities()) {
      Annotation annotation = new Annotation();
      annotation.type = entity.getLabel();
      annotation.start = entity.getStartIndex();
      annotation.end = entity.getEndIndex();
      annotation.page = 1;
      annotation.length = annotation.end - annotation.start;
      annotation.originalValue = entity.getValue();

      annotation.result = new LinkedList<>();

      Result setByUserResult = new Result();
      setByUserResult.type = "setByUser";
      setByUserResult.result = String.valueOf(entity.isSetByUser());

      Result preferredResult = new Result();
      preferredResult.type = "preferred";
      preferredResult.result = String.valueOf(entity.isPreferred());

      Result colorResult = new Result();
      colorResult.type = "color";
      colorResult.result = entity.getColor();

      annotation.result.add(setByUserResult);
      annotation.result.add(preferredResult);
      annotation.result.add(colorResult);

      output.extractor.annotation.add(annotation);
    }


    output.classifier = new Classifier();
    output.classifier.step = 2;
    output.classifier.software = "Querimonia-Extraction";
    output.classifier.type = "classifier";

    output.classifier.classList = new LinkedList<>();

    for (Map.Entry<String, Double> entry: complaint.getSubject().getProbabilities().entrySet()) {
      Classes classes = new Classes();
      classes.classification = entry.getKey();
      classes.confidence = entry.getValue();

      classes.best = complaint.getSubject().getValue().equals(entry.getKey()) ? "true" : null;

      output.classifier.classList.add(classes);
    }

    output.sentiment = new Classifier();
    output.sentiment.step = 3;
    output.sentiment.type = "sentiment";
    output.sentiment.software = "Querimonia-Sentiment";

    Classes sentimentClasses = new Classes();
    sentimentClasses.confidence = complaint.getSentiment().getTendency();
    output.sentiment.classList = List.of(sentimentClasses);

    output.emotion = new Classifier();
    output.emotion.step = 4;
    output.emotion.software = "Querimonia-Emotion";
    output.emotion.classList = new LinkedList<>();

    for (Map.Entry<String, Double> entry:
        complaint.getSentiment().getEmotion().getProbabilities().entrySet()) {
      Classes classes = new Classes();
      classes.classification = entry.getKey();
      classes.confidence = entry.getValue();

      classes.best = complaint.getSentiment().getEmotion().getValue().equals(entry.getKey()) ? "true" :
          null;

      output.emotion.classList.add(classes);
    }

  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class Input {
    private Input() {
    }

    public String text;
    public List<File> file;
    public Metadata metadata;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class File {
    private File() {
    }

    @XmlValue
    public String value;

    @XmlAttribute
    public String id;

    @XmlAttribute
    public String name;

    @XmlAttribute
    public String encoding;

    @XmlAttribute
    public String type;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class Metadata {

    private Metadata() {
    }

    public String date;
    @XmlPath("date/@type")
    public String dateType;

    public String domain;

    public String author;

    public String documentId;

    public String caseId;

    @XmlElement(name = "metadata")
    public List<MetadataItem> metadataItems;

  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class MetadataItem {
    private MetadataItem() {
    }

    @XmlValue
    public String value;
    @XmlAttribute
    public String key;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class Output {
    private Output(){
    }

    @XmlPath("analysis[1]")
    public Extractor extractor;

    @XmlPath("analysis[2]")
    public Classifier classifier;

    @XmlPath("analysis[3]")
    public Classifier sentiment;

    @XmlPath("analysis[4]")
    public Classifier emotion;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class Extractor {
    private Extractor(){
    }

    @XmlAttribute
    public int step;

    @XmlAttribute
    public String software;

    @XmlAttribute
    public String type;

    public String input;

    @XmlElementWrapper(name = "output")
    List<Annotation> annotation;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class Annotation {
    private Annotation() {
    }

    @XmlAttribute
    public String type;

    @XmlAttribute
    public String id;

    @XmlPath("position/@page")
    public int page;

    @XmlPath("position/@startIndex")
    public int start;

    @XmlPath("position/@endIndex")
    public int end;

    @XmlPath("position/@length")
    public int length;

    public String originalValue;

    @XmlElementWrapper(name = "results")
    @XmlElement(name = "result")
    public List<Result> result;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class Result {
    private Result() {
    }

    @XmlValue
    public String result;
    @XmlAttribute
    public String type;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class Classifier {
    private Classifier() {
    }

    @XmlAttribute
    public int step;

    @XmlAttribute
    public String software;

    @XmlAttribute
    public String type;

    public String input;

    @XmlPath("output/classification/")
    @XmlElement(name = "class")
    public List<Classes> classList;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  private static class Classes {
    private Classes() {
    }

    @XmlAttribute
    public double confidence;

    @XmlAttribute(name = "best-class")
    public String best;

    @XmlAttribute()

    @XmlValue
    public String classification;
  }
}
