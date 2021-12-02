package com.github.pambrose.slides

import mu.KLogging

class SlideDeck {
  private val allSlides = mutableMapOf<String, Slide>()
  lateinit var rootSlide: Slide

  fun slide(
    title: String,
    content: String = "Default Text",
    success: Boolean = false,
    block: Slide.() -> Unit = { }
  ) =
    Slide(title, content, success, this).apply {
      block()
    }.apply {
      validateSlide()
      slideDeck.assignSlide(this)
    }

  fun validateSlideDeck() {
    allSlides.forEach { (_, slide) ->
      if (slide.success && slide.hasChoices)
        error("""Slide "${slide.fqName}" cannot be a success slide and have choices""")
    }

    allSlides.filter { it.value.success }.count()
      .also { successCount ->
        when (successCount) {
          0 -> error("No success slide found")
          1 -> logger.debug("Success slide found")
          else -> error("Multiple success slides found")
        }
      }

    rootSlide =
      allSlides.values.filter { it.parentSlide == null }.let { nullParents ->
        when {
          nullParents.size > 1 -> error("Multiple top-level slides: ${nullParents.map { it.fqName }}")
          nullParents.isEmpty() -> error("Missing a top-level slide")
          else -> nullParents.first()
        }
      }
  }

  fun findSlide(fqName: String) = allSlides[fqName]

  fun containsSlide(fqName: String) = allSlides.containsKey(fqName)

  fun assignSlide(slide: Slide) {
    allSlides[slide.fqName] = slide
  }

  companion object : KLogging() {
    fun slideDeck(block: SlideDeck.() -> Unit) =
      SlideDeck().apply(block).apply { }
  }
}

