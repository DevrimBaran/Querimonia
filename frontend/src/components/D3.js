/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Debug from './Debug';

var cloud = require('../assets/js/d3.layout.cloud');

class D3 extends Component {
  constructor (props) {
    super(props);
    this.state = {
      data: [],
      updating: true
    };
  }
  list = (object, index) => {
    return (
      <tr key={index}>
        <td>{object.text}</td>
        <td>{object.value}</td>
      </tr>
    );
  }
  renderChart = (type, data) => {
    switch (type) {
      case 'cloud': {
        return (
          <svg width={window.innerWidth / 100 * 80} height={window.innerHeight - 190}>
            <g transform={'translate(' + (window.innerWidth / 100 * 80) + ', ' + (window.innerHeight - 190) + ')'}>
              {data.map((word, index) => {
                if (word.text && word.x && word.y && word.rotate) {
                  return <text key={word.text} textAnchor='middle' transform={'translate(' + word.x + ',' + word.y + ')rotate(' + word.rotate + ')'}>{word.text}</text>;
                }
                return <Debug key={index} data={word} />;
              })}
            </g>
          </svg>
        );
      }
      case 'list': {
        return (
          <table>
            <tbody>
              {data.map((object, index) => {
                return (
                  <tr key={index}>
                    <td>{object.text}</td>
                    <td>{object.value}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        );
      }
      default: {
        return (
          <Debug data={data} />
        );
      }
    }
  }
  chartState = (type, data) => {
    this.setState({
      updating: true
    });
    switch (type) {
      case 'cloud': {
        let mappedData = Object.keys(data).map(word => ({ text: word, size: data[word] }));
        cloud()
          .size(window.innerWidth / 100 * 80, window.innerHeight - 190)
          .words(mappedData)
          .padding(0)
          .rotate(() => 0)
          .on('end', words => {
            this.setState({
              data: words,
              updating: false
            });
          })
          .start();
        break;
      }
      case 'list': {
        let mappedData = Object.keys(data)
          .map(word => ({ text: word, value: data[word] }))
          .sort((a, b) => (a.value - b.value));
        this.setState({
          data: mappedData,
          updating: false
        });
        break;
      }
      default: {
        return data;
      }
    }
  }
  componentDidMount = () => {
    console.log('didMount');
    this.chartState(this.props.type, this.props.data);
  }
  componentDidUpdate = (prevProps) => {
    console.log('didUpdate');
    if (prevProps.type !== this.props.type && !this.state.updating) {
      this.componentDidMount();
    }
  }
  render () {
    const { type, data, ...passThrough } = { ...this.props };
    return (
      <div className='d3' {...passThrough}>
        {this.state.updating ? <i className='fa fa-spinner' /> : this.renderChart(type, this.state.data)}
      </div>
    );
  }
}

export default D3;
