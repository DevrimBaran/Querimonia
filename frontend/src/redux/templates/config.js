export default {
  type: 'object',
  children: {
    id: {
      type: 'number',
      label: 'ID',
      attributes: { readOnly: true }
    },
    name: {
      type: 'text',
      label: 'Name'
    },
    extractors: {
      type: 'array',
      label: 'Extraktoren',
      children: {
        type: 'object',
        label: '#$i',
        children: {
          name: {
            type: 'text',
            label: 'Name'
          },
          type: {
            type: 'select',
            label: 'Typ',
            attributes: {
              values: [
                { value: 'KIKUKO_TOOL', label: 'KIKUKO_TOOL' },
                { value: 'LOREM', label: 'LOREM' },
                { value: 'IPSUM', label: 'IPSUM' }
              ]
            }
          },
          colors: {
            type: 'array',
            label: 'Entitäten',
            children: {
              type: 'object',
              label: '#$i',
              children: {
                label: {
                  type: 'text',
                  label: 'Name'
                },
                color: {
                  type: 'color',
                  label: 'Farbe'
                }
              }
            }
          }
        }
      }
    },
    classifiers: {
      type: 'array',
      label: 'Klassifikatoren',
      children: {
        type: 'object',
        label: '#$i',
        children: {
          name: {
            type: 'text',
            label: 'Name'
          },
          propertyName: {
            type: 'text',
            label: 'Eigenschaft'
          },
          type: {
            type: 'select',
            label: 'Typ',
            attributes: {
              values: [
                { value: 'KIKUKO_CLASSIFIER', label: 'KIKUKO_CLASSIFIER' },
                { value: 'LOREM', label: 'LOREM' },
                { value: 'IPSUM', label: 'IPSUM' }
              ]
            }
          }
        }
      }
    },
    sentimentAnalyzer: {
      type: 'object',
      label: 'Sentiment',
      children: {
        name: {
          label: 'Name',
          type: 'text'
        },
        type: {
          label: 'Typ',
          type: 'text'
        }
      }
    },
    emotionAnalyzer: {
      type: 'object',
      label: 'Emotion',
      children: {
        name: {
          label: 'Name',
          type: 'text'
        },
        type: {
          label: 'Typ',
          type: 'text'
        }
      }
    },
    active: {
      type: 'checkbox',
      label: 'Aktiv',
      attributes: { readOnly: true }
    }
  }
};
