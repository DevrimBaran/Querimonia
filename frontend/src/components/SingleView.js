import React, { Component } from 'react';
import { connect } from 'react-redux';

import { setActive, saveActive } from '../redux/actions/';

export default (endpoint, partial, stateToProps) => {
  return connect((state, props) => ({ ...state[endpoint].data, ...(stateToProps ? stateToProps(state) : {}) }))(class extends Component {
    save = () => {
      return (
        <button
          type='button'
          className='important'
          disabled={this.props.active.saving}
          onClick={(e) => {
            console.log('save', this.props.active);
            this.props.dispatch(saveActive(endpoint));
          }}
        >Speichern</button>
      );
    }
    componentDidMount = () => {
      const id = parseInt(this.props.match.params.id);
      if (!this.props.active || id !== this.props.active.id) {
        if (!this.props.fetching) {
          this.props.dispatch(setActive(endpoint, id));
        }
      }
    }
    render () {
      return (
        <React.Fragment>
          {partial.Single(this.props.active, this.props.dispatch, { save: this.save, props: this.props })}
        </React.Fragment>
      );
    }
  });
};
