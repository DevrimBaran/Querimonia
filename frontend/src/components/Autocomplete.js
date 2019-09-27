/**
 * Sends Request to Backend and displays the answers.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import Api from '../utility/Api';

class Autocomplete extends Component {
  constructor (props) {
    super(props);
    this.controller = null;
    this.lastWord = '';
    this.state = {
      words: [],
      value: '',
      selected: 0
    };
  }
  fetchPredictions = (word) => {
    this.controller && this.controller.abort();
    this.controller = new AbortController();
    const signal = this.controller.signal;
    Api.post('/python/predict_word', {
      query: word,
      model: this.props.model,
      limit: 5
    }, { signal: signal })
      .then(this.onFetch);
  }
  selectWord = (e) => {
    const toAdd = e.target.value.substr(this.lastWord.length);
    this.setState((state) => ({ words: [], value: state.value + toAdd }));
  }
  mapWords = (word, index) => {
    return (<input type='text' className={this.state.selected === index + 1 ? 'active' : ''} readOnly key={word} value={word} onClick={this.selectWord} />);
  }
  onFetch = (words) => {
    if (words) {
      this.setState({ words: words });
    }
  }
  onChange = (e) => {
    const value = e.target.value;
    this.setState({ value: value, selected: 0 });
    const match = value.match(/(\w|[äöüßÄÖÜ]){3,}$/);
    if (match) {
      this.lastWord = match[0];
      this.fetchPredictions(this.lastWord);
    } else {
      this.lastWord = '';
      this.setState({ words: [] });
    }
    this.props.onChange && this.props.onChange(e);
  }
  onKeyUp = (e) => {
    switch (e.keyCode) {
      case 38: { // Up
        this.setState((state) => ({ selected: state.selected === 0 ? state.words.length : state.selected - 1 }));
        return;
      }
      case 40: { // Down
        this.setState((state) => ({ selected: state.selected === state.words.length ? 0 : state.selected + 1 }));
        return;
      }
      case 13: { // Enter
        if (this.state.words.length !== 0 && this.state.selected !== 0) {
          const predictions = e.target.parentNode.getElementsByClassName('predictions')[0];
          if (predictions) {
            const active = predictions.getElementsByClassName('active')[0];
            this.selectWord({ target: active });
            return;
          }
        }
        break;
      }
      default: {
        break;
      }
    }
    this.props.onKeyUp && this.props.onKeyUp(e);
  }
  render () {
    const classes = 'autocomplete';
    const { className, onChange, onKeyUp, type, label, values, value, id, model, ref, ...passThroughProps } = this.props;

    let injectedProp = {
      className: className ? className + ' ' + classes : classes
    };
    return (
      <div {...injectedProp}>
        {model ? (
          <React.Fragment>
            <div className='input'>
              {label && (<label htmlFor={id}>{label}</label>)}
              <input value={this.state.value} onKeyUp={this.onKeyUp} id={id} autoComplete='off' type='text' onChange={this.onChange} {...passThroughProps} />
              {this.state.words.length > 0 && (
                <div className='predictions'>
                  {this.state.words.filter((value, index, self) => {
                    return self.indexOf(value) === index;
                  }).map(this.mapWords)}
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
