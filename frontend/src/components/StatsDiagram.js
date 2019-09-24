/**
 * Diagrams for the Statistic Page
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import D3 from '../components/D3';

class StatsDiagram extends Component {
  constructor (props) {
    super(props);
    this.container = React.createRef();
    this.stats = {
      data: null
    };
  }

  render () {
    const { id, data, style } = { ...this.props };
    let barcharts = [renderBarchart, renderBarchartPercent, renderBarchartSentiment];
    let index = 0;
    if (data.colors) {
      index = 1;
    } else if (id.startsWith('Sentiment')) {
      index = 2;
    }
    return (
      <D3 id={id} style={style} data={data} render={barcharts[index]} />
    );
  }
}

const renderBarchartSentiment = (target, data3, d3) => {
  let data = data3.data.reverse();

  let width = 350;
  var height = 350;

  var widthView = 500;
  var heightView = 400;

  // set the ranges
  var x = d3.scaleLinear()
    .range([0, width])
    .domain([-1, 1]);

  var y = d3.scaleBand()
    .range([height, 0])
    .padding(0.1)
    .domain(data.map(function (d, i) { return d.key; }));

  // append the svg object to the body of the page
  // append a 'group' element to 'svg'
  // moves the 'group' element to the top left margin
  d3.select(target).selectAll('div').remove();
  var svg = d3.select(target)
    .append('div')
  // Container class to make it responsive.
    .classed('svg-container', true)
    .append('svg')
    .attr('preserveAspectRatio', 'xMinYMin meet')
    .attr('viewBox', '-100 -20 ' + widthView + ' ' + heightView);

  /*
  svg.selectAll('.bar')
    .data(data)
    .enter().append('rect')
    .attr('class', function (d) { return d.value < 0 ? 'bar negative' : 'bar positive'; })
    .attr('y', function (d, i) { return y(d.key); })
    .attr('x', function (d, i) { return x(d.value - 0.02); })
    .attr('width', function (d) { return Math.abs(x(d.value + 0.02) - x(d.value - 0.02)); })
    .attr('height', y.bandwidth())
    .attr('fill', (d, i) => '#179c7d');
*/
  svg.selectAll('.bar')
    .data(data)
    .enter().append('rect')
    .attr('class', function (d) { return d.value < 0 ? 'bar negative' : 'bar positive'; })
    .attr('y', function (d, i) { return y(d.key); })
    .attr('x', function (d, i) { return Math.abs(d.value) <= 0.02 ? x(-0.02) : Math.min(x(0), x(d.value)); })
    .attr('width', function (d) { return Math.abs(d.value) <= 0.02 ? x(0.02) - x(-0.02) : Math.abs(x(d.value) - x(0)); })
    .attr('height', y.bandwidth())
    .attr('fill', (d, i) => (d.value < -0.02 ? 'red' : d.value > 0.02 ? 'green' : 'gray'));
  svg.append('line')
    .style('stroke', 'black')
    .style('stroke-dasharray', '10')
    .attr('y1', 0)
    .attr('y2', height)
    .attr('x1', x(0))
    .attr('x2', x(0));

  // add the y Axis
  svg.append('g')
    .call(d3.axisLeft(y));
  // add the x Axis
  svg.append('g')
    .attr('transform', 'translate(0,' + height + ')')
    .call(d3.axisBottom(x));

  svg.append('g')
    .attr('class', 'y axis')
    .append('line')
    .attr('y1', y(0))
    .attr('y2', y(0))
    .attr('x1', 0)
    .attr('x2', width);
};
const renderBarchartPercent = (target, data3, d3) => {
  let data = data3.data;
  let colors = data3.colors;

  let width = 270;
  var height = 350;

  var widthView = 500;
  var heightView = 400;

  // set the ranges
  var x = d3.scaleLinear()
    .range([0, width]);

  var y = d3.scaleBand()
    .range([height, 0])
    .padding(0.2);

  // append the svg object to the body of the page
  // append a 'group' element to 'svg'
  // moves the 'group' element to the top left margin
  d3.select(target).selectAll('div').remove();
  var svg = d3.select(target)
    .append('div')
  // Container class to make it responsive.
    .classed('svg-container', true)
    .append('svg')
    .attr('preserveAspectRatio', 'xMinYMin meet')
    .attr('viewBox', '-100 -20 ' + widthView + ' ' + heightView);

  // Scale the range of the data in the domains
  y.domain(data.map(function (d, i) { return d.key; }));
  x.domain([0, d3.max(data, function (d, i) { return 100; })]);

  // append the rectangles for the bar chart
  svg.selectAll('.bar')
    .data(data)
    .enter().append('rect')
    .attr('class', 'bar')
    .attr('x', function (d, i) { return x(d.value[1] - d.value[0]); })
    .attr('height', y.bandwidth())
    .attr('y', function (d, i) { return y(d.key); })
    .attr('width', function (d, i) { return x(d.value[0]); })
    .attr('fill', (d, i) => d.value[2]);

  // add the x Axis
  svg.append('g')
    .attr('transform', 'translate(0,' + height + ')')
    .call(d3.axisBottom(x));

  // add the y Axis
  svg.append('g')
    .call(d3.axisLeft(y));

  var legendRectSize = 12;
  var legendSpacing = 8;
  var legend = svg.selectAll('.legend')
    .data(Object.values(colors))
    .enter()
    .append('g')
    .attr('class', 'legend')
    .attr('transform', function (d, i) {
      var height = legendRectSize + legendSpacing;
      var horz = 280;
      var vert = i * height + 50;
      return 'translate(' + horz + ',' + vert + ')';
    });

  legend.append('rect')
    .attr('width', legendRectSize)
    .attr('height', legendRectSize)
    .style('fill', function (d, i) { return d; })
    .style('stroke', 'black');

  legend.append('text')
    .attr('x', legendRectSize + legendSpacing / 2)
    .attr('y', legendRectSize - legendSpacing / 4)
    .style('font-size', '0.8125em')
    .text(function (d, i) { return Object.keys(colors)[i]; });
};
const renderBarchart = (target, data3, d3) => {
  let data = data3.data.reverse();

  let width = 350;
  var height = 350;

  var widthView = 500;
  var heightView = 400;

  // set the ranges
  var x = d3.scaleLinear()
    .range([0, width]);
  var y = d3.scaleBand()
    .range([height, 0])
    .padding(0.1);

  d3.select(target).selectAll('div').remove();
  var svg = d3.select(target)
    .append('div')
  // Container class to make it responsive.
    .classed('svg-container', true)
    .append('svg')
    .attr('preserveAspectRatio', 'xMinYMin meet')
    .attr('viewBox', '-100 -20 ' + widthView + ' ' + heightView);

  // Scale the range of the data in the domains
  y.domain(data.map(function (d, i) { return d.key; }));
  x.domain([0, d3.max(data, function (d) { return d.value; })]);

  // append the rectangles for the bar chart
  svg.selectAll('.bar')
    .data(data)
    .enter().append('rect')
    .attr('class', 'bar')
    .attr('x', function (d, i) { return 0; })
    .attr('height', y.bandwidth())
    .attr('y', function (d, i) { return y(d.key); })
    .attr('width', function (d) { return x(d.value); })
    .attr('fill', (d, i) => '#179c7d');

  const xAxisTicks = x.ticks()
    .filter(tick => Number.isInteger(tick));

  // add the x Axis
  svg.append('g')
    .attr('transform', 'translate(0,' + height + ')')
    .call(d3.axisBottom(x).tickValues(xAxisTicks)
      .tickFormat(d3.format('d')));

  // add the y Axis
  svg.append('g')
    .call(d3.axisLeft(y));
};

export default StatsDiagram;
