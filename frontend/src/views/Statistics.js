/**
 * This class creates the Statistics view.
 *
 * @version <0.1>
 */

import Block from '../components/Block';
import Content from '../components/Content';
import Row from '../components/Row';
import React, { Component } from 'react';
import Api from '../utility/Api';
import Tabbed from '../components/Tabbed';
import Input from '../components/Input';
import StatsTable from '../components/StatsTable';
import StatsDiagram from '../components/StatsDiagram';
import Table from '../components/Table';

class Statistics extends Component {
  constructor (props) {
    super(props);
    this.container = React.createRef();
    this.setState({
      data: null
    });
  }
  activateComplaint = () => {
    let requests = [];
    requests.push(Api.get('/api/stats/categoriesStats', {})
      .catch(() => {
        return { status: 404 };
      }));
    requests.push(Api.get('/api/stats/entitiesStats', {})
      .catch(() => {
        return { status: 404 };
      }));
    requests.push(Api.get('/api/stats/rulesStats', {})
      .catch(() => {
        return { status: 404 };
      }));
    requests.push(Api.get('/api/stats/monthsStats', {})
      .catch(() => {
        return { status: 404 };
      }));
    Promise.all(requests).then((values) => {
      this.setState({
        data: values
      });
    });
  };

  getComplaintStatsData = (data2) => {
    let datas = [];

    let statVar = 'count';
    let data = Object.values(data2).map((d, i) => { return d[statVar]; });
    let dataKeys = Object.keys(data2);
    datas.push({ key: dataKeys, value: data, header: ['Kategorie', 'Anzahl'] });

    statVar = 'tendency';
    data = Object.values(data2).map((d, i) => { return d[statVar]; });
    dataKeys = Object.keys(data2);
    datas.push({ key: dataKeys, value: data, header: ['Kategorie', 'Sentiment'] });

    let colors = { Ekel: 'brown', Freude: 'green', Furcht: 'black', Trauer: 'pink', Ueberraschung: 'steelblue', Verachtung: 'orange', Wut: 'red', Unbekannt: 'gray' };
    statVar = 'emotion';
    let sum = 0;
    data = [];
    dataKeys = [];
    Object.values(data2).forEach((d, i) => { sum = 0; Object.values(d[statVar]).forEach((e, j) => { e = e * 100; let key = Object.keys(data2)[i]; let key2 = Object.keys(d[statVar])[j]; dataKeys.push(key); sum += e; data.push([e, sum, colors[key2], key2, key]); }); });
    datas.push({ key: dataKeys, value: data, colors: colors });

    colors = { ERROR: 'red', ANALYSING: 'orange', NEW: 'green', IN_PROGRESS: 'steelblue', CLOSED: 'grey' };
    statVar = 'status';
    data = [];
    dataKeys = [];
    Object.values(data2).forEach((d, i) => { sum = 0; Object.values(d[statVar]).forEach((e, j) => { e = e * 100; let key = Object.keys(data2)[i]; let key2 = Object.keys(d[statVar])[j]; dataKeys.push(key); sum += e; data.push([e, sum, colors[key2], key2, key]); }); });
    datas.push({ key: dataKeys, value: data, colors: colors });

    return datas;
  };

  getEntitiesStatsData = (data2) => {
    let datas = [];

    Object.keys(data2).forEach((k, i) => {
      let data = Object.values(data2[k]);
      let dataKeys = Object.keys(data2[k]);
      datas.push({ key: dataKeys, value: data, header: ['Wert', 'Anzahl'] });
    });
    return datas;
  };

  getRulesStatsData = (data2) => {
    let datas = [];

    let data = Object.values(data2);
    let dataKeys = Object.keys(data2);
    datas.push({ key: dataKeys, value: data, header: ['ID', 'Anzahl'] });

    return datas;
  };

  getMonthsStatsData = (data2) => {
    let datas = [];

    let statVar = 'count';
    let data = Object.values(data2).map((d, i) => { return d[statVar]; });
    let dataKeys = Object.keys(data2);
    datas.push({ key: dataKeys, value: data, header: ['Monat', 'Anzahl'] });

    let colors = { ERROR: 'red', ANALYSING: 'orange', NEW: 'green', IN_PROGRESS: 'steelblue', CLOSED: 'grey' };
    statVar = 'status';
    let sum = 0;
    data = [];
    dataKeys = [];
    Object.values(data2).forEach((d, i) => { sum = 0; Object.values(d[statVar]).forEach((e, j) => { e = e * 100; let key = Object.keys(data2)[i]; let key2 = Object.keys(d[statVar])[j]; dataKeys.push(key); sum += e; data.push([e, sum, colors[key2], key2, key]); }); });
    datas.push({ key: dataKeys, value: data, colors: colors });

    statVar = 'processingDuration';
    data = Object.values(data2).map((d, i) => { return d[statVar]; });
    dataKeys = Object.keys(data2);
    datas.push({ key: dataKeys, value: data, header: ['Monat', 'Bearbeitungszeit'] });

    return datas;
  };

  editCountCategories = () => {
    let query = {};
    let count = document.getElementById('countCategories').value;
    query['count'] = (count < 0 ? 0 : count);
    Api.get('/api/stats/categoriesStats', query)
      .then(data => {
        let oldData = this.state.data;
        oldData[0] = data;
        this.setState({
          data: oldData
        });
      });
  }
  editCountEntities = () => {
    let query = {};
    let count = document.getElementById('countEntities').value;
    query['count'] = (count < 0 ? 0 : count);
    Api.get('/api/stats/entitiesStats', query)
      .then(data => {
        let oldData = this.state.data;
        oldData[1] = data;
        this.setState({
          data: oldData
        });
      });
  }
  editCountRules = () => {
    let query = {};
    let count = document.getElementById('countRules').value;
    query['count'] = (count < 0 ? 0 : count);
    Api.get('/api/stats/rulesStats', query)
      .then(data => {
        let oldData = this.state.data;
        oldData[2] = data;
        this.setState({
          data: oldData
        });
      });
  }
  editDateMonths = () => {
    let query = {};
    query['date_min'] = document.getElementById('startDateMonths').value;
    query['date_max'] = document.getElementById('endDateMonths').value;

    Api.get('/api/stats/monthsStats', query)
      .then(data => {
        let oldData = this.state.data;
        oldData[3] = data;
        this.setState({
          data: oldData
        });
      });
  }

  render () {
    if (!(this.state && this.state.data)) this.activateComplaint();
    if (this.state && this.state.data) {
      let data = this.state.data;

      let catArray = [];
      let monArray = [];
      let entArray = [];
      let rulArray = [];
      let categoriesData = [];
      let entitiesData = [];
      let rulesData = [];
      let monthsData = [];
      if (data[0] && !data[0].exception) {
        catArray = ['Anzahl', 'Sentiment', 'Emotion', 'Status'];
        categoriesData = this.getComplaintStatsData(data[0]);
      }
      if (data[1] && !data[1].exception) {
        entArray = Object.keys(data[1]);
        entitiesData = this.getEntitiesStatsData(data[1]);
      }
      if (data[2] && !data[2].exception) {
        rulArray = ['Anzahl'];
        rulesData = this.getRulesStatsData(data[2]);
      }
      if (data[3] && !data[3].exception) {
        monArray = ['Anzahl', 'Status', 'Durchschnittliche Bearbeitungszeit'];
        monthsData = this.getMonthsStatsData(data[3]);
      }

      return (
        <React.Fragment>
          <Block>
            <Row vertical>
              <h1 className='center'>Statistiken</h1>
              <Content>
                <Tabbed vertical>
                  <div label='Kategorien'>
                    <Table className='stats-table'>
                      <tbody>
                        <tr>
                          <td style={{ width: '50%', borderBottom: '5px solid grey' }}><Input type='select' label='Option' values={catArray} onChange={(e) => showMe(e, catArray, 1)} /></td>
                          <td style={{ width: '50%', borderBottom: '5px solid grey' }}><Input type='number' min={0} id='countCategories' label='Anzahl' onChange={(e) => this.editCountCategories()} /></td>
                        </tr>
                        <tr>
                          <td>
                            {categoriesData.map((k, i) => <StatsTable id={catArray[i] + '1_t'} style={{ display: 'none' }} data={k} />)}
                          </td>
                          <td>
                            {categoriesData.map((k, i) => <StatsDiagram id={catArray[i] + '1_d'} style={{ display: 'none' }} data={k} />)}
                          </td>
                        </tr>
                      </tbody>
                    </Table>
                  </div>
                  <div label='Entitäten'>
                    <Table className='stats-table'>
                      <tbody>
                        <tr>
                          <td style={{ width: '50%', borderBottom: '5px solid grey' }}><Input type='select' label='Label' values={entArray} onChange={(e) => showMe(e, entArray, 2)} /></td>
                          <td style={{ width: '50%', borderBottom: '5px solid grey' }}><Input type='number' min={0} id='countEntities' label='Anzahl' onChange={(e) => this.editCountEntities()} /></td>
                        </tr>
                        <tr>
                          <td>
                            {Object.keys(this.state.data[1]).map((k, i) => <StatsTable id={entArray[i] + '2_t'} style={{ display: 'none' }} data={entitiesData[i]} />)}
                          </td>
                          <td>
                            {Object.keys(this.state.data[1]).map((k, i) => <StatsDiagram id={entArray[i] + '2_d'} style={{ display: 'none' }} data={entitiesData[i]} />)}
                          </td>
                        </tr>
                      </tbody>
                    </Table>
                  </div>
                  <div label='Regeln'>
                    <Table className='stats-table'>
                      <tbody>
                        <tr>
                          <td style={{ width: '50%', borderBottom: '5px solid grey' }}><Input type='select' label='Option' values={rulArray} onChange={(e) => showMe(e, rulArray, 3)} /></td>
                          <td style={{ width: '50%', borderBottom: '5px solid grey' }}><Input type='number' min={0} id='countRules' label='Anzahl' onChange={(e) => this.editCountRules()} /></td>
                        </tr>
                        <tr>
                          <td>
                            {rulesData.map((k, i) => <StatsTable id={rulArray[i] + '3_t'} style={{ display: 'none' }} data={k} />)}
                          </td>
                          <td>
                            {rulesData.map((k, i) => <StatsDiagram id={rulArray[i] + '3_d'} style={{ display: 'none' }} data={k} />)}
                          </td>
                        </tr>
                      </tbody>
                    </Table>
                  </div>
                  <div label='Monate'>
                    <Table className='stats-table'>
                      <tbody>
                        <tr>
                          <td style={{ width: '50%', borderBottom: '5px solid grey' }}><Input type='select' label='Option' values={monArray} onChange={(e) => showMe(e, monArray, 4)} /></td>
                          <td style={{ width: '50%', borderBottom: '5px solid grey' }}><Input type='date' id='startDateMonths' label='Start-Datum' onChange={(e) => this.editDateMonths()} /><Input type='date' id='endDateMonths' label='End-Datum' onChange={(e) => this.editDateMonths()} /></td>
                        </tr>
                        <tr>
                          <td>
                            {monthsData.map((k, i) => <StatsTable id={monArray[i] + '4_t'} style={{ display: 'none' }} data={k} />)}
                          </td>
                          <td>
                            {monthsData.map((k, i) => <StatsDiagram id={monArray[i] + '4_d'} style={{ display: 'none' }} data={k} />)}
                          </td>
                        </tr>
                      </tbody>
                    </Table>
                  </div>
                </Tabbed>
              </Content>
            </Row>
          </Block>
        </React.Fragment>
      );
    } else {
      return (
        <React.Fragment>
          <Block>
            <Row vertical>
              <h1 className='center'>Statistiken</h1>
              <Content>
                <Tabbed vertical>
                  <div label='Kategorien' />
                  <div label='Entitäten' />
                  <div label='Regeln' />
                  <div label='Monate' />
                </Tabbed>
              </Content>
            </Row>
          </Block>
        </React.Fragment>
      );
    }
  }
}

function showMe (e, idAr, id) {
  let val = e.value;
  var vis = 'none';
  for (var i = 0; i < idAr.length; i++) {
    vis = 'none';
    if (idAr[i] === val) {
      vis = 'block';
    }
    document.getElementById(idAr[i] + id + '_t').style.display = vis;
    document.getElementById(idAr[i] + id + '_d').style.display = vis;
  }
}

export default Statistics;
