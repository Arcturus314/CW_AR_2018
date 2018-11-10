package com.example.myfirstapp;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    private FloatBuffer vertexBuffer;

    // number of coordinates per vertex in buffer
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            0.0f, 0.1f, 0.0f,    // top
            -0.1f, 0f, 0.0f,  // bottom left
            0.1f, 0f, 0.0f   // bottom right
    };

    // define colour: r, g, b, opacity
    float colour[] = {0.63f, 0.456f, 0.222f, 1.0f};

    // define simple vertex and fragment shaders
    // this is all boilerplate taken from a rendering tutorial

    // code to paint the vertices of the triangle
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;

    // code to render faces of triangle with colours and such
    public final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";


    // program for running vertex, fragment shaders
    // to render the triangle
    private final int mProgram;

    private int mPositionHandle;
    private int mColourHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Triangle() {
        // initialise ByteBuffer for triangle coordinates
        // allocate 4 bytes (float) for all coordinates
        ByteBuffer buffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        // use the device hardware's native byte order
        // little endian or big endian
        buffer.order(ByteOrder.nativeOrder());
        // create a float buffer from the byte buffer
        vertexBuffer = buffer.asFloatBuffer();
        // add coordinates to the vertex buffer
        vertexBuffer.put(triangleCoords);
        // set buffer to start reading from first coordinate
        vertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // create empty openGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add vertex and fragment shaders to the program
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // create executable
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        // add program to this OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // prepare triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColourHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // set colour for drawing the triangle
        GLES20.glUniform4fv(mColourHandle, 1, colour,  0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}