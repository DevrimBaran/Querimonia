const api = {
    "/api/issues": [
        {id:  1, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 1 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id:  2, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 2 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id:  3, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 3 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id:  4, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 4 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id:  5, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 5 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id:  6, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 6 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id:  7, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 7 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id:  8, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 8 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id:  9, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 9 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 10, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 10 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 11, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 11 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 12, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 12 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 13, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 13 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 14, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 14 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 15, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 15 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 16, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 16 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 17, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 17 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 18, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 18 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 19, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 19 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]}},
        {id: 20, datum: '01.01.1970', thema: '', dringlichkeit: '', text:{text: "ID 20 Der Bus der Linie 42 kam am 07.02.1990 zu spät.", entities:[]} }
    ],
    "/api/test/recognizer": [{
        text: "Lorem, ipsum dolor sit amet consectetur adipisicing elit. Accusamus, similique!",
        answer: null,
        entities: [
            {
                label: "test",
                start: 3,
                end: 6
            },
            {
                label: "test",
                start: 15,
                end: 20
            }
        ]
    }],
    "/api/test/textominado": [{
        text: "Lorem, ipsum dolor sit amet consectetur adipisicing elit. Accusamus, similique!",
        answer: "Heute ist nicht alle Tage, ich komm wieder, keine Frage!",
        entities: [
            {
                label: "test",
                start: 3,
                end: 6
            },
            {
                label: "test",
                start: 15,
                end: 20
            }
        ]
    }],
    "/api/test/textominado-batch": [
        {
            text: "Lorem, ipsum dolor sit amet consectetur adipisicing elit. Accusamus, similique!",
            answer: null,
            entities: [
                {
                    label: "test",
                    start: 3,
                    end: 6
                },
                {
                    label: "test",
                    start: 15,
                    end: 20
                }
            ]
        }, {
            text: "Lorem, ipsum dolor sit amet consectetur adipisicing elit. Accusamus, similique!",
            answer: null,
            entities: [
                {
                    label: "test",
                    start: 3,
                    end: 6
                },
                {
                    label: "test",
                    start: 15,
                    end: 20
                }
            ]
        }, {
            text: "Lorem, ipsum dolor sit amet consectetur adipisicing elit. Accusamus, similique!",
            answer: null,
            entities: [
                {
                    label: "test",
                    start: 3,
                    end: 6
                },
                {
                    label: "test",
                    start: 15,
                    end: 20
                }
            ]
        }
    ]
}
const _fetch = fetch;

function fakeResponse(uri, options) {
    return api[uri];
}

fetch = function(uri, options) {
    return new Promise(
        function (resolve, reject) {
            _fetch(uri, options)
            .then(resolve)
            .catch(() => {
                window.setTimeout(
                    () => {
                        resolve({
                            json: () => fakeResponse(uri, options)
                        })
                    }, 1000
                )
            });
        }
    );
}

export default fetch;