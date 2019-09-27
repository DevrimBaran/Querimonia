package de.fraunhofer.iao.querimonia.nlp.analyze;


import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.languagetool.tagging.de.GermanTagger;

import java.util.*;
import java.util.Map.Entry;

/**
 * This class analysed the specified text for their tokens.
 */
public class TokenAnalyzer implements StopWordFilter {

  /**
   * Sorts a map in ascending order of the values.
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
   * Extracts the tokens from a string. Stopwords are removed and the frequencies of the tokens are
   * calculated and output together with the tokens.
   *
   * @param complaintText the text from which the tokens are to be evaluated.
   * @return a sorted map which contains all non stop-words as keys mapped to the count they occur
   * in the text.
   */
  @Override
  public Map<String, Integer> filterStopWords(String complaintText) {

    //String of german NLTK-Stopwords
    String stopwordsString =
        "aber, alle, allem, allen, aller, alles, als, also, am, an, ander, andere, anderem, "
            + "anderen, anderer, anderes, anderm, andern, anderr, anders, auch, auf, aus, bei, bin"
            + ", bis, bist, da, damit, dann, der, den, des, dem, die, das, daß, dass, derselbe, "
            + "derselben, denselben, desselben, demselben, dieselbe, dieselben, dasselbe, dazu, "
            + "dein, deine, deinem, deinen, deiner, deines, denn, derer, dessen, dich, dir, du, "
            + "dies, diese, diesem, diesen, dieser, dieses, doch, dort, durch, ein, eine, einem, "
            + "einen, einer, eines, einig, einige, einigem, einigen, einiger, einiges, einmal, er,"
            + " ihn, ihm, es, etwas, euer, eure, eurem, euren, eurer, eures, für, gegen, gewesen, "
            + "hab, habe, haben, hat, hatte, hatten, hier, hin, hinter, ich, mich, mir, ihr, ihre,"
            + " ihrem, ihren, ihrer, ihres, euch, im, in, indem, ins, ist, jede, jedem, jeden, "
            + "jeder, jedes, jene, jenem, jenen, jener, jenes, jetzt, kann, kein, keine, keinem, "
            + "keinen, keiner, keines, können, könnte, machen, man, manche, manchem, manchen, "
            + "mancher, manches, mein, meine, meinem, meinen, meiner, meines, mit, muss, musste, "
            + "nach, nicht, nichts, noch, nun, nur, ob, oder, ohne, sehr, sein, seine, seinem, "
            + "seinen, seiner, seines, selbst, sich, sie, ihnen, sind, so, solche, solchem, "
            + "solchen, solcher, solches, soll, sollte, sondern, sonst, über, um, und, uns, "
            + "unsere, unserem, unseren, unser, unseres, unter, viel, vom, von, vor, während, "
            + "war, waren, warst, was, weg, weil, weiter, welche, welchem, welchen, welcher, "
            + "welches, wenn, werde, werden, wie, wieder, will, wir, wird, wirst, wo, wollen, "
            + "wollte, würde, würden, zu, zum, zur, zwar, zwischen";

    //List of Stopwords
    List<String> stopwords = Arrays.asList(stopwordsString.replaceAll("\\s+", "")
        .split(","));


    Annotation germanAnnotation = new Annotation(complaintText);
    Properties properties = new Properties();

    //Selection of the analysis steps (here Tokenization and Sentence-Splitting)
    properties.setProperty("annotators", "tokenize,ssplit");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);
    pipeline.annotate(germanAnnotation);

    //Unwanted signs ('hehl' = hyphen)
    String punctuations = ". , : ; ! ? hehl";
    String brackets = "-lrb- -rrb- -lsb- -rsb- -lcb- -rcb-";

    Map<String, Integer> countByWords = new HashMap<>();
    GermanTagger gt = new GermanTagger();

    for (CoreLabel token : germanAnnotation.get(CoreAnnotations.TokensAnnotation.class)) {
      String tokenString;
      //Lemma available?
      if (gt.tag(token.word()).size() <= 0) {
        tokenString = token.word().toLowerCase();
      } else {
        tokenString = gt.tag(token.word()).get(0).getLemma().toLowerCase();
      }
      if (stopwords.contains(tokenString) || punctuations.contains(tokenString)
          || brackets.contains(tokenString)) {
        continue;
      }
      countByWords.merge(tokenString, 1, Integer::sum);
    }

    return sortByValue(countByWords);
  }


}
