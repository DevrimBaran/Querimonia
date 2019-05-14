import React, { Component } from 'react'
import Collapsible from '../Collapsible/Collapsible';
import './Body.scss';

class Body extends Component {
    render() { 
        return (
            <div className="Body">
                <Collapsible collapse="false" side="right">
                    <ul>
                        <li>Item 1</li>
                        <li>Item 2</li>
                        <li>Item 3</li>
                        <li>Item 4</li>
                        <li>Item 5</li>
                    </ul>
                </Collapsible>
                <div id="content"></div>
                <Collapsible collapse="false" side="left">
                    <ul>
                        <li>Item 1</li>
                        <li>Item 2</li>
                        <li>Item 3</li>
                        <li>Item 4</li>
                        <li>Item 5</li>
                    </ul>
                </Collapsible>
            </div> 
        );
    }
}
 
export default Body;