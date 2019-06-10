import string
import sys
import nltk
import argparse


# Sarcasm, Irony not detected. For example: 'Ich hasse dich und liebe dich nicht' has value > 0


def negative_sentiment(query):
    tokens = filter_query(query)
    sentiment_value_negative = 0

    for j in tokens:
        with open('SentiWS_v2.0_Negative.txt', 'r' , encoding='utf-8') as sentis:
            for s in sentis:
                cells = s.split('\t')
                lemma = cells[0].split('|')[0]

                value = float(cells[1].strip())

                infl = cells[2].split(',')
                # delete the \n in the last word
                infl[len(infl) - 1] = infl[len(infl) - 1].strip()
                # loop over inflections
                for i in infl:
                    # if token matches with one of infl.
                    if j == i:
                        #print(j, '&', value)
                        sentiment_value_negative += value
                # if token matches with lemma
                if j == lemma:
                    #print(j, '&', value)
                    sentiment_value_negative += value

    return sentiment_value_negative


def positive_sentiment(query):
    tokens = filter_query(query)
    sentiment_value_positive = 0

    for j in tokens:
        with open('SentiWS_v2.0_Positive.txt', 'r' , encoding='utf-8') as sentis:
            for s in sentis:
                cells = s.split('\t')
                lemma = cells[0].split('|')[0]

                value = float(cells[1].strip())

                infl = cells[2].split(',')
                # delete the \n in the last word
                infl[len(infl) - 1] = infl[len(infl) - 1].strip()
                # loop over inflections
                for i in infl:
                    # if token matches with one of infl.
                    if j == i:
                        #print(j, '&', value)
                        sentiment_value_positive += value
                # if token matches with lemma
                if j == lemma:
                    #print(j, '&', value)
                    sentiment_value_positive += value

    return sentiment_value_positive


def filter_query(query):
    # tokenize, erase punctuation(#, $, %, ., ~, ...), erase whitespaces
    tokens = nltk.tokenize.word_tokenize(query, 'german')
    tokens = [''.join(i for i in s if i not in string.punctuation)
              for s in tokens]
    tokens = list(filter(None, tokens))
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

    test_pos = "Ich mag es mit der Bahn zu fahren und liebe Zugausfälle "
    test_neg = "Ich hasse Zerstörung und Gewalt"
    print("Call main(param) to get senti value for param \n Or use examples in comments")

    #sentiment_value = positive_sentiment(test_neg) + negative_sentiment(test_neg)
    #print(sentiment_value)

    #In cmd " python sentiment_analyse.py *query* "
    #print("Sentiment Value is: " + (str)(parse_arg()))










