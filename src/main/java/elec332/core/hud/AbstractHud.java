package elec332.core.hud;

import elec332.core.hud.position.Alignment;
import elec332.core.hud.position.HorizontalStartingPoint;
import elec332.core.hud.position.IStartingPoint;
import elec332.core.hud.position.VerticalStartingPoint;
import elec332.core.main.ElecCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 13-1-2017.
 */
public abstract class AbstractHud {

    public AbstractHud(@Nonnull Alignment alignment, @Nonnull IStartingPoint horizontal, @Nonnull IStartingPoint vertical){
        MinecraftForge.EVENT_BUS.register(this);
        this.alignment = alignment;
        this.horiz = horizontal;
        this.ver = vertical;
    }

    private Alignment alignment = Alignment.LEFT;
    private IStartingPoint horiz = HorizontalStartingPoint.LEFT, ver = VerticalStartingPoint.MIDDLE;

    public final void configureHud(Configuration config){
        if (config != null) {
            config.load();
            this.alignment = Alignment.valueOf(config.getString("alignment", Configuration.CATEGORY_CLIENT, alignment.toString(), "The alignment for this hud.", a));
            if (!(horiz instanceof HorizontalStartingPoint && ver instanceof VerticalStartingPoint)){
                configureCustom(config, horiz, ver);
            } else {
                horiz = HorizontalStartingPoint.valueOf(config.getString("horizontalPosition", Configuration.CATEGORY_CLIENT, horiz.toString(), "The horizontal position of this hud.", h));
                ver = VerticalStartingPoint.valueOf(config.getString("verticalPosition", Configuration.CATEGORY_CLIENT, ver.toString(), "The vertical position of this hud.", v));
            }
            configure(config);
            if (config.hasChanged()){
                config.save();
            }
        }
    }

    /**
     * If your HUD uses one or more non-default horizontal or vertical starting points, this method will be called,
     * as the system doesn't know how to configure those.
     *
     * @param config The config
     * @param horizontal The (non-default) horizontal starting point
     * @param vertical The (non-default) vertical starting point
     */
    protected void configureCustom(@Nonnull Configuration config, @Nonnull IStartingPoint horizontal, @Nonnull IStartingPoint vertical){
        throw new UnsupportedOperationException();
    }

    /**
     * Use this internally to configure additional HUD settings
     *
     * @param config The config
     */
    protected abstract void configure(@Nonnull Configuration config);

    private static final String[] a, h, v;

    @Nonnull
    protected Alignment getAlignment(){
        return alignment;
    }

    @Nonnull
    protected IStartingPoint getHorizontalStartingPoint(){
        return horiz;
    }

    @Nonnull
    protected IStartingPoint getVerticalStartingPoint(){
        return ver;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public final void onRenderTick(TickEvent.RenderTickEvent event) {
        EntityPlayer player = ElecCore.proxy.getClientPlayer();
        if (player != null && shouldRenderHud(player, event.renderTickTime, event.phase)) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution res = new ScaledResolution(mc);

            int hudHeight = getHudHeight();
            int startX = getHorizontalStartingPoint().getStartingPoint(mc, res, hudHeight);
            int startY = getVerticalStartingPoint().getStartingPoint(mc, res, hudHeight);

            renderHud((EntityPlayerSP) player, ElecCore.proxy.getClientWorld(), getAlignment(), startX, startY, event.renderTickTime);

        }
    }

    protected boolean shouldRenderHud(@Nonnull EntityPlayer player, float partialTicks, TickEvent.Phase phase){
        return phase == TickEvent.Phase.END;
    }

    public abstract int getHudHeight();

    @SideOnly(Side.CLIENT)
    public abstract void renderHud(@Nonnull EntityPlayerSP player, @Nonnull World world, @Nonnull Alignment alignment, int startX, int startY, float partialTicks);

    static {
        a = new String[Alignment.values().length];
        for (int i = 0; i < a.length; i++) {
            a[i] = Alignment.values()[i].toString();
        }
        h = new String[HorizontalStartingPoint.values().length];
        for (int i = 0; i < h.length; i++) {
            a[i] = HorizontalStartingPoint.values()[i].toString();
        }
        v = new String[VerticalStartingPoint.values().length];
        for (int i = 0; i < v.length; i++) {
            a[i] = VerticalStartingPoint.values()[i].toString();
        }
    }

}