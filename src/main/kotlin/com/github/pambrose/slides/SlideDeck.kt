package com.github.pambrose.slides

import mu.KLogging

class SlideDeck {
  private val slideList = mutableListOf<Slide>()
  private val slideMap = mutableMapOf<String, Slide>()
  lateinit var rootSlide: Slide

  fun slide(
    title: String,
    content: String = "Default Text",
    success: Boolean = false,
    block: Slide.() -> Unit = { }
  ) =
    Slide(title, content, success, this).apply {
      block()
    }.also { slide ->
      addSlideToDeck(slide)
    }

  private fun validateSlideDeck() {
    slideList.forEach { slide ->
      if (slide.success && slide.hasChoices)
        error("""Slide "${slide.fqName}" cannot be a success slide and have choices""")
    }

    slideList.filter { it.success }.count()
      .also { successCount ->
        when (successCount) {
          0 -> error("No success slide found")
          1 -> logger.debug("Success slide found")
          else -> error("Multiple success slides found")
        }
      }

    rootSlide =
      slideList.filter { it.parentSlide == null }.let { nullParents ->
        when {
          nullParents.size > 1 -> error("Multiple top-level slides: ${nullParents.map { it.fqName }}")
          nullParents.isEmpty() -> error("Missing a top-level slide")
          else -> nullParents.first()
        }
      }
  }

  fun findSlide(fqName: String) = slideMap[fqName]

  fun containsSlide(fqName: String) = slideMap.containsKey(fqName)

  private fun addSlideToDeck(slide: Slide) {
    slideList += slide
  }

  companion object : KLogging() {
    fun slideDeck(block: SlideDeck.() -> Unit) =
      SlideDeck()
        .apply(block)
        .apply {
          slideList.forEach { slide ->
            slideMap[slide.fqName] = slide   // Built after all slides are added to get fqname right
            slide.validateSlide()
          }
          validateSlideDeck()
        }
  }
}

