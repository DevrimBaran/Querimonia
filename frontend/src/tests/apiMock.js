const api = {
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

export const fetch = function(uri, options) {
    return new Promise(
        function (resolve, reject) {
            _fetch(uri, options)
                .then(resolve)
                .catch(() => {
                    resolve({
                        json: () => api[uri]
                    })
                });
        });
}

export default fetch;