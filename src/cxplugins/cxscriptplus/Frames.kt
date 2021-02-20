package cxplugins.cxscriptplus

import cxplugins.cxfundamental.minecraft.command.CXCommand
import cxplugins.cxfundamental.minecraft.command.CXScript
import cxplugins.cxfundamental.minecraft.file.CXYamlConfiguration
import cxplugins.cxfundamental.minecraft.kotlindsl.*
import cxplugins.cxfundamental.minecraft.server.CXInventory
import cxplugins.cxfundamental.minecraft.server.CXItemStack
import cxplugins.cxfundamental.minecraft.ui.CXButton
import cxplugins.cxfundamental.minecraft.ui.CXFrame
import cxplugins.cxfundamental.minecraft.ui.CXPanel
import cxplugins.plugins.cxpoint.CXEconomy
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class ScriptMenuFrame:CXFrame(6){
    init{
        val configuration=CXYamlConfiguration("CXPlugins\\CXScriptPlus","scripts.yml")
        this.apply {
            multipagePanel {
                this.setButtonOnButtonBar(2){
                    itemStack {
                        type=Material.BOOK
                        meta{
                            displayName="&b&l查询准星所指方块绑定脚本信息".toColor()
                            lore=listOf("&b&l点我即可查询准星所指方块绑定脚本信息").toColor()
                        }
                        leftClick { inventoryClickEvent, cxFrame ->
                            CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,"cxsp b info")
                            inventoryClickEvent.whoClicked.closeFrame()
                        }
                    }
                }
                this.setButtonOnButtonBar(3){
                    itemStack {
                        type= Material.PAPER
                        meta{
                            displayName="&4解除脚本绑定".toColor()
                            lore=listOf("&4点我进入脚本解绑菜单").toColor()
                        }
                        leftClick{inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                            inventoryClickEvent.whoClicked.openFrame(ScriptUnbindFrame())
                        }
                    }
                }
                this.setButtonOnButtonBar(4){
                    itemStack {
                        type= Material.COMMAND
                        meta{
                            displayName="&3新建一个脚本".toColor()
                            lore=listOf("&3点击新建一个脚本").toColor()
                        }
                        leftClick{inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->  
                            val player=inventoryClickEvent.whoClicked
                            player.closeFrame()
                            player.askQuestion("&3&l请输入需要创建的脚本的名字"){
                                CXCommand.runWithoutPermission(player as Player,"cxsp s create $it")
                                player.openFrameSynchronously(ScriptMenuFrame(),CXScriptPlus.plugin)
                            }
                        }
                    }
                }
                this.setButtonOnButtonBar(5){
                    itemStack {
                        type=Material.BOOK
                        meta{
                            displayName="&b&l查询手上物品绑定脚本信息".toColor()
                            lore=listOf("&b&l点我即可查询手上物品绑定脚本信息").toColor()
                        }
                        leftClick { inventoryClickEvent, cxFrame ->
                            CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,"cxsp i info")
                            inventoryClickEvent.whoClicked.closeFrame()
                        }
                    }
                }
                if(configuration.getKeys(false).size==0){
                    panel("&3&lCXScriptPlus".toColor()){

                    }
                }
                for((i, scriptNames) in configuration.getKeys(false).withIndex()){
                    val x=CXInventory.integerToPos(i%54).blockX
                    val y=CXInventory.integerToPos(i%54).blockY
                    val button=object:CXButton(){
                        init{
                            this.apply {

                                val scriptName=scriptNames
                                val script=configuration.get("$scriptNames.script") as CXScript
                                itemStack {
                                    type=Material.BOOK
                                    meta {
                                        displayName="&6&l$scriptNames".toColor()
                                        lore= listOf("&3&l长度: ${script.commands.size}",
                                                    "&3&l点我进入详情界面").toColor()
                                    }
                                }
                                leftClick { event,frame ->
                                    event.whoClicked.openFrame(ScriptInformationFrame(scriptName))
                                }
                            }
                        }
                    }

                    this.setWithCreateNewPage(i/54,x,y,button,"&3&lCXScriptPlus".toColor())
                }
                this.updateButtonBar()
            }
        }
    }
}
class ScriptInformationFrame(var name:String):CXFrame(6){
    init{
        this.apply {
            multipagePanel {
                val configuration=CXYamlConfiguration("CXPlugins\\CXScriptPlus","scripts.yml")
                val script=configuration.get("$name.script") as CXScript
                if(script.commands.size==0){
                    panel("&6$name".toColor()){
                    }
                }
                for(i in script.commands.indices){
                    val x=CXInventory.integerToPos(i%54).blockX
                    val y=CXInventory.integerToPos(i%54).blockY
                    val button=object:CXButton(){
                        val scriptName=name
                        val scriptLine=i

                        init{
                            this.apply {
                                itemStack {
                                    type=Material.COMMAND
                                    meta {
                                        displayName="&9${script.commands[i]}".toColor()
                                        lore= listOf("&9身份:${script.roles[i]}",
                                            "&9第 ${i + 1} 行命令",
                                                "&3左键点我在此命令前插入一行命令",
                                                "&4右键点我删除此命令").toColor()
                                    }
                                }
                                leftClick { inventoryClickEvent, cxFrame ->
                                    inventoryClickEvent.whoClicked.closeFrame()
                                    inventoryClickEvent.whoClicked.askQuestion("&6请输入你想要插入的命令内容"){

                                        val command=it
                                        inventoryClickEvent.whoClicked.openFrame(ScriptRoleSelectFrame(command,scriptName,scriptLine))
                                        /*inventoryClickEvent.whoClicked.askQuestion("&6请输入身份名"){
                                            val role=it
                                            CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,"cxsp s insert $name $commandLine \"$command\" $role")
                                            inventoryClickEvent.whoClicked.openFrameSynchronously(ScriptInformationFrame(name),CXScriptPlus.plugin)
                                        }*/
                                    }
                                }
                                rightClick{ event: InventoryClickEvent, frame: CXFrame ->
                                    CXCommand.runWithoutPermission(event.whoClicked as Player, "cxsp s remove $name ${i+1}")
                                    event.whoClicked.openFrameSynchronously(ScriptInformationFrame(name),CXScriptPlus.plugin)
                                }
                            }
                        }
                    }
                    this.setWithCreateNewPage(i/54,x,y,button,"&6$name".toColor())
                }
                val createCommandButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                type=Material.BOOK_AND_QUILL
                                meta{
                                    displayName="&3&l新建一行命令".toColor()
                                    lore= listOf("&6&l左键点我新建一行命令").toColor()
                                }

                            }
                            leftClick{inventoryClickEvent, cxFrame ->
                                inventoryClickEvent.whoClicked.closeFrame()
                                inventoryClickEvent.whoClicked.askQuestion("&6&l请输入你想要创建的命令内容"){
                                    val command=it
                                    inventoryClickEvent.whoClicked.openFrame(ScriptRoleSelectFrame(command,name,script.commands.size))
                                }
                            }
                        }
                    }
                }
                val backButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                type=Material.IRON_DOOR
                                meta{
                                    displayName="&3&l返回".toColor()
                                    lore= listOf("&3&l左键单击返回主菜单").toColor()
                                }
                            }
                            leftClick{ event: InventoryClickEvent, frame: CXFrame ->
                                event.whoClicked.openFrame(ScriptMenuFrame())
                            }
                        }
                    }
                }
                val setCdEnableButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                type=Material.COMPASS
                                meta{
                                    displayName="&b&l设置冷却启用".toColor()
                                    lore=listOf("&b&l左键点我启用此脚本的冷却时间","&c&l右键点我禁止此脚本的冷却时间").toColor()
                                }

                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                "cxsp s cdenable $name true")
                            }
                            rightClick{inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp s cdenable $name false")
                            }
                        }
                    }
                }
                val setCdTypeButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=347
                                meta{
                                    displayName="&c&l设置冷却类型".toColor()
                                    lore=listOf("&b&l左键点我将冷却类型设置为公共的","&b&l右键点我将冷却时间设置为私有的").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp s cdtype $name public")
                            }
                            rightClick{inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp s cdtype $name private")
                            }
                        }
                    }
                }
                val setCdTimeButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=347
                                meta{
                                    displayName="&c&l设置冷却时间".toColor()
                                    lore=listOf("&b&l点我设置冷却时间").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                inventoryClickEvent.whoClicked.closeFrame()
                                  inventoryClickEvent.whoClicked.askQuestion("&3&l请输入冷却时间 单位 毫秒 1毫秒=0.001秒"){
                                      try {
                                          val time=it.toLong()
                                          CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                              "cxsp s cdtime $name $time")

                                      }
                                      catch(exception:Exception){
                                          inventoryClickEvent.whoClicked.sendMessageWithColor("&4[错误]你必须输入一个数字!")
                                      }
                                  }
                            }
                        }
                    }
                }
                val bindScriptToItemButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=347
                                meta{
                                    displayName="&c&l绑定此脚本到手持物品".toColor()
                                    lore=listOf("&b&l点我进入物品脚本绑定菜单").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                inventoryClickEvent.whoClicked.openFrame(ScriptBindToItemFrame(name))
                            }
                        }
                    }
                }
                var bindScriptToBlockButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=347
                                meta{
                                    displayName="&c&l绑定此脚本到准星所对准的方块".toColor()
                                    lore=listOf("&b&l点我绑定此脚本到准星所对准的方块").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                inventoryClickEvent.whoClicked.openFrame(ScriptBindToBlockFrame(name))
                            }

                        }
                    }
                }
                this.setButtonOnButtonBar(1,bindScriptToBlockButton)
                this.setButtonOnButtonBar(2,bindScriptToItemButton)
                this.setButtonOnButtonBar(3,createCommandButton)
                this.setButtonOnButtonBar(4,backButton)
                this.setButtonOnButtonBar(5,setCdEnableButton)
                this.setButtonOnButtonBar(6,setCdTypeButton)
                this.setButtonOnButtonBar(7,setCdTimeButton)

                this.updateButtonBar()
            }
        }
    }
}
class ScriptBindToItemFrame(private var name:String):CXFrame(1){
    init{
        this.apply {
            panel("&3将此脚本 绑定/解绑 到拿在手上的物品".toColor()){
                button(0,0){
                    itemStack {
                        typeId=420
                        meta{
                            displayName="&3&l绑定为左键单击的脚本".toColor()
                            lore=listOf("&3&l点我绑定 $name 为手持物品左键单击触发的脚本").toColor()
                        }
                        leftClick{ event: InventoryClickEvent, frame: CXFrame ->
                            CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp i bindleft $name")
                        }
                    }
                }
                button(1,0){
                    itemStack {
                        typeId=420
                        meta{
                            displayName="&c&l绑定为右键单击的脚本".toColor()
                            lore=listOf("&c&l点我绑定 $name 为手持物品右键单击触发的脚本").toColor()
                        }
                        leftClick{ event: InventoryClickEvent, frame: CXFrame ->
                            CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp i bindright $name")
                        }
                    }
                }
                button(8,0){
                    itemStack {
                        type=Material.IRON_DOOR
                        meta{
                            displayName="&2&l返回".toColor()
                            lore=listOf("&2&l点我返回脚本信息菜单").toColor()
                        }
                        leftClick{ event: InventoryClickEvent, frame: CXFrame ->
                            event.whoClicked.openFrame(ScriptInformationFrame(name))
                        }
                    }
                }
            }
        }
    }
}
class ScriptBindToBlockFrame(var name:String):CXFrame(1){
    init{
        this.apply {
            panel("&3&l绑定脚本到方块".toColor()){
                val bindToLeftClickButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=278
                                meta{
                                    displayName="&c&l绑定此脚本为准星所指方块的左键单击触发脚本".toColor()
                                    lore=listOf("&b&l点我绑定此脚本到准星所对准的方块").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp b bindleft $name")
                            }

                        }
                    }
                }
                val bindToRightClickButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=278
                                meta{
                                    displayName="&c&l绑定此脚本为准星所指方块的右键单击触发脚本".toColor()
                                    lore=listOf("&b&l点我绑定此脚本到准星所对准的方块").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp b bindright $name")
                            }

                        }
                    }
                }
                val bindToBreakButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=278
                                meta{
                                    displayName="&c&l绑定此脚本为准星所指方块的破坏触发脚本".toColor()
                                    lore=listOf("&b&l点我绑定此脚本到准星所对准的方块").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp b bindbreak $name")
                            }

                        }
                    }
                }
                val bindToWalkButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=313
                                meta{
                                    displayName="&c&l绑定此脚本为准星所指方块的行走触发脚本".toColor()
                                    lore=listOf("&b&l点我绑定此脚本到准星所对准的方块").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp b bindwalk $name")
                            }

                        }
                    }
                }
                /*val unbindToLeftClickButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=278
                                meta{
                                    displayName="&4&l解绑此脚本为准星所指方块的左键单击触发脚本".toColor()
                                    lore=listOf("&b&l点我解绑此脚本到准星所对准的方块").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp b unbindleft")
                            }

                        }
                    }
                }
                val unbindToRightClickButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=278
                                meta{
                                    displayName="&4&l解绑此脚本为准星所指方块的右键单击触发脚本".toColor()
                                    lore=listOf("&b&l点我解绑此脚本到准星所对准的方块").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp b unbindright")
                            }

                        }
                    }
                }
                val unbindToBreakButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=278
                                meta{
                                    displayName="&4&l解绑此脚本为准星所指方块的破坏触发脚本".toColor()
                                    lore=listOf("&b&l点我解绑此脚本到准星所对准的方块").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp b unbindbreak")
                            }

                        }
                    }
                }
                val unbindToWalkButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=313
                                meta{
                                    displayName="&4&l解绑此脚本为准星所指方块的行走触发脚本".toColor()
                                    lore=listOf("&b&l点我解绑此脚本到准星所对准的方块").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp b unbindwalk")
                            }

                        }
                    }
                }*/
                val backButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                type=Material.IRON_DOOR
                                meta{
                                    displayName="&c&l返回".toColor()
                                    lore=listOf("&b&l点我返回脚本菜单").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                inventoryClickEvent.whoClicked.openFrame(ScriptInformationFrame(name))
                            }

                        }
                    }
                }
                this.set(0,0,bindToLeftClickButton)
                this.set(1,0,bindToRightClickButton)
                this.set(2,0,bindToBreakButton)
                this.set(3,0,bindToWalkButton)
                /*this.set(4,0,unbindToLeftClickButton)
                this.set(5,0,unbindToRightClickButton)
                this.set(6,0,unbindToBreakButton)
                this.set(7,0,unbindToWalkButton)*/
                this.set(8,0,backButton)
            }
        }
    }
}
class ScriptUnbindFrame :CXFrame(1){
    init{
        this.apply {
            panel("&c&l解除绑定菜单".toColor()){
                button(0,0){
                    itemStack {
                        type=Material.SHEARS
                        meta{
                            displayName="&4&l解除手持物品的左键脚本绑定".toColor()
                            lore=listOf("&4点我解除手持物品绑定的左键触发脚本").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp i unbindleft")
                    }
                }
                button(1,0){
                    itemStack {
                        type=Material.SHEARS
                        meta{
                            displayName="&4&l解除手持物品的右键脚本绑定".toColor()
                            lore=listOf("&4点我解除手持物品绑定的右键触发脚本").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp i unbindright")
                    }
                }
                button(2,0){
                    itemStack {
                        type=Material.COMMAND
                        meta{
                            displayName="&4&l解除准星所指物品的左键脚本绑定".toColor()
                            lore=listOf("&4点我解除准星所指物品绑定的左键触发脚本").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp b unbindleft")
                    }
                }
                button(3,0){
                    itemStack {
                        type=Material.COMMAND
                        meta{
                            displayName="&4&l解除准星所指物品的右键脚本绑定".toColor()
                            lore=listOf("&4点我解除准星所指物品绑定的右键触发脚本").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp b unbindright")
                    }
                }
                button(4,0){
                    itemStack {
                        type=Material.COMMAND
                        meta{
                            displayName="&4&l解除准星所指物品的破坏脚本绑定".toColor()
                            lore=listOf("&4点我解除准星所指物品绑定的破坏触发脚本").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp b unbindbreak")
                    }
                }
                button(5,0){
                    itemStack {
                        type=Material.COMMAND
                        meta{
                            displayName="&4&l解除准星所指物品的行走脚本绑定".toColor()
                            lore=listOf("&4点我解除准星所指物品绑定的行走触发脚本").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp b unbindwalk")
                    }
                }
                button(8,0){
                    itemStack {
                        type=Material.IRON_DOOR
                        meta{
                            displayName="&3&l返回".toColor()
                            lore=listOf("&3点我回到脚本主菜单").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        event.whoClicked.openFrame(ScriptMenuFrame())
                    }
                }
            }
        }
    }
}
class ScriptRoleSelectFrame(private var command:String, private var scriptName:String, location:Int):CXFrame(1){
    init{
        this.apply{
            panel("&3&l请选择命令所对应的<执行身份>".toColor()){
                button(0,0){
                    itemStack {
                        type=Material.DIAMOND_CHESTPLATE
                        meta{
                            displayName="&c&l以 管理员 身份执行$command".toColor()
                            lore=listOf("&c点我选择以管理员身份执行此命令").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp s insert $scriptName $location \"$command\" op")
                        event.whoClicked.openFrame(ScriptInformationFrame(scriptName))
                    }
                }

                button(1,0){
                    itemStack {
                        type=Material.IRON_CHESTPLATE
                        meta{
                            displayName="&c&l以 普通玩家 身份执行$command".toColor()
                            lore=listOf("&c点我选择以普通玩家身份执行此命令").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp s insert $scriptName $location  \"$command\" player")
                        event.whoClicked.openFrame(ScriptInformationFrame(scriptName))
                    }
                }

                button(2,0){
                    itemStack {
                        type=Material.COMMAND
                        meta{
                            displayName="&c&l以 命令方块 身份执行$command".toColor()
                            lore=listOf("&c点我选择以命令方块身份执行此命令").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp s insert $scriptName $location \"$command\" commandBlock")
                        event.whoClicked.openFrame(ScriptInformationFrame(scriptName))
                    }
                }
                button(3,0){
                    itemStack {
                        type=Material.DIAMOND
                        meta{
                            displayName="&9&l以 特殊身份 执行$command".toColor()
                            lore=listOf("&9点我进入特殊执行身份设置菜单").toColor()
                        }
                    }
                    leftClick { event, frame ->
                        event.whoClicked.openFrame(ScriptSpecialRoleSelectFrame(command,scriptName,location))
                    }
                }

            }
        }
    }
}
class ScriptSpecialRoleSelectFrame(command:String,scriptName:String,location:Int):CXFrame(1){
    init{
        this.apply{
            panel("&6&l脚本特殊执行身份设置菜单"){
                val buttonCXPointLabel=CXButton(CXItemStack(Material.PAPER,1,"&6&lcxpoint",""))
                val lineLabel=CXButton(CXItemStack(Material.ARROW,1,"&6&l-",""))
                val setPointNameButton=object:CXButton(CXItemStack(Material.GOLD_INGOT,1,"&6&l设置货币种类","&6&l点我设置货币种类")){
                    override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                        super.onLeftClick(event, frame)
                        event.whoClicked.openFrame(ScriptPointTypeSelectFrame(this@ScriptSpecialRoleSelectFrame))
                    }
                }
                val setOperatorButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l设置操作符","&3&l点我设置操作符")){
                    override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                        super.onLeftClick(event, frame)
                        event.whoClicked.openFrame(ScriptOperatorSelectFrame(this@ScriptSpecialRoleSelectFrame))
                    }
                }
                val confirmButton=object:CXButton(CXItemStack(Material.STONE_BUTTON,1,"&2&l确认特殊身份","&2&l进行确认")){
                override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                    super.onLeftClick(event, frame)
                    val operation=((this@ScriptSpecialRoleSelectFrame.mainPanel as CXPanel).inventory!!.getItem(2).itemMeta.displayName).replace("&3&l".toColor(),"")
                    val pointName=((this@ScriptSpecialRoleSelectFrame.mainPanel as CXPanel).inventory!!.getItem(4).itemMeta.displayName).replace("&6&l".toColor(),"")
                    if(operation=="设置操作符"||pointName=="设置货币种类"){
                        event.whoClicked.sendMessageWithColor("&4&l[错误] 请先设置操作符和货币种类")
                        return
                    }
                    event.whoClicked.closeFrame()
                    event.whoClicked.askQuestion("&2&l请输入 [设定值]"){
                        var value:Double=try{
                            it.toDouble()
                        }
                        catch(exception:NumberFormatException){
                            event.whoClicked.sendMessage("&4&l[错误] 请输入一个数字")
                            event.whoClicked.openFrame(this@ScriptSpecialRoleSelectFrame)
                            return@askQuestion
                        }

                        val finalRole="cxpoint-$operation-$pointName-$value"
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp s insert $scriptName $location \"$command\" $finalRole")
                        event.whoClicked.openFrame(ScriptInformationFrame(scriptName))
                        }

                    }
                }
                val backButton=object:CXButton(CXItemStack(Material.IRON_DOOR,1,"&b&l返回","&b&l点我返回")){
                    override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                        super.onLeftClick(event, frame)
                        event.whoClicked.openFrame(ScriptRoleSelectFrame(command,scriptName,location))

                    }
                }
                this.set(0,0,buttonCXPointLabel)
                this.set(1,0,lineLabel)
                this.set(2,0,setOperatorButton)
                this.set(3,0,lineLabel)
                this.set(4,0,setPointNameButton)
                this.set(5,0,lineLabel)
                this.set(6,0,confirmButton)
                this.set(8,0,backButton)
            }

        }
    }
}
class ScriptPointTypeSelectFrame(var fatherFrame:ScriptSpecialRoleSelectFrame):CXFrame(6){
    init{
        this.apply{
            multipagePanel {
                this.add(CXPanel(6,"&c&lCXPoint-货币类型选择界面".toColor()))
                val pointNameList = CXEconomy.pointNameList
                for ((i, name) in pointNameList.withIndex()) {
                    val button=object:CXButton(){
                        val pointName=name
                        init{
                            this.apply {
                                itemStack {
                                    type=Material.GOLD_INGOT
                                    meta {
                                        displayName="&3&l$name".toColor()
                                        lore=listOf("&3&l点我选择 $name 为货币").toColor()
                                    }
                                }
                                leftClick { inventoryClickEvent, cxFrame ->
                                    var panel=fatherFrame.mainPanel!! as CXPanel
                                    val newButton=object:CXButton(CXItemStack(Material.GOLD_INGOT,1,"&6&l$pointName","&6&l点我设置货币种类")){
                                        override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                                            super.onLeftClick(event, frame)
                                            event.whoClicked.openFrame(ScriptPointTypeSelectFrame(fatherFrame))
                                        }
                                    }
                                    panel.set(4,0,newButton)
                                    fatherFrame.setPanel(panel)
                                    inventoryClickEvent.whoClicked.openFrame(fatherFrame)
                                }
                            }
                        }
                    }
                    this.set(i,button)
                }
            }

        }
    }
}
class ScriptOperatorSelectFrame(var fatherFrame:ScriptSpecialRoleSelectFrame):CXFrame(1){
    init{
        this.apply{
            panel("&3&l特殊身份操作符设置窗口".toColor()){
                button(0,0){
                    itemStack{
                        type=Material.REDSTONE
                        meta{
                            displayName="&3&l>".toColor()
                            lore=listOf("&3&l若[玩家钱数] > [设定值] 则执行命令 否则执行失败").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l>","&3&l点我设置操作符")){
                            override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                                super.onLeftClick(event, frame)
                                event.whoClicked.openFrame(ScriptOperatorSelectFrame(fatherFrame))
                            }
                        }
                        panel.set(2,0,newButton)
                        fatherFrame.setPanel(panel)
                        inventoryClickEvent.whoClicked.openFrame(fatherFrame)

                    }
                }
                button(1,0){
                    itemStack{
                        type=Material.REDSTONE
                        meta{
                            displayName="&3&l>=".toColor()
                            lore=listOf("&3&l若[玩家钱数] >= [设定值] 则执行命令 否则执行失败").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l>=","&3&l点我设置操作符")){
                            override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                                super.onLeftClick(event, frame)
                                event.whoClicked.openFrame(ScriptOperatorSelectFrame(fatherFrame))
                            }
                        }
                        panel.set(2,0,newButton)
                        fatherFrame.setPanel(panel)
                        inventoryClickEvent.whoClicked.openFrame(fatherFrame)

                    }
                }
                button(2,0){
                    itemStack{
                        type=Material.REDSTONE
                        meta{
                            displayName="&3&l<".toColor()
                            lore=listOf("&3&l若[玩家钱数] < [设定值] 则执行命令 否则执行失败").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l<","&3&l点我设置操作符")){
                            override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                                super.onLeftClick(event, frame)
                                event.whoClicked.openFrame(ScriptOperatorSelectFrame(fatherFrame))
                            }
                        }
                        panel.set(2,0,newButton)
                        fatherFrame.setPanel(panel)
                        inventoryClickEvent.whoClicked.openFrame(fatherFrame)

                    }
                }
                button(3,0){
                    itemStack{
                        type=Material.REDSTONE
                        meta{
                            displayName="&3&l<=".toColor()
                            lore=listOf("&3&l若[玩家钱数] <= [设定值] 则执行命令 否则执行失败").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l<=","&3&l点我设置操作符")){
                            override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                                super.onLeftClick(event, frame)
                                event.whoClicked.openFrame(ScriptOperatorSelectFrame(fatherFrame))
                            }
                        }
                        panel.set(2,0,newButton)
                        fatherFrame.setPanel(panel)
                        inventoryClickEvent.whoClicked.openFrame(fatherFrame)

                    }
                }
                button(4,0){
                    itemStack{
                        type=Material.REDSTONE
                        meta{
                            displayName="&3&l==".toColor()
                            lore=listOf("&3&l若[玩家钱数] == [设定值] 则执行命令 否则执行失败").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l==","&3&l点我设置操作符")){
                            override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                                super.onLeftClick(event, frame)
                                event.whoClicked.openFrame(ScriptOperatorSelectFrame(fatherFrame))
                            }
                        }
                        panel.set(2,0,newButton)
                        fatherFrame.setPanel(panel)
                        inventoryClickEvent.whoClicked.openFrame(fatherFrame)

                    }
                }
                button(5,0){
                    itemStack{
                        type=Material.REDSTONE
                        meta{
                            displayName="&3&l!=".toColor()
                            lore=listOf("&3&l若[玩家钱数] != [设定值] 则执行命令 否则执行失败").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l!=","&3&l点我设置操作符")){
                            override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                                super.onLeftClick(event, frame)
                                event.whoClicked.openFrame(ScriptOperatorSelectFrame(fatherFrame))
                            }
                        }
                        panel.set(2,0,newButton)
                        fatherFrame.setPanel(panel)
                        inventoryClickEvent.whoClicked.openFrame(fatherFrame)

                    }
                }
                button(6,0){
                    itemStack{
                        type=Material.REDSTONE
                        meta{
                            displayName="&3&lcost".toColor()
                            lore=listOf("&3&l若[玩家钱数] >= [设定值] 则执行命令,并扣除玩家[设定值]的货币 否则执行失败").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&lcost","&3&l点我设置操作符")){
                            override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                                super.onLeftClick(event, frame)
                                event.whoClicked.openFrame(ScriptOperatorSelectFrame(fatherFrame))
                            }
                        }
                        panel.set(2,0,newButton)
                        fatherFrame.setPanel(panel)
                        inventoryClickEvent.whoClicked.openFrame(fatherFrame)

                    }
                }
            }
        }
    }
}
