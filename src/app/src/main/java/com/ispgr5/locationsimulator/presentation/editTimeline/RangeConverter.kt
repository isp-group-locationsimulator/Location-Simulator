package com.ispgr5.locationsimulator.presentation.editTimeline

/**
 * Class to help converting user friendly ranges into technical values for configuration and vise versa.
 */
class RangeConverter {
    companion object {
        /**
         * converts UI value in 0f..100f to 0f..1f
         */
        fun transformPercentageToFactor(input: Float) : Float{
            return input/100f
        }

        /**
         * converts technical value in 0f..1f to 0f..100f
         */
        fun transformFactorToPercentage(input : Float) : Float{
            return input * 100f
        }

        /**
         * converts UI value in 0f..100f to 0..255
         */
        fun floatTo8BitInt( input: Float) :Int{
            return (input/100f * 255).toInt()
        }

        /**
         * converts technical value in 0..255 to 0f..100f
         */
        fun eightBitIntToPercentageFloat(input : Int) : Float{
            return ((input/255f) * 100)
        }

        /**
         * converts UI value in s to technical value in ms
         */
        fun sToMs(sliderValue : Float) : Int{
            return (sliderValue * 1000).toInt()
        }
        /**
         * converts technical value in ms to UI value in s
         */
        fun msToS(valueInMs : Int) : Float{
            return valueInMs / 1000f
        }
    }
}