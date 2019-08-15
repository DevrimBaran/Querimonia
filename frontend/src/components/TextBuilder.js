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
    this.variableRegex = /\${[a-zA-U0-9]*}/g;
    this.state = {
      text: '',
      components: { ids: [] },
      actions: { ids: [] }
    };
  }

  onChange = () => {
    this.setState({ text: this.refs.responseText.value });
  };

  add = (componentId) => {
    let newComponents = { ids: [] };
    this.state.components.ids.filter(id => id !== componentId).forEach((id) => {
      newComponents[id] = this.state.components[id];
      newComponents.ids.push(id);
    });
    this.setState({
      text: this.state.text + this.insertEntities(componentId) + '\r\n',
      components: newComponents
    });
  };

  cycle = (componentId) => {
    const component = this.state.components[componentId];
    component.activeTextIndex = ((component.activeTextIndex + 1) % component.component.texts.length);
    this.setState((state) => {
      return {
        components: {
          ...state.components,
          componentId: component
        }
      };
    });
  };

  finish = (mailto) => {
    document.location.href = mailto;
    // document.location.href = document.location.origin + '/complaints';
  };

  insertEntities = (componentId) => {
    const component = this.state.components[componentId];
    let text = component.component.texts[component.activeTextIndex] || '';
    const variables = text.match(this.variableRegex) || [];

    variables.forEach(variable => {
      let entity = component.activeEntities[variable];
      entity.start = text.indexOf(variable);
      entity.end = text.indexOf(variable) + entity.value.length;
      entity.variable = variable;
      text = text.replace(variable, entity.value);
    });
    return text;
  };

  setActiveEntity = (componentId, variable, entity) => {
    let updatedComponents = this.state.components;
    updatedComponents[componentId].activeEntities[variable] = entity;
    this.setState({
      components: updatedComponents
    });
  };

  setUpActiveEntities = (component) => {
    component.activeEntities = {};
    component.component.texts.forEach((text) => {
      const variables = text.match(this.variableRegex) || [];
      variables.forEach((variable) => {
        if (Object.keys(component.activeEntities).includes(variable)) return;
        const possibleEntities = component.entities.filter(entity => entity.label === variable.substring(2, 2 + entity.label.length));
        component.activeEntities[variable] = possibleEntities.find(entity => entity.preferred) || possibleEntities[0];
      });
    });
  };

  setData = (data) => {
    console.log(data);
    const components = data.components.filter(component => component.component.texts.length !== 0).reduce((obj, component) => {
      component.activeTextIndex = 0;
      this.setUpActiveEntities(component);
      obj[component.id] = component;
      obj.ids.push(component.id);
      return obj;
    }, { ids: [] });
    const actions = data.actions.reduce((obj, component) => {
      obj[component.id] = component;
      obj.ids.push(component.id);
      return obj;
    }, { ids: [] });
    this.setState({ components, actions });
    console.log({ components, actions });
  };

  fetch = (refresh) => {
    (refresh ? Api.patch('/api/complaints/' + this.props.complaintId + '/response/refresh', '') : Api.get('/api/complaints/' + this.props.complaintId + '/response', ''))
      .catch(() => {
        return { status: 404 };
      })
      .then(Debug.log)
      .then((response) => this.setData(response));
  };

  componentDidMount () {
    this.fetch(false);
  }

  render () {
    if (this.props.refreshResponse !== null) {
      this.fetch(this.props.refreshResponse);
    }
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
            this.state.actions.ids.map((id) => {
              const action = this.state.actions[id];
              return (
                <Input key={id} type='checkbox' label={action.name} onClick={() => this.addAction(id)} />
              );
            })
          }
        </Content>
        <Collapsible className='Content' label='Antworten' collapse={false} id='responses' />
        <Content>
          {
            this.state.components.ids.map((id) => {
              const component = this.state.components[id];
              return (
                <div className='response' key={id}>
                  <span className='content'>
                    <ChangeableEntityText taggedText={{ text: this.insertEntities(id), entities: Object.keys(component.activeEntities).map(key => component.activeEntities[key]) }}
                      setActiveEntity={this.setActiveEntity}
                      entities={component.entities}
                      responseId={id} />
                    <div className='part'>{component.component.name}</div>
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
