import React from 'react';
import './Fa.scss';

function Fa(props) {
    return (
        <span className="toggle-fa" active={props.active ? "true" : "false"} onClick={props.onClick}>
            <i className={"on fa " + props.on}></i>
            <i className={"off fa " + props.off}></i>
        </span>
    );
}

export default Fa;