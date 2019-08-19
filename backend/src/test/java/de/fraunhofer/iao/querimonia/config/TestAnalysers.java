package de.fraunhofer.iao.querimonia.config;

import de.fraunhofer.iao.querimonia.nlp.TestEntities;
import de.fraunhofer.iao.querimonia.nlp.classifier.ClassifierType;
import de.fraunhofer.iao.querimonia.nlp.classifier.MockClassifierDefinition;
import de.fraunhofer.iao.querimonia.nlp.emotion.EmotionAnalyzerType;
import de.fraunhofer.iao.querimonia.nlp.emotion.MockEmotionAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.extractor.ExtractorType;
import de.fraunhofer.iao.querimonia.nlp.extractor.MockExtractorDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.MockSentimentAnalyzerDefinition;
import de.fraunhofer.iao.querimonia.nlp.sentiment.SentimentAnalyzerType;

import java.util.Collections;
import java.util.Map;

public class TestAnalysers {

  public static final MockClassifierDefinition MOCK_CLASSIFIER_A = new MockClassifierDefinition(
      ClassifierType.MOCK,
      "MockClassifier",
      "Kategorie",
      Map.ofEntries(
          Map.entry("Fahrer unfreundlich", 0.75)//,
          //Map.entry("Fahrt nicht erfolgt", 0.25),
          //Map.entry("Sonstiges", 0.0)
      )
  );

  public static final MockExtractorDefinition MOCK_EXTRACTOR_A = new MockExtractorDefinition(
      "Test",
      ExtractorType.MOCK,
      "Unknown",
      "#ffffff",
      Collections.singletonList(TestEntities.ENTITY_A)
  );

  public static final MockExtractorDefinition MOCK_EXTRACTOR_B = new MockExtractorDefinition(
      "Test",
      ExtractorType.MOCK,
      "Unknown",
      "#ffffff",
      Collections.singletonList(TestEntities.ENTITY_B)
  );

  public static final MockSentimentAnalyzerDefinition MOCK_SENTIMENT_ANALYZER_A =
      new MockSentimentAnalyzerDefinition(
          SentimentAnalyzerType.MOCK,
          "MockSentiment",
          0.5
      );

  public static final MockEmotionAnalyzerDefinition MOCK_EMOTION_ANALYZER_A =
      new MockEmotionAnalyzerDefinition(
          EmotionAnalyzerType.MOCK,
          "MockEmotion",
          Map.ofEntries(
              Map.entry("Wut", 0.6),
              Map.entry("Trauer", 0.4),
              Map.entry("Freude", 0.1)
          )
      );
}
