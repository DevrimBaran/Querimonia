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
    this.props.dispatch(fetchData(this.props.endpoint));
  }
  onChange = (e) => {
    this.props.dispatch({
      type: 'FILTER_CHANGE',
      endpoint: this.props.endpoint,
      name: e.name,
      value: e.value
    });
  }
  renderInput = (input) => {
    return (
      <Input key={input.name} multiple={input.multiple} label={input.label} type={input.type} id={input.name} name={input.name} onChange={this.onChange} value={input.value} values={input.values} />
    );
  }
  render () {
    // const QueryText = Query(input);
    const pathname = document.location.pathname;
    const mappedInputs = this.props.filter ? this.props.filter.map(this.renderInput) : [];
    return (
      <React.Fragment>
        <Collapsible label='Filter' className='Filter' />
        <form action={pathname} onSubmit={this.submit}>
          {mappedInputs}
          {mappedInputs.length > 0 && (<Input type='submit' value='Anwenden' />)}
        </form>
      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => ({
  filter: state[props.endpoint].filter
});

export default connect(mapStateToProps)(Filter);
