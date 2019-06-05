import React, { Component } from 'react';

import Block from 'components/Block/Block';

import Api from 'utility/Api';

import Body from 'components/Body/Body';
import Collapsible from 'components/Collapsible/Collapsible';
// import Fa from 'components/Fa/Fa';
import Filter from 'components/Filter/Filter';
// import Home from 'components/Home/Home';
import Log from 'components/Log/Log.js';
// import Modal from 'components/Modal/Modal';
import Stats from 'components/Stats/Stats';
import Tabbed from 'components/Tabbed/Tabbed';
import Table from 'components/Table/Table';
import TaggedText from 'components/TaggedText/TaggedText';
import Text from 'components/Text/Text';
import TextBuilder from 'components/TextBuilder/TextBuilder';
import Topbar from 'components/Topbar/Topbar';

function Home() {
    return (
        <React.Fragment>
            <Block>
                <h6 class="center">Antwort</h6>
            </Block>
            <Block>
                <h6 class="center">Meldetext</h6>
            </Block>
        </React.Fragment>
    );
}

export default Home;
