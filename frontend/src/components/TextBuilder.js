/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Api from '../utility/Api';

import Debug from './../components/Debug';
import Collapsible from './../components/Collapsible';
import TaggedText from './../components/TaggedText';
import Content from './../components/Content';

class TextBuilder extends Component {
  // Die Antworten kommen über api/response und die muss die ID der Beschwerde übergeben werden
  constructor (props) {
    super(props);

    this.state = {
      text: '',
      categories: []
    };
    this.data = {};
  }

  add = (index) => {
    const category = this.state.categories[index];
    const answers = this.data[index].alternatives;
    const answer = answers[this.state[category] % answers.length];
    this.setState((state) => {
      return {
        text: state.text + answer.completedText + '\r\n',
        categories: state.categories.filter(cat => cat !== category)
      };
    });
  };
  onChange = () => {
    this.setState({ text: this.refs.responseText.value });
  }
  cycle = (index) => {
    const category = this.state.categories[index];
    this.setState(
      (state) => {
        return {
          [category]: state[category] + 1
        };
      }
    );
  }
  delete = () => {
    Api.delete('/api/complaints/' + this.props.complaintId);
    document.location.href = document.location.origin + '/complaints';
  }
  setData = (data) => {
    /* this.data = data;
    const categories = data.map(block => block.component.componentName);
    let state = categories.reduce((obj, category) => {
      obj[category] = 0;
      return obj;
    }, {});
    state.categories = categories;
    this.setState(state); */
  };

  fetch = () => {
    Api.get('/api/responses/' + this.props.complaintId, '')
      .catch(() => {
        return { status: 404 };
      })
      .then(Debug.log)
      .then((response) => this.setData(response));
  };

  componentDidMount () {
    this.fetch();
  }
  render () {
    return (
      <React.Fragment>
        <Content style={{ flexBasis: '10%' }}>
          <textarea className="margin" id='responseText' ref='responseText' value={this.state.text} placeholder='Klicken Sie Ihre Antwort zusammen :)'
            onChange={this.onChange} />
          <input type='button' onClick={this.delete} value='Abschließen (löschen)' />
        </Content>
        <Collapsible style={{ minHeight: this.state.categories.length > 0 ? '150px' : '0' }} className='Content' label='Antworten' collapse={false} id='responses'>
          {
            this.state.categories.map((category, index) => {
              const answers = this.data[index].alternatives;
              const answer = answers[this.state[category] % answers.length];
              return (
                <div className='response' key={index}>
                  <span className='content'>
                    <TaggedText taggedText={{ text: answer.completedText, entities: answer.entities }} />
                    <div className='part'>{category}</div>
                  </span>
                  <i className='fa fa-check add' onClick={() => { this.add(index); }} />
                  <i className='fa fa-sync remove' data-category={category} onClick={() => this.cycle(index)} />
                </div>
              );
            })
          }
        </Collapsible>
      </React.Fragment>
    );
  }
}

export default TextBuilder;
