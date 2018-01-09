package se.gory_moon.chargers.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.gory_moon.chargers.tile.TileEntityWirelessCharger;

public class MessageUpdatePower implements IMessage {

    private BlockPos pos;
    private int power;

    public MessageUpdatePower() {
    }

    public MessageUpdatePower(TileEntityWirelessCharger tile) {
        this.pos = tile.getPos();
        this.power = tile.storage.getEnergyStored();
    }

    @Override
    public final void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBlockPos(pos);
        buffer.writeInt(power);
    }

    @Override
    public final void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        pos = buffer.readBlockPos();
        power = buffer.readInt();
    }

    @SideOnly(Side.CLIENT)
    public void handle(MessageContext ctx) {
        World world = FMLClientHandler.instance().getWorldClient();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWirelessCharger) {
            TileEntityWirelessCharger tile = (TileEntityWirelessCharger) te;
            tile.storage.setEnergy(power);
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}
