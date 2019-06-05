import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";

import Menu from "components/Menu/Menu";

import Home from "views/Home";
import Complaints from "views/Complaints";
import Statistics from "views/Statistics";
import WordVectors from "views/WordVectors";

function App() {
  return (
    <Router>
      <Menu />

      <Route exact path="/" component={Home} />
      <Route path="/complaints" component={Complaints} />
      <Route path="/statistics" component={Statistics} />
      <Route path="/wordvectors" component={WordVectors} />
    </Router>
  );
}
export default App;
