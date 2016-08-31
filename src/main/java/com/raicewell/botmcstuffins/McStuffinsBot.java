/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raicewell.botmcstuffins;

import sx.blah.discord.api.*;
import sx.blah.discord.modules.ModuleLoader;
import sx.blah.discord.util.DiscordException;

/**
 *
 * @author Raice
 */
public class McStuffinsBot {
    protected static IDiscordClient botClient;
    protected static McStuffinsBot bot;
    private static ModuleLoader modLoader;
    
    public static void main(String[] args) throws InterruptedException {
        bot = new McStuffinsBot();
    }
    
    public McStuffinsBot() throws InterruptedException{
        System.out.println("Creating Client and logging in.");
        try{
            botClient = new ClientBuilder().withToken(getToken()).login();
            botClient.getDispatcher().registerListener(new CommandCenter(botClient));
//            modLoader = new ModuleLoader(botClient);
//            enableModules();
        }
        catch(DiscordException ex){
            System.out.println(ex.getErrorMessage());
        }        
    }
    
    private String getToken(){
        System.out.println("Getting token");
        return "MjEwMjA3NjczMTIyMDI5NTY4.CqD2jA.XWQtYVkFD42oXIjy6v-pgZDiMgo";
    }
    
    private static void enableModules(){
//        File qqModule = new File("")
//        modLoader.loadExternalModules("");
    }
} 
