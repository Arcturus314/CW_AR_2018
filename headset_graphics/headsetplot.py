#Kaveh Pezeshki
#9/10/2018
#Basic 3D plotting for the Clay-Wolkin pilot augmented reality project

#Uses pyglet for fast OpenGL plotting

triangle_color   = (0,1,0)

from pyglet.gl import *
window = pyglet.window.Window(1280, 720, resizable=False)

#we now create a basic triangle
vlist = pyglet.graphics.vertex_list(3, ('v2f', [0,0, 400,50, 200,300]))

#we set draw mode to wireframe
glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
#we then set desired line thickness
glLineWidth(3)

@window.event
def on_draw():
    window.clear()
    #we draw the triangles
    glColor3f(triangle_color[0], triangle_color[1], triangle_color[2])
    vlist.draw(GL_TRIANGLES)

pyglet.app.run()
