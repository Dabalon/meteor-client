package meteor.plugins.seaweedcooker

import dev.hoot.api.input.Keyboard
import dev.hoot.api.widgets.Dialog
import dev.hoot.api.widgets.Widgets
import eventbus.events.GameTick
import meteor.api.Items
import meteor.api.NPCs
import meteor.api.Objects
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import net.runelite.api.InventoryID
import net.runelite.api.ItemID
import net.runelite.api.NpcID
import net.runelite.api.ObjectID

@PluginDescriptor(name = "Seaweed Cooker", description = "", enabledByDefault = false)
class SeaweedCooker  : Plugin()   {

    override fun onGameTick(it: GameTick) {
        val sodaAsh = Items.getCount(ItemID.SODA_ASH)
        val seaweed = Items.getCount(ItemID.SEAWEED)
        return when {
            seaweed == 0 && !bankOpen() -> useBank()
            seaweed == 0 && sodaAsh == 0 && bankOpen() -> withdrawSeaweed()
            seaweed == 28 && bankOpen() -> cookFire()
            Widgets.get(270, 13) != null && !bankOpen() -> Keyboard.type(1)
            seaweed != 0 && Dialog.canLevelUpContinue() -> cookFire()
            sodaAsh == 28 && bankOpen() -> depositInv()
            else -> return
        }
    }

    private fun withdrawSeaweed() {
        Items.getFirst(ItemID.SEAWEED, container = InventoryID.BANK)?.let {
            Items.withdraw(it, 28)
        }
    }
    private fun depositInv() {
        println("deposit all")
        client.invokeMenuAction(
            "Deposit inventory",
            "",
            1,
            57,
            -1,
            786474
        )
    }
    fun useBank() {
        NPCs.getFirst(NpcID.EMERALD_BENEDICT)?.interact("Bank")
    }
    private fun cookFire() {
        Objects.getFirst(ObjectID.FIRE_43475)?.interact("Cook")
    }
    private fun bankOpen(): Boolean {
        return client.getItemContainer(InventoryID.BANK) != null
    }
}