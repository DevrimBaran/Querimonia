import fasttext
from gensim.models.wrappers import FastText
import numpy as np
import sys

#print(sys.argv)

# Entferne das erste Element (das der Befehlsname ist)
sys.argv.pop(0)

# Das zweite Element ist dasd erste Argument, das Modell nämlich
model = FastText.load_fasttext_format(sys.argv.pop(0))

def normalize( vec ):
    return vec / np.sqrt(np.sum(vec**2))

def vectorize( word ):
    return np.array(model[word])

def getword(vec):
    return model.similar_by_vector(vec)

def add(vec1, vec2):
    #print("Addition")
    return normalize(vectorize(vec1) + vectorize(vec2))

def sub(vec1, vec2):
    #print("Subtraktion")
    return normalize(vectorize(vec1) - vectorize(vec2))

def mult(vec1, vec2):
    #print("Multiplikation)
    try: 
        vec1 = float(vec1)
    except:
        vec1 = vectorize(vec1)
    try: 
        vec2 = float(vec2)
    except:
        vec2 = vectorize(vec2)
    return normalize(vec1 * vec2)

def div(vec1, vec2):
    #print("Division")
    try: 
        vec1 = float(vec1)
    except:
        vec1 = vectorize(vec1)
    try: 
        vec2 = float(vec2)
    except:
        vec2 = vectorize(vec2)
    return normalize(vec1 / vec2)

def chooseCalc(arg, vec1, vec2):
    if arg == "+": return add(vec1, vec2)
    elif arg == "-": return sub(vec1, vec2)
    elif arg == "x": return mult(vec1, vec2)
    elif arg == "/": return div(vec1, vec2)
    else: print('Bad Argument: %s. Kann es sein dass an Stelle von x für die Multiplikation * genutzt wurde?' % (arg))

def parse( array ):
    for i in range(1, len(array), 2):
        if len(array) >= 3:
            vec1 = array.pop(0)
            arg = array.pop(0)
            vec2 = array.pop(0)
            array.insert(0, getword(chooseCalc(arg, vec1, vec2))[0][0])
            #print(array)
        else:
            #print(" Es ist noch übrig: %s ", sys.argv)
            break
    else:
        return array[0]

result = parse(sys.argv)
#result = model['bahn']

#vec3 = np.array(model['auto'])
#Trainiere ein neues Modell so:
#model = fasttext.skipgram('fastText/data/beschwerden3k.txt', 'beschwerden3kCompat')
#model = fasttext.load_model('beschwerden3kCompat.bin')

#print(sys.argv.pop(0))
#print(sys.argv)
print(getword(result))
#print(model.words)# list of words in dictionary