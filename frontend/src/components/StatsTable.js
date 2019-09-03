/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import Table from '../components/Table';
// eslint-disable-next-line
import { BrowserRouter as Router, Link, withRouter } from 'react-router-dom';

class StatsTable extends Component {
  constructor (props) {
    super(props);
    this.container = React.createRef();
    this.setState({
      data: null
    });
  }

  renderTable1 = (data) => {
    return (
      <Table>
        <thead>
          <tr>
            {data.header.map((h, i) => <th>{h}</th>)}
          </tr>
        </thead>
        <tbody>
          {data.key.map((d, i) => <tr>
            {data.header[0] === 'ID' ? <td><Link to={'/components/' + d}>{d}</Link></td> : <td>{d}</td>}
            <td>{parseFloat(Number(data.value[i]).toFixed(2))}</td>
          </tr>)}
        </tbody>
      </Table>
    );
  }

  renderTable2 = (data) => {
    let s = [...new Set(data.key)];
    let stati = Object.keys(data.colors);
    return (
      <Table>
        <thead>
          <tr>
            <th>%</th>
            {s.map((d, i) => <th>{d}</th>)}
          </tr>
        </thead>
        <tbody>
          {stati.map((d, i) => <tr>
            <th>{d}</th>
            {s.map((l, j) =>
              <td>{parseFloat(Number((data.value.filter(v => v[3] === d).find((v, k) => v[4] === l) ? data.value.filter(v => v[3] === d).find((v, k) => v[4] === l)[0] : 0).toFixed(2)))}</td>
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
