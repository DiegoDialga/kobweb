package com.varabyte.kobweb.silk.components.forms

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.BoxScope
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.ComponentVariant
import com.varabyte.kobweb.silk.components.style.addVariant
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.common.PlaceholderColor
import com.varabyte.kobweb.silk.components.style.common.ariaInvalid
import com.varabyte.kobweb.silk.components.style.disabled
import com.varabyte.kobweb.silk.components.style.focusVisible
import com.varabyte.kobweb.silk.components.style.hover
import com.varabyte.kobweb.silk.components.style.not
import com.varabyte.kobweb.silk.components.style.placeholder
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorVar
import com.varabyte.kobweb.silk.theme.colors.PlaceholderOpacityVar
import org.jetbrains.compose.web.attributes.AutoComplete
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.autoComplete
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.w3c.dom.HTMLInputElement

object InputDefaults {
    const val Valid = true
    const val Enabled = true
    const val ReadOnly = false
    const val Required = false
    const val SpellCheck = false
    val Size = InputSize.MD
    val Variant = OutlinedInputVariant
}

val InputBorderColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val InputBorderRadiusVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val InputBorderFocusColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val InputBorderHoverColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val InputBorderInvalidColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val InputFilledColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val InputFilledHoverColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val InputFilledFocusColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val InputFontSizeVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val InputHeightVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val InputPaddingVar by StyleVariable<CSSLengthValue>(prefix = "silk")
val InputPlaceholderOpacityVar by StyleVariable(prefix = "silk", defaultFallback = PlaceholderOpacityVar.value())
val InputPlaceholderColorVar by StyleVariable<CSSColorValue>(prefix = "silk")
val InputInsetLeftWidthVar by StyleVariable<CSSLengthValue>(prefix = "silk", defaultFallback = 2.25.cssRem)
val InputInsetRightWidthVar by StyleVariable<CSSLengthValue>(prefix = "silk", defaultFallback = 2.25.cssRem)

val InputGroupStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .outline(0.px, LineStyle.Solid, Colors.Transparent) // Disable, we'll use box shadow instead
        .border(0.px, LineStyle.Solid, Colors.Transparent) // Overridden by variants
        // Although the group container itself has no border, we still want to round the corners in case the user sets
        // this element's background color, so it will match the shape of the elements on top of it.
        .borderRadius(InputBorderRadiusVar.value())
        .fontSize(InputFontSizeVar.value())
}

val InputStyle by ComponentStyle(prefix = "silk") {
    base {
        Modifier
            .styleModifier { property("appearance", "none") } // Disable browser styles
            .color(ColorVar.value())
            .height(InputHeightVar.value())
            .fontSize(InputFontSizeVar.value())
            .backgroundColor(Colors.Transparent)
            .outline(0.px, LineStyle.Solid, Colors.Transparent) // Disable, we'll use box shadow instead
            .border(0.px, LineStyle.Solid, Colors.Transparent) // Overridden by variants
            .transition(CSSTransition.group(listOf("border-color", "box-shadow", "background-color"), 200.ms))
    }

    placeholder {
        Modifier
            .opacity(InputPlaceholderOpacityVar.value())
            .color(InputPlaceholderColorVar.value())
    }
}

private fun Modifier.inputPadding(): Modifier {
    val padding = InputPaddingVar.value()
    return this.paddingInline(start = padding, end = padding)
}

val OutlinedInputVariant by InputStyle.addVariant {
    fun Modifier.bordered(color: CSSColorValue): Modifier {
        return this.border(1.px, LineStyle.Solid, color).boxShadow(spreadRadius = 1.px, color = color)
    }

    base {
        Modifier
            .inputPadding()
            .borderRadius(InputBorderRadiusVar.value())
            .border(1.px, LineStyle.Solid, InputBorderColorVar.value())
    }

    ariaInvalid { Modifier.bordered(InputBorderInvalidColorVar.value()) }
    (hover + not(disabled)) { Modifier.borderColor(InputBorderHoverColorVar.value()) }
    (focusVisible + not(disabled)) { Modifier.bordered(InputBorderFocusColorVar.value()) }
}

val FilledInputVariant by InputStyle.addVariant {
    fun Modifier.bordered(color: CSSColorValue): Modifier {
        return this.borderColor(color).boxShadow(spreadRadius = 1.px, color = color)
    }

    base {
        Modifier
            .inputPadding()
            .backgroundColor(InputFilledColorVar.value())
            .borderRadius(InputBorderRadiusVar.value())
            .border(1.px, LineStyle.Solid, Colors.Transparent)
    }
    (hover + not(disabled)) { Modifier.backgroundColor(InputFilledHoverColorVar.value()) }
    ariaInvalid { Modifier.bordered(InputBorderInvalidColorVar.value()) }
    (focusVisible + not(disabled)) {
        Modifier
            .backgroundColor(InputFilledFocusColorVar.value())
            .bordered(InputBorderFocusColorVar.value())
    }
}

val FlushedInputVariant by InputStyle.addVariant {
    fun Modifier.bordered(color: CSSColorValue): Modifier {
        return this.borderColor(color).boxShadow(offsetY = 1.px, color = color)
    }

    base { Modifier.borderBottom(1.px, LineStyle.Solid, InputBorderColorVar.value()) }
    ariaInvalid { Modifier.bordered(InputBorderInvalidColorVar.value()) }
    (hover + not(disabled)) { Modifier.borderColor(InputBorderHoverColorVar.value()) }
    (focusVisible + not(disabled)) { Modifier.bordered(InputBorderFocusColorVar.value()) }
}

val UnstyledInputVariant by InputStyle.addVariant {}

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class InputGroupScopeMarker

internal class InputParams<T : Any>(
    private val type: InputType<T>,
    private val value: T,
    private val onValueChanged: (T) -> Unit,
    private val modifier: Modifier = Modifier,
    private val variant: ComponentVariant? = InputDefaults.Variant,
    private val placeholder: String? = null,
    private val placeholderColor: PlaceholderColor? = null,
    private val focusBorderColor: CSSColorValue? = null,
    private val invalidBorderColor: CSSColorValue? = null,
    private val enabled: Boolean = InputDefaults.Enabled,
    private val valid: Boolean = InputDefaults.Valid,
    private val required: Boolean = InputDefaults.Required,
    private val readOnly: Boolean = InputDefaults.ReadOnly,
    private val spellCheck: Boolean = InputDefaults.SpellCheck,
    private val autoComplete: AutoComplete? = null,
    private val onCommit: () -> Unit = {},
    private val ref: ElementRefScope<HTMLInputElement>? = null,
) {
    @Composable
    fun renderInput(modifier: Modifier = Modifier) {
        _Input(
            type,
            value,
            onValueChanged,
            this.modifier.then(modifier),
            variant,
            placeholder,
            placeholderColor,
            focusBorderColor,
            invalidBorderColor,
            enabled,
            valid,
            required,
            readOnly,
            spellCheck,
            autoComplete,
            onCommit,
            ref,
        )
    }
}

@InputGroupScopeMarker
class InputGroupScope {
    internal var inputParams: InputParams<out Any>? = null

    internal var leftModifier: Modifier = Modifier
    internal var rightModifier: Modifier = Modifier
    internal var leftAddon: (@Composable BoxScope.() -> Unit)? = null
    internal var rightAddon: (@Composable BoxScope.() -> Unit)? = null
    internal var leftInset: (@Composable BoxScope.() -> Unit)? = null
    internal var leftInsetWidth: CSSLengthOrPercentageValue? = null
    internal var rightInset: (@Composable BoxScope.() -> Unit)? = null
    internal var rightInsetWidth: CSSLengthOrPercentageValue? = null

    fun <T : Any> Input(
        type: InputType<T>,
        value: T,
        onValueChanged: (T) -> Unit,
        modifier: Modifier = Modifier,
        variant: ComponentVariant? = InputDefaults.Variant,
        placeholder: String? = null,
        placeholderColor: PlaceholderColor? = null,
        focusBorderColor: CSSColorValue? = null,
        invalidBorderColor: CSSColorValue? = null,
        enabled: Boolean = InputDefaults.Enabled,
        valid: Boolean = InputDefaults.Valid,
        required: Boolean = InputDefaults.Required,
        readOnly: Boolean = InputDefaults.ReadOnly,
        spellCheck: Boolean = InputDefaults.SpellCheck,
        autoComplete: AutoComplete? = null,
        onCommit: () -> Unit = {},
        ref: ElementRefScope<HTMLInputElement>? = null,
    ) {
        require(inputParams == null) { "Can only call `Input` once" }

        inputParams = InputParams(
            type,
            value,
            onValueChanged,
            modifier,
            variant,
            placeholder,
            placeholderColor,
            focusBorderColor,
            invalidBorderColor,
            enabled,
            valid,
            required,
            readOnly,
            spellCheck,
            autoComplete,
            onCommit,
            ref
        )
    }

    /**
     * Declare an addon element which will be placed on the left side of the input.
     *
     * This is usually a bit of decorative text.
     *
     * NOTE: You can only declare a left addon OR inset element, not both.
     *
     * @see RightAddon
     * @see LeftInset
     */
    fun LeftAddon(modifier: Modifier = Modifier, block: @InputGroupScopeMarker @Composable BoxScope.() -> Unit) {
        require(leftAddon == null && leftInset == null) { "Can only set one left addon or inset element" }
        leftModifier = modifier
        leftAddon = block
    }

    /**
     * Declare an addon element which will be placed on the right side of the input.
     *
     * NOTE: You can only declare a right addon OR inset element, not both.
     *
     * @see LeftAddon
     * @see RightInset
     */
    fun RightAddon(modifier: Modifier = Modifier, block: @InputGroupScopeMarker @Composable BoxScope.() -> Unit) {
        require(rightAddon == null && rightInset == null) { "Can only set one right addon or inset element" }
        rightModifier = modifier
        rightAddon = block
    }

    /**
     * Declare an inset element which will be placed within the left side of the input.
     *
     * This is usually a text icon.
     *
     * Due to technical limitations, insets cannot detect their size or grow based on their content. Instead, if you
     * want to change its width, you'll have to set it manually via the [width] parameter.
     *
     * NOTE: You can only declare a left addon OR inset element, not both.
     *
     * @see RightInset
     * @see LeftAddon
     */
    fun LeftInset(
        modifier: Modifier = Modifier,
        width: CSSLengthOrPercentageValue? = null,
        block: @InputGroupScopeMarker @Composable BoxScope.() -> Unit
    ) {
        require(leftAddon == null && leftInset == null) { "Can only set one left addon or inset element" }
        leftModifier = modifier
        leftInset = block
        leftInsetWidth = width
    }

    /**
     * Declare an inset element which will be placed within the right side of the input.
     *
     * Due to technical limitations, insets cannot detect their size or grow based on their content. Instead, if you
     * want to change its width, you'll have to set it manually via the [width] parameter.
     *
     * NOTE: You can only declare a right addon OR inset element, not both.
     *
     * @see LeftInset
     * @see RightAddon
     */
    fun RightInset(
        modifier: Modifier = Modifier,
        width: CSSLengthOrPercentageValue? = null,
        block: @InputGroupScopeMarker @Composable BoxScope.() -> Unit
    ) {
        require(rightAddon == null && rightInset == null) { "Can only set one right addon or inset element" }
        rightModifier = modifier
        rightInset = block
        rightInsetWidth = width
    }
}

fun InputGroupScope.TextInput(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = InputDefaults.Variant,
    placeholder: String? = null,
    placeholderColor: PlaceholderColor? = null,
    focusBorderColor: CSSColorValue? = null,
    invalidBorderColor: CSSColorValue? = null,
    password: Boolean = false,
    enabled: Boolean = InputDefaults.Enabled,
    valid: Boolean = InputDefaults.Valid,
    required: Boolean = InputDefaults.Required,
    readOnly: Boolean = InputDefaults.ReadOnly,
    spellCheck: Boolean = InputDefaults.SpellCheck,
    autoComplete: AutoComplete? = null,
    onCommit: () -> Unit = {},
    ref: ElementRefScope<HTMLInputElement>? = null,
) {
    Input(
        if (password) InputType.Password else InputType.Text,
        text,
        onValueChanged = { onTextChanged(it) },
        modifier,
        variant,
        placeholder,
        placeholderColor,
        focusBorderColor,
        invalidBorderColor,
        enabled,
        valid,
        required,
        readOnly,
        spellCheck,
        autoComplete,
        onCommit,
        ref,
    )
}


interface InputSize {
    val fontSize: CSSLengthValue
    val height: CSSLengthValue
    val padding: CSSLengthValue
    val borderRadius: CSSLengthValue

    object XS : InputSize {
        override val fontSize = 0.75.cssRem
        override val height = 1.25.cssRem
        override val padding = 0.375.cssRem
        override val borderRadius = 0.125.cssRem
    }

    object SM : InputSize {
        override val fontSize = 0.875.cssRem
        override val height = 1.75.cssRem
        override val padding = 0.5.cssRem
        override val borderRadius = 0.25.cssRem
    }

    object MD : InputSize {
        override val fontSize = 1.cssRem
        override val height = 2.25.cssRem
        override val padding = 0.625.cssRem
        override val borderRadius = 0.375.cssRem
    }

    object LG : InputSize {
        override val fontSize = 1.125.cssRem
        override val height = 2.5.cssRem
        override val padding = 0.875.cssRem
        override val borderRadius = 0.375.cssRem
    }
}

fun InputSize.toModifier() = Modifier
    .setVariable(InputBorderRadiusVar, borderRadius)
    .setVariable(InputFontSizeVar, fontSize)
    .setVariable(InputHeightVar, height)
    .setVariable(InputPaddingVar, padding)

private fun PlaceholderColor.toModifier(): Modifier {
    return Modifier
        .setVariable(InputPlaceholderColorVar, color)
        .setVariable(InputPlaceholderOpacityVar, opacity)
}

@Composable
private fun <T : Any> _Input(
    type: InputType<T>,
    value: T,
    onValueChanged: (T) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    placeholder: String? = null,
    placeholderColor: PlaceholderColor? = null,
    focusBorderColor: CSSColorValue? = null,
    invalidBorderColor: CSSColorValue? = null,
    enabled: Boolean = InputDefaults.Enabled,
    valid: Boolean = InputDefaults.Valid,
    required: Boolean = InputDefaults.Required,
    readOnly: Boolean = InputDefaults.ReadOnly,
    spellCheck: Boolean = InputDefaults.SpellCheck,
    autoComplete: AutoComplete? = null,
    onCommit: () -> Unit = {},
    ref: ElementRefScope<HTMLInputElement>? = null,
) {
    if (ref != null) {
        Div(Modifier.display(DisplayStyle.None).toAttrs()) {
            registerRefScope(ref) { it.nextSibling as HTMLInputElement }
        }
    }
    Input(
        type,
        attrs = InputStyle
            .toModifier(variant)
            .thenIf(placeholderColor != null) { placeholderColor!!.toModifier() }
            .thenIf(focusBorderColor != null) { Modifier.setVariable(InputBorderFocusColorVar, focusBorderColor!!) }
            .thenIf(invalidBorderColor != null) {
                Modifier.setVariable(
                    InputBorderInvalidColorVar,
                    invalidBorderColor!!
                )
            }
            .thenIf(!valid) {
                Modifier.ariaInvalid().setVariable(InputBorderColorVar, InputBorderInvalidColorVar.value())
            }
            .thenIf(!enabled) { Modifier.ariaDisabled() }
            .thenIf(required) { Modifier.ariaRequired() }
            .then(modifier)
            .toAttrs {
                when (value) {
                    is String -> value(value)
                    is Number -> value(value)
                    is Boolean -> checked(value)
                    is Unit -> {}
                    else -> error("Unexpected `Input` value type: ${value::class}")
                }

                placeholder?.let { this.placeholder(it) }
                if (!enabled) disabled()
                if (readOnly) readOnly()
                if (required) required()
                spellCheck(spellCheck)
                autoComplete?.let { this.autoComplete(it) }

                onInput { evt -> onValueChanged(type.inputValue(evt.nativeEvent)) }
                onKeyUp { evt ->
                    if (valid && evt.code == "Enter") {
                        evt.preventDefault()
                        evt.stopPropagation()
                        onCommit()
                    }
                }
            }
    )
}

/**
 * Like [Input][com.varabyte.kobweb.silk.components.forms.Input] but for the common case of dealing with text.
 *
 * By default, acts like `Input(InputType.Text)` unless `password` is set to true, in which case it acts like
 * `Input(InputType.Password)`.
 *
 * A simple example looks like this:
 *
 * ```
 * var text by remember { mutableStateOf("") }
 * TextInput(text, onTextChanged = { text = it })
 * ```
 *
 * See `Input` for an explanation of all the parameters.
 */
@Composable
fun TextInput(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = InputDefaults.Variant,
    placeholder: String? = null,
    placeholderColor: PlaceholderColor? = null,
    focusBorderColor: CSSColorValue? = null,
    invalidBorderColor: CSSColorValue? = null,
    size: InputSize = InputDefaults.Size,
    password: Boolean = false,
    enabled: Boolean = InputDefaults.Enabled,
    valid: Boolean = InputDefaults.Valid,
    required: Boolean = InputDefaults.Required,
    readOnly: Boolean = InputDefaults.ReadOnly,
    spellCheck: Boolean = InputDefaults.SpellCheck,
    autoComplete: AutoComplete? = null,
    onCommit: () -> Unit = {},
    ref: ElementRefScope<HTMLInputElement>? = null,
) {
    Input(
        if (!password) InputType.Text else InputType.Password,
        text,
        onValueChanged = { onTextChanged(it) },
        modifier,
        variant,
        placeholder,
        size,
        placeholderColor,
        focusBorderColor,
        invalidBorderColor,
        enabled,
        valid,
        required,
        readOnly,
        spellCheck,
        autoComplete,
        onCommit,
        ref,
    )
}

/**
 * Create and configure an HTML input element.
 *
 * This creates what is called a "controlled" input, where the value of the input is controlled by the caller. A simple
 * example looks like this, where you supply the value directly and then modify it in response to a callback:
 *
 * ```
 * var text by remember { mutableStateOf("") }
 * Input(InputType.Text, text, onValueChanged = { text = it })
 * ```
 *
 * @param type The type of input to create. See [InputType] for the full list of choices.
 * @param value The current value of the input.
 * @param onValueChanged An event triggered when input value wants to change (e.g. in response to user input)
 * @param placeholder Placeholder text to show when the input is empty.
 * @param size The size of the input, with standard T-shirt sizes (XS, SM, MD, LG) available. See also: [InputSize].
 * @param placeholderColor An optional override for the placeholder color.
 * @param focusBorderColor An optional override for the border color when the input is focused.
 * @param invalidBorderColor An optional override for the border color when the [valid] is false.
 * @param enabled If set to false, this input will be disabled.
 * @param valid If set to false, this input will be decorated with a special border color when unfocused, and the
 *   element itself will be tagged with an `aria-invalid` attribute.
 * @param readOnly If set to true, the user will not be able to change the value of this input (but unlike [enabled]
 *   this shouldn't affect the ability to select the input).
 * @param spellCheck If set to true, the input will underline misspelled words. Defaults to false.
 * @param autoComplete An optional strategy to help the browser autocomplete the value automatically. See [AutoComplete]
 *   for a full list.
 * @param onCommit An optional callback that gets triggered when the user presses ENTER while focused on the input.
 *   Note that this method will not be triggered if [valid] is set to false.
 */
@Composable
fun <T : Any> Input(
    type: InputType<T>,
    value: T,
    onValueChanged: (T) -> Unit,
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = InputDefaults.Variant,
    placeholder: String? = null,
    size: InputSize = InputDefaults.Size,
    placeholderColor: PlaceholderColor? = null,
    focusBorderColor: CSSColorValue? = null,
    invalidBorderColor: CSSColorValue? = null,
    enabled: Boolean = InputDefaults.Enabled,
    valid: Boolean = InputDefaults.Valid,
    required: Boolean = InputDefaults.Required,
    readOnly: Boolean = InputDefaults.ReadOnly,
    spellCheck: Boolean = InputDefaults.SpellCheck,
    autoComplete: AutoComplete? = null,
    onCommit: () -> Unit = {},
    ref: ElementRefScope<HTMLInputElement>? = null,
) {
    _Input(
        type,
        value,
        onValueChanged,
        size.toModifier().then(modifier),
        variant,
        placeholder,
        placeholderColor,
        focusBorderColor,
        invalidBorderColor,
        enabled,
        valid,
        required,
        readOnly,
        spellCheck,
        autoComplete,
        onCommit,
        ref,
    )
}

/**
 * Create an input group, which is a collection of related elements that decorate an
 * [Input][com.varabyte.kobweb.silk.components.forms.Input].
 *
 * You can declare addons, which appear to either the right or left of the input element outside its borders,
 * and you can declare insets, which appear within the right or left of the input element's borders.
 *
 * Addons look like tags that appear on the side of the input, and are useful places to add a bit of clarifying text,
 * while insets are useful for icons or buttons that "float" within the input's borders.
 *
 * An example of an input group looks like this:
 *
 * ```
 * var password by remember { mutableStateOf("") }
 * InputGroup {
 *   LeftAddon { Text("Password:") }
 *   TextInput(password, password = true, onTextChanged = { password = it })
 *   RightInset {
 *     if (isValid(password)) {
 *       FaCheck(Modifier.color(Colors.Green))
 *     }
 *   }
 * }
 * ```
 *
 * Note that the `InputGroup` scope is NOT composable! So you must declare any `remember` blocks above it.
 */
@Composable
fun InputGroup(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    size: InputSize = InputDefaults.Size,
    block: InputGroupScope.() -> Unit,
) {
    val scope = InputGroupScope().apply(block)
    val inputParams = scope.inputParams ?: error("Must call `Input` within `InputGroup` block.")
    Row(
        InputGroupStyle.toModifier(variant)
            .then(size.toModifier())
            .position(Position.Relative) // So we can place inset elements
            .alignItems(AlignItems.Stretch)
            .then(modifier)
    ) {
        val inputModifier = Modifier
            .width(100.percent)
            .thenIf(scope.leftAddon != null) {
                Modifier.styleModifier {
                    property("border-top-left-radius", 0.px)
                    property("border-bottom-left-radius", 0.px)
                }
            }
            .thenIf(scope.leftInset != null) {
                Modifier.paddingInlineStart(scope.leftInsetWidth ?: InputInsetLeftWidthVar.value())
            }
            .thenIf(scope.rightAddon != null) {
                Modifier.styleModifier {
                    property("border-top-right-radius", 0.px)
                    property("border-bottom-right-radius", 0.px)
                }
            }
            .thenIf(scope.rightInset != null) {
                Modifier.paddingInlineEnd(scope.rightInsetWidth ?: InputInsetRightWidthVar.value())
            }

        // Render addons (if set) and the main input

        scope.leftAddon?.let { leftAddon ->
            val padding = InputPaddingVar.value()
            Box(
                Modifier
                    .borderRadius(topLeft = InputBorderRadiusVar.value(), bottomLeft = InputBorderRadiusVar.value())
                    .border(1.px, LineStyle.Solid, InputBorderColorVar.value())
                    .flexShrink(0) // Prevent content from collapsing
                    .borderRight(0.px) // prevent double border with input
                    .paddingInline(start = padding, end = padding)
                    .backgroundColor(InputFilledColorVar.value())
                    .then(scope.leftModifier), contentAlignment = Alignment.Center
            ) {
                leftAddon()
            }
        }

        inputParams.renderInput(inputModifier)

        scope.rightAddon?.let { rightAddon ->
            val padding = InputPaddingVar.value()
            Box(
                Modifier
                    .borderRadius(topRight = InputBorderRadiusVar.value(), bottomRight = InputBorderRadiusVar.value())
                    .border(1.px, LineStyle.Solid, InputBorderColorVar.value())
                    .flexShrink(0) // Prevent content from collapsing
                    .borderLeft(0.px) // prevent double border with input
                    .paddingInline(start = padding, end = padding)
                    .backgroundColor(InputFilledColorVar.value())
                    .then(scope.rightModifier),
                contentAlignment = Alignment.Center
            ) {
                rightAddon()
            }
        }

        // Render insets (if any)

        scope.leftInset?.let { leftInset ->
            Box(
                Modifier
                    .position(Position.Absolute).top(0.px).bottom(0.px).left(0.px)
                    .width(scope.leftInsetWidth ?: InputInsetLeftWidthVar.value())
                    .then(scope.leftModifier),
                contentAlignment = Alignment.Center
            ) {
                leftInset()
            }
        }

        scope.rightInset?.let { rightInset ->
            Box(
                Modifier
                    .position(Position.Absolute).top(0.px).bottom(0.px).right(0.px)
                    .width(scope.rightInsetWidth ?: InputInsetRightWidthVar.value())
                    .then(scope.rightModifier),
                contentAlignment = Alignment.Center
            ) {
                rightInset()
            }
        }
    }
}
