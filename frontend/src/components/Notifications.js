/**
 * Renders a json object unminified in a html pre-tag.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
// import SockJS from 'sockjs-client';
// eslint-disable-next-line
import { withRouter } from 'react-router-dom';

import Button from './Button';

const Stomp = window.Stomp;
const SockJS = window.SockJS;
class Notifications extends Component {
  constructor (props) {
    super(props);
    this.icon = document.querySelector('link[rel*=icon]');
    this.url = new URL(this.icon.href);
    this.total = 0;
    this.state = {
      hidden: false,
      notifications: {}
    };
  }

  stompMessage = (e) => {
    const msg = JSON.parse(e.body);
    msg.order = this.total++;
    this.setState(state => ({ notifications: { ...state.notifications, [msg.id]: msg } }));
  }

  closeAll = () => {
    this.setState({ notifications: {} });
  }

  close = (id) => {
    this.setState(state => {
      const remaining = { ...state.notifications };
      delete remaining[id];
      return { notifications: remaining };
    });
  }

  toggleFavicon = () => {
    const showLetter = (Object.keys(this.state.notifications).length > 0);
    this.url.pathname = showLetter ? '/favicon2.ico' : '/favicon.ico';
    this.icon.href = this.url.href;
  }

  toggle = () => {
    this.setState(state => ({ hidden: !state.hidden }));
  }

  onClick = (id) => {
    const notification = this.state.notifications[id];
    this.close(id);
    this.props.history.push(`/complaints/${notification.id}`);
  }

  mapNotification = (notification) => {
    let text = false;
    if (notification.oldState === null) {
      text = 'Neue Beschwerde eingegangen.';
    } else if (notification.state === 'ANALYSING') {
      text = 'Analyse gestartet.';
    } else if (notification.oldState === 'ANALYSING') {
      text = `Analyse ${notification.state === 'ERROR' ? 'mit Fehler' : ''} abgeschlossen`;
    }
    if (!text) return <React.Fragment />;
    return (
      <div key={notification.id} className='notification' onClick={() => this.onClick(notification.id)}>
        <div className='title'>
          <span>Anliegen {notification.id}</span>
          <Button title='Schließen' className='close' icon='fas fa-times' onClick={() => this.close(notification.id)} />
        </div>
        <p>{text}</p>
      </div>
    );
  }

  componentDidMount = () => {
    const sock = new SockJS(`${process.env.REACT_APP_BACKEND_PATH}/api/websocket`);
    const stompClient = Stomp.over(sock);
    stompClient.connect({}, frame => {
      stompClient.subscribe('/complaints/state', this.stompMessage);
    });
    // sock.onopen = this.socketOpen;
    // sock.onclose = this.socketClose;
    // sock.onmessage = this.socketMessage;
  }
  render () {
    // const { a, b, ...passThrough } = { ...this.props };
    const notifications = Object.values(this.state.notifications).sort((a, b) => a.order - b.order);
    this.toggleFavicon();
    return (
      <div id='notifications' data-count={notifications.length}>
        <div className='header'>
          <Button title='Minimieren' icon={`fas fa-caret-${this.state.hidden ? 'right' : 'down'}`} style={{ color: 'white' }} onClick={this.toggle}>
            {notifications.length} Benachrichtigungen
          </Button>
          <Button title='Alle schließen' icon='fas fa-times' style={{ color: 'white' }} onClick={this.closeAll} />
        </div>
        <div className={'notificationList'} style={{ display: this.state.hidden ? 'none' : 'block' }}>
          {notifications.map(this.mapNotification)}
        </div>
      </div>
    );
  }
}

export default withRouter(Notifications);
