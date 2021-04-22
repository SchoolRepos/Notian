# ProfiSync™

## Synchronization process

1. Get all local entities(1)
2. Split into scheduled for deletion(2) with `additionalData=DELETE` and remaining(3)
3. Execute remote and local deletions of 2
4. Filter 3 into "new" entities(4) with `id=NEW`
5. Upload 4 to remote and adjust `remoteID` of entities
6. Get new list of remaining local entities(5) because `remoteID`s have changed
7. Get remote entities(6)
8. Split 6 into merge conflicts(7) that have a matching `remoteID` in 6 and downloaded lists(8)
9. Store 8 in local database
10. Pair each entity from 7 with the entity with matching `remoteID` from 6(9)
11. Merge the pairs from 9 to a single entity(10) and update the timestamp in `additionalData`
    * If a property only exists locally, use the local property and vice versa
    * If a property exists both locally and remotely then use the values from the entity with higher timestamp
12.  Compare server-relevant properties of each item of 10 with the remotely stored version and update if different
13. Compare client-relevant properties of each item of 10 with the locally stored version and update if different
    * The comparison could be omitted because SQLite performance is not a concern

## Common functionality for entities

* `remoteID` and `additionalData`
* server-relevant comparison
* client-relevant comparison
* database crud
* remote crud
* merging