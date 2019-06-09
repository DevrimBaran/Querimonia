package de.fraunhofer.iao.querimonia.nlp.analyze;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class analysed the specified text for their tokens
 */
public class TokenAnalyzer implements StopWordFilter {

  /**
   * Sorts a map in ascending order of the values
   *
   * @return sorted map
   */
  private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
    List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
    list.sort(Entry.comparingByValue());

    Map<K, V> result = new LinkedHashMap<>();
    for (Entry<K, V> entry : list) {
      result.put(entry.getKey(), entry.getValue());
    }

    return result;
  }

  /**
   * Extracts the tokens from a string.
   * Stopwords are removed and the frequencies of the tokens are calculated and output together with the tokens.
   *
   * @param complaintText the text from which the tokens are to be evaluated.
   * @return a sorted map which contains all non stop-words as keys mapped to the count they occur in the text.
   */
  @Override
  public Map<String, Integer> filterStopWords(String complaintText) {

    StringBuilder readString = new StringBuilder();
    try {
      //reads the Stopwords-List
      BufferedReader stopWordsReader
          = new BufferedReader(new FileReader(
              new File(("backend/src/main/resources/Stopwords.txt"))));
      String s;
      while ((s = stopWordsReader.readLine()) != null) {
        readString.append(s);
      }
      stopWordsReader.close();
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Stop word file not found!");
    }

    //List of Stopwords
    List<String> stopwords = Arrays.asList(readString.toString().replaceAll("\\s+", "")
        .split(","));


    Annotation germanAnnotation = new Annotation(complaintText);
    Properties properties = new Properties();

    //Selection of the analysis steps (here Tokenization and Sentence-Splitting)
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    pipeline.annotate(germanAnnotation);

    //Unwanted signs
    String punctuations = ".,:;!?";

    Map<String, Integer> countByWords = new HashMap<>();

    for (CoreLabel token : germanAnnotation.get(CoreAnnotations.TokensAnnotation.class)) {
      String tokenString = token.word().toLowerCase();
      if (stopwords.contains(tokenString) || punctuations.contains(tokenString)) {
        continue;
      }
      countByWords.merge(tokenString, 1, Integer::sum);
    }

    return sortByValue(countByWords);
  }


}
