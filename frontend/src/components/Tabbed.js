/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Content from './Content';

class Tabbed extends Component {
  constructor (props) {
    super(props);
    this.style = this.props.style || {};
    this.style['--tabCount'] = this.props.children ? this.props.children.length + 1 : 1;
    this.state = {
      index: 0
    };
  }
    handleClick = (i) => {
      this.setState({ index: i });
    }
    render () {
      const { className = '', style, ...passThrough } = { ...this.props };
      return (
        <div className={className + ' tabbed'} style={this.style} {...passThrough}>
          {this.props.children && this.props.children.map((tab, i) => {
            const { label, titleHeader, children, disabled, ...passThrough } = { ...tab.props };
            let className = 'tab';
            this.state.index === i && (className += ' active');
            disabled && (className += ' disabled');
            return (
              [
                <h5 key={'label' + i} title={titleHeader} className={className} onClick={disabled ? undefined : () => this.handleClick(i)}>{label}</h5>,
                <Content key={'content' + i}>
                  <div {...passThrough}>
                    {children}
                  </div>
                </Content>
              ]
            );
          })}
        </div>
      );
    }
}

export default Tabbed;
