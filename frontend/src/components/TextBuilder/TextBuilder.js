import React, { Component } from 'react';

import Api from '../../utility/Api';

// import Block from '../Block/Block';
// import Body from '../Body/Body';
// import Collapsible from '../Collapsible/Collapsible';
// import Fa from '../Fa/Fa';
// import Filter from '../Filter/Filter';
// import Issues from '../Issues/Issues';
// import Log from '../Log/Log.js';
// import Modal from '../Modal/Modal';
// import Stats from '../Stats/Stats';
// import Tabbed from '../Tabbed/Tabbed';
// import Table from '../Table/Table';
// import TaggedText from '../TaggedText/TaggedText';
// import Text from '../Text/Text';
// import TextBuilder from '../TextBuilder/TextBuilder';
// import Topbar from '../Topbar/Topbar';

import './TextBuilder.scss';
class TextBuilder extends Component {
  // Die Antworten kommen über api/response und die muss die ID der Beschwerde übergeben werden
  constructor (props) {
    super(props);

    this.state = {
      text: '',
      responses: []
    };
  }

  add = (index) => {
    this.setState((state, props) => {
      const text = state.text += state.responses[index].completedText + '\r\n';
      return {
        text: text,
        responses: state.responses.filter((text, i) => {
          return index !== i;
        })
      };
    });
  };

  remove = (index) => {
    this.setState((state, props) => {
      return {
        responses: state.responses.filter((text, i) => {
          return index !== i;
        })
      };
    });
  };

  setData = (data) => {
    this.setState((state, props) => {
      return {
        responses: state.responses.concat(data)
      };
    });
  };

  random = (min, max) => {
    return Math.floor((Math.random() * (max - min + 1)) + min);
  };

  fakeResponse = () => {
    const words = ['Lorem', 'ipsum', 'dolor', 'sit', 'amet', 'consectetur', 'adipisicing', 'elit', 'Dolore', 'consequuntur'];
    let text = words[Math.floor(Math.random() * words.length)];
    for (var i = Math.random() * 16; i > 0; i--) {
      text += ' ' + words[Math.floor(Math.random() * words.length)];
    }
    return text;
  };

  fetch = () => {
    Api.get('/api/responses/' + this.props.complaintId, '')
      .catch((e) => {
        return { status: 404 };
      })
      .then((response) => this.setData(response));
  };

  componentDidMount () {
    this.fetch();
    // this.fetch();
    // this.fetch();
  }

  render () {
    return (
      <div className='TextBuilder' ref='TextBuilder'>
        <pre contentEditable>{this.state.text}</pre>
        <br />
        {
          this.state.responses.map((response, index) => {
            return (
              <div className='response Block' key={index}>
                <pre onClick={() => this.add(index)}>
                  {response.completedText}
                </pre>
                <span className='remove' onClick={() => { this.remove(index); this.fetch(); }} />
                <p className='part'>{response.component.responsePart}</p>
              </div>
            );
          })
        }
      </div>
    );
  }
}

export default TextBuilder;
