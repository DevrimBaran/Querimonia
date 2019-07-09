import string
import nltk
from enum import Enum
from pathlib import Path
from collections import defaultdict
from nltk.corpus import stopwords

# This is an approach for emotion analysis in the german language
# created by phuszár

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


def tokenize(t):
    # tokenize, erase punctuation(#, $, %, ., ~, ...), erase whitespaces
    t = nltk.tokenize.word_tokenize(t, 'german')
    return list(filter(None, [''.join(i for i in s if i not in string.punctuation)
                              for s in t]))


def tokenize_and_stem(x):
    # CISTEM Stemmer
    tokens = nltk.tokenize.word_tokenize(x, 'german')
    tokens = [''.join(i for i in s if i not in string.punctuation)
              for s in tokens]

    stemmer = nltk.stem.cistem.Cistem()
    stemmedTokens = [''.join(stemmer.stem(t))
                     for t in tokens]
    return stemmedTokens


def stopword_filter(query):
    stopWords = set(stopwords.words('german'))
    return list(filter(None, [''.join(q for q in t if t not in stopWords)
                              for t in query]))


def emotion_analysis(query):
    emotions_d = defaultdict(int)
    stemmed = tokenize_and_stem(query)
    swFiltered = stopword_filter(stemmed)
    for emo in emotions:
        file_to_open = datafolder / emo.value
        for sw in swFiltered:
            with open(file_to_open, 'r', encoding='utf-8') as file:
                list = [''.join(tokenize_and_stem(t))
                        for t in file]
                for l in list:
                    if sw == l:
                        print('Found word in: ' + emo.name + '-Dictionary')
                        emotions_d[emo.name] += 1
    # ToDo: value with respect to freqeuncy
    return dict(emotions_d)


# for flask
def main(query):
    # :return dict of possible Emotions - dict like a hashmap.
    # Form like: {'Freude': 6, 'Furcht': 1, 'Ueberraschung': 1, 'Verachtung': 6}
    emotion_analysis(query)


if __name__ == '__main__':
    print('Use test in comments in code')

    testQuery = "Ein dickes Lob an eine Fahrerin von einer Kundin. Durch den starken Schneefall sind an dem Tag mehrere Busse ausgefallen. Viele Leute standen an der Bushaltestelle Neviges Bahnhof. Eine Fahrerin der Linie 649 , bat in der Leitstelle darum, die wartenden Fahrgäste an Ihr Ziel bringen zu können nach Velbert, da der Linienweg des Busses den sie eigentlich fahren sollte, nicht befahrbar war.Die Leitstelle sagte hier zu.Die Fahrgäste waren total glücklich und stellvertretend für alle möchte die Kundin ein dickes Lob aussprechen für die vorausschauende Art und Weise, die diese Fahrerin an den Tag gelegt hat."
    print(emotion_analysis(testQuery))
