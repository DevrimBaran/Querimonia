import React from 'react';
import './Content.scss';

const Content = (props) => (
    <div {...props}>
        {props.children}
    </div>
);

export default Content;
