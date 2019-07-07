import string
import nltk
from enum import Enum


# This is an approach for emotion analysis in the german language
# created by phuszár

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
    tokens = nltk.tokenize.word_tokenize(t, 'german')
    tokens = [''.join(i for i in s if i not in string.punctuation)
              for s in tokens]
    return tokens


def stemmer(s):
    # CISTEM Stemmer
    tokens = tokenize(s)
    stemmer = nltk.stem.cistem.Cistem()
    stemmedTokens = [''.join(stemmer.stem(t))
                     for t in tokens]
    return stemmedTokens


def emotion_analysis(query):
    # ToDo: Analyze the emotion
    return


# for flask
def main(query):
    # return a map with percentage of emotion
    return


if __name__ == '__main__':
    print('hello')
