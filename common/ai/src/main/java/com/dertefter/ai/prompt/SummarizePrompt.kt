package com.dertefter.ai.prompt

import com.dertefter.ai.model.SummarizeOptions

/**
 * Промпты для сокращения текста.
 *
 * Системная инструкция написана на английском: инструкции-обёртки моделей
 * обучены в первую очередь на английском, а язык результата задаётся отдельным правилом.
 */
internal object SummarizePrompt {

    private const val SYSTEM_INSTRUCTION = """You are a precise text editor that shortens texts.
Rules:
1. Return only the shortened text: no greetings, no explanations, no comments, no markdown, no quotes.
2. Keep the key meaning and all important facts, names, dates and numbers.
3. Do not invent anything and do not distort the meaning of the original.
4. Keep hashtags, @mentions, emoji and links unchanged.
5. Write in the same language as the original text.
6. Do not show your reasoning or thinking steps.
7. If the text is already short, return it with minimal edits.
"""

    /** Формирует системную инструкцию с учётом [options]. */
    fun system(options: SummarizeOptions): String = buildString {
        append(SYSTEM_INSTRUCTION)
        options.maxWords?.let { append("Limit the result to at most ").append(it).append(" words.\n") }
        options.language?.let { append("Write the result in ").append(it).append(".\n") }
    }

    /** Формирует пользовательское сообщение с исходным текстом. */
    fun user(text: String): String = "Shorten the following text:\n\n$text"
}
