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

interface ICreateTitleView {
    var createdTitleView: View
    fun onCreateTitleView(inflater: LayoutInflater): View
}

class CreateTitleView(
    @LayoutRes
    private val titleBarLayoutId: Int
) : ICreateTitleView {
    override lateinit var createdTitleView: View
    override fun onCreateTitleView(inflater: LayoutInflater): View {
        return inflater.inflate(titleBarLayoutId, null, false).apply {
            createdTitleView = this
        }
    }
}

//==================================================================================================

interface ICreateContentView {
    var createdContentView: View
    fun onCreateContentViewLayoutParams(parent: LinearLayout?): ViewGroup.LayoutParams
    fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup?): View
}

class CreateContentView(
    @LayoutRes
    private val contentLayoutId: Int
) : ICreateContentView {
    override lateinit var createdContentView: View
    override fun onCreateContentViewLayoutParams(parent: LinearLayout?): ViewGroup.LayoutParams {
        return if (parent == null) {
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        } else {
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
    }

    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(contentLayoutId, container, false).apply {
            createdContentView = this
        }
    }
}

//==================================================================================================

open class AutoCreateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return autoCreateView(
            inflater,
            (this as? ICreateTitleView),
            (this as? ICreateContentView)
        )
    }
}

open class AutoCreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            autoCreateView(
                LayoutInflater.from(this),
                (this as? ICreateTitleView),
                (this as? ICreateContentView)
            )
        )
    }
}

//==================================================================================================

private fun autoCreateView(
    inflater: LayoutInflater,
    createTitleView: ICreateTitleView?,
    createContentView: ICreateContentView?
): View {
    val titleBar: View? = createTitleView?.onCreateTitleView(inflater)
    val content: View? = createContentView?.onCreateContentView(inflater, null)
    // both not null
    return if (titleBar != null && content != null) {
        LinearLayout(inflater.context).apply {
            orientation = LinearLayout.VERTICAL
            addView(titleBar)
            addView(content, createContentView.onCreateContentViewLayoutParams(this))
        }
    } else {
        //no content
        if (titleBar != null && content == null) {
            titleBar
        }
        //no title bar
        else if (titleBar == null && content != null) {
            content
        }
        //both null
        else {
            Space(inflater.context)
        }
    }
}