package icu.takeneko.gridmap.screen;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import icu.takeneko.gridmap.all.AllNetworking;
import icu.takeneko.gridmap.map.MapData;
import icu.takeneko.gridmap.map.MapElement;
import icu.takeneko.gridmap.map.elements.CheckboxElement;
import icu.takeneko.gridmap.map.elements.LineElement;
import icu.takeneko.gridmap.map.elements.MarkElement;
import icu.takeneko.gridmap.map.elements.RectElement;
import icu.takeneko.gridmap.map.elements.TextElement;
import icu.takeneko.gridmap.networking.ServerboundAddComponentPacket;
import imgui.ImGui;
import imgui.type.ImString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static imgui.ImGui.*;

public class MapEditScreen extends Screen implements ImApp {
    private final float[] SELECTED_COLOR = {1, 1, 1, 1};
    private final float[] UNSELECTED_COLOR = {1, 0.5f, 0.5f, 0.5f};

    private final Minecraft minecraft = Minecraft.getInstance();
    private final MapData selectedMap;
    private final List<MapData> mapData;

    private float scale = 1;

    private float xOffset = 0;
    private float yOffset = 0;

    private float mouseCanvasX = 0;
    private float mouseCanvasY = 0;
    private boolean mouseInCanvas = false;
    private boolean firstPointSelected = false;

    private boolean askForText = false;
    private boolean askForCheckbox = false;

    private float[] currentColor = {1, 1, 1};

    private Vector2f firstPoint;
    private Vector2f secondPoint;

    private ImString name;

    private MapElement currentSelectedElement = null;

    private EditMode mode = EditMode.NONE;

    public MapEditScreen(List<MapData> mapData) {
        super(Component.literal("MapEdit"));
        this.mapData = new ArrayList<>(mapData);
        selectedMap = mapData.get(0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            xOffset += (float) (pDragX / scale);
            yOffset += (float) (pDragY / scale);
            return true;
        }
        return false;
    }

    public boolean checkHoveredOn(MapElement element) {
        int canvasSize = Math.min(width, height) - 10;
        float mouseX = this.mouseCanvasX - canvasSize / 2f;
        float mouseY = (canvasSize - this.mouseCanvasY) - canvasSize / 2f;
        System.out.println("x = " + mouseX + ", y = " + mouseY);
        Vector2f mousePosition = new Vector2f(mouseX + xOffset, mouseY + yOffset);
        int chunks = selectedMap.mapWidthInChunks();
        float pixelPerBlock = (float) canvasSize / (chunks * 16f);

        if (element instanceof CheckboxElement checkboxElement) {

        }
        if (element instanceof LineElement lineElement) {

        }
        if (element instanceof MarkElement markElement) {
            Vector2f position = new Vector2f(markElement.getX() * pixelPerBlock, markElement.getY() * pixelPerBlock);
            return position.distance(mousePosition) <= 4;
        }
        if (element instanceof RectElement rectElement) {
            Vector2f pos1 = new Vector2f(rectElement.getX1() * pixelPerBlock, rectElement.getY1() * pixelPerBlock);
            Vector2f pos2 = new Vector2f(rectElement.getX2() * pixelPerBlock, rectElement.getY2() * pixelPerBlock);
            float minX = Math.min(pos1.x, pos2.x);
            float maxX = Math.max(pos1.x, pos2.x);
            float minY = Math.min(pos1.y, pos2.y);
            float maxY = Math.max(pos1.y, pos2.y);

            return (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY);
        }
        if (element instanceof TextElement textElement) {

        }
        return false;
    }

    private Vector2f canvasToScreenPosition(Vector2f position) {
        int canvasSize = Math.min(width, height) - 10;
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        float canvasCenterX = ((centerX - canvasSize / 2f) * scale + xOffset * scale) + canvasSize / 2f;
        float canvasCenterY = ((centerY - canvasSize / 2f) * scale + yOffset * scale) + canvasSize / 2f;
        //position.y = canvasSize - position.y;
        return new Vector2f(canvasCenterX + position.x, canvasCenterY - position.y);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        float oldScale = scale;
        if (pDelta > 0) {
            scale += 0.1F;
        } else {
            scale -= 0.1F;
        }
        scale = Mth.clamp(scale, 1, 5);

        float zoomFactor = scale / oldScale;
        xOffset = (float) (xOffset + (pMouseX - xOffset) * (1 - zoomFactor));
        yOffset = (float) (yOffset + (pMouseY - yOffset) * (1 - zoomFactor));

        return true;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int canvasSize = Math.min(width, height) - 10;
        float mouseX = this.mouseCanvasX - canvasSize / 2f;
        float mouseY = this.mouseCanvasY - canvasSize / 2f;
        int chunks = selectedMap.mapWidthInChunks();
        float pixelPerBlock = (float) canvasSize / (chunks * 16f);

        if (pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (mode == EditMode.NONE && mouseInCanvas) {

            }
            if (mode == EditMode.TEXT && mouseInCanvas) {
                askForText = true;
                firstPoint = new Vector2f(
                    mouseX,
                    mouseY
                );
                return true;
            }
            if ((mode == EditMode.LINE || mode == EditMode.RECT) && mouseInCanvas) {
                if (!firstPointSelected) {
                    firstPoint = new Vector2f(
                        mouseX,
                        mouseY
                    );
                    firstPointSelected = true;
                    return true;
                }
                secondPoint = new Vector2f(
                    mouseX,
                    mouseY
                );
                MapElement element;
                if (mode == EditMode.LINE) {
                    element = new LineElement(
                        FastColor.ARGB32.color(
                            255,
                            (int) (currentColor[0] * 255),
                            (int) (currentColor[1] * 255),
                            (int) (currentColor[2] * 255)
                        ),
                        firstPoint.x / pixelPerBlock,
                        firstPoint.y / pixelPerBlock,
                        secondPoint.x / pixelPerBlock,
                        secondPoint.y / pixelPerBlock
                    );
                } else {
                    element = new RectElement(
                        FastColor.ARGB32.color(
                            255,
                            (int) (currentColor[0] * 255),
                            (int) (currentColor[1] * 255),
                            (int) (currentColor[2] * 255)
                        ),
                        firstPoint.x / pixelPerBlock,
                        firstPoint.y / pixelPerBlock,
                        secondPoint.x / pixelPerBlock,
                        secondPoint.y / pixelPerBlock
                    );
                }
                firstPointSelected = false;
                addElement(element);
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void addElement(MapElement element) {
        selectedMap.getElements().add(element);
        AllNetworking.sendToServer(new ServerboundAddComponentPacket(element, selectedMap.getUuid()));
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == GLFW.GLFW_KEY_ESCAPE) {
            minecraft.setScreen(null);
        }
        return true;
    }

    private void calculateMouse(int mouseX, int mouseY) {
        int canvasSize = Math.min(width, height) - 10;
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        float canvasCornerX = ((centerX - canvasSize / 2f) * scale + xOffset * scale);
        float canvasCornerY = ((centerY - canvasSize / 2f) * scale + yOffset * scale);
        if (mouseX >= canvasCornerX && mouseX <= canvasCornerX + canvasSize * scale
            && mouseY >= canvasCornerY && mouseY <= canvasCornerY + canvasSize * scale
        ) {
            mouseInCanvas = true;
            mouseCanvasX = (mouseX - canvasCornerX) / scale;
            mouseCanvasY = (mouseY - canvasCornerY) / scale;
        } else {
            mouseInCanvas = false;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        //renderBackground(guiGraphics);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        calculateMouse(pMouseX, pMouseY);
        PoseStack poseStack = guiGraphics.pose();
        poseStack.scale(scale, scale, scale);
        poseStack.translate(xOffset, yOffset, 0);
        int chunks = selectedMap.mapWidthInChunks();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        poseStack.translate(centerX, centerY, 0);
        int canvasSize = Math.min(width, height) - 10;
        int rectMinX = -canvasSize / 2;
        int rectMinY = -canvasSize / 2;
        float pixelPerBlock = (float) canvasSize / (chunks * 16f);

        guiGraphics.fill(rectMinX, rectMinY, rectMinX + canvasSize, rectMinY + canvasSize, 0x55dddddd);

        for (MapElement element : selectedMap.getElements()) {
            poseStack.pushPose();
            if (element instanceof CheckboxElement checkboxElement) {

            }
            if (element instanceof LineElement lineElement) {

            }
            if (element instanceof MarkElement markElement) {
                poseStack.translate(markElement.getX() * pixelPerBlock, -markElement.getY() * pixelPerBlock, 0);
                guiGraphics.fill(-2, -2, 2, 2, 0xff000000 | markElement.getColor());
                guiGraphics.drawString(minecraft.font, Component.translatable(markElement.getName()), 2, 2, 0xffffffff);
            }
            if (element instanceof RectElement rectElement) {
                guiGraphics.fill(
                    (int) (rectElement.getX1() * pixelPerBlock),
                    (int) (rectElement.getY1() * pixelPerBlock),
                    (int) (rectElement.getX2() * pixelPerBlock),
                    (int) (rectElement.getY2() * pixelPerBlock),
                    rectElement.getFillColor()
                );
            }
            if (element instanceof TextElement textElement) {
                poseStack.translate(textElement.getX() * pixelPerBlock, -textElement.getY() * pixelPerBlock, 0);
                guiGraphics.drawString(minecraft.font, textElement.getText(), 0, 0, 0xffffffff);
            }
            poseStack.popPose();
        }
        poseStack.pushPose();
        if (askForText) {
            setNextWindowPos(
                (float) (firstPoint.x * minecraft.getWindow().getGuiScale()),
                (float) (firstPoint.y * minecraft.getWindow().getGuiScale())
            );
            askForText();
        }
        debugMapInfo();
        toolsWindow();
        colorPalette();
        begin("Hovering");
        for (MapElement element : selectedMap.getElements()) {
            if (checkHoveredOn(element)) {
                text("hovering element = " + element);
            } else {
                text("hovering nothing");
            }
        }
        end();
    }

    @Override
    public void render(Window window) {
        //debugMapInfo();
    }

    private void askForText() {
        begin(tr("askForText"));
        inputText(tr("name"), name);
        if (button("ok")) {
            askForText = false;
            addElement(new TextElement(name.get(), (int) firstPoint.x, (int) firstPoint.y));
        }
        sameLine();
        if (button("cancel")) {
            askForText = false;
        }
        end();
    }

    private void askForCheckbox() {
        begin(tr("askForCheckbox"));
        inputText(tr("name"), name);
        if (button("ok")) {
            askForText = false;
            addElement(new TextElement(name.get(), (int) firstPoint.x, (int) firstPoint.y));
        }
        sameLine();
        if (button("cancel")) {
            askForText = false;
        }
        end();
    }

    private void colorPalette() {
        begin(tr("colorPalette"));
        ImGui.colorPicker3(tr("color"), currentColor);
        end();
    }

    private void toolsWindow() {
        begin(tr("tools"));
        if (radioButton(tr("add_text"), this.mode == EditMode.TEXT)) {
            if (this.mode == EditMode.TEXT) {
                this.mode = EditMode.NONE;
                return;
            }
            this.mode = EditMode.TEXT;
        }
        if (radioButton(tr("add_line"), this.mode == EditMode.LINE)) {
            if (this.mode == EditMode.LINE) {
                this.mode = EditMode.NONE;
                return;
            }
            this.mode = EditMode.LINE;
            askForText = false;
        }
        if (radioButton(tr("add_rect"), this.mode == EditMode.RECT)) {
            if (this.mode == EditMode.RECT) {
                this.mode = EditMode.NONE;
                return;
            }
            this.mode = EditMode.RECT;
            askForText = false;
        }
        if (radioButton(tr("add_checkbox"), this.mode == EditMode.CHECKBOX)) {
            if (this.mode == EditMode.CHECKBOX) {
                this.mode = EditMode.NONE;
                return;
            }
            this.mode = EditMode.CHECKBOX;
            askForText = false;
        }
        if (radioButton(tr("pencil"), this.mode == EditMode.PENCIL)) {
            if (this.mode == EditMode.PENCIL) {
                this.mode = EditMode.NONE;
                return;
            }
            this.mode = EditMode.PENCIL;
            askForText = false;
        }
        if (radioButton(tr("eraser"), this.mode == EditMode.ERASER)) {
            if (this.mode == EditMode.ERASER) {
                this.mode = EditMode.NONE;
                return;
            }
            this.mode = EditMode.ERASER;
            askForText = false;
        }
        if (radioButton(tr("none"), this.mode == EditMode.NONE)) {
            this.mode = EditMode.NONE;
            askForText = false;
        }
        end();
    }


    private String tr(String key) {
        return Component.translatable("ui.peacecraftgridmap." + key).getString();
    }

    private void debugMapInfo() {
        begin("MapInfo");
        text("Maps: ");
        for (MapData data : mapData) {
            text("  " + data.getUuid());
            text("    centerChunkX: " + data.getCenterChunkX());
            text("    centerChunkZ: " + data.getCenterChunkZ());
            text("    locked: " + data.isLocked());
            text("    scale:" + data.getScale());
            text("  Elements:");
            for (MapElement element : data.getElements()) {
                text("    " + element.toString());
            }
        }
        end();
    }

    public void update(List<MapData> data) {
        mapData.clear();
        mapData.addAll(data);
    }

    public enum EditMode {
        PENCIL, TEXT, LINE, RECT, CHECKBOX, ERASER, NONE
    }
}
