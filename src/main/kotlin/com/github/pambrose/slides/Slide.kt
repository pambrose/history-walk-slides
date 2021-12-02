package com.github.pambrose.slides

class Slide(
  val title: String,
  val content: String,
  val success: Boolean,
  val slideDeck: SlideDeck
) {
  var parentSlide: Slide? = null
  var verticalChoices = true
  val choices = mutableMapOf<String, Slide>()

  val fqName: String
    get() = "${parentSlide?.fqName ?: ""}/$title"

  val hasChoices: Boolean
    get() = choices.isNotEmpty()

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

  fun validateSlide() {
    if (choices.map { it.value.title }.toSet().size != choices.size)
      error("Duplicate choice titles")
  }
}