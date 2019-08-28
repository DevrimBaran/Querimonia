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

// nur demo, styles sollten in scss datei
const barStyle = {
  display: 'flex',
  flexDirection: 'row',
  flexWrap: 'no-wrap',
  alignItems: 'flex-end',
  alignSelf: 'flex-start',
  padding: 0,
  height: '100px',
  border: '1px solid black',
  margin: '0 10px'
};

const renderBarchart = (target, data, d3) => {
  d3.select(target)
    .selectAll('div')
    .data(data)
    .enter().append('div')
    .style('background-color', 'black')
    .style('border', '1px solid white')
    .style('width', '20px')
    .style('height', d => (d.height * 10) + 'px');
};

const fakeData = (count = 5) => {
  let data = [];
  for (var i = count; i > 0; i--) {
    data.push({ height: ~~(Math.random() * 10) });
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
              <D3 data={fakeData()} render={renderBarchart} style={barStyle} />
              <D3 data={fakeData()} render={renderBarchart} style={barStyle} />
              <D3 data={fakeData()} render={renderBarchart} style={barStyle} />
            </Row>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}

export default Statistics;
