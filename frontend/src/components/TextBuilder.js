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
import Content from './../components/Content';
import Input from './../components/Input';
import ChangeableEntityText from './ChangeableEntityText';

class TextBuilder extends Component {
  // Die Antworten kommen über api/response und die muss die ID der Beschwerde übergeben werden
  constructor (props) {
    super(props);

    this.state = {
      text: '',
      components: [],
      actions: []
    };
  }

  add = (componentId) => {
    const component = this.state.components.find((component) => {
      return component.component.id === componentId;
    });
    this.setState({
      text: this.state.text + this.insertEntities(component.component.texts[0], componentId) + '\r\n',
      components: this.state.components.filter(component => component.component.id !== componentId)
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

  finish = (mailto) => {
    document.location.href = mailto;
    // document.location.href = document.location.origin + '/complaints';
  }

  insertEntities = (text, componentId) => {
    const component = this.state.components.find((component) => {
      return component.component.id === componentId;
    });
    const preferredEntity = component.entities.find(entity => entity.preferred);
    const componentEntities = component.activeEntityLabel || (preferredEntity ? preferredEntity.label : false) || (component.entities[0] ? component.entities[0].label : false) || null;
    const variableRegex = /\${.*}/g;
    const variables = text.match(variableRegex) || [];
    variables.forEach(variable => {
      text = text.replace(variable, componentEntities);
    });
    return text;
  };

  setActiveEntity = (componentId, activeEntityLabel) => {
    this.setState({
      components: this.state.components.map((component) => {
        if (component.component.id === componentId) component.activeEntityLabel = activeEntityLabel;
        return component;
      })
    });
  };

  setData = (data) => {
    const components = data.components;
    const actions = data.actions;
    this.setState({
      components: components,
      actions: actions });
    console.log({ components, actions });
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
    const mail = 'querimonia@g-laber.de';
    const subject = 'Querimonia Beschwerdeabschluss ' + this.props.complaintId;
    const message = `Die Beschwerde mit der ID: ${this.props.complaintId} wurde mit folgender Nachricht abgeschlossen:

    ${this.state.text}

    `;
    const link = 'Link: https://querimonia.iao.fraunhofer.de/complaints/' + this.props.complaintId;
    const mailto = 'mailto:' + encodeURIComponent(mail) + '?subject=' + encodeURIComponent(subject) + '&body=' + encodeURIComponent(message) + link;
    return (
      <React.Fragment>
        <Content>
          <textarea className='margin' id='responseText' ref='responseText' value={this.state.text} placeholder='Klicken Sie Ihre Antwort zusammen :)'
            onChange={this.onChange} />
        </Content>
        <div>
          <Input type='button' value='Abschließen' onClick={() => this.finish(mailto)} />
        </div>
        <Collapsible className='Content' label='Aktionen' collapse={false} id='actions' />
        <Content>
          {
            this.state.actions.map((action, i) => {
              const id = action.id;
              return (
                <Input key={i} type='checkbox' label={action.name} onClick={() => this.addAction(id)} />
              );
            })
          }
        </Content>
        <Collapsible className='Content' label='Antworten' collapse={false} id='responses' />
        <Content>
          {
            this.state.components.map((component) => {
              const id = component.component.id;
              return (
                <div className='response' key={id}>
                  <span className='content'>
                    <ChangeableEntityText taggedText={{ text: this.insertEntities(component.component.texts[0], id), entities: component.entities }}
                      possibleEntities={component.entities}
                      setActiveEntity={this.setActiveEntity}
                      activeEntity={component.activeEntityLabel || null}
                      complaintId={id} />
                    <div className='part'>{component.component.componentName}</div>
                  </span>
                  <i className='fa fa-check add' onClick={() => { this.add(id); }} />
                  <i className='fa fa-sync remove' onClick={() => this.cycle(id)} />
                </div>
              );
            })
          }
        </Content>
      </React.Fragment>
    );
  }
}

export default TextBuilder;
