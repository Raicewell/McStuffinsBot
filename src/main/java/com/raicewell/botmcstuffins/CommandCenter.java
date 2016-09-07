/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.raicewell.botmcstuffins;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import javax.sound.sampled.UnsupportedAudioFileException;
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
import sx.blah.discord.util.audio.AudioPlayer;

/**
 *
 * @author Raice
 */
public class CommandCenter {
    private static java.util.ArrayList<String> commandList;
    private static String resourcePath;
    private static boolean isReady = false;
    protected IDiscordClient botClient;
    
    public CommandCenter(IDiscordClient client){
        botClient = client;
    }
    
     private void initializeDefaultCommands(){
        commandList = new ArrayList<>();
        commandList.add("cmd");
        commandList.add("logout");
        
        //Initialize resource path for clips.
        resourcePath = System.getProperty("java.class.path").split(";")[0] + "\\";
        System.out.println(resourcePath);
        
        //If the class path isn't the classes folder, this is being run from a jar. Find that path!
        if(!resourcePath.contains("classes")){
        resourcePath = resourcePath.substring(0, resourcePath.indexOf("/"))+ "/classes/";
        System.out.println(resourcePath);
        }
       
        System.out.println("Resource path found: " + resourcePath);
        
        //Get the files in the resource path.
        File[] fileList = new File(resourcePath).listFiles();
        for(File file: fileList){
            System.out.println(file.getName());
            if(file.getName().endsWith(".wav")){
                addCommand(file.getName().replace(".wav", ""));
            }
        }
       
        System.out.println("Found " + commandList.size() + " commands.");
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
        try{
            AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(channel.getGuild());
            channel.join();            
            File getFile = new File(resourcePath + filename + ".wav");                       
            player.queue(getFile);
            while(player.getPlaylistSize() > 0){}
            channel.leave();
        }
        catch(IOException | UnsupportedAudioFileException | MissingPermissionsException | java.lang.NullPointerException e){
            System.out.println("Error Thrown in PlayClip attempting to play " + filename);
            System.out.println("Resource Path: " + resourcePath);
            e.printStackTrace();
            channel.leave();
        }
    }
    
    public void executeCommand (String[] commandParams, IMessage message){
        String command = commandParams[0];
        if(command.equals("cmd")) {getCommands(message.getChannel());}
        else if(command.equals("logout")) {
            if(message.getAuthor().getName().contains("Raice")){
                logout();
            }
        }
        else if(!message.getAuthor().getConnectedVoiceChannels().isEmpty()){
            IVoiceChannel vChannel = message.getAuthor().getConnectedVoiceChannels().get(0);
            playClip(vChannel, command);
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
                    if(commandList.contains(eventCommand[0])){
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
