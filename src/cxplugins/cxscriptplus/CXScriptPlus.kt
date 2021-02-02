package cxplugins.cxscriptplus
import cxplugins.cxfundamental.minecraft.kotlindsl.toColor
import cxplugins.cxfundamental.minecraft.server.CXItemStack
import cxplugins.cxfundamental.minecraft.server.CXPluginMain
import org.bukkit.Bukkit
import org.bukkit.Material


class CXScriptPlus: CXPluginMain(CXItemStack(Material.EMPTY_MAP,1,"&3&lCXScriptPlus","&3&lCXScriptPlus提供了一个更好的脚本执行的玩法")) {
    companion object{
        lateinit var plugin:CXPluginMain

    }
    override fun onEnable() {
        this.noticePrefix="&4&l[错误]".toColor()
        registerAllCommands()
        plugin=this
        Bukkit.getPluginManager().registerEvents(Listeners(),this)
    }
}
