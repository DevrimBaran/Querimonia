import React, { Component } from 'react';
import Api from '../utility/Api.js';

class Log extends Component {
  constructor (props) {
    super(props);
    this.state = {
      log: []
    };
  }

  setData = () => {
    Api.get(`/api/complaints/${this.props.complaintId}/log`, '')
      .then((data) => {
        this.setState({
          log: data
        });
      });
  };

  printLog = (log) => {
    return log.map((key, i) => {
      return <div key={i}>
        <div>{`Kategorie: ${key.category}`}</div>
        <div>{`Nachricht: ${key.message}`}</div>
        <div>{`Datum: ${key.date} ${key.time}`}</div>
        -----------------
      </div>;
    });
  };

  componentDidMount () {
    this.setData();
  }

  render () {
    return <div>
      { this.printLog(this.state.log) }
    </div>;
  }
}

export default Log;
