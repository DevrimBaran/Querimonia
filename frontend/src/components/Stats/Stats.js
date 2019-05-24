import React, { Component } from 'react'
import Chart from 'chart.js';
//import Api from '../../utility/Api';

//import Block from '../Block/Block';
//import Body from '../Body/Body';
//import Collapsible from '../Collapsible/Collapsible';
//import Fa from '../Fa/Fa';
//import Filter from '../Filter/Filter';
//import Issues from '../Issues/Issues';
//import Log from '../Log/Log.js';
//import Modal from '../Modal/Modal';
//import Stats from '../Stats/Stats';
//import Tabbed from '../Tabbed/Tabbed';
//import Table from '../Table/Table';
//import TaggedText from '../TaggedText/TaggedText';
//import Text from '../Text/Text';
//import TextBuilder from '../TextBuilder/TextBuilder';
//import Topbar from '../Topbar/Topbar';

import './Stats.scss';
class Stats extends Component {
    constructor(props) {
        super(props);
        this.state = {}
    }

    componentDidMount = () => {
        var data = {
            datasets: [{
                data: [12, 19, 3, ],
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
                ]
            }],
        
            labels:  ['faher unfreundlich', 'bus verpasst', 'sonstiges']
        };
        var ctx = this.refs.canvas;
        var myChart = new Chart(ctx, {
            type: 'pie',
            data:data,
            options: {
                scales: {
                
                }
            }
        });
        
    }

    render() {
        return (
            <div className="Stats">
                <canvas ref="canvas" id="myChart" width="400" height="400"></canvas>
            </div>
        );
    }
}

export default Stats;