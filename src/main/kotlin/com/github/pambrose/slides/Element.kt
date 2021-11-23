package com.github.pambrose.slides

interface Element

class TextElement(val text: String) : Element

class ImageElement(val src: String, width: Int, height: Int) : Element
