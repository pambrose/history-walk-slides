package com.github.pambrose.slides

class Slide(
  val title: String,
  val success: Boolean,
  slideDeck: SlideDeck
) {
  var parentSlide: Slide? = null
  var content = mutableListOf<String>()
  var verticalChoices = true
  val choices = mutableMapOf<String, Slide>()
  val fqName: String = "${parentSlide?.fqName ?: ""}/$title"

  val hasChoices: Boolean
    get() = choices.isNotEmpty()

  init {
    require(!slideDeck.containsSlide(fqName)) { "Slide titles must be unique: $fqName" }
    slideDeck.assignSlide(this)
  }

  fun body(text: String) {
    content += text
  }

  fun verticalChoices() {
    verticalChoices = true
  }

  fun horizontalChoices() {
    verticalChoices = false
  }

  fun choice(text: String, slide: Slide) {
    choices[text] = slide
    slide.parentSlide = this
  }
}