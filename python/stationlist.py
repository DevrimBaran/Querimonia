import numpy as np
import pandas as pd
import sqlalchemy as sqla
import pymysql

if __name__ == "__main__":
    df = pd.read_excel("data/Haltestellenliste.xlsx", usecols="A,D")
    engine = sqla.create_engine("mysql+pymysql://root:Temarai-31@localhost/querimonia_db", encoding='UTF8', echo=True)
    df.to_sql("stations", engine, if_exists='replace')
