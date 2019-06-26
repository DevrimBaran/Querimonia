/**
 * This class creates the WordVector view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Block from 'components/Block';

// import Api from '../utility/Api';

class WordVectors extends Component {
  constructor () {
    super();
    this.state = {
      result: ''
    };
  }
  parseText = (e) => {
    let text = e.target.value.replace(/\s+/g, '');
    let parameters = [];
    let match;
    while (true) {
      match = text.match(/^(\+|-)?([a-zA-Z_]+)(.*)$/);
      if (match) {
        parameters.push({
          sign: match[1] || '+',
          word: match[2]
        });
        text = match[3] || '';
      } else {
        break;
      }
    }
    this.analogy = parameters;
  }
  word2vec = (data) => {
    let v = /* Api.get('/api/word2vec/' + data.word, '')
      .catch(() => {
        console.log('catch');
        return [1, 0, 0, 1];
      })
      */
      new Promise(function (resolve, reject) {
        setTimeout(resolve, 100, [1, 0, 0, 1]);
      }).then((response) => {
        if (data.sign !== '-') return response;
        return response.map(a => -a);
      });
    return v;
  }
  vec2word = () => {

  }
  calculate = () => {
    let v = [0, 0, 0, 0];
    Promise.all(this.analogy.map(w => { return this.word2vec(w); }))
      .then(vectors => vectors.reduce((result, vec) => {
        return result.map((a, i) => {
          return vec[i] + a;
        });
      }, v))
      .then(result => {
        this.setState({ result: result });
      });
  }
  render () {
    return (
      <React.Fragment>
        <Block>
          <h6 className='center'>Wortvektoren</h6>
          <div className='content'>
            <br />
            <h6>Nearest Neighbor</h6>
            <label>Befehl:
              <select name='commands'>
                <option>nn</option>
                <option>analogy</option>
              </select>
            </label>
            <label>Textkorpora:
              <select name='textkorpora'>
                <option>beschwerden3kPolished.bin</option>
                <option>cc.de.300.bin</option>
                <option>ngram_ger.bin</option>
                <option>BeschwerdenCATLeipzig.bin</option>
                <option>leipzigCorporaCollection1M.bin</option>
              </select>
            </label>
            <p>Worteingabe:</p>
            <input type='text' id='text' />
            <input type='button' name='berechneButton' value='Berechnen' />
            <br />
            <br />
            <h6>Analogie</h6>
            <input type='text' onChange={this.parseText} />
            <input type='button' name='berechneButton' onClick={this.calculate} value='Berechnen' />
            <p>{this.state.result}</p>
          </div>
        </Block>
      </React.Fragment>
    );
  }
}

export default WordVectors;
