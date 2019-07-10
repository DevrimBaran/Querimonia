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
import WordCloud from 'react-d3-cloud';

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
          <Row vertical>
            <h1 className='center'>Wortliste</h1>
            <br />
            <div>
              <Row vertical={false} style={{ justifyContent: 'space-around' }}>
                <div>
                  <label htmlFor='minDate'>Von:</label><br />
                  <input type='date' id='minDate' ref='minDate' />
                </div>
                <div>
                  <label htmlFor='maxDate'>Bis:</label><br />
                  <input type='date' id='maxDate' ref='maxDate' />
                </div>
                <div>
                  <label htmlFor='cloudActive'>Nur Wörter anzeigen:</label><br />
                  <input type='checkbox' id='onlyWords' ref='onlyWords' defaultChecked />
                </div>
                <div>
                  <label htmlFor='count'>Wortanzahl:</label><br />
                  <input type='number' id='count' ref='count' defaultValue='70' min='0' />
                </div>
              </Row>
            </div>
            <div className='center'>
              <input type='button' onClick={this.fetchData} value='Aktualisieren' />
            </div>
            <br />
            {this.state.cloudActive
              ? (<Content className='center' id='TagCloud'>
                <WordCloud
                  data={this.createWordArray(this.state.words)}
                  width={window.innerWidth / 100 * 80}
                  // 190 = Filterbar + Downloadbar
                  height={window.innerHeight - 190}
                  padding={0.5}
                  font={'Impact'}
                  //    onWordClick = {()=>{console.log()}}
                />
              </Content>)
              : (<Content className='center' id='OccurrenceList'>
                <ul>
                  <li><h4>Wort : Anzahl</h4></li>
                  {this.createWordArray(this.state.words).map((element) => {
                    return (<li>{element['text'] + ' : ' + (element['size'])}</li>);
                  })}
                </ul>
              </Content>)}
            <div>
              <div>
                <label htmlFor='cloudActive'>Listenansicht</label><br />
                <input type='checkbox' id='activeMode' ref='activeMode' checked={!this.state.cloudActive} onChange={this.toggleChange} />
              </div>
              <Row vertical={false} style={{ justifyContent: 'center' }}>
                <i className='fa fa-file-csv fa-3x export-button' style={{ cursor: 'pointer' }}
                  onClick={this.exportCsv} />
                <div style={{ width: '2em' }} />
                <i className='fas fa-file-image fa-3x export-button' style={{ cursor: 'pointer' }}
                  onClick={this.exportSvg} />
              </Row>
            </div>
            <br />
          </Row>
        </Block>
      </React.Fragment>
    );
  }
}

export default TagCloud;
