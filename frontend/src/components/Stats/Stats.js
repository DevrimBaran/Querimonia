import React, { Component } from 'react';
import Chart from 'chart.js';
// import Api from '../../utility/Api';

// import Block from '../Block/Block';
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

import './Stats.scss';
class Stats extends Component {
  constructor (props) {
    super(props);
    this.data = [];
    this.labels = [];
    for (let key in this.props.data) {
      this.labels.push(key);
      this.data.push(this.props.data[key]);
    }
    this.state = {};
  }

    componentDidMount = () => {
      var ctx = this.refs.canvas;
      new Chart(ctx, {
        type: 'horizontalBar',
        data: {
          labels: this.labels,
          datasets: [{
            data: this.data,
            label: '',
            backgroundColor: [
              'rgba(255, 99, 132, 0.2)',
              'rgba(54, 162, 235, 0.2)',
              'rgba(255, 206, 86, 0.2)',
              'rgba(75, 192, 192, 0.2)',
              'rgba(153, 102, 255, 0.2)',
              'rgba(255, 159, 64, 0.2)'
            ],
            borderColor: [
              'rgba(255, 99, 132, 1)',
              'rgba(54, 162, 235, 1)',
              'rgba(255, 206, 86, 1)',
              'rgba(75, 192, 192, 1)',
              'rgba(153, 102, 255, 1)',
              'rgba(255, 159, 64, 1)'
            ],
            hoverBackgroundColor: [
              'rgba(255, 99, 132, 0.5)',
              'rgba(54, 162, 235, 0.5)',
              'rgba(255, 206, 86, 0.5)',
              'rgba(75, 192, 192, 0.5)',
              'rgba(153, 102, 255, 0.5)',
              'rgba(255, 159, 64, 0.5)'],
            borderWidth: 1
          }]
        },
        options: {
          scales: {
            yAxes: [{
              ticks: {
                beginAtZero: true
              }
            }],
            xAxes: [{
              ticks: {
                beginAtZero: true
              }
            }]
          }
        }
      }
      );
    }

    render () {
      return (
        <div className='Stats'>
          <h2>{this.props.label}</h2> 
          <canvas ref='canvas' id='CategorieChart' />
        </div>
      );
    }
}

export default Stats;
