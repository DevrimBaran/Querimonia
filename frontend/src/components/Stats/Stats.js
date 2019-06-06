import React, { Component } from 'react';
import Chart from 'chart.js';
//import Api from '../../utility/Api';

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
    this.state = {
      //loading: true,
      //active: null,
      //issues: []
    };
  }

    componentDidMount = () => {
      var jsonexample = {
        'subject': [
          {
            'object': 'Fahrer unfreundlich',
            'number': 0.5
          },
          {
            'object': 'Fahrt nicht erfolgt',
            'number': 0.4
          },
          {
            'object': 'Sonstiges',
            'number': 0.1
          }

        ]
      };

      //var labels = [jsonexample.subject[0].object, jsonexample.subject[1].object, jsonexample.subject[2].object];
      //console.log(labels);
      var data = {
        datasets: [{
          data: [jsonexample.subject[0].number, jsonexample.subject[1].number, jsonexample.subject[2].number],
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
            'rgba(75, 192, 192, 1)'
          ],
          hoverBackgroundColor: [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)',
            'rgba(255, 159, 64, 1)']

        }],

        labels: [jsonexample.subject[0].object, jsonexample.subject[1].object, jsonexample.subject[2].object]
      };
      var ctx = this.refs.canvas;
      new Chart(ctx, {
        type: 'pie',
        data: data,
        options: {
          scales: {

          }
        }
      });
    }

    render () {
      return (
        <div className='Stats'>
          <canvas ref='canvas' id='CategorieChart' width='400' height='400' />
        </div>
      );
    }
}

export default Stats;
