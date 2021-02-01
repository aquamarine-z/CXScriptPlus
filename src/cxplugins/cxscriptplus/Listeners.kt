package cxplugins.cxscriptplus

import cxplugins.cxfundamental.minecraft.kotlindsl.getData
import cxplugins.cxfundamental.minecraft.kotlindsl.getNBTValue
import cxplugins.cxfundamental.minecraft.kotlindsl.hasNBTValue
import cxplugins.cxfundamental.minecraft.kotlindsl.sendMessageWithColor
import cxplugins.cxfundamental.minecraft.server.CXRandom
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

class Listeners: Listener {
    /*@EventHandler
    fun onPlayerClickInventory(event:InventoryClickEvent){
        if(event.whoClicked.hasMetadata("viewingFrame")){
            var rawSlot=event.rawSlot
            if(event.click== ClickType.LEFT){
                var frameName=event.whoClicked.getMetadata("viewingFrame")[0].asString()
                var configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
                var script=configuration.get("$frameName.leftClick.$rawSlot",null) as? CXScript
                if(script==null) return
                else{
                    executeScript(event.whoClicked as Player,script)
                }
            }
            if(event.click== ClickType.RIGHT){
                var frameName=event.whoClicked.getMetadata("viewingFrame")[0].asString()
                var configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
                var script=configuration.get("$frameName.rightClick.$rawSlot",null) as? CXScript
                if(script==null) return
                else{
                    executeScript(event.whoClicked as Player,script)
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerCloseInventory(event: InventoryCloseEvent){
        if(event.player.hasMetadata("editingFrame")){
            val inventory=event.inventory
            //println(inventory==null)
            //println(inventory?.contents)
            val frameName=event.player.getMetadata("editingFrame")[0].asString()
            event.player.removeMetadata("editingFrame",CXScriptPlus.plugin)
            val configuration= CXYamlConfiguration("CXPlugins\\CXScriptPlus", "frames.yml")
            configuration.set("$frameName.inventory",inventory)
            configuration.save()
            event.player.sendMessageWithColor("&6[CXScriptPlus]做出的更改已成功保存!")

        }
        if(event.player.hasMetadata("viewingFrame")){
           event.player.removeMetadata("viewingFrame",CXScriptPlus.plugin)
        }
    }*/
    @EventHandler
    fun onPlayerBreakBlock(event:BlockBreakEvent){
        var block=event.block
        if(block.getData("CXScriptPlus.bindBreak")!=null){
            event.isCancelled=true
            var script= getScriptByName(block.getData("CXScriptPlus.bindBreak") as String)
            if(script==null){
                event.player.sendMessageWithColor("&4[错误] 需要触发的脚本${block.getData("CXScriptPlus.bindBreak")} 不存在!")
                return
            }
            if(checkCooldown(event.player,block.getData("CXScriptPlus.bindBreak") as String)) {
                executeScript(event.player, script)
            }
        }
    }
    @EventHandler
    fun onPlayerWalkOnBlock(event:PlayerMoveEvent){
        if(CXRandom.lottery(70.0)) return  //因为MoveEvent的触发频率太高 对此进行降频操作 平均触发时间0.4S
        if(!event.player.isOnGround)return
        else{
            var location=event.player.location
            var block=location.world.getBlockAt(location.add(0.0,-1.0,0.0))
            if(block.getData("CXScriptPlus.bindWalk")!=null){
                //event.isCancelled=true
                var script= getScriptByName(block.getData("CXScriptPlus.bindWalk") as String)
                if(script==null){
                    event.player.sendMessageWithColor("&4[错误] 需要触发的脚本${block.getData("CXScriptPlus.bindWalk")} 不存在!")
                    return
                }
                if(checkCooldown(event.player,block.getData("CXScriptPlus.bindWalk") as String)) {
                    executeScript(event.player, script)
                }

            }
        }
    }
    @EventHandler
    fun onPlayerInteractBlock(event:PlayerInteractEvent){
        if(event.action==Action.LEFT_CLICK_BLOCK||event.action==Action.RIGHT_CLICK_BLOCK){

            var block=event.clickedBlock
            if((block.getData("CXScriptPlus.bindLeft") !=null)||(block.getData("CXScriptPlus.bindRight") !=null)) event.isCancelled=true
            if((block.getData("CXScriptPlus.bindLeft")!=null)&&event.action==Action.LEFT_CLICK_BLOCK){
                event.isCancelled=true
                var script= getScriptByName(block.getData("CXScriptPlus.bindLeft") as String)
                if(script==null){
                    event.player.sendMessageWithColor("&4[错误] 需要触发的脚本${block.getData("CXScriptPlus.bindLeft")} 不存在!")
                    return
                }

                if(checkCooldown(event.player,block.getData("CXScriptPlus.bindLeft") as String)) {
                    executeScript(event.player, script)
                }
            }
            if((block.getData("CXScriptPlus.bindRight")!=null)&&event.action==Action.RIGHT_CLICK_BLOCK){
                event.isCancelled=true
                var script= getScriptByName(block.getData("CXScriptPlus.bindRight") as String)
                if(script==null){
                    event.player.sendMessageWithColor("&4[错误] 需要触发的脚本${block.getData("CXScriptPlus.bindRight")} 不存在!")
                    return
                }

                if(checkCooldown(event.player,block.getData("CXScriptPlus.bindRight") as String)) {
                    executeScript(event.player, script)
                }
            }

        }
    }
    @EventHandler
    fun onPlayerInteractWithItemInHand(event:PlayerInteractEvent){
        var item=event.player.itemInHand
        if(item.hasNBTValue("CXScriptPlus","bindleft")&&(event.action==Action.LEFT_CLICK_AIR||event.action==Action.LEFT_CLICK_BLOCK)){
            event.isCancelled=true
            var scriptName=item.getNBTValue("CXScriptPlus","bindleft") as String
            if(!isScriptExist(scriptName)){
                event.player.sendMessageWithColor("&4[错误] 绑定的脚本名不存在")
                return
            }
            else{
                var script= getScriptByName(scriptName)
                if(checkCooldown(event.player,scriptName)) {
                    executeScript(event.player,script!!)
                }
            }

        }
        if(item.hasNBTValue("CXScriptPlus","bindright")&&(event.action==Action.RIGHT_CLICK_AIR||event.action==Action.RIGHT_CLICK_BLOCK)){
            event.isCancelled=true
            var scriptName=item.getNBTValue("CXScriptPlus","bindright") as String
            if(!isScriptExist(scriptName)){
                event.player.sendMessageWithColor("&4[错误] 绑定的脚本名不存在")
                return
            }
            else{
                var script= getScriptByName(scriptName)
                if(checkCooldown(event.player,scriptName)) {
                    executeScript(event.player,script!!)
                }
            }
        }

    }
}