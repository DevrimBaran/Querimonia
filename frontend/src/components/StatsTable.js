/**
 * Tables for the Statistic Page
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import Table from './Table';
// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';

class StatsTable extends Component {
  constructor (props) {
    super(props);
    this.container = React.createRef();
    this.state = {
      data: null
    };
  }

  renderTable1 = (data) => {
    return (
      <Table fix=''>
        <thead>
          <tr>
            {data.header.map((h, i) => <th key={i}>{h}</th>)}
          </tr>
        </thead>
        <tbody>
          {data.data.map((d, i) => <tr key={i}>
            {d.key.id && !String(d.key.id).startsWith('gelöscht')
              ? <td><Link to={'/components/' + d.key.id}>{d.key.name + ' (' + d.key.id + ')'}</Link></td>
              : <td>{d.key.id ? d.key.name + ' (' + d.key.id + ')' : d.key}</td>}
            <td>{Number(d.value).toFixed(2)}</td>
          </tr>)}
        </tbody>
      </Table>
    );
  }

  renderTable2 = (data) => {
    let s = data.keys;
    let stati = Object.keys(data.colors);
    return (
      <Table fix='left'>
        <thead>
          <tr>
            <th>%</th>
            {s.map((d, i) => <th key={i}>{d}</th>)}
          </tr>
        </thead>
        <tbody>
          {stati.map((d, i) => <tr key={i}>
            <th>{d}</th>
            {s.map((l, j) =>
              <td className='td-align-right' key={j}>{Number((data.data.filter(v => v.value[3] === d).find((v, k) => v.value[4] === l) ? data.data.filter(v => v.value[3] === d).find((v, k) => v.value[4] === l).value[0] : 0)).toFixed(2)}</td>
            )}
          </tr>)}
        </tbody>
      </Table>
    );
  }

  render () {
    const { id, data, style } = { ...this.props };
    return (
      <div id={id} style={style} >
        {data.colors ? this.renderTable2(data) : this.renderTable1(data)}
      </div>
    );
  }
}

export default StatsTable;
