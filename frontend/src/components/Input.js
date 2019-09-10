/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import Textarea from './Textarea';
import FileInput from './FileInput';
import ColorPicker from './ColorPicker';
import CodeMirror from './CodeMirror';
class Input extends Component {
  constructor (props) {
    super(props);
    this.target = React.createRef();
    this.state = { conditional: null };
  }
    onChange = (e, fake) => {
      if (!e.target) {
        this.props.onChange && this.props.onChange({
          value: e
        });
        return;
      }
      let value = e.value || e.target.value;
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
      }
      if (e.target.type === 'checkbox') {
        value = e.target.checked;
      }
      this.props.onChange && this.props.onChange({
        target: e.target,
        name: e.target.name,
        value: value,
        fake: fake
      });
    }
    componentDidMount = () => {
      // TODO
      if (this.target.current.nodeName === 'SELECT') {
        this.onChange({
          target: this.target.current,
          name: this.props.name,
          value: null
        }, true);
      }
    }
    render () {
      const classes = '';
      const { className, onChange, type, label, values, value, checked, name, inline, id = this.props.name, ...passThroughProps } = this.props;

      let injectedProp = {
        className: className ? className + ' ' + classes : classes,
        type: type
      };

      let input;
      switch (this.props.type) {
        case 'select': {
          input = (<select value={value} name={name} onChange={this.onChange} ref={this.target} {...injectedProp} {...passThroughProps}>
            {this.props.required || <option key='null' value=''>-</option>}
            {
              values && values.map((data, i) => {
                const { value, label, ...passThrough } = (typeof data === 'object') ? { ...data } : { value: data, label: data };
                return (
                  <option key={i} value={value} {...passThrough}>{label}</option>
                );
              })
            }
          </select>);
          break;
        }
        case 'textarea': {
          input = <Textarea value={value} id={id} name={name} ref={this.target} onChange={this.onChange} {...injectedProp} {...passThroughProps} />;
          break;
        }
        case 'colorpicker': {
          input = <ColorPicker value={value} id={id} name={name} ref={this.target} onChange={this.onChange} {...injectedProp} {...passThroughProps} />;
          break;
        }
        case 'codemirror': {
          input = <CodeMirror value={value} id={id} name={name} ref={this.target} onChange={this.onChange} {...injectedProp} {...passThroughProps} />;
          break;
        }
        case 'file': {
          input = <FileInput value={value} id={id} name={name} ref={this.target} onChange={this.onChange} {...injectedProp} {...passThroughProps} />;
          break;
        }
        case 'checkbox': {
          input = <input value={value} checked={value} id={id} name={name} ref={this.target} onChange={this.onChange} {...injectedProp} {...passThroughProps} />;
          break;
        }
        default: {
          input = <input value={value} id={id} name={name} ref={this.target} onChange={this.onChange} {...injectedProp} {...passThroughProps} />;
          break;
        }
      }
      return (
        <div className={inline ? 'input inline' : 'input'} style={type === 'hidden' ? { display: 'none' } : {}}>
          { label && (<label htmlFor={id}>{label}</label>) }
          { input }
        </div>
      );
    }
}

export default Input;
