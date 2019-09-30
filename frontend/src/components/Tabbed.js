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
    this.state = {
      index: props.active || 0
    };
  }
    handleClick = (i) => {
      this.setState({ index: i });
    }
    render () {
      const { className = '', style = {}, active, ...passThrough } = { ...this.props };
      const tabs = (this.props.children && this.props.children.filter && this.props.children.filter(t => t)) || [];
      return (
        <div className={className + ' tabbed'} style={{ ...style, '--tabCount': tabs.length + 1 }} {...passThrough}>
          {tabs && tabs.map((tab, i) => {
            const { label, titleHeader, children = [], disabled, ...passThrough } = { ...tab.props };
            let className = 'tab';
            this.state.index === i && (className += ' active');
            disabled && (className += ' disabled');
            return (
              [
                <h5 key={'label' + i} title={titleHeader} className={className} onClick={disabled ? undefined : () => this.handleClick(i)}>{label}</h5>,
                <Content key={'content' + i} {...passThrough}>
                  {children}
                </Content>
              ]
            );
          })}
        </div>
      );
    }
}

export default Tabbed;
