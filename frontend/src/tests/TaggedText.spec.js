/* eslint no-undef:0 */
import TaggedText from '../components/TaggedText';

describe('prepareEntities', () => {
  let taggedText;
  beforeAll(() => { taggedText = new TaggedText({ text: '' }); });

  test('Empty', () => {
    expect(taggedText.prepareEntities([
      {}
    ])).toEqual([
      {}
    ]);
  });
  test('Complete overlap', () => {
    expect(taggedText.prepareEntities([
      {
        label: 'Date',
        start: 0,
        end: 1
      },
      {
        label: 'Date',
        start: 0,
        end: 1
      }
    ])).toEqual([
      {
        label: 'Date',
        start: 0,
        end: 1
      }
    ]);
  });

  test('Complete overlap, different labels', () => {
    expect(taggedText.prepareEntities([
      {
        label: 'Date',
        start: 0,
        end: 1
      },
      {
        label: 'Person',
        start: 0,
        end: 1
      }
    ])).toEqual([
      {
        label: 'Date Person',
        start: 0,
        end: 1
      }
    ]);
  });

  test('Overlap, same start, style 1', () => {
    expect(taggedText.prepareEntities([
      {
        label: 'Date',
        start: 0,
        end: 5
      },
      {
        label: 'Person',
        start: 0,
        end: 3
      },
      {
        label: 'Date',
        start: 0,
        end: 5
      }
    ])).toEqual([
      {
        label: 'Person Date',
        start: 0,
        end: 3
      },
      {
        label: 'Date',
        start: 3,
        end: 5
      }
    ]);
  });

  test('Overlap, style 1', () => {
    expect(taggedText.prepareEntities([
      {
        label: 'Date',
        start: 0,
        end: 20
      },
      {
        label: 'Person',
        start: 5,
        end: 15
      },
      {
        label: 'Person',
        start: 5,
        end: 15
      },
      {
        label: 'Location',
        start: 10,
        end: 12
      }
    ])).toEqual([
      {
        label: 'Date',
        start: 0,
        end: 5
      },
      {
        label: 'Person Date',
        start: 5,
        end: 10
      },
      {
        label: 'Location Person Date',
        start: 10,
        end: 12
      },
      {
        label: 'Person Date',
        start: 12,
        end: 15
      },
      {
        label: 'Date',
        start: 15,
        end: 20
      }
    ]);
  });

  test('Overlap, style 2', () => {
    expect(taggedText.prepareEntities([
      {
        label: 'Date',
        start: 0,
        end: 10
      },
      {
        label: 'Person',
        start: 5,
        end: 15
      },
      {
        label: 'Location',
        start: 5,
        end: 10
      },
      {
        label: 'Location',
        start: 5,
        end: 10
      }
    ])).toEqual([
      {
        label: 'Date',
        start: 0,
        end: 5
      },
      {
        label: 'Location Date Person',
        start: 5,
        end: 10
      },
      {
        label: 'Person',
        start: 10,
        end: 15
      }
    ]);
  });

  test('Overlap, style 3', () => {
    expect(taggedText.prepareEntities([
      {
        label: 'Date',
        start: 5,
        end: 20
      },
      {
        label: 'Person',
        start: 0,
        end: 15
      },
      {
        label: 'Location',
        start: 3,
        end: 15
      },
      {
        label: 'Location',
        start: 3,
        end: 15
      }
    ])).toEqual([
      {
        end: 3,
        label: 'Person',
        start: 0
      },
      {
        end: 5,
        label: 'Location Person',
        start: 3
      },
      {
        end: 15,
        label: 'Location Person Date',
        start: 5
      },
      {
        end: 20,
        label: 'Date',
        start: 15
      }
    ]);
  });
});
