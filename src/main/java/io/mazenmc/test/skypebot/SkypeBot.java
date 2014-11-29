package io.mazenmc.test.skypebot;

import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.Skype;
import com.skype.SkypeException;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SkypeBot {

    private static final TypeThread typeThread = new TypeThread();

    public static void main(String[] args) throws Exception {
        typeThread.start();
        Skype.setDeamon(false);
        Skype.addChatMessageListener(new ChatMessageListener() {
            @Override
            public void chatMessageReceived(ChatMessage received) throws SkypeException {
                handle(received);
            }

            @Override
            public void chatMessageSent(ChatMessage chatMessage) throws SkypeException {
                // doesn't matter
            }
        });
    }

    public static void handle(ChatMessage received) throws SkypeException {
        typeThread.responseQueue.add(received.getSenderDisplayName() + " said: " + received.getContent());
    }

    private static class TypeThread extends Thread {

        private final Robot robot;
        private Queue<String> responseQueue = new ConcurrentLinkedQueue<>();

        TypeThread() {
            Robot r = null;

            try {
                r = new Robot();
            } catch (AWTException ignored) {}

            robot = r;
        }

        @Override
        public void run() {
            while(true) {
                if(responseQueue.peek() != null) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    StringSelection stringSelection = new StringSelection(responseQueue.poll());
                    clipboard.setContents(stringSelection, (cb, contents) -> {});

                    robot.keyPress(KeyEvent.VK_CONTROL);
                    robot.keyPress(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_V);
                    robot.keyRelease(KeyEvent.VK_CONTROL);
                    robot.keyPress(KeyEvent.VK_ENTER);
                    robot.keyRelease(KeyEvent.VK_ENTER);
                }
            }
        }
    }
}
