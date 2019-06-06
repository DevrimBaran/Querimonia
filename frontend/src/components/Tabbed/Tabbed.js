import React, { Component } from 'react';
import './Tabbed.scss';

class Tabbed extends Component {
  constructor (props) {
    super(props);
    this.style = this.props.style || {};
    this.style["--tabCount"] = this.props.children ? this.props.children.length + 1 : 1;
    this.state = {
      index: 0
    };
  }
    handleClick = (i) => {
      this.setState({ index: i });
    }
    render () {
      return (
        <div className='tabbed' style={this.style}>
          {this.props.children && this.props.children.map((tab, i) => {
            return (
              [
                <h5 key={i} className={i === this.state.index ? 'tab active' : 'tab'} onClick={() => this.handleClick(i)}>{tab.props.label}</h5>,
                tab
              ]
            );
          })}
        </div>
      );
    }
}

export default Tabbed;
