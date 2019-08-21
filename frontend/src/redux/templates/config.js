export default (extractors) => ({
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
            type: 'select',
            label: 'Typ',
            attributes: {
              values: extractors
            }
          },
          type: {
            type: 'hidden'
          },
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
              values: [{ label: 'NONE', value: 'NONE' }, { label: 'KIKUKO_CLASSIFIER', value: 'KIKUKO_CLASSIFIER' }]
            }
            /*
            type: 'conditional',
            label: 'Typ',
            attributes: {
              name2: 'name',
              label2: 'Name',
              values: extractors
            }
            */
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
});
