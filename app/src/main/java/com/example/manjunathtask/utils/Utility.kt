package com.example.manjunathtask.utils

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import java.text.NumberFormat
import java.util.Locale

object Utility {
    fun formatNumber(number: Double?): String {
        val safeNum = number ?: 0.0
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        formatter.maximumFractionDigits = 2
        formatter.minimumFractionDigits = 2
        return formatter.format(safeNum)
    }

    fun formatWithPercentage(value: String, percentage: Double): SpannableString {
        val safePercentage = if (percentage.isFinite()) percentage else 0.0
        val formattedPercentage = String.format("(%.2f%%)", safePercentage)

        val fullText = "$value $formattedPercentage"
        val spannable = SpannableString(fullText)

        // reduce font size of percentage part by 2dp
        val start = fullText.indexOf(formattedPercentage)
        val end = start + formattedPercentage.length
        spannable.setSpan(
            AbsoluteSizeSpan(14, true), // adjust dp (if text is 16dp, make this 14dp)
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    fun buildStyledText(
        str1: String,
        str2: String,
        str2Color: Int,
        isBoldRequired: Boolean = false
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        // str1 (label before colon)
        builder.append(str1)
        builder.append(": ")

        // starting index of str2
        val start = builder.length
        builder.append(str2)
        val end = builder.length

        // apply color to str2
        builder.setSpan(
            ForegroundColorSpan(str2Color),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // apply bold if needed
        if (isBoldRequired) {
            builder.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // apply size increase (+2sp)
        builder.setSpan(
            RelativeSizeSpan(1f + (4f / 14f)), // base ~14sp text
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return builder
    }
}