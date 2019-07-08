/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { fetchData } from '../redux/actions';
import { connect } from 'react-redux';
import Collapsible from '../components/Collapsible';
import Input from '../components/Input';

class Filter extends Component {
  submit = (e) => {
    e.preventDefault();
    this.props.dispatch(fetchData('complaints'));
  }
  onChange = (e) => {
    this.props.dispatch({
      type: 'FILTER_CHANGE',
      endpoint: this.props.endpoint,
      name: e.target.name,
      value: e.target.value
    });
  }
  renderInput = (input) => {
    return (
      <Input key={input.name} label={input.label} type={input.type} id={input.name} name={input.name} onChange={this.onChange} value={input.value} values={input.values} />
    );
  }
  render () {
    // const QueryText = Query(input);
    const pathname = document.location.pathname;
    const mappedInputs = this.props.filter ? this.props.filter.map(this.renderInput) : [];
    return (
      <Collapsible label='Filter' className='Filter'>
        <form action={pathname} onSubmit={this.submit}>
          {mappedInputs}
          {mappedInputs.length > 0 && (<input type='submit' />)}
        </form>
      </Collapsible>
    );
  }
}

const mapStateToProps = (state, props) => ({
  filter: state[props.endpoint].filter
});

export default connect(mapStateToProps)(Filter);
