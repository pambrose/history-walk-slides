package com.github.pambrose.slides

import mu.KLogging

class Slide(
  val id: String,
  val title: String,
  val content: String,
  val root: Boolean = false,
  val success: Boolean = false,
  val slideDeck: SlideDeck,
  val offset: Int = 0
) {
  var verticalChoices = true
  val choices = mutableMapOf<String, Slide>()

  // Assigned when the slideMap is built from the slideList
  var parentSlide: Slide? = null

  val hasChoices get() = choices.isNotEmpty()

  val isSubTree: Boolean get() = parentSlide?.isSubTree ?: !root

  val pathName: String get() = "${parentSlide?.pathName ?: ""}/$title"

  init {
    if (offset == 0)
      require(title.isNotEmpty()) { "Slide title cannot be empty" }
  }

  fun copyOf(): Slide {
    // Each version of the slide is kept in a list
    (slideDeck.slideIdMap.computeIfAbsent(id) { mutableListOf() }) += this
    return Slide(id, title, content, root, success, slideDeck, offset)
      .also { copy ->
        copy.verticalChoices = verticalChoices
        choices.forEach { text, slide -> copy.choices[text] = slide.copyOf().also { it.parentSlide = copy } }
        slideDeck.addSlideToDeck(copy)
      }
  }

  fun verticalChoices() {
    verticalChoices = true
  }

  fun horizontalChoices() {
    verticalChoices = false
  }

  fun choice(text: String, slide: Slide) {
    require(text.isNotEmpty()) { "Choice text cannot be empty for ${slide.title}" }
    choices[text] = slide
    slide.parentSlide = this
  }

  fun validateSlide() {
    if (choices.map { it.key }.toSet().size != choices.size)
      error("""Slide "$title" has duplicate choice titles""")

    if (choices.map { it.value.title }.toSet().size != choices.size)
      error("""Slide "$title" has duplicate choice slide titles""")
  }

  override fun toString() = "Slide(title='$title', choices=${choices.size}, parentSlide=$parentSlide)"

  companion object : KLogging()
}