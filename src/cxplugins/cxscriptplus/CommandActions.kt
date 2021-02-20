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
                    sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
            sender.sendMessageWithColor("&3��ǰ��Ծ���߳���: ${threadPool.size}")
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
                sender.sendMessageWithColor("&4&l[����] [$name]�Ѵ���!!")
                return@action true
            } else {
                var emptyScript = CXScript()

                scriptConfiguration.set("$name.script", emptyScript)
                scriptConfiguration.set("$name.cooldown.enable", false)
                scriptConfiguration.set("$name.cooldown.time", 0)
                scriptConfiguration.set("$name.cooldown.type", "private")
                sender.sendMessageWithColor("&6[CXScriptPlus] �ű� $name �Ѵ����ɹ�!")
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
                sender.sendMessageWithColor("&4&l[����] $name ������!!")
                return@action true
            } else {
                var command = strings["command"] ?: ""
                var role = strings["role"] ?: ""
                var script = scriptConfiguration.get("$name.script") as CXScript
                script.add(command, role)
                scriptConfiguration.set("$name.script", script)
                sender.sendMessageWithColor("&6[CXScriptPlus] $command : $role ����ӵ� $name")
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
                sender.sendMessageWithColor("&4&l[����] $name ������!!")
                return@action true
            } else {
                var line = integers["line"] ?: 0
                var script = scriptConfiguration.get("$name.script") as CXScript
                script.remove(line - 1)
                scriptConfiguration.set("$name.script", script)
                sender.sendMessageWithColor("&6[CXScriptPlus] ��ɾ�� $name �ĵ� $line ������")
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
                sender.sendMessageWithColor("&4&l[����] $name ������!!")
                return@action true
            } else {
                scriptConfiguration.set("$name", null)
                sender.sendMessageWithColor("&6[CXScriptPlus] ��ɾ���ű� $name ")
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
                sender.sendMessageWithColor("&4&l[����] $name ������!!")
                return@action true
            } else {
                var command = strings["command"] ?: ""
                var role = strings["role"] ?: ""
                var position = integers["position"] ?: 0
                var script = scriptConfiguration.get("$name.script") as CXScript
                script.commands.add(position, command)
                script.roles.add(position, role)
                scriptConfiguration.set("$name.script", script)
                sender.sendMessageWithColor("&6[CXScriptPlus] $command : $role �Ѳ��뵽 $name �ĵ� $position ��")
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
                sender.sendMessageWithColor("&4&l[����] $name ������!!")
                return@action true
            } else {
                var script = scriptConfiguration.get("$name.script") as CXScript
                sender.sendMessageWithColor("&b�ű���: $name")
                sender.sendMessageWithColor("&b����: ${script.commands.size}")
                sender.sendMessageWithColor("&b����:")
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
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&4&l[����] �˽ű�������")
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
                sender.sendMessageWithColor("&4&l[����] �˽ű�������")
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
                sender.sendMessageWithColor("&4&l[����] �˽ű�������")
            } else {
                val configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
                configuration.set("$name.cooldown.enable",booleans["enable"])
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] �˽ű�����ȴʱ������������Ϊ${booleans["enable"]}")
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
                sender.sendMessageWithColor("&4&l[����] ��ȴʱ������ֻ��Ϊpublic(������)private(���˽�е�)")
            }
            var name = strings["name"] ?: ""
            if (!isScriptExist(name)) {
                sender.sendMessageWithColor("&4&l[����] �˽ű�������")
            }
            else {
                val configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
                configuration.set("$name.cooldown.type",type)
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] �˽ű�����ȴʱ������������Ϊ${type}")
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
                sender.sendMessageWithColor("&4&l[����] �˽ű�������")
            }
            else {
                val configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "scripts.yml")
                configuration.set("$name.cooldown.time",time)
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] �˽ű�����ȴʱ��������Ϊ${time}")
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
                sender.sendMessageWithColor("&4&l[����] �˽ű�������")
            }
            else {
                val configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "auto.yml")
                val list=configuration.getStringList("autoStartOnEnable")
                list.add(name)
                configuration.set("autoStartOnEnable",list)
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] $name �Ѿ���ӵ��Զ������б�")
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
                sender.sendMessageWithColor("&4&l[����] �˽ű�������")
            } else {
                val configuration = CXYamlConfiguration("CXPlugins\\CXScriptPlus", "auto.yml")
                val list = configuration.getStringList("autoStartOnEnable")
                if (!list.contains(name)) {
                    sender.sendMessageWithColor("&4&l[����] �˽ű�δ����ӵ��Զ������б�")
                    return@action true
                }
                list.remove(name)
                configuration.set("autoStartOnEnable", list)
                configuration.save()
                sender.sendMessageWithColor("&6[CXScriptPlus] $name �ѱ��Ƴ��Զ������б�")
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
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var itemInHand = (sender as Player).itemInHand
                var name = strings["name"]!!
                itemInHand = itemInHand.setNBTValueToCopy(name, "CXScriptPlus", "bindleft")
                (sender as Player).itemInHand = itemInHand
                sender.sendMessageWithColor("&6[CXScriptPlus] ����Ʒ����ű��Ѱ�Ϊ$name")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var itemInHand = (sender as Player).itemInHand
                var name = strings["name"]!!
                itemInHand = itemInHand.setNBTValueToCopy(name, "CXScriptPlus", "bindright")
                (sender as Player).itemInHand = itemInHand
                sender.sendMessageWithColor("&6[CXScriptPlus] ����Ʒ�Ҽ��ű��Ѱ�Ϊ$name")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&6[CXScriptPlus] ����Ʒ����ű��ѽ����")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&6[CXScriptPlus] ����Ʒ�Ҽ��ű��ѽ����")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
                return@action true
            }
        }
    }
    register("cxsp i info") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var itemInHand = (sender as Player).itemInHand
                var leftScriptName = itemInHand.getNBTValue("CXScriptPlus", "bindleft") ?: "��"
                var rightScriptName = itemInHand.getNBTValue("CXScriptPlus", "bindright") ?: "��"
                sender.sendMessageWithColor("&6&l[CXScriptPlus] ������Ʒ�������Ϣ: ")
                sender.sendMessageWithColor("&6&l[CXScriptPlus] ��������󶨽ű�: $leftScriptName")
                sender.sendMessageWithColor("&6&l[CXScriptPlus] �Ҽ������󶨽ű�: $rightScriptName")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var name = strings["name"]
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[����] �㲻��ָ�����!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindLeft", name)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] ��������˷��鴥���Ľű��Ѱ�Ϊ$name!")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var name = strings["name"]
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[����] �㲻��ָ�����!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindRight", name)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] �Ҽ������˷��鴥���Ľű��Ѱ�Ϊ$name!")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var name = strings["name"]
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[����] �㲻��ָ�����!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindBreak", name)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] �ƻ��˷��鴥���Ľű��Ѱ�Ϊ$name!")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var name = strings["name"]
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[����] �㲻��ָ�����!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindWalk", name)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] ���ϴ˷��鴥���Ľű��Ѱ�Ϊ$name!")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
                return@action true
            }
            true
        }
    }
    register("cxsp b unbindleft") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[����] �㲻��ָ�����!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindLeft", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] ��������󶨽ű��ѽ��")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
                return@action true
            }
            true
        }
    }
    register("cxsp b unbindright") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[����] �㲻��ָ�����!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindRight", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] �Ҽ������󶨽ű��ѽ��")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
                return@action true
            }
            true
        }
    }
    register("cxsp b unbindbreak") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[����] �㲻��ָ�����!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindBreak", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] �ƻ�����󶨽ű��ѽ��")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
                return@action true
            }
            true
        }
    }
    register("cxsp b unbindwalk") {
        action {
            if (!isScriptExist(strings["name"] ?: "")) {
                sender.sendMessageWithColor("&4&l[����] δ�ҵ� ${strings["name"] ?: ""} �ű�")
            }
            if (sender is Player) {
                var targetBlock = (sender as Player).getTargetBlock(null, 20)
                if (targetBlock == null) {
                    sender.sendMessageWithColor("&4&l[����] �㲻��ָ�����!")
                    return@action true
                }
                targetBlock.setData("CXScriptPlus.bindWalk", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] ���Ϸ���󶨽ű��ѽ��")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                    sender.sendMessageWithColor("&4&l[����] �㲻��ָ�����!")
                    return@action true
                }

                var leftScriptName = targetBlock.getData("CXScriptPlus.bindLeft") ?: "��"
                var rightScriptName = targetBlock.getData("CXScriptPlus.bindRight") ?: "��"
                var breakScriptName = targetBlock.getData("CXScriptPlus.bindBreak") ?: "��"
                var walkScriptName = targetBlock.getData("CXScriptPlus.bindWalk") ?: "��"
                sender.sendMessageWithColor("&b[CXScriptPlus] ������Ϣ:")
                sender.sendMessageWithColor("&b[CXScriptPlus] ��������ű� : $leftScriptName")
                sender.sendMessageWithColor("&b[CXScriptPlus] �Ҽ������ű� : $rightScriptName")
                sender.sendMessageWithColor("&b[CXScriptPlus] �ƻ������ű� : $breakScriptName")
                sender.sendMessageWithColor("&b[CXScriptPlus] ���ߴ����ű� : $walkScriptName")
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&4&l[����] [$name]�Ѵ���!!")
                return@action true
            } else {
                configuration.set("$name.inventory", CXInventory(Bukkit.createInventory(null, 54)))
                configuration.set("$name.title",name)
                configuration.save()
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$name]�Ѵ���!!")
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
                sender.sendMessageWithColor("&4&l[����] [$name]������!!")
                return@action true
            } else {
                configuration.set("$name.title", title)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$name]�ı����Ѿ�����Ϊ$title!!")
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
                sender.sendMessageWithColor("&4&l[����] [$scriptName]������!!")
                return@action true
            }
            if (!configuration.getKeys(false).contains(name)) {
                sender.sendMessageWithColor("&4&l[����] [$name]������!!")
                return@action true
            }
            if (CXInventory.posToInteger(x, y) !in 0..53) {
                sender.sendMessageWithColor("&4&l[����] �����ڵ�λ��!!")
                return@action true
            } else {
                configuration.set("$name.leftClick.$position", scriptName)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$scriptName]�Ѱ󶨵�[$name]!!")
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
                sender.sendMessageWithColor("&4&l[����] [$scriptName]������!!")
                return@action true
            }
            if (!configuration.getKeys(false).contains(name)) {
                sender.sendMessageWithColor("&4&l[����] [$name]������!!")
                return@action true
            }
            if (CXInventory.posToInteger(x, y) !in 0..53) {
                sender.sendMessageWithColor("&4&l[����] �����ڵ�λ��!!")
                return@action true
            } else {
                configuration.set("$name.rightClick.$position", scriptName)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$scriptName]�Ѱ󶨵�[$name]!!")
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
                sender.sendMessageWithColor("&4&l[����] [$name]������!!")
                return@action true
            }
            if (CXInventory.posToInteger(x, y) !in 0..53) {
                sender.sendMessageWithColor("&4&l[����] �����ڵ�λ��!!")
                return@action true
            } else {
                configuration.set("$name.leftClick.$position", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$name]����������Ѿ����!!")
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
                sender.sendMessageWithColor("&4&l[����] [$name]������!!")
                return@action true
            }
            if (CXInventory.posToInteger(x, y) !in 0..53) {
                sender.sendMessageWithColor("&4&l[����] �����ڵ�λ��!!")
                return@action true
            } else {
                configuration.set("$name.leftRight.$position", null)
                sender.sendMessageWithColor("&6&l[CXScriptPlus] [$name]���Ҽ������Ѿ����!!")
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
                inventory= CXInventory(CXInventory.create(inventory,"&6&l���� $name".toColor()))
                (sender as Player).openInventory(inventory)
                (sender as Player).setMetadata("editingFrame",FixedMetadataValue(CXScriptPlus.plugin,name))
                return@action true
            } else {
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
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
                sender.sendMessageWithColor("&4&l[����] ����������������ִ��")
                return@action true
            }
            true
        }
    }*/
}
fun printCommandHelp(sender: CommandSender,page:Int=1){
    if(page==1){
        sender.sendMessageWithColor("&6[CXScriptPlus] 1./cxsp h <ҳ��>(1~5) : ��ѯĳһҳ����")
    }
    if(page==2) {
        sender.sendMessageWithColor("&6[CXScriptPlus] 1./cxsp s create <�ű���> : �½�һ����Ϊ <�ű���> �Ľű�")
        sender.sendMessageWithColor("&6[CXScriptPlus] 2./cxsp s add <�ű���> \"<����>\" <ִ�����> : ��<�ű���> �����������һ��ִ�����Ϊ<ִ�����> ������ ע������Ҫ��˫����")
        sender.sendMessageWithColor("&6[CXScriptPlus] 3./cxsp s remove <�ű���> <����> : �� <�ű���> �ĵ� <����> ��ɾ��")
        sender.sendMessageWithColor("&6[CXScriptPlus] 4./cxsp s delete <�ű���> : �� <�ű���> ɾ��")
        sender.sendMessageWithColor("&6[CXScriptPlus] 5./cxsp s insert <�ű���> <����> \"<����>\" <ִ�����> : ��<�ű���> �� <����> �����һ��ִ�����Ϊ<ִ�����> ������ ע������Ҫ��˫����")
        sender.sendMessageWithColor("&6[CXScriptPlus] 6./cxsp s info <�ű���> : ��ȡ <�ű���> �������Ϣ")
        sender.sendMessageWithColor("&6[CXScriptPlus] 7./cxsp s menu : �򿪿��ӻ��ű��༭�˵�")
        sender.sendMessageWithColor("&6[CXScriptPlus] 8./cxsp s run <�ű���> : ִ��<�ű���>")
        sender.sendMessageWithColor("&6[CXScriptPlus] 8./cxsp s runtiming <�ű���> <ʱ��(����)>: �趨����ʱ<ʱ��>��ִ��<�ű���>")
        sender.sendMessageWithColor("&6[CXScriptPlus] 9./cxsp s cdenable <�ű���> <true/false>: ����<�ű���>�Ƿ�������ȴʱ��")
        sender.sendMessageWithColor("&6[CXScriptPlus] 10./cxsp s cdtype <�ű���> <private/public>: ����<�ű���>��ȴʱ������ private(���˽�е�) public(��ҹ�����)")
        sender.sendMessageWithColor("&6[CXScriptPlus] 11./cxsp s cdtime <�ű���> <ʱ��(����)>: ����<�ű���>����ȴʱ��Ϊ<ʱ��>���� 1000����=1��")
        sender.sendMessageWithColor("&6[CXScriptPlus] 12./cxsp s addauto <�ű���>: ����<�ű���>Ϊ�����Զ������Ľű�")
        sender.sendMessageWithColor("&6[CXScriptPlus] 13./cxsp s removeauto <�ű���>: ��<�ű���>�ӿ��������ű��б����Ƴ�")
    }
    if(page==3){
        sender.sendMessageWithColor("&b[CXScriptPlus] 1./cxsp i bindleft <�ű���> : ���㵱ǰ�ֳ���Ʒ����������ű�Ϊ<�ű���>")
        sender.sendMessageWithColor("&b[CXScriptPlus] 2./cxsp i bindright <�ű���> : ���㵱ǰ�ֳ���Ʒ���Ҽ������ű�Ϊ<�ű���>")
        sender.sendMessageWithColor("&b[CXScriptPlus] 3./cxsp i unbindleft : ������ֳ���Ʒ����Ľű���")
        sender.sendMessageWithColor("&b[CXScriptPlus] 4./cxsp i unbindright : ������ֳ���Ʒ�Ҽ��Ľű���")
        sender.sendMessageWithColor("&b[CXScriptPlus] 5./cxsp i info : ��ȡ���ֳ���Ʒ�Ľű�����Ϣ")
    }
    if(page==4){
        sender.sendMessageWithColor("&c[CXScriptPlus] 1./cxsp b bindleft <�ű���> : ���㵱ǰ׼����ָ�򷽿����������ű�Ϊ<�ű���>")
        sender.sendMessageWithColor("&c[CXScriptPlus] 2./cxsp b bindright <�ű���> : ���㵱ǰ׼����ָ�򷽿���Ҽ������ű�Ϊ<�ű���>")
        sender.sendMessageWithColor("&c[CXScriptPlus] 3./cxsp b bindbreak <�ű���> : ���㵱ǰ׼����ָ�򷽿���ƻ������ű�Ϊ<�ű���>")
        sender.sendMessageWithColor("&c[CXScriptPlus] 4./cxsp b bindwalk <�ű���> : ���㵱ǰ׼����ָ�򷽿�����ߴ����ű�Ϊ<�ű���>")
        sender.sendMessageWithColor("&c[CXScriptPlus] 5./cxsp b unbindleft : ����㵱ǰ׼����ָ�������������ű�")
        sender.sendMessageWithColor("&c[CXScriptPlus] 6./cxsp b unbindright : ����㵱ǰ׼����ָ������Ҽ������ű�")
        sender.sendMessageWithColor("&c[CXScriptPlus] 7./cxsp b unbindbreak : ����㵱ǰ׼����ָ������ƻ������ű�")
        sender.sendMessageWithColor("&c[CXScriptPlus] 8./cxsp b unbindwalk : ����㵱ǰ׼����ָ��������ߴ����ű�")
        sender.sendMessageWithColor("&c[CXScriptPlus] 9./cxsp b info : ��ȡ�㵱ǰ��ָ����Ľű�����Ϣ")
    }
    if(page==5){
        sender.sendMessageWithColor("&2[CXScriptPlus] ��������: <�ű���> : �½��ű�������")
        sender.sendMessageWithColor("&2[CXScriptPlus] ��������: <ִ�����> : ��op/���/��������ִ�д����� ��һ��������÷�")
        sender.sendMessageWithColor("&2[CXScriptPlus] ��������: <ִ�����> : �����÷�:cxpoint-<������>-<��������>-<���>")
        sender.sendMessageWithColor("&2[CXScriptPlus] ��������: <ִ�����> : <������> : ������ ����> С��< ���ڵ���>= С�ڵ���<= ������== ����== �۳�cost ��һ�� ��ʾ��ҵĻ���������������Ĺ�ϵ ��ϵ�ж�Ϊ:<������> <������> <���>")
        sender.sendMessageWithColor("&2[CXScriptPlus] ��������: run <�ű���> : ��������һ���ű�")
        sender.sendMessageWithColor("&2[CXScriptPlus] ��������: costItemInHand <����> : �۳�����һ����������Ʒ (���������ĵ��ߵĽű�)")
        sender.sendMessageWithColor("&2[CXScriptPlus] ��������: breakBlock <����> : �ݻ�<����>֮�����׼����ָ�ĵ�һ������ (�������ڿ�Ľű�)")
        sender.sendMessageWithColor("&2[CXScriptPlus] ��������: stopIfFailed : ��<ִ�����>�������÷�����ʹ�� ��ʾ���ǰ������Ҫ��<ִ�����>�������÷������� ��ִ��ʧ��(��Ϊ���Ǯ��������Ҫ��) ��ʱ�� �˳��˽ű����ٽ��к��������")
        sender.sendMessageWithColor("&4[CXScriptPlus] ��������(ʵ������): runsuspend <�ű���> : ������������һ���ű� ����ʱ������������")
        sender.sendMessageWithColor("&4[CXScriptPlus] ��������(ʵ������): delay <ʱ��> : ������ǰ���нű����߳�ʹ��ȴ�<ʱ��>����")
        sender.sendMessageWithColor("&4[CXScriptPlus] ��������(ʵ������): always <�ű���> : �����ظ����� <�ű���> ����ʱ������������")
        sender.sendMessageWithColor("&4[CXScriptPlus] ��������(ʵ������): repeat <����> <�ű���> : �����ظ�����ָ�������� <�ű���> ����ʱ������������")
    }
}