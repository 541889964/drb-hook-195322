package com.drb.hook;

import android.os.Process;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.*;
import java.util.*;

public class DrbHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {
        if (!"com.deepseek.chat".equals(lp.packageName)) return;
        XposedBridge.log("[DRB] injected pid=" + Process.myPid());
        try {
            Class<?> rq2 = XposedHelpers.findClass("rq2", lp.classLoader);
            XposedBridge.hookAllMethods(rq2, "h", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam p) {
                    if (p.args == null || p.args.length != 13) return;
                    XposedBridge.log("[DRB] >>> rq2.h called");
                    for (int i = 0; i < p.args.length; i++) {
                        Object a = p.args[i];
                        if (a == null) continue;
                        Class<?>[] ifs = a.getClass().getInterfaces();
                        if (ifs.length > 0) {
                            XposedBridge.log("[DRB] arg[" + i + "] ifaces=" + Arrays.toString(ifs));
                        }
                    }
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("[DRB] hook failed: " + t);
        }
    }
}
