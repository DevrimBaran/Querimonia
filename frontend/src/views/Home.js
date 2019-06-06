import React from 'react';

import Block from 'components/Block/Block';
import logo from './../assets/img/StuproLogo2.svg';
import team from './../assets/img/Gruppenfoto.jpg';
import iaoPartner from './../assets/img/iao.svg';
import iatPartner from './../assets/img/iat.svg';

import './Home.scss';

function Home () {
  return (
    <React.Fragment>
      <Block>
        <div className='header'>
          <img src={logo} className='Topbar-logo' alt='logo' width='60%' />
        </div>
        <div className='body'>
          <br></br>
          <p>
          Im Rahmen des Studienprojektes der Universität Stuttgart im Sommersemester 2019
          wurde am IAT der Universität Stuttgart und dem Fraunhofer IAO die webbasierte
          Plattform Querimonia für die Erprobung und Anwendung von Verfahren zur Analyse
          und Aufbereitung von Texten am Beispiel des Beschwerdemanagements entwickelt.
          </p>
          <br></br>
          <p>
          Ein typisches Anwendungsbeispiel ist die automatische Generierung einer Antwort
          auf eine Beschwerdenachricht mit Hilfe von Mensch-Maschine-Kollaboration mit
          selbstlernenden Textminingverfahren.
          </p>
          <br></br>
          <h1>Unser Team</h1>
          <img src={team} className='Topbar-logo' alt='logo' width='50%' />
          <p>&copy; 2019 Fraunhofer IAO, IAT Universität Stuttgart</p>        
        </div>
        <div className='footer'>
          <h1>Unsere Partner</h1>
          <img src={iaoPartner} className='iaoPartner' alt='logo' width='40%' />
          <img src={iatPartner} className='iatPartner' alt='logo' width='40%' />
        </div>
      </Block>
    </React.Fragment>
  );
}

export default Home;
