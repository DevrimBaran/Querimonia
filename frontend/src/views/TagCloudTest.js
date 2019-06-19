import React, { Component } from 'react';
// import randomColor from 'randomcolor';
// import TagCloud from 'react-tag-cloud';
import Block from 'components/Block/Block';
import Row from 'components/Row/Row';
import Content from 'components/Content/Content';
import cloud from 'd3-cloud';

import Api from 'utility/Api';

class TagCloudTest extends Component {
  constructor (props) {
    super(props);
    this.state = {
      word: [],
      words: {},
      color: '#179c7d',
      size: 200,
      max: 0
    };
  }
  getCSV = () => {
    // sort by value
    function Comparator (a, b) {
      if (a[1] < b[1]) return 1;
      if (a[1] > b[1]) return -1;
      return 0;
    }

    var w = this.state.words;
    var ar = [['Token', 'Anzahl']];
    for (var i = 0; i < Object.keys(w).length; i++) {
      ar.push([Object.keys(w)[i], Object.values(w)[i]]);
    }
    exportToCsv('tagcloudCSV.csv', ar.sort(Comparator));
  }
  fetchData = () => {
    let query = {};
    this.refs.minDate.value && (query.date_min = this.refs.minDate.value);
    this.refs.maxDate.value && (query.date_max = this.refs.maxDate.value);
    this.refs.onlyWords.checked && (query.words_only = this.refs.onlyWords.checked);
    this.refs.count.value && this.refs.count.value > 0 && (query.count = this.refs.count.value);
    Api.get('/api/stats/tagcloud', query)
      .then(a => { console.log(a); return a; })
      .then(this.setData);
  }
  setData = (data) => {
    var maxVal = Math.max.apply(Math, Object.values(data));
    this.setState({ words: data });
    this.setState({ max: maxVal });
  }
  onChange = (e) => {
    let s = {};
    s[e.target.id] = e.target.value;
    this.setState(s);
  }
  componentDidMount () {
    this.fetchData();

    for (let key in this.state.words) {
      this.state.word.push({
        text: this.state.words[key],
        size: 10 + Math.random() * 90
      });
    }

    this.state.word.forEach((e) => { console.log(e); });
    console.log(this.state.words);

    cloud().size([960, 500])
      .canvas(() => { return this.refs.canvas; })
      .words(this.state.word)
      .padding(5)
      .font('Impact')
      .fontSize((d) => { return d.size; })
      .start();
  }
  renderWord = (word, index) => {
    return (<abbr key={index} title={this.state.words[word]} style={{ fontSize: (this.state.words[word] / this.state.max) * this.state.size }}>{word}</abbr>);
  }
  render () {
    return (
      <React.Fragment>
        <Block>
          <Row vertical>
            <h6 className='center'>Wortliste</h6>
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
                  <label htmlFor='onlyWords'>Nur Wörter anzeigen:</label><br />
                  <input type='checkbox' id='onlyWords' ref='onlyWords' />
                </div>
                <div>
                  <label htmlFor='count'>Wortanzahl:</label><br />
                  <input type='number' id='count' ref='count' defaultValue='0' min='0' />
                </div>
              </Row>
            </div>
            <div className='center'>
              <input type='button' onClick={this.fetchData} value='aktualisieren' />
            </div>
            <Content>

              <canvas ref='canvas' id='TagCloudChart' />

            </Content>
            <div>
              <Row vertical={false} style={{ justifyContent: 'center' }}>
                <div>
                  <label htmlFor='color'>Farbe:</label><br />
                  <input type='color' id='color' onChange={this.onChange} value={this.state.color} />
                </div>
                <div style={{ width: '2em' }} />
                <div>
                  <label htmlFor='factor'>Schriftgröße:</label><br />
                  <input type='number' id='size' onChange={this.onChange} value={this.state.size} />
                </div>
                <div style={{ width: '2em' }} />
                <div className='fa fa-file-csv fa-3x export-button' onClick={this.getCSV} />
              </Row>
            </div>
            <br />
          </Row>
        </Block>
      </React.Fragment>
    );
  }
}

/**
 * Opens a dialog to download the CSV
 * @param filename name of the file
 * @param rows Array (Rows) of Arrays (Columns) include the data of the CSV
 */
function exportToCsv (filename, rows) {
  var processRow = function (row) {
    var finalVal = '';
    for (var j = 0; j < row.length; j++) {
      var result = row[j].toString();
      if (j > 0) { finalVal += ';'; }
      finalVal += result;
    }
    return finalVal + '\r\n';
  };

  var csvFile = '';
  for (var i = 0; i < rows.length; i++) {
    csvFile += processRow(rows[i]);
  }

  var blob = new Blob([csvFile], { type: 'text/csv;charset=utf-8;' });
  if (navigator.msSaveBlob) { // IE 10+
    navigator.msSaveBlob(blob, filename);
  } else {
    var link = document.createElement('a');
    if (link.download !== undefined) { // feature detection
      // Browsers that support HTML5 download attribute
      var url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', filename);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
}

// ReactDOM.render(<TagCloudTest />, document.getElementById('root'));
export default TagCloudTest;
