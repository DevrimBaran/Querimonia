import spacy
nlp = spacy.load("de")


def lemmatize(text):
    data = {}
    doc = nlp(text)
    for token in doc:
        data.update({token.text: token.lemma_})
    return data
