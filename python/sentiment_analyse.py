import string
import sys
from nltk.tokenize import word_tokenize
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
    f = open(file_to_open, 'r')
    stopWords = [''.join(t)
                 for t in f]
    for i, s in enumerate(stopWords):
        stopWords[i] = s.strip()
    return list(filter(None, [''.join(q for q in t if t not in stopWords)
                              for t in query]))


def negation_words():
    negdict = datafolder / 'negationswoerter.txt'
    negwords = open(negdict, 'r', encoding='utf-8')
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
    if value > 0.8:
        return {'Glücklich': value}
    elif value < 0.8 and value > 0.6:
        return {'Fröhlich': value}
    elif value < 0.6 and value > 0.4:
        return {'Zufrieden': value}
    elif value < 0.4 and value > 0.1:
        return {'Erfreut': value}

    elif value < 0.1 and value > -0.2:
        return {'Neutral': 0}

    elif value < -0.2 and value > -0.4:
        return {'Betrübt': value}
    elif value < -0.4 and value > -0.6:
        return {'Unzufrieden': value}
    elif value < -0.6 and value > -0.8:
        return {'Verärgert': value}
    elif value < -0.8:
        return {'Sauer': value}


if __name__ == '__main__':
    print('In cmd " python sentiment_analyse.py *query* \n or use examples in comments')

    # for example
    # test = 'Sehr geehrte Damen und Herren,der vorstehende Bus ist - mal wieder - ausgefallen. Gestern fiel der Bus um 08.07 aus. Ich hoffe, dass ich auf die heutige Mail von Ihnen auch einmal eine Antwort erhalte, die letzten Hinweise auf Ausfälle der Linie 603 wurden von Ihnen ignoriert. Ich habe wirklich kein Verständnis mehr für die dauernden Ausfälle der Linie 603. Gehen Sie bitte davon aus, dass es durchaus Kunden gibt, die zur Arbeit müssen oder einen Zug erreichen müssen. Ich erwarte für die regelmäßigen Fahrpreiserhöhungen auch die entsprechende Leistung Ihrerseits!!!Ich erwarte nunmehr eine Antwort und ein Angebot der Entschädigung. Vielen Dank.'
    # test_pos = 'Ich liebe Frieden und finde Blumen schön'
    # test_neg = 'Ich mag Zerstörung und Gewalt und hasse Glück'
    # neg_pos = 'Ich mag keine Pünktlichkeit und mag Höflichkeit nicht'
    # neg_neg = 'Ich mag keine Verspätungen und finde Ausfälle nicht schön'

    # print(main(test))
    # for cmd
    # print("Sentiment Value is: " + (str)(parse_arg()))
