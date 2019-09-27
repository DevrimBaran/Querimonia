# Studienprojekt 2019 Mensch-Maschine-Kollaboration Beschwerdemanagement

Studienprojekt Sommersemester 2019
Mensch-Maschine-Kollaboration mit selbstlernenden Textminingverfahren am Beispiel des Beschwerdemanagements 


# README (Deutsch)

### Beschreibung:
Querimonia ist eine webbasierte Plattform für die Erprobung und Anwendung von Verfahren zur Aufbereitung und Analyse von Text im Beschwerde-Management, die im Rahmen des Studienprojektes der Universität Stuttgart im Sommersemester 2019 entwickelt wurde. Das Studienprojekt, dass von Studenten der Studiengänge Softwaretechnik B.Sc. und Medieninformatik B.Sc. durchgeführt wurde, wird vom Fraunhofer-Institut für Arbeitswirtschaft und Organisation (IAO) und dem Institut für Arbeitswissenschaft und Technologiemanagement (IAT) der Universität Stuttgart geleitet.

Detailliertere Informationen zum Projekt sind im Wiki von Querimonia nachzulesen.

##### Frontend:

1. Verwendete Technologien

- Node und NPM

- React

- Sass

- Redux

- Git

2. Installation

- Entwicklungsumgebung oder Editor installieren

- Node und NPM installieren

- Git installieren

- Repository clonen (git clone https://stuprogit.iao.fraunhofer.de/mmk-bm/stupro-2019-mmk-bm)

- In Frontend Branch wechseln (git checkout frontend)

- Module installieren (npm install oder npm ci)

3. Starten

- In den Frontend-Ordner wechseln (cd frontend)

- Plattform starten (npm start)



##### Backend:

1. Verwendete Technologien

- Maven

- MySQL Community Edition

- Hibernate

- Spring Boot

- Swagger Editor

- IntelliJ Plugins (Checkstyle und FindBugs) – optional

- Flask Server

- FastText

- Spacy

- Scikit-learn

2. Installation

- Entwicklungsumgebung oder Editor installieren

- Java installieren

- Git installieren

- Checkstyle und FindBugs installieren (optional)

- MySQL-Community-Server installieren

- Datenbank “querimonia_db“ erstellen (create database “querimonia_db“)

- Repository klonen (git clone https://stuprogit.iao.fraunhofer.de/mmk-bm/stupro-2019-mmk-bm)

- In Backend-Branch wechseln (git checkout backend)

- Python:

  * git clone https://stuprogit.iao.fraunhofer.de/mmk-bm/stupro-2019-mmk-bm.git

  * git checkout python_poc

  * cd python

  * pipenv install

  * pipenv shell

  * python -m spacy download de

  * Beispielkorpus herunterladen und in Verzeichnis kopieren: https://querimonia.iao.fraunhofer.de/infra/data/beschwerden3kPolished.txt und https://querimonia.iao.fraunhofer.de/infra/data/beschwerden3kPolished.bin

•	Starten
- Aufbauen mit mvn package jar
- Ausführen mit java -jar
- python app.py # starte den flask Server, alternativ pipenv run python app.py



# README (English)

### Description:
Querimonia is a web-based platform for the testing and application of procedures for the preparation and analysis of text in complaint management, which was developed as part of the study project of the University of Stuttgart in the summer semester 2019. The study project, which was conducted by students of the Software Engineering B.Sc. and Media Informatics B.Sc. programs, is led by the Fraunhofer Institute for Industrial Engineering and Organization (IAO) and the Institute for Industrial Engineering and Technology Management (IAT) of the University of Stuttgart.

More detailed information on the project can be found in the Querimonia Wiki.

##### Frontend:

1. Used technologies

- Node and NPM

- React

- Sass

- Redux

- Git

2. Installation

- Install the development environment or an editor

- Install Node and NPM

- Install Git

- Clone repository (git clone https://stuprogit.iao.fraunhofer.de/mmk-bm/stupro-2019-mmk-bm)

- Switch to frontend-branch (git checkout frontend)

- Install modules (npm install or npm ci)

3. Start

- Change to the frontend folder (cd frontend)

- Start platform (npm start)



##### Backend:

1. Used technologies

- Maven

- MySQL Community Edition

- Hibernate

- Spring Boot

- Swagger Editor

- IntelliJ plugins (Checkstyle and FindBugs) – optional

- Flask Server

- FastText

- Spacy

- Scikit-learn

2. Installation

- Install the development environment or an editor

- Install Java

- Install Git

- Install Checkstyle and FindBugs (optional)

- Install MySQL Community Server

- Create database "querimonia_db" (create database "querimonia_db")

- Clone repository (git clone https://stuprogit.iao.fraunhofer.de/mmk-bm/stupro-2019-mmk-bm)

- Switch to backend-branch (git checkout backend)

- Python:

  * git clone https://stuprogit.iao.fraunhofer.de/mmk-bm/stupro-2019-mmk-bm.git
  * git checkout python_poc
  * cd python
  * pipenv install
  * pipenv shell
  * python -m spacy download de
  * Download example corpus and copy it in the directory: https://querimonia.iao.fraunhofer.de/infra/data/beschwerden3kPolished.txt andhttps://querimonia.iao.fraunhofer.de/infra/data/beschwerden3kPolished.bin

3. Start

- Build with mvn package jar

- Execute with java -jar

- python app.py # start the flask server, optional pipenv run python app.py


