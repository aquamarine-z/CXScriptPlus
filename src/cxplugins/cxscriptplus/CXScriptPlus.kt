package cxplugins.cxscriptplus
import cxplugins.cxfundamental.minecraft.file.CXYamlConfiguration
import cxplugins.cxfundamental.minecraft.kotlindsl.toColor
import cxplugins.cxfundamental.minecraft.server.CXItemStack
import cxplugins.cxfundamental.minecraft.server.CXPluginMain
import org.bukkit.Bukkit
import org.bukkit.Material
import java.util.*
import kotlin.collections.ArrayList


class CXScriptPlus: CXPluginMain(CXItemStack(Material.EMPTY_MAP,1,"&3&lCXScriptPlus","&3&lCXScriptPlus提供了一个更好的脚本执行的玩法")) {

    companion object{
        lateinit var plugin:CXPluginMain

    }

    override fun onEnable() {
        runtimingScripts=Pair(Vector<String>(),Vector<Long>())
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
        getAllTimingScripts()


        timeCheckThread = TimeCheckThread()



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
        timeCheckThread.runTaskTimerAsynchronously(plugin,0,1)
        Bukkit.getPluginManager().registerEvents(Listeners(),this)
    }

    override fun onDisable() {
        super.onDisable()
        threadDaemon.stop()
        saveAllTimingScripts()
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
