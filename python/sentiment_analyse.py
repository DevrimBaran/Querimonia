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
    return (analyze(query, 'SentiWS_v2.0_Positive.txt'))


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
    sentiment_value = 0.0
    negative = negation_words()
    with open(dict, 'r', encoding='utf-8') as sentis:
        for s in sentis:
            cells = s.split('\t')
            lemma = cells[0].split('|')
            value = float(cells[1].strip())
            try:
                infl = cells[2].split(',')
                # delete the \n in the last word
                infl[len(infl) - 1] = infl[len(infl) - 1].strip()
            except IndexError:
                infl = []
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
    sentiment_value = 0.0
    for arg in sys.argv[1:]:
        sentiment_value += positive_sentiment(arg) + negative_sentiment(arg)
    return sentiment_value


# for flask
def main(query):
    value = round(positive_sentiment(query) + negative_sentiment(query), 2)
    # lambda function for set the value in interval of [-1 to 1]
    value = (lambda x: x if -1.0 < x < 1.0 else (1.0 if x > 1.0 else -1.0))(value)
    return {'sentiment': value}


if __name__ == '__main__':
    print('Use test in comments in code')
    # TESTS
    # test = 'Sehr geehrte Damen und Herren, ich bin nun seit 16 Jahren treuer Kunde bei Ihnen und nutze Ihr Busangebot täglich. Bisher bin ich auch wirklich zufrieden gewesen, aber seit paar Monaten verschlechtert sich der Zustand in den Bussen sehr. Es liegt viel Müll herum, die Sitze werden mit Stiften bemalt und der Stoff wird rausgerissen. Ich weiß, dass es nicht fair ist, die letzten 16 Jahre schlecht zu reden, nur weil seit paar Monaten ein Abwärtstrend herrscht. Jedoch sehe ich es nicht mehr ein, weiterhin so viel Geld für solch verdreckte Umstände zu bezahlen. Bitte kümmern Sie sich um das Problem, sonst verlieren Sie einen treuen Kunden. Mit freundlichen Grüßen Herr Max Mustermann'
    # test_pos = 'Ich liebe Frieden und finde Blumen schön'
    # test_neg = 'Ich mag Zerstörung und Gewalt und hasse Glück'
    # neg_pos = 'Ich mag keine Pünktlichkeit und mag Höflichkeit nicht'
    # neg_neg = 'Ich mag keine Verspätungen und finde Ausfälle nicht schön'
    # print(main(test))
    # for cmd
    # print("Sentiment Value is: " + (str)(parse_arg()))
