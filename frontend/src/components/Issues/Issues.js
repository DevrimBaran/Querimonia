import React, { Component } from 'react'

import Api from '../../utility/Api';

import Block from '../Block/Block';
import Body from '../Body/Body';
import Collapsible from '../Collapsible/Collapsible';
//import Fa from '../Fa/Fa';
import Filter from '../Filter/Filter';
//import Issues from '../Issues/Issues';
import Log from '../Log/Log.js';
//import Modal from '../Modal/Modal';
import Stats from '../Stats/Stats';
import Tabbed from '../Tabbed/Tabbed';
import Table from '../Table/Table';
import TaggedText from '../TaggedText/TaggedText';
import Text from '../Text/Text';
import TextBuilder from '../TextBuilder/TextBuilder';
import Topbar from '../Topbar/Topbar';

import './Issues.scss';

class Issues extends Component {
    constructor(props) {
        super(props);

        this.state = {
            loading: true,
            active: null,
            issues: []
        }
    }
    fetchData = (data) => {
        console.log(data);
        this.setState({active: null, loading: true});
        Api.get('/api/issues', data)
            .then(this.setData);
    }
    setData = (data) => {
        this.setState({loading: false, issues: data});
    }
    activate = (issue) => {
        console.log(issue)
        this.setState({ active: this.state.issues.filter((a) => a.id === issue.id)[0]});
    }
    componentDidMount = () => {
        this.fetchData({query: { limit: 20, offset: 0}});
    }
    render() {
        return (
            <React.Fragment>
                <Topbar />
                {
                    this.state.active ? (
                        <Body>
                            <Collapsible collapse="false" side="right">
                                <ul class="dark">
                                    <li><strong className="a" onClick={() => this.activate({ id: -1 })}>Zurück</strong></li>
                                    {
                                        this.state.issues.map((issue, index) => {
                                            return <li key={index}><span className={issue.id === this.state.active.id ? 'a active' : 'a'} onClick={() => this.activate(issue)}>#{index} Anliegen {issue.id}</span></li>
                                        })
                                    }
                                </ul>
                            </Collapsible>
                            <Block>
                                <h2>???</h2>
                                <Tabbed>
                                    <TextBuilder label="Antwort erstellen"></TextBuilder>
                                    <div label="Details">
                                        <Stats></Stats>
                                        <Log></Log>
                                    </div>
                                </Tabbed>
                            </Block>
                            <Block>
                                <h2>Meldetext</h2>
                                <Tabbed>
                                    <TaggedText label="Überarbeitet" text={this.state.active.text}></TaggedText>
                                    <Text label="Original"></Text>
                                </Tabbed>
                            </Block>
                        </Body>
                    ) : ( 
                        <Body>
                            <Filter 
                                    onSubmit={(query) => this.fetchData({ query: query })}
                                keys={['id', 'date', 'thema', 'dringlichkeit']}
                                comparators={['=', '!=', '<', '<=', '>', '>=', 'LIKE', 'IN', 'BETWEEN', 'REGEXP']} />
                            {this.state.loading ?
                                (
                                    <i className="fa fa-spinner"></i>
                                ) : (
                                    <Table data={this.state.issues} onClick={this.activate} tags={['id', 'date', ['text', 'text'], 'thema', 'dringlichkeit']} />
                                )}
                        </Body>
                    )
                }
            </React.Fragment>
        );
    }
}
 
export default Issues;