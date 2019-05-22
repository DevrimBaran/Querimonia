import React, { Component } from 'react'

import './Select.scss';
class Select extends Component {
    constructor(props) {
        super(props);
        this.state = {
            value: this.props.value
        }
    }
    onChange = (e) => {
        this.props.onChange && this.props.onChange(e);
    }
    render() {
        return (
            <select className="Select" name={this.props.name} onChange={this.onChange}>
                {
                    this.props.values.map((value) => {
                        return (
                            <option key={value} value={value}>{value}</option>
                        )
                    })
                }
            </select>
        );
    }
}

export default Select;