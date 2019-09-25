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
      attributes: {
        className: 'content'
      },
      children: {
        type: 'textarea',
        label: 'Antwortvariation',
        attributes: {
          min: 2,
          max: 5
        }
      }
    },
    actions: {
      type: 'array',
      children: {
        type: 'object',
        label: '#$i',
        children: {
          name: {
            type: 'text',
            label: 'Name'
          },
          email: {
            type: 'mail',
            label: 'E-Mail'
          }
        }
      }
    }
  }
};
