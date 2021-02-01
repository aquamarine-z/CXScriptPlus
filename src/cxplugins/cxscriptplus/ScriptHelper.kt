@file:JvmName("ScriptHelper")
package cxplugins.cxscriptplus
import cxplugins.cxfundamental.minecraft.command.CXCommand
import cxplugins.cxfundamental.minecraft.command.CXScript
import cxplugins.cxfundamental.minecraft.file.CXYamlConfiguration
import cxplugins.cxfundamental.minecraft.kotlindsl.*
import cxplugins.plugins.cxpoint.CXEconomy
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

fun isScriptExist(name:String):Boolean{
    var configuration=CXYamlConfiguration("CXPlugins\\CXScriptPlus","scripts.yml")
    return configuration.get("$name.script") != null
}
fun getScriptByName(name:String) : CXScript?{
    var configuration=CXYamlConfiguration("CXPlugins\\CXScriptPlus","scripts.yml")
    return configuration.get("$name.script") as CXScript
}
fun checkCooldown(sender:CommandSender,scriptName:String):Boolean {
    val configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
    if (!configuration.getBoolean("$scriptName.cooldown.enable", false)) return true
    val cooldownType = configuration.getString("$scriptName.cooldown.type", "private")
    if (cooldownType == "private" && sender !is Player) {
        //println("1")
        return true
    }
    else if (cooldownType == "private" && sender is Player) {
        if (!sender.hasData("CXScriptPlus.cooldown.$scriptName")) {
            sender.editData("CXScriptPlus.cooldown.$scriptName", Date().time)
            //println("2")
            return true
        }
        val cooldownTime = configuration.getLong("$scriptName.cooldown.time", 0)
        val lastRunScriptTime = sender.getData("CXScriptPlus.cooldown.$scriptName") as Long
        return if (lastRunScriptTime + cooldownTime <= Date().time) {
            sender.editData("CXScriptPlus.cooldown.$scriptName", Date().time)
            //println("3")
            true
        } else {
            sender.sendMessageWithColor("&2[CXScriptPlus]冷却时间还剩${(lastRunScriptTime + cooldownTime-Date().time).toDouble()/1000}秒")
            false
        }
    }
    else if (cooldownType == "public") {
        return if (!configuration.contains("$scriptName.cooldown.lastRunTime")) {
            configuration.set("$scriptName.cooldown.lastRunTime", Date().time)
            true
        }
        else {
            val cooldownTime = configuration.getLong("$scriptName.cooldown.time", 0)
            val lastRunTime = configuration.getLong("$scriptName.cooldown.lastRunTime")
            if (lastRunTime + cooldownTime <= Date().time) {
                configuration.set("$scriptName.cooldown.lastRunTime", Date().time)
                configuration.save()
                true
            }
            else {
                sender.sendMessageWithColor("&2[CXScriptPlus]冷却时间还剩${(lastRunTime + cooldownTime-Date().time).toDouble()/1000}秒")
                false
            }
        }
    }
    return false
}


fun executeScript(sender: CommandSender, script:CXScript){
    var hasFailedCommand=false
    for(i in script.commands.indices){
        var command=script.commands[i]
        var role=script.roles[i]
        if(command.matches(Regex("run .*"))){
            var scriptName=command.split(" ")[1]
            if(isScriptExist(scriptName)){
                executeScript(sender, getScriptByName(scriptName)!!)
            }
            else{
                sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                sender.sendMessageWithColor("&4问题: run <脚本名> 执行的脚本不存在")
                sender.sendMessageWithColor("&4脚本将停止运行.................")
                return
            }
        }
        else if(command.matches(Regex("costItemInHand .*"))){
            try {
                var amount = command.split(" ")[1].toInt()
                if(sender !is Player){
                    sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                    sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                    sender.sendMessageWithColor("&4问题: costItemInHand <数量> 执行者不是一个玩家")
                    sender.sendMessageWithColor("&4脚本将停止运行.................")
                    return
                }
                if(sender.itemInHand.amount<amount){
                    hasFailedCommand=true
                }
                else{
                    sender.itemInHand.amount=sender.itemInHand.amount-amount
                }
            }
            catch (exception:Exception){
                sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                sender.sendMessageWithColor("&4问题: costItemInHand <数量> 参数 <数量> 不是一个数字")
                sender.sendMessageWithColor("&4脚本将停止运行.................")
                return
            }
        }
        else if(command.matches(Regex("breakBlock .*"))){
            try {
                var distance =command.split(" ")[1].toInt()
                if(sender !is Player){
                    sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                    sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                    sender.sendMessageWithColor("&4问题: breakBlock <距离> 执行者不是一个玩家")
                    sender.sendMessageWithColor("&4脚本将停止运行.................")
                    return
                }
                var block=sender.getTargetBlock(null,distance)
                block.breakNaturally(sender.itemInHand)
            }
            catch (exception:Exception){
                sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                sender.sendMessageWithColor("&4问题: breakBlock <距离> 参数 <距离> 不是一个数字")
                sender.sendMessageWithColor("&4脚本将停止运行.................")
                return
            }
        }
        else if(command.matches(Regex("stopIfFailed"))){
            if(hasFailedCommand) return
        }
        else {
            command=command.replace("<self>",sender.name)
            if (role.split("-")[0]=="cxpoint"){
                //println(1)
                var operator=role.split("-")[1]
                var pointName=role.split("-")[2]
                var amount=try{role.split("-")[3].toDouble()} catch (exception:Exception){
                    sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                    sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                    sender.sendMessageWithColor("&4问题: cxpoint-<运算符>-<货币名>-<数量> 数量不为数字")
                    sender.sendMessageWithColor("&4脚本将停止运行.................")
                    return
                }
                if(operator !in listOf("==","!=","<=",">=","<",">","cost")){
                    sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                    sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                    sender.sendMessageWithColor("&4问题: cxpoint-<运算符>-<货币名>-<数量> 未知的运算符")
                    sender.sendMessageWithColor("&4脚本将停止运行.................")
                    return
                }
                if(pointName !in CXEconomy.pointNameList){
                    sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                    sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                    sender.sendMessageWithColor("&4问题: cxpoint-<运算符>-<货币名>-<数量> 货币名不存在")
                    sender.sendMessageWithColor("&4脚本将停止运行.................")
                    return
                }
                if(sender !is Player){
                    sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                    sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                    sender.sendMessageWithColor("&4问题: $command 执行者不是一个玩家")
                    sender.sendMessageWithColor("&4脚本将停止运行.................")
                    return
                }
                var deposit=CXEconomy.get(sender,pointName)
                var result=true
                when(operator){
                    "=="->{
                        result= deposit==amount
                    }
                    "!="->{
                        result= deposit!=amount
                    }
                    ">="->{
                        result= deposit>=amount
                    }
                    "<="->{
                        result= deposit<=amount
                    }
                    "<"->{
                        result= deposit<amount
                    }
                    ">"->{
                        result= deposit>amount
                    }
                    "cost"->{
                        result=if(deposit>=amount) {
                            sender.costPoint(pointName,amount)
                            true
                        } else false
                    }
                }
                if(result){
                    CXCommand.runWithoutPermission(sender,"$command")
                }
                else{
                    hasFailedCommand=true
                }
            }
            else{
                when(role){
                    "op"->{
                        try {
                            CXCommand.runWithoutPermission(sender as Player,command)
                        }
                        catch (exception:Exception){
                            sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                            sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                            sender.sendMessageWithColor("&4问题: $command 执行者不是一个玩家")
                            sender.sendMessageWithColor("&4脚本将停止运行.................")
                            return
                        }

                    }
                    "player","p"->{
                        try {
                            (sender as Player).performCommand(command)
                        }
                        catch (exception:Exception){
                            sender.sendMessageWithColor("&4[错误] 一个错误在脚本运行的时候发生了 ")
                            sender.sendMessageWithColor("&4位置: 第 ${i+1} 行 : $command ")
                            sender.sendMessageWithColor("&4问题: $command 执行者不是一个玩家")
                            sender.sendMessageWithColor("&4脚本将停止运行.................")
                            return
                        }
                    }
                    "commandBlock"->{
                        CXCommand.sendCommandToConsole(command)
                    }
                }
            }
        }

    }
}



