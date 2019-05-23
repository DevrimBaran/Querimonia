import React, { Component } from 'react'
import logo from './../../assets/img/logo.svg';
import Modal from '../Modal/Modal';
import './Topbar.scss';
import api from '../../utility/Api';

class Topbar extends Component {
    constructor(props) {
        super(props);
            this.state = {
                image: ''
        }
    }

    onChange(e){
        let files = e.target.files;

        let reader = new FileReader();
        reader.readAsDataURL(files[0]);

        reader.onload = (e) => {
            const url ="https://localhost:3000/api/import/file"
            const formData = {file:e.target.result}
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
            <h1>Upload</h1>
            <input type="file" name="file" onChange={(e) => this.onChange(e)}/>

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
