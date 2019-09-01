/**
 * This class creates the Statistics view.
 *
 * @version <0.1>
 */

import React from 'react';

import Block from '../components/Block';
import Content from '../components/Content';
import Row from '../components/Row';
import D3 from '../components/D3';

const renderBarchart = (target, data, d3) => {
  var width = 420;
  var barHeight = 20;

  var x = d3.scaleLinear()
    .domain([0, d3.max(data, d => d.width)])
    .range([0, width]);

  var chart = d3.select(target).select('.chart')
    .attr('width', width)
    .attr('height', barHeight * data.length);

  var bar = chart.selectAll('g')
    .data(data)
    .enter().append('g')
    .attr('transform', function (d, i) { return 'translate(0,' + i * barHeight + ')'; });

  bar.append('rect')
    .attr('width', d => x(d.width))
    .attr('height', barHeight - 1);

  bar.append('text')
    .attr('x', function (d) { return x(d.width) - 3; })
    .attr('y', barHeight / 2)
    .attr('dy', '.35em')
    .text(function (d) { return d.label; });
};

const fakeData = (count = 5) => {
  let data = [];
  for (var i = count; i > 0; i--) {
    data.push({ width: ~~(Math.random() * 10), label: i });
  }
  return data;
};

function Statistics () {
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <h6 className='center'>Statistiken</h6>
          <Content>
            <Row>
              <D3 data={fakeData()} render={renderBarchart}>
                <svg className='chart' />
              </D3>
              <D3 data={fakeData()} render={renderBarchart}>
                <svg className='chart' />
              </D3>
              <D3 data={fakeData()} render={renderBarchart}>
                <svg className='chart' />
              </D3>
            </Row>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default Statistics;
