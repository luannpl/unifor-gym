package com.example.unifor_gym.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class ScreenTextExtractor {

    data class ExtractedText(
        val title: String = "",
        val content: List<String> = emptyList(),
        val actions: List<String> = emptyList()
    )

    companion object {

        /**
         * Extract all visible text from current activity
         */
        fun extractFromActivity(activity: Activity): ExtractedText {
            val rootView = activity.findViewById<View>(android.R.id.content)
            return extractFromView(rootView, activity)
        }

        /**
         * Extract all visible text from a specific view hierarchy
         */
        fun extractFromView(rootView: View, activity: Activity? = null): ExtractedText {
            val titles = mutableListOf<String>()
            val content = mutableListOf<String>()
            val actions = mutableListOf<String>()

            // First, try to get activity/fragment title
            activity?.let { act ->
                // Get activity title
                act.title?.toString()?.let { title ->
                    if (title.isNotBlank() && title != act.packageName) {
                        titles.add(title)
                    }
                }

                // Get current fragment title if it's a FragmentActivity
                if (act is FragmentActivity) {
                    getCurrentFragmentTitle(act)?.let { fragmentTitle ->
                        titles.add(fragmentTitle)
                    }
                }
            }

            // Extract text from view hierarchy
            extractTextRecursively(rootView, titles, content, actions)

            return ExtractedText(
                title = titles.joinToString(", "),
                content = content.distinct(),
                actions = actions.distinct()
            )
        }

        /**
         * Get current fragment title
         */
        private fun getCurrentFragmentTitle(activity: FragmentActivity): String? {
            return try {
                activity.supportFragmentManager.fragments
                    .firstOrNull { it.isVisible }
                    ?.javaClass?.simpleName
                    ?.replace("Fragment", "")
                    ?.let { convertCamelCaseToReadable(it) }
            } catch (e: Exception) {
                null
            }
        }

        /**
         * Recursively extract text from view hierarchy
         */
        private fun extractTextRecursively(
            view: View,
            titles: MutableList<String>,
            content: MutableList<String>,
            actions: MutableList<String>
        ) {
            // Skip if view is not visible
            if (view.visibility != View.VISIBLE) return

            when (view) {
                is TextView -> {
                    val text = view.text?.toString()?.trim()
                    if (!text.isNullOrBlank() && text.length > 1) {
                        when {
                            // Detect titles (larger text, bold, or specific IDs)
                            isTitle(view, text) -> titles.add(text)
                            // Detect action buttons
                            view is Button -> actions.add(text)
                            // Regular content
                            else -> content.add(text)
                        }
                    }
                }

                is EditText -> {
                    val hint = view.hint?.toString()?.trim()
                    if (!hint.isNullOrBlank()) {
                        content.add("Campo: $hint")
                    }
                }

                is RecyclerView -> {
                    // For RecyclerViews, get a sample of visible items
                    extractFromRecyclerView(view, content)
                }

                is ViewGroup -> {
                    // Recursively check child views
                    for (i in 0 until view.childCount) {
                        extractTextRecursively(view.getChildAt(i), titles, content, actions)
                    }
                }
            }
        }

        /**
         * Extract text from RecyclerView items
         */
        private fun extractFromRecyclerView(recyclerView: RecyclerView, content: MutableList<String>) {
            try {
                val adapter = recyclerView.adapter ?: return
                val itemCount = adapter.itemCount

                if (itemCount > 0) {
                    // Get text from visible ViewHolders (simpler approach)
                    var sampleCount = 0

                    // Iterate through all currently visible ViewHolders
                    for (i in 0 until recyclerView.childCount) {
                        if (sampleCount >= 3) break // Limit to 3 samples

                        val childView = recyclerView.getChildAt(i)
                        if (childView != null) {
                            val itemTexts = mutableListOf<String>()
                            extractTextRecursively(childView, mutableListOf(), itemTexts, mutableListOf())
                            if (itemTexts.isNotEmpty()) {
                                content.add("Item da lista: ${itemTexts.joinToString(", ")}")
                                sampleCount++
                            }
                        }
                    }

                    // Add info about total items if there are more
                    if (itemCount > sampleCount && sampleCount > 0) {
                        content.add("E mais ${itemCount - sampleCount} itens na lista")
                    } else if (sampleCount == 0 && itemCount > 0) {
                        // Fallback: if we couldn't extract from visible items, just mention the list
                        content.add("Lista com $itemCount itens")
                    }
                }
            } catch (e: Exception) {
                content.add("Lista com múltiplos itens")
            }
        }

        /**
         * Determine if a TextView is likely a title
         */
        private fun isTitle(textView: TextView, text: String): Boolean {
            return when {
                // Check text size (titles are usually larger)
                textView.textSize > 50f -> true // 18sp+ in pixels

                // Check if text is bold
                textView.typeface?.isBold == true -> true

                // Check ID names for title patterns
                textView.id != View.NO_ID -> {
                    val resourceName = try {
                        textView.context.resources.getResourceEntryName(textView.id)
                    } catch (e: Exception) {
                        ""
                    }
                    resourceName.contains("title", ignoreCase = true) ||
                            resourceName.contains("header", ignoreCase = true) ||
                            resourceName.contains("name", ignoreCase = true)
                }

                // Check text content patterns
                text.length < 50 && text.contains(Regex("[A-Z]")) -> true

                else -> false
            }
        }

        /**
         * Convert CamelCase to readable text
         */
        private fun convertCamelCaseToReadable(camelCase: String): String {
            return camelCase
                .replace(Regex("([a-z])([A-Z])"), "$1 $2")
                .lowercase()
                .replaceFirstChar { it.uppercase() }
        }

        /**
         * Format extracted text for VLibras
         */
        fun formatForVLibras(extractedText: ExtractedText): String {
            val parts = mutableListOf<String>()

            // Add title
            if (extractedText.title.isNotBlank()) {
                parts.add("Tela: ${extractedText.title}")
            }

            // Add main content
            if (extractedText.content.isNotEmpty()) {
                // Limit content to prevent overwhelming translation
                val limitedContent = extractedText.content.take(10) // Max 10 items
                parts.add("Conteúdo: ${limitedContent.joinToString(". ")}")
            }

            // Add available actions
            if (extractedText.actions.isNotEmpty()) {
                val limitedActions = extractedText.actions.take(5) // Max 5 actions
                parts.add("Ações disponíveis: ${limitedActions.joinToString(", ")}")
            }

            return parts.joinToString(". ")
        }

        /**
         * Get context-specific introduction text
         */
        fun getContextualIntro(activity: Activity): String {
            val activityName = activity.javaClass.simpleName

            return when {
                activityName.contains("User", ignoreCase = true) ->
                    "Você está na área do usuário do Unifor Gym. "

                activityName.contains("Admin", ignoreCase = true) ->
                    "Você está na área administrativa do Unifor Gym. "

                activityName.contains("Exercise", ignoreCase = true) ||
                        activityName.contains("Exercicio", ignoreCase = true) ->
                    "Você está visualizando detalhes de um exercício. "

                activityName.contains("Chat", ignoreCase = true) ->
                    "Você está no chat com o assistente virtual. "

                activityName.contains("Aula", ignoreCase = true) ->
                    "Você está visualizando informações sobre uma aula. "

                else -> "Você está no aplicativo Unifor Gym. "
            }
        }
    }
}