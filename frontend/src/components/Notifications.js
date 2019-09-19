/**
 * Renders a json object unminified in a html pre-tag.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';
import SockJS from 'sockjs-client';

// eslint-disable-next-line
import { withRouter } from 'react-router-dom';

import Button from './Button';

class Notifications extends Component {
  constructor (props) {
    super(props);
    this.icon = document.querySelector('link[rel*=icon]');
    this.url = new URL(this.icon.href);
    this.state = {
      hidden: false,
      notifications: [
        {
          id: 1,
          oldState: null,
          state: 'ANALYSING'
        },
        {
          id: 2,
          oldState: 'NEW',
          state: 'ANALYSING'
        },
        {
          id: 3,
          oldState: 'ANALYSING',
          state: 'ERROR'
        },
        {
          id: 4,
          oldState: 'ANALYSING',
          state: 'NEW'
        }
      ]
    };
  }

  onOpen = () => {
    console.log('Socket opened');
  }

  onClose = () => {
    console.log('Socket closed');
  }

  onMessage = (e) => {
    console.log('new Message', e);
  }

  closeAll = () => {
    this.setState({ notifications: [] });
  }

  close = (i) => {
    this.setState(state => {
      const remaining = state.notifications.concat();
      remaining.splice(i, 1);
      console.log(state.notifications, remaining);
      return { notifications: remaining };
    });
  }

  changeFavicon = (filename) => {
    this.url.pathname = `/${filename}.ico`;
    this.icon.href = this.url.href;
  }

  toggle = () => {
    this.setState(state => ({ hidden: !state.hidden }));
  }

  onClick = (i) => {
    const notification = this.state.notifications[i];
    this.close(i);
    if (notification.state !== 'ANALYSING' && notification.state !== 'ERROR') {
      this.props.history.push(`/complaints/${notification.id}`);
    }
  }

  mapNotification = (notification, i) => {
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
      <div key={i} className='notification' onClick={() => this.onClick(i)}>
        <div className='title'>
          <span>Anliegen {notification.id}</span>
          <Button title='Schließen' className='close' icon='fas fa-trash-alt' onClick={() => this.close(i)} />
        </div>
        <p>{text}</p>
      </div>
    );
  }

  componentDidMount = () => {
    const sock = new SockJS(`${process.env.REACT_APP_BACKEND_PATH}/api/websocket`);
    sock.onopen = this.onOpen;
    sock.onclose = this.onClose;
    sock.onmessage = this.onMessage;
  }
  render () {
    // const { a, b, ...passThrough } = { ...this.props };
    return (
      <div id='notifications' data-count={this.state.notifications.length}>
        <div className='header'>
          <Button title='Minimieren' icon={`fas fa-caret-${this.state.hidden ? 'right' : 'down'}`} onClick={this.toggle}>
            {this.state.notifications.length} Benachrichtigungen
          </Button>
          <Button title='Alle schließen' icon='fas fa-trash-alt' onClick={this.closeAll} />
        </div>
        <div className={'notificationList'} style={{ display: this.state.hidden ? 'none' : 'block' }}>
          {this.state.notifications.map(this.mapNotification)}
        </div>
      </div>
    );
  }
}

export default withRouter(Notifications);
