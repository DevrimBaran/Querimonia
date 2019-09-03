/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import D3 from '../components/D3';

class StatsDiagram extends Component {
  constructor (props) {
    super(props);
    this.container = React.createRef();
    this.setState({
      data: null
    });
  }

  render () {
    const { id, data, style } = { ...this.props };
    let barcharts = [renderBarchart3, renderBarchart5, renderBarchart6];
    let index = 0;
    if (data.colors) {
      index = 1;
    } else if (data.value.find(d => d < 0)) {
      index = 2;
    }
    return (
      <D3 id={id} style={style} data={data} render={barcharts[index]} />
    );
  }
}

const renderBarchart6 = (target, data3, d3) => {
  let data = data3.value;
  let dataKeys = data3.key;

  var width = 300;
  var height = 300;

  if (dataKeys.length > 4) {
    width = 400;
  }

  var widthView = 500;
  var heightView = 450;

  // set the ranges
  var x = d3.scaleBand()
    .range([0, width])
    .padding(0.1)
    .domain(data.map(function (d, i) { return dataKeys[i]; }));
  var y = d3.scaleLinear()
    .range([height, 0])
    .domain([-1, 1]);

  var yAxis = d3.axisLeft(y);

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
    .attr('viewBox', '-50 -20 ' + widthView + ' ' + heightView);

  svg.selectAll('.bar')
    .data(data)
    .enter().append('rect')
    .attr('class', function (d) { return d < 0 ? 'bar negative' : 'bar positive'; })
    .attr('y', function (d) { return y(d + 0.02); })
    .attr('x', function (d, i) { return x(dataKeys[i]); })
    .attr('height', function (d) { return Math.abs(y(d + 0.02) - y(d - 0.02)); })
    .attr('width', x.bandwidth())
    .attr('fill', (d, i) => '#179c7d');

  svg.append('g')
    .attr('class', 'x axis')
    .call(yAxis);

  // add the x Axis
  svg.append('g')
    .attr('transform', 'translate(0,' + height + ')')
    .call(d3.axisBottom(x))
    .selectAll('text')
    .attr('y', 0)
    .attr('x', 9)
    .attr('dy', '.35em')
    .attr('transform', 'rotate(90)')
    .style('text-anchor', 'start');

  svg.append('g')
    .attr('class', 'y axis')
    .append('line')
    .attr('y1', y(0))
    .attr('y2', y(0))
    .attr('x1', 0)
    .attr('x2', width);
};
const renderBarchart5 = (target, data3, d3) => {
  let data = data3.value;
  let dataKeys = data3.key;
  let colors = data3.colors;

  let width = 300;
  var height = 300;

  var widthView = 500;
  var heightView = 450;

  // set the ranges
  var x = d3.scaleBand()
    .range([0, width])
    .padding(0.2);
  var y = d3.scaleLinear()
    .range([height, 0]);

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
    .attr('viewBox', '-50 -20 ' + widthView + ' ' + heightView);

  // Scale the range of the data in the domains
  x.domain(data.map(function (d, i) { return dataKeys[i]; }));
  y.domain([0, d3.max(data, function (d, i) { return 100; })]);

  // append the rectangles for the bar chart
  svg.selectAll('.bar')
    .data(data)
    .enter().append('rect')
    .attr('class', 'bar')
    .attr('x', function (d, i) { return x(dataKeys[i]); })
    .attr('width', x.bandwidth())
    .attr('y', function (d, i) { return y(d[1]); })
    .attr('height', function (d, i) { return height - y(d[0]); })
    .attr('fill', (d, i) => d[2]);

  // add the x Axis
  svg.append('g')
    .attr('transform', 'translate(0,' + height + ')')
    .call(d3.axisBottom(x))
    .selectAll('text')
    .attr('y', 0)
    .attr('x', 9)
    .attr('dy', '.35em')
    .attr('transform', 'rotate(90)')
    .style('text-anchor', 'start');

  // add the y Axis
  svg.append('g')
    .call(d3.axisLeft(y));

  var legendRectSize = 15;
  var legendSpacing = 8;
  var legend = svg.selectAll('.legend')
    .data(Object.values(colors))
    .enter()
    .append('g')
    .attr('class', 'legend')
    .attr('transform', function (d, i) {
      var height = legendRectSize + legendSpacing;
      var horz = 300;
      var vert = i * height + 10;
      return 'translate(' + horz + ',' + vert + ')';
    });

  legend.append('rect')
    .attr('width', legendRectSize)
    .attr('height', legendRectSize)
    .style('fill', function (d, i) { return d; })
    .style('stroke', 'black');

  legend.append('text')
    .attr('x', legendRectSize + legendSpacing)
    .attr('y', legendRectSize - legendSpacing / 4)
    .text(function (d, i) { return Object.keys(colors)[i]; });
};
const renderBarchart3 = (target, data3, d3) => {
  let data = data3.value;
  let dataKeys = data3.key;

  var width = 300;
  var height = 300;

  if (dataKeys.length > 4) {
    width = 400;
  }

  var widthView = 500;
  var heightView = 450;

  // set the ranges
  var x = d3.scaleBand()
    .range([0, width])
    .padding(0.1);
  var y = d3.scaleLinear()
    .range([height, 0]);

  d3.select(target).selectAll('div').remove();
  var svg = d3.select(target)
    .append('div')
  // Container class to make it responsive.
    .classed('svg-container', true)
    .append('svg')
    .attr('preserveAspectRatio', 'xMinYMin meet')
    .attr('viewBox', '-50 -20 ' + widthView + ' ' + heightView);

  // Scale the range of the data in the domains
  x.domain(data.map(function (d, i) { return dataKeys[i]; }));
  y.domain([0, d3.max(data, function (d) { return d; })]);

  // append the rectangles for the bar chart
  svg.selectAll('.bar')
    .data(data)
    .enter().append('rect')
    .attr('class', 'bar')
    .attr('x', function (d, i) { return x(dataKeys[i]); })
    .attr('width', x.bandwidth())
    .attr('y', function (d) { return y(d); })
    .attr('height', function (d) { return height - y(d); })
    .attr('fill', (d, i) => '#179c7d');

  // add the x Axis
  svg.append('g')
    .attr('transform', 'translate(0,' + height + ')')
    .call(d3.axisBottom(x))
    .selectAll('text')
    .attr('y', 0)
    .attr('x', 9)
    .attr('dy', '.35em')
    .attr('transform', 'rotate(90)')
    .style('text-anchor', 'start');

  // add the y Axis
  svg.append('g')
    .call(d3.axisLeft(y));
};

export default StatsDiagram;
