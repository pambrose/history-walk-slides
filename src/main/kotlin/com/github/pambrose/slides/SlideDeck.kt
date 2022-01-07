package com.github.pambrose.slides

import mu.KLogging

class SlideDeck {
  private val slideList = mutableListOf<Slide>()
  private val slideMap = mutableMapOf<String, Slide>()
  val slideIdMap = mutableMapOf<Int, MutableList<Slide>>()
  lateinit var rootSlide: Slide

  fun slide(
    id: Int,
    title: String,
    content: String,
    root: Boolean = false,
    success: Boolean = false,
    block: Slide.() -> Unit = { }
  ) =
    Slide(id, title, content, root, success, this).apply {
      block()
    }.also { slide ->
      addSlideToDeck(slide)
    }

  fun goBack(offset: Int): Slide {
    require(offset != 0) { "Offset cannot be 0" }
    return Slide(-1, "", "", false, false, this, offset)
  }

  private fun validateSlideDeck() {
    slideList.forEach { slide ->
      if (slide.success && slide.hasChoices)
        error("""Slide "${slide.pathName}" cannot be a success slide and have choices""")
    }

    slideList.count { it.success }
      .also { successCount ->
        when (successCount) {
          0 -> error("No success slide found")
          1 -> logger.debug("Success slide found")
          else -> logger.warn { "$successCount success slides found" }
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

  fun findSlideByPathName(pathName: String): Slide {
    val slide = if (pathName == ROOT)
      rootSlide
    else
      slideMap[pathName]

    return if (slide == null) {
      logger.error("Invalid slide name: $pathName")
      rootSlide
    } else {
      slide
    }
  }

  fun findSlideById(id: Int, version: Int = 0) =
    slideIdMap[id]?.let {
      if (version < it.size) it[version] else null
    }

  fun containsSlideByPathName(pathName: String) = slideMap.containsKey(pathName)

  fun addSlideToDeck(slide: Slide) {
    slideList += slide
  }

  // Each instance of the slide is kept in a list
  fun addSlideToIdMap(slide: Slide) {
    slideIdMap.computeIfAbsent(slide.id) { mutableListOf() } += slide
  }

  companion object : KLogging() {
    const val ROOT = "/"

    fun slideDeck(block: SlideDeck.() -> Unit) =
      SlideDeck()
        .apply(block)
        .apply {
          slideList
            .filterNot { it.isSubTree }
            .forEach { slide ->
              logger.debug { "Added to map Title: ${slide.title} Path: '${slide.pathName}'" }
              slideMap[slide.pathName] = slide   // Built after all slides are added to get pathName right
              slide.validateSlide()
            }

          // Remove slides that are no in main tree
          slideIdMap.values.forEach { slideList -> slideList.removeIf { it.isSubTree } }

          // Validate it
          validateSlideDeck()

          // Empty slideList
          slideList.clear()
        }
  }
}