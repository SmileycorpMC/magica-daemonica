package net.smileycorp.magiadaemonica.common.rituals;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.smileycorp.magiadaemonica.client.rituals.RitualsClient;

public interface Rituals {

    static Rituals get(World world) {
        return world.isRemote ? RitualsClient.getInstance() : RitualsServer.get((WorldServer) world);
    }

    Ritual getRitual(BlockPos pos);

    Ritual getRitual(double x, double y, double z, double range);

    void addRitual(Ritual ritual);

    void removeRitual(BlockPos pos);

    void tick();

}
