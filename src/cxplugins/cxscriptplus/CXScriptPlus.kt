package cxplugins.cxscriptplus
import cxplugins.cxfundamental.minecraft.server.CXItemStack
import cxplugins.cxfundamental.minecraft.server.CXPluginMain
import org.bukkit.Bukkit
import org.bukkit.Material


class CXScriptPlus: CXPluginMain(CXItemStack(Material.EMPTY_MAP,1,"&3&lCXScriptPlus","&3&lCXScriptPlus�ṩ��һ�����õĽű�ִ�е��淨")) {
    companion object{
        lateinit var plugin:CXPluginMain

    }
    override fun onEnable() {
        registerAllCommands()
        plugin=this
        Bukkit.getPluginManager().registerEvents(Listeners(),this)
    }
}
