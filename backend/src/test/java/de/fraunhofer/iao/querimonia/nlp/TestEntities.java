package de.fraunhofer.iao.querimonia.nlp;

public class TestEntities {

  public static final NamedEntity ENTITY_A = new NamedEntityBuilder()
      .setStart(12)
      .setEnd(19)
      .setValue("Jürgen")
      .setLabel("Name")
      .setExtractor("Test")
      .createNamedEntity();

  public static final NamedEntity ENTITY_B = new NamedEntityBuilder()
      .setStart(19)
      .setEnd(23)
      .setValue("Karl")
      .setLabel("Name")
      .setExtractor("Test")
      .createNamedEntity();

  public static final NamedEntity ENTITY_C = new NamedEntityBuilder()
      .setStart(45)
      .setEnd(56)
      .setValue("Stuttgart")
      .setLabel("Ort")
      .setExtractor("Test")
      .setSetByUser(true)
      .createNamedEntity();

  public static final NamedEntity ENTITY_D = new NamedEntityBuilder()
      .setStart(59)
      .setEnd(71)
      .setValue("Freiburg am Neckar")
      .setLabel("Ort")
      .setExtractor("Test")
      .setSetByUser(true)
      .createNamedEntity();
}