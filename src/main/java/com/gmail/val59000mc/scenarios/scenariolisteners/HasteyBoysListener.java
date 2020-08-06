package com.gmail.val59000mc.scenarios.scenariolisteners;

import com.gmail.val59000mc.scenarios.ScenarioListener;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class HasteyBoysListener extends ScenarioListener{

    @EventHandler
    public void onPlayerCraft(CraftItemEvent e){
        ItemStack item = e.getCurrentItem();

        try {
            // Firestarter start :: alter enchantments
            item.addEnchantment(Enchantment.DIG_SPEED,5);
            item.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS,1);
            // Firestarter end
        }catch (IllegalArgumentException ex){
            // Nothing
        }

    }

}