/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

// import Api from '../../utility/Api';

import Collapsible from 'components/Collapsible';
import Select from 'components/Select';
import Row from 'components/Row';
import Query from 'components/Query';
import Input from 'components/Input';

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

const QuerySelect = Query(Select);
const QueryInput = Query(Input);
class Filter extends Component {
  submit = (e) => {
    console.dir(e.target);
    // e.preventDefault();
    /* let query = {};
    for (let i = 0; i < e.target.elements.length; i++) {
      query[e.target.elements[i].name] = e.target.elements[i].value;
    }
    this.props.onSubmit && this.props.onSubmit(query);
   */
  }
  render () {
    // const QueryText = Query(input);
    const pathname = document.location.pathname;
    return (
      <Collapsible label='Filter' className='Filter'>
        <form action={pathname} onSubmit={this.submit}>
          <QueryInput type='hidden' name='page' />
          <QueryInput type='hidden' name='count' />
          <Row vertical style={{ alignItems: 'center' }}>
            <Row style={{ justifyContent: 'center' }}>
              <div>
                <label htmlFor='sentiment'><strong>Stimmung:</strong></label><br />
                <QuerySelect name='sentiment' values={filterTemplate.sentiment} />
              </div>
              <div style={{ width: '2em' }} />
              <div>
                <label htmlFor='subject'><strong>Kategorie:</strong></label><br />
                <QuerySelect name='subject' values={filterTemplate.subject} />
              </div>
              <div style={{ width: '2em' }} />
              <div>
                <label htmlFor='text_contains'><strong>Text:</strong></label><br />
                <QueryInput name='text_contains' type='text' />
              </div>
              <div style={{ width: '2em' }} />
              <div>
                <label htmlFor='order_by'><strong>Sortieren nach:</strong></label><br />
                <QuerySelect name='order_by' values={filterTemplate.order_by} />
              </div>
              <div style={{ width: '2em' }} />
              <div>
                <label htmlFor='desc'><strong>Absteigend:</strong></label><br />
                <QueryInput name='desc' type='checkbox' />
              </div>
            </Row>
            <br />
            <input type='submit' value='Anwenden' />
            <br />
          </Row>
        </form>
      </Collapsible>
    );
  }
}

export default Filter;
