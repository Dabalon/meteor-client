package meteor.plugins.flaxspinner

import dev.hoot.api.input.Keyboard
import dev.hoot.api.widgets.Dialog
import dev.hoot.api.widgets.Widgets
import eventbus.events.GameTick
import meteor.api.Items
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import net.runelite.api.*
import net.runelite.api.coords.WorldPoint
import net.runelite.api.queries.GameObjectQuery

@PluginDescriptor(
    name = "Flax Spinner", description = "Spins flax at fossil island and banks it", enabledByDefault = false
)
class FlaxSpinner : Plugin() {
    val objects = meteor.api.Objects
    val spinSpot = WorldPoint(3732, 3822, 0)
    override fun onGameTick(it: GameTick) {
        val flax = Items.getCount("Flax")
        val bowString = Items.getCount("Bow string")
        val isIdle = client.localPlayer?.isIdle
        return when {
            client.gameState != GameState.LOGGED_IN -> {
            }
            Dialog.canLevelUpContinue() -> useSpinningWheel()
            !isFull() && !bankOpen() && isIdle == true -> useNullBank()
            !isFull() && bankOpen() && !Dialog.isEnterInputOpen() -> withdrawFlax()
            Dialog.isEnterInputOpen() -> Keyboard.type("28", true)
            flax == 28 && isIdle == true && client.localPlayer?.distanceTo(spinSpot) != 0 -> useSpinningWheel()
            Widgets.get(270, 13) != null -> Keyboard.type(3)
            bowString == 28 && !bankOpen() && isIdle == true -> useNullBank()
            bowString == 28 && bankOpen() -> depositBowString()

            else -> return
        }
    }

    private fun useSpinningWheel() {
        val obj = getGameObject(31431) ?: return
        client.invokeMenuAction(
            "Spin", null, 31431, 4, obj.sceneMinLocation.x, obj.sceneMinLocation.y
        )
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Clicking the Spinning Wheel.", null)
    }
    private fun isFull(): Boolean {
        return Items.isFull()
    }
    private fun getGameObject(id: Int): GameObject? {
        return GameObjectQuery().idEquals(id).result(client).nearestTo(client.localPlayer)
    }
    private fun useNullBank() {
        val obj = getGameObject(31427) ?: return
        client.invokeMenuAction(
            "Use", null, 31427, 3, obj.sceneMinLocation.x, obj.sceneMinLocation.y
        )
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Clicking the Bank chest.", null)
    }
    private fun withdrawFlax() {
        Items.getFirst(ItemID.FLAX, container = InventoryID.BANK)?.let {
            Items.withdraw(it, 28)
        }
    }
    private fun depositBowString() {
        Items.getFirst(ItemID.BOW_STRING)?.let {
            Items.deposit(it, 28)
        }
    }
    private fun bankOpen(): Boolean {
        return client.getItemContainer(InventoryID.BANK) != null
    }
}