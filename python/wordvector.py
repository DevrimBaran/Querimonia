#import fasttext
from gensim.models.wrappers import FastText
import numpy as np
import pdb


model_beschwerden3kPolished = FastText.load_fasttext_format("../beschwerden3kPolished.bin")
model_cc_de_300 = FastText.load_fasttext_format("../beschwerden3kPolished.bin")
model_ngram_ger = FastText.load_fasttext_format("../beschwerden3kPolished.bin")
model_beschwerden_CAT_leipzig = FastText.load_fasttext_format("../beschwerden3kPolished.bin")
model_leipzig_Corpora_collection_1M = FastText.load_fasttext_format("../beschwerden3kPolished.bin")

#basepath = "../wortvektoren/fastText/models/"
#model_beschwerden3kPolished = FastText.load_fasttext_format(basepath + "beschwerden3kPolished.bin")
#model_cc_de_300 = FastText.load_fasttext_format(basepath + "cc.de.300.bin")
#model_ngram_ger = FastText.load_fasttext_format(basepath + "ngram_ger.bin")
#model_beschwerden_CAT_leipzig = FastText.load_fasttext_format(basepath + "BeschwerdenCATLeipzig.bin")
#model_leipzig_Corpora_collection_1M = FastText.load_fasttext_format(basepath + "leipzigCorporaCollection1M.bin")

models = [model_beschwerden3kPolished, model_cc_de_300, model_ngram_ger, model_beschwerden_CAT_leipzig\
        , model_leipzig_Corpora_collection_1M]
model = model_beschwerden3kPolished


class Calc:
    @staticmethod
    def calculate(operation1, vec1, vec2, operation2=None, vec3=None, verbose=False, textcorpus=None):
        # TODO better strucure
        # TODO rewrite and find better strategy for verbose
        if textcorpus != None:
            model = set_corpus(textcorpus)
        if   operation1 == '+': result = Calc.add(vec1, vec2)
        elif operation1 == "-": result = Calc.sub(vec1, vec2)
        elif operation1 == "x": result = Calc.mult(vec1, vec2)
        elif operation1 == "/": result = Calc.div(vec1, vec2)
        else: print('Bad Argument: %s.' % (operation1))
        result = Calc.getword(result)
        # TODO evaluate calculation with n vectors
        if vec3 != None and operation2 != None:
            # TODO alternative for verbose?
            if verbose == True:
                return Calc.calculate_vec3(result, vec3, operation2)
            else:
                # use first result to calculate result
                return Calc.calculate(operation2, result[0][0], vec3)
        return result

    @staticmethod
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
    def getword(vec):
        return model.similar_by_vector(vec)

    @staticmethod
    def normalize(vec):
        return vec / np.sqrt(np.sum(vec**2))
    
    @staticmethod
    def vectorize(word):
        return np.array(model[word])
    
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

def set_corpus(textcorpus_name):
    # TODO error 
    if   textcorpus_name == 'beschwerden3kPolished.bin': return models[0] 
    elif textcorpus_name == 'cc.de.300.bin': return models[1] 
    elif textcorpus_name == 'ngram_ger.bin': return models[2] 
    elif textcorpus_name == 'BeschwerdenCATLeipzig.bin': return models[3] 
    elif textcorpus_name == 'leipzigCorporaCollection1M.bin': return models[4] 