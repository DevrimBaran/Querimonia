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
    priority: {
      type: 'number',
      label: 'Priorität'
    },
    rulesXml: {
      type: 'codemirror'
    },
    texts: {
      type: 'array',
      label: 'Antwortvariationen',
      children: {
        type: 'textarea',
        label: '#$i',
        attributes: {
          min: 2,
          max: 5
        }
      }
    },
    actions: {
      type: 'array',
      label: 'Aktionen',
      children: {
        type: 'object',
        label: '#$i',
        children: {
          name: {
            type: 'text',
            label: 'Name'
          },
          actionCode: {
            type: 'select',
            label: 'Art',
            attributes: {
              values: [
                { value: 'ATTACH_VOUCHER', label: 'Gutschein' },
                { value: 'SEND_MAIL', label: 'E-Mail' }
              ]
            }
          },
          parameters: {
            type: 'object',
            label: 'Parameter',
            children: {
              'E-Mail': {
                type: 'text',
                label: 'E-Mail'
              },
              Text: {
                type: 'text',
                label: 'Text'
              },
              Gutscheinwert: {
                type: 'number',
                label: 'Gutscheinwert'
              }
            }
          }
        }
      }
    }
  }
};
