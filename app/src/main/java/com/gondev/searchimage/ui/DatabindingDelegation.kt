@file:JvmName("DatabindingDelegation")
package com.gondev.searchimage.ui

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Use setContentView before using the binding variable.
 * ``` kotlin
 * class DataBindingActivity : FragmentActivity() {
 *     // Declare the `binding` variable using `by dataBinding()`.
 *     private val binding: DataBindingActivityBinding by dataBinding()
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContentView(R.layout.data_binding_activity)
 *         // You can use binding
 *     }
 * }
 * ```
 *
 * Use Activity's secondary constructor passing layout res id.
 * ``` kotlin
 * class DataBindingActivity : AppCompatActivity(R.layout.data_binding_activity) {
 *     // Declare the `binding` variable using `by dataBinding()`.
 *     private val binding: DataBindingActivityBinding by dataBinding()
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         // You can use binding
 *     }
 * }
 * ```
 */
fun <T : ViewDataBinding> FragmentActivity.dataBinding(): Lazy<T> = object : Lazy<T> {
    private var binding: T? = null
    override fun isInitialized(): Boolean = binding != null
    override val value: T
        get() = binding ?: bind<T>(getContentView()).also {
            it.lifecycleOwner = this@dataBinding
            binding = it
        }

    private fun FragmentActivity.getContentView(): View {
        return checkNotNull(findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) {
            "Call setContentView or Use Activity's secondary constructor passing layout res id."
        }
    }
}

/**
 * Use inflater.inflate in onCreateView.
 * ``` kotlin
 * class DataBindingFragment : Fragment() {
 *
 *     // Declare the `binding` variable using `by dataBinding()`.
 *     private val binding: DataBindingFragmentBinding by dataBinding()
 *
 *     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
 *         return inflater.inflate(R.layout.data_binding_fragment, container, false)
 *     }
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         // You can use binding
 *     }
 * }
 * ```
 * Use Fragment's secondary constructor passing layout res id.
 * ``` kotlin
 * class DataBindingFragment : Fragment(R.layout.data_binding_fragment) {
 *
 *     // Declare the `binding` variable using `by dataBinding()`.
 *     private val binding: DataBindingFragmentBinding by dataBinding()
 *
 *     // DO NOT override onCreateView
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         // You can use binding
 *     }
 * }
 * ```
 */
fun <T : ViewDataBinding> Fragment.dataBinding(): ReadOnlyProperty<Fragment, T> {
    return object : ReadOnlyProperty<Fragment, T> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            (requireView().getTag(property.name.hashCode()) as? T)?.let { return it }
            return bind<T>(requireView()).also {
                it.lifecycleOwner = thisRef.viewLifecycleOwner
                it.root.setTag(property.name.hashCode(), it)
            }
        }
    }
}

private fun <T : ViewDataBinding> bind(view: View): T = DataBindingUtil.bind(view)!!