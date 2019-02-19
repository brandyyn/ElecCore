package elec332.core.world;

import elec332.core.ElecCore;
import elec332.core.util.FMLHelper;
import elec332.core.util.ItemStackHelper;
import elec332.core.util.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 20-3-2015.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class WorldHelper {

    /**
     * Flags are as follows:
     * <p>
     * 1 will cause a block update.
     * 2 will send the change to clients.
     * 4 will prevent the block from being re-rendered.
     * 8 will force any re-renders to run on the main thread instead
     * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
     * 32 will prevent neighbor reactions from spawning drops.
     * 64 will signify the block is being moved.
     * <p>
     * Flags can be OR-ed
     */
    public static final int PLACEBLOCK_NOTHING = 0;
    public static final int PLACEBLOCK_UPDATE = 1;
    public static final int PLACEBLOCK_SENDCHANGE = 2;
    public static final int PLACEBLOCK_NO_RERENDER = 4;
    public static final int PLACEBLOCK_RENDERMAIN = 8;
    public static final int PLACEBLOCK_NO_NEIGHBOR_REACTION = 16;
    public static final int PLACEBLOCK_NO_NEIGHBOR_REACTION_DROPS = 32;
    public static final int PLACEBLOCK_BLOCK_BEING_MOVED = 64;

    /**
     * Gets the biome types opf the provided biome
     *
     * @param biome The biome
     * @return The biome types opf the provided biome
     */
    public static Set<BiomeDictionary.Type> getTypes(Biome biome) {
        return BiomeDictionary.getTypes(biome);
    }

    /**
     * Gets the biome at the specified location
     *
     * @param world The world in which the position is located
     * @param pos   The position
     * @return The biome at the specified location
     */
    public static Biome getBiome(World world, BlockPos pos) {
        return world.getBiome(pos);
    }

    /**
     * Spawns an entity in the specified world
     *
     * @param world  The world in which to spawn the entity
     * @param entity The entity to be spawned
     * @return Whether the entity has been spawned in the world
     */
    public static boolean spawnEntityInWorld(IWorldWriter world, Entity entity) {
        return world.spawnEntity(entity);
    }

    /*
     * Whether the provided block can be placed at the specified location
     *
     * @param world              The world
     * @param block              The block to be placed
     * @param pos                The position that has been clicked (pos + facing offset = block location)
     * @param skipCollisionCheck Whether to skip collision checking
     * @param facing             The facing the block will be placed at
     * @param entity             The entity placing the block
     * @return Whether the block can actually be placed at the specified location
     *
    public static boolean canBlockBePlaced(World world, Block block, BlockPos pos, boolean skipCollisionCheck, EnumFacing facing, @Nullable Entity entity) {
        return world.mayPlace(block, pos, skipCollisionCheck, facing, entity);
    }*/

    /**
     * Notifies neighboring blocks & observers of a state change at the specified location
     *
     * @param world The world in which the position is located
     * @param pos   The position
     * @param block The block that has been changed
     */
    public static void notifyNeighborsOfStateChange(World world, BlockPos pos, Block block) {
        world.notifyNeighborsOfStateChange(pos, block);
    }

    /**
     * Serializes the position in which the specified position is located to a long
     *
     * @param pos The position to be converted
     * @return The serialized long
     */
    public static long chunkLongFromBlockPos(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Serializes the position of the provided4 {@link Chunk} to a long
     *
     * @param chunk The {@link Chunk}
     * @return The serialized long
     */
    public static long longFromChunk(Chunk chunk) {
        return longFromChunkPos(chunk.getPos());
    }

    /**
     * Gets a chunk from the provided {@link ChunkPos}
     *
     * @param world The world to get the chunk from
     * @param pos   The chunk position
     * @return The chunk at the provided position
     */
    public static IChunk getChunk(IWorld world, ChunkPos pos) {
        return world.getChunk(pos.x, pos.z);
    }

    /**
     * Serializes a {@link ChunkPos} to a long
     *
     * @param chunkCoordIntPair The {@link ChunkPos} to be serialized
     * @return The serialized long
     */
    public static long longFromChunkPos(ChunkPos chunkCoordIntPair) {
        return ChunkPos.asLong(chunkCoordIntPair.x, chunkCoordIntPair.z);
    }

    /**
     * Sets the {@link IBlockState} at the provided position to the one given in {@param state}
     * in the provided world
     *
     * @param world The world in which to chenge the {@link IBlockState}
     * @param pos   The pos at which to change the {@link IBlockState}
     * @param state The new {@link IBlockState}
     * @param flags Placement flags, can be {@link WorldHelper#PLACEBLOCK_NOTHING} or any combination of
     *              {@link WorldHelper#PLACEBLOCK_UPDATE}, {@link WorldHelper#PLACEBLOCK_SENDCHANGE}, {@link WorldHelper#PLACEBLOCK_NO_RERENDER}, {@link WorldHelper#PLACEBLOCK_RENDERMAIN},
     *              {@link WorldHelper#PLACEBLOCK_NO_NEIGHBOR_REACTION}, {@link WorldHelper#PLACEBLOCK_NO_NEIGHBOR_REACTION_DROPS}, {@link WorldHelper#PLACEBLOCK_BLOCK_BEING_MOVED}
     *              (flags can be added together)
     */
    public static void setBlockState(IWorldWriter world, BlockPos pos, IBlockState state, int flags) {
        world.setBlockState(pos, state, flags);
    }

    /**
     * Marks the specified location for a block update.
     *
     * @param world The world in which the position is located
     * @param pos   The position
     */
    public static void markBlockForUpdate(World world, BlockPos pos) {
        if (!world.isRemote) {
            ((WorldServer) world).getPlayerChunkMap().markBlockForUpdate(pos);
        } else {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    /**
     * Checks whether the chunk in which the provided coordinate is located is loaded
     *
     * @param world The world in which the position is located
     * @param pos   The position to be checked
     * @return Whether the chunk in which the provided coordinate is located is loaded
     */
    public static boolean chunkLoaded(IWorld world, BlockPos pos) {
        ChunkPos cp = chunkPosFromBlockPos(pos);
        IChunkProvider chunkProvider = world.getChunkProvider();
        boolean b1;
        Chunk chunk = chunkProvider.getChunk(cp.x, cp.z, false, false);
        if (chunk == null) {
            return false;
        }
        if (chunkProvider instanceof ChunkProviderServer) {
            b1 = ((ChunkProviderServer) chunkProvider).chunkExists(cp.x, cp.z);
        } else {
            b1 = chunk instanceof EmptyChunk;
        }
        return b1 && chunk.isLoaded();
    }

    /**
     * Marks the provided position for a render update
     * (The whole chunk in which the position is located will be re-drawn)
     *
     * @param world The world in which the position is located
     * @param pos   The position
     */
    public static void markBlockForRenderUpdate(World world, BlockPos pos) {
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    /**
     * Spawns an explosion at the provided coordinates
     *
     * @param worldObj The world in which to spawn the explosion
     * @param xCoord   The X coordinate
     * @param yCoord   The Y coordinate
     * @param zCoord   The Z coordinate
     * @param force    The force of the explosion
     */
    public static void spawnExplosion(World worldObj, double xCoord, double yCoord, double zCoord, float force) {
        worldObj.createExplosion(null, xCoord, yCoord, zCoord, force * 4, true);
    }

    /**
     * Requests a {@link //ForgeChunkManager.Ticket} for the specified location
     *
     * @param //world       The world in which the position is located
     * @param loc           The position (normal world x,y,z) to be loaded
     * @param //modInstance The mod requesting the ticket
     * @param loc           The position
     * @return The position of the chunk in which the provided position is located
     * @Nullable public static ForgeChunkManager.Ticket requestTicket(World world, BlockPos loc, Object modInstance) {
     * ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(modInstance, world, ForgeChunkManager.Type.NORMAL);
     * if (ticket != null) {
     * NBTBuilder.from(ticket.getModData()).setBlockPos(loc);
     * }
     * return ticket;
     * }
     * <p>
     * /**
     * Gets the position of the chunk in which the provided position is located
     */
    public static ChunkPos chunkPosFromBlockPos(BlockPos loc) {
        return new ChunkPos(loc.getX() >> 4, loc.getZ() >> 4);
    }

    /**
     * Forces a chunk to stay loaded using the provided {@link ForgeChunkManager.Ticket}
     * The chunk in which the provided position is located will be loaded.
     * The position is a {@link BlockPos} serialized as a long in the Ticket's moddata,
     * with as tag name: "position"
     *
     * @param ticket The ticket
     *
    public static void forceChunk(ForgeChunkManager.Ticket ticket) {
    ForgeChunkManager.forceChunk(ticket, chunkPosFromBlockPos(new NBTBuilder(ticket.getModData()).getBlockPos()));
    }*/

    /**
     * Drops all contents of the provided {@link IItemHandler} into the world at the specified coordinates.
     * Adheres to the {@link net.minecraft.world.GameRules}
     * Does _NOT_ clear the inventory!
     *
     * @param world     The world
     * @param pos       The drop location
     * @param inventory The inventory to be dropped
     */
    public static void dropInventoryItems(World world, BlockPos pos, IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (ItemStackHelper.isStackValid(stack)) {
                WorldHelper.dropStack(world, pos, stack);
            }
        }
    }

    /**
     * Drops the provided {@link ItemStack} at the specified coordinates, with a bit of randomness.
     * Adheres to the {@link net.minecraft.world.GameRules}
     *
     * @param world    The world in which to drop the item(s)
     * @param blockLoc The position at which to drop the item(s)
     * @param stack    The {@link ItemStack} to be dropped
     */
    public static boolean dropStack(World world, BlockPos blockLoc, ItemStack stack) {
        return dropStack(world, blockLoc.getX(), blockLoc.getY(), blockLoc.getZ(), stack);
    }

    /**
     * Drops the provided {@link ItemStack} at the specified coordinates, with a bit of randomness.
     * Adheres to the {@link net.minecraft.world.GameRules}
     *
     * @param world     The world in which to drop the item(s)
     * @param x         The X coordinate
     * @param y         The Y coordinate
     * @param z         The Z coordinate
     * @param itemStack The {@link ItemStack} to be dropped
     */
    public static boolean dropStack(World world, int x, int y, int z, ItemStack itemStack) {
        if (!world.isRemote() && world.getGameRules().getBoolean("doTileDrops")) {
            float f = 0.7F;
            double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, itemStack);
            entityitem.setDefaultPickupDelay();
            return WorldHelper.spawnEntityInWorld(world, entityitem);
        }
        return false;
    }

    /**
     * Schedules a block update at the specified location for the next tick.
     *
     * @param world    The world
     * @param blockLoc The position to be updated
     */
    public static void scheduleBlockUpdate(IWorld world, BlockPos blockLoc) {
        scheduleBlockUpdate(world, blockLoc, 1);
    }

    /**
     * Schedules a block update at the specified location in {@param delay} ticks
     *
     * @param world    The world
     * @param blockLoc The position to be updated
     * @param delay    The delay in ticks
     */
    public static void scheduleBlockUpdate(IWorld world, BlockPos blockLoc, int delay) {
        world.getPendingBlockTicks().scheduleTick(blockLoc, getBlockAt(world, blockLoc), delay);
    }

    /**
     * Gets the dimension-ID of the specified world.
     * If the {@link World#dimension} is null, it tries to fetch it
     * from Forge's {@link DimensionManager}
     *
     * @param world The world from which to fetch the dimension-ID
     * @return The dimension-ID of the specified world.
     */
    @SuppressWarnings("all")
    public static DimensionType getDimID(IWorldReaderBase world) {
        if (world == null) {
            throw new IllegalArgumentException("Cannot fetch the Dimension-ID from a null world!");
        }
        if (world.getDimension() == null) {
            return ElecCore.proxy.getServer().forgeGetWorldMap().entrySet().stream()
                    .filter(e -> e.getValue() == world)
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElseThrow(() -> new RuntimeException("Unable to determine the dimension of world: " + world));
        }
        return world.getDimension().getType();
    }

    @Nullable
    public static World getWorld(DimensionType dimension) {
        if (FMLHelper.getLogicalSide() == LogicalSide.CLIENT) {
            World ret = ElecCore.proxy.getClientWorld();
            if (ret.getDimension().getType() != dimension) {
                ret = null;
            }
            return ret;
        }
        return getServerWorldDirect(dimension);
    }

    @SuppressWarnings("all")
    public static WorldServer getServerWorldDirect(DimensionType type) {
        return ElecCore.proxy.getServer().getWorld(type);
    }

    /*
     * Gets the metadata of the block at the specified location
     *
     * @param world    The world
     * @param blockLoc The position
     * @return The metadata of the block at the specified location
     *
    public static int getBlockMeta(IBlockReader world, BlockPos blockLoc) {
        return getBlockMeta(getBlockState(world, blockLoc));
    }

    /**
     * Gets the metadata value of the specified {@link IBlockState}
     *
     * @param state The {@link IBlockState}
     * @return The metadata value of the specified {@link IBlockState}
     *
    public static int getBlockMeta(IBlockState state) {
        return state.getBlock().getMetaFromState(state);
    }*/

    /**
     * Gets the {@link TileEntity} at the specified location
     *
     * @param world The world
     * @param loc   The position
     * @return The {@link TileEntity} at the specified location
     */
    public static TileEntity getTileAt(IBlockReader world, BlockPos loc) {
        return world.getTileEntity(loc);
    }

    /**
     * Gets the {@link Block} at the specified location
     *
     * @param world The world
     * @param loc   The position
     * @return The {@link Block} at the specified location
     */
    public static Block getBlockAt(IBlockReader world, BlockPos loc) {
        return getBlockState(world, loc).getBlock();
    }

    /**
     * Gets the {@link IBlockState} at the specified location
     *
     * @param world The world
     * @param pos   The position
     * @return The {@link IBlockState} at the specified location
     */
    public static IBlockState getBlockState(IBlockReader world, BlockPos pos) {
        return world.getBlockState(pos);
    }

    /**
     * Spawns a lightning bolt at the location the player is looking at.
     *
     * @param player The player
     * @param range  The maximum raytracing range
     */
    public static void spawnLightningAtLookVec(EntityPlayer player, Double range) {
        RayTraceResult position = PlayerHelper.getPosPlayerIsLookingAt(player, range);
        spawnLightningAt(player.getEntityWorld(), position.getBlockPos());
    }

    /**
     * Spawns a lightning bolt at the specified location.
     *
     * @param world    The world
     * @param blockPos The position
     */
    public static void spawnLightningAt(World world, BlockPos blockPos) {
        spawnLightningAt(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    /**
     * Spawns a lightning bolt at the specified x,y,z coordinates.
     *
     * @param world The world
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public static void spawnLightningAt(World world, double x, double y, double z) {
        //world.playSoundEffect(x, y, z,"ambient.weather.thunder", 10000.0F, 0.8F);
        //world.playSoundEffect(x, y, z,"random.explode", 10000.0F, 0.8F);
        world.playSound(x, y, z, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F, true);
        world.playSound(x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.WEATHER, 10000.0F, 0.8F, true);
        WorldHelper.spawnEntityInWorld(world, new EntityLightningBolt(world, x, y, z, false));
    }

}
