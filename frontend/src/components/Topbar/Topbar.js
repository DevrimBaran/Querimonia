import React from 'react';
import logo from './../../assets/img/logo.svg';
import Collapsible from '../Collapsible/Collapsible';
import './Topbar.scss';

function Topbar() {
    return (
        <header className="Topbar">
            <img src={logo} className="Topbar-logo" alt="logo" />
            <Collapsible collapse={false} side="bottom">
                <ul>
                    <li>1</li>
                    <li>2</li>
                    <li>3</li>
                    <li>4</li>
                    <li>5</li>
                </ul>
            </Collapsible>
        </header>
    );
}

export default Topbar;
