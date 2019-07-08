import fasttext
from gensim.models.wrappers import FastText
import numpy as np
import pdb

# Pfad zu den Modellen
basepath = "../wortvektoren/fastText/models/"

# Lade Modelle
model_beschwerden3kPolished = FastText.load_fasttext_format( basepath + "beschwerden3kPolished.bin")
model_cc_de_300 = FastText.load_fasttext_format(basepath + "cc.de.300.bin")
model_ngram_ger = FastText.load_fasttext_format(basepath + "ngram_ger.bin")
model_beschwerden_CAT_leipzig = FastText.load_fasttext_format(basepath + "BeschwerdenCATLeipzig.bin")
model_leipzig_Corpora_collection_1M = FastText.load_fasttext_format(basepath + "leipzigCorporaCollection1M.bin")

# Standardmodell
model = model_beschwerden3kPolished

class Calc:

    @staticmethod
    def set_model(modelName):
        if modelName == "beschwerden3kPolished.bin":
            return model_beschwerden3kPolished
        elif modelName == "cc.de.300.bin":
            return model_cc_de_300
        elif modelName == "ngram_ger.bin":
            model = model_ngram_ger
        elif modelName == "BeschwerdenCATLeipzig.bin":
            return model_beschwerden_CAT_leipzig
        elif modelName == "leipzigCorporaCollection1M.bin":
            return model_leipzig_Corpora_collection_1M
        else:
            print("Modell nicht erkannt.")
        return model_beschwerden3kPolished


    @staticmethod
    def getword(vec, modelName):
        model = Calc.set_model(modelName)
        return model.similar_by_vector(vec)

    @staticmethod
    def vectorize( word, modelName):
        model = Calc.set_model(modelName)
        return np.array(model[word])

    # Alles Folgende ist obsolet weil die Logik im Frontend gerechnet wird

    @staticmethod
    def calculate(operation1, vec1, vec2, operation2=None, vec3=None):
        # TODO better strucure
        if   operation1 == '+': result = Calc.add(vec1, vec2)
        elif operation1 == "-": result = Calc.sub(vec1, vec2)
        elif operation1 == "x": result = Calc.mult(vec1, vec2)
        elif operation1 == "/": result = Calc.div(vec1, vec2)
        else: print('Bad Argument: %s.' % (operation1))
        result = Calc.getword(result)
        # TODO evaluate calculation with n vectors
        if vec3 != None and operation2 != None:
            return Calc.calculate_vec3(result, vec3, operation2)
        return result

    def calculate_vec3(subresult, vec3, operation2):
        result_multiple = {}
        for e in subresult:
            # TODO better structue
            if   operation2 == '+': result = Calc.add(e[0], vec3)
            elif operation2 == "-": result = Calc.sub(e[0], vec3)
            elif operation2 == "x": result = Calc.mult(e[0], vec3)
            elif operation2 == "/": result = Calc.div(e[0], vec3)
            else: print('Bad Argument: %s' % (operation2))
            result = Calc.getword(result)
            result_multiple[e[0]] = result
        return result_multiple

    @staticmethod
    def normalize( vec ):
        return vec / np.sqrt(np.sum(vec**2))
    
    
    @staticmethod
    def add(vec1, vec2):
        #print("Addition")
        return Calc.normalize(Calc.vectorize(vec1) + Calc.vectorize(vec2))
    
    @staticmethod
    def sub(vec1, vec2):
        #print("Subtraktion")
        return Calc.normalize(Calc.vectorize(vec1) - Calc.vectorize(vec2))
    
    @staticmethod
    def mult(vec1, vec2):
        #print("Multiplikation)
        try: 
            vec1 = float(vec1)
        except:
            vec1 = Calc.vectorize(vec1)
        try: 
            vec2 = float(vec2)
        except:
            vec2 = Calc.vectorize(vec2)
        return Calc.normalize(vec1 * vec2)
    
    @staticmethod
    def div(vec1, vec2):
        #print("Division")
        try: 
            vec1 = float(vec1)
        except:
            vec1 = Calc.vectorize(vec1)
        try: 
            vec2 = float(vec2)
        except:
            vec2 = Calc.vectorize(vec2)
        return Calc.normalize(vec1 / vec2)