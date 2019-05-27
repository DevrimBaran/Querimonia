import React, { Component } from 'react'

import Api from '../../utility/Api';

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

    onClick = () =>{
        var element = this.refs.TextBuilder;
        while(element.firstChild){
            element.removeChild(element.firstChild);
        }
    }

    componentDidMount() {
        Api.post('/api/response/new/' + this.props.complaintId)
            .then((data) => { console.log(data); return data; })
            .then(this.setData);
    }

    render() {
        return (
            <div className="TextBuilder" ref="TextBuilder">
                
            </div>
        );
    }
}

export default TextBuilder;
/*

<div className="TextBuilder">
    <div id="TextBuilder" ref="TextBuilder">
        <p>Hallo Mr.Bond,</p><br />
        <p>vielen Dank für Ihre Anfrage.</p>
        <br />
        <div className="antwortFeld1">
            <input id="button" type="button" name="antwort"
                value="Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam"
                onClick={this.onClick} />
        </div>
        <br />
        <div className="antwortFeld2">
            <input id="button" type="button" name="antwort"
                value="erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,"
                onClick={this.onClick} />
        </div>
        <br />
        <div className="antwortFeld3">
            <input id="button" type="button" name="antwort"
                value="no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam"
                onClick={this.onClick} />
        </div>
        <br />
        <br />
        <p>Mit freundlichen Grüßen</p>
        <p>Eure Spezialisten für Hardcoden!</p>
    </div>
</div>

*/