package meteor.plugins.bowfletcher

import meteor.config.legacy.Config
import meteor.config.legacy.ConfigGroup
import meteor.config.legacy.ConfigItem
import net.runelite.api.ItemID

@ConfigGroup("bowfletcher")
interface BowFletcherConfig : Config {
    @ConfigItem(
        keyName = "methodType",
        name = "Method",
        description = "Choose which method to do",
        position = 1
    )
    fun type() : FletchingType{
        return FletchingType.Cut_Logs
    }
    @ConfigItem(
        keyName = "bowType",
        name = "Bow Type",
        description = "Choose which bow to fletch",
        position = 2
    )
    fun longbows() : Longbows{
        return Longbows.Regular
    }

    enum class FletchingType (val message : String){
        Cut_Logs("cutting logs"), String_Bow ("stinging bows")
    }

    enum class Longbows (val bowID : Int, val logID : Int, val finishedBowID : Int) {
        Regular(ItemID.LONGBOW_U, ItemID.LOGS, ItemID.LONGBOW),
        Oak(ItemID.OAK_LONGBOW_U, ItemID.OAK_LOGS, ItemID.OAK_LONGBOW),
        Willow(ItemID.WILLOW_LONGBOW_U, ItemID.WILLOW_LOGS, ItemID.WILLOW_LONGBOW),
        Maple(ItemID.MAPLE_LONGBOW_U, ItemID.MAPLE_LOGS, ItemID.MAPLE_LONGBOW),
        Yew(ItemID.YEW_LONGBOW_U, ItemID.YEW_LOGS, ItemID.YEW_LONGBOW),
        Magic(ItemID.MAGIC_LONGBOW_U, ItemID.MAGIC_LOGS, ItemID.MAGIC_LONGBOW)
    }
}