package code.name.monkey.retromusic.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.ActionMenuView
import com.google.android.material.appbar.MaterialToolbar

/**
 * A [MaterialToolbar] subclass that distributes its navigation button and action menu
 * items evenly across the full available width.
 */
class EqualSpacingToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.toolbarStyle
) : MaterialToolbar(context, attrs, defStyleAttr) {

    init {
        setContentInsetsAbsolute(0, 0)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        distributeButtons()
    }

    private fun distributeButtons() {
        val navButton = findNavButton()
        val actionMenuView = findActionMenuView() ?: return

        val visibleMenuItems = mutableListOf<View>()
        for (i in 0 until actionMenuView.childCount) {
            val child = actionMenuView.getChildAt(i)
            if (child.visibility != View.GONE) {
                visibleMenuItems.add(child)
            }
        }

        val hasNav = navButton != null && navButton.visibility != View.GONE
        val totalButtons = (if (hasNav) 1 else 0) + visibleMenuItems.size
        if (totalButtons == 0) return

        val availableWidth = measuredWidth - paddingLeft - paddingRight
        if (availableWidth <= 0) return

        val slotWidth = availableWidth / totalButtons.toFloat()
        val toolbarHeight = measuredHeight
        val isRtl = layoutDirection == View.LAYOUT_DIRECTION_RTL

        if (hasNav && navButton != null) {
            val navSlotIndex = if (isRtl) totalButtons - 1 else 0
            val navCenter = paddingLeft + (navSlotIndex + 0.5f) * slotWidth
            val navW = navButton.measuredWidth
            val navH = navButton.measuredHeight
            val navL = (navCenter - navW / 2f).toInt()
            val navT = (toolbarHeight - navH) / 2
            navButton.layout(navL, navT, navL + navW, navT + navH)

            val menuL: Int
            val menuR: Int
            if (!isRtl) {
                menuL = (paddingLeft + slotWidth).toInt()
                menuR = paddingLeft + availableWidth
            } else {
                menuL = paddingLeft
                menuR = (paddingLeft + availableWidth - slotWidth).toInt()
            }
            actionMenuView.layout(menuL, 0, menuR, toolbarHeight)

            for (i in visibleMenuItems.indices) {
                val child = visibleMenuItems[i]
                val slotIndex = if (isRtl) visibleMenuItems.size - 1 - i else i
                val childCenter = (slotIndex + 0.5f) * slotWidth
                val childW = child.measuredWidth
                val childH = child.measuredHeight
                val childL = (childCenter - childW / 2f).toInt()
                val childT = (toolbarHeight - childH) / 2
                child.layout(childL, childT, childL + childW, childT + childH)
            }
        } else {
            actionMenuView.layout(paddingLeft, 0, paddingLeft + availableWidth, toolbarHeight)
            for (i in visibleMenuItems.indices) {
                val child = visibleMenuItems[i]
                val slotIndex = if (isRtl) visibleMenuItems.size - 1 - i else i
                val childCenter = (slotIndex + 0.5f) * slotWidth
                val childW = child.measuredWidth
                val childH = child.measuredHeight
                val childL = (childCenter - childW / 2f).toInt()
                val childT = (toolbarHeight - childH) / 2
                child.layout(childL, childT, childL + childW, childT + childH)
            }
        }
    }

    private fun findNavButton(): ImageButton? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ImageButton && child !is ActionMenuView) {
                return child
            }
        }
        return null
    }

    private fun findActionMenuView(): ActionMenuView? {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ActionMenuView) {
                return child
            }
        }
        return null
    }
}
