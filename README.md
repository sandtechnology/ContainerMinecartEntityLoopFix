# ContainerMinecartEntityLoopFix
A mod to fix annoying chunk loading loop when unloading minecart entity which having container.

## How this happened?
To explain its cause, it's needed to explain how the chunk-unloading works:
1. Find out which chunk need to unload, then add into the pending unload list.
2. When its amount is over 2000, start to handle the chunk to unload:
   1. Try to pull the chunk to unload from chunk.
   2. If success, set the chunk loaded status to false.
   3. Save the chunk to disk.
   4. Remove chunk itself from the chunk set, in this time, chunk was acted as unloaded
   5. Process entities to be unloaded:
      1. Put all the block entities in this chunk to the block entities unload pending list
      2. Remove each entity (not block entity) in this chunk from world entity list then invoke the remove method of each entity
      3. ...

The problem is happened there, it uses the same method which use to remove entity normally (like kill, break action), 
so there is no way to know if this entity was removed due to chunk unloading from its side, and it will does the normal logic.

In vanilla, it's okay since all the changes just happened in memory and chunk has been saved to disk, but in modded server, this can be a big problem.

For Minecart which having container, it will drop items in its container when it was broke or killed, so does it in unloading, what if a mod getting any block or entity in this chunk this time for checking something? It will cause chunk load for unloading chunk, so a perfect dead loop raised and will nearly kill your server, but lucky, this is not a silent kill, it will also raise the log spam for duplicated uuid of minecraft:item entity like that:
```

[Server thread/WARN] [net.minecraft.world.server.ServerWorld/]: Trying to add entity with duplicated UUID 68e9dfd6-b738-459f-87df-f2227281f1bf. Existing minecraft:item#3732, new: minecraft:item#3760
[Server thread/WARN] [net.minecraft.world.server.ServerWorld/]: Trying to add entity with duplicated UUID f1fd9343-164c-4de1-8504-26dcf630c888. Existing minecraft:item#3733, new: minecraft:item#3761
[Server thread/WARN] [net.minecraft.world.server.ServerWorld/]: Trying to add entity with duplicated UUID 81d4cf43-1430-4af7-b11b-21d85e7439ac. Existing minecraft:item#3734, new: minecraft:item#3762
[Server thread/WARN] [net.minecraft.world.server.ServerWorld/]: Trying to add entity with duplicated UUID e7541f7c-03d8-4b93-b9cc-703818d6ef84. Existing minecraft:item#3735, new: minecraft:item#3763
[Server thread/WARN] [net.minecraft.world.server.ServerWorld/]: Trying to add entity with duplicated UUID cc604267-9eac-4b14-925c-786a0d622ea2. Existing minecraft:item#3599, new: minecraft:item#3796

```
It's because the entity UUID used by minecraft is using insecure random, and it's seed is time based, so its uuid could be the same for the entites created at the nearly same time.

This mod simply prevent it by stop dropping items when unloading such entity, but more case like that may happen in some mod entitites, in this case, please report to it's author to let them fix it.
