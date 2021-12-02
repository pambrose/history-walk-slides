package com.github.pambrose.slides

import mu.KLogging

class Slide(val title: String, val content: String, val root: Boolean, val success: Boolean, val slideDeck: SlideDeck) {
  var verticalChoices = true
  val choices = mutableMapOf<String, Slide>()

  // Assigned when the slideMap is built from the slideList
  var parentSlide: Slide? = null

  val hasChoices get() = choices.isNotEmpty()

  val isSubTree get() = !root && parentSlide == null

  val pathName: String get() = "${parentSlide?.pathName ?: ""}/$title"

  init {
    require(title.isNotEmpty()) { "Slide title cannot be empty" }
  }

  fun copyOf(): Slide {
    val origChoices = choices
    val origVertical = verticalChoices
    return Slide(title, content, root, success, slideDeck)
      .also { copy ->
        copy.verticalChoices = origVertical
        origChoices.forEach {
          val newSlide = it.value.copyOf().also { it.parentSlide = copy }
          this@Slide.slideDeck.addSlideToDeck(newSlide)
          copy.choices[it.key] = newSlide
          logger.info { "Added choice ${it.key}} = $newSlide" }
        }
      }
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

  fun validateSlide() {
    if (choices.map { it.key }.toSet().size != choices.size)
      error("""Slide "$title" has duplicate choice titles""")
  }

  override fun toString() = "Slide(title='$title', choices=${choices.size}, parentSlide=$parentSlide)"

  companion object : KLogging()
}