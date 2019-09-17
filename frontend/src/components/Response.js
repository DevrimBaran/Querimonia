/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { connect } from 'react-redux';

import FillerText from './FillerText';

class Response extends Component {
  constructor (props) {
    super(props);
    this.state = {
      selected: 0,
      value: ''
    };
  }
  cycle = () => {
    this.setState(state => ({ selected: state.selected + 1 }));
  }
  onChange = (e) => {
    this.setState({ value: e.value });
  }
  render () {
    const { component, entities, dispatch, onSelect, ...passThrough } = { ...this.props };
    return component && component.texts.length > 0 ? (
      <div className='response' {...passThrough} >
        <span className='content'>
          <FillerText onChange={this.onChange} texts={component.texts} selected={this.state.selected} />
          <div className='part'>{component.name}</div>
        </span>
        <i className='fa fa-check add' onClick={e => onSelect({ target: e, value: this.state.value })} />
        <i className='fa fa-sync remove' onClick={this.cycle} />
      </div>
    ) : '';
  }
}

const mapStateToProps = (state, props) => {
  return {
    entities: state.complaintStuff.entities
  };
};

export default connect(mapStateToProps)(Response);
