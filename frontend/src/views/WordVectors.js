/**
 * This class creates the WordVector view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Block from 'components/Block';
import Content from 'components/Content';

// TODO Api anpassen/backend ändern
const Api = {
  post: (endpoint, data) => {
    return fetch('https://querimonia.iao.fraunhofer.de/python' + endpoint, {
      method: 'post',
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })
      .then(response => { return response.ok ? response.json() : []; });
  }
};
class WordVectors extends Component {
  constructor () {
    super();
    this.state = {
      result: [],
      error: false
    };
  }
  parseText = (e) => {
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
    let chars = e.target.value.replace(/\s+/g, '').split('');
    let outputQueue = [];
    let operatorStack = [];
    while (chars.length > 0) {
      // read a token
      let char = chars.shift();
      let token = char;
      if (char.match(/\w/)) {
        while (chars.length > 0 && chars[0].match(/\w/)) {
          token += chars.shift();
        }
      }
      switch (token) {
        case '+': // fallthrough
        case '-': // fallthrough
        case '*': // fallthrough
        case '/': // fallthrough
          // token is operator
          while (precedence(operatorStack[operatorStack.length - 1]) >= precedence(token) &&
            operatorStack[operatorStack.length - 1] !== '(') {
            outputQueue.push(operatorStack.pop());
          }
        case '(': // fallthrough
          operatorStack.push(token);
          break;
        case ')':
          while (operatorStack[operatorStack.length - 1] !== '(') {
            outputQueue.push(operatorStack.pop());
            if (operatorStack.length === 0) {
              this.setState({ error: 'parentheses missmatch!' });
              return;
            }
          }
          operatorStack.pop();
          break;
        default:
          // token is a word
          outputQueue.push(token);
      }
    }
    while (operatorStack.length > 0) {
      switch (operatorStack[operatorStack.length - 1]) {
        case '(':// fallthrough
        case ')':
          this.setState({ error: 'parentheses missmatch!' });
          return;
        default:
          outputQueue.push(operatorStack.pop());
      }
    }
    this.setState({ error: null });
    this.analogy = outputQueue;
  }
  calculateOnEnter = (e) => {
    if (e.keyCode === 13) {
      this.calculate();
    }
  }
  word2vec = (word) => {
    // return new Promise(function (resolve, reject) {
    //  resolve([parseInt(data), parseInt(data)]);
    // })
    return Api.post('/word_to_vec', { word: word });
    // .then((response) => {
    //  if (data.sign !== '-') return response;
    //  return response.map(a => -a);
    // });
  }
  vec2word = (vec) => {
    // console.log('result: ' + vec[0]);
    // return vec;
    return Api.post('/vec_to_word', { vector: vec })
      .then((response) => {
        return {
          result: response.map(function (arr, index) {
            return arr[0] + ': ' + arr[1];
          })
        };
      });
  };
  calculate = () => {
    if (this.state.error) return;
    // this.analogy is postfix expression
    let words = this.analogy.filter((token) => {
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
      let postfix = this.analogy;
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
          let len = x.reduce((len, a) => len + a * a, 0);
          stack.push(x.map(a => a / len));
        } else {
          stack.push(dictionary[token]);
        }
      }
      return this.vec2word(stack.pop());
    })
      .then(result => {
        console.log(result);
        this.setState(result);
      });
  }
  render () {
    return (
      <React.Fragment>
        <Block>
          <h6 className='center'>Wortvektoren</h6>
          <Content style={{ flexBasis: '100%' }}>
            <label htmlFor='textkorpora'>Textkorpora: </label>
            <select id='textkorpora' name='textkorpora'>
              <option>beschwerden3kPolished.bin</option>
              <option>cc.de.300.bin</option>
              <option>ngram_ger.bin</option>
              <option>BeschwerdenCATLeipzig.bin</option>
              <option>leipzigCorporaCollection1M.bin</option>
            </select>
            <label htmlFor='analogy'>Analogie: </label>
            <input id='analogy' type='text' onKeyUp={this.calculateOnEnter} onChange={this.parseText} />
            <input type='button' name='berechneButton' onClick={this.calculate} value='Berechnen' />
            <ul>
              {this.state.result.map((word, index) => {
                return (<li key={index}>{word}</li>);
              })}
            </ul>
          </Content>
        </Block>
      </React.Fragment>
    );
  }
}

export default WordVectors;
