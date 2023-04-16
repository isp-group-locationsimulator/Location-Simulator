package com.ispgr5.locationsimulator.presentation.delay

import calculateTimerValue
import org.junit.Assert.*
import org.junit.Test

/**Testing the Helper function of the Delay Timer*/
class TimerTest{

    /**
     * testing that the calcTimer Value correctly converts the string of Integers to Integers between 0 and 59
     */
    @Test
    fun `test calcTimerValue`(){
        assertEquals( calculateTimerValue("Dds"),0);
        assertEquals( calculateTimerValue("43"),43)
        assertEquals( calculateTimerValue("0"),0)
        assertEquals( calculateTimerValue("-7"),0)
        assertEquals( calculateTimerValue("60"),59)
        assertEquals( calculateTimerValue("6999"),59)
        assertEquals( calculateTimerValue("20"),20)
        assertEquals( calculateTimerValue("5r"),0)
    }

}