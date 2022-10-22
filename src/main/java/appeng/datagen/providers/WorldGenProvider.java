package appeng.datagen.providers;

import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricBuiltinRegistriesProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;

import appeng.core.AppEng;

/**
 * Based on {@link net.minecraft.data.worldgen.BuiltinRegistriesDatapackGenerator}
 */
public class WorldGenProvider implements DataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(FabricBuiltinRegistriesProvider.class);

    private final PackOutput output;

    public WorldGenProvider(PackOutput output) {
        this.output = output;
    }

    public void run(CachedOutput writer) {
        RegistryAccess dynamicRegistryManager = BuiltinRegistries.createAccess();
        DynamicOps<JsonElement> dynamicOps = RegistryOps.create(JsonOps.INSTANCE, dynamicRegistryManager);
        RegistryDataLoader.WORLDGEN_REGISTRIES
                .forEach((info) -> this.writeRegistryEntries(writer, dynamicRegistryManager, dynamicOps, info));
    }

    private <T> void writeRegistryEntries(CachedOutput writer, RegistryAccess registryManager,
            DynamicOps<JsonElement> ops, RegistryDataLoader.RegistryData<T> registryData) {
        var registryKey = registryData.key();
        var registry = registryManager.registryOrThrow(registryKey);
        var pathResolver = this.output.createPathProvider(PackOutput.Target.DATA_PACK,
                registryKey.location().getPath());

        for (var regEntry : registry.entrySet()) {
            var key = regEntry.getKey();

            if (!key.location().getNamespace().equals(AppEng.MOD_ID)) {
                continue;
            }

            var path = pathResolver.json(key.location());
            writeToPath(path, writer, ops, registryData.elementCodec(), regEntry.getValue());
        }
    }

    private static <E> void writeToPath(Path path, CachedOutput cache, DynamicOps<JsonElement> json, Encoder<E> encoder,
            E value) {
        try {
            var optional = encoder.encodeStart(json, value).resultOrPartial((error) -> {
                LOGGER.error("Couldn't serialize element {}: {}", path, error);
            });

            if (optional.isPresent()) {
                DataProvider.saveStable(cache, optional.get(), path);
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't save element {}", path, e);
        }
    }

    public String getName() {
        return "AE2 Worldgen";
    }
}
