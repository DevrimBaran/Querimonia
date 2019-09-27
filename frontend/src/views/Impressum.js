/**
 * This class creates the Impressum view.
 *
 * @version <0.1>
 */

import React from 'react';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';
import team from './../assets/img/Gruppenfoto.jpg';
import spotlightOT from './../assets/img/spotlight_ohneText.svg';
import '../assets/scss/_images.scss';
import '../assets/scss/toSort/_boxWithMargin.scss';

function Impressum () {
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <Content>
            <div className='header'>
              <img src={spotlightOT} className='spotlight' alt='logo' />
              <br />
            </div>
            <div style={{
              margin: 'auto',
              width: 'fit-content',
              display: 'flex',
              flexDirection: 'column'
            }}>
              <div className='boxWithMargin' >
                <h1>Impressum</h1>
              </div>
              <div style={{ display: 'flex', justifyItems: 'center' }}>
                <div className='boxWithMargin' >
                  <br />
                  <p>
                  Das Fraunhofer-Institut für Arbeitswirtschaft und Organisation IAO<br />
                  Nobelstraße 12<br />
                  70569 Stuttgart<br />
                  </p>
                  <br />
                  <p>
                  ist eine rechtlich nicht selbstständige Einrichtung der
                  </p>
                  <br />
                  <p>
                  Fraunhofer-Gesellschaft<br />
                  zur Förderung der angewandten Forschung e.V.<br />
                  Hansastraße 27 c<br />
                  80686 München<br />
                  Internet: www.fraunhofer.de<br />
                  E-Mail: info(at)zv.fraunhofer.de<br />
                  </p>
                  <br />
                  <h2>Verantwortlicher Redakteur:</h2>
                  <p>
                  Maximilien Kintz<br />
                  Telefon +49 711 970-2182<br />
                  Maximilien.Kintz(at)iao.fraunhofer.de<br />
                  </p>
                  <br />
                  <h3>Hinweis:<br /></h3>
                  <p>
                  Bei allgemeinen Fragen zum Institut wenden Sie sich bitte an:<br />
                  </p>
                  <p>
                  Fraunhofer IAO<br />
                  Presse und Öffentlichkeitsarbeit<br />
                  Nobelstraße 12<br />
                  70569 Stuttgart<br />
                  Telefon +49 711 970-2124<br />
                  presse(at)iao.fraunhofer.de<br />
                  </p>
                  <br />
                  <p>
                  Umsatzsteuer-Identifikationsnummer gemäß § 27 a Umsatzsteuergesetz: DE 129515865
                  </p>
                  <br />
                  <h3>Registergericht<br /></h3>
                  <p>
                    Amtsgericht München<br />
                    Eingetragener Verein<br />
                    Register-Nr. VR 4461<br />
                  </p>
                  <br />
                  <h2>Vorstand<br /></h2>
                  <p>
                  Prof. Dr.-Ing. Reimund Neugebauer, Präsident, Unternehmenspolitik und Forschung<br />
                  Prof. Dr. Georg Rosenfeld, Technologiemarketing und Geschäftsmodelle<br />
                  Prof. Dr. Alexander Kurz, Personal, Recht und Verwertung<br />
                  Dipl.-Kfm. Andreas Meuer, Controlling und Digitale Geschäftsprozesse<br />
                  </p>
                  <br />
                </div>
                <div className='boxWithMargin' >
                  <h3>Nutzungsrechte<br /></h3>
                  <p>
                    Copyright © by Fraunhofer-Gesellschaft
                  </p>
                  <br />
                  <p>
                  Alle Rechte vorbehalten. <br />
                  Die Urheberrechte dieser Webseite liegen vollständig bei der Fraunhofer-Gesellschaft.<br />
                  </p>
                  <br />
                  <p>
                  Ein Download oder Ausdruck dieser Veröffentlichungen ist ausschließlich für den persönlichen Gebrauch gestattet. Alle darüber hinaus gehenden Verwendungen, insbesondere die kommerzielle Nutzung und Verbreitung, sind grundsätzlich nicht gestattet und bedürfen der schriftlichen Genehmigung.
                    <br />
                  </p>
                  <br />
                  <p>
                  Ein Download oder Ausdruck ist darüber hinaus lediglich zum Zweck der Berichterstattung über die Fraunhofer-Gesellschaft und ihrer Institute nach Maßgabe untenstehender Nutzungsbedingungen gestattet:
                    <br />
                  </p>
                  <br />
                  <p>
                  Grafische Veränderungen an Bildmotiven - außer zum Freistellen des Hauptmotivs - sind nicht gestattet.  <br />Es ist stets die Quellenangabe und Übersendung von zwei kostenlosen Belegexemplaren an die oben genannte Adresse erforderlich. Die Verwendung ist honorarfrei.
                    <br />
                  </p>
                  <br />
                  <h3>Haftungshinweis<br /></h3>
                  <p>
                  Wir übernehmen keine Haftung für die Inhalte externer Links.  <br />Für den Inhalt der verlinkten Seiten sind ausschließlich deren Betreiber verantwortlich.
                    <br />
                  </p>
                  <br />
                  <p>
                  Wir sind bemüht, das Webangebot stets aktuell und inhaltlich richtig sowie vollständig anzubieten. <br /> Dennoch ist das Auftreten von Fehlern nicht völlig auszuschließen. Das Fraunhofer-Institut bzw. die Fraunhofer-Gesellschaft übernimmt keine Haftung für die Aktualität, die inhaltliche Richtigkeit sowie für die Vollständigkeit der in ihrem Webangebot eingestellten Informationen. Dies bezieht sich auf eventuelle Schäden materieller oder ideeller Art Dritter, die durch die Nutzung dieses Webangebotes verursacht wurden.
                    <br />
                  </p>
                  <br />
                  <p>
                  Geschützte Marken und Namen, Bilder und Texte werden auf unseren Seiten in der Regel nicht als solche kenntlich gemacht.  <br />Das Fehlen einer solchen Kennzeichnung bedeutet jedoch nicht, dass es sich um einen freien Namen, ein freies Bild oder einen freien Text im Sinne des Markenzeichenrechts handelt.
                    <br />
                  </p>
                  <br />
                </div>
              </div>
              <div className='boxWithMargin' >
                <img src={team} className='team' alt='logo' />
                <figcaption className='center' >Unser Team des Studienprojektes der Universität Stuttgart im Sommersemester 2019 </figcaption>
              </div>
            </div>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}
export default Impressum;
