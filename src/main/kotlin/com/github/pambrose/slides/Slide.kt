package com.github.pambrose.slides

class Slide(val title: String, val content: String, val root: Boolean, val success: Boolean) {
  var verticalChoices = true
  val choices = mutableMapOf<String, Slide>()

  // These are assigned when the slideMap is built from the slideList
  lateinit var pathName: String
  var parentSlide: Slide? = null

  val hasChoices get() = choices.isNotEmpty()

  val isSubTree get() = !root && parentSlide == null

  init {
    require(title.isNotEmpty()) { "Slide title cannot be empty" }

    if (root)
      pathName = "/$title"
  }

  fun copyOf(): Slide {
    return Slide(title, content, root, success)
      .also { copy ->
        copy.verticalChoices = verticalChoices
        copy.choices.forEach {
          copy.choices[it.key] = it.value.copyOf().also { it.parentSlide = copy }
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
    slide.assignPathName()
  }

  fun assignPathName() {
    pathName = "${parentSlide?.pathName ?: ""}/$title"
  }

  fun validateSlide() {
    if (choices.map { it.value.title }.toSet().size != choices.size)
      error("Duplicate choice titles")
  }
}