/**
 * ToDO:
 * Please describe this class.
 *
 * @version <0.1>
 */

import React, { Component } from 'react';

import { pd } from 'pretty-data';

class CodeMirror extends Component {
    componentDidMount = () => {
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
                    "!top": ["top"],
                    "!attrs": {
                    id: null,
                    class: ["A", "B", "C"]
                    },
                    top: {
                    attrs: {
                        lang: ["en", "de", "fr", "nl"],
                        freeform: null
                    },
                    children: ["animal", "plant"]
                    },
                    animal: {
                    attrs: {
                        name: null,
                        isduck: ["yes", "no"]
                    },
                    children: ["wings", "feet", "body", "head", "tail"]
                    },
                    plant: {
                    attrs: {name: null},
                    children: ["leaves", "stem", "flowers"]
                    },
                    wings: dummy, feet: dummy, body: dummy, head: dummy, tail: dummy,
                    leaves: dummy, stem: dummy, flowers: dummy
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
    componentWillUnmount = () => {
      delete window.CodeMirrorCallback;
      this.script.remove();
    }
    callback = (value) => {
      this.props.onChange && this.props.onChange(pd.xmlmin(value));
    }
    render () {
      const { onChange, value, ...passThroughProps } = this.props;

      return (
        <React.Fragment>
          <textarea id='codemirror' readOnly value={pd.xml(value)} {...passThroughProps} />
        </React.Fragment>
      );
    }
}

export default CodeMirror;
