package com.github.sandtechnology.containerminecartentityloopfix.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Redirect(method = "onChunkUnloading", at = @At(value = "INVOKE",target = "Lnet/minecraft/world/server/ServerWorld;onEntityRemoved(Lnet/minecraft/entity/Entity;)V"))
    public void ContainerMinecartEntityLoopFix$unload$onEntityRemoved(ServerWorld instance, Entity entityForUnloading) {
        if(entityForUnloading instanceof ContainerMinecartEntity){
            //In this time chunk was saved, so this change just happen on runtime and not persisted
            ((ContainerMinecartEntity) entityForUnloading).dropContentsWhenDead(false);
        }
        instance.onEntityRemoved(entityForUnloading);
    }
}
