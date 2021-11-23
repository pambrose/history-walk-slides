package com.github.pambrose.slides

interface Element

class TextElement(val text: String) : Element

class ImageElement(val src: String, val width: Int, val height: Int) : Element
