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
import TaggedText from 'components/TaggedText/TaggedText';
import Row from 'components/Row/Row';
import Content from 'components/Content/Content';
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
      console.log('text', state.responses[index], text);
      return {
        text: text,
        responses: state.responses.filter((text, i) => {
          return index !== i;
        })
      };
    });
  };
  onChange = () => {
    this.setState({ text: this.refs.responseText.value });
  }
  remove = (index) => {
    this.setState((state, props) => {
      return {
        responses: state.responses.filter((text, i) => {
          return index !== i;
        })
      };
    });
    this.fetch();
  };

  setData = (data) => {
    if (this.state.responses) {
      data = data.filter((response, i) => {
        let isDuplicate = false;
        this.state.responses.forEach((element) => {
          isDuplicate = isDuplicate || element.component.responsePart === response.component.responsePart;
        });
        return !isDuplicate;
      });
    }
    this.setState((state, props) => {
      return {
        responses: state.responses.concat(data)
      };
    });
  };

  fetch = () => {
    Api.get('/api/responses/' + this.props.complaintId, '')
      .catch(() => {
        return { status: 404 };
      })
      .then((response) => this.setData(response));
  };

  componentDidMount () {
    this.fetch();
  }

  render () {
    return (
      <Row vertical>
        <Content>
          <textarea id='responseText' ref='responseText' value={this.state.text} placeholder='Klicken sie ihre Antwort zusammen :)'
            onChange={this.onChange} />
        </Content>
        <div id='responses'>
          {
            this.state.responses.map((response, index) => {
              return (
                <div className='response Block' key={index}>
                  <span className='content' onClick={() => { this.add(index); }}>
                    <TaggedText text={{ text: response.completedText, entities: response.entities }} />
                    <div className='part'>{response.component.responsePart}</div>
                  </span>
                  <i className='fa fa-check add' onClick={() => { this.add(index); }} />
                  <i className='fa fa-sync remove' onClick={() => { this.remove(index); }} />
                </div>
              );
            })
          }
        </div>
      </Row>
    );
  }
}

export default TextBuilder;
