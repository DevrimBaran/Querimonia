import React, { Component } from 'react';

import DeepObject from '../../components/DeepObject';

export default class extends Component {
  constructor (props) {
    super();
    this.template = {
      type: 'object',
      label: 'Test',
      children: {
        foo: {
          type: 'text',
          label: 'Foo'
        },
        faa: {
          type: 'number',
          label: 'Faa'
        },
        bar: {
          type: 'array',
          label: 'Bar',
          children: {
            type: 'number',
            label: 'Bars'
          }
        },
        temp: {
          type: 'array',
          label: 'Temp',
          children: {
            type: 'object',
            label: '#$i',
            children: {
              a: {
                type: 'number',
                label: 'A'
              },
              b: {
                type: 'number',
                label: 'B'
              }
            }
          }
        }
      }
    };
    this.state = {
      foo: 'foo',
      faa: 0,
      bar: [1, 2, 3],
      temp: [
        {
          a: 0,
          b: 0
        }
      ]
    };
  }
  save = (data) => {
    console.log(data);
    this.setState(data);
  }
  render () {
    return (
      <DeepObject data={this.state} template={this.template} save={this.save} />
    );
  }
}
