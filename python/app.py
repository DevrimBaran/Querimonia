from flask import Flask, request, jsonify
from flask_restplus import Resource, Api
import numpy as np
import sentiment_analyse
import wordvector

app = Flask(__name__)
# TODO change base url
# app.wsgi_app = PrefixMiddleware(app.wsgi_app, prefix='/python')
api = Api(
    app,
    version="0.1",
    title="Python API",
    description="API für Wortvektoren und Sentimentanaylse",
)


@api.route("/python/sentiment_analyse")
class Sentiment(Resource):
    def post(self):
        # get complaint text
        content = request.get_json()
        if "text" not in content:
            # TODO create error class
            return jsonify({"error": "wrong json"})
        query = content["text"]
        sentiment_value = sentiment_analyse.main(query)
        return jsonify({"sentiment": sentiment_value})


@api.route("/python/word_to_vec")
class Word_to_vec(Resource):
    def post(self):
        content = request.get_json()
        word = content["word"]
        model_name = content["model"]
        result = wordvector.Calc.vectorize(word, model_name)
        return jsonify(result.tolist())


@api.route("/python/vec_to_word")
class Vec_to_word(Resource):
    def post(self):
        content = request.get_json()
        vector = content["vector"]
        model_name = content["model"]
        result = wordvector.Calc.getword(np.asarray(vector), model_name)
        return jsonify(result)


@api.route("/python/predict_word")
class Predict_word(Resource):
    def post(self):
        content = request.get_json()
        query = content["query"]
        model_name = content["model"]
        result = wordvector.Calc.predict_words(query, model_name)
        return jsonify(result)


if __name__ == "__main__":
    app.run(debug=False)
