/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import Content from './../components/Content';
import Response from './../components/Response';
import Input from './../components/Input';
import Collapsible from './Collapsible';
import Debug from './Debug';
import Tabbed from './Tabbed';

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

  render () {
    // const { ...passThrough } = { ...this.props };
    return (
      <React.Fragment>
        <Input type='textarea' min='5' value={this.state.response} onChange={this.changeText} />
        {this.props.counter}
        <Collapsible label='Antworten / Aktionen' />
        <Content>
          <Tabbed>
            <div label='Antworten'>
              {
                this.props.components.filter(c => !this.state.used[c.id]).map((component, i) => (
                  <Response key={component.id} component={component.component} onSelect={(e) => { this.onSelect(e.value, component.id); }} />
                ))
              }
            </div>
            <div label='Aktionen'>
              <Debug data={this.props.actions} />
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
    actions: state.complaintStuff.actions,
    counter: state.complaintStuff.counter
  };
};

export default connect(mapStateToProps)(TextBuilder);
