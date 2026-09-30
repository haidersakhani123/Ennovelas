package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.regex.Pattern

class EnpantallaParsingUnitTest {

    @Test
    fun testEpisodeBadgeExtraction() {
        val pattern = Pattern.compile("(?i)(?:cap[ií]tulo|cap\\.?|episodio|ep\\.?)\\s*(\\d+)")

        val title1 = "Pa’ Seguirte Queriendo Capítulo 47 Completa"
        val m1 = pattern.matcher(title1)
        assertTrue(m1.find())
        assertEquals("47", m1.group(1))

        val title2 = "Baskalarinin Hayati – Capitulo 1"
        val m2 = pattern.matcher(title2)
        assertTrue(m2.find())
        assertEquals("1", m2.group(1))

        val title3 = "Al Fondo Hay Sitio Temporada 13 Episodio 05"
        val m3 = pattern.matcher(title3)
        assertTrue(m3.find())
        assertEquals("05", m3.group(1))

        val title4 = "Pelicula Estreno Sin Capitulo"
        val m4 = pattern.matcher(title4)
        assertTrue(!m4.find())
    }

    @Test
    fun testVideoEmbedExtraction() {
        val pattern = Pattern.compile("<(?:iframe|video)[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
        val html = "<p><iframe loading=\"lazy\" width=\"560\" height=\"315\" src=\"//ok.ru/videoembed/16117001685652?nochat=1\" frameborder=\"0\" allow=\"autoplay\" allowfullscreen></iframe></p>"

        val m = pattern.matcher(html)
        assertTrue(m.find())
        var url = m.group(1)
        assertNotNull(url)
        if (url!!.startsWith("//")) {
            url = "https:$url"
        }
        assertEquals("https://ok.ru/videoembed/16117001685652?nochat=1", url)
    }

    @Test
    fun testYearExtraction() {
        val dateStr = "2026-09-27T06:48:36"
        val year = if (dateStr.length >= 4) dateStr.substring(0, 4) else "2026"
        assertEquals("2026", year)
    }
}
