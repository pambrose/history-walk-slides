package com.github.pambrose.slides

class Slide(
  val title: String,
  val success: Boolean,
  private val slideDeck: SlideDeck
) {
  var parentSlide: Slide? = null
  var content = mutableListOf<Element>()
  var verticalChoices = true
  var embeddedSlide = false
  val choices = mutableMapOf<String, Pair<String, Boolean>>()

  val hasChoices: Boolean
    get() = choices.isNotEmpty()

  init {
    require(title !in slideDeck.allSlides.keys) { "Slide titles must be unique: $title" }
    slideDeck.allSlides[title] = this
  }

  fun body(text: String) {
    content += TextElement(text)
  }

  fun image(src: String, width: Int, height: Int) {
    content += ImageElement(src, width, height)
  }

  fun verticalChoices() {
    verticalChoices = true
  }

  fun horizontalChoices() {
    verticalChoices = false
  }

  fun choice(choice: String, choiceTitle: String, advance: Boolean = false) {
    choices[choice] = choiceTitle to advance
  }

  fun choice(choice: String, slide: Slide, advance: Boolean = false) {
    choices[choice] = slide.title to advance
    slide.embeddedSlide = true
    slide.parentSlide = this
  }
}