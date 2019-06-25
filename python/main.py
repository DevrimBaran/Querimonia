from flask import Flask, request, jsonify

# TODO better directory structutre
import sentiment_analyse
import wordvector


app = Flask(__name__)

# TODO routes in seperate dir
@app.route('/python/sentiment_analyse', methods=['POST'])
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

@app.route('/python/vector_calculation', methods=['POST'])
def return_wortvektor():
    # TODO rename  operation
    # {"vector1": "wrong json",  "operation": "+", "vector2": "wrong json",  "operation": "+", "textkorpus": "beschwerden3kPolished.bin"}
    # {"vector1": "wrong json", "operation": "+", "vector2": "wrong json", "operation2": "+", "vector3": "wrong json", "textcorpus": "beschwerden3kPolished.bin"}
    # TODO move into seperate file
    # TODO error handling 
    # TODO select textcorpus
    content = request.get_json()
    vector1 = content['vector1']
    vector2 = content['vector2']
    operation1 = content['operator1']
    textcorpus = None
    if 'textcorpus' in content:
        textcorpus = content['textcorpus']
    # vec3
    if 'vector3' and 'operator2' in content:
        vector3 = content['vector3']
        operation2 = content['operator2']
        similiarities = wordvector.Calc.calculate(operation1, vector1, vector2, operation2, vector3, textcorpus=textcorpus)
    else:
        # just for vec1 and vec2
        similiarities = wordvector.Calc.calculate(operation1, vector1, vector2, textcorpus=textcorpus)
    result = {}
    for e in similiarities:
        result[e[0]] = e[1]
    return jsonify(result)

@app.route('/python/vector_calculation_verbose', methods=['POST'])
def return_wortvektor_verbose():
    # returns 100 results --> copied from not verbose 
    # TODO rewrite
    # TODO rename  operation
    # {"vector1": "wrong json",  "operation": "+", "vector2": "wrong json",  "operation": "+", "textcorpus": "beschwerden3kPolished.bin"}
    # {"vector1": "wrong json", "operation": "+", "vector2": "wrong json", "operation2": "+", "vector3": "wrong json", "textcorpus": "beschwerden3kPolished.bin"}
    # TODO move into seperate file
    # TODO error handling 
    # TODO select textcorpus
    content = request.get_json()
    textcorpus = None
    if 'textcorpus' in content:
        textcorpus = content['textcorpus']
    vector1 = content['vector1']
    vector2 = content['vector2']
    operation1 = content['operator1']
    # vec3
    if 'vector3' and 'operator2' in content:
        vector3 = content['vector3']
        operation2 = content['operator2']
        similiarities = wordvector.Calc.calculate(operation1, vector1, vector2, operation2, vector3, verbose=True, textcorpus=textcorpus)
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
        similiarities = wordvector.Calc.calculate(operation1, vector1, vector2, textcorpus=textcorpus)
        # transform into json for vec1, vec2
        result = {}
        for e in similiarities:
            result[e[0]] = e[1]
        return jsonify(result)

@app.route('/python/word_nn', methods=['POST'])
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
    app.run(debug=False)
