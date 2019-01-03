package elec332.core.handler;

import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.info.IInfoProvider;
import elec332.core.api.info.IInfoProviderEntity;
import elec332.core.api.registry.ISingleObjectRegistry;
import elec332.core.api.registry.ISingleRegister;
import elec332.core.api.registry.SimpleRegistries;
import elec332.core.api.world.IAdvancedChunkPopulator;
import elec332.core.api.world.ILegacyFeatureGenerator;
import elec332.core.api.world.IWorldGenHook;
import elec332.core.grid.IStructureWorldEventHandler;
import elec332.core.util.FMLHelper;

import java.util.function.Predicate;

/**
 * Created by Elec332 on 1-8-2016.
 */
@StaticLoad
@SuppressWarnings("all")
public final class ElecCoreRegistrar {

    private ElecCoreRegistrar() {
        throw new IllegalAccessError();
    }

    public static final ISingleObjectRegistry<IStructureWorldEventHandler> GRIDHANDLERS;
    public static final ISingleObjectRegistry<IInfoProvider> INFORMATION_PROVIDERS;
    public static final ISingleObjectRegistry<IInfoProviderEntity> INFORMATION_PROVIDERS_ENTITY;
    public static final ISingleObjectRegistry<IWorldGenHook> WORLD_GEN_HOOKS;
    public static final ISingleRegister<IAdvancedChunkPopulator> WORLD_POPULATORS;
    public static final ISingleRegister<ILegacyFeatureGenerator> LEGACY_WORLD_FEATURE_GENERATORS;

    static {
        IN_MOD_LOADING = input -> FMLHelper.isInModInitialisation();
        GRIDHANDLERS = SimpleRegistries.newSingleObjectRegistry(ElecCoreRegistrar.IN_MOD_LOADING);
        INFORMATION_PROVIDERS = SimpleRegistries.newSingleObjectRegistry(ElecCoreRegistrar.IN_MOD_LOADING);
        INFORMATION_PROVIDERS_ENTITY = SimpleRegistries.newSingleObjectRegistry(ElecCoreRegistrar.IN_MOD_LOADING);
        WORLD_GEN_HOOKS = (ISingleObjectRegistry<IWorldGenHook>) ElecCoreSetup.worldGenManager;
        WORLD_POPULATORS = chunkPopulator -> ElecCoreSetup.worldGenManager.register(chunkPopulator);
        LEGACY_WORLD_FEATURE_GENERATORS = generator -> ElecCoreSetup.worldGenManager.register(generator);
    }

    private static final Predicate IN_MOD_LOADING;

}
