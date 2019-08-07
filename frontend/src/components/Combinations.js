import React, { Component } from 'react';
import Api from '../utility/Api.js';

class Combinations extends Component {
  constructor (props) {
    super(props);
    this.state = {
      combinations: []
    };
  }

  setData = () => {
    Api.get(`/api/combinations/${this.props.complaintId}`, '')
      .then((data) => {
        this.setState({
          combinations: data
        });
      });
  };

  componentDidMount () {
    this.setData();
  }

  printCombinations = (combinations) => {
    return combinations.map((combination) => {
      console.error(combination);
      return <div>
        {
          Object.keys(combination).map((key) => {
            return `${key}: ${combination.key}`;
          })
        }
      </div>;
    });
  };

  render () {
    return <div>
      { this.printCombinations(this.state.combinations) }
    </div>;
  }
}

export default Combinations;
