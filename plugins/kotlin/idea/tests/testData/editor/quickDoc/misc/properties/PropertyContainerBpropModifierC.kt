class UberPropertyContainer {
    open class PropertyContainerA {
        open var propModifierB = 0
        protected open var propModifierG = AuxClassA()
        final val propModifierQ = object : AuxFaceA {}
    }
    interface PropertyContainerB {
        val <caret>propModifierC: Int
    }
    class PropertyContainerC : PropertyContainerA(), PropertyContainerB {
        override var propModifierB = 0
        override val propModifierC = 0
        lateinit var propModifierE: String
        inline val propModifierK: Int
            get() = 0

        var AuxClassA.propReceiverA
            get() = 0
            set(value) {}
        val AuxClassA?.propReceiverB
            get() = 0

        var <@AuxAnnA X> AuxFaceD<X>.propTypeParamD: Int
            get() = 0
            set(value) {}
        @Suppress("MISPLACED_TYPE_PARAMETER_CONSTRAINTS") val <X: AuxFaceA> AuxFaceD<X>.propTypeParamI: Int where X: AuxFaceB
            get() = 0
        val <X> AuxFaceD<X>.propTypeParamJ: Int where X: AuxFaceA, X: AuxFaceB
            get() = 0
    }
}
//INFO: <div class='definition'><pre><span style="color:#000080;font-weight:bold;">public</span> <span style="color:#000080;font-weight:bold;">abstract</span> <span style="color:#000080;font-weight:bold;">val</span> <span style="color:#660e7a;font-weight:bold;">propModifierC</span><span style="">: </span><span style="color:#000000;"><a href="psi_element://kotlin.Int">Int</a></span></pre></div><div class='bottom'><icon src="KotlinBaseResourcesIcons.ClassKotlin"/>&nbsp;<a href="psi_element://UberPropertyContainer.PropertyContainerB"><code><span style="color:#000000;">UberPropertyContainer.PropertyContainerB</span></code></a><br/></div>
