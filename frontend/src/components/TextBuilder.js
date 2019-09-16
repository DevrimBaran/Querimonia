/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import { finishComplaint, refreshResponses } from '../redux/actions/';

import Content from './../components/Content';
import Response from './../components/Response';
import Input from './../components/Input';
import Collapsible from './Collapsible';
// import Debug from './Debug';
import Tabbed from './Tabbed';
import Button from './Button';

class TextBuilder extends Component {
  constructor (props) {
    super(props);
    this.state = {
      response: '',
      used: {}
    };
  }
  changeText = (e) => {
    this.setState((state) => ({
      response: e.value
    }));
  }
  onSelect = (value, id) => {
    this.setState((state) => ({
      response: state.response + value,
      used: { ...state.used, [id]: true }
    }));
  }
  refresh = (e) => {
    this.props.dispatch(refreshResponses(this.props.complaintId));
  }
  finish = () => {
    fetch('https://querimonia.iao.fraunhofer.de/tmp/done', {
      method: 'post',
      mode: 'cors',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic YWRtaW46UXVlcmltb25pYVBhc3MyMDE5'
      },
      body: JSON.stringify({
        id: this.props.complaintId,
        message: this.state.response
      })
    });
    this.props.dispatch(finishComplaint(this.props.complaintId));
  };
  render () {
    const { disabled, components = [] } = { ...this.props };
    const actions = components.reduce((array, component) => array.concat(component.component.actions), []);
    console.log(actions);
    const disableResponses = components.filter(c => !this.state.used[c.id]).length === 0;
    const disableActions = actions.length === 0;
    return (
      <React.Fragment>
        <Input type='textarea' readOnly={disabled} min='5' value={this.state.response} onChange={this.changeText} />
        {disabled || <Button onClick={this.finish}>Abschließen</Button>}
        <Button confirm='Der bisherige Antworttext wird gelöscht. Sind Sie sicher?' onClick={this.refresh}>Antwortbausteine neu generieren</Button>
        <Collapsible label='Antworten / Aktionen'
          disabled={disabled || (disableResponses && disableActions)}
          collapse={disabled || (disableResponses && disableActions)}
        />
        <Content>
          <Tabbed>
            <div label='Antworten' disabled={disableResponses}>
              {
                components.filter(c => !this.state.used[c.component.id]).map((c, i) => {
                  return (<Response key={c.component.id} component={c.component} onSelect={(e) => { this.onSelect(e.value, c.component.id); }} />);
                })
              }
            </div>
            <div label='Aktionen' disabled={disableActions}>
              {actions.map((action, index) => (
                <Input type='checkbox' label={action.name} key={index} />
              ))}
            </div>
          </Tabbed>
        </Content>
      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => {
  return {
    entities: state.complaintStuff.entities,
    complaintId: state.complaintStuff.id,
    components: state.complaintStuff.components,
    // actions: state.complaintStuff.actions,
    counter: state.complaintStuff.counter
  };
};

export default connect(mapStateToProps)(TextBuilder);
