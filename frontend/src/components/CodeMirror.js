/**
 * Displays XML Code
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import { pd } from 'pretty-data';

class CodeMirror extends Component {
    inject = () => {
      window.CodeMirrorCallback = this.callback;
      this.script = document.createElement('script');
      this.script.type = 'text/javascript';
      this.script.async = true;
      this.script.innerHTML =
        `
            (function () {
                var dummy = {
                    attrs: {
                    color: ["red", "green", "blue", "purple", "white", "black", "yellow"],
                    size: ["large", "medium", "small"],
                    description: null
                    },
                    children: []
                };

                var tags = {
                    "!top": ["Rules"],
                    Rules: {
                        attrs: {
                        },
                        children: ["Subject", "Sentiment", "EntityAvailable", "Predecessor", "PredecessorCount", "UploadDate", "UploadTime", "Not", "Or", "And"]
                    },
                    Subject: {
                        attrs: {
                            value: [""]
                        },
                        children: []
                    },
                    Sentiment: {
                        attrs: {
                            value: [""]
                        },
                        children: []
                    },
                    EntityAvailable: {
                        attrs: {
                            label: [""],
                            value: [""]
                        },
                        children: []
                    },
                    Predecessor: {
                        attrs: {
                            matches: [""],
                            position: ["any", "last", "0"]
                        },
                        children: []
                    },
                    PredecessorCount: {
                        attrs: {
                            min: ["0"],
                            max: ["0"]
                        },
                        children: []
                    },
                    UploadDate: {
                        attrs: {
                            min: ["2019-01-01"],
                            max: ["2019-01-01"]
                        },
                        children: []
                    },
                    UploadTime: {
                        attrs: {
                            min: ["00:00:00"],
                            max: ["00:00:00"]
                        },
                        children: []
                    },
                    Not: {
                        attrs: {

                        },
                        children: ["Subject", "Sentiment", "EntityAvailable", "Predecessor", "PredecessorCount", "UploadDate", "UploadTime", "Not", "Or", "And"]
                    },
                    Or: {
                        attrs: {

                        },
                        children: ["Subject", "Sentiment", "EntityAvailable", "Predecessor", "PredecessorCount", "UploadDate", "UploadTime", "Not", "Or", "And"]
                    },
                    And: {
                        attrs: {

                        },
                        children: ["Subject", "Sentiment", "EntityAvailable", "Predecessor", "PredecessorCount", "UploadDate", "UploadTime", "Not", "Or", "And"]
                    }
                };

                function completeAfter(cm, pred) {
                    var cur = cm.getCursor();
                    if (!pred || pred()) setTimeout(function() {
                    if (!cm.state.completionActive)
                        cm.showHint({completeSingle: false});
                    }, 100);
                    return CodeMirror.Pass;
                }

                function completeIfAfterLt(cm) {
                    return completeAfter(cm, function() {
                    var cur = cm.getCursor();
                    return cm.getRange(CodeMirror.Pos(cur.line, cur.ch - 1), cur) == "<";
                    });
                }

                function completeIfInTag(cm) {
                    return completeAfter(cm, function() {
                    var tok = cm.getTokenAt(cm.getCursor());
                    if (tok.type == "string" && (!/['"]/.test(tok.string.charAt(tok.string.length - 1)) || tok.string.length == 1)) return false;
                    var inner = CodeMirror.innerMode(cm.getMode(), tok.state).state;
                    return inner.tagName;
                    });
                }

                console.log('LOADING CODEMIRROR');
                const editor = document.getElementById('codemirror');
                const codemirror_dummy = document.getElementById('codemirror_dummy');
                const myCodeMirror = CodeMirror.fromTextArea(editor, {
                    mode: 'xml',
                    theme: 'neat',
                    lineNumbers: true,
                    viewportMargin: Infinity,
                    extraKeys: {
                        "'<'": completeAfter,
                        "'/'": completeIfAfterLt,
                        "' '": completeIfInTag,
                        "'='": completeIfInTag,
                        "Ctrl-Space": "autocomplete"
                    },
                    hintOptions: {schemaInfo: tags}
                });
                myCodeMirror.on('change',(cMirror) => {
                    window.CodeMirrorCallback(cMirror.getValue());
                });
            })()
            `;
      document.body.appendChild(this.script);
    }
    remove = () => {
      delete window.CodeMirrorCallback;
      this.script.remove();
    }
    componentDidUpdate = (prevProps, prevState, prevContext) => {
      // this.remove();
      // this.inject();
    }
    componentDidMount = () => {
      this.inject();
    }
    componentWillUnmount = () => {
      this.remove();
    }
    callback = (value) => {
      this.props.onChange && this.props.onChange(pd.xmlmin(value));
    }
    render () {
      const { onChange, value, id, ...passThroughProps } = this.props;

      return (
        <React.Fragment>
          <textarea id='codemirror' readOnly value={pd.xml(value)} {...passThroughProps} />
        </React.Fragment>
      );
    }
}

export default CodeMirror;
