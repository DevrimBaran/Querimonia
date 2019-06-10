from flask import Flask, request, jsonify

# TODO better directory structutre
import matplotlib_gererator
import sentiment_analyse


app = Flask(__name__)
@app.route('/sentiment_analyse', methods=['POST'])
# TODO correct Rest API with post, get
def return_sentiment():
    # Example: python -c "import requests; res = requests.post('http://localhost:5000/sentiment', json={'text':'Ich hasse Hawaiipizza'}); print(res.json())"
    # get complaint text
    content = request.get_json()
    query = content['text']
    # TODO add SentiWS_v2.0_Negative.txt then uncomment this
    #sentiment_value = sentiment_analyse.main(query)
    sentiment_value = 0.5
    return jsonify({"sentiment": sentiment_value})

if __name__ == '__main__':
    app.run(debug=True)
