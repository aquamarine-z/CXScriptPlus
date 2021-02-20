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
                            displayName="&b&l��ѯ׼����ָ����󶨽ű���Ϣ".toColor()
                            lore=listOf("&b&l���Ҽ��ɲ�ѯ׼����ָ����󶨽ű���Ϣ").toColor()
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
                            displayName="&4����ű���".toColor()
                            lore=listOf("&4���ҽ���ű����˵�").toColor()
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
                this.setButtonOnButtonBar(5){
                    itemStack {
                        type=Material.BOOK
                        meta{
                            displayName="&b&l��ѯ������Ʒ�󶨽ű���Ϣ".toColor()
                            lore=listOf("&b&l���Ҽ��ɲ�ѯ������Ʒ�󶨽ű���Ϣ").toColor()
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
                                itemStack {
                                    type=Material.COMMAND
                                    meta {
                                        displayName="&9${script.commands[i]}".toColor()
                                        lore= listOf("&9���:${script.roles[i]}",
                                            "&9�� ${i + 1} ������",
                                                "&3��������ڴ�����ǰ����һ������",
                                                "&4�Ҽ�����ɾ��������").toColor()
                                    }
                                }
                                leftClick { inventoryClickEvent, cxFrame ->
                                    inventoryClickEvent.whoClicked.closeFrame()
                                    inventoryClickEvent.whoClicked.askQuestion("&6����������Ҫ�������������"){

                                        val command=it
                                        inventoryClickEvent.whoClicked.openFrame(ScriptRoleSelectFrame(command,scriptName,scriptLine))
                                        /*inventoryClickEvent.whoClicked.askQuestion("&6�����������"){
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
                                    displayName="&3&l�½�һ������".toColor()
                                    lore= listOf("&6&l��������½�һ������").toColor()
                                }

                            }
                            leftClick{inventoryClickEvent, cxFrame ->
                                inventoryClickEvent.whoClicked.closeFrame()
                                inventoryClickEvent.whoClicked.askQuestion("&6&l����������Ҫ��������������"){
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
                                inventoryClickEvent.whoClicked.closeFrame()
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
                                    lore=listOf("&b&l���ҽ�����Ʒ�ű��󶨲˵�").toColor()
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
class ScriptBindToItemFrame(private var name:String):CXFrame(1){
    init{
        this.apply {
            panel("&3���˽ű� ��/��� ���������ϵ���Ʒ".toColor()){
                button(0,0){
                    itemStack {
                        typeId=420
                        meta{
                            displayName="&3&l��Ϊ��������Ľű�".toColor()
                            lore=listOf("&3&l���Ұ� $name Ϊ�ֳ���Ʒ������������Ľű�").toColor()
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
                            displayName="&c&l��Ϊ�Ҽ������Ľű�".toColor()
                            lore=listOf("&c&l���Ұ� $name Ϊ�ֳ���Ʒ�Ҽ����������Ľű�").toColor()
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
                            displayName="&2&l����".toColor()
                            lore=listOf("&2&l���ҷ��ؽű���Ϣ�˵�").toColor()
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
                /*val unbindToLeftClickButton=object:CXButton(){
                    init{
                        this.apply {
                            itemStack {
                                typeId=278
                                meta{
                                    displayName="&4&l���˽ű�Ϊ׼����ָ�����������������ű�".toColor()
                                    lore=listOf("&b&l���ҽ��˽ű���׼������׼�ķ���").toColor()
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
                                    displayName="&4&l���˽ű�Ϊ׼����ָ������Ҽ����������ű�".toColor()
                                    lore=listOf("&b&l���ҽ��˽ű���׼������׼�ķ���").toColor()
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
                                    displayName="&4&l���˽ű�Ϊ׼����ָ������ƻ������ű�".toColor()
                                    lore=listOf("&b&l���ҽ��˽ű���׼������׼�ķ���").toColor()
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
                                    displayName="&4&l���˽ű�Ϊ׼����ָ��������ߴ����ű�".toColor()
                                    lore=listOf("&b&l���ҽ��˽ű���׼������׼�ķ���").toColor()
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
            panel("&c&l����󶨲˵�".toColor()){
                button(0,0){
                    itemStack {
                        type=Material.SHEARS
                        meta{
                            displayName="&4&l����ֳ���Ʒ������ű���".toColor()
                            lore=listOf("&4���ҽ���ֳ���Ʒ�󶨵���������ű�").toColor()
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
                            displayName="&4&l����ֳ���Ʒ���Ҽ��ű���".toColor()
                            lore=listOf("&4���ҽ���ֳ���Ʒ�󶨵��Ҽ������ű�").toColor()
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
                            displayName="&4&l���׼����ָ��Ʒ������ű���".toColor()
                            lore=listOf("&4���ҽ��׼����ָ��Ʒ�󶨵���������ű�").toColor()
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
                            displayName="&4&l���׼����ָ��Ʒ���Ҽ��ű���".toColor()
                            lore=listOf("&4���ҽ��׼����ָ��Ʒ�󶨵��Ҽ������ű�").toColor()
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
                            displayName="&4&l���׼����ָ��Ʒ���ƻ��ű���".toColor()
                            lore=listOf("&4���ҽ��׼����ָ��Ʒ�󶨵��ƻ������ű�").toColor()
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
                            displayName="&4&l���׼����ָ��Ʒ�����߽ű���".toColor()
                            lore=listOf("&4���ҽ��׼����ָ��Ʒ�󶨵����ߴ����ű�").toColor()
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
                            displayName="&3&l����".toColor()
                            lore=listOf("&3���һص��ű����˵�").toColor()
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
            panel("&3&l��ѡ����������Ӧ��<ִ�����>".toColor()){
                button(0,0){
                    itemStack {
                        type=Material.DIAMOND_CHESTPLATE
                        meta{
                            displayName="&c&l�� ����Ա ���ִ��$command".toColor()
                            lore=listOf("&c����ѡ���Թ���Ա���ִ�д�����").toColor()
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
                            displayName="&c&l�� ��ͨ��� ���ִ��$command".toColor()
                            lore=listOf("&c����ѡ������ͨ������ִ�д�����").toColor()
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
                            displayName="&c&l�� ����� ���ִ��$command".toColor()
                            lore=listOf("&c����ѡ������������ִ�д�����").toColor()
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
                            displayName="&9&l�� ������� ִ��$command".toColor()
                            lore=listOf("&9���ҽ�������ִ��������ò˵�").toColor()
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
            panel("&6&l�ű�����ִ��������ò˵�"){
                val buttonCXPointLabel=CXButton(CXItemStack(Material.PAPER,1,"&6&lcxpoint",""))
                val lineLabel=CXButton(CXItemStack(Material.ARROW,1,"&6&l-",""))
                val setPointNameButton=object:CXButton(CXItemStack(Material.GOLD_INGOT,1,"&6&l���û�������","&6&l�������û�������")){
                    override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                        super.onLeftClick(event, frame)
                        event.whoClicked.openFrame(ScriptPointTypeSelectFrame(this@ScriptSpecialRoleSelectFrame))
                    }
                }
                val setOperatorButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l���ò�����","&3&l�������ò�����")){
                    override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                        super.onLeftClick(event, frame)
                        event.whoClicked.openFrame(ScriptOperatorSelectFrame(this@ScriptSpecialRoleSelectFrame))
                    }
                }
                val confirmButton=object:CXButton(CXItemStack(Material.STONE_BUTTON,1,"&2&lȷ���������","&2&l����ȷ��")){
                override fun onLeftClick(event: InventoryClickEvent, frame: CXFrame) {
                    super.onLeftClick(event, frame)
                    val operation=((this@ScriptSpecialRoleSelectFrame.mainPanel as CXPanel).inventory!!.getItem(2).itemMeta.displayName).replace("&3&l".toColor(),"")
                    val pointName=((this@ScriptSpecialRoleSelectFrame.mainPanel as CXPanel).inventory!!.getItem(4).itemMeta.displayName).replace("&6&l".toColor(),"")
                    if(operation=="���ò�����"||pointName=="���û�������"){
                        event.whoClicked.sendMessageWithColor("&4&l[����] �������ò������ͻ�������")
                        return
                    }
                    event.whoClicked.closeFrame()
                    event.whoClicked.askQuestion("&2&l������ [�趨ֵ]"){
                        var value:Double=try{
                            it.toDouble()
                        }
                        catch(exception:NumberFormatException){
                            event.whoClicked.sendMessage("&4&l[����] ������һ������")
                            event.whoClicked.openFrame(this@ScriptSpecialRoleSelectFrame)
                            return@askQuestion
                        }

                        val finalRole="cxpoint-$operation-$pointName-$value"
                        CXCommand.runWithoutPermission(event.whoClicked as Player,"cxsp s insert $scriptName $location \"$command\" $finalRole")
                        event.whoClicked.openFrame(ScriptInformationFrame(scriptName))
                        }

                    }
                }
                val backButton=object:CXButton(CXItemStack(Material.IRON_DOOR,1,"&b&l����","&b&l���ҷ���")){
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
                this.add(CXPanel(6,"&c&lCXPoint-��������ѡ�����".toColor()))
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
                                        lore=listOf("&3&l����ѡ�� $name Ϊ����").toColor()
                                    }
                                }
                                leftClick { inventoryClickEvent, cxFrame ->
                                    var panel=fatherFrame.mainPanel!! as CXPanel
                                    val newButton=object:CXButton(CXItemStack(Material.GOLD_INGOT,1,"&6&l$pointName","&6&l�������û�������")){
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
            panel("&3&l������ݲ��������ô���".toColor()){
                button(0,0){
                    itemStack{
                        type=Material.REDSTONE
                        meta{
                            displayName="&3&l>".toColor()
                            lore=listOf("&3&l��[���Ǯ��] > [�趨ֵ] ��ִ������ ����ִ��ʧ��").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l>","&3&l�������ò�����")){
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
                            lore=listOf("&3&l��[���Ǯ��] >= [�趨ֵ] ��ִ������ ����ִ��ʧ��").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l>=","&3&l�������ò�����")){
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
                            lore=listOf("&3&l��[���Ǯ��] < [�趨ֵ] ��ִ������ ����ִ��ʧ��").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l<","&3&l�������ò�����")){
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
                            lore=listOf("&3&l��[���Ǯ��] <= [�趨ֵ] ��ִ������ ����ִ��ʧ��").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l<=","&3&l�������ò�����")){
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
                            lore=listOf("&3&l��[���Ǯ��] == [�趨ֵ] ��ִ������ ����ִ��ʧ��").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l==","&3&l�������ò�����")){
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
                            lore=listOf("&3&l��[���Ǯ��] != [�趨ֵ] ��ִ������ ����ִ��ʧ��").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&l!=","&3&l�������ò�����")){
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
                            lore=listOf("&3&l��[���Ǯ��] >= [�趨ֵ] ��ִ������,���۳����[�趨ֵ]�Ļ��� ����ִ��ʧ��").toColor()
                        }

                    }
                    leftClick { inventoryClickEvent, cxFrame ->
                        var panel=fatherFrame.mainPanel!! as CXPanel
                        val newButton=object:CXButton(CXItemStack(Material.REDSTONE,1,"&3&lcost","&3&l�������ò�����")){
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
