package cxplugins.cxscriptplus
import cxplugins.cxfundamental.minecraft.command.CPMLCommandExecutor.Companion.register
import cxplugins.cxfundamental.minecraft.command.CXScript
import cxplugins.cxfundamental.minecraft.file.CXYamlConfiguration
import cxplugins.cxfundamental.minecraft.kotlindsl.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

fun registerAllCommands() {
    register("cxsp") {
        action {

            //println(1)
            if (sender is Player) {
                    (sender as Player).openFrame(ScriptMenuFrame())
                } else {
                    sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                }
            true

        }
    }
    register("cxsp h") {
        parameter {
            int {
                name = "page"
            }
        }
        action {
            var page = integers["page"] ?: 1
            printCommandHelp(sender, page)
            true
        }
    }

    register("cxsp t amount"){
        action{
            sender.sendMessageWithColor("&3当前活跃的线程数: ${threadPool.size}")
            true
        }
    }

    register("cxsp s create") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            var name = strings["name"] ?: "null"
            println(name)
            var scriptConfiguration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
            if (scriptConfiguration.getKeys(false).contains(name)) {
                sender.sendMessageWithColor("&4&l[错误] [$name]已存在!!")
                return@action true
            } else {
                var emptyScript = CXScript()

                scriptConfiguration.set("$name.script", emptyScript)
                scriptConfiguration.set("$name.cooldown.enable", false)
                scriptConfiguration.set("$name.cooldown.time", 0)
                scriptConfiguration.set("$name.cooldown.type", "private")
                sender.sendMessageWithColor("&6[CXScriptPlus] 脚本 $name 已创建成功!")
                scriptConfiguration.save()
            }
            true
        }
        //println(this.commandParameter)
    }
    register("cxsp s add") {
        parameter {
            string {
                name = "name"
            }
            string {
                name = "command"
                multiparameter {
                    enable = true
                    start = "\""
                    end = "\""
                }
            }
            string {
                name = "role"
            }
        }
        //println(this.commandParameter)
        action {
            var name = strings["name"] ?: ""
            var scriptConfiguration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
            if (!scriptConfiguration.getKeys(false).contains(strings["name"]!!)) {
                sender.sendMessageWithColor("&4&l[错误] $name 不存在!!")
                return@action true
            } else {
                var command = strings["command"] ?: ""
                var role = strings["role"] ?: ""
                var script = scriptConfiguration.get("$name.script") as CXScript
                script.add(command, role)
                scriptConfiguration.set("$name.script", script)
                sender.sendMessageWithColor("&6[CXScriptPlus] $command : $role 已添加到 $name")
                scriptConfiguration.save()
            }
            true
        }
    }
    register("cxsp s remove") {
        parameter {
            string {
                name = "name"
            }
            int {
                name = "line"
                //calculate=true
            }
        }
        action {
            var name = strings["name"] ?: ""
            var scriptConfiguration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
            if (!scriptConfiguration.getKeys(false).contains(strings["name"]!!)) {
                sender.sendMessageWithColor("&4&l[错误] $name 不存在!!")
                return@action true
            } else {
                var line = integers["line"] ?: 0
                var script = scriptConfiguration.get("$name.script") as CXScript
                script.remove(line - 1)
                scriptConfiguration.set("$name.script", script)
                sender.sendMessageWithColor("&6[CXScriptPlus] 已删除 $name 的第 $line 行命令")
                scriptConfiguration.save()
            }
            true
        }
    }
    register("cxsp s delete") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            var name = strings["name"] ?: ""
            var scriptConfiguration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
            if (!scriptConfiguration.getKeys(false).contains(strings["name"]!!)) {
                sender.sendMessageWithColor("&4&l[错误] $name 不存在!!")
                return@action true
            } else {
                scriptConfiguration.set("$name", null)
                sender.sendMessageWithColor("&6[CXScriptPlus] 已删除脚本 $name ")
                scriptConfiguration.save()
            }
            true
        }
    }
    register("cxsp s insert") {
        parameter {
            string {
                name = "name"
            }
            int {
                name = "position"
            }
            string {
                name = "command"
                multiparameter {
                    start = "\""
                    end = "\""
                    enable = true
                }

            }
            string {
                name = "role"
            }
        }
        action {
            var name = strings["name"] ?: ""
            var scriptConfiguration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
            if (!scriptConfiguration.getKeys(false).contains(strings["name"]!!)) {
                sender.sendMessageWithColor("&4&l[错误] $name 不存在!!")
                return@action true
            } else {
                var command = strings["command"] ?: ""
                var role = strings["role"] ?: ""
                var position = integers["position"] ?: 0
                var script = scriptConfiguration.get("$name.script") as CXScript
                script.commands.add(position, command)
                script.roles.add(position, role)
                scriptConfiguration.set("$name.script", script)
                sender.sendMessageWithColor("&6[CXScriptPlus] $command : $role 已插入到 $name 的第 $position 行")
                scriptConfiguration.save()
            }
            true
        }
    }
    register("cxsp s info") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            var name = strings["name"] ?: ""
            var scriptConfiguration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
            if (!scriptConfiguration.getKeys(false).contains(strings["name"]!!)) {
                sender.sendMessageWithColor("&4&l[错误] $name 不存在!!")
                return@action true
            } else {
                var script = scriptConfiguration.get("$name.script") as CXScript
                sender.sendMessageWithColor("&b脚本名: $name")
                sender.sendMessageWithColor("&b行数: ${script.commands.size}")
                sender.sendMessageWithColor("&b内容:")
                for (i in script.commands.indices) {
                    sender.sendMessageWithColor("&b${i + 1} ${script.commands[i]} ${script.roles[i]}")
                }
            }
            true
        }
    }
    register("cxsp s menu") {
        action {
            //println(1)
            if (sender is Player) {
                (sender as Player).openFrame(ScriptMenuFrame())
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
            }
            true
        }
    }
    register("cxsp s runtiming") {
        parameter {
            string {
                name = "name"
            }
            long{
                name="time"
            }
        }
        action {
            var name = strings["name"] ?: ""
            if (!isScriptExist(name)) {
                sender.sendMessageWithColor("&4&l[错误] 此脚本不存在")
            } else {
                runtimingScripts.first.add(name)
                runtimingScripts.second.add(longs["time"]!! + Date().time)
                //saveAllTimingScripts()
                //executeScript(sender, getScriptByName(name)!!)
            }
            true
        }
    }
    register("cxsp s run") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            var name = strings["name"] ?: ""
            if (!isScriptExist(name)) {
                sender.sendMessageWithColor("&4&l[错误] 此脚本不存在")
            } else {
                executeScript(sender, getScriptByName(name)!!)
            }
            true
        }
    }
    register("cxsp s cdenable"){

        parameter {
            string{
                name="name"
            }
            boolean {
                name="enable"
            }
        }
        action{
            var name = strings["name"] ?: ""
            if (!isScriptExist(name)) {
                sender.sendMessageWithColor("&4&l[错误] 此脚本不存在")
            } else {
                val configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
                configuration.set("$name.cooldown.enable",booleans["enable"])
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] 此脚本的冷却时间启用已设置为${booleans["enable"]}")
            }
            true
        }
    }
    register("cxsp s cdtype"){
        parameter {
            string{
                name="name"
            }
            string{
                name="type"
            }
        }
        action{
            val type=strings["type"]
            if(type !in listOf("private","public")){
                sender.sendMessageWithColor("&4&l[错误] 冷却时间类型只能为public(公共的)private(玩家私有的)")
            }
            var name = strings["name"] ?: ""
            if (!isScriptExist(name)) {
                sender.sendMessageWithColor("&4&l[错误] 此脚本不存在")
            }
            else {
                val configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
                configuration.set("$name.cooldown.type",type)
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] 此脚本的冷却时间类型已设置为${type}")
            }
            true
        }
    }
    register("cxsp s cdtime"){
        parameter {
            string{
                name="name"
            }
            long{
                name="time"
            }
        }
        action{
            val time=longs["time"]
            var name = strings["name"] ?: ""
            if (!isScriptExist(name)) {
                sender.sendMessageWithColor("&4&l[错误] 此脚本不存在")
            }
            else {
                val configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
                configuration.set("$name.cooldown.time",time)
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] 此脚本的冷却时间已设置为${time}")
            }
            true
        }
    }
    register("cxsp s addauto"){
        parameter {
            string{
                name="name"
            }
        }
        action{
            var name = strings["name"] ?: ""
            if (!isScriptExist(name)) {
                sender.sendMessageWithColor("&4&l[错误] 此脚本不存在")
            }
            else {
                val configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "auto.yml")
                val list=configuration.getStringList("autoStartOnEnable")
                list.add(name)
                configuration.set("autoStartOnEnable",list)
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] $name 已经添加到自动启动列表")
            }
            true
        }

    }
    register("cxsp s removeauto") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            var name = strings["name"] ?: ""
            if (!isScriptExist(name)) {
                sender.sendMessageWithColor("&4&l[错误] 此脚本不存在")
            } else {
                val configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "auto.yml")
                val list = configuration.getStringList("autoStartOnEnable")
                if (!list.contains(name)) {
                    sender.sendMessageWithColor("&4&l[错误] 此脚本未被添加到自动启动列表")
                    return@action true
                }
                list.remove(name)
                configuration.set("autoStartOnEnable", list)
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] $name 已被移出自动启动列表")
            }
            true
        }
    }
    register("cxsp i bindleft") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var itemInHand = (sender as Player).itemInHand
                var name = strings["name"]!!
                itemInHand = itemInHand.setNBTValueToCopy(name, "CXScriptPlus", "bindleft")
                (sender as Player).itemInHand = itemInHand
                sender.sendMessageWithColor("&6[CXScriptPlus] 该物品左键脚本已绑定为$name")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
            }
            true
        }
    }
    register("cxsp i bindright") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var itemInHand = (sender as Player).itemInHand
                var name = strings["name"]!!
                itemInHand = itemInHand.setNBTValueToCopy(name, "CXScriptPlus", "bindright")
                (sender as Player).itemInHand = itemInHand
                sender.sendMessageWithColor("&6[CXScriptPlus] 该物品右键脚本已绑定为$name")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
            }
            true
        }
    }
    register("cxsp i unbindleft") {
        action {
            if (sender is Player) {
                var itemInHand = (sender as Player).itemInHand

                //var name=strings["name"]!!
                itemInHand = itemInHand.setNBTValueToCopy(null, "CXScriptPlus", "bindleft")
                (sender as Player).itemInHand = itemInHand
                sender.sendMessageWithColor("&6[CXScriptPlus] 该物品左键脚本已解除绑定")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
        }
    }
    register("cxsp i unbindright") {
        action {
            if (sender is Player) {
                var itemInHand = (sender as Player).itemInHand

                //var name=strings["name"]!!
                itemInHand = itemInHand.setNBTValueToCopy(null, "CXScriptPlus", "bindright")
                (sender as Player).itemInHand = itemInHand
                sender.sendMessageWithColor("&6[CXScriptPlus] 该物品右键脚本已解除绑定")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
        }
    }
    register("cxsp i info") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var itemInHand = (sender as Player).itemInHand
                var leftScriptName = itemInHand.getNBTValue("CXScriptPlus", "bindleft") ?: "无"
                var rightScriptName = itemInHand.getNBTValue("CXScriptPlus", "bindright") ?: "无"
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 手上物品的相关信息: ")
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 左键单击绑定脚本: $leftScriptName")
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 右键单击绑定脚本: $rightScriptName")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }

    register("cxsp b bindleft") {
        parameter {
            string {
                name = "name"
            }
        }
        action {

            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var name = strings["name"]
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[错误] 你不能指向空气!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindLeft", name)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 左键单击此方块触发的脚本已绑定为$name!")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }
    register("cxsp b bindright") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var name = strings["name"]
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[错误] 你不能指向空气!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindRight", name)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 右键单击此方块触发的脚本已绑定为$name!")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }
    register("cxsp b bindbreak") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var name = strings["name"]
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[错误] 你不能指向空气!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindBreak", name)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 破坏此方块触发的脚本已绑定为$name!")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }
    register("cxsp b bindwalk") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var name = strings["name"]
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[错误] 你不能指向空气!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindWalk", name)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 走上此方块触发的脚本已绑定为$name!")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }
    register("cxsp b unbindleft") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[错误] 你不能指向空气!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindLeft", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 左键单击绑定脚本已解绑")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }
    register("cxsp b unbindright") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[错误] 你不能指向空气!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindRight", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 右键单击绑定脚本已解绑")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }
    register("cxsp b unbindbreak") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[错误] 你不能指向空气!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindBreak", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 破坏方块绑定脚本已解绑")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }
    register("cxsp b unbindwalk") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[错误] 未找到 ${strings["name"] ?: ""} 脚本")
            }
            if (sender is Player) {
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[错误] 你不能指向空气!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindWalk", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] 走上方块绑定脚本已解绑")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }
    register("cxsp b info") {
        action {
            if (sender is Player) {
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[错误] 你不能指向空气!")
                    return@action true
                }

                var leftScriptName = targetBlock.getData("CXScriptPlus.bindLeft") ?: "无"
                var rightScriptName = targetBlock.getData("CXScriptPlus.bindRight") ?: "无"
                var breakScriptName = targetBlock.getData("CXScriptPlus.bindBreak") ?: "无"
                var walkScriptName = targetBlock.getData("CXScriptPlus.bindWalk") ?: "无"
                sender.sendMessageWithColor("&b[CXScriptPlus] 方块信息:")
                sender.sendMessageWithColor("&b[CXScriptPlus] 左键触发脚本 : $leftScriptName")
                sender.sendMessageWithColor("&b[CXScriptPlus] 右键触发脚本 : $rightScriptName")
                sender.sendMessageWithColor("&b[CXScriptPlus] 破坏触发脚本 : $breakScriptName")
                sender.sendMessageWithColor("&b[CXScriptPlus] 行走触发脚本 : $walkScriptName")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }

    /*register("cxsp f create") {
        parameter {
            string {
                name = "name"
            }
        }
        action {
            var name = strings["name"] ?: ""
            var configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
            if (configuration.getKeys(false).contains(name)) {
                sender.sendMessageWithColor("&4&l[错误] [$name]已存在!!")
                return@action true
            } else {
                configuration.set("$name.inventory", CXInventory(Bukkit.createInventory(null, 54)))
                configuration.set("$name.title",name)
                configuration.save()
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$name]已创建!!")
            }
            true
        }
    }
    register("cxsp f settitle"){
        parameter {
            string{
                name="name"
            }
            string{
                name="title"
            }
        }
        action{
            var name = strings["name"] ?: ""
            var title = strings["title"] ?: ""
            var configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
            if (!configuration.getKeys(false).contains(name)) {
                sender.sendMessageWithColor("&4&l[错误] [$name]不存在!!")
                return@action true
            } else {
                configuration.set("$name.title", title)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$name]的标题已经设置为$title!!")
                configuration.save()

            }
            true
        }
    }
    register("cxsp f bindleft") {
        parameter {
            string {
                name = "frameName"
            }
            int {
                name = "x"
            }
            int {
                name = "y"
            }
            string {
                name = "scriptName"
            }
        }
        action {
            var name = strings["frameName"] ?: ""
            var configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
            var x = integers["x"]?.minus(1) ?: 0
            var y = integers["y"]?.minus(1) ?: 0
            var position = CXInventory.posToInteger(x, y)
            var scriptName = strings["scriptName"] ?: ""
            if (!isScriptExist(scriptName)) {
                sender.sendMessageWithColor("&4&l[错误] [$scriptName]不存在!!")
                return@action true
            }
            if (!configuration.getKeys(false).contains(name)) {
                sender.sendMessageWithColor("&4&l[错误] [$name]不存在!!")
                return@action true
            }
            if (CXInventory.posToInteger(x, y) !in 0..53) {
                sender.sendMessageWithColor("&4&l[错误] 不存在的位置!!")
                return@action true
            } else {
                configuration.set("$name.leftClick.$position", scriptName)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$scriptName]已绑定到[$name]!!")
                configuration.save()
            }
            true
        }

    }
    register("cxsp f bindright") {
        parameter {
            string {
                name = "frameName"
            }
            int {
                name = "x"
            }
            int {
                name = "y"
            }
            string {
                name = "scriptName"
            }
        }
        action {
            var name = strings["frameName"] ?: ""
            var configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
            var x = integers["x"]?.minus(1) ?: 0
            var y = integers["y"]?.minus(1) ?: 0
            var position = CXInventory.posToInteger(x, y)
            var scriptName = strings["scriptName"] ?: ""
            if (!isScriptExist(scriptName)) {
                sender.sendMessageWithColor("&4&l[错误] [$scriptName]不存在!!")
                return@action true
            }
            if (!configuration.getKeys(false).contains(name)) {
                sender.sendMessageWithColor("&4&l[错误] [$name]不存在!!")
                return@action true
            }
            if (CXInventory.posToInteger(x, y) !in 0..53) {
                sender.sendMessageWithColor("&4&l[错误] 不存在的位置!!")
                return@action true
            } else {
                configuration.set("$name.rightClick.$position", scriptName)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$scriptName]已绑定到[$name]!!")
                configuration.save()
            }
            true
        }
    }
    register("cxsp f unbindleft"){
        parameter {
            string {
                name = "frameName"
            }
            int {
                name = "x"
            }
            int {
                name = "y"
            }
        }
        action {
            var name = strings["frameName"] ?: ""
            var configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
            var x = integers["x"]?.minus(1) ?: 0
            var y = integers["y"]?.minus(1) ?: 0
            var position = CXInventory.posToInteger(x, y)
            if (configuration.getKeys(false).contains(name)) {
                sender.sendMessageWithColor("&4&l[错误] [$name]不存在!!")
                return@action true
            }
            if (CXInventory.posToInteger(x, y) !in 0..53) {
                sender.sendMessageWithColor("&4&l[错误] 不存在的位置!!")
                return@action true
            } else {
                configuration.set("$name.leftClick.$position", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$name]的左键触发已经解绑!!")
                configuration.save()
            }
            true
        }
    }
    register("cxsp f unbindright"){
        parameter {
            string {
                name = "frameName"
            }
            int {
                name = "x"
            }
            int {
                name = "y"
            }
        }
        action {
            var name = strings["frameName"] ?: ""
            var configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
            var x = integers["x"]?.minus(1) ?: 0
            var y = integers["y"]?.minus(1) ?: 0
            var position = CXInventory.posToInteger(x, y)
            if (configuration.getKeys(false).contains(name)) {
                sender.sendMessageWithColor("&4&l[错误] [$name]不存在!!")
                return@action true
            }
            if (CXInventory.posToInteger(x, y) !in 0..53) {
                sender.sendMessageWithColor("&4&l[错误] 不存在的位置!!")
                return@action true
            } else {
                configuration.set("$name.leftRight.$position", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$name]的右键触发已经解绑!!")
                configuration.save()
            }
            true
        }
    }
    register("cxsp f edit"){
        parameter {
            string {
                name = "frameName"
            }
        }
        action{
            if (sender is Player) {
                var configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
                var name = strings["frameName"] ?: ""
                var inventory=configuration.get("$name.inventory") as CXInventory
                inventory= CXInventory(CXInventory.create(inventory,"&6&l设置 $name".toColor()))
                (sender as Player).openInventory(inventory)
                (sender as Player).setMetadata("editingFrame",FixedMetadataValue(CXScriptPlus.plugin,name))
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }
    register("cxsp f open"){
        parameter {
            string {
                name = "frameName"
            }
        }
        action {
            if (sender is Player) {
                var configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
                var name = strings["frameName"] ?: ""
                var inventory = configuration.get("$name.inventory") as CXInventory
                var title = configuration.getString("$name.title")
                inventory = CXInventory(CXInventory.create(inventory, title))
                (sender as Player).openInventory(inventory)
                (sender as Player).setMetadata("viewingFrame", FixedMetadataValue(CXScriptPlus.plugin, name))
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[错误] 该命令必须在线玩家执行")
                return@action true
            }
            true
        }
    }*/
}
fun printCommandHelp(sender: CommandSender,page:Int=1){
    if(page==1){
        sender.sendMessageWithColor("&6[CXScriptPlus] 1./cxsp h <页数>(1~5) : 查询某一页帮助")
    }
    if(page==2) {
        sender.sendMessageWithColor("&6[CXScriptPlus] 1./cxsp s create <脚本名> : 新建一个名为 <脚本名> 的脚本")
        sender.sendMessageWithColor("&6[CXScriptPlus] 2./cxsp s add <脚本名> \"<命令>\" <执行身份> : 将<脚本名> 的内容中添加一行执行身份为<执行身份> 的命令 注意命令要加双引号")
        sender.sendMessageWithColor("&6[CXScriptPlus] 3./cxsp s remove <脚本名> <行数> : 将 <脚本名> 的第 <行数> 行删除")
        sender.sendMessageWithColor("&6[CXScriptPlus] 4./cxsp s delete <脚本名> : 将 <脚本名> 删除")
        sender.sendMessageWithColor("&6[CXScriptPlus] 5./cxsp s insert <脚本名> <行数> \"<命令>\" <执行身份> : 在<脚本名> 的 <行数> 后插入一行执行身份为<执行身份> 的命令 注意命令要加双引号")
        sender.sendMessageWithColor("&6[CXScriptPlus] 6./cxsp s info <脚本名> : 获取 <脚本名> 的相关信息")
        sender.sendMessageWithColor("&6[CXScriptPlus] 7./cxsp s menu : 打开可视化脚本编辑菜单")
        sender.sendMessageWithColor("&6[CXScriptPlus] 8./cxsp s run <脚本名> : 执行<脚本名>")
        sender.sendMessageWithColor("&6[CXScriptPlus] 8./cxsp s runtiming <脚本名> <时间(毫秒)>: 设定在延时<时间>后执行<脚本名>")
        sender.sendMessageWithColor("&6[CXScriptPlus] 9./cxsp s cdenable <脚本名> <true/false>: 设置<脚本名>是否启用冷却时间")
        sender.sendMessageWithColor("&6[CXScriptPlus] 10./cxsp s cdtype <脚本名> <private/public>: 设置<脚本名>冷却时间类型 private(玩家私有的) public(玩家公共的)")
        sender.sendMessageWithColor("&6[CXScriptPlus] 11./cxsp s cdtime <脚本名> <时间(毫秒)>: 设置<脚本名>的冷却时间为<时间>毫秒 1000毫秒=1秒")
        sender.sendMessageWithColor("&6[CXScriptPlus] 12./cxsp s addauto <脚本名>: 设置<脚本名>为开服自动启动的脚本")
        sender.sendMessageWithColor("&6[CXScriptPlus] 13./cxsp s removeauto <脚本名>: 把<脚本名>从开服启动脚本列表中移除")
    }
    if(page==3){
        sender.sendMessageWithColor("&b[CXScriptPlus] 1./cxsp i bindleft <脚本名> : 绑定你当前手持物品的左键触发脚本为<脚本名>")
        sender.sendMessageWithColor("&b[CXScriptPlus] 2./cxsp i bindright <脚本名> : 绑定你当前手持物品的右键触发脚本为<脚本名>")
        sender.sendMessageWithColor("&b[CXScriptPlus] 3./cxsp i unbindleft : 解除你手持物品左键的脚本绑定")
        sender.sendMessageWithColor("&b[CXScriptPlus] 4./cxsp i unbindright : 解除你手持物品右键的脚本绑定")
        sender.sendMessageWithColor("&b[CXScriptPlus] 5./cxsp i info : 获取你手持物品的脚本绑定信息")
    }
    if(page==4){
        sender.sendMessageWithColor("&c[CXScriptPlus] 1./cxsp b bindleft <脚本名> : 绑定你当前准星所指向方块的左键触发脚本为<脚本名>")
        sender.sendMessageWithColor("&c[CXScriptPlus] 2./cxsp b bindright <脚本名> : 绑定你当前准星所指向方块的右键触发脚本为<脚本名>")
        sender.sendMessageWithColor("&c[CXScriptPlus] 3./cxsp b bindbreak <脚本名> : 绑定你当前准星所指向方块的破坏触发脚本为<脚本名>")
        sender.sendMessageWithColor("&c[CXScriptPlus] 4./cxsp b bindwalk <脚本名> : 绑定你当前准星所指向方块的行走触发脚本为<脚本名>")
        sender.sendMessageWithColor("&c[CXScriptPlus] 5./cxsp b unbindleft : 解绑你当前准星所指方块的左键触发脚本")
        sender.sendMessageWithColor("&c[CXScriptPlus] 6./cxsp b unbindright : 解绑你当前准星所指方块的右键触发脚本")
        sender.sendMessageWithColor("&c[CXScriptPlus] 7./cxsp b unbindbreak : 解绑你当前准星所指方块的破坏触发脚本")
        sender.sendMessageWithColor("&c[CXScriptPlus] 8./cxsp b unbindwalk : 解绑你当前准星所指方块的行走触发脚本")
        sender.sendMessageWithColor("&c[CXScriptPlus] 9./cxsp b info : 获取你当前所指方块的脚本绑定信息")
    }
    if(page==5){
        sender.sendMessageWithColor("&2[CXScriptPlus] 参数解释: <脚本名> : 新建脚本的名字")
        sender.sendMessageWithColor("&2[CXScriptPlus] 参数解释: <执行身份> : 以op/玩家/命令方块身份执行此命令 有一种特殊的用法")
        sender.sendMessageWithColor("&2[CXScriptPlus] 参数解释: <执行身份> : 特殊用法:cxpoint-<操作符>-<货币名称>-<金额>")
        sender.sendMessageWithColor("&2[CXScriptPlus] 参数解释: <执行身份> : <操作符> : 可以是 大于> 小于< 大于等于>= 小于等于<= 不等于== 等于== 扣除cost 的一种 表示玩家的货币数量与操作符的关系 关系判断为:<玩家余额> <操作符> <金额>")
        sender.sendMessageWithColor("&2[CXScriptPlus] 特殊命令: run <脚本名> : 运行另外一个脚本")
        sender.sendMessageWithColor("&2[CXScriptPlus] 特殊命令: costItemInHand <数量> : 扣除手上一定数量的物品 (多用于消耗道具的脚本)")
        sender.sendMessageWithColor("&2[CXScriptPlus] 特殊命令: breakBlock <距离> : 摧毁<距离>之内玩家准星所指的第一个方块 (多用于挖矿的脚本)")
        sender.sendMessageWithColor("&2[CXScriptPlus] 特殊命令: stopIfFailed : 与<执行身份>的特殊用法搭配使用 表示如果前面有需要用<执行身份>的特殊用法的命令 且执行失败(因为玩家钱数不符合要求) 的时候 退出此脚本不再进行后面的内容")
        sender.sendMessageWithColor("&4[CXScriptPlus] 特殊命令(实验内容): runsuspend <脚本名> : 挂起运行另外一个脚本 运行时不阻塞服务器")
        sender.sendMessageWithColor("&4[CXScriptPlus] 特殊命令(实验内容): delay <时间> : 阻塞当前运行脚本的线程使其等待<时间>毫秒")
        sender.sendMessageWithColor("&4[CXScriptPlus] 特殊命令(实验内容): always <脚本名> : 挂起并重复运行 <脚本名> 运行时不阻塞服务器")
        sender.sendMessageWithColor("&4[CXScriptPlus] 特殊命令(实验内容): repeat <次数> <脚本名> : 挂起并重复运行指定次数的 <脚本名> 运行时不阻塞服务器")
    }
}