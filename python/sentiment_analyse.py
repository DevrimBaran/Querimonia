import string
import sys
from nltk.data import load
from nltk.tokenize.treebank import TreebankWordTokenizer
from pathlib import Path

# Sarcasm, Irony not detected. For example: 'Ich hasse dich und liebe dich nicht' has value > 0
# created by phuszár

# ToDo: Respect to Frequency in Dictionary!

# Variables
datafolder = Path("data/")


def positive_sentiment(query):
    return analyze(query, 'SentiWS_v2.0_Positive.txt')


def negative_sentiment(query):
    return analyze(query, 'SentiWS_v2.0_Negative.txt')


def treebank_tokenizer(sentence):
    tokenizer = load('data/german.pickle')
    treebank_word_tokenize = TreebankWordTokenizer().tokenize
    tokens = []
    for s in tokenizer.tokenize(sentence):
        tokens.extend([token for token in treebank_word_tokenize(s)])
    tokens = [''.join(i for i in s if i not in string.punctuation)
              for s in tokens]
    tokens = list(filter(None, tokens))
    return tokens


# Check for matches in Dictionaries
def analyze(query, dict):
    tokens = treebank_tokenizer(query)
    tokens = stopword_filter(tokens)
    dict = datafolder / dict
    sentiment_value = 0
    negative = negation_words()
    with open(dict, 'r', encoding='utf-8') as sentis:
        for s in sentis:
            cells = s.split('\t')
            lemma = cells[0].split('|')
            value = float(cells[1].strip())
            infl = cells[2].split(',')
            # delete the \n in the last word
            infl[len(infl) - 1] = infl[len(infl) - 1].strip()
            for j in tokens:
                if j in infl or j in lemma:
                    try:
                        if tokens[tokens.index(j) - 1] in negative:
                            sentiment_value += value * -0.5
                        elif tokens[tokens.index(j) + 1] in negative:
                            sentiment_value += value * -0.5
                        else:
                            sentiment_value += value
                    except IndexError:
                        sentiment_value += value
    return sentiment_value


def stopword_filter(query):
    file_to_open = datafolder / 'stopWords.txt'
    with open(file_to_open, 'r', encoding='utf-8') as f:
        stopWords = [''.join(t)
                     for t in f]
        for i, s in enumerate(stopWords):
            stopWords[i] = s.strip()
    return list(filter(None, [''.join(q for q in t if t not in stopWords)
                              for t in query]))


def negation_words():
    negdict = datafolder / 'negationswoerter.txt'
    with open(negdict, 'r', encoding='utf-8') as negwords:
        negwords = [''.join(n)
                    for n in negwords]
        for i, s in enumerate(negwords):
            negwords[i] = s.strip()
    return negwords


# parsing params von command line
def parse_arg():
    sentiment_value = 0
    for arg in sys.argv[1:]:
        sentiment_value += positive_sentiment(arg) + negative_sentiment(arg)
    return sentiment_value


# for flask
def main(query):
    value = round(positive_sentiment(query) + negative_sentiment(query), 2)
    return {'sentiment': value}


if __name__ == '__main__':
    print('Use test in comments in code')

    # TESTS
    # test = 'In Ihrem Schreiben vom 8. 2. 2013 schreiben Sie, nur ein Bus der Linie CE62 von Wuppertal-Elberfeld nach Wuppertal-Ronsdorf sei ausgefallen. Dies kann so nicht stimmen. Vielmehr stellt sich mir und anderen die Frage, weshalb ständig und gehäuft Buslinien der WSW ausfallen und Fahrten entgegen dem Fahrplan nicht angeboten werden. Als Rad-, Bahn- und gelegentlicher Autofahrer benutze ich eher selten die Busse der WSW. Wenn ich sie benutze, ergeben sich auffällig häufig Ausfälle und deutliche Verspätungen. Dies betrifft auffallend oft die als schnell konzipierten CE-Linien, nach meiner jüngeren Erfahrung CE61/CE62, weniger auch CE64/CE65 (Elberfeld-Cronenberg), und Nebenlinien wie etwa 643. Einzelne Ihrer Mitarbeiter berichten mir übereinstimmend, die Personaldecke sei zu dünn, um Ausfälle einzelner Mitarbeiter auszugleichen. Wegen spürbaren Personalmangels - nicht, wie Sie schreiben, vereinzelt wegen Krankheit - würden häufig Fahrten ausfallen. Viele Fahrer berichten anschaulich, dass in manchen Stoßzeiten vergeblich wartende Passagiere an Haltestellen stünden. Teilweise berichtet man mir von gezielten Maßnahmen, um Einsparungen herbeizuführen sowie nicht ausgeglichenen Überstunden. Ihre Fahrer sehen dies unmittelbar mit der Politik der WSW verknüpft, die CE-Linien ab März/April 2013 auszudünnen.Der Vorgang, auf den sich Ihr Schreiben bezieht, lief tatsächlich so ab: Am 1. 2. 2013 wartete ich an der Morianstraße in Wuppertal mit meinem x Monate alten Sohn nachmittags auf eine Busverbindung nach Ronsdorf, Haltestelle Kniprodestraße. Ich beabsichtigte, den Bus CE62 um 15:47 Uhr zu nehmen. Wir waren auf dem Weg zu einer Nachmittagsveranstaltung für Eltern mit Kleinstkindern, die ich verpasste:Die CE62 um 15:47 Uhr fuhr nicht, ebenso nicht die nachfolgenden Busse der Linie CE62:- 16:07 Uhr- 16:27 UhrEs fuhren ferner nicht die Busse der Linie 620 um- 15:55 Uhr- 16:15 Uhr- 16:35 Uhrdie Haltestelle an. Ihr Schreiben legt nahe, dass es sich nur um vereinzelte Fälle handele. Unmittelbar bei meinem nächsten Fahrtantritt mit der WSW eine Woche später kam es wiederum zu einem Ausfall. Am Freitag, 8. 2. 2013, fiel Linie CE61 um 15:38 Uhr an der Haltestelle Am Stadtbahnhof in Richtung Barmen aus. Mit ca. 25 Minuten Verspätung erreichte ich mit der Linie 640 (ab 15:48 Uhr) gegen 16:25 Uhr den Alten Markt in Barmen.Heute, am 21. 2. 2013, fielen die Busse der Linie CE64 von Wuppertal Hbf in Richtung Cronenberg um 06:40 Uhr und um 07:00 Uhr aus. Ein Fahrgast erreichte deshalb nicht pünktlich seine Arbeitsstelle in Solingen. Würde ich häufiger fahren, würde ich vermutlich noch häufiger dadurch Zeit verlieren. Ich bitte Sie daher freundlich, Ihre Dienstleistungen bestmöglich fahrplangemäß anzubieten'
    # test_pos = 'Ich liebe Frieden und finde Blumen schön'
    # test_neg = 'Ich mag Zerstörung und Gewalt und hasse Glück'
    # neg_pos = 'Ich mag keine Pünktlichkeit und mag Höflichkeit nicht'
    # neg_neg = 'Ich mag keine Verspätungen und finde Ausfälle nicht schön'
    # print(main(test_pos))

    # for cmd
    # print("Sentiment Value is: " + (str)(parse_arg()))
