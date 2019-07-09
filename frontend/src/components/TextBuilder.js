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
import Input from './../components/Input';

class TextBuilder extends Component {
  // Die Antworten kommen über api/response und die muss die ID der Beschwerde übergeben werden
  constructor (props) {
    super(props);

    this.state = {
      text: '',
      components: { ids: [] },
      actions: { ids: [] }
    };
    this.data = {};
  }

  add = (id) => {
    this.setState(
      (state) => {
        return {
          text: state.text + state.components[id].alternatives[state.components[id].currentAlternative].completedText + '\r\n',
          components: {
            ...state.components,
            ids: state.components.ids.filter(componentid => id !== componentid)
          }
        };
      }
    );
  };
  onChange = () => {
    this.setState({ text: this.refs.responseText.value });
  }
  cycle = (id) => {
    this.setState(
      (state) => {
        return {
          components: {
            ...state.components,
            [id]: {
              ...state.components[id],
              currentAlternative: (state.components[id].currentAlternative + 1) % state.components[id].alternatives.length
            }
          }
        };
      }
    );
  }
  delete = () => {
    Api.delete('/api/complaints/' + this.props.complaintId);
    document.location.href = document.location.origin + '/complaints';
  }
  setData = (data) => {
    const components = data.components.reduce((obj, component) => {
      component.currentAlternative = 0;
      obj[component.responsePartId] = component;
      obj.ids.push(component.responsePartId);
      return obj;
    }, { ids: [] });
    const actions = data.actions.reduce((obj, component) => {
      obj[component.actionId] = component;
      obj.ids.push(component.actionId);
      return obj;
    }, { ids: [] });
    this.setState({ components, actions });
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
          <textarea className='margin' id='responseText' ref='responseText' value={this.state.text} placeholder='Klicken Sie Ihre Antwort zusammen :)'
            onChange={this.onChange} />
          <input type='button' onClick={this.delete} value='Abschließen (löschen)' />
        </Content>
        <Collapsible className='Content' label='Aktionen' collapse={false} id='actions'>
          {
            this.state.actions.ids.map((id) => {
              const action = this.state.actions[id];
              return (
                <Input key={id} type='checkbox' label={action.name} onClick={() => this.addAction(id)} />
              );
            })
          }
        </Collapsible>
        <Collapsible className='Content' label='Antworten' collapse={false} id='responses'>
          {
            this.state.components.ids.map((id) => {
              const component = this.state.components[id];
              const answer = component.alternatives[component.currentAlternative];
              return (
                <div className='response' key={id}>
                  <span className='content'>
                    <TaggedText text={{ text: answer.completedText, entities: answer.entities }} />
                    <div className='part'>{component.component.componentName}</div>
                  </span>
                  <i className='fa fa-check add' onClick={() => { this.add(id); }} />
                  <i className='fa fa-sync remove' onClick={() => this.cycle(id)} />
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
