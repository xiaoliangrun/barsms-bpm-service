package com.cpic.barsms.bpm.common.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringSplitterTest {

    @Test
    void splitByPipe_returnsIndexedSegment() {
        assertEquals("总办", StringSplitter.splitByPipe("总办|分办|中办|支办", 0));
        assertEquals("分办", StringSplitter.splitByPipe("总办|分办|中办|支办", 1));
        assertEquals("支办", StringSplitter.splitByPipe("总办|分办|中办|支办", 3));
    }

    @Test
    void splitByPipe_trimsWhitespace() {
        assertEquals("分办", StringSplitter.splitByPipe(" 总办 |  分办  | 中办", 1));
    }

    @Test
    void splitByPipe_indexOutOfBoundsReturnsNull() {
        assertNull(StringSplitter.splitByPipe("总办|分办", 5));
    }

    @Test
    void splitByPipe_blankSourceReturnsNull() {
        assertNull(StringSplitter.splitByPipe(null, 0));
        assertNull(StringSplitter.splitByPipe("", 0));
        assertNull(StringSplitter.splitByPipe("   ", 0));
    }

    @Test
    void splitByComma_splitsAndTrimsAndFiltersEmpty() {
        List<String> result = StringSplitter.splitByComma(" 综合岗 , , 财务岗 ,");
        assertEquals(2, result.size());
        assertEquals("综合岗", result.get(0));
        assertEquals("财务岗", result.get(1));
    }

    @Test
    void splitByComma_blankSourceReturnsEmptyList() {
        assertTrue(StringSplitter.splitByComma(null).isEmpty());
        assertTrue(StringSplitter.splitByComma("").isEmpty());
    }
}
