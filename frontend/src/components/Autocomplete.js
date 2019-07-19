/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Input from '../components/Input';

class Autocomplete extends Component {
  constructor (props) {
    super(props);
    this.input = React.createRef();
    this.controller = null;
    this.lastWord = '';
    this.state = {
      words: [],
      value: ''
    };
  }
  fetchPredictions = (word) => {
    this.controller && this.controller.abort();
    this.controller = new AbortController();
    const signal = this.controller.signal;
    const words = [
      'auto',
      'haus',
      'automat',
      'automatisch',
      'atomar',
      'aural',
      'aura',
      'hallo',
      'haribo',
      'otto',
      'hanuta',
      'bus', 'busfahrer', 'bushaltestelle'
    ];
    const fakefetch = function (word) {
      return new Promise((resolve, reject) => {
        resolve(words.filter(w => w.substr(0, word.length) === word));
      });
    };
    fetch('https://querimonia.iao.fraunhofer.de/python/predict_word',
      {
        method: 'post',
        mode: 'cors',
        signal: signal,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          query: word,
          model: this.props.model,
          limit: 5
        })
      }
    ).catch(() => {
      return fakefetch(word);
    }).then(this.onFetch);
  }
  selectWord = (e) => {
    const toAdd = e.target.value.substr(this.lastWord.length);
    this.setState((state) => ({ words: [], value: state.value + toAdd }));
  }
  mapWords = (word) => {
    return (<input type='text' readOnly key={word} value={word} onClick={this.selectWord} />);
  }
  onFetch = (words) => {
    console.log(words);
    this.setState({ words: words });
  }
  onChange = (e) => {
    this.setState({ value: e.value });
    const match = e.value.match(/\w+$/);
    if (match) {
      this.lastWord = match[0];
      this.fetchPredictions(this.lastWord);
    } else {
      this.lastWord = '';
      this.setState({ words: [] });
    }
    this.props.onChange && this.props.onChange(e);
  }
  render () {
    const classes = 'autocomplete';
    const { className, onChange, type, label, values, value, model, ref, ...passThroughProps } = this.props;

    let injectedProp = {
      className: className ? className + ' ' + classes : classes
    };
    return (
      <div {...injectedProp}>
        {model ? (
          <React.Fragment>
            <Input value={this.state.value} ref={this.input} type='text' label={label} onChange={this.onChange} {...passThroughProps} />
            {this.state.words.length > 0 && (
              <div>
                {this.state.words.map(this.mapWords)}
              </div>
            )}
          </React.Fragment>
        ) : (
          <p>Wählen Sie einen Textkorpus aus.</p>
        ) }
      </div>
    );
  }
}

export default Autocomplete;
