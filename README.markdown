Chum: Android Game Framework
============================


* [Overview](#overview)
* [Current State](#current)
* [What's next](#roadmap)
* [How to contribute](#contribute)


<h2 id="overview">Overview</h2>

This is another of the many game frameworks / engines being created for Android.  I have borrowed ideas from others, but I haven't liked the overall approach of any of them.  So this is my way of doing things.

The basic structure is that the whole game / activity is set up as a tree of GameNodes.  The tree is divided into two parts: the logic subtree, and the rendering subtree.  Communication between nodes is inteded to be done primarily by GameEvents, which propogate up and down the branches of the tree.

All rendering is done with OpenGL.  Chum provides many shortcuts for common things, but makes direct OpenGL access simple as well.  Both floating-point and fixed-point constructs are supported

<h2 id="current">Current State</h2>

I'm just getting started, and really haven't proved out how well the approach will work on a large scale.  But at this point, the main pieces are there:

* GameActivity which sets up the main activity, and manages the GLSurfaceView for rendering
* GameTree and various GameNode subclasses for implementing both the logic and the rendering
* 2D (orthographic) and 3D (perspective) projections are supported, and can be mixed (3D world + 2D overlay)
* The GameEvent system is in place for coordinating what's going on.
* Several examples are included to show the basics


<h2 id="roadmap">What's next</h2>

The next major bit of functionality is the get Sprites and SpriteBatches working.

Then, I need to implement a real game...



<h2 id="roadmap">How to contribute</h2>

