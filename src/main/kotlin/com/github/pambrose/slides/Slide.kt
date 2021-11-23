package com.github.pambrose.slides

class Slide(val title: String, val success: Boolean, private val slideDeck: SlideDeck) {
  var parentSlide: Slide? = null
  var content = mutableListOf<Element>()
  var verticalChoices = true
  val choices = mutableMapOf<String, String>()

  val hasChoices: Boolean
    get() = choices.isNotEmpty()

  init {
    require(title !in slideDeck.allSlides.keys) { "Slide titles must be unique: $title" }
    slideDeck.allSlides[title] = this
  }

  fun addText(text: String) {
    content += TextElement(text)
  }

  fun addImage(src: String, width: Int, height: Int) {
    content += ImageElement(src, width, height)
  }

  fun verticalChoices() {
    verticalChoices = true
  }

  fun horizontalChoices() {
    verticalChoices = false
  }

  fun addChoice(choice: String, destination: String) {
    choices[choice] = destination
  }
}