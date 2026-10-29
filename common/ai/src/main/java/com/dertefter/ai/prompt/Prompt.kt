
package com.dertefter.ai.prompt

import com.dertefter.ai.model.GenerateOptions

sealed class Prompt {

    abstract val id: String

    protected abstract val systemInstruction: String

    protected abstract val userInstruction: String

    fun system(options: GenerateOptions = GenerateOptions()): String = buildString {
        append(systemInstruction).append('\n')
        append(RULES)
        append(languageRule(options.language))
        options.maxWords?.let {
            append("Ограничь результат максимум ").append(it).append(" словами.\n")
        }
    }

    fun user(
        text: String,
        options: GenerateOptions = GenerateOptions(),
    ): String = buildString {
        append(userInstruction).append("\n\n")
        append(text).append("\n\n")
        append(languageRule(options.language))
    }

    private fun languageRule(language: String?): String =
        if (language == null) {
            "Отвечай на языке исходного текста: никогда не переводи его и не переходи на другой язык.\n"
        } else {
            "Напиши результат на языке $language, как указано в настройках.\n"
        }

    data object Summarize : Prompt() {
        override val id = "summarize"
        override val systemInstruction =
            "Ты — точный текстовый редактор, который сокращает тексты. " +
                    "Если текст уже короткий, верни его с минимальными изменениями."
        override val userInstruction = "Сократи следующий текст:"
    }

    data object Zoomer : Prompt() {
        override val id = "zoomer"
        override val systemInstruction =
            "Ты — редактор социальных сетей, который переписывает тексты на современном интернет-сленге поколения Z, " +
                    "используя строчные буквы и короткие, ёмкие формулировки."
        override val userInstruction = "Перепиши следующий текст на сленге поколения Z:"
    }

    data object Boomer : Prompt() {
        override val id = "boomer"
        override val systemInstruction =
            "Ты — редактор социальных сетей, который переписывает тексты в стиле старшего поколения интернет-пользователей: " +
                    "длинные предложения, многоточия, восклицательные знаки и старомодные выражения."
        override val userInstruction = "Перепиши следующий текст в стиле старшего интернет-пользователя:"
    }

    data object Millennial : Prompt() {
        override val id = "millennial"
        override val systemInstruction =
            "Ты — редактор социальных сетей, который переписывает тексты в ироничном, " +
                    "самоироничном стиле интернет-общения миллениалов."
        override val userInstruction = "Перепиши следующий текст в стиле публикации миллениала в социальных сетях:"
    }

    data object Scientific : Prompt() {
        override val id = "scientific"
        override val systemInstruction =
            "Ты — научный редактор, который переписывает тексты в формальном научном стиле, " +
                    "используя точные формулировки и нейтральный тон."
        override val userInstruction = "Перепиши следующий текст в формальном научном стиле:"
    }

    companion object {

        val available: List<Prompt> =
            listOf(Summarize, Zoomer, Boomer, Millennial, Scientific)

        private const val RULES = """Правила:
1. Всегда отвечай на языке исходного текста: никогда не переводи его и не переходи на другой язык, если явно не запрошен другой язык.
2. Возвращай только результат: никаких приветствий, объяснений, комментариев, Markdown-разметки или кавычек.
3. Сохраняй основной смысл и все важные факты, имена, даты и числа.
4. Ничего не выдумывай и не искажай смысл оригинала.
5. Оставляй хештеги, упоминания через @, эмодзи и ссылки без изменений.
6. Не показывай ход рассуждений или этапы обдумывания.
"""
    }
}
