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
import java.util.Collections;
import java.util.List;

import static de.fraunhofer.iao.querimonia.config.TestAnalysers.*;

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
      .setExtractors(List.of(new ExtractorDefinition("None", ExtractorType.NONE, "Unknown",
          "#ffffff")))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .createConfiguration();

  public static final Configuration CONFIGURATION_D = new ConfigurationBuilder()
      .setId(4)
      .setName("D")
      .setClassifiers(List.of())
      .setExtractors(List.of(new ExtractorDefinition("None", ExtractorType.NONE, "Unknown",
          "#ffffff")))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .createConfiguration();

  public static final Configuration CONFIGURATION_E = new ConfigurationBuilder()
      .setId(5)
      .setName("E")
      .setClassifiers(List.of())
      .setExtractors(List.of(new ExtractorDefinition("None", ExtractorType.NONE, "Unknown",
          "#ffffff")))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .setActive(true)
      .createConfiguration();

  public static final Configuration CONFIGURATION_F = new ConfigurationBuilder()
      .setId(6)
      .setName("F")
      .setClassifiers(List.of())
      .setExtractors(List.of(new ExtractorDefinition("None", ExtractorType.NONE, "Unknown",
          "#ffffff")))
      .setSentimentAnalyzer(new SentimentAnalyzerDefinition(SentimentAnalyzerType.NONE, ""))
      .setEmotionAnalyzer(new EmotionAnalyzerDefinition(EmotionAnalyzerType.NONE, ""))
      .setActive(true)
      .createConfiguration();

  public static final Configuration CONFIGURATION_G = new ConfigurationBuilder()
      .setId(7)
      .setName("G")
      .setClassifiers(Collections.singletonList(MOCK_CLASSIFIER_A))
      .setExtractors(List.of(MOCK_EXTRACTOR_A, MOCK_EXTRACTOR_B))
      .setSentimentAnalyzer(MOCK_SENTIMENT_ANALYZER_A)
      .setEmotionAnalyzer(MOCK_EMOTION_ANALYZER_A)
      .setActive(true)
      .createConfiguration();
}