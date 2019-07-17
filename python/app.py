from flask import Flask, request, jsonify
from flask_restplus import Resource, Api, fields
import numpy as np
import sentiment_analyse
import wordvector

app = Flask(__name__)
# TODO change base url
# app.wsgi_app = PrefixMiddleware(app.wsgi_app, prefix='/python')
api = Api(app, version='0.1', title='Python API', description='API für Wortvektoren und Sentimentanaylse')


@api.route('/python/sentiment_analyse')
class Sentiment(Resource):
    def post(self):
        # Example: python -c "import requests; res = requests.post('http://localhost:5000/sentiment', json={'text':'Ich hasse Hawaiipizza'}); print(res.json())"
        # get complaint text
        content = request.get_json()
        if 'text' not in content:
            # TODO create error class
            return jsonify({"error": "wrong json"})
        query = content['text']
        sentiment_value = sentiment_analyse.main(query)
        return jsonify({"sentiment": sentiment_value})


@api.route('/python/word_to_vec')
class Word_to_vec(Resource):
    def post(self):
        content = request.get_json()
        word = content['word']
        modelName = content['model']
        result = wordvector.Calc.vectorize(word, modelName)
        return jsonify(result.tolist())


@api.route('/python/vec_to_word')
class Vec_to_word(Resource):
    def post(self):
        content = request.get_json()
        vector = content['vector']
        modelName = content['model']
        result = wordvector.Calc.getword(np.asarray(vector), modelName)
        return jsonify(result)


if __name__ == '__main__':
    app.run(debug=False)
