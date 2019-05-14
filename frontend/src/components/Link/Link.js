import React from 'react';
import {ViewContext} from '../../context/view-context';
import './Link.scss';

function Link(props) {
    return (
        <ViewContext.Consumer>
            {(changeView) => (
                <span className="link" onClick={() => changeView(props.view)}>{props.children}</span>
            )}
        </ViewContext.Consumer>
    );
}

export default Link;