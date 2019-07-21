/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

class Autocomplete extends Component {
  constructor (props) {
    super(props);
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
    )
      .then((response) => response.json())
      .then(this.onFetch);
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
    const value = e.target.value;
    this.setState({ value: value });
    const match = value.match(/\w{3,}$/);
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
    const { className, onChange, type, label, values, value, id, model, ref, ...passThroughProps } = this.props;

    let injectedProp = {
      className: className ? className + ' ' + classes : classes
    };
    return (
      <div {...injectedProp}>
        {model ? (
          <React.Fragment>
            {label && (<label htmlFor={id}>{label}</label>)}
            <div className='input'>
              <input value={this.state.value} id={id} autoComplete='off' type='text' onChange={this.onChange} {...passThroughProps} />
              {this.state.words.length > 0 && (
                <div className='predictions'>
                  {this.state.words.map(this.mapWords)}
                </div>
              )}
            </div>
          </React.Fragment>
        ) : (
          <p>Wählen Sie einen Textkorpus aus.</p>
        ) }
      </div>
    );
  }
}

export default Autocomplete;
