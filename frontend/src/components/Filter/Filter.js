import React, { Component } from 'react';

// import Api from '../../utility/Api';

import Block from '../Block/Block';
import Select from '../Select/Select';
// import RadioButton from '../RadioButton/RadioButton';
// import Body from '../Body/Body';
// import Collapsible from '../Collapsible/Collapsible';
// import Fa from '../Fa/Fa';
// import Filter from '../Filter/Filter';
// import Issues from '../Issues/Issues';
// import Log from '../Log/Log.js';
// import Modal from '../Modal/Modal';
// import Stats from '../Stats/Stats';
// import Tabbed from '../Tabbed/Tabbed';
// import Table from '../Table/Table';
// import TaggedText from '../TaggedText/TaggedText';
// import Text from '../Text/Text';
// import TextBuilder from '../TextBuilder/TextBuilder';
// import Topbar from '../Topbar/Topbar';

import './Filter.scss';

var filterTemplate = {
  count: 'number',
  page: 'number',
  order_by: ['upload_date', 'subject', 'sentiment'],
  desc: 'checkbox',
  date_min: 'date',
  date_max: 'date',
  sentiment: ['foo', 'faa'],
  subject: ['foo', 'faa'],
  text_contains: 'text'
};

class Filter extends Component {
  constructor (props) {
    super(props);
    this.state = {};
    this.handleChange = {};
    for (let name in filterTemplate) {
      this.handleChange[name] = this.onChange.bind(this, name);
      this.state[name] = '';
    }
  }
  onChange (name, e) {
    let c = {};
    c[name] = e.target.value;
    this.setState(c);
  }
    submit = (e) => {
      this.props.onSubmit && this.props.onSubmit(this.state);
    }
    render () {
      return (
        <Block className='Filter'>
          <label htmlFor='count'><strong>Anzuzeigende Elemente:</strong></label>
          <input name='count' type='number' onChange={this.handleChange.count} value={this.state.count} />

          <label htmlFor='page'><strong>Seite:</strong></label>
          <input name='page' type='number' onChange={this.handleChange.page} value={this.state.page} />

          <label htmlFor='order_by'><strong>Sortieren:</strong></label>
          <Select name='order_by' values={filterTemplate.order_by} onChange={this.handleChange.order_by} value={this.state.order_by} />

          <label htmlFor='desc'><strong>Absteigend:</strong></label>
          <input name='desc' type='checkbox' onChange={this.handleChange.desc} value={this.state.desc} />

          <label htmlFor='sentiment'><strong>Sortieren:</strong></label>
          <Select name='sentiment' values={filterTemplate.sentiment} onChange={this.handleChange.sentiment} value={this.state.sentiment} />

          <label htmlFor='subject'><strong>Sortieren:</strong></label>
          <Select name='subject' values={filterTemplate.subject} onChange={this.handleChange.subject} value={this.state.subject} />

          <label htmlFor='text_contains'><strong>Anzuzeigende Elemente:</strong></label>
          <input name='text_contains' type='text' onChange={this.handleChange.text_contains} value={this.state.text_contains} />

          <input type='button' value='Anwenden' onClick={this.submit} />
        </Block>
      );
    }
}

export default Filter;
