/**
 * This class creates the Impressum view.
 *
 * @version <0.1>
 */

import React from 'react';

import Block from './../components/Block';
import Row from './../components/Row';
import Content from './../components/Content';

function Impressum () {
  return (
    <React.Fragment>
      <Block>
        <Row vertical>
          <Content>
            <h1>Impressum</h1>
            <p>
            Das Fraunhofer-Institut für Arbeitswirtschaft und Organisation IAO
            Nobelstraße 12
            70569 Stuttgart
            </p>
            <p>
              ist eine rechtlich nicht selbstständige Einrichtung der
            </p>
            <p>
              Fraunhofer-Gesellschaft
            zur Förderung der angewandten Forschung e.V.
            Hansastraße 27 c
            80686 München
            Internet: www.fraunhofer.de
            E-Mail: info(at)zv.fraunhofer.de
            </p>
            <h2>Verantwortliche Redakteur:</h2>
            <p>
            Maximilien Kintz
            Telefon +49 711 970-2182
            Maximilien.Kintz@iao.fraunhofer.de
            </p>
            <h3>Hinweis:</h3>
            <p>
              Bei allgemeinen Fragen zum Institut wenden Sie sich bitte an:
            </p>
            <p>
              Fraunhofer IAO
            Presse und Öffentlichkeitsarbeit
            Nobelstraße 12
            70569 Stuttgart
            Telefon +49 711 970-2124
            presse@iao.fraunhofer.de
            </p>
            <p>
              Umsatzsteuer-Identifikationsnummer gemäß § 27 a Umsatzsteuergesetz: DE 129515865
            </p>
            <h3>Registergericht</h3>
            <p>
            Amtsgericht München
            Eingetragener Verein
            Register-Nr. VR 4461
            </p>
            <h2>Vorstand</h2>
            <p>
            Prof. Dr.-Ing. Reimund Neugebauer, Präsident, Unternehmenspolitik und Forschung
            Prof. Dr. Georg Rosenfeld, Technologiemarketing und Geschäftsmodelle
            Prof. Dr. Alexander Kurz, Personal, Recht und Verwertung
            Dipl.-Kfm. Andreas Meuer, Controlling und Digitale Geschäftsprozesse
            </p>
            <h3>Nutzungsrechte</h3>
            <p>
            Copyright © by Fraunhofer-Gesellschaft
            </p>
            <p>
              Alle Rechte vorbehalten.
            Die Urheberrechte dieser Webseite liegen vollständig bei der Fraunhofer-Gesellschaft.
            </p>
            <p>
              Ein Download oder Ausdruck dieser Veröffentlichungen ist ausschließlich für den persönlichen Gebrauch gestattet.Alle darüber hinaus gehenden Verwendungen, insbesondere die kommerzielle Nutzung und Verbreitung, sind grundsätzlich nicht gestattet und bedürfen der schriftlichen Genehmigung.
            </p>
            <p>
              Ein Download oder Ausdruck ist darüber hinaus lediglich zum Zweck der Berichterstattung über die Fraunhofer-Gesellschaft und ihrer Institute nach Maßgabe untenstehender Nutzungsbedingungen gestattet:
            </p>
            <p>
              Grafische Veränderungen an Bildmotiven - außer zum Freistellen des Hauptmotivs - sind nicht gestattet. Es ist stets die Quellenangabe und Übersendung von zwei kostenlosen Belegexemplaren an die oben genannte Adresse erforderlich. Die Verwendung ist honorarfrei.
            </p>
            <h3>Haftungshinweis</h3>
            <p>
            Wir übernehmen keine Haftung für die Inhalte externer Links. Für den Inhalt der verlinkten Seiten sind ausschließlich deren Betreiber verantwortlich.
            </p>
            <p>
              Wir sind bemüht, das Webangebot stets aktuell und inhaltlich richtig sowie vollständig anzubieten. Dennoch ist das Auftreten von Fehlern nicht völlig auszuschließen. Das Fraunhofer-Institut bzw. die Fraunhofer-Gesellschaft übernimmt keine Haftung für die Aktualität, die inhaltliche Richtigkeit sowie für die Vollständigkeit der in ihrem Webangebot eingestellten Informationen. Dies bezieht sich auf eventuelle Schäden materieller oder ideeller Art Dritter, die durch die Nutzung dieses Webangebotes verursacht wurden.
            </p>
            <p>
              Geschützte Marken und Namen, Bilder und Texte werden auf unseren Seiten in der Regel nicht als solche kenntlich gemacht. Das Fehlen einer solchen Kennzeichnung bedeutet jedoch nicht, dass es sich um einen freien Namen, ein freies Bild oder einen freien Text im Sinne des Markenzeichenrechts handelt.
            </p>
          </Content>
        </Row>
      </Block>
    </React.Fragment>
  );
}
export default Impressum;
