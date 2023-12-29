package com.ispgr5.locationsimulator.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testing the Range Converter Class
 */
internal class RangeConverterTest {

    @Test
    fun `test msToS is correct`() {
        assertEquals(RangeConverter.msToS(1000), 1.0f)
        assertEquals(RangeConverter.msToS(2356), 2.356f)
        assertEquals(RangeConverter.msToS(123356), 123.356f)
    }

    @Test
    fun `test sToMs is correct`() {
        assertEquals(RangeConverter.sToMs(5f), 5000)
        assertEquals(RangeConverter.sToMs(5.56f), 5560)
        assertEquals(RangeConverter.sToMs(10.123f), 10123)
    }

    @Test
    fun `test transformPercentageToFactor and transformFactorToPercentage is correct`() {
        assertEquals(RangeConverter.transformPercentageToFactor(56f), 0.56f)

        assertEquals(RangeConverter.transformFactorToPercentage(0.87f), 87.0f)

        //test if they are inverse functions
        assertEquals(
            RangeConverter.transformFactorToPercentage(
                RangeConverter.transformPercentageToFactor(
                    78.9f
                )
            ), 78.9f
        )
    }

    @Test
    fun `test floatToEightBitInt and eightBitIntToPercentageFloat`() {
        assertEquals(RangeConverter.eightBitIntToPercentageFloat(255), 100f)
        assertEquals(RangeConverter.eightBitIntToPercentageFloat(0), 0f)

        assertEquals(RangeConverter.floatToEightBitInt(100f), 255)
        assertEquals(RangeConverter.floatToEightBitInt(0f), 0)
    }
}