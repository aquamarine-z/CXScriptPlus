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
                            displayName="&3�½�һ���ű�".toColor()
                            lore=listOf("&3����½�һ���ű�").toColor()
                        }
                        leftClick{inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->  
                            val player=inventoryClickEvent.whoClicked
                            player.closeFrame()
                            player.askQuestion("&3&l��������Ҫ�����Ľű�������"){
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
                                        lore= listOf("&3&l����: ${script.commands.size}",
                                                    "&3&l���ҽ����������").toColor()
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
                                        lore= listOf("&9���:${script.roles[i]}",
                                                "&9�� ${commandLine+1} ������",
                                                "&3��������ڴ�����ǰ����һ������",
                                                "&4�Ҽ�����ɾ��������").toColor()
                                    }
                                }
                                leftClick { inventoryClickEvent, cxFrame ->
                                    inventoryClickEvent.whoClicked.closeFrame()
                                    inventoryClickEvent.whoClicked.askQuestion("&6����������Ҫ�������������"){

                                        val command=it
                                        inventoryClickEvent.whoClicked.askQuestion("&6�����������"){
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
                                    displayName="&3&l�½�һ������".toColor()
                                    lore= listOf("&6&l��������½�һ������").toColor()
                                }

                            }
                            leftClick{inventoryClickEvent, cxFrame ->
                                inventoryClickEvent.whoClicked.closeFrame()
                                inventoryClickEvent.whoClicked.askQuestion("&6&l����������Ҫ�������������"){
                                    val command=it
                                    inventoryClickEvent.whoClicked.askQuestion("&6&l�����������"){ it1 ->
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
                                    displayName="&3&l����".toColor()
                                    lore= listOf("&3&l��������������˵�").toColor()
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
                                    displayName="&b&l������ȴ����".toColor()
                                    lore=listOf("&b&l����������ô˽ű�����ȴʱ��","&c&l�Ҽ����ҽ�ֹ�˽ű�����ȴʱ��").toColor()
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
                                    displayName="&c&l������ȴ����".toColor()
                                    lore=listOf("&b&l������ҽ���ȴ��������Ϊ������","&b&l�Ҽ����ҽ���ȴʱ������Ϊ˽�е�").toColor()
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
                                    displayName="&c&l������ȴʱ��".toColor()
                                    lore=listOf("&b&l����������ȴʱ��").toColor()
                                }
                            }
                            leftClick{ inventoryClickEvent: InventoryClickEvent, cxFrame: CXFrame ->
                                  inventoryClickEvent.whoClicked.askQuestion("&3&l��������ȴʱ�� ��λ ���� 1����=0.001��"){
                                      try {
                                          val time=it.toLong()
                                          CXCommand.runWithoutPermission(inventoryClickEvent.whoClicked as Player,
                                              "cxsp s cdtime $name $time")
                                      }
                                      catch(exception:Exception){
                                          inventoryClickEvent.whoClicked.sendMessageWithColor("&4[����]���������һ������!")
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
                                    displayName="&c&l�󶨴˽ű����ֳ���Ʒ".toColor()
                                    lore=listOf("&b&l������Ұ󶨵���Ʒ��������Ľű�","&b&l�Ҽ����Ұ󶨵���Ʒ�Ҽ������Ľű�").toColor()
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
                                    displayName="&c&l�󶨴˽ű���׼������׼�ķ���".toColor()
                                    lore=listOf("&b&l���Ұ󶨴˽ű���׼������׼�ķ���").toColor()
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
            panel("&3&l�󶨽ű�������".toColor()){
                val bindToLeftClickButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=278
                                meta{
                                    displayName="&c&l�󶨴˽ű�Ϊ׼����ָ�����������������ű�".toColor()
                                    lore=listOf("&b&l���Ұ󶨴˽ű���׼������׼�ķ���").toColor()
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
                                    displayName="&c&l�󶨴˽ű�Ϊ׼����ָ������Ҽ����������ű�".toColor()
                                    lore=listOf("&b&l���Ұ󶨴˽ű���׼������׼�ķ���").toColor()
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
                                    displayName="&c&l�󶨴˽ű�Ϊ׼����ָ������ƻ������ű�".toColor()
                                    lore=listOf("&b&l���Ұ󶨴˽ű���׼������׼�ķ���").toColor()
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
                                    displayName="&c&l�󶨴˽ű�Ϊ׼����ָ��������ߴ����ű�".toColor()
                                    lore=listOf("&b&l���Ұ󶨴˽ű���׼������׼�ķ���").toColor()
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
                                    displayName="&c&l����".toColor()
                                    lore=listOf("&b&l���ҷ��ؽű��˵�").toColor()
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