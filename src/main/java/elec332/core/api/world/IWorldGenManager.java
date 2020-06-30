package elec332.core.api.world;

import elec332.core.api.registration.IWorldGenRegister;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.ModContainer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Elec332 on 17-10-2016.
 */
public interface IWorldGenManager {

    public void registerBlockChangedHook(IWorldEventHook listener);

    public void registerWorldGenRegistry(IWorldGenRegister worldGenRegistry, Object owner);

    public void registerWorldGenRegistry(IWorldGenRegister worldGenRegistry, ModContainer owner);

    public IBiomeGenWrapper getBiomeRegister(Biome biome);

    public Map<String, Structure<?>> getRegisteredStructures();

    /**
     * Enqueues one or multiple IFeatureGenerators for retrogen.
     * The WorldGenManager will process this request as soon as possible,
     * without causing any tick issues when a lot of IFeatureGenerators are
     * enqueued at once.
     *
     * @param world             The world the chunk is located in
     * @param chunk             The position of the chunk
     * @param featureGenerators The IFeatureGenerators to register
     */
    public void registerForRetroGen(@Nonnull IWorld world, @Nonnull ChunkPos chunk, ILegacyFeatureGenerator... featureGenerators);

    /**
     * Enqueues one or multiple IFeatureGenerators for retrogen.
     * The WorldGenManager will process this request as soon as possible,
     * without causing any tick issues when a lot of IFeatureGenerators are
     * enqueued at once.
     *
     * @param world             The world the chunk is located in
     * @param chunk             The position of the chunk
     * @param featureGenerators The IFeatureGenerators to register
     */
    public void registerForRetroGen(@Nonnull IWorld world, @Nonnull ChunkPos chunk, Collection<ILegacyFeatureGenerator> featureGenerators);


    /**
     * Registers a FeatureGenerator.
     *
     * @param featureGenerator The Feature Manager to register
     * @return Whether the FeatureGenerator was successfully registered.
     */
    public boolean register(ILegacyFeatureGenerator featureGenerator);

    /**
     * Registers a ChunkPopulator.
     *
     * @param chunkPopulator The Chunk Populator to register
     * @return Whether the ChunkPopulator was successfully registered.
     */
    public boolean register(IAdvancedChunkPopulator chunkPopulator);

    /**
     * Registers a WorldGenHook.
     *
     * @param worldGenHook The WorldGen Hook to register
     * @return Whether the WorldGenHook was successfully registered.
     */
    public boolean register(IWorldGenHook worldGenHook);

    /**
     * Registers a Chunk IO hook.
     *
     * @param chunkIOHook The Chunk IO Hook to register
     * @return Whether the Chunk IO Hook was successfully registered.
     */
    public boolean register(IChunkIOHook chunkIOHook);

}
