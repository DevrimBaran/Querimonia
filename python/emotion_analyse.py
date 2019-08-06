import string
from enum import Enum
from nltk.data import load
from nltk.tokenize.treebank import TreebankWordTokenizer
from pathlib import Path
from collections import defaultdict

# This is an approach for emotion analysis in the german language
# created by phuszár


# Constants
datafolder = Path('data/')


class emotions(Enum):
    Ekel = 'Ekel.txt'
    Freude = 'Freude.txt'
    Furcht = 'Furcht.txt'
    Trauer = 'Trauer.txt'
    Ueberraschung = 'Ueberraschung.txt'
    Verachtung = 'Verachtung.txt'
    Wut = 'Wut.txt'


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


def stopword_filter(query):
    file_to_open = datafolder / 'stopWords.txt'
    with open(file_to_open, 'r', encoding='utf-8') as f:
        stopWords = [''.join(treebank_tokenizer(t))
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
    if not dict:
        dict['Neutral'] = '0'
        return dict
    return dict


def emotion_analysis(query):
    emotions_d = defaultdict(int)
    tokens = treebank_tokenizer(query)
    swFiltered = stopword_filter(tokens)
    for emo in emotions:
        file_to_open = datafolder / emo.value
        with open(file_to_open, 'r', encoding='utf-8') as file:
            emoList = [''.join(treebank_tokenizer(t))
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

    # testQuery = 'Vielen Dank für Ihre Rückmeldung, es hat mich sehr gefreut, dass Sie meine Kritik an den Fahrer weitergeleitet haben. Das Busfahren ist weiterhin keine Freude. Heute Morgen fiel der CE 62 aus um 08.13 Uhr in Richtung statt. Und heute nachmittag der CE62 17.37 ab Karlsplatz Richtung Eckbusch. Zweimal am Tag, nicht schön. Inzwischen habe ich aber auch wieder jede Menge nette und hilfsbereite Busfahrer auf den Linien angetroffen, da bin ich immer schnell getröstet und wünsche alle eine gute Fahrt'
    # print(main(testQuery))
