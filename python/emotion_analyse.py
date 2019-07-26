import string
from nltk.tokenize import word_tokenize
from nltk import download
from enum import Enum
from pathlib import Path
from collections import defaultdict

# This is an approach for emotion analysis in the german language
# created by phuszár

# intial loading for nltk
download('punkt')
# Variables
datafolder = Path("data/")


class emotions(Enum):
    Ekel = 'Ekel.txt'
    Freude = 'Freude.txt'
    Furcht = 'Furcht.txt'
    Trauer = 'Trauer.txt'
    Ueberraschung = 'Ueberraschung.txt'
    Verachtung = 'Verachtung.txt'
    Wut = 'Wut.txt'


def tokenize_and_stem(x):
    tokens = word_tokenize(x, 'german')
    tokens = [''.join(i for i in s if i not in string.punctuation)
              for s in tokens]
    tokens = list(filter(None, tokens))
    # CISTEM Stemmer
    # Results arent better with stemmer, 'Gefahr' and 'Fahrer' are equal after stemming
    # stemmer = nltk.stem.cistem.Cistem()
    # stemmedTokens = [''.join(stemmer.stem(t))
    #                 for t in tokens]
    return tokens


def stopword_filter(query):
    file_to_open = datafolder / 'stopWords.txt'
    f = open(file_to_open, 'r')
    stopWords = [''.join(tokenize_and_stem(t))
                 for t in f]
    for i, s in enumerate(stopWords):
        stopWords[i] = s.strip()
    return list(filter(None, [''.join(q for q in t if t not in stopWords)
                              for t in query]))


def prozent(dict):
    # Convert from count to prozent ratio
    sum = 0
    for value in dict.values():
        sum += value
    for key, value in dict.items():
        dict[key] = round((100 / sum) * value, 2)
    print(dict)


def emotion_analysis(query):
    emotions_d = defaultdict(int)
    stemNtokenize = tokenize_and_stem(query)
    swFiltered = stopword_filter(stemNtokenize)
    print(swFiltered)
    for emo in emotions:
        file_to_open = datafolder / emo.value
        with open(file_to_open, 'r', encoding='utf-8') as file:
            emoList = [''.join(tokenize_and_stem(t))
                       for t in file]
            # delete duplicates happend due stemming
            emoList = list(dict.fromkeys(emoList))
            for sw in swFiltered:
                if sw in emoList:
                    # Sum and normalize by the Dictionary Size
                    emotions_d[emo.name] += 1 * (1 / len(emoList))
    return prozent(dict(emotions_d))


# for flask
def main(query):
    # :return dict of possible Emotions - dict like a hashmap.
    # Form like: {'Freude': 6, 'Furcht': 1, 'Ueberraschung': 1, 'Verachtung': 6}
    return emotion_analysis(query)


if __name__ == '__main__':
    print('Use test in comments in code')

    # testQuery = 'In Ihrem Schreibenvom 8. 2. 2013schreiben Sie, nur "ein" Bus der Linie CE62 von Wuppertal-Elberfeld nach Wuppertal-Ronsdorf sei ausgefallen. Dies kann so nicht stimmen. Vielmehr stellt sich mir und anderen die Frage, weshalbständig undgehäuft Busliniender WSWausfallen undFahrten entgegen dem Fahrplan nicht angeboten werden. Als Rad-, Bahn- und gelegentlicher Autofahrer benutze ich eher selten die Busse der WSW. Wenn ichsie benutze, ergeben sich auffällig häufig Ausfälle und deutliche Verspätungen. Dies betrifft auffallend oft die als schnell konzipierten CE-Linien, nach meiner jüngeren Erfahrung CE61/CE62, weniger auch CE64/CE65 (Elberfeld-Cronenberg), und Nebenlinienwie etwa 643. Einzelne Ihrer Mitarbeiter berichten mir übereinstimmend, die Personaldecke sei zu dünn, um Ausfälle einzelner Mitarbeiter auszugleichen. Wegen spürbaren Personalmangels - nicht, wie Sie schreiben, vereinzelt wegen Krankheit - würden häufig Fahrten ausfallen. Viele Fahrer berichten anschaulich, dass in manchen Stoßzeiten vergeblich wartende Passagiere an Haltestellen stünden. Teilweise berichtet man mir von gezielten Maßnahmen, um Einsparungen herbeizuführen sowie nicht ausgeglichenen Überstunden. Ihre Fahrer sehen dies unmittelbar mit der Politikder WSW verknüpft, die CE-Linien ab März/April 2013auszudünnen.Der Vorgang, auf den sich Ihr Schreiben bezieht, lief tatsächlich so ab: Am 1. 2. 2013 wartete ich an der Morianstraße in Wuppertalmit meinem x Monate alten Sohn nachmittags auf eine Busverbindung nach Ronsdorf, Haltestelle Kniprodestraße. Ich beabsichtigte, den Bus CE62 um 15:47 Uhr zu nehmen. Wir waren auf dem Weg zu einer Nachmittagsveranstaltung für Eltern mit Kleinstkindern, die ich verpasste:Die CE62um 15:47 Uhrfuhr nicht, ebenso nicht die nachfolgenden Busse der Linie CE62:- 16:07 Uhr- 16:27 UhrEs fuhren ferner nicht die Busse der Linie 620um- 15:55 Uhr- 16:15 Uhr- 16:35 Uhrdie Haltestelle an. Ihr Schreiben legt nahe, dass es sich nur um vereinzelte Fälle handele. Unmittelbar bei meinem nächsten Fahrtantritt mit der WSW eine Woche später kam es wiederum zu einem Ausfall. Am Freitag, 8. 2. 2013, fiel Linie CE61 um 15:38 Uhran der Haltestelle Am Stadtbahnhof in Richtung Barmenaus. Mit ca. 25 MinutenVerspätung erreichte ich mit der Linie 640(ab 15:48 Uhr) gegen 16:25 Uhrden Alten Markt in Barmen.Heute, am 21. 2. 2013, fielen die Busse der Linie CE64von Wuppertal Hbf in Richtung Cronenbergum 06:40 Uhr und um 07:00 Uhr aus. Ein Fahrgast erreichte deshalb nicht pünktlich seine Arbeitsstelle in Solingen. Würde ich häufiger fahren, würdeich vermutlich noch häufiger dadurch Zeit verlieren. Ich bitte Sie daher freundlich, Ihre Dienstleistungen bestmöglich fahrplangemäß anzubieten.'
    # main(testQuery)
