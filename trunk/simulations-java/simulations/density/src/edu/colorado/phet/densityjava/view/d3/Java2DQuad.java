package edu.colorado.phet.densityjava.view.d3;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * A screen overlay or heads-up-display (HUD). Painting of the HUD is done using
 * Java 2D, in a different thread. This is done to prevent Java 2D dragging down
 * the framerate of the entire game.<br><br>
 * After adding the quad to the scene, the Java 2D painting thread can be
 * controlled using {@code #start()} and {@code stop()}. During the updates and
 * renders of the game {@code #requestUpdate()} and {@code #requestRender()} must
 * be called, which will at some point invoke {@code #paint(Graphics2D)} from the
 * Java 2D painting thread.
 *
 * @author Dennis Bijlsma
 *         modified by Sam Reid
 */

public abstract class Java2DQuad extends Quad implements Runnable {

    private int width;
    private int height;

    private BufferedImage image;
    private ImageStatus status;
    private boolean render;
    private float updateTime;
    private float currentTime;

    private TextureState state;
    private Image teximg;
    private ByteBuffer buffer;

    private enum ImageStatus {
        DIRTY,
        PAINTING,
        AVAILABLE,
        RENDERING,
        STOPPED
    }

    /**
     * Creates a new overlay with the specified dimensions. Note that the Y
     * coordinateis measured from the bottom of the display.
     */

    public Java2DQuad(String name, int width, int height) {
        super(name, width, height);

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid dimensions: " + width + "x" + height);
        }

        this.width = width;
        this.height = height;

        image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        status = ImageStatus.DIRTY;
        render = false;
        updateTime = 0f;
        currentTime = 0f;

        // Create the quad

//        quad = new Quad("Java2dOverlay", width, height);
//        setRenderQueueMode(Renderer.QUEUE_ORTHO);
        setCullHint(Spatial.CullHint.Never);
        setLightCombineMode(Spatial.LightCombineMode.Off);
//        setLocalTranslation(width / 2f, height / 2f, 0f);
        updateRenderState();

        // Use the BufferedImage as texture

        teximg = new Image();
        teximg.setFormat(Image.Format.RGBA8);
        teximg.setWidth(image.getWidth());
        teximg.setHeight(image.getHeight());

        Texture tex = TextureManager.loadTexture(image,
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear, true);
        tex.setImage(teximg);
        tex.setApply(Texture.ApplyMode.Modulate);

        buffer = ByteBuffer.allocateDirect(4 * image.getWidth() * image.getHeight());

        FloatBuffer texCoords = BufferUtils.createVector2Buffer(4);
        texCoords.put(getTextureU(0)).put(getTextureV(image.getHeight()));
        texCoords.put(getTextureU(0)).put(getTextureV(0));
        texCoords.put(getTextureU(image.getWidth())).put(getTextureV(0));
        texCoords.put(getTextureU(image.getWidth())).put(getTextureV(image.getHeight()));
        setTextureCoords(new TexCoords(texCoords), 0);

        state = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        state.setEnabled(true);
        state.setTexture(tex);
        setRenderState(state);
        updateRenderState();

        BlendState bs = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        bs.setEnabled(true);
        bs.setBlendEnabled(true);
        bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        bs.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        bs.setTestEnabled(false);
        setRenderState(bs);
        updateRenderState();

        paint();
        render();
    }

    /**
     * Requests an update of the overlay as soon as possible. Whether the update
     * is actually done depends on the current state of the overlay.
     */

    public void requestUpdate(float dt) {

        if (currentTime >= updateTime) {
            render = true;
            currentTime = 0f;
        } else {
            render = false;
            currentTime += dt;
        }
    }

    /**
     * Requests a render of the overlay as soon as possible. Whether the render
     * is done depends if the overlay's contents have changed since the last one.
     */

    public void requestRender() {

        if ((render) && (status == ImageStatus.AVAILABLE)) {
            setStatus(ImageStatus.RENDERING);
            render();
            setStatus(ImageStatus.DIRTY);
        }
    }

    /**
     * Paints the overlay for the current frame. Note that this method is called
     * in a different thread from the main game thread.
     */

    private void paint() {

        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setClip(0, 0, image.getWidth(), image.getHeight());
        Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1f));
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getGraphicsWidth(), getGraphicsHeight());
        g2.setComposite(composite);
        paint(g2);
        g2.dispose();
    }

    /**
     * Paints the overlay. Note that this method is called in a different thread
     * from the game thread.
     */

    public abstract void paint(Graphics2D g2);

    /**
     * Updates the quad's texture with the contents of the BufferedImage.
     */

    private void render() {

        byte[] data = (byte[]) image.getRaster().getDataElements(0, 0,
                image.getWidth(), image.getHeight(), null);
        buffer.clear();
        buffer.put(data, 0, data.length);
        buffer.rewind();

        teximg.setData(buffer);
        state.deleteAll();
    }

    /**
     * Sets the update interval of this overlay. This interval can be different
     * from that of the rest of the game. A value of 0 indicates that the overlay
     * will be updated every frame.
     */

    public void setUpdateTime(float updateTime) {
        this.updateTime = updateTime;
        this.currentTime = 0f;
    }

    /**
     * Returns the update interval of this overlay. This interval can be different
     * from that of the rest of the game. A value of 0 indicates that the overlay
     * will be updated every frame.
     */

    public float getUpdateTime() {
        return updateTime;
    }

    /**
     * Changes the paint status to the specified value.
     */

    private synchronized void setStatus(ImageStatus newStatus) {
        status = newStatus;
    }

    /**
     * Starts the internal thread that will paint this overlay.
     */

    public void start() {
        Thread t = new Thread(this, "jMonkeyEngine-Java2dOverlay");
        t.start();
    }

    /**
     * Stops the internal thread that will paint this overlay.
     */

    public void stop() {
        setStatus(ImageStatus.STOPPED);
    }

    /**
     * Animation loop that repaints the images. This method will run until it
     * is manually stopped using {@code #stop()}.
     */

    public void run() {

        while (status != ImageStatus.STOPPED) {
            if (status == ImageStatus.DIRTY) {
                setStatus(ImageStatus.PAINTING);
                paint();
                setStatus(ImageStatus.AVAILABLE);
            }

            Thread.yield();
        }
    }

    public int getGraphicsWidth() {
        return width;
    }

    public int getGraphicsHeight() {
        return height;
    }

    private float getTextureU(int x) {
        return x / width;
    }

    private float getTextureV(int y) {
        return 1f - y / height;
    }
}
