import React, { Component } from 'react'

//import Api from '../../utility/Api';

//import Block from '../Block/Block';
//import Body from '../Body/Body';
//import Collapsible from '../Collapsible/Collapsible';
//import Fa from '../Fa/Fa';
//import Filter from '../Filter/Filter';
//import Issues from '../Issues/Issues';
//import Log from '../Log/Log.js';
//import Modal from '../Modal/Modal';
//import Stats from '../Stats/Stats';
//import Tabbed from '../Tabbed/Tabbed';
//import Table from '../Table/Table';
//import TaggedText from '../TaggedText/TaggedText';
//import Text from '../Text/Text';
//import TextBuilder from '../TextBuilder/TextBuilder';
//import Topbar from '../Topbar/Topbar';

import './Table.scss';
class Table extends Component {
    constructor(props) {
        super(props);

        this.state = {}
    }
    map = (row, cb) => {
        var index = -1;
        if (!this.props.tags) return [];
        return this.props.tags.map((tag) => {
            let temp;
            index++;
            if (cb) {
                if (Array.isArray(tag)) {
                    temp = row[tag[0]];
                    for (let i = 1; i < tag.length; i++) {
                        temp = temp[tag[i]];
                    }
                } else {
                    temp = row[tag];
                }
                return cb(temp, index);
            } else if (Array.isArray(tag)) {
                return tag[tag.length];
            }
            return tag;
        });
    }
    render() {
        return (
            <table className="dark">
                <thead>
                    <tr>
                        {this.map().map((key, index) => {
                            return (<th key={index}>{key}</th>);
                        })}
                    </tr>
                </thead>
                <tbody>
                    {this.props.data && this.props.data.map((row, index) => {
                        return (
                            <tr key={index} className={index % 2 === 1 ? 'dark' : ''} onClick={() => this.props.onClick(row)}>
                                {this.map(row, (col, i) => {
                                    return (<td key={index + '/' + i}>{col}</td>);
                                })}
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        );
    }
}

export default Table;