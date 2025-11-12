package net.smileycorp.magiadaemonica.common.trades.rewards;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.smileycorp.magiadaemonica.common.TradeUtils;

public interface Reward {

    void grant(EntityPlayer player);

    static Reward item(ItemStack stack) {
        return player -> {
            if (player.addItemStackToInventory(stack)) return;;
            EntityItem item = player.dropItem(stack, false);
            if (item == null) return;
            item.setNoPickupDelay();
            item.setOwner(player.getName());
        };
    }

    static Reward attributeModifier(IAttribute attribute, double value) {
        return player -> TradeUtils.addBonusAttribute(player, attribute, value);
    }

}
