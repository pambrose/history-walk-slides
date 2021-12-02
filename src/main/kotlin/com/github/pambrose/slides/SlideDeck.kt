package com.github.pambrose.slides

import mu.KLogging

class SlideDeck {
  private val slideList = mutableListOf<Slide>()
  private val slideMap = mutableMapOf<String, Slide>()
  lateinit var rootSlide: Slide

  fun slide(
    title: String,
    content: String = "Default Text",
    root: Boolean = false,
    success: Boolean = false,
    block: Slide.() -> Unit = { }
  ) =
    Slide(title, content, root, success).apply {
      block()
    }.also { slide ->
      addSlideToDeck(slide)
    }

  private fun validateSlideDeck() {
    slideList.forEach { slide ->
      if (slide.success && slide.hasChoices)
        error("""Slide "${slide.pathName}" cannot be a success slide and have choices""")
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
      slideList.filter { it.root }.let { rootSlides ->
        when {
          rootSlides.isEmpty() -> error("Missing a top-level slide")
          rootSlides.size > 1 -> error("Multiple top-level slides: ${rootSlides.map { it.title }}")
          else -> rootSlides.first()
        }
      }
  }

  fun findSlide(pathName: String) = slideMap[pathName]

  fun containsSlide(pathName: String) = slideMap.containsKey(pathName)

  private fun addSlideToDeck(slide: Slide) {
    slideList += slide
  }

  companion object : KLogging() {
    fun slideDeck(block: SlideDeck.() -> Unit) =
      SlideDeck()
        .apply(block)
        .apply {
          slideList
            .filterNot { it.isSubTree }
            .forEach { slide ->
              slideMap[slide.pathName] = slide   // Built after all slides are added to get pathName right
              slide.validateSlide()
            }
          validateSlideDeck()
        }
  }
}

