package elec332.core.network.packets;

import elec332.core.ElecCore;
import elec332.core.api.network.IExtendedMessageContext;
import elec332.core.util.NBTBuilder;
import elec332.core.world.WorldHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by Elec332 on 4-9-2015.
 */
public abstract class AbstractPacketTileAction extends AbstractPacket {

    public AbstractPacketTileAction() {
    }

    public AbstractPacketTileAction(TileEntity tile, NBTTagCompound message) {
        this(tile, message, 0);
    }

    public AbstractPacketTileAction(TileEntity tile, NBTTagCompound message, int id) {
        super(new NBTBuilder().setTag("data", message).setInteger("id", id).setBlockPos(tile.getPos()).serializeNBT());
    }

    @Override
    public void onMessageThreadSafe(AbstractPacket message, IExtendedMessageContext ctx) {
        NBTBuilder tag = new NBTBuilder(message.networkPackageObject);
        BlockPos loc = tag.getBlockPos();
        int i = tag.getInteger("id");
        NBTTagCompound data = tag.getTag("data");
        World world;
        if (ctx.getSide() == LogicalSide.SERVER) {
            world = ctx.getWorld();
        } else {
            world = ElecCore.proxy.getClientWorld();
        }
        processPacket(world, WorldHelper.getTileAt(world, loc), i, data, ctx);
    }

    public abstract void processPacket(World world, TileEntity tile, int id, NBTTagCompound message, IExtendedMessageContext ctx);

}
