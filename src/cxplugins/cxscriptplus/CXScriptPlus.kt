package cxplugins.cxscriptplus
import cxplugins.cxfundamental.minecraft.file.CXYamlConfiguration
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
        threadDaemon=object:Thread(){
            override fun run() {
                sleep(1000)
                for(i in threadPool.indices){
                    val thread= threadPool[i]
                    if(!thread.isAlive){
                        threadPool.removeAt(i)
                    }
                }
            }
        }
        threadDaemon.start()
        var autoStartupScriptConfiguration=CXYamlConfiguration("CXPlugins\\CXScriptPlus","auto.yml")
        var list=autoStartupScriptConfiguration.getStringList("autoStartOnEnable")
        for(scriptName in list){
            if(!isScriptExist(scriptName)){
                continue
            }
            executeScript(null, getScriptByName(scriptName)!!)
        }
        this.noticePrefix="&4&l[错误]".toColor()
        registerAllCommands()
        plugin=this
        Bukkit.getPluginManager().registerEvents(Listeners(),this)
    }

    override fun onDisable() {
        super.onDisable()
        threadDaemon.stop()

        for(thread in threadPool){
            thread.stop()
        }
        for(i in threadPool.indices){
            threadPool.removeAt(i)
        }
    }
}
val threadPool=ArrayList<Thread>()
var threadDaemon=object:Thread(){
    override fun run() {
        sleep(1000)
        for(thread in threadPool){
            if(!thread.isAlive){
                threadPool.remove(thread)
            }
        }
    }
}
