import string
import sys
import nltk
from pathlib import Path

# Sarcasm, Irony not detected. For example: 'Ich hasse dich und liebe dich nicht' has value > 0
# created by phuszár

# ToDo: Respect to Frequency in Dictionary!

# Variables
datafolder = Path("data/")


def positive_sentiment(query):
    sentiment = analyze(query, 'SentiWS_v2.0_Positive.txt')
    return sentiment


def negative_sentiment(query):
    sentiment = analyze(query, 'SentiWS_v2.0_Negative.txt')
    return sentiment


# Check for matches in Dictionaries
def analyze(query, dict):
    tokens = filter_query(query)
    dict = datafolder / dict
    sentiment_value = 0
    for j in tokens:
        with open(dict, 'r', encoding='utf-8') as sentis:
            for s in sentis:
                cells = s.split('\t')
                lemma = cells[0].split('|')[0]

                value = float(cells[1].strip())

                infl = cells[2].split(',')
                # delete the \n in the last word
                infl[len(infl) - 1] = infl[len(infl) - 1].strip()
                # loop over inflections
                for i in infl:
                    # if token matches with one of infl or lemma.
                    if j == i or j == lemma:
                        sentiment_value += value
    return sentiment_value


def filter_query(query):
    # tokenize, erase punctuation(#, $, %, ., ~, ...), erase whitespaces
    tokens = nltk.tokenize.word_tokenize(query, 'german')
    tokens = [''.join(i for i in s if i not in string.punctuation)
              for s in tokens]
    return tokens


# parsing params von command line
def parse_arg():
    sentiment_value = 0
    for arg in sys.argv[1:]:
        sentiment_value += positive_sentiment(arg) + negative_sentiment(arg)
    return sentiment_value


# for flask
def main(query):
    return positive_sentiment(query) + negative_sentiment(query)


if __name__ == '__main__':
    print('In cmd " python sentiment_analyse.py *query* \n or use examples in comments')

    # for example
    # test_pos = 'liebe ist was schönes'
    # test_neg = 'Ich hasse Zerstörung und Gewalt'
    # print(main(test_neg))

    # for cmd
    # print("Sentiment Value is: " + (str)(parse_arg()))
