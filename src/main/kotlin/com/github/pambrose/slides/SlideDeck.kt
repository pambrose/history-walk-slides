package com.github.pambrose.slides

import mu.KLogging

class SlideDeck {
  val allSlides = mutableMapOf<String, Slide>()
  lateinit var rootSlide: Slide

  fun slide(title: String, success: Boolean = false, block: Slide.() -> Unit = { }) {
    Slide(title, success, this).block()
  }

  fun validate() {
    allSlides.forEach { (title, slide) ->
      slide.choices.forEach { (_, dest) ->
        val destSlide = allSlides[dest] ?: error("Missing slide with title: $dest")
        if (destSlide.parentSlide != null)
          error("""Parent slide already assigned to: "$dest"""")
        destSlide.parentSlide = slide
      }

      if (slide.success && slide.hasChoices)
        error("""Slide "$title" has both success and choices""")
    }

    allSlides.filter { it.value.success }.count()
      .also { successCount ->
        if (successCount == 0)
          error("No success slide found")

        if (successCount > 1)
          error("Multiple success slides found")
      }

    rootSlide =
      allSlides.values.filter { it.parentSlide == null }.let { nullParents ->
        when {
          nullParents.size > 1 -> error("Multiple top-level slides: ${nullParents.map { it.title }}")
          nullParents.isEmpty() -> error("Missing a top-level slide")
          else -> nullParents.first()
        }
      }
  }

  companion object : KLogging() {
    fun slideDeck(block: SlideDeck.() -> Unit) =
      SlideDeck().apply(block).apply { }
  }
}

