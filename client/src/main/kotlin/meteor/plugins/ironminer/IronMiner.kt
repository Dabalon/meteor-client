package meteor.plugins.ironminer

import eventbus.events.GameTick
import meteor.api.Items
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import net.runelite.api.GameState
import net.runelite.api.InventoryID
import net.runelite.api.ObjectID
import net.runelite.api.coords.WorldPoint

@PluginDescriptor(
    name = "Iron Miner", description = "", enabledByDefault = false
)
class IronMiner : Plugin() {
    val objects = meteor.api.Objects
    override fun onGameTick(it: GameTick) {
        val isIdle = client.localPlayer?.isIdle
        return when{
            client.gameState != GameState.LOGGED_IN -> {
            }
            !isFull() && isIdle == true && client.localPlayer?.distanceTo(WorldPoint(3013,9718,0)) == 0 -> mineRock1()
            !isFull() && isIdle == true && client.localPlayer?.distanceTo(WorldPoint(3020,9720,0)) == 0 && objects.getFirst(ObjectID.ROCKS_11390) == null -> mineRock2()
            !isFull() && isIdle == true && client.localPlayer?.distanceTo(WorldPoint(3020,9720,0)) == 0 && objects.getFirst(ObjectID.ROCKS_11391) == null -> mineRock1()
            isFull() && isIdle == true &&client.localPlayer?.distanceTo(WorldPoint(3020,9720,0)) == 0 -> useBank()
            isFull() && bankOpen() -> depositInv()
            else -> return
        }
    }
    private fun isFull(): Boolean {
        return Items.isFull()
    }
    private fun mineRock1(){
        println("mining rock 1")
        client.invokeMenuAction(
            "Mine",
            "Rocks",
            11365,
            3,
            61,
            56
        )
    }
    private fun mineRock2(){
        println("mining rock 2")
        client.invokeMenuAction(
            "Mine",
            "Rocks",
            11364,
            3,
            60,
            57
        )
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
    private fun bankOpen(): Boolean {
        return client.getItemContainer(InventoryID.BANK) != null
    }
    private fun useBank() {
        val bank = objects.getFirst("Bank chest")
        bank?.interact("Use")
    }
}