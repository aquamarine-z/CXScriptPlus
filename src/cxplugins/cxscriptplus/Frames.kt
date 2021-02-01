package cxplugins.cxscriptplus

import cxplugins.cxfundamental.minecraft.command.CXCommand
import cxplugins.cxfundamental.minecraft.command.CXScript
import cxplugins.cxfundamental.minecraft.file.CXYamlConfiguration
import cxplugins.cxfundamental.minecraft.kotlindsl.*
import cxplugins.cxfundamental.minecraft.server.CXInventory
import cxplugins.cxfundamental.minecraft.ui.CXButton
import cxplugins.cxfundamental.minecraft.ui.CXFrame
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class ScriptMenuFrame:CXFrame(6){
    init{
        val configuration=CXYamlConfiguration("CXPlugins\\CXScriptPlus","scripts.yml")
        this.apply {
            multipagePanel {
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
                                val commandLine=i
                                itemStack {
                                    type=Material.COMMAND
                                    meta {
                                        displayName="&9${script.commands[i]}".toColor()
                                        lore= listOf("&9身份:${script.roles[i]}",
                                                "&9第 ${commandLine+1} 行命令",
                                                "&3左键点我在此命令前插入一行命令",
                                                "&4右键点我删除此命令").toColor()
                                    }
                                }
                                leftClick { inventoryClickEvent, cxFrame ->
                                    inventoryClickEvent.whoClicked.closeFrame()
                                    inventoryClickEvent.whoClicked.askQuestion("&6请输入你想要插入的命令内容"){

                                        val command=it
                                        inventoryClickEvent.whoClicked.askQuestion("&6请输入身份名"){
                                            val role=it
                                            CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,"cxsp s insert $name $commandLine \"$command\" $role")
                                            inventoryClickEvent.whoClicked.openFrameSynchronously(ScriptInformationFrame(name),CXScriptPlus.plugin)
                                        }
                                    }
                                }
                                rightClick{ event: InventoryClickEvent, frame: CXFrame ->
                                    CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp s remove $name $commandLine")
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
                                inventoryClickEvent.whoClicked.askQuestion("&6&l请输入你想要插入的命令内容"){
                                    val command=it
                                    inventoryClickEvent.whoClicked.askQuestion("&6&l请输入身份名"){ it1 ->
                                        //println(1)
                                        CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                            "cxsp s add $name \"$command\" $it1")

                                        inventoryClickEvent.whoClicked.openFrameSynchronously(ScriptInformationFrame(name),CXScriptPlus.plugin)
                                    }
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
                                    lore=listOf("&b&l左键点我绑定到物品左键单击的脚本","&b&l右键点我绑定到物品右键单击的脚本").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                    "cxsp i bindleft $name")
                            }
                            rightClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                "cxsp i bindright $name")
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
                this.set(8,0,backButton)
            }
        }
    }
}