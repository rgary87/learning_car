//package lwjgl;
//
//
//import fr.rgary.learningcar.display.Font;
//import org.lwjgl.*;
//
//import java.nio.*;
//
//import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.stb.STBEasyFont.*;
//
///** STB Easy Font demo. */
//public final class EasyFont extends FontDemo {
//
//    private static final int BASE_HEIGHT = 12;
//
//    private EasyFont(String filePath) {
//        super(BASE_HEIGHT, filePath);
//    }
//
//    public static void main(String[] args) {
//        String filePath;
//        if (args.length == 0) {
//            System.out.println("Use 'ant demo -Dclass=org.lwjgl.demo.stb.EasyFont -Dargs=<path>' to load a different text file (must be UTF8-encoded).\n");
//            filePath = "doc/HELP.md";
//        } else {
//            filePath = args[0];
//        }
//
//        new EasyFont("filePath").run("STB Easy Font Demo");
//    }
//
//    @Override
//    protected void loop() {
//        ByteBuffer firstCharBuffer;
//        ByteBuffer secondCharBuffer;
//
//        String firstText = "FIRST_TEXT";
//        String secondText = "SECOND_TEXT";
//
//        Font FONT = null;
//        try {
//            FONT = new Font("src/main/resources/times.ttf", 25f);
//        } catch (Exception ignore) {
//            return;
//        }
//
//        while (!glfwWindowShouldClose(getWindow())) {
//            glfwPollEvents();
//
//            /*
////            START ?!
//            firstCharBuffer = BufferUtils.createByteBuffer(firstText.length() * 270);
//            int firstQuads = stb_easy_font_print(0, 0, firstText, null, firstCharBuffer);
//
//
//            glEnableClientState(GL_VERTEX_ARRAY);
//            glVertexPointer(2, GL_FLOAT, 16, firstCharBuffer);
//
//            glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
//            glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color
//
//            glPushMatrix();
//
//            glDrawArrays(GL_QUADS, 0, firstQuads * 4);
//
//            glPopMatrix();
//
////            END ?!
//
////            START 2 ?!
//
//            secondCharBuffer = BufferUtils.createByteBuffer(secondText.length() * 270);
//            int secondQuads = stb_easy_font_print(10, 50, secondText, null, secondCharBuffer );
////            glEnableClientState(GL_VERTEX_ARRAY);
//            glVertexPointer(2, GL_FLOAT, 16, secondCharBuffer);
//
//            glPushMatrix();
//            // Zoom
//            glScalef(2, 2, 1f);
//
//            glDrawArrays(GL_QUADS, 0, secondQuads * 4);
//
//            glPopMatrix();
//
////            END 2 ?!
//*/
//            glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
//
////            this.drawAnyText(firstText, 0, 0);
////            this.drawAnyText(secondText, 10, 50);
//
//            FONT.drawText(String.format("%d FPS %n", System.currentTimeMillis() - 12), 10, 10);
//
//            glfwSwapBuffers(getWindow());
//            glClear(GL_COLOR_BUFFER_BIT);
//
//        }
//
//        glDisableClientState(GL_VERTEX_ARRAY);
//    }
//
//    public void drawAnyText(String text, float x, float y) {
//        this.drawAnyText(text, x, y, 169f, 183f, 198f);
//    }
//
//    public void drawAnyText(String text, float x, float y, float r, float g, float b) {
//        ByteBuffer charBuffer = BufferUtils.createByteBuffer(text.length() * 270);
//        int firstQuads = stb_easy_font_print(x, y, text, null, charBuffer);
//
//        glEnableClientState(GL_VERTEX_ARRAY);
//        glVertexPointer(2, GL_FLOAT, 16, charBuffer);
//
//        glColor3f(r / 255f, g / 255f, b / 255f); // Text color
//
//        glPushMatrix();
//
//        glDrawArrays(GL_QUADS, 0, firstQuads * 4);
//
//        glPopMatrix();
//    }
//
//}
