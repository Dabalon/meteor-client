package meteor.plugins.bowfletcher

import dev.hoot.api.input.Keyboard
import dev.hoot.api.widgets.Dialog
import dev.hoot.api.widgets.Widgets
import eventbus.events.GameTick
import eventbus.events.WidgetLoaded
import meteor.api.Items
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import meteor.plugins.bowfletcher.BowFletcherConfig.FletchingType
import net.runelite.api.GameState
import net.runelite.api.InventoryID
import net.runelite.api.ItemID
import net.runelite.api.MenuAction
import net.runelite.api.widgets.WidgetID

@PluginDescriptor(name = "Bow Fletcher", description = "", enabledByDefault = false)
class BowFletcher : Plugin()   {
    val objects = meteor.api.Objects
    val config = configuration<BowFletcherConfig>()
    var tickTimer = 0

    override fun onGameTick(it: GameTick) {

        val logs = Items.getCount(config.longbows().logID)
        val knife = Items.getCount(ItemID.KNIFE)
        val bows = Items.getCount(config.longbows().bowID)
        val finishedBows = Items.getCount(config.longbows().finishedBowID)
        val bowstring = Items.getCount(ItemID.BOW_STRING)

        return when {
                client.gameState != GameState.LOGGED_IN -> {
            }
                tickTimer > 0 -> {
                tickTimer--
                return
            }
            config.type() == FletchingType.Cut_Logs && logs == 0 && !bankOpen() -> useBank()
            config.type() == FletchingType.Cut_Logs && knife == 0 && bankOpen() -> withdrawKnife()
            config.type() == FletchingType.Cut_Logs && logs == 0 && bows == 0 && knife == 1 && bankOpen() && !Dialog.isEnterInputOpen()->
                withdrawLogs()
            config.type() == FletchingType.Cut_Logs && Dialog.isEnterInputOpen() -> Keyboard.type("27", true)
            config.type() == FletchingType.Cut_Logs && logs > 1 && knife == 1 && bankOpen() -> closeBank()
            config.type() == FletchingType.Cut_Logs && logs > 1 && !bankOpen() && Widgets.get(270, 13) == null && client.localPlayer?.isIdle == true -> fletchLog()
            config.type() == FletchingType.Cut_Logs && Widgets.get(270, 13) != null -> Keyboard.type(3)
            config.type() == FletchingType.Cut_Logs && Dialog.canLevelUpContinue() -> fletchLog()
            config.type() == FletchingType.Cut_Logs && bows > 1 && bankOpen() -> depositBows()
            config.type() == FletchingType.String_Bow && bowstring == 0 && !bankOpen() -> useBank()
            config.type() == FletchingType.String_Bow && finishedBows == 0 && bows == 0 && bankOpen() && !Dialog.isEnterInputOpen() -> {
                withdrawBows().also { withdrawBowstring()}
            }
            config.type() == FletchingType.String_Bow && Dialog.isEnterInputOpen() -> Keyboard.type("14", true)
            config.type() == FletchingType.String_Bow && bowstring > 1 && bows > 1 && bankOpen() -> closeBank()
            config.type() == FletchingType.String_Bow && Dialog.canLevelUpContinue() -> stringBow()
            config.type() == FletchingType.String_Bow && bows == 14 && !bankOpen() && bowstring == 14 ->
                stringBow().also { tickTimer = 3 }
            config.type() == FletchingType.String_Bow && finishedBows > 1 && bankOpen() -> depositFinishedBows()
            else -> return
        }
    }

    override fun onWidgetLoaded(it: WidgetLoaded) {
        val groupId: Int = it.groupId
        val bowstring = Items.getCount(ItemID.BOW_STRING)
        if (groupId == WidgetID.MULTISKILL_MENU_GROUP_ID && bowstring >= 1){
            Keyboard.type(1)
        }
    }
    private fun closeBank() {
        client.invokeMenuAction(
            "Close",
            null,
            1,
            MenuAction.WIDGET_CLOSE.id,
            11,
            786434
        )
    }
    private fun withdrawKnife() {
        Items.getFirst(ItemID.KNIFE, container = InventoryID.BANK)?.let {
            Items.withdraw(it, 1)
        }
    }
    private fun withdrawLogs() {
        Items.getFirst(config.longbows().logID, container = InventoryID.BANK)?.let {
            Items.withdraw(it, 27)
        }
    }
    private fun withdrawBows() {
        Items.getFirst(config.longbows().bowID, container = InventoryID.BANK)?.let {
            Items.withdraw(it, 14)
        }
    }
    fun withdrawBowstring() {
        Items.getFirst(ItemID.BOW_STRING, container = InventoryID.BANK)?.let {
            Items.withdraw(it, 14)
        }
    }
    fun stringBow(){
        Items.getFirst(ItemID.BOW_STRING)?.useOn(Items.getFirst(config.longbows().bowID)!!)
    }
    fun fletchLog() {
        Items.getFirst(ItemID.KNIFE)?.useOn(Items.getFirst(config.longbows().logID)!!)
    }
    fun useBank() {
        objects.getFirst("Bank chest")?.interact("Use")
        objects.getFirst("Bank booth")?.interact("Bank")
    }
    private fun bankOpen(): Boolean {
        return client.getItemContainer(InventoryID.BANK) != null
    }
    fun depositBows() {
        Items.getFirst(config.longbows().bowID)?.let {
            Items.deposit(it, 27)
        }
    }
        fun depositFinishedBows() {
            Items.getFirst(config.longbows().finishedBowID)?.let {
                Items.deposit(it, 14)
            }
        }
}