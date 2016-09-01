/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raicewell.botmcstuffins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.modules.IModule;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

/**
 *
 * @author Raice
 */
public class CommandCenter {
    private static java.util.ArrayList<String> commandList;
    private static boolean isReady = false;
    protected IDiscordClient botClient;
    
    public CommandCenter(IDiscordClient client){
        botClient = client;
    }
    
     private static void initializeDefaultCommands(){
        commandList = new ArrayList<>();
        addCommand("!!");
        addCommand("cmd");        
        addCommand("logout");
        addCommand("pantyraid");
        addCommand("420");
        addCommand("fdup");
        addCommand("fdup_long");
        addCommand("grape");
        addCommand("chum");
        addCommand("starfish");
    }
    
    protected void getCommands(IChannel channel){
        sendMessage(channel, "The active commands currently are: " + String.join(", ", commandList));
    }
    
    protected void logout(){
        try{
            for(IVoiceChannel channel : botClient.getConnectedVoiceChannels()){channel.leave();}
            botClient.logout();
        }
        catch(DiscordException | RateLimitException ex){
            ex.printStackTrace();
        }
    }
       
    public static void addCommand(String command){
        commandList.add(command);
    }
    
    private void sendMessage(IChannel channel, String text){
        RequestBuffer.request(() -> {
            try{
                new MessageBuilder(botClient).withChannel(channel).withContent(text).build();
            }
            catch (DiscordException | MissingPermissionsException ex){
                ex.printStackTrace();
            }
        });
    }
    
    public void playClip (IVoiceChannel channel, String filename){
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(channel.getGuild());
        try{
           IVoiceChannel targetChannel = channel;
           targetChannel.join();
           ClassLoader classLoader = getClass().getClassLoader();
           try{
            File getFile = new File(classLoader.getResource(filename + ".wav").getFile());
            player.queue(getFile);
            while(player.getPlaylistSize() > 0){}
            targetChannel.leave();
           }
           catch (NullPointerException E){
               System.out.println(classLoader.getResource(filename + ".wav"));
           }
           
        }
        catch(IOException | UnsupportedAudioFileException | MissingPermissionsException e){
            System.out.println(e);
        }
    }
    
    public void executeCommand (String[] commandParams, IMessage message){
        String command = commandParams[0];
        if(command.equals("cmd")) {getCommands(message.getChannel());}
        else if(command.equals("logout")) {logout();}
        else if(!message.getAuthor().getConnectedVoiceChannels().isEmpty()){
            playClip(message.getAuthor().getConnectedVoiceChannels().get(0), command);
        }
    }
    
    public void registerListener(IModule module){
        botClient.getDispatcher().registerListener(module);
    }
    
    public void unregisterListener(IModule module){
        botClient.getDispatcher().unregisterListener(module);
    }
    
    @EventSubscriber
    public void onReadyState(ReadyEvent event) throws DiscordException{
        isReady = true;
        initializeDefaultCommands();
        System.out.println("Bot ready for commands!");            
    }

    @EventSubscriber
    public void onIncomingMessage(MessageReceivedEvent event){
        if(isReady == true){
            String inputString = event.getMessage().getContent();
            //If the string doesn't start with our key, skip.
            if(inputString.startsWith("!!")){
                if(inputString.equals("!!")){
                    sendMessage(event.getMessage().getChannel(), "Yep, McStuffinsBot online.");
                }
                else{
                    //Remove command header.
                    inputString = inputString.substring(2);
                    String[] eventCommand = inputString.split(" ");

                    //If the command exists, execute. 
                    if(commandList.contains(eventCommand[0].toLowerCase())){
                        executeCommand(eventCommand, event.getMessage());
                    }
                    else{
                        System.out.println("Received invalid command: " + inputString);
                        sendMessage(event.getMessage().getChannel(), "Dunno if you were talking to me, but that's not one of my active commands. Try !!cmd for active commands. If that doesn't work, too bad!");
                    }
                }
            }
        }
    }
    
}
