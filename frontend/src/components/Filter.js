/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import { fetchData } from '../redux/actions';
import { connect } from 'react-redux';
// import Collapsible from '../components/Collapsible';
import Input from '../components/Input';
import Form from '../components/Form';

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
  reset = () => {
    this.props.filter.forEach((input) => {
      this.onChange({ name: input.name, value: '' });
    });
  }
  renderInput = (input) => {
    const { name, value, multiple, ...passThrough } = { ...input };
    return (
      <Input key={name} name={name} onChange={this.onChange} value={value || (multiple ? [] : '')} multiple={multiple} {...passThrough} />
    );
  }
  render () {
    // const QueryText = Query(input);
    const pathname = document.location.pathname;
    const mappedInputs = this.props.filter ? this.props.filter.map(this.renderInput) : [];
    return (
      <React.Fragment>
        {/* <Collapsible label='Filter' className='Filter' /> */}
        <Form className='Filter responsive' action={pathname} onSubmit={this.submit} >
          {mappedInputs}
          {/* {mappedInputs.length > 0 && (<Input type='submit' value='Anwenden' />)} */}
          <Input type='reset' onClick={this.reset} />
        </Form>
      </React.Fragment>
    );
  }
}

const mapStateToProps = (state, props) => ({
  filter: state[props.endpoint].filter
});

export default connect(mapStateToProps)(Filter);
