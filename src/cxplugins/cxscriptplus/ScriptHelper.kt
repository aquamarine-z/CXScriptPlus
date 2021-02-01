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
            sender.sendMessageWithColor("&2[CXScriptPlus]��ȴʱ�仹ʣ${(lastRunScriptTime + cooldownTime-Date().time).toDouble()/1000}��")
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
                sender.sendMessageWithColor("&2[CXScriptPlus]��ȴʱ�仹ʣ${(lastRunTime + cooldownTime-Date().time).toDouble()/1000}��")
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
                sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                sender.sendMessageWithColor("&4����: run <�ű���> ִ�еĽű�������")
                sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
                return
            }
        }
        else if(command.matches(Regex("costItemInHand .*"))){
            try {
                var amount = command.split(" ")[1].toInt()
                if(sender !is Player){
                    sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                    sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                    sender.sendMessageWithColor("&4����: costItemInHand <����> ִ���߲���һ�����")
                    sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
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
                sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                sender.sendMessageWithColor("&4����: costItemInHand <����> ���� <����> ����һ������")
                sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
                return
            }
        }
        else if(command.matches(Regex("breakBlock .*"))){
            try {
                var distance =command.split(" ")[1].toInt()
                if(sender !is Player){
                    sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                    sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                    sender.sendMessageWithColor("&4����: breakBlock <����> ִ���߲���һ�����")
                    sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
                    return
                }
                var block=sender.getTargetBlock(null,distance)
                block.breakNaturally(sender.itemInHand)
            }
            catch (exception:Exception){
                sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                sender.sendMessageWithColor("&4����: breakBlock <����> ���� <����> ����һ������")
                sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
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
                    sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                    sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                    sender.sendMessageWithColor("&4����: cxpoint-<�����>-<������>-<����> ������Ϊ����")
                    sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
                    return
                }
                if(operator !in listOf("==","!=","<=",">=","<",">","cost")){
                    sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                    sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                    sender.sendMessageWithColor("&4����: cxpoint-<�����>-<������>-<����> δ֪�������")
                    sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
                    return
                }
                if(pointName !in CXEconomy.pointNameList){
                    sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                    sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                    sender.sendMessageWithColor("&4����: cxpoint-<�����>-<������>-<����> ������������")
                    sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
                    return
                }
                if(sender !is Player){
                    sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                    sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                    sender.sendMessageWithColor("&4����: $command ִ���߲���һ�����")
                    sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
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
                            sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                            sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                            sender.sendMessageWithColor("&4����: $command ִ���߲���һ�����")
                            sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
                            return
                        }

                    }
                    "player","p"->{
                        try {
                            (sender as Player).performCommand(command)
                        }
                        catch (exception:Exception){
                            sender.sendMessageWithColor("&4[����] һ�������ڽű����е�ʱ������ ")
                            sender.sendMessageWithColor("&4λ��: �� ${i+1} �� : $command ")
                            sender.sendMessageWithColor("&4����: $command ִ���߲���һ�����")
                            sender.sendMessageWithColor("&4�ű���ֹͣ����.................")
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



