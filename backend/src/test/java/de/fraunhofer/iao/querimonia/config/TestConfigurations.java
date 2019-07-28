package de.fraunhofer.iao.querimonia.config;

import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierType;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerType;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorType;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerType;

import java.util.ArrayList;
import java.util.List;

public class TestConfigurations {

  public static final Configuration CONFIGURATION_A = new ConfigurationBuilder()
      .setName("A")
      .setId(1)
      .setClassifiers(new ArrayList<>())
      .setExtractors(new ArrayList<>())
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .createConfiguration();

  public static final Configuration CONFIGURATION_B = new ConfigurationBuilder()
      .setId(2)
      .setName("B")
      .setClassifiers(List.of(new ClassifierDefinition(ClassifierType.NONE, "", "Kategorie")))
      .setExtractors(new ArrayList<>())
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .createConfiguration();

  public static final Configuration CONFIGURATION_C = new ConfigurationBuilder()
      .setId(3)
      .setName("C")
      .setClassifiers(List.of())
      .setExtractors(List.of(new ExtractorDefinition("None", ExtractorType.NONE, List.of())))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .createConfiguration();

  public static final Configuration CONFIGURATION_D = new ConfigurationBuilder()
      .setId(4)
      .setName("D")
      .setClassifiers(List.of())
      .setExtractors(List.of(new ExtractorDefinition("None", ExtractorType.NONE, List.of())))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .createConfiguration();

  public static final Configuration CONFIGURATION_E = new ConfigurationBuilder()
      .setId(5)
      .setName("E")
      .setClassifiers(List.of())
      .setExtractors(List.of(new ExtractorDefinition("None", ExtractorType.NONE, List.of())))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .setActive(true)
      .createConfiguration();

  public static final Configuration CONFIGURATION_F = new ConfigurationBuilder()
      .setId(6)
      .setName("F")
      .setClassifiers(List.of())
      .setExtractors(List.of(new ExtractorDefinition("None", ExtractorType.NONE, List.of())))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .setActive(true)
      .createConfiguration();
}