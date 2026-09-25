package io.github.youndie.oldge.sample.ios

import androidx.compose.ui.window.ComposeUIViewController
import io.github.youndie.oldge.sample.OldgeSampleApp
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectBase.OverrideInit
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCValues
import platform.Foundation.NSStringFromClass
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UIKit.UIApplicationDelegateProtocolMeta
import platform.UIKit.UIApplicationMain
import platform.UIKit.UIResponder
import platform.UIKit.UIResponderMeta
import platform.UIKit.UIScreen
import platform.UIKit.UIWindow

/**
 * The sample app on iOS, with no Xcode project (B-41): Kotlin/Native links the Mach-O, and
 * `scripts/ios-sample-app.sh` puts it in a `.app` with its `Info.plist` and the library's fonts. The
 * three lines of UIKit under it are kvadrant-ui's.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public fun main() {
    memScoped {
        val args = arrayOf("OldgeSample")
        UIApplicationMain(
            argc = args.size,
            argv = args.map { it.cstr.ptr }.toCValues().ptr,
            principalClassName = null,
            // By name, which is how UIKit finds a delegate without a storyboard; a misspelling is a
            // black screen and no error.
            delegateClassName = NSStringFromClass(OldgeSampleDelegate),
        )
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
public class OldgeSampleDelegate :
    UIResponder,
    UIApplicationDelegateProtocol {
    // UIKit makes the delegate with `[[Class alloc] init]`, which a Kotlin class does not export
    // unless it says so: without this the app dies before any Kotlin runs.
    @OverrideInit
    public constructor() : super()

    public companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    // `window` is the protocol's own property; a private one of the same name would hide it.
    private var held: UIWindow? = null

    override fun window(): UIWindow? = held

    override fun setWindow(window: UIWindow?) {
        held = window
    }

    override fun application(
        application: UIApplication,
        didFinishLaunchingWithOptions: Map<Any?, *>?,
    ): Boolean {
        held =
            UIWindow(frame = UIScreen.mainScreen.bounds).apply {
                rootViewController = ComposeUIViewController { OldgeSampleApp() }
                makeKeyAndVisible()
            }
        return true
    }
}
