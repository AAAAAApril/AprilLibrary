package com.april.develop.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface IInflateView {
    @LayoutRes
    fun inflateTitleLayoutRes(): Int

    @LayoutRes
    fun inflateContentLayoutRes(): Int

    fun inflateContentLayoutParams(): LinearLayout.LayoutParams
}

class InflateView(
    @LayoutRes
    private val title: Int = 0,
    @LayoutRes
    private val content: Int = 0
) : IInflateView {
    override fun inflateTitleLayoutRes(): Int = title
    override fun inflateContentLayoutRes(): Int = content
    override fun inflateContentLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        )
    }
}

//==================================================================================================

open class AutoCreateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflateView(
            inflater,
            container,
            (this as? IInflateView)
        )
    }
}

open class AutoCreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            inflateView(
                LayoutInflater.from(this),
                null,
                (this as? IInflateView)
            )
        )
    }
}

//==================================================================================================

private fun inflateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    autoInflate: IInflateView?
): View {
    if (autoInflate == null) {
        return Space(inflater.context)
    }
    val titleView: View? = autoInflate.inflateTitleLayoutRes().let { titleLayoutRes ->
        return@let if (titleLayoutRes != 0) {
            inflater.inflate(titleLayoutRes, container, false)
        } else {
            null
        }
    }
    val contentView: View? = autoInflate.inflateContentLayoutRes().let { contentLayoutRes ->
        return@let if (contentLayoutRes != 0) {
            inflater.inflate(contentLayoutRes, container, false)
        } else {
            null
        }
    }
    return if (titleView != null && contentView != null) {
        LinearLayout(inflater.context).apply {
            orientation = LinearLayout.VERTICAL
            addView(titleView)
            addView(contentView, autoInflate.inflateContentLayoutParams())
        }
    } else {
        titleView ?: (contentView ?: Space(inflater.context))
    }
}