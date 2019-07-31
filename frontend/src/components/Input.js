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
    onChange = (e) => {
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
      const { className, onChange, type, label, values, value, ...passThroughProps } = this.props;

      let injectedProp = {
        className: className ? className + ' ' + classes : classes,
        type: type,
        onChange: this.onChange
      };

      let input;
      switch (this.props.type) {
        case 'select': {
          input = (<select value={value} {...injectedProp} {...passThroughProps}>
            {this.props.required || <option key='null' value=''>-</option>}
            {
              values && values.map((value) => {
                return (
                  <option key={value.value} value={value.value}>{value.label}</option>
                );
              })
            }
          </select>);
          break;
        }
        case 'textarea': {
          input = <Textarea value={value} {...injectedProp} {...passThroughProps} />;
          break;
        }
        case 'codemirror': {
          input = <CodeMirror value={value} {...injectedProp} {...passThroughProps} />;
          break;
        }
        default: {
          input = <input value={value} {...injectedProp} {...passThroughProps} />;
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
