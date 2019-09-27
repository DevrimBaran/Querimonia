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
    sentimentAnalyzer: {
      type: 'object',
      label: 'Sentiment',
      children: {
        name: {
          label: 'Name',
          type: 'hidden',
          default: ''
        },
        type: {
          label: 'Typ',
          type: 'select',
          attributes: {
            required: true,
            values: [
              {
                label: 'Keine',
                value: 'NONE'
              },
              {
                label: 'Querimonia',
                value: 'QUERIMONIA_SENTIMENT'
              }
            ]
          }
        }
      }
    },
    emotionAnalyzer: {
      type: 'object',
      label: 'Emotion',
      children: {
        name: {
          label: 'Name',
          type: 'hidden',
          default: ''
        },
        type: {
          label: 'Typ',
          type: 'select',
          attributes: {
            required: true,
            values: [
              {
                label: 'Keine',
                value: 'NONE'
              },
              {
                label: 'Querimonia',
                value: 'QUERIMONIA_EMOTION'
              }
            ]
          }
        }
      }
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
            label: 'Name',
            attributes: {
              values: extractors
            }
          },
          type: {
            type: 'hidden',
            default: 'KIKUKO_PIPELINE'
          },
          label: {
            type: 'text',
            label: 'Label'
          },
          color: {
            type: 'color',
            label: 'Farbe',
            default: '#cccccc'
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
            type: 'select',
            label: 'Typ',
            conditional: 'type',
            attributes: {
              required: true,
              values: {
                NONE: [''],
                KIKUKO_CLASSIFIER: extractors,
                ML_CLASSIFIER: ['Querimonia Klassifikator']
              }
            }
          },
          propertyName: {
            type: 'text',
            label: 'Eigenschaft'
          },
          type: {
            type: 'select',
            attributes: {
              required: true,
              values: [
                {
                  label: 'Ohne',
                  value: 'NONE'
                },
                {
                  label: 'Querimonia',
                  value: 'ML_CLASSIFIER'
                },
                {
                  label: 'KIKuKo',
                  value: 'KIKUKO_CLASSIFIER'
                }
              ]
            }
          }
        }
      }
    }
  }
});
