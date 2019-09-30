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
import Grid from './Grid';
import Input from './Input';

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
      hidden: true,
      animate: false,
      showNew: true,
      showAnalyse: true,
      showProgress: false,
      showError: true,
      showDone: true,
      count: 5,
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

  toggleFavicon = (showLetter) => {
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
    console.log(this.props.history, this.props.location);
    this.props.history.push(`/complaints/${notification.id}`);
  }

  mapNotification = (notification) => {
    let text = false;
    let icon = '';
    if (notification.oldState === null) {
      text = this.state.showNew && 'Neue Beschwerde eingegangen.';
      icon = 'fas fa-plus';
      this.count.new++;
    } else if (notification.state === 'ANALYSING') {
      text = this.state.showAnalyse && 'Analyse gestartet.';
      icon = 'fas fa-project-diagram';
      this.count.analyse++;
    } else if (notification.state === 'IN_PROGRESS') {
      text = this.state.showProgress && 'Wird bearbeitet.';
      icon = 'fas fa-tools';
      this.count.progress++;
    } else if (notification.oldState === 'ANALYSING') {
      if (notification.state === 'ERROR') {
        text = this.state.showError && 'Fehler bei Analyse';
        icon = 'fas fa-exclamation';
        this.count.error++;
      } else {
        text = this.state.showDone && 'Analyse abgeschlossen';
        icon = 'fas fa-check';
        this.count.done++;
      }
    }
    if (!text) return undefined;
    return (
      <Grid columns='auto 1fr' key={notification.id} className='notification' onClick={() => this.onClick(notification.id)}>
        <i className={icon + ' icon'} />
        <span className='text'>
          <div className='title'>
            <span>Anliegen {notification.id}</span>
            <Button title='Schließen' className='close' icon='fas fa-times' onClick={() => this.close(notification.id)} />
          </div>
          <p>{text}</p>
        </span>
      </Grid>
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
    const notifications = Object.values(this.state.notifications).sort((a, b) => b.order - a.order);
    let total = 0;
    this.count = { new: 0, analyse: 0, progress: 0, error: 0, done: 0 };
    const mappedNotifications = notifications.map(this.mapNotification).filter(n => {
      return n && ++total <= this.state.count ? n : false;
    }).sort((a, b) => a.order - b.order);
    this.toggleFavicon(mappedNotifications.length > 0);
    return (
      <div id='notifications' data-animate={this.state.animate || undefined} data-count={notifications.length} style={{ '--notifications': mappedNotifications.length, '--hidden': notifications.length - mappedNotifications.length }}>
        {mappedNotifications.length > 0 && !this.state.hidden && (
          <div className={'notificationList'}>
            {mappedNotifications}
          </div>
        )}
        {this.state.hidden ? (
          <div className='footer'>
            <Button title='Benachrichtigungen' icon={['fas fa-envelope white', 'counter']} style={{ fontSize: '2rem' }} onClick={this.toggle} />
          </div>
        ) : (
          <div className='footer'>
            <Button title='Benachrichtigungen' icon='fas fa-caret-down' onClick={this.toggle}>
              <span>{mappedNotifications.length}</span>
              {notifications.length - mappedNotifications.length > 0 && (
                <span className='dimPrimary'> +{notifications.length - mappedNotifications.length}</span>
              )}
              <span> Benachrichtigungen</span>
            </Button>
            <Button title='Filter' icon='fas white fa-filter' onClick={() => this.setState(state => ({ showFilter: !this.state.showFilter }))} />
            {this.state.showFilter && (
              <div className='notificationFilter'>
                <Button title={`Benachrichtigungen ${this.state.showNew ? 'verstecken' : 'anzeigen'}`} icon={this.state.showNew ? ['fas fa-plus'] : ['fas dimPrimary fa-plus', 'fas primary fa-slash']} onClick={() => this.setState(state => ({ showNew: !this.state.showNew }))}>
                  Neue Beschwerde ({this.count.new})
                </Button>
                <Button title={`Benachrichtigungen ${this.state.showAnalyse ? 'verstecken' : 'anzeigen'}`} icon={this.state.showAnalyse ? ['fas fa-project-diagram'] : ['fas dimPrimary fa-project-diagram', 'fas primary fa-slash']} onClick={() => this.setState(state => ({ showAnalyse: !this.state.showAnalyse }))}>
                  Analyse gestartet ({this.count.analyse})
                </Button>
                <Button title={`Benachrichtigungen ${this.state.showDone ? 'verstecken' : 'anzeigen'}`} icon={this.state.showDone ? ['fas fa-check'] : ['fas dimPrimary fa-check', 'fas primary fa-slash']} onClick={() => this.setState(state => ({ showDone: !this.state.showDone }))}>
                  Analyse abgeschlossen ({this.count.done})
                </Button>
                <Button title={`Benachrichtigungen ${this.state.showProgress ? 'verstecken' : 'anzeigen'}`} icon={this.state.showProgress ? ['fas fa-tools'] : ['fas dimPrimary fa-tools', 'fas primary fa-slash']} onClick={() => this.setState(state => ({ showProgress: !this.state.showProgress }))}>
                  Wird bearbeitet ({this.count.progress})
                </Button>
                <Button title={`Benachrichtigungen ${this.state.showError ? 'verstecken' : 'anzeigen'}`} icon={this.state.showError ? ['fas fa-exclamation'] : ['fas dimPrimary fa-exclamation', 'fas primary fa-slash']} onClick={() => this.setState(state => ({ showError: !this.state.showError }))}>
                  Fehler bei Analyse ({this.count.error})
                </Button>
                <Input label='Anzahl' min='0' type='number' value={this.state.count} onChange={(e) => this.setState({ count: e.value })} />
              </div>
            )}
            <Button title='Alle schließen' icon='fas white fa-times' onClick={this.closeAll} />
          </div>
        )}
      </div>
    );
  }
}

export default withRouter(Notifications);
