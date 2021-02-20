package cxplugins.cxscriptplus

import cxplugins.cxfundamental.minecraft.file.CXYamlConfiguration
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.HashMap

var runtimingScripts=Pair(Vector<String>(), Vector<Long>())
private fun <E> List<E>.toArrayList():ArrayList<E>{
    val result=ArrayList<E>()
    for(element in this){
        result.add(element)
    }
    return result
}
private fun <E> List<E>.toVector():Vector<E>{
    val result=Vector<E>()
    for(element in this){
        result.add(element)
    }
    return result
}
fun getAllTimingScripts(){
    val configuration=CXYamlConfiguration("CXPlugins\\CXScriptPlus","runTimingScripts.yml")
    val times=configuration.getLongList("times")
    val scriptNames=configuration.getStringList("scriptNames")
    runtimingScripts=Pair(scriptNames.toVector(),times.toVector())
}
fun saveAllTimingScripts(){
    val configuration=CXYamlConfiguration("CXPlugins\\CXScriptPlus","runTimingScripts.yml")
    configuration.set("times", runtimingScripts.second)
    configuration.set("scriptNames", runtimingScripts.first)
    configuration.save()
}
class TimeCheckThread:BukkitRunnable(){
    private var tickCounter=0
    override fun run() {
        tickCounter++
        val time= Date().time
        val indicesToRemove=Vector<Int>()
        for(i in runtimingScripts.first.indices){
            if(runtimingScripts.second[i]<=time){
                executeScript(null,getScriptByName(runtimingScripts.first[i])!!)
                indicesToRemove.add(i)
            }
        }
        for(i in indicesToRemove){
            runtimingScripts.first.removeAt(i)
            runtimingScripts.second.removeAt(i)
        }
        if(tickCounter%6000==0){
            saveAllTimingScripts()
        }
    }

}
lateinit var timeCheckThread:TimeCheckThread