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
class Filter extends Component {
  constructor (props) {
    super(props);
    this.data = {
      'where': {
        'relation': 'AND',
        'clauses': []
      },
      'orderby': [],
      'offset': 0,
      'limit': 20
    };
  }
  remove (array, index) {
    array.splice(index, 1);
    this.forceUpdate();
  }
  add (array, data) {
    array.push(data);
    this.forceUpdate();
  }
  modifyObject (obj, key, value) {
    if (value.target) value = value.target.value;
    obj[key] = value;
    this.forceUpdate();
  }
    parseQueryBlock = (block, index, parent) => {
      if (block.relation) {
        return (
          <Block className='relation' key={index}>
            {parent && (<span className='remove' key='remove' onClick={this.remove.bind(this, parent.clauses, index)} />)}
            <Select className='relation' key='relation' onChange={this.modifyObject.bind(this, block, 'relation')} name={'relation' + index} values={['AND', 'OR']} value={block.relation} />
            <br />
            {block.clauses.map((clause, index) => {
              return this.parseQueryBlock(clause, index, block);
            })}
            <input type='button' onClick={this.add.bind(this, block.clauses, { 'relation': 'AND', 'clauses': [] })} value='Add Relation' />
            <input type='button' onClick={this.add.bind(this, block.clauses, { 'key': this.props.keys[0], 'value': '', 'compare': this.props.comparators[0] })} value='Add' />
          </Block>
        );
      }
      return (
        <Block className='clause' key={index}>
          <Select onChange={this.modifyObject.bind(this, block, 'key')} name='key' values={this.props.keys} value={block.key} />
          <Select onChange={this.modifyObject.bind(this, block, 'compare')} name='compare' values={this.props.comparators} value={block.compare} />
          <input onChange={this.modifyObject.bind(this, block, 'value')} name='value' type='text' value={block.value} />
          {parent && <span key='remove' className='remove' onClick={this.remove.bind(this, parent.clauses, index)} />}
        </Block>
      );
    }
    parseSortBlock = (block) => {
      let items = [];
      for (let order of block) {
        items.push(
          <Block className='sort-item' key={items.length}>
            <Select onChange={this.modifyObject.bind(this, order, 'key')} name={order.key} values={this.props.keys} value={order.key} />
            <Select onChange={this.modifyObject.bind(this, order, 'order')} values={['ASC', 'DESC']} value={order.order} name={order.key} />
            <span key='remove' onClick={this.remove.bind(this, block, items.length)} />
          </Block>
        );
      }
      return (
        <Block className='sort'>
          {items}
          <input type='button' onClick={this.add.bind(this, block, { key: this.props.keys[0], order: 'ASC' })} value='Add' />
        </Block>
      );
    }
    submit = () => {
      this.props.onSubmit && this.props.onSubmit(this.data);
    }
    render () {
      return (
        <Block className='Filter'>
          <strong>Anzuzeigende Elemente:</strong>
          <br />
          <input type='number' onChange={this.modifyObject.bind(this, this.data, 'limit')} value={this.data.limit} />
          <br />
          <strong>Filter:</strong>
          {this.parseQueryBlock(this.data.where)}
          <strong>Sortierung:</strong>
          {this.parseSortBlock(this.data.orderby)}
          <input type='button' value='Anwenden' onClick={this.submit} />
        </Block>
      );
    }
}

export default Filter;
