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
  cycle = (componentId) => {
    this.setState({
      components: this.state.components.map((component) => {
        if (component.component.id === componentId) component.activeTextIndex = ((component.activeTextIndex + 1) % component.component.texts.length);
        return component;
      })
    }
    );
  };

  finish = (mailto) => {
    document.location.href = mailto;
    // document.location.href = document.location.origin + '/complaints';
  }

  insertEntities = (text, componentId) => {
    const component = this.state.components.find((component) => {
      return component.component.id === componentId;
    });
    const variableRegex = /\${[a-zA-U0-9]*}/g;
    const variables = text.match(variableRegex) || [];
    variables.forEach(variable => {
      const preferredEntity = component.entities.find(entity => entity.preferred && `\${${entity.label}}` === variable);
      const firstSuitableEntity = component.entities.find(entity => `\${${entity.label}}` === variable);
      const componentEntityLabel = component.activeEntityLabel || (preferredEntity ? preferredEntity.value : false) || (firstSuitableEntity ? firstSuitableEntity.value : false) || null;
      const entity = component.entities.find(entity => entity.value === componentEntityLabel);
      text = text.replace(variable, componentEntityLabel);
      entity.start = text.indexOf(componentEntityLabel);
      entity.end = text.indexOf(componentEntityLabel) + componentEntityLabel.length;
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
    console.log(data);
    const components = data.components.filter((e) => {
      return e.component.texts.length !== 0;
    });
    const actions = data.actions;
    components.forEach((component) => {
      component.activeTextIndex = 0;
    });
    this.setState({
      components: components,
      actions: actions });
    console.log({ components, actions });
  };

  fetch = (refresh) => {
    (refresh ? Api.patch('/api/responses/' + this.props.complaintId + '/refresh', '') : Api.get('/api/responses/' + this.props.complaintId, ''))
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
    if (this.props.refreshResponse) {
      this.fetch(true);
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
                    <ChangeableEntityText taggedText={{ text: this.insertEntities(component.component.texts[component.activeTextIndex], id), entities: component.entities }}
                      possibleEntities={component.entities}
                      setActiveEntity={this.setActiveEntity}
                      activeEntity={component.activeEntityLabel || null}
                      complaintId={id} />
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
