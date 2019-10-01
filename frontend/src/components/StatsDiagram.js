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
    const { type = 0, data, style } = { ...this.props };
    console.log(type);
    let barcharts = [renderBarchart, renderBarchartPercent, renderBarchartSentiment];
    let index = 0;
    if (data.colors) {
      index = 1;
    } else if (type === 'Sentiment') {
      index = 2;
    }
    return (
      <D3 style={style} data={data} render={barcharts[index]} />
    );
  }
}

const renderBarchartSentiment = (target, data3, d3) => {
  let data = data3.data.reverse();

  data = data.map(d => { return { key: shorterText(String(d.key)), value: d.value }; });

  let width = 350;
  const barHeight = 32;
  var height = barHeight * data.length;

  var widthView = 500;
  var heightView = height + 50;

  // set the ranges
  var x = d3.scaleLinear()
    .range([0, width])
    .domain([-1, 1]);

  var y = d3.scaleBand()
    .range([height, 0])
    .padding(0)
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
    .attr('height', barHeight)
    .attr('fill', (d, i) => (d.value < -0.02 ? 'red' : d.value > 0.02 ? 'green' : 'gray'))
    .attr('stroke', '#ffffff')
    .attr('stroke-width', '2')
    .attr('stroke-dasharray', d => (Math.abs(d.value) <= 0.02 ? x(0.02) - x(-0.02) : Math.abs(x(d.value) - x(0))) + ', ' + barHeight);
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

  data = data.map(d => { return { key: shorterText(String(d.key)), value: d.value }; });

  let width = 270;
  const barHeight = 32;
  var legendRectSize = 12;
  var legendSpacing = 8;

  var height = barHeight * data.map(d => d.key).filter((value, index, self) => {
    return self.indexOf(value) === index;
  }).length;

  var widthView = 500;
  var heightView = Math.max(height + 50, (Object.values(colors).length) * (legendRectSize + legendSpacing));

  // set the ranges
  var x = d3.scaleLinear()
    .range([0, width]);

  var y = d3.scaleBand()
    .range([height, 0])
    .padding(0);

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
    .attr('x', function (d, i) { return x(d.value[1] - d.value[0]) + 1; })
    .attr('height', barHeight)
    .attr('y', function (d, i) { return y(d.key); })
    .attr('width', function (d, i) { return x(d.value[0]); })
    .attr('fill', (d, i) => d.value[2])
    .attr('stroke', '#ffffff')
    .attr('stroke-width', '2')
    .attr('stroke-dasharray', d => x(d.value[0]) + ', ' + barHeight);

  // add the x Axis
  svg.append('g')
    .attr('transform', 'translate(0,' + height + ')')
    .call(d3.axisBottom(x));

  // add the y Axis
  svg.append('g')
    .call(d3.axisLeft(y));

  var legend = svg.selectAll('.legend')
    .data(Object.values(colors))
    .enter()
    .append('g')
    .attr('class', 'legend')
    .attr('transform', function (d, i) {
      var height = legendRectSize + legendSpacing;
      var horz = 280;
      var vert = i * height;
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
    .style('font-size', '0.8125rem')
    .text(function (d, i) { return Object.keys(colors)[i]; });
};
const renderBarchart = (target, data3, d3) => {
  let data = data3.data.reverse();

  if (data[0] && data[0].key.id) {
    data = data.map(d => { return { key: d.key.id, value: d.value }; });
  }
  data = data.map(d => { return { key: shorterText(String(d.key)), value: d.value }; });

  let width = 350;
  const barHeight = 32;
  var height = barHeight * data.length;

  var widthView = 500;
  var heightView = height + 50;

  // set the ranges
  var x = d3.scaleLinear()
    .range([0, width]);
  var y = d3.scaleBand()
    .range([height, 0])
    .padding(0);

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
    .attr('x', 1)
    // .attr('height', y.bandwidth())
    .attr('height', barHeight)
    .attr('y', function (d, i) { return y(d.key); })
    // .attr('y', function (d, i) { return i * 20.1; })
    .attr('width', function (d) { return x(d.value); })
    .attr('fill', '#179c7d')
    .attr('stroke', '#ffffff')
    .attr('stroke-width', '2')
    .attr('stroke-dasharray', d => x(d.value) + ', ' + barHeight);

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
/**
 * If the text is too long, the last characters are removed and marked with "..."
 * @param {*} text which is analyzed
 */
const shorterText = (text) => {
  var canvas = shorterText.canvas || (shorterText.canvas = document.createElement('canvas'));
  var context = canvas.getContext('2d');
  context.font = '16px helvetica';
  if (context.measureText(text).width <= 147) {
    return text;
  }
  while (context.measureText(text).width > 134) {
    text = text.substring(0, text.length - 1);
  }
  return text + '...';
};

export default StatsDiagram;
