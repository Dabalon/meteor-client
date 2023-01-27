package meteor.plugins.cakeyoinker

import dev.hoot.api.commons.Rand
import dev.hoot.api.movement.Movement
import eventbus.events.GameTick
import meteor.api.Items
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import net.runelite.api.GameState
import net.runelite.api.InventoryID
import net.runelite.api.ItemID
import net.runelite.api.coords.WorldPoint

@PluginDescriptor(name = "Cake Yoinker", description = "Steals Cakes for you in Ardy", enabledByDefault = false)
class CakeYoinker : Plugin() {

    val startPoint = WorldPoint(2669, 3310, 0)
    val objects = meteor.api.Objects
    var tickTimer = 0

    override fun onGameTick(it: GameTick) {
        val cakes = Items.getCount("Cake")
        return when {
            client.gameState != GameState.LOGGED_IN -> {
            }
            tickTimer > 0 -> {
                tickTimer--
                return
            }
            Items.inventoryContains(ItemID.BREAD, ItemID.CHOCOLATE_SLICE) -> dropUseless()
            client.localPlayer?.distanceTo(startPoint) != 0 && cakes == 0 -> walkTo()
            cakes < 28 -> yoinkCake()
            cakes == 28 && !bankOpen() && client.localPlayer?.isIdle == true -> useBank()
            bankOpen() -> depositCake()
            else -> return
        }
    }

    private fun bankOpen(): Boolean {
        return client.getItemContainer(InventoryID.BANK) != null
    }
    fun dropUseless() {
        val item = Items.getFirst(ItemID.BREAD, ItemID.CHOCOLATE_SLICE)
        item?.interact("Drop")
    }
    fun walkTo(){
        Movement.walkTo(startPoint)
        tickTimer = Rand.nextInt(3,5)
    }
    fun depositCake(){
        val cake = Items.getFirst(ItemID.CAKE)
        if (cake != null) {
            Items.deposit(cake,28)
        }
    }
    fun yoinkCake(){
        val cakeStall = objects.getFirst("Baker's stall")
        if (client.localPlayer?.distanceTo(cakeStall?.worldLocation) == 2)
        cakeStall?.interact("Steal-from")
    }
    fun useBank(){
        val bank = objects.getFirst("Bank booth")
        bank?.interact("Bank")
    }
}