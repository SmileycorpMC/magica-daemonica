package net.smileycorp.magiadaemonica.common.command;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.smileycorp.magiadaemonica.common.Constants;
import net.smileycorp.magiadaemonica.common.capabilities.DaemonicaCapabilities;
import net.smileycorp.magiadaemonica.common.capabilities.Soul;
import net.smileycorp.magiadaemonica.common.network.SyncSoulMessage;

import javax.annotation.Nullable;
import java.util.List;

public class CommandSoul extends CommandBase {

    @Override
    public String getName() {
        return "soul";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return "/soul <player> <set¦consume¦get>";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        if (args.length == 2) return getListOfStringsMatchingLastWord(new String[]{"set", "consume", "get"});
        return Lists.newArrayList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        for (EntityPlayerMP player : getPlayers(server, sender, args[0])) server.addScheduledTask(() -> {
            try {
                String command = args[1];
                //:skull:
                if (!player.hasCapability(DaemonicaCapabilities.SOUL, null)) return;
                Soul soul = player.getCapability(DaemonicaCapabilities.SOUL, null);
                if (command.equals("get")) {
                    notifyCommandListener(sender, this, "command." + Constants.MODID + ".soul.get.success", player.getDisplayName(), soul.getSoul());
                    return;
                }
                float value = Float.parseFloat(args[2]);
                float original = soul.getSoul();
                if (command.equals("set")) soul.setSoul(value);
                else if (command.equals("consume"))
                    soul.consumeSoul(value, args.length > 3 ? Boolean.getBoolean(args[3]) : false);
                else {
                    notifyCommandListener(sender, this, "command" + Constants.MODID + ".soul.failure", command);
                    return;
                }
                SyncSoulMessage.send(player);
                notifyCommandListener(sender, this, "command." + Constants.MODID + ".soul.change.success", player.getDisplayName(), original, soul.getSoul());
            } catch (Exception e) {}
        });
    }

}
