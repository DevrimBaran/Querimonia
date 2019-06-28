from flask import Flask, request, jsonify

# TODO better directory structutre
import sentiment_analyse
import wordvector
import numpy as np


app = Flask(__name__)

# TODO routes in seperate dir
@app.route('/sentiment_analyse', methods=['POST'])
def return_sentiment():
    # Example: python -c "import requests; res = requests.post('http://localhost:5000/sentiment', json={'text':'Ich hasse Hawaiipizza'}); print(res.json())"
    # get complaint text
    content = request.get_json()
    if 'text' not in content:
        # TODO create error class
        return jsonify({"error": "wrong json"})
    query = content['text']
    sentiment_value = sentiment_analyse.main(query)
    #sentiment_value = 0.5
    return jsonify({"sentiment": sentiment_value})

@app.route('/vector_calculation', methods=['POST'])
def return_wortvektor():
    # TODO rename  operation
    # {"vector1": "wrong json",  "operation": "+", "vector2": "wrong json",  "operation": "+", "textkorpus": "beschwerden3kPolished.bin"}
    # {"vector1": "wrong json", "operation": "+", "vector2": "wrong json", "operation2": "+", "vector3": "wrong json", "textkorpus": "beschwerden3kPolished.bin"}
    # TODO move into seperate file
    # TODO error handling 
    # TODO select textkorpus
    content = request.get_json()
    vector1 = content['vector1']
    vector2 = content['vector2']
    operation1 = content['operator1']
    # vec3
    if 'vector3' and 'operator2' in content:
        vector3 = content['vector3']
        operation2 = content['operator2']
        similiarities = wordvector.Calc.calculate(operation1, vector1, vector2, operation2, vector3)
        # transform into json for vec1, vec2, vec3
        result = {}
        #pdb.set_trace()
        # build correct json without lists
        for answer in similiarities:
            # TODO better names
            subresult = {}
            for subsubresult in similiarities[answer]:
                subresult[subsubresult[0]] = subsubresult[1]
            result[answer] = {}
            result[answer] = subresult
            

            #result[answer][similiarities[answer]] = [similiarities[answer[1]]]
            #print(similiarities[answer])
            #print(word)
            #result[answer][word[0]] = word[1]
        print(result)
        return jsonify(result)
    else:
        # vec1 and vec2
        similiarities = wordvector.Calc.calculate(operation1, vector1, vector2)
        # transform into json for vec1, vec2
        result = {}
        for e in similiarities:
            result[e[0]] = e[1]
        return jsonify(result)

    #return jsonify(result)

@app.route('/word_to_vec', methods=['POST'])
def return_vektor():
    content = request.get_json()
    word = content['word']
    modelName = content['model']
    result = wordvector.Calc.vectorize(word, modelName)
    return jsonify(result.tolist())

@app.route('/vec_to_word', methods=['POST'])
def return_wort():
    content = request.get_json()
    vector = content['vector']
    modelName = content['model']
    result = wordvector.Calc.getword(np.asarray(vector), modelName)
    # print(result)
    return jsonify(result)

@app.route('/word_nn', methods=['POST'])
# nearest neighbour
def return_word():
    # {"vector1": "wrong json", "textkorpus": "beschwerden3kPolished"}
    # TODO error handling 
    content = request.get_json()
    vector1 = content['vector1']
    # TODO implement textkorpus
    similiarities = wordvector.Calc.getword(vector1)
    result = {}
    # TODO rename
    # transform into json
    for e in similiarities:
        result[e[0]] = e[1]
    print(result)
    return jsonify(result)


if __name__ == '__main__':
    app.run(debug=True)
