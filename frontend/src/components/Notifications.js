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
    this.timeout = 0;
    this.state = {
      hidden: false,
      animate: false,
      showNew: true,
      showAnalyse: true,
      showError: true,
      showDone: true,
      notifications: {}
    };
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

  onMessage = (e) => {
    const msg = JSON.parse(e.body);
    this.props.dispatch({
      type: 'UPDATE_STATE',
      id: msg.id,
      state: msg.state,
      endpoint: 'complaints'
    });
    msg.order = this.total++;
    clearTimeout(this.timeout);
    this.setState(state => ({ animate: false, notifications: { ...state.notifications, [msg.id]: msg } }));
    this.timeout = setTimeout(() => this.setState({ animate: true }), 200);
  }

  onClick = (id) => {
    const notification = this.state.notifications[id];
    this.close(id);
    this.props.history.push(`/complaints/${notification.id}`);
  }

  mapNotification = (notification) => {
    let text = false;
    if (notification.oldState === null) {
      text = this.state.showNew && 'Neue Beschwerde eingegangen.';
    } else if (notification.state === 'ANALYSING') {
      text = this.state.showAnalyse && 'Analyse gestartet.';
    } else if (notification.oldState === 'ANALYSING') {
      if (notification.state === 'ERROR') {
        text = this.state.showError && 'Fehler bei Analyse';
      } else {
        text = this.state.showDone && 'Analyse abgeschlossen';
      }
    }
    if (!text) return undefined;
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
      stompClient.subscribe('/complaints/state', this.onMessage);
    });
  }
  render () {
    // const { a, b, ...passThrough } = { ...this.props };
    const notifications = Object.values(this.state.notifications).sort((a, b) => a.order - b.order);
    const mappedNotifications = notifications.map(this.mapNotification).filter(n => n);
    this.toggleFavicon();
    return (
      <div id='notifications' data-animate={this.state.animate || undefined} data-count={notifications.length} style={{ '--notifications': mappedNotifications.length }}>
        {this.state.hidden ? (
          <div className='header'>
            <Button title='Benachrichtigungen' icon={['far fa-envelope', 'counter']} style={{ fontSize: '2rem' }} onClick={this.toggle} />
          </div>
        ) : (
          <div className='header'>
            <Button title='Benachrichtigungen' icon='fas fa-caret-down' onClick={this.toggle}>
              <span>{mappedNotifications.length}</span>
              <span className='dimPrimary'> {notifications.length - mappedNotifications.length || undefined}</span>
              <span> Benachrichtigungen</span>
            </Button>
            <Button title='Filter' icon='fas white fa-filter' onClick={() => this.setState(state => ({ showFilter: !this.state.showFilter }))} />
            {this.state.showFilter && (
              <div className='notificationFilter'>
                <Button title='Filter' icon={this.state.showNew ? ['fas fa-plus'] : ['fas primary fa-slash', 'fas dimPrimary fa-plus']} onClick={() => this.setState(state => ({ showNew: !this.state.showNew }))}>
                  Neue Beschwerde
                </Button>
                <Button title='Filter' icon={this.state.showAnalyse ? ['fas fa-project-diagram'] : ['fas primary fa-slash', 'fas dimPrimary fa-project-diagram']} onClick={() => this.setState(state => ({ showAnalyse: !this.state.showAnalyse }))}>
                  Analyse gestartet
                </Button>
                <Button title='Filter' icon={this.state.showDone ? ['fas fa-check'] : ['fas primary fa-slash', 'fas dimPrimary fa-check']} onClick={() => this.setState(state => ({ showDone: !this.state.showDone }))}>
                  Analyse abgeschlossen
                </Button>
                <Button title='Filter' icon={this.state.showError ? ['fas fa-exclamation'] : ['fas primary fa-slash', 'fas dimPrimary fa-exclamation']} onClick={() => this.setState(state => ({ showError: !this.state.showError }))}>
                  Fehler bei Analyse
                </Button>
              </div>
            )}
            <Button title='Alle schließen' icon='fas white fa-times' onClick={this.closeAll} />
          </div>
        )}
        <div className={'notificationList'} style={{ display: this.state.hidden ? 'none' : 'block' }}>
          {mappedNotifications}
        </div>
      </div>
    );
  }
}

export default withRouter(Notifications);
