/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raicewell.botmcstuffins;

import com.raicewell.botmcstuffins.CommandCenter;
import java.util.ArrayList;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.UserVoiceChannelJoinEvent;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;

/**
 *
 * @author Raice
 */ 
public class QuickieQuoterModule implements IModule {
    static CommandCenter cCenter;
    
    @Override
    public boolean enable(IDiscordClient client) {
        cCenter.registerListener(this);
        initializeCommandList();
        return true;
    }
 
    @Override
    public void disable()  {
        cCenter.unregisterListener(this);
        //removeQQcommands
    }

    @Override
    public String getName() {
            return "QuickieQuoter";
    }

    @Override
    public String getAuthor() {
            return "Raice McStuffins";
    }

    @Override
    public String getVersion() {
            return "1.0";
    }

    @Override
    public String getMinimumDiscord4JVersion() {
            return "2.4.4";
    } 

   
    private static void initializeCommandList(){
        cCenter.addCommand("pantyraid");
        cCenter.addCommand("420");
        cCenter.addCommand("fdup");
        cCenter.addCommand("fdup_long");
        cCenter.addCommand("grape");
        cCenter.addCommand("chum");
        cCenter.addCommand("starfish");
    }
    
//    protected static class MessageInterceptor_QQ{        
//        @EventSubscriber
//        public void onPlayerJoinedVoice(UserVoiceChannelJoinEvent event) throws MissingPermissionsException{
//            if(event.getUser().getName().equals("Raice") || event.getUser().getName().equals("Hunter")  ){
//                String[] command = new String[1];
//                command[0] = "420";
//                cCenter.executeCommand(command, );
//            }
//        }
//    }

}
