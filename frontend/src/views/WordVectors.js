/**
 * This class creates the WordVector view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Block from './../components/Block';
import Input from '../components/Input';
import Row from '../components/Row';
import Content from './../components/Content';
import ListTable from './../components/ListTable';
import Autocomplete from '../components/Autocomplete';

import Api from '../utility/Api';

class WordVectors extends Component {
  constructor () {
    super();
    this.corpora = [
      { label: 'Beispielbeschwerden', value: 'beschwerden3kPolished.bin' },
      { label: 'FastText', value: 'cc.de.300.bin' },
      { label: 'Google nGrams', value: 'ngram_ger.bin' },
      { label: 'Beispielbeschwerden & Leipzig', value: 'BeschwerdenCATLeipzig.bin' },
      { label: 'Leipzig (1M)', value: 'leipzigCorporaCollection1M.bin' }
    ];
    this.state = {
      result: [],
      analogy: [],
      value: '',
      error: false,
      corpora: this.corpora[0].value
    };
  };

  parseText = (text) => {
    const precedence = (o) => {
      if (o === '*' || o === '/') {
        return 3;
      }
      if (o === '+' || o === '-') {
        return 2;
      }
      return 0;
    };
    // Shunting-yard algorithm
    let chars = text.replace(/\s+/g, '').split('');
    let outputQueue = [];
    let operatorStack = [];
    while (chars.length > 0) {
      // read a token
      let char = chars.shift();
      let token = char;
      if (char.match(/\w/)) {
        while (chars.length > 0 && chars[0].match(/\w|[äöüßÄÖÜ]/)) {
          token += chars.shift();
        }
      }
      if (token === '+' || token === '-' || token === '*' || token === '/') {
        while (precedence(operatorStack[operatorStack.length - 1]) >= precedence(token) &&
          operatorStack[operatorStack.length - 1] !== '(') {
          outputQueue.push(operatorStack.pop());
        }
        operatorStack.push(token);
      } else if (token === '(') {
        operatorStack.push(token);
      } else if (token === ')') {
        while (operatorStack[operatorStack.length - 1] !== '(') {
          outputQueue.push(operatorStack.pop());
          if (operatorStack.length === 0) {
            this.setState({ error: 'parentheses missmatch!' });
            return;
          }
        }
        operatorStack.pop();
      } else {
        // token is a word
        outputQueue.push(token);
      }
    }
    while (operatorStack.length > 0) {
      if (operatorStack[operatorStack.length - 1] === '(' || operatorStack[operatorStack.length - 1] === ')') {
        this.setState({ error: 'parentheses missmatch!' });
        return;
      } else {
        outputQueue.push(operatorStack.pop());
      }
    }

    this.setState({ error: null, analogy: outputQueue });
    return outputQueue;
  };

  calculateOnEnter = (e) => {
    if (e.keyCode === 13) {
      this.calculate();
    }
  };

  word2vec = (word) => {
    return Api.post('/python/word_to_vec', { word: word, model: this.state.corpora });
  }
  vec2word = (vec) => {
    return Api.post('/python/vec_to_word', { vector: vec, model: this.state.corpora })
      .then((response) => {
        return {
          result: response.map(r => {
            r[1] = (r[1] * 100).toFixed(2) + '%';
            return r;
          })
        };
      });
  };

  calculate = () => {
    const value = document.getElementById('analogy').value;
    if (!value) return;
    let analogy = this.parseText(value);// this.state.analogy.slice();
    const normalize = (x) => {
      let len = Math.sqrt(x.reduce((len, a) => len + a * a, 0));
      return x.map(a => a / len);
    };
    if (this.state.error) return;
    // this.analogy is postfix expression
    let words = analogy.filter((token) => {
      return !(token === '+' || token === '-' || token === '*' || token === '/');
    });
    Promise.all(
      words.map(w => { return this.word2vec(w); })
    ).then(wordvectors => {
      return wordvectors.reduce((obj, vector, index) => {
        obj[words[index]] = vector;
        return obj;
      }, {});
    }).then(dictionary => {
      // Reverse Polish
      let a; let b; let token; let stack = [];
      let postfix = analogy;
      while (postfix.length > 0) {
        token = postfix.shift();
        if (token === '+' || token === '-' || token === '*' || token === '/') {
          b = stack.pop();
          a = stack.pop();
          let x = [];
          for (let i = 0; i < a.length; i++) {
            if (token === '+') {
              x.push(a[i] + b[i]);
            } else
            if (token === '-') {
              x.push(a[i] - b[i]);
            } else
            if (token === '*') {
              x.push(a[i] * b[i]);
            } else
            if (token === '/') {
              x.push(a[i] / b[i]);
            }
          }
          // let len = x.reduce((len, a) => len + a * a, 0);
          // stack.push(x.map(a => a / len));
          stack.push(x);
        } else {
          stack.push(dictionary[token]);
        }
      }
      return this.vec2word(normalize(stack.pop()));
    })
      .then(result => {
        this.setState(result);
      });
  };

  changeCorpora = (e) => {
    this.setState({ corpora: e.target.value });
  };

  getDescription = () => {
    switch (this.state.corpora) {
      case 'beschwerden3kPolished.bin': {
        return 'Der Korpus besteht aus den 3041 uns zur Verfügung gestellten Beispielbeschwerden, was 207.917 Wörtern entspricht. Er ist auf unsere Aufgabenstellung maßgeschneidert, kommt aber mit abweichenden Inhalten kaum zurecht.';
      }
      case 'cc.de.300.bin': {
        return 'Facebook stellt diesen 300-dimensionalen Korpus zur Verfügung. Er wurde auf Wikipedia-Artikeln und Common Crawl trainiert. Wie viele Wörter dazu betrachtet wurden ist nicht bekannt, es sind vermutlich mehr als 24.000.000.';
      }
      case 'ngram_ger.bin': {
        return 'Der Korpus wurde an den von Google zur Verfügung gestellten nGrams trainiert, die 270 032 618 Wörter beinhalten. Leider lassen sich Wortvektor-Modelle nicht mit nGrams trainieren, die Ergebnisse sind furchtbar.';
      }
      case 'BeschwerdenCATLeipzig.bin': {
        return 'Ein Modell, welches auf den Beispielbeschwerden und dem Leipzig-Korpus trainiert wurde. Die gute Performanz der Beispielbeschwerden auf unserem Gebiet soll mit der guten allgemeinen Performanz des Leipzig-Korpus kombiniert werden.';
      }
      case 'leipzigCorporaCollection1M.bin': {
        return 'Die Grundlage für diesen Korpus ist der größte von der Uni Leipzig zu Verfügung gestellte Textkorpus. Der wurde 2011 mit Webcrawlern erstellt und enthält 6 597 048 Wörter.';
      }
      default: {
        return 'Keine Beschreibung verfügbar';
      }
    }
  }
  render () {
    return (
      <React.Fragment>
        <Block>
          <Row vertical>
            <h1 className='center'>Wortvektoren</h1>
            <Content className='center' style={{ maxHeight: 'none' }}>
              <Input type='select' label='Textkorpus' required id='textkorpora' name='textkorpora' value={this.state.corpora} values={this.corpora} onChange={this.changeCorpora} />
              <p className='center' style={{ textAlign: 'justify' }}>{this.getDescription()}</p>
              <div className='smallmargin'>
                <Autocomplete model={this.state.corpora} id='analogy' label='Anfrage' type='text' onKeyUp={this.calculateOnEnter} />
                <Input type='submit' name='berechneButton' onClick={this.calculate} value='Berechnen' />
              </div>
              <ListTable
                header={['Wort', 'Wahrscheinichkeit']}
                headerStyles={[{ textAlign: 'left', paddingRight: '1rem' }]}
                styles={[{ textAlign: 'left', paddingRight: '1rem' }]}
                data={this.state.result}
                style={{ margin: 'auto' }}
              />
            </Content>
          </Row>
        </Block>
      </React.Fragment>
    );
  }
}

export default WordVectors;
