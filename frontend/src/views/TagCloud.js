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
import Form from './../components/Form';

import { Color } from '../utility/colors';

var cloud = require('../assets/js/d3.layout.cloud');
class TagCloud extends Component {
  constructor (props) {
    super(props);
    this.state = {
      redraw: 0,
      words: {},
      minOccurrence: 0,
      maxOccurrence: 0,
      lemmatizedWords: {},
      lemmatizedMinOccurrence: 0,
      lemmatizedMaxOccurrence: 0,
      color: '#000000',
      cloudActive: true,
      lemmatize: true,
      query: {
        date_min: undefined,
        date_max: undefined,
        count: 0,
        emotion: undefined,
        subject: undefined,
        words_only: true
      }
    };
  }
  renderCloud = (target, data, d3, loaded) => {
    const padding = [0, 0];
    const hsl = new Color(data.color).hsl();
    if (hsl.l === 100) { // white
      hsl.l = 50;
      hsl.s = 70;
      hsl.random = () => {
        hsl.h = Math.random() * 360;
        return hsl.css();
      };
    } else if (hsl.s === 0) { // grey
      hsl.random = () => {
        hsl.l = Math.random() * 60 + 30;
        return hsl.css();
      };
    } else { // color
      hsl.l = 50;
      hsl.random = () => {
        // hsl.l = Math.random() * 0.6 + 0.3;
        hsl.s = Math.random() * 80 + 20;
        return hsl.css();
      };
    }
    let draw = (words) => {
      loaded();
      let text = d3.select(target).append('svg')
        .attr('width', layout.size()[0])
        .attr('height', layout.size()[1])
        .style('margin', 'auto')
        .style('display', 'block')
        .append('g')
        .attr('transform', 'translate(' + layout.size()[0] / 2 + ',' + layout.size()[1] / 2 + ')')
        .selectAll('text')
        .data(words)
        .enter().append('text');

      text
        .style('font-family', 'Impact')
        .style('font-weight', 'normal')
        .style('font-size', function (d) {
          return d.size + 'px';
        })
        .attr('fill', d => {
          return hsl.random();
        })
        .attr('text-anchor', 'middle')
        .attr('transform', function (d) {
          return 'translate(' + [d.x, d.y] + ')rotate(' + d.rotate + ')';
        })
        .text(function (d) {
          return d.text;
        });

      text
        .append('title')
        .text(d => d.text + ' - ' + d.value + 'mal');
    };

    let layout = cloud()
      .size([target.clientWidth - padding[0] * 2, (target.clientHeight || 1) - (padding[1]) * 2])
      .words(data.words)
      .spiral('rectangular')
      .padding(1)
      .rotate(function () {
        return 0;
      })
      .font('Impact')
      .fontWeight('normal')
      .fontSize(function (d) {
        return d.size;
      })
      .on('end', draw);

    layout.timeInterval(10);
    layout.start();

    return true;
  };
  renderList = (target, data, d3) => {
    let row = d3.select(target).append('table')
      .selectAll('tr')
      .data(data.words)
      .enter()
      .append('tr');
    row.append('td')
      .text(d => d.text);
    row.append('td')
      .text(d => d.value);
  };

    onChange = (e) => {
      console.log(e.name, e.value);
      this.setState({ [e.name]: e.value });
    }

    changeQuery = (e) => {
      this.setState(state => ({ query: { ...state.query, [e.name]: e.value } }));
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

    createWordArray = () => {
      const wordsObject = this.state.lemmatize ? this.state.lemmatizedWords : this.state.words;
      let wordArray = [];
      Object.keys(wordsObject).forEach((element) => {
        wordArray.push({
          text: element,
          value: wordsObject[element],
          size: this.calculateSize(wordsObject[element])
        });
      });
      return wordArray;
    };

    calculateSize = (size) => {
      const tmax = this.state.lemmatize ? this.state.lemmatizedMaxOccurrence : this.state.maxOccurrence; // Höchste Anzahl
      const tmin = this.state.lemmatize ? this.state.lemmatizedMinOccurrence : this.state.minOccurrence; // mindest Anzahl an Wörter
      const fmax = 130; // maximale Schriftgröße
      const fmin = 20; // minimale Schriftgröße
      return Math.max(fmax * ((size - tmin) / (tmax - tmin)), fmin);
    };

    fetchData = () => {
      this.setState(state => ({ redraw: state.redraw + 1 }));
      Api.get('/api/stats/tagcloud', this.state.query)
        .then(this.lemmatizeData);
    };

    lemmatizeData = (data) => {
      if (data) {
        fetch('https://querimonia.iao.fraunhofer.de/python/lemmatize', {
          method: 'post',
          mode: 'cors',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic YWRtaW46UXVlcmltb25pYVBhc3MyMDE5'
          },
          body: JSON.stringify({ text: Object.keys(data).join(' ') })
        })
          .then(response => response.json())
          .then((lemma) => this.setData(data, lemma));
      }
    }

    setData = (data, lemma) => {
      if (data) {
        const max = Math.max(...Object.values(data));
        const min = Math.min(...Object.values(data));
        // TODO: Calculate other constants
        this.setState({ words: data, maxOccurrence: max, minOccurence: min });
      };
      if (lemma) {
        const lemmatizedWords = {};
        for (let word in lemma) {
          lemmatizedWords[lemma[word]] || (lemmatizedWords[lemma[word]] = 0);
          lemmatizedWords[lemma[word]] += data[word];
        }
        const max = Math.max(...Object.values(lemmatizedWords));
        const min = Math.min(...Object.values(lemmatizedWords));
        this.setState({ lemmatizedWords: lemmatizedWords, lemmatizedMaxOccurrence: max, lemmatizedMinOccurence: min });
      };
    };

    onResize = () => {
      this.setState(this.state);
    };

    componentDidMount () {
      this.fetchData();
      // window.addEventListener('resize', this.onResize);
    }

    componentWillUnmount () {
      // window.removeEventListener('resize', this.onResize);
    }

    changeColor = (e) => {
      this.setState({ color: e.value });
    }

    render () {
      return (
        <React.Fragment>
          <Block>
            <Row vertical>
              <h1 className='center'>Worthäufigkeiten</h1>
              <Form>
                <Input type='date' label='Eingangsdatum (von)' name='date_min' onChange={this.changeQuery} value={this.state.query.date_min} />
                <Input type='date' label='Eingangsdatum (bis)' name='date_max' onChange={this.changeQuery} value={this.state.query.date_max} />
                <Input type='number' label='Wortanzahl' name='count' onChange={this.changeQuery} value={this.state.query.count} />
                <Input type='select' label='Emotion' multiple name='emotion' onChange={this.changeQuery} value={this.state.query.emotion} />
                <Input type='select' label='Kategorie' multiple name='subject' onChange={this.changeQuery} value={this.state.query.subject} />
                <Input type='checkbox' label='Nur Wörter' name='words_only' onChange={this.changeQuery} value={this.state.query.words_only} />
                <Input type='checkbox' label='Lemmatize' name='lemmatize' onChange={this.onChange} value={this.state.lemmatize} />
                <Input type='colorpicker' label='Farbton' onChange={this.changeColor} value={this.state.color} />
                <Input type='checkbox' className='listenCheckbox' label='Listenansicht' name='cloudActive' onChange={this.onChange} value={this.state.cloudActive} />
                {this.state.cloudActive
                  ? (<i className='fas fa-file-csv fa-3x export-button' title='Download CSV'
                    style={{ cursor: 'pointer' }}
                    onClick={this.exportSvg} />)
                  : (<i className='fa fa-file-csv fa-2x export-button' style={{ cursor: 'pointer' }}
                    onClick={this.exportCsv} />)}
              </Form>
              <Input type='submit' onClick={this.fetchData} value='Aktualisieren' />
              <Content style={{ width: '100%', height: '100%', padding: '20px' }}>
                <D3
                  render={this.state.cloudActive ? this.renderCloud : this.renderList}
                  data={{ color: this.state.color, words: this.createWordArray(), redraw: this.state.redraw }}
                  style={{ height: '100%' }}
                />
              </Content>
            </Row>
          </Block>
        </React.Fragment>
      );
    }
}

export default TagCloud;
