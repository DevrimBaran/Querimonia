import React from 'react';
import AsyncWrapper from '../components/AsyncWrapper/AsyncWrapper';
import Async from '../components/Async/Async';
import Collapsible from '../components/Collapsible/Collapsible';
import Api from '../utility/Api';

export const Edit = function() {
    return (
        <AsyncWrapper data={Api.post()}>
            <Collapsible id="sidebar">
                <nav id="menu">
                    <ul>
                        <Async fetch={() => Api.post('/api/test/recognizer', { text: "" })}>
                            <li >Issue 1</li>
                        </Async>
                        {

                        }
                        <li>Issue 2</li>
                        <li>Issue 3</li>
                    </ul>
                </nav>
            </Collapsible>
            <div>
                <h2>Edit</h2>

                <AsyncWrapper data={Api.post()}>


                </AsyncWrapper>
            </div>
        </AsyncWrapper>
    );
}

export default Edit;