import React, { Component } from 'react';
import './Page.scss';
import logo from '../../Logo_Querimonia.svg';
import Link from '../Link/Link';
import Home from '../../views/Home';
import Test1 from '../../views/Test1';
import Test2 from '../../views/Test2';
import Test3 from '../../views/Test3';
import Collapsible from '../Collapsible/Collapsible';
import { ViewContext } from '../../context/view-context';

class Page extends Component {
    constructor(props) {
        super(props);

        this.views = {
            'Home': <Home />,
            'Test1': <Test1 />,
            'Test2': <Test2 />,
            'Test3': <Test3 />
        }

        this.changeView = (name) => {
            console.log("changeView: ", name);
            this.setState({
                view: this.views[name] || this.views['Home']
            });
        }

        this.state = {
            view: <Home />,
            changeView : this.changeView
        }
    }
    render() { 
        return ( 
            <div id="page">
                <ViewContext.Provider value={this.state.changeView}>
                    <div id="topbar">
                        <Link view="Home"><img src={logo} className="App-logo" alt="logo" /></Link>
                    </div>
                    <Collapsible id="sidebar">
                        <nav id="menu">
                            <ul>
                                <Link view="Test1"><li>Test 1</li></Link>
                                <Link view="Test2"><li>Test 2</li></Link>
                                <Link view="Test3"><li>Test 3</li></Link>
                            </ul>
                        </nav>
                    </Collapsible>
                    <div id="body">
                        {this.state.view}
                    </div>
                </ViewContext.Provider>
            </div>
        );
    }
}
 
export default Page;