/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Textarea from './Textarea';
import CodeMirror from './CodeMirror';
class Input extends Component {
  constructor (props) {
    super(props);
    this.state = { conditional: null };
  }
    conditionalChange = (e) => {
      console.log('conditionalChange');
      this.setState({ conditional: e.target.value });
      this.props.onChange && this.props.onChange({
        target: e.target,
        name: e.target.name,
        value: e.target.value
      });
    }
    onChange = (e) => {
      console.log('onChange');
      if (!e.target) {
        this.props.onChange && this.props.onChange({
          value: e
        });
        return;
      }
      let value = e.target.value;
      if (e.target.multiple) {
        switch (e.target.type) {
          case 'text': {
            value = value.split(',');
            break;
          }
          case 'select-multiple': {
            value = [].filter.call(e.target.options, o => o.selected).map(o => o.value);
            break;
          }
          default: {
            value = e.target.value;
            break;
          }
        }
      } else {
        console.log('not multiple');
      }
      this.props.onChange && this.props.onChange({
        target: e.target,
        name: e.target.name,
        value: value
      });
    }
    render () {
      const classes = '';
      const { className, onChange, type, label, values, value, name, ...passThroughProps } = this.props;

      let injectedProp = {
        className: className ? className + ' ' + classes : classes,
        type: type
      };

      let input;
      switch (this.props.type) {
        case 'select': {
          input = (<select value={value} name={name} onChange={this.onChange} {...injectedProp} {...passThroughProps}>
            {this.props.required || <option key='null' value=''>-</option>}
            {
              values && values.map((value, i) => {
                return (
                  <option key={value.value + i} value={value.value}>{value.label}</option>
                );
              })
            }
          </select>);
          break;
        }
        case 'conditional': {
          input = (
            <React.Fragment>
              <select value={value} name={name} onChange={this.conditionalChange} {...injectedProp} {...passThroughProps}>
                {this.props.required || <option key='null' value=''>-</option>}
                {
                  values && Object.keys(values).map((key) => {
                    return (
                      <option key={key} value={key}>{key}</option>
                    );
                  })
                }
              </select>
              (<label htmlFor={passThroughProps.name2}>{passThroughProps.label2}</label>)
              <select value={passThroughProps.value2} name={passThroughProps.name2} onChange={this.onChange} {...injectedProp} {...passThroughProps}>
                {this.props.required || <option key='null' value=''>-</option>}
                {
                  values && values[this.state.conditional] && values[this.state.conditional].map((key) => {
                    return (
                      <option key={key} value={key}>{key}</option>
                    );
                  })
                }
              </select>
            </React.Fragment>
          );
          break;
        }
        case 'textarea': {
          input = <Textarea value={value} name={name} onChange={this.onChange} {...injectedProp} {...passThroughProps} />;
          break;
        }
        case 'codemirror': {
          input = <CodeMirror value={value} name={name} onChange={this.onChange} {...injectedProp} {...passThroughProps} />;
          break;
        }
        default: {
          input = <input value={value} name={name} onChange={this.onChange} {...injectedProp} {...passThroughProps} />;
          break;
        }
      }
      return (
        <React.Fragment>
          { label && (<label htmlFor={this.props.name}>{label}</label>) }
          { input }
        </React.Fragment>
      );
    }
}

export default Input;
