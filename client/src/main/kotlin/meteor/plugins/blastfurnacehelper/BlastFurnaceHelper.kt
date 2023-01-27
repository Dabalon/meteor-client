package meteor.plugins.blastfurnacehelper

import dev.hoot.api.input.Keyboard
import dev.hoot.api.widgets.Dialog
import dev.hoot.api.widgets.Widgets
import eventbus.events.GameTick
import meteor.api.Items
import meteor.api.Objects
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import net.runelite.api.InventoryID
import net.runelite.api.ItemID

@PluginDescriptor(name = "Cannonball maker", description = "", enabledByDefault = false)
class BlastFurnaceHelper  : Plugin()   {
    override fun onGameTick(it: GameTick) {
        val steelBar = Items.getCount(ItemID.STEEL_BAR)
        return when {
            steelBar == 0 && !bankOpen() -> useBank()
            steelBar == 0 && bankOpen() -> withdrawBars()
            steelBar == 26 && bankOpen() -> useFurnace()
            Widgets.get(270, 13) != null && !bankOpen() -> Keyboard.type(1)
            steelBar != 0 && Dialog.canLevelUpContinue() -> useFurnace()
            else -> return
        }
    }

    private fun withdrawBars() {
        Items.getFirst(ItemID.STEEL_BAR, container = InventoryID.BANK)?.let {
            Items.withdraw(it, 26)
        }
    }
    fun useFurnace() {
        Objects.getFirst("Furnace")?.interact("Smelt")
    }
    fun useBank() {
        Objects.getFirst("Bank chest")?.interact("Use")
        Objects.getFirst("Bank booth")?.interact("Bank")
    }
    private fun bankOpen(): Boolean {
        return client.getItemContainer(InventoryID.BANK) != null
    }
}