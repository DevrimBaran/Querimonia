from gensim.models.wrappers import FastText
import numpy as np
import logging

# Pfad zu den Modellen
basepath = "/home/beschwerdemanagement/wortvektoren/fastText/models/"
# Lade Modelle
model_beschwerden3kPolished = FastText.load_fasttext_format(
    basepath + "beschwerden3kPolished.bin"
)
model_cc_de_300 = FastText.load_fasttext_format(basepath + "cc.de.300.bin")
model_ngram_ger = FastText.load_fasttext_format(basepath + "ngram_ger.bin")
model_beschwerden_CAT_leipzig = FastText.load_fasttext_format(
    basepath + "BeschwerdenCATLeipzig.bin"
)
model_leipzig_Corpora_collection_1M = FastText.load_fasttext_format(
    basepath + "leipzigCorporaCollection1M.bin"
)

# Standardmodell
model = model_beschwerden3kPolished


class Calc:
    @staticmethod
    def set_model(model_name):
        if model_name == "beschwerden3kPolished.bin":
            return model_beschwerden3kPolished
        elif model_name == "cc.de.300.bin":
            return model_cc_de_300
        elif model_name == "ngram_ger.bin":
            return model_ngram_ger
        elif model_name == "BeschwerdenCATLeipzig.bin":
            return model_beschwerden_CAT_leipzig
        elif model_name == "leipzigCorporaCollection1M.bin":
            return model_leipzig_Corpora_collection_1M
        else:
            print("Modell nicht erkannt.")
        logging.info("Set model to: {}".format(model_name))
        return model_beschwerden3kPolished

    @staticmethod
    def getword(vec, model_name):
        model = Calc.set_model(model_name)
        return model.similar_by_vector(vec)

    @staticmethod
    def vectorize(word, model_name):
        model = Calc.set_model(model_name)
        return np.array(model[word])

    @staticmethod
    def predict_words(query, model_name, limit):

        # Pfad zu den Listen
        basepath = "/home/beschwerdemanagement/wortvektoren/fastText/predictionLists/"

        word_file = model_name.split(".")[0] + ".txt"
        model_file = open(basepath + word_file, "r")
        words = model_file.read().split("\n")
        result = []

        if len(query) >= 3:
            for word in words:
                if word[0:len(query)] == query:
                    if len(result) < int(limit):
                        result.append(word)
                    else:
                        break

        return result
