import React from 'react';
import logo from './../../assets/img/logo.svg';
import Modal from '../Modal/Modal';
import './Topbar.scss';

function Topbar() {
    return (
        <header className="Topbar dark">
            <img src={logo} className="Topbar-logo" alt="logo" />
            <Modal label="Import">
                <textbox placeholder="Meldung eingeben oder Datei per drag and drop ablegen." />
                <input className="primary" type="button" value="importieren"/>
            </Modal>
            <Modal label="Export">
                <p>Lorem Ipsum</p>
            </Modal>
        </header>
    );
}

export default Topbar;
