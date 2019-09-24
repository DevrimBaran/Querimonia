import pandas
import pickle


def classify(text):
    texts = [text]

    # create a dataframe using texts and lables
    inputDF = pandas.DataFrame()
    inputDF['text'] = texts

    pkl_vectorizer = "data/pickle_CountVector_model.pkl"
    with open(pkl_vectorizer, 'rb') as file:
        pickle_count_vec_model = pickle.load(file)

    # transform the training and validation data using count vectorizer object
    input_data = pickle_count_vec_model.transform(inputDF['text'])

    pkl_model = "data/pickle_model.pkl"
    with open(pkl_model, 'rb') as file:
        pickle_model = pickle.load(file)

    result = pickle_model.predict(input_data)[0]

    if result == 0:
        label = "Fahrer unfreundlich"
    elif result == 1:
        label = "Fahrt unpünktlich"
    elif result == 2:
        label = "Fahrt nicht erfolgt"
    elif result == 3:
        label = "Nicht an Haltestelle gehalten"
    else:
        label = "Sonstiges"

    return label
