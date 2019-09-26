import pandas
import pickle


def classify(text):
    texts = [text]

    # create a dataframe using texts and lables
    inputDF = pandas.DataFrame()
    inputDF['text'] = texts

    pkl_vectorizer = "python/data/pickle_CountVector_model.pkl"
    with open(pkl_vectorizer, 'rb') as file:
        pickle_count_vec_model = pickle.load(file)

    # transform the training and validation data using count vectorizer object
    input_data = pickle_count_vec_model.transform(inputDF['text'])

    pkl_model = "python/data/pickle_model.pkl"
    with open(pkl_model, 'rb') as file:
        pickle_model = pickle.load(file)

    probabilities = {"Fahrer unfreundlich": pickle_model.predict_proba(input_data)[0][0],
                     "Fahrt unpünktlich": pickle_model.predict_proba(input_data)[0][1],
                     "Fahrt nicht erfolgt": pickle_model.predict_proba(input_data)[0][2],
                     "Nicht an Haltestelle gehalten": pickle_model.predict_proba(input_data)[0][3],
                     "Sonstiges": pickle_model.predict_proba(input_data)[0][4]}

    return probabilities
