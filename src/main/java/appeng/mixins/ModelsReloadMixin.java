package appeng.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.profiling.ProfilerFiller;

import appeng.hooks.ModelsReloadCallback;

@Mixin(ModelManager.class)
public class ModelsReloadMixin {
    @Inject(method = "apply(Lnet/minecraft/client/resources/model/ModelManager$ReloadState;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;replaceCache(Ljava/util/Map;)V"))
    public void onGetBakedModelMap(ModelManager.ReloadState reloadState, ProfilerFiller profilerFiller,
            CallbackInfo callbackInfo) {
        ModelsReloadCallback.EVENT.invoker().onModelsReloaded(reloadState.modelBakery().getBakedTopLevelModels());
    }
}
