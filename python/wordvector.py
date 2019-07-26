from gensim.models.wrappers import FastText
import numpy as np
import logging
from collections import defaultdict
import os

# setup
if os.getenv("DEPLOY", "not found") == "not found":
    # Pfad zu den Modellen
    basepath = "/home/beschwerdemanagement/wortvektoren/fastText/models/"
    # Lade Modelle
    model_beschwerden3kPolished = FastText.load_fasttext_format(
        basepath + "beschwerden3kPolished.bin"
    )
    model_cc_de_300 = FastText.load_fasttext_format(basepath + "cc.de.300.bin")
    model_ngram_ger = FastText.load_fasttext_format(basepath + "ngram_ger.bin")
    model_beschwerden_CAT_leipzig = FastText.load_fasttext_format(
        basepath + "BeschwerdenCATLeipzig"
    )
    model_leipzig_Corpora_collection_1M = FastText.load_fasttext_format(
        basepath + "leipzigCorporaCollection1M.bin"
    )
    # base path for predict words
    predict_basepath = "/home/beschwerdemanagement/wortvektoren/fastText/predictionLists/"
else:
    # Lade Modelle
    print("Lade dir ein Beispielkorpus herunter und lege es zwei Ordner weiter oben ab")
    print("Für Korpus: https://querimonia.iao.fraunhofer.de/infra/data/beschwerden3kPolished.bin")
    print("Für Predict Words: https://querimonia.iao.fraunhofer.de/infra/data/beschwerden3kPolished.txt")
    path = "../../model/beschwerden3kPolished.bin"
    model_beschwerden3kPolished = FastText.load_fasttext_format(path)
    model_cc_de_300 = FastText.load_fasttext_format(path)
    model_ngram_ger = FastText.load_fasttext_format(path)
    model_beschwerden_CAT_leipzig = FastText.load_fasttext_format(path)
    model_leipzig_Corpora_collection_1M = FastText.load_fasttext_format(path)
    # base path for predict words
    predict_basepath = "../../"

# standard model
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
        try:
            result = np.array(model[word])
        except KeyError:
            result = np.array(model["bus"])
            for entry in range(0, len(result)):
                result[entry] = 0
        return result

    @staticmethod
    def predict_words(query, model_name, limit):
        word_file = model_name.split(".")[0] + ".txt"
        with open(predict_basepath + word_file, "r") as model_file:
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

    @staticmethod
    def analogy(word1, word2, word3, model_name):
        vec1 = Calc.vectorize(word1, model_name)
        vec2 = Calc.vectorize(word2, model_name)
        vec3 = Calc.vectorize(word3, model_name)
        result = np.add(np.subtract(vec1, vec2), vec3)
        words = Calc.getword(result, model_name)
        json_map = defaultdict(float)
        for pair in words:
            json_map[pair[0]] = pair[1]
        return json_map
