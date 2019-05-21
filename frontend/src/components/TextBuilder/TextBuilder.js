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

import './TextBuilder.scss';
class TextBuilder extends Component {
    //Die Antworten kommen über api/response und die muss die ID der Beschwerde übergeben werden
    constructor(props) {
        super(props);

        this.state = {}
    }
    render() {
        return (
            <div className="TextBuilder">
                {this.props.children}
            </div>
        );
    }
}

export default TextBuilder;