import React, { Component } from 'react';

const jsonPath = 'https://querimonia.iao.fraunhofer.de/inf/data/backend-openapi/openapi.json';
const realFetch = fetch;
const log = (r, e) => {
  console.log(r, e);
  return r;
};

/**
 *  Wird vorraussichlich durch externen mock Server ersetzt
 * */
class OpenApi extends Component {
  constructor (props) {
    super(props);
    this.state = {
      active: props.active || false,
      hasUpdate: false,
      newJson: null,
      json: null,
      error: false
    };
  }
    componentWillMount = () => {
      this.checkUpdate();
    }
    checkOk = (response) => {
      if (response && response.ok) return response;
      throw new Error('Response not okay!');
    }
    checkUpdate = () => {
      realFetch(jsonPath, { mode: 'cors' })
        .then(this.checkOk)
        .then(log)
        .then(response => response.json())
        .then(json => {
          if (JSON.stringify(json) !== JSON.stringify(this.state.json)) {
            this.setState({
              newJson: json,
              hasUpdate: true
            });
          }
        })
        .catch(error => {
          this.setState({
            error: true
          });
        });
    }
    update = () => {
      this.setState(state => ({
        hasUpdate: false,
        json: state.newJson,
        newJson: null
      }));
    }
    getSchema = (ref) => {
      let response;
      const name = ref.substr(response['$ref'].lastIndexOf('/') + 1);
      return this.state.json.components.schemas[name];
    }
    getRoute = (resource, mode) => {
      return this.state.json.paths[resource][mode];
    }
    gererateExample = () => {
      return {};
    }
    generateResponse = (resource, options) => {
      console.log('Generating mock data for ' + resource, options);
      const route = this.getRoute(resource, options.mode);
      const response = route.responses['200'].content['application/json'].schema;
      return response;
      // if (response['$ref']) {
      //    const data = schemas[schemaName];
      // }
    }
    fakeFetch = (resource, options) => {
      if (!this.state.json) return realFetch(resource, options);
      let response = new Response(
        new Blob([JSON.stringify(this.generateResponse(resource, options))], { type: 'application/json' }),
        { 'status': 200, 'statusText': 'OpenApiMock' });

      return new Promise(function (resolve, reject) {
        if (this.props.timeout) {
          setTimeout(resolve, this.props.timeout, response);
        } else {
          resolve(response);
        }
      });
    }
    toggle = () => {
      this.setState(state => {
        // eslint-disable-next-line
            fetch = state.active ? this.fakeFetch : realFetch;
        return {
          active: !state.active
        };
      });
    }
    render () {
      if ((process.env.NODE_ENV === 'development' || process.env.REACT_APP_BACKEND_PATH === 'mock')) {
        return (
          <div {...this.props}>
            <label htmlFor='openapi-active'>OpenApi Mock:</label>
            <input id='openapi-active' type='checkbox' onChange={this.toggle} checked={this.state.active} />
            {this.state.hasUpdate
              ? (<i className={'fa fa-sync' + (this.state.error ? '' : ' primary')} onClick={this.update} />)
              : (this.state.json
                ? (<i className={'fa fa-check' + (this.state.error ? '' : ' primary')} />)
                : (<i className={'fa fa-times' + (this.state.error ? '' : ' primary')} />)
              )}
          </div>
        );
      }
    }
}

export default OpenApi;
