package meteor.plugins.flaxyoinker

import dev.hoot.api.commons.Rand
import eventbus.events.GameTick
import meteor.api.Items
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import net.runelite.api.GameState
import net.runelite.api.InventoryID
import net.runelite.api.ItemID

@PluginDescriptor(name = "Flax Yoinker", description = "Yoinks flax at lands end and banks it", enabledByDefault = false)
class FlaxYoinker  : Plugin() {
    val objects = meteor.api.Objects
    var tickTimer = 0

    override fun onGameTick(it: GameTick) {
        val flax = Items.getCount("Flax")
        return when {
            client.gameState != GameState.LOGGED_IN -> {
            }
            tickTimer > 0 -> {
                tickTimer--
                return
            }
            flax == 0 && client.localPlayer?.isIdle == true -> yoinkFlax().also { tickTimer = Rand.nextInt(7,9) }
            flax < 28 && client.localPlayer?.isMoving == false -> yoinkFlax()
            flax == 28 && !bankOpen() && client.localPlayer?.isIdle == true -> useBank()
            bankOpen() -> depositFlax()
            else -> return
        }
    }
    private fun bankOpen(): Boolean {
        return client.getItemContainer(InventoryID.BANK) != null
    }
    private fun depositFlax() {
        Items.getFirst(ItemID.FLAX)?.let {
            Items.deposit(it, 28)
        }

    }
        private fun yoinkFlax() {
            val flaxObject = objects.getFirst("Flax")
            flaxObject?.interact("Pick")
        }
        private fun useBank() {
            val bank = objects.getFirst("Bank chest")
            bank?.interact("Use")
        }
}