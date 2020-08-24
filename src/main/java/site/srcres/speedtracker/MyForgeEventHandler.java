/*
 * SpeedTracker - A MinecraftForge mod that offers speed information in the game
 * Copyright (C) 2020  src_resources
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package site.srcres.speedtracker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class MyForgeEventHandler {
    private boolean isSpeedTrackerThreadRunning = false;
    private Thread speedTrackerThread;
    private PlayerEntity playerToTrack = null;
    private double lastPosX = 0;
    private double lastPosY = 0;
    private double lastPosZ = 0;
    double deltaX = 0.0;
    double deltaY = 0.0;
    double deltaZ = 0.0;
    double deltaTotal = 0.0;

//    @SubscribeEvent
//    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
//        if (event.getPlayer().equals(Minecraft.getInstance().getRenderViewEntity())) {
//            playerToTrack = event.getPlayer();
//            initSpeedTrackerThread();
//            isSpeedTrackerThreadRunning = true;
//            speedTrackerThread.start();
//        }
//    }
//
//    @SubscribeEvent
//    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
//        if (isSpeedTrackerThreadRunning) {
//            isSpeedTrackerThreadRunning = false;
//            speedTrackerThread.interrupt();
//        }
//    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                if (isSpeedTrackerThreadRunning) {
                    isSpeedTrackerThreadRunning = false;
                    speedTrackerThread.interrupt();
                }
            } else {
                if (!isSpeedTrackerThreadRunning) {
                    playerToTrack = mc.player;
                    initSpeedTrackerThread();
                    isSpeedTrackerThreadRunning = true;
                    speedTrackerThread.start();
                }
            }
        }
    }

    private void initSpeedTrackerThread() {
        speedTrackerThread = new Thread(this::trackSpeed);
    }

    private void trackSpeed() {
        System.out.println("Speed tracker thread is starting...");
        for (;;) {
            try {
                double posX = playerToTrack.getPosX();
                double posY = playerToTrack.getPosY();
                double posZ = playerToTrack.getPosZ();
//                if (lastPos == null) {
//                    System.out.println("Last position is null, initializing...");
//                } else {
//                    int deltaX = Math.abs(pos.getX() - lastPos.getX());
//                    int deltaY = Math.abs(pos.getY() - lastPos.getY());
//                    int deltaZ = Math.abs(pos.getZ() - lastPos.getZ());
//                    int total = (int) Math.round(Math.sqrt(Math.pow(deltaX, 2)
//                            + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2)));
//                    System.out.println("Speed on X: " + deltaX);
//                    System.out.println("Speed on Y: " + deltaY);
//                    System.out.println("Speed on Z: " + deltaZ);
//                    System.out.println("Speed total: " + total);
//                    playerToTrack.sendMessage(new StringTextComponent(total + " m/s"));
//                    playerToTrack.sendMessage(new StringTextComponent(
//                            ((int) Math.round(3.6 * total)) + " km/h"));
//                }
                deltaX = Math.abs(lastPosX - posX);
                deltaY = Math.abs(lastPosY - posY);
                deltaZ = Math.abs(lastPosZ - posZ);
                deltaTotal = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));
//                playerToTrack.sendMessage(new StringTextComponent(((int) Math.round(deltaTotal)) + " m/s"));
//                playerToTrack.sendMessage(new StringTextComponent(
//                        ((int) Math.round(3.6 * deltaTotal)) + " km/h"));
                lastPosX = posX;
                lastPosY = posY;
                lastPosZ = posZ;
//                System.out.println(String.format("posY: %.5f, %d; %.5f, %d", posY, (int) Math.round(posY),
//                        Math.abs(1000.0 - posY), (int) Math.round(Math.abs(1000.0 - posY))));
//                try {
//                    fos.write(String.format("%d,\n", (int) Math.round(total)).getBytes(Charset.forName("utf-8")));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                System.out.println("Speed tracker thread is shutting down...");
                break;
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            String msg0 = I18n.format("speedtracker.unit.m_s.d", Math.round(deltaTotal));
            String msg1 = I18n.format("speedtracker.unit.km_h.d", Math.round(deltaTotal * 3.6));
            int top = 0;
            FontRenderer fr = Minecraft.getInstance().fontRenderer;
            fr.drawStringWithShadow(msg0, 2, top, 14737632);
            top += fr.FONT_HEIGHT;
            fr.drawStringWithShadow(msg1, 2, top, 14737632);
        }
    }
}
