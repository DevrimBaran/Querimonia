/**
 * This class creates the TagCloud view.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
// import randomColor from 'randomcolor';
// import TagCloud from 'react-tag-cloud';
import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import Api from './../utility/Api';
import D3 from './../components/D3';
import Input from './../components/Input';

var cloud = require('../assets/js/d3.layout.cloud');
const renderCloud = (target, data, d3) => {
  let draw = (words) => {
    d3.select(target).select('svg')
      .attr('width', layout.size()[0])
      .attr('height', layout.size()[1])
      .append('g')
      .attr('transform', 'translate(' + layout.size()[0] / 2 + ',' + layout.size()[1] / 2 + ')')
      .selectAll('text')
      .data(words)
      .enter().append('text')
      .style('font-size', function (d) { return d.size + 'px'; })
      .attr('text-anchor', 'middle')
      .attr('transform', function (d) {
        return 'translate(' + [d.x, d.y] + ')rotate(' + d.rotate + ')';
      })
      .text(function (d) { return d.text; });
  };

  let layout = cloud()
    .size([target.clientWidth, target.clientHeight])
    .words(data)
    .padding(5)
    .rotate(function () { return 0; })
    .fontSize(function (d) { return d.size; })
    .on('end', draw);

  layout.start();
};
const renderList = (target, data, d3) => {
  let row = d3.select(target).select('table')
    .selectAll('tr')
    .data(data)
    .enter()
    .append('tr');
  row.append('td')
    .text(d => d.text);
  row.append('td')
    .text(d => d.value);
};

class TagCloud extends Component {
  constructor (props) {
    super(props);
    this.state = {
      words: {},
      maxOccurrence: 0,
      cloudActive: true
    };
  }
  toggleChange = () => {
    this.setState({
      cloudActive: !this.state.cloudActive
    });
  }

  exportSvg = () => {
    let svgElement = document.getElementById('TagCloud').firstChild.firstChild;
    svgElement.setAttribute('xmlns', 'http://www.w3.org/2000/svg');
    const svg = svgElement.outerHTML;

    this.createDownload(svg, 'image/svg+xml; charset=utf-8', 'tagcloudSVG.svg');
  };

  exportCsv = () => {
    let csv = 'Token;Anzahl\r\n';
    Object.keys(this.state.words)
      .sort((a, b) => {
        return this.state.words[b] - this.state.words[a];
      })
      .forEach((word) => {
        csv += `${word};${this.state.words[word]}\r\n`;
      });

    this.createDownload(csv, 'text/csv;charset=utf-8;', 'tagCloudCSV.csv');
  };

  createDownload = (file, type, name) => {
    let link = document.createElement('a');
    if (link.download !== undefined) {
      let url = URL.createObjectURL(new Blob([file], { type: type }));
      link.setAttribute('href', url);
      link.setAttribute('download', name);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  };

  createWordArray = (wordsObject) => {
    let wordArray = [];
    Object.keys(wordsObject).forEach((element) => {
      wordArray.push({ text: element, value: this.calculateSize(wordsObject[element]), size: wordsObject[element] });
    });
    return wordArray;
  };

  calculateSize = (size) => {
    const tmax = this.state.maxOccurrence; // Höchste Anzahl
    const tmin = 1; // mindest Anzahl an Wörter
    const fmax = 130; // maximale Schriftgröße
    const fmin = 10; // minimale Schriftgröße
    const t = size; // value
    if (t > tmin) {
      return ((fmax * (t - tmin)) / (tmax - tmin));
    // (130 * (size / this.state.maxOccurrence));
    // ((fmax*(t - tmin))/(tmax-tmin));
    } else {
      return fmin;
      // fmin;
    }
    // (130 * (size / this.state.maxOccurrence));
    // ((fmax - fmin)*((size-tmin)/(this.state.maxOccurrence-tmin))+ fmin);
    // TODO: caclitlate size
  };

  fetchData = () => {
    let query = {};
    this.refs.minDate.value && (query.date_min = this.refs.minDate.value);
    this.refs.maxDate.value && (query.date_max = this.refs.maxDate.value);
    this.refs.onlyWords.checked && (query.words_only = this.refs.onlyWords.checked);
    this.refs.activeMode.checked && (this.setState({ cloudActive: this.refs.activeMode.checked }));
    this.refs.count.value && this.refs.count.value > 0 && (query.count = this.refs.count.value);
    Api.get('/api/stats/tagcloud', query)
      .then(data => {
        return data;
      })
      .then(this.setData);
  };

  setData = (data) => {
    const max = Math.max(...Object.values(data));
    // TODO: Calculate other constants
    this.setState({ words: data, maxOccurrence: max });
  };

  onResize = () => {
    this.setState(this.state);
  };

  componentDidMount () {
    this.fetchData();
    window.addEventListener('resize', this.onResize);
  }

  componentWillUnmount () {
    window.removeEventListener('resize', this.onResize);
  }

  render () {
    return (
      <React.Fragment>
        <Block>
          <h1 className='center'>Worthäufigkeiten</h1>
          <Row vertical>
            <br />
            <h1 className='center'>Worthäufigkeiten</h1>
            <div>
              <Row vertical={false} style={{ justifyContent: 'space-around' }}>
                <div>
                  <Input type='date' label='Eingangsdatum (von): ' id='minDate' ref='minDate' />
                </div>
                <div>
                  <Input type='date' label='Eingangsdatum (bis): ' id='maxDate' ref='maxDate' />
                </div>
                <div>
                  <Input type='number' label='Wortanzahl:' id='count' ref='count' defaultValue='70' min='0' />
                </div>
                <div>
                  <Input type='checkbox' label='Nur Wörter anzeigen:' id='onlyWords' ref='onlyWords' defaultChecked />
                </div>
                <div>
                  <Input type='checkbox' label='Listenansicht' id='activeMode' ref='activeMode' checked={!this.state.cloudActive} onChange={this.toggleChange} />
                </div>
              </Row>
            </div>
            <div className='center'>
              <Input type='submit' onClick={this.fetchData} value='Aktualisieren' />
            </div>
            <br />
            <Content>
              <D3
                render={this.state.cloudActive ? renderCloud : renderList}
                data={this.createWordArray(this.state.words)}
                style={{ width: '100%', height: '100%' }}
              >
                {this.state.cloudActive ? <svg /> : <table />}
              </D3>
              {/* <WordCloud
                data={this.createWordArray(this.state.words)}
                width={window.innerWidth / 100 * 80}
                // 190 = Filterbar + Downloadbar
                height={window.innerHeight - 190}
                padding={0.5}
                font={'Impact'}
                //    onWordClick = {()=>{console.log()}}
              /> */}
            </Content>
            {this.state.cloudActive
              ? (<i className='fas fa-file-image fa-3x export-button' style={{ cursor: 'pointer' }}
                onClick={this.exportSvg} />)
              : (<i className='fa fa-file-csv fa-3x export-button' style={{ cursor: 'pointer' }}
                onClick={this.exportCsv} />)}
          </Row>
        </Block>
      </React.Fragment>
    );
  }
}

export default TagCloud;
