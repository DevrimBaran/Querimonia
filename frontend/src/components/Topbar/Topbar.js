import React, { Component } from 'react'
import logo from './../../assets/img/logo.svg';
import Modal from '../Modal/Modal';
import './Topbar.scss';
import api from '../../utility/Api';

class Topbar extends Component {
    constructor(props) {
        super(props);
            this.state = {
              
        }
    }

    onClick = () => {
            if (this.refs.textInput.value) {
                const url ="/api/import/text"
                return api.post(url,{text: this.refs.textInput.value})
                .then(response => console.warn("result",response))

            } else {
                const url ="/api/import/file"
                const formData = new FormData();
                formData.append("file",this.refs.fileInput.files[0]);
                return api.post(url,formData)
                .then(response => console.warn("result",response))
            }
        
    }

    render(){
    return (
        <header className="Topbar dark">
            <img src={logo} className="Topbar-logo" alt="logo" />
            <Modal label="Import">
                <div className="upload" onSubmit={this.onFormSubmit}>
                    <h1>Click to upload</h1>
                    <form method="post" enctype="multipart/form-data">
                    <input type="text" name="text" ref="textInput"/>
                    <input type="file" name="file" ref="fileInput" />
                    <input type="button" name="uploadButton" onClick={this.onClick}/>
                    </form>
                </div>
            </Modal>
            <Modal label="Export">
                <p>Hier sollte man auswählen können was man exportieren möchte?</p>
                <input className="primary" type="button" value="exportieren"/>
            </Modal>
        </header>
    );
}}

export default Topbar;
