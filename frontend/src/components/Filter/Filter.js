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

import './Filter.scss';
class Filter extends Component {
    constructor(props) {
        super(props);
        this.filter = {
            "data": {}, // or undefined
            "query": {
                "where": {
                    "relation": "AND",
                    "clauses": [
                        {
                            "key": "color",
                            "value": ["red", "orange"],
                            "compare": "IN"
                        },
                        {
                            "relation": "OR",
                            "clauses": [
                                {
                                    "key": "brightness",
                                    "value": [3, 7],
                                    "compare": "BETWEEN"
                                },
                                {
                                    "key": "saturation",
                                    "value": 100,
                                    "compare": "="
                                }
                            ]
                        },
                    ]
                },
                "orderby": {
                    "color": "ASC",
                    "brightness": "DESC"
                }
            }
        }
        this.state = {}
    }
    render() {
        return (
            <div className="Filter">
                {this.props.children}
            </div>
        );
    }
}

export default Filter;