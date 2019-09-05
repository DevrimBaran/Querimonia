export default function calculateEntities (entities) {
  let calculated = entities.map((entity) => ({ ids: { [entity.id]: 0 }, start: entity.start, end: entity.end }));
  let i = 0;
  const compare = (a, b) => { return a.start - b.start || a.end - b.end; };
  while (i < calculated.length - 1) {
    calculated.sort(compare);
    const entityA = calculated[i];
    const entityB = calculated[i + 1];

    if (entityA.start === entityB.start) {
      entityA.ids = { ...entityA.ids, ...entityB.ids };
      if (entityA.end === entityB.end) {
        calculated.splice(i + 1, 1);
      } else {
        entityB.start = entityA.end;
      }
    } else if (entityA.end === entityB.end) {
      entityB.ids = { ...entityA.ids, ...entityB.ids };
      entityA.end = entityB.start;
    } else if (entityA.end > entityB.end) {
      calculated.push({
        ids: entityA.ids,
        start: entityB.end,
        end: entityA.end
      });
      entityB.ids = { ...entityA.ids, ...entityB.ids };
      entityA.end = entityB.start;
    } else if (entityA.end < entityB.end && entityA.end > entityB.start) {
      calculated.push({
        ids: { ...entityA.ids, ...entityB.ids },
        start: entityB.start,
        end: entityA.end
      });
      entityA.end = entityB.start;
      entityB.start = entityA.end;
    } else {
      i++;
    }
  }
  return calculated.map((entity) => {
    entity.ids = Object.keys(entity.ids);
    return entity;
  });
};
